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

package fr.distrimind.oss.upnp.common.model.message.header;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * @author Christian Bauer
 */
public class CallbackHeader extends UpnpHeader<List<URL>> {

    final private static DMLogger log = Log.getLogger(CallbackHeader.class);

    public CallbackHeader() {
        setValue(new ArrayList<>());
    }

    public CallbackHeader(List<URL> urls) {
        this();
        getValue().addAll(urls);
    }

    public CallbackHeader(URL url) {
        this();
        getValue().add(url);
    }

    @Override
	public void setString(String _s) throws InvalidHeaderException {

        if (_s.isEmpty()) {
            // Well, no callback URLs are not useful, but we have to consider this state
            return;
        }

        if (!_s.contains("<") || !_s.contains(">")) {
            throw new InvalidHeaderException("URLs not in brackets: " + _s);
        }

        String s = _s.replaceAll("<", "");
        String[] split = s.split(">");
        try {
            List<URL> urls = new ArrayList<>();
            for (String oneSplit : split) {
                String sp = oneSplit.trim();

                if (!sp.startsWith("http://")) {
                    if (log.isWarnEnabled()) log.warn("Discarding non-http callback URL: " + sp);
                    continue;
                }

                URL url = new URL(sp);
                try {
                    /*
                        On some platforms (Android...), a valid URL might not be a valid URI, so
                        we need to test for this and skip any invalid URI, e.g.

                        Java.net.URISyntaxException: Invalid % sequence: %wl in authority at index 32: http://[fe80::208:caff:fec4:824e%wlan0]:8485/eventSub
    		                at libcore.net.UriCodec.validate(UriCodec.java:58)
                            at java.net.URI.parseURI(URI.java:394)
                            at java.net.URI.<init>(URI.java:204)
                            at java.net.URL.toURI(URL.java:497)
            	    */
                    url.toURI();
                } catch (URISyntaxException ex) {
                    if (log.isWarnEnabled()) log.warn("Discarding callback URL, not a valid URI on this platform: " + url, ex);
                    continue;
                }

                urls.add(url);
            }
            setValue(urls);
        } catch (MalformedURLException ex) {
            throw new InvalidHeaderException("Can't parse callback URLs from '" + s + "': " + ex);
        }
    }

    @Override
	public String getString() {
        StringBuilder s = new StringBuilder();
        for (URL url : getValue()) {
            s.append("<").append(url.toString()).append(">");
        }
        return s.toString();
    }
}
