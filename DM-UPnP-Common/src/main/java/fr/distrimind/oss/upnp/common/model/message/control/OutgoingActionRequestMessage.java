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

package fr.distrimind.oss.upnp.common.model.message.control;

import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

import fr.distrimind.oss.upnp.common.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpRequest;
import fr.distrimind.oss.upnp.common.model.action.ActionInvocation;
import fr.distrimind.oss.upnp.common.model.action.RemoteActionInvocation;
import fr.distrimind.oss.upnp.common.model.meta.Action;
import fr.distrimind.oss.upnp.common.model.meta.QueryStateVariableAction;
import fr.distrimind.oss.upnp.common.model.message.header.ContentTypeHeader;
import fr.distrimind.oss.upnp.common.model.message.header.SoapActionHeader;
import fr.distrimind.oss.upnp.common.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.common.model.message.header.UserAgentHeader;
import fr.distrimind.oss.upnp.common.model.types.SoapActionType;

import java.net.URL;

/**
 * @author Christian Bauer
 */
public class OutgoingActionRequestMessage extends StreamRequestMessage implements ActionRequestMessage {

    final private static DMLogger log = Log.getLogger(OutgoingActionRequestMessage.class);

    final private String actionNamespace;

    public OutgoingActionRequestMessage(ActionInvocation<?> actionInvocation, URL controlURL) {
        this(actionInvocation.getAction(), new UpnpRequest(UpnpRequest.Method.POST, controlURL));

        // For proxy remote invocations, pass through the user agent header
        if (actionInvocation instanceof RemoteActionInvocation) {
            RemoteActionInvocation<?> remoteActionInvocation = (RemoteActionInvocation<?>) actionInvocation;
            if (remoteActionInvocation.getRemoteClientInfo() != null
                && remoteActionInvocation.getRemoteClientInfo().getRequestUserAgent() != null) {
                getHeaders().add(
                    UpnpHeader.Type.USER_AGENT,
                    new UserAgentHeader(remoteActionInvocation.getRemoteClientInfo().getRequestUserAgent())
                );
            }
        } else if (actionInvocation.getClientInfo() != null) {
            getHeaders().putAll(actionInvocation.getClientInfo().getRequestHeaders());
        }
    }

    public OutgoingActionRequestMessage(Action<?> action, UpnpRequest operation) {
        super(operation);

        getHeaders().add(
                UpnpHeader.Type.CONTENT_TYPE,
                new ContentTypeHeader(ContentTypeHeader.DEFAULT_CONTENT_TYPE_UTF8)
        );

        SoapActionHeader soapActionHeader;
        if (action instanceof QueryStateVariableAction) {
            log.debug("Adding magic control SOAP action header for state variable query action");
            soapActionHeader = new SoapActionHeader(
                    new SoapActionType(
                            SoapActionType.MAGIC_CONTROL_NS, SoapActionType.MAGIC_CONTROL_TYPE, null, action.getName()
                    )
            );
        } else {
            soapActionHeader = new SoapActionHeader(
                    new SoapActionType(
                            action.getService().getServiceType(),
                            action.getName()
                    )
            );
        }

        // We need to keep it for later, convenience for writing the SOAP body XML
        actionNamespace = soapActionHeader.getValue().getTypeString();

        if (getOperation().getMethod().equals(UpnpRequest.Method.POST)) {

            getHeaders().add(UpnpHeader.Type.SOAPACTION, soapActionHeader);
			if (log.isDebugEnabled()) {
				log.debug("Added SOAP action header: " + soapActionHeader);
			}

        /* TODO: Finish the M-POST crap (or not)
        } else if (getOperation().getMethod().equals(UpnpRequest.Method.MPOST)) {

            getHeaders().add(UpnpHeader.Type.MAN, new MANHeader(Constants.SOAP_NS_ENVELOPE, "01"));

            getHeaders().add(UpnpHeader.Type.SOAPACTION, soapActionHeader);
            getHeaders().setPrefix(UpnpHeader.Type.SOAPACTION, "01");
            log.debug("Added SOAP action header with prefix '01': " + getHeaders().getFirstHeader(UpnpHeader.Type.SOAPACTION).getString());
            */

        } else {
            throw new IllegalArgumentException("Can't send action with request method: " + getOperation().getMethod());
        }
    }

    @Override
	public String getActionNamespace() {
        return actionNamespace;
    }

}
