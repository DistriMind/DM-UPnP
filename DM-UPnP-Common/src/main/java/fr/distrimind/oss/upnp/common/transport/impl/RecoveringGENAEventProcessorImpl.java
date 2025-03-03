/*
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

/*
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

/*
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package fr.distrimind.oss.upnp.common.transport.impl;

import fr.distrimind.oss.upnp.common.model.UnsupportedDataException;
import fr.distrimind.oss.upnp.common.model.XMLUtil;
import fr.distrimind.oss.upnp.common.model.message.gena.IncomingEventRequestMessage;
import fr.distrimind.oss.upnp.common.xml.XmlPullParserUtils;

import jakarta.enterprise.inject.Alternative;

import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Implementation based on the <em>Xml Pull Parser</em> XML processing API.
 * <p>
 * This processor extends {@link PullGENAEventProcessorImpl}, it will always
 * first try to read messages regularly with the superclass' methods before
 * trying to recover from a failure.
 * </p>
 * <p>
 * When the superclass can't read the message, this processor will try to
 * recover from broken XML by for example, detecting wrongly encoded XML entities,
 * and working around other vendor-specific bugs caused by incompatible UPnP
 * stacks in the wild.
 * </p>
 * <p>
 * This processor will also return partial results, if at least a single
 * state variable value could be recovered from the (broken) event XML.
 * </p>
 *
 * @author Michael Pujos
 */
@Alternative
public class RecoveringGENAEventProcessorImpl extends PullGENAEventProcessorImpl {

    final private static DMLogger log = Log.getLogger(RecoveringGENAEventProcessorImpl.class);

    @Override
	public void readBody(IncomingEventRequestMessage requestMessage) throws UnsupportedDataException {
        try {
            super.readBody(requestMessage);
        } catch (UnsupportedDataException ex) {

            // Can't recover from this
            if (!requestMessage.isBodyNonEmptyString())
                throw ex;

            if (log.isWarnEnabled()) log.warn("Trying to recover from invalid GENA XML event: ", ex);

            // Some properties may have been read at this point, so reset the list
            requestMessage.getStateVariableValues().clear();

            String body = getMessageBody(requestMessage);

            String fixedBody = fixXMLEncodedLastChange(
                XmlPullParserUtils.fixXMLEntities(body)
            );

            try {
                // Try again, if this fails, we are done...
                requestMessage.setBody(fixedBody);
                super.readBody(requestMessage);
            } catch (UnsupportedDataException ex2) {
                // Check if some properties were read
                if (requestMessage.getStateVariableValues().isEmpty()) {
                    // Throw the initial exception containing unmodified XML
                    throw ex;
                }
                log.warn("Partial read of GENA event properties (probably due to truncated XML)");
            }
        }
    }

    protected String fixXMLEncodedLastChange(String xml) {
        Pattern pattern = Pattern.compile("<LastChange>(.*)</LastChange>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xml);

        if (matcher.find() && matcher.groupCount() == 1) {

            String lastChange = matcher.group(1);

            if (XmlPullParserUtils.isNullOrEmpty(lastChange))
                return xml;

            lastChange = lastChange.trim();

            String fixedLastChange = lastChange;

            if (lastChange.charAt(0) == '<') {
            // TODO: UPNP VIOLATION: Orange Liveradio does not encode LastChange XML properly
                fixedLastChange = XMLUtil.encodeText(fixedLastChange);
            } else {
                /* Doesn't work for Philips NP2900, there is complete garbage after the HTML
                // TODO: UPNP VIOLATION: Philips NP2900 inserts garbage HTML, try to fix it
                fixedLastChange = fixedLastChange.replaceAll("<", "");
                fixedLastChange = fixedLastChange.replaceAll(">", "");
                */
            }

            if (fixedLastChange.equals(lastChange)) {
                return xml;
            }

            return "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<e:propertyset xmlns:e=\"urn:schemas-upnp-org:event-1-0\">" +
                "<e:property>" +
                "<LastChange>" +
                fixedLastChange +
                "</LastChange>" +
                "</e:property>" +
                "</e:propertyset>";
        }
        return xml;
    }
}
