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

import fr.distrimind.oss.upnp.common.protocol.ReceivingSync;
import fr.distrimind.oss.upnp.common.transport.RouterException;
import fr.distrimind.oss.upnp.common.UpnpService;
import fr.distrimind.oss.upnp.common.model.gena.CancelReason;
import fr.distrimind.oss.upnp.common.model.gena.LocalGENASubscription;
import fr.distrimind.oss.upnp.common.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.common.model.message.gena.IncomingSubscribeRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.gena.OutgoingSubscribeResponseMessage;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;
import fr.distrimind.oss.upnp.common.model.resource.ServiceEventSubscriptionResource;
import fr.distrimind.oss.upnp.common.util.Exceptions;

import java.net.URL;
import java.util.List;
import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Handles reception of GENA event subscription (initial and renewal) messages.
 * <p>
 * This protocol tries to find a local event subscription URI matching the requested URI,
 * then creates a new {@link LocalGENASubscription} if no
 * subscription identifer was supplied.
 * </p>
 * <p>
 * The subscription is however only registered with the local service, and monitoring
 * of state changes is established, if the response of this protocol was successfully
 * delivered to the client which requested the subscription.
 * </p>
 * <p>
 * Once registration and monitoring is active, an initial event with the current
 * state of the service is send to the subscriber. This will only happen after the
 * subscription response message was successfully delivered to the subscriber.
 * </p>
 *
 * @author Christian Bauer
 */
public class ReceivingSubscribe extends ReceivingSync<StreamRequestMessage, OutgoingSubscribeResponseMessage> {

    final private static DMLogger log = Log.getLogger(ReceivingSubscribe.class);

    protected LocalGENASubscription<?> subscription;

    public ReceivingSubscribe(UpnpService upnpService, StreamRequestMessage inputMessage) {
        super(upnpService, inputMessage);
    }

    @Override
	protected OutgoingSubscribeResponseMessage executeSync() throws RouterException {

        ServiceEventSubscriptionResource<?> resource =
                getUpnpService().getRegistry().getResource(
                        ServiceEventSubscriptionResource.class,
                        getInputMessage().getUri()
        );

        if (resource == null) {
			if (log.isDebugEnabled()) {
				log.debug("No local resource found: " + getInputMessage());
			}
			return null;
        }

		if (log.isDebugEnabled()) {
            log.debug("Found local event subscription matching relative request URI: " + getInputMessage().getUri());
		}

		IncomingSubscribeRequestMessage requestMessage =
                new IncomingSubscribeRequestMessage(getInputMessage(), resource.getModel());

        // Error conditions UDA 1.0 section 4.1.1 and 4.1.2
        if (requestMessage.getSubscriptionId() != null &&
                (requestMessage.hasNotificationHeader() || requestMessage.getCallbackURLs() != null)) {
			if (log.isDebugEnabled()) {
				log.debug("Subscription ID and NT or Callback in subscribe request: " + getInputMessage());
			}
			return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.BAD_REQUEST);
        }

        if (requestMessage.getSubscriptionId() != null) {
            return processRenewal(resource.getModel(), requestMessage);
        } else if (requestMessage.hasNotificationHeader() && requestMessage.getCallbackURLs() != null){
            return processNewSubscription(resource.getModel(), requestMessage);
        } else {
			if (log.isDebugEnabled()) {
				log.debug("No subscription ID, no NT or Callback, neither subscription or renewal: " + getInputMessage());
			}
			return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.PRECONDITION_FAILED);
        }

    }

    protected OutgoingSubscribeResponseMessage processRenewal(LocalService<?> service,
                                                              IncomingSubscribeRequestMessage requestMessage) {

        subscription = getUpnpService().getRegistry().getLocalSubscription(requestMessage.getSubscriptionId());

        // Error conditions UDA 1.0 section 4.1.1 and 4.1.2
        if (subscription == null) {
			if (log.isDebugEnabled()) {
				log.debug("Invalid subscription ID for renewal request: " + getInputMessage());
			}
			return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.PRECONDITION_FAILED);
        }

		if (log.isDebugEnabled()) {
            log.debug("Renewing subscription: " + subscription);
		}
		subscription.setSubscriptionDuration(requestMessage.getRequestedTimeoutSeconds());
        if (getUpnpService().getRegistry().updateLocalSubscription(subscription)) {
            return new OutgoingSubscribeResponseMessage(subscription);
        } else {
			if (log.isDebugEnabled()) {
				log.debug("Subscription went away before it could be renewed: " + getInputMessage());
			}
			return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.PRECONDITION_FAILED);
        }
    }

    protected <T> OutgoingSubscribeResponseMessage processNewSubscription(LocalService<T> service,
                                                                      IncomingSubscribeRequestMessage requestMessage) {
        List<URL> callbackURLs = requestMessage.getCallbackURLs();

        // Error conditions UDA 1.0 section 4.1.1 and 4.1.2
        if (callbackURLs == null || callbackURLs.isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("Missing or invalid Callback URLs in subscribe request: " + getInputMessage());
			}
			return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.PRECONDITION_FAILED);
        }

        if (!requestMessage.hasNotificationHeader()) {
			if (log.isDebugEnabled()) {
				log.debug("Missing or invalid NT header in subscribe request: " + getInputMessage());
			}
			return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.PRECONDITION_FAILED);
        }

        Integer timeoutSeconds; 
        if(getUpnpService().getConfiguration().isReceivedSubscriptionTimeoutIgnored()) {
        	timeoutSeconds = null; // Use default value
        } else {
        	timeoutSeconds = requestMessage.getRequestedTimeoutSeconds();
        }
        
        try {
            subscription = new LocalGENASubscription<>(service, timeoutSeconds, callbackURLs) {
                @Override
				public void established() {
                }

                @Override
				public void ended(CancelReason reason) {
                }

                @Override
				public void eventReceived() {
                    // The only thing we are interested in, sending an event when the state changes
                    getUpnpService().getConfiguration().getSyncProtocolExecutorService().execute(
                            getUpnpService().getProtocolFactory().createSendingEvent(this)
                    );
                }
            };
        } catch (Exception ex) {
			if (log.isWarnEnabled()) log.warn("Couldn't create local subscription to service: ", Exceptions.unwrap(ex));
            return new OutgoingSubscribeResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR);
        }

		if (log.isDebugEnabled()) {
            log.debug("Adding subscription to registry: " + subscription);
		}
		getUpnpService().getRegistry().addLocalSubscription(subscription);

        log.debug("Returning subscription response, waiting to send initial event");
        return new OutgoingSubscribeResponseMessage(subscription);
    }

    @Override
    public void responseSent(StreamResponseMessage responseMessage) {
        if (subscription == null) return; // Preconditions failed very early on
        if (responseMessage != null
                && !responseMessage.getOperation().isFailed()
                && subscription.getCurrentSequence().getValue() == 0) { // Note that renewals should not have 0

            // This is a minor concurrency issue: If we now register on the service and henceforth send a new
            // event message whenever the state of the service changes, there is still a chance that the initial
            // event message arrives later than the first on-change event message. Shouldn't be a problem as the
            // subscriber is supposed to figure out what to do with out-of-sequence messages. I would be
            // surprised though if actual implementations won't crash!
            log.debug("Establishing subscription");
            subscription.registerOnService();
            subscription.establish();

            log.debug("Response to subscription sent successfully, now sending initial event asynchronously");
            getUpnpService().getConfiguration().getAsyncProtocolExecutor().execute(
                    getUpnpService().getProtocolFactory().createSendingEvent(subscription)
            );

        } else if (subscription.getCurrentSequence().getValue() == 0) {
            log.debug("Subscription request's response aborted, not sending initial event");
            if (responseMessage == null) {
                log.debug("Reason: No response at all from subscriber");
            } else {
				if (log.isDebugEnabled()) {
					log.debug("Reason: " + responseMessage.getOperation());
				}
			}
			if (log.isDebugEnabled()) {
				log.debug("Removing subscription from registry: " + subscription);
			}
			getUpnpService().getRegistry().removeLocalSubscription(subscription);
        }
    }

    @Override
    public void responseException(Throwable t) {
        if (subscription == null) return; // Nothing to do, we didn't get that far
		if (log.isDebugEnabled()) {
            log.debug("Response could not be send to subscriber, removing local GENA subscription: " + subscription);
		}
		getUpnpService().getRegistry().removeLocalSubscription(subscription);
    }
}