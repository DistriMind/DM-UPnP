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
import fr.distrimind.oss.upnp.common.UpnpService;
import fr.distrimind.oss.upnp.common.model.gena.CancelReason;
import fr.distrimind.oss.upnp.common.model.gena.RemoteGENASubscription;
import fr.distrimind.oss.upnp.common.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.common.model.message.gena.OutgoingUnsubscribeRequestMessage;

import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Disconnecting a GENA event subscription with a remote host.
 * <p>
 * Calls the {@link RemoteGENASubscription#end(CancelReason, UpnpResponse)}
 * method if the subscription request was responded to correctly. No {@link CancelReason}
 * will be provided if the unsubscribe procedure completed as expected, otherwise <code>UNSUBSCRIBE_FAILED</code>
 * is used. The response might be <code>null</code> if no response was received from the remote host.
 * </p>
 *
 * @author Christian Bauer
 */
public class SendingUnsubscribe extends SendingSync<OutgoingUnsubscribeRequestMessage, StreamResponseMessage> {

    final private static DMLogger log = Log.getLogger(SendingUnsubscribe.class);

    final protected RemoteGENASubscription subscription;

    public SendingUnsubscribe(UpnpService upnpService, RemoteGENASubscription subscription) {
        super(
            upnpService,
            new OutgoingUnsubscribeRequestMessage(
                subscription,
                upnpService.getConfiguration().getEventSubscriptionHeaders(subscription.getService())
            )
        );
        this.subscription = subscription;
    }

    @Override
	protected StreamResponseMessage executeSync() throws RouterException {

		if (log.isDebugEnabled()) {
            log.debug("Sending unsubscribe request: " + getInputMessage());
		}

		StreamResponseMessage response = null;
        try {
            response = getUpnpService().getRouter().send(getInputMessage());
            return response;
        } finally {
            onUnsubscribe(response);
        }
    }

    protected void onUnsubscribe(final StreamResponseMessage response) {
        // Always remove from the registry and end the subscription properly - even if it's failed
        getUpnpService().getRegistry().removeRemoteSubscription(subscription);

        getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
				() -> {
					if (response == null) {
						log.debug("Unsubscribe failed, no response received");
						subscription.end(CancelReason.UNSUBSCRIBE_FAILED, null);
					} else if (response.getOperation().isFailed()) {
						if (log.isDebugEnabled()) {
							log.debug("Unsubscribe failed, response was: " + response);
						}
						subscription.end(CancelReason.UNSUBSCRIBE_FAILED, response.getOperation());
					} else {
						if (log.isDebugEnabled()) {
							log.debug("Unsubscribe successful, response was: " + response);
						}
						subscription.end(null, response.getOperation());
					}
				}
		);
    }
}