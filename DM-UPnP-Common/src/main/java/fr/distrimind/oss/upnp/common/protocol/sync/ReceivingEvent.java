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

import fr.distrimind.oss.upnp.common.UpnpServiceConfiguration;
import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerFourBytes;
import fr.distrimind.oss.upnp.common.protocol.ReceivingSync;
import fr.distrimind.oss.upnp.common.transport.RouterException;
import fr.distrimind.oss.upnp.common.UpnpService;
import fr.distrimind.oss.upnp.common.model.gena.RemoteGENASubscription;
import fr.distrimind.oss.upnp.common.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.common.model.message.gena.IncomingEventRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.gena.OutgoingEventResponseMessage;
import fr.distrimind.oss.upnp.common.model.resource.ServiceEventCallbackResource;
import fr.distrimind.oss.upnp.common.model.UnsupportedDataException;

import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Handles incoming GENA event messages.
 * <p>
 * Attempts to find an outgoing (remote) subscription matching the callback and subscription identifier.
 * Once found, the GENA event message payload will be transformed and the
 * {@link RemoteGENASubscription#receive(UnsignedIntegerFourBytes,
 * java.util.Collection)} method will be called asynchronously using the executor
 * returned by {@link UpnpServiceConfiguration#getRegistryListenerExecutor()}.
 * </p>
 *
 * @author Christian Bauer
 */
public class ReceivingEvent extends ReceivingSync<StreamRequestMessage, OutgoingEventResponseMessage> {

    final private static DMLogger log = Log.getLogger(ReceivingEvent.class);

    public ReceivingEvent(UpnpService upnpService, StreamRequestMessage inputMessage) {
        super(upnpService, inputMessage);
    }

    @Override
	protected OutgoingEventResponseMessage executeSync() throws RouterException {

        if (!getInputMessage().isContentTypeTextUDA()) {
			if (log.isWarnEnabled()) log.warn("Received without or with invalid Content-Type: " + getInputMessage());
            // We continue despite the invalid UPnP message because we can still hope to convert the content
            // return new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.UNSUPPORTED_MEDIA_TYPE));
        }

        ServiceEventCallbackResource resource =
                getUpnpService().getRegistry().getResource(
                        ServiceEventCallbackResource.class,
                        getInputMessage().getUri()
                );

        if (resource == null) {
			if (log.isDebugEnabled()) {
				log.debug("No local resource found: " + getInputMessage());
			}
			return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.NOT_FOUND));
        }

        final IncomingEventRequestMessage requestMessage =
                new IncomingEventRequestMessage(getInputMessage(), resource.getModel());

        // Error conditions UDA 1.0 section 4.2.1
        if (requestMessage.getSubscrptionId() == null) {
			if (log.isDebugEnabled()) {
				log.debug("Subscription ID missing in event request: " + getInputMessage());
			}
			return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.PRECONDITION_FAILED));
        }

        if (!requestMessage.hasValidNotificationHeaders()) {
			if (log.isDebugEnabled()) {
				log.debug("Missing NT and/or NTS headers in event request: " + getInputMessage());
			}
			return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.BAD_REQUEST));
        }

        if (!requestMessage.hasValidNotificationHeaders()) {
			if (log.isDebugEnabled()) {
				log.debug("Invalid NT and/or NTS headers in event request: " + getInputMessage());
			}
			return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.PRECONDITION_FAILED));
        }

        if (requestMessage.getSequence() == null) {
			if (log.isDebugEnabled()) {
				log.debug("Sequence missing in event request: " + getInputMessage());
			}
			return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.PRECONDITION_FAILED));
        }

        try {

            getUpnpService().getConfiguration().getGenaEventProcessor().readBody(requestMessage);

		} catch (final UnsupportedDataException ex) {
			if (log.isDebugEnabled()) {
				log.debug("Can't read event message request body, ", ex);
			}

			// Pass the parsing failure on to any listeners, so they can take action if necessary
            final RemoteGENASubscription subscription =
                getUpnpService().getRegistry().getRemoteSubscription(requestMessage.getSubscrptionId());
            if (subscription != null) {
                getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
						() -> subscription.invalidMessage(ex)
				);
            }

            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.INTERNAL_SERVER_ERROR));
        }

        // get the remove subscription, if the subscription can't be found, wait for pending subscription
        // requests to finish
        final RemoteGENASubscription subscription =
                getUpnpService().getRegistry().getWaitRemoteSubscription(requestMessage.getSubscrptionId());

        if (subscription == null) {
			if (log.isErrorEnabled()) log.error("Invalid subscription ID, no active subscription: " + requestMessage);
            return new OutgoingEventResponseMessage(new UpnpResponse(UpnpResponse.Status.PRECONDITION_FAILED));
        }

        getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
				() -> {
					log.debug("Calling active subscription with event state variable values");
					subscription.receive(
							requestMessage.getSequence(),
							requestMessage.getStateVariableValues()
					);
				}
		);

        return new OutgoingEventResponseMessage();

    }
}
