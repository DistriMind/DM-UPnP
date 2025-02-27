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

package fr.distrimind.oss.upnp.protocol.sync;

import fr.distrimind.oss.upnp.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.protocol.SendingSync;
import fr.distrimind.oss.upnp.transport.RouterException;
import fr.distrimind.oss.upnp.UpnpService;
import fr.distrimind.oss.upnp.model.gena.CancelReason;
import fr.distrimind.oss.upnp.model.gena.RemoteGENASubscription;
import fr.distrimind.oss.upnp.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.model.message.gena.IncomingSubscribeResponseMessage;
import fr.distrimind.oss.upnp.model.message.gena.OutgoingRenewalRequestMessage;

import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.Log;

/**
 * Renewing a GENA event subscription with a remote host.
 * <p>
 * This protocol is executed periodically by the local registry, for any established GENA
 * subscription to a remote service. If renewal failed, the subscription will be removed
 * from the registry and the
 * {@link RemoteGENASubscription#end(CancelReason, UpnpResponse)}
 * method will be called. The <code>RENEWAL_FAILED</code> reason will be used, however,
 * the response might be <code>null</code> if no response was received from the remote host.
 * </p>
 * @author Christian Bauer
 */
public class SendingRenewal extends SendingSync<OutgoingRenewalRequestMessage, IncomingSubscribeResponseMessage> {

    final private static DMLogger log = Log.getLogger(SendingRenewal.class);

    final protected RemoteGENASubscription subscription;

    public SendingRenewal(UpnpService upnpService, RemoteGENASubscription subscription) {
        super(
            upnpService,
            new OutgoingRenewalRequestMessage(
                subscription,
                upnpService.getConfiguration().getEventSubscriptionHeaders(subscription.getService())
            )
        );
        this.subscription = subscription;
    }

    @Override
	protected IncomingSubscribeResponseMessage executeSync() throws RouterException {
		if (log.isDebugEnabled()) {
            log.debug("Sending subscription renewal request: " + getInputMessage());
		}

		StreamResponseMessage response;
        try {
            response = getUpnpService().getRouter().send(getInputMessage());
        } catch (RouterException ex) {
            onRenewalFailure();
            throw ex;
        }

        if (response == null) {
            onRenewalFailure();
            return null;
        }

        final IncomingSubscribeResponseMessage responseMessage = new IncomingSubscribeResponseMessage(response);

        if (response.getOperation().isFailed()) {
			if (log.isDebugEnabled()) {
				log.debug("Subscription renewal failed, response was: " + response);
			}
			getUpnpService().getRegistry().removeRemoteSubscription(subscription);
            getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
					() -> subscription.end(CancelReason.RENEWAL_FAILED,responseMessage.getOperation())
			);
        } else if (!responseMessage.isValidHeaders()) {
            log.error("Subscription renewal failed, invalid or missing (SID, Timeout) response headers");
            getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
					() -> subscription.end(CancelReason.RENEWAL_FAILED, responseMessage.getOperation())
			);
        } else {
			if (log.isDebugEnabled()) {
				log.debug("Subscription renewed, updating in registry, response was: " + response);
			}
			subscription.setActualSubscriptionDurationSeconds(responseMessage.getSubscriptionDurationSeconds());
            getUpnpService().getRegistry().updateRemoteSubscription(subscription);
        }

        return responseMessage;
    }

    protected void onRenewalFailure() {
        log.debug("Subscription renewal failed, removing subscription from registry");
        getUpnpService().getRegistry().removeRemoteSubscription(subscription);
        getUpnpService().getConfiguration().getRegistryListenerExecutor().execute(
				() -> subscription.end(CancelReason.RENEWAL_FAILED, null)
		);
    }
}