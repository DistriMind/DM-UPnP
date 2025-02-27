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

import fr.distrimind.oss.upnp.protocol.ReceivingSync;
import fr.distrimind.oss.upnp.transport.RouterException;
import fr.distrimind.oss.upnp.UpnpService;
import fr.distrimind.oss.upnp.model.gena.LocalGENASubscription;
import fr.distrimind.oss.upnp.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.model.message.gena.IncomingUnsubscribeRequestMessage;
import fr.distrimind.oss.upnp.model.resource.ServiceEventSubscriptionResource;

import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.Log;

/**
 * Handles reception of GENA event unsubscribe messages.
 *
 * @author Christian Bauer
 */
public class ReceivingUnsubscribe extends ReceivingSync<StreamRequestMessage, StreamResponseMessage> {

    final private static DMLogger log = Log.getLogger(ReceivingUnsubscribe.class);

    public ReceivingUnsubscribe(UpnpService upnpService, StreamRequestMessage inputMessage) {
        super(upnpService, inputMessage);
    }

    @Override
	protected StreamResponseMessage executeSync() throws RouterException {

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

		IncomingUnsubscribeRequestMessage requestMessage =
                new IncomingUnsubscribeRequestMessage(getInputMessage(), resource.getModel());

        // Error conditions UDA 1.0 section 4.1.3
        if (requestMessage.getSubscriptionId() != null &&
                (requestMessage.hasNotificationHeader() || requestMessage.hasCallbackHeader())) {
			if (log.isDebugEnabled()) {
				log.debug("Subscription ID and NT or Callback in unsubcribe request: " + getInputMessage());
			}
			return new StreamResponseMessage(UpnpResponse.Status.BAD_REQUEST);
        }

        LocalGENASubscription<?> subscription =
                getUpnpService().getRegistry().getLocalSubscription(requestMessage.getSubscriptionId());

        if (subscription == null) {
			if (log.isDebugEnabled()) {
				log.debug("Invalid subscription ID for unsubscribe request: " + getInputMessage());
			}
			return new StreamResponseMessage(UpnpResponse.Status.PRECONDITION_FAILED);
        }

		if (log.isDebugEnabled()) {
            log.debug("Unregistering subscription: " + subscription);
		}
		if (getUpnpService().getRegistry().removeLocalSubscription(subscription)) {
            subscription.end(null); // No reason, just an unsubscribed
        } else {
            log.debug("Subscription was already removed from registry");
        }

        return new StreamResponseMessage(UpnpResponse.Status.OK);
    }
}