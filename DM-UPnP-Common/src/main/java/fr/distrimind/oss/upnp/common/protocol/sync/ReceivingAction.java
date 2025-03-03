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

package fr.distrimind.oss.upnp.common.protocol.sync;

import fr.distrimind.oss.flexilogxml.common.FlexiLogXML;
import fr.distrimind.oss.upnp.common.model.action.ActionExecutor;
import fr.distrimind.oss.upnp.common.model.meta.Action;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;
import fr.distrimind.oss.upnp.common.protocol.ReceivingSync;
import fr.distrimind.oss.upnp.common.transport.RouterException;
import fr.distrimind.oss.upnp.common.UpnpService;
import fr.distrimind.oss.upnp.common.model.action.ActionCancelledException;
import fr.distrimind.oss.upnp.common.model.action.ActionException;
import fr.distrimind.oss.upnp.common.model.action.RemoteActionInvocation;
import fr.distrimind.oss.upnp.common.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.common.model.message.control.IncomingActionRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.control.OutgoingActionResponseMessage;
import fr.distrimind.oss.upnp.common.model.message.header.ContentTypeHeader;
import fr.distrimind.oss.upnp.common.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.common.model.resource.ServiceControlResource;
import fr.distrimind.oss.upnp.common.model.types.ErrorCode;
import fr.distrimind.oss.upnp.common.model.UnsupportedDataException;
import fr.distrimind.oss.upnp.common.util.Exceptions;

import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

import fr.distrimind.oss.flexilogxml.common.log.Level;

/**
 * Handles reception of control messages, invoking actions on local services.
 * <p>
 * Actions are invoked through the {@link ActionExecutor} returned
 * by the registered {@link LocalService#getExecutor(Action)}
 * method.
 * </p>
 *
 * @author Christian Bauer
 */
public class ReceivingAction extends ReceivingSync<StreamRequestMessage, StreamResponseMessage> {

    final private static DMLogger log = Log.getLogger(ReceivingAction.class);

    public ReceivingAction(UpnpService upnpService, StreamRequestMessage inputMessage) {
        super(upnpService, inputMessage);
    }

    @Override
	protected StreamResponseMessage executeSync() throws RouterException {

        ContentTypeHeader contentTypeHeader =
                getInputMessage().getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE, ContentTypeHeader.class);

        // Special rules for action messages! UDA 1.0 says:
        // 'If the CONTENT-TYPE header specifies an unsupported value (other than "text/xml") the
        // device must return an HTTP status code "415 Unsupported Media Type".'
        if (contentTypeHeader != null && !contentTypeHeader.isUDACompliantXML()) {
			if (log.isWarnEnabled())
                log.warn("Received invalid Content-Type '" + contentTypeHeader + "': " + getInputMessage());
            return new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.UNSUPPORTED_MEDIA_TYPE));
        }

        if (contentTypeHeader == null) {
			if (log.isWarnEnabled()) log.warn("Received without Content-Type: " + getInputMessage());
        }

        ServiceControlResource<?> resource =
                getUpnpService().getRegistry().getResource(
                        ServiceControlResource.class,
                        getInputMessage().getUri()
                );

        if (resource == null) {
			if (log.isDebugEnabled()) {
				log.debug("No local resource found: " + getInputMessage());
			}
			return null;
        }

		if (log.isDebugEnabled()) {
            log.debug("Found local action resource matching relative request URI: " + getInputMessage().getUri());
		}

		RemoteActionInvocation<? extends LocalService<?>> invocation;
        OutgoingActionResponseMessage responseMessage;

        try {

            // Throws ActionException if the action can't be found
            IncomingActionRequestMessage requestMessage =
                    new IncomingActionRequestMessage(getInputMessage(), resource.getModel());

			if (log.isTraceEnabled()) {
				log.trace("Created incoming action request message: " + requestMessage);
			}
			invocation = new RemoteActionInvocation<>(requestMessage.getAction(), getRemoteClientInfo());

            // Throws UnsupportedDataException if the body can't be read
            log.debug("Reading body of request message");
            getUpnpService().getConfiguration().getSoapActionProcessor().readBody(requestMessage, invocation);

			if (log.isDebugEnabled()) {
				log.debug("Executing on local service: " + invocation);
			}
			resource.getModel().getExecutor(invocation.getAction()).executeWithUntypedGeneric(invocation);

            if (invocation.getFailure() == null) {
                responseMessage =
                        new OutgoingActionResponseMessage(invocation.getAction());
            } else {

                if (invocation.getFailure() instanceof ActionCancelledException) {
                    log.debug("Action execution was cancelled, returning 404 to client");
                    // A 404 status is appropriate for this situation: The resource is gone/not available, and it's
                    // a temporary condition. Most likely the cancellation happened because the client connection
                    // has been dropped, so it doesn't really matter what we return here anyway.
                    return null;
                } else {
                    responseMessage =
                            new OutgoingActionResponseMessage(
                                UpnpResponse.Status.INTERNAL_SERVER_ERROR,
                                invocation.getAction()
                            );
                }
            }

        } catch (ActionException ex) {
			if (log.isTraceEnabled()) {
				log.trace("Error executing local action: ", ex);
			}

			invocation = new RemoteActionInvocation<>(ex, getRemoteClientInfo());
            responseMessage = new OutgoingActionResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR);

        } catch (UnsupportedDataException ex) {
			if (log.isWarnEnabled()) log.warn("Error reading action request XML body: ", ex, Exceptions.unwrap(ex));

            invocation =
                    new RemoteActionInvocation<>(
                        Exceptions.unwrap(ex) instanceof ActionException
                                ? (ActionException)Exceptions.unwrap(ex)
                                : new ActionException(ErrorCode.ACTION_FAILED, ex.getMessage()),
                        getRemoteClientInfo()
                    );
            responseMessage = new OutgoingActionResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR);

        }

        try {

            log.debug("Writing body of response message");
            getUpnpService().getConfiguration().getSoapActionProcessor().writeBody(responseMessage, invocation);

			if (log.isDebugEnabled()) {
				log.debug("Returning finished response message: " + responseMessage);
			}
			return responseMessage;

        } catch (UnsupportedDataException ex) {
            FlexiLogXML.log(Level.WARN, ex);
			if (log.isWarnEnabled()) {
				log.warn("Failure writing body of response message, sending '500 Internal Server Error' without body");
				log.warn("Exception root cause: ", Exceptions.unwrap(ex));
			}
            return new StreamResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR);
        }
    }

}
