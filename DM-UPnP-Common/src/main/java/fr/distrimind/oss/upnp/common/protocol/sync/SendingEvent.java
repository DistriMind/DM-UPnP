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

import java.util.ArrayList;
import java.util.Collection;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

import fr.distrimind.oss.upnp.common.protocol.SendingSync;
import fr.distrimind.oss.upnp.common.transport.RouterException;
import fr.distrimind.oss.upnp.common.UpnpService;
import fr.distrimind.oss.upnp.common.model.gena.LocalGENASubscription;
import fr.distrimind.oss.upnp.common.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.common.model.message.gena.OutgoingEventRequestMessage;
import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerFourBytes;

import java.net.URL;

/**
 * Sending GENA event messages to remote subscribers.
 * <p>
 * Any {@link LocalGENASubscription} instantiates and executes this protocol
 * when the state of a local service changes. However, a remote subscriber might require event
 * notification messages on more than one callback URL, so this protocol potentially sends
 * many messages. What is returned is always the last response, that is, the response for the
 * message sent to the last callback URL in the list of the subscriber.
 * </p>
 *
 * @author Christian Bauer
 */
public class SendingEvent extends SendingSync<OutgoingEventRequestMessage, StreamResponseMessage> {

    final private static DMLogger log = Log.getLogger(SendingEvent.class);

    final protected String subscriptionId;
    final protected Collection<OutgoingEventRequestMessage> requestMessages;
    final protected UnsignedIntegerFourBytes currentSequence;

    public SendingEvent(UpnpService upnpService, LocalGENASubscription<?> subscription) {
        super(upnpService, null); // Special case, we actually need to send several messages to each callback URL

        // TODO: Ugly design! It is critical (concurrency) that we prepare the event messages here, in the constructor thread!

        subscriptionId = subscription.getSubscriptionId();

        requestMessages = new ArrayList<>(subscription.getCallbackURLs().size());
        for (URL url : subscription.getCallbackURLs()) {
            OutgoingEventRequestMessage o = new OutgoingEventRequestMessage(subscription, url);
            getUpnpService().getConfiguration().getGenaEventProcessor().writeBody(o);
            requestMessages.add(o);
        }

        currentSequence = subscription.getCurrentSequence();

        // Always increment sequence now, as (its value) has already been set on the headers and the
        // next event will use the incremented value
        subscription.incrementSequence();
    }

    @Override
	protected StreamResponseMessage executeSync() throws RouterException {

		if (log.isDebugEnabled()) {
            log.debug("Sending event for subscription: " + subscriptionId);
		}

		StreamResponseMessage lastResponse = null;

        for (OutgoingEventRequestMessage requestMessage : requestMessages) {

            if (currentSequence.getValue() == 0) {
				if (log.isDebugEnabled()) {
					log.debug("Sending initial event message to callback URL: " + requestMessage.getUri());
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Sending event message '"+currentSequence+"' to callback URL: " + requestMessage.getUri());
				}
			}


            // Send request
            lastResponse = getUpnpService().getRouter().send(requestMessage);
			if (log.isDebugEnabled()) {
				log.debug("Received event callback response: " + lastResponse);
			}

		}

        // It's not really used, so just return the last one - we have only one callback URL most of the
        // time anyway
        return lastResponse;

    }
}