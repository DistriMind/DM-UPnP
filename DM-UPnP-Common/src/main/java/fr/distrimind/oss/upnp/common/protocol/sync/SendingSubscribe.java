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

import fr.distrimind.oss.upnp.common.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.common.protocol.SendingSync;
import fr.distrimind.oss.upnp.common.transport.RouterException;
import fr.distrimind.oss.upnp.common.model.NetworkAddress;
import fr.distrimind.oss.upnp.common.model.gena.RemoteGENASubscription;
import fr.distrimind.oss.upnp.common.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.common.model.message.gena.IncomingSubscribeResponseMessage;
import fr.distrimind.oss.upnp.common.model.message.gena.OutgoingSubscribeRequestMessage;
import fr.distrimind.oss.upnp.common.UpnpService;

import java.util.List;
import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Establishing a GENA event subscription with a remote host.
 * <p>
 * Calls the {@link RemoteGENASubscription#establish()} method
 * if the subscription request was responded to correctly.
 * </p>
 * <p>
 * The {@link RemoteGENASubscription#fail(UpnpResponse)}
 * method will be called if the request failed. No response from the remote host is indicated with
 * a <code>null</code> argument value. Note that this is also the response if the subscription has
 * to be aborted early, when no local stream server for callback URL creation is available. This is
 * the case when the local network transport layer is switched off, subscriptions will fail
 * immediately with no response.
 * </p>
 *
 * @author Christian Bauer
 */
public class SendingSubscribe extends SendingSync<OutgoingSubscribeRequestMessage, IncomingSubscribeResponseMessage> {

    final private static DMLogger log = Log.getLogger(SendingSubscribe.class);

    final protected RemoteGENASubscription subscription;

    public SendingSubscribe(UpnpService upnpService,
                            RemoteGENASubscription subscription,
                            List<NetworkAddress> activeStreamServers) {
        super(
            upnpService,
            new OutgoingSubscribeRequestMessage(
                subscription,
                subscription.getEventCallbackURLs(
                    activeStreamServers,
                    upnpService.getConfiguration().getNamespace()
                ),
                upnpService.getConfiguration().getEventSubscriptionHeaders(subscription.getService())
            )
        );

        this.subscription = subscription;
    }

    @Override
	protected IncomingSubscribeResponseMessage executeSync() throws RouterException {

        if (!getInputMessage().hasCallbackURLs()) {
            log.debug("Subscription failed, no active local callback URLs available (network disabled?)");
            getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
					() -> subscription.fail(null)
			);
            return null;
        }

		if (log.isDebugEnabled()) {
            log.debug("Sending subscription request: " + getInputMessage());
		}

		try {
            // register this pending Subscription to bloc if the notification is received before the
            // registration result.
            getUpnpService().getRegistry().registerPendingRemoteSubscription(subscription);

            StreamResponseMessage response;
            try {
                response = getUpnpService().getRouter().send(getInputMessage());
            } catch (RouterException ex) {
                onSubscriptionFailure();
                return null;
            }

            if (response == null) {
                onSubscriptionFailure();
                return null;
            }

            final IncomingSubscribeResponseMessage responseMessage = new IncomingSubscribeResponseMessage(response);

            if (response.getOperation().isFailed()) {
				if (log.isDebugEnabled()) {
					log.debug("Subscription failed, response was: " + responseMessage);
				}
				getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
						() -> subscription.fail(responseMessage.getOperation())
				);
            } else if (!responseMessage.isValidHeaders()) {
                log.error("Subscription failed, invalid or missing (SID, Timeout) response headers");
                getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
						() -> subscription.fail(responseMessage.getOperation())
				);
            } else {

				if (log.isDebugEnabled()) {
					log.debug("Subscription established, adding to registry, response was: " + response);
				}
				subscription.setSubscriptionId(responseMessage.getSubscriptionId());
                subscription.setActualSubscriptionDurationSeconds(responseMessage.getSubscriptionDurationSeconds());

                getUpnpService().getRegistry().addRemoteSubscription(subscription);

                getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
						subscription::establish
				);

            }
            return responseMessage;
        } finally {
            getUpnpService().getRegistry().unregisterPendingRemoteSubscription(subscription);
        }
    }

    protected void onSubscriptionFailure() {
        log.debug("Subscription failed");
        getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
				() -> subscription.fail(null)
		);
    }
}
