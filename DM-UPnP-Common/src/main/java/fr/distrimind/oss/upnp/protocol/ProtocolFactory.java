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

package fr.distrimind.oss.upnp.protocol;

import fr.distrimind.oss.upnp.controlpoint.ControlPoint;
import fr.distrimind.oss.upnp.model.gena.GENASubscription;
import fr.distrimind.oss.upnp.model.message.UpnpRequest;
import fr.distrimind.oss.upnp.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.protocol.async.*;
import fr.distrimind.oss.upnp.protocol.sync.*;
import fr.distrimind.oss.upnp.registry.Registry;
import fr.distrimind.oss.upnp.UpnpService;
import fr.distrimind.oss.upnp.model.action.ActionInvocation;
import fr.distrimind.oss.upnp.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.model.gena.LocalGENASubscription;
import fr.distrimind.oss.upnp.model.gena.RemoteGENASubscription;
import fr.distrimind.oss.upnp.model.message.IncomingDatagramMessage;
import fr.distrimind.oss.upnp.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.model.message.header.UpnpHeader;

import java.net.URL;

/**
 * Factory for UPnP protocols, the core implementation of the UPnP specification.
 * <p>
 * This factory creates an executable protocol either based on the received UPnP messsage, or
 * on local device/search/service metadata). A protocol is an aspect of the UPnP specification,
 * you can override individual protocols to customize the behavior of the UPnP stack.
 * </p>
 * <p>
 * An implementation has to be thread-safe.
 * </p>
 * 
 * @author Christian Bauer
 */
public interface ProtocolFactory {

    UpnpService getUpnpService();

    /**
     * Creates a {@link ReceivingNotification},
     * {@link ReceivingSearch},
     * or {@link ReceivingSearchResponse} protocol.
     *
     * @param message The incoming message, either {@link UpnpRequest} or
     *                {@link UpnpResponse}.
     * @return        The appropriate protocol that handles the messages or <code>null</code> if the message should be dropped.
     * @throws ProtocolCreationException If no protocol could be found for the message.
     */
	ReceivingAsync<?> createReceivingAsync(IncomingDatagramMessage<?> message) throws ProtocolCreationException;

    /**
     * Creates a {@link ReceivingRetrieval},
     * {@link ReceivingAction},
     * {@link ReceivingSubscribe},
     * {@link ReceivingUnsubscribe}, or
     * {@link ReceivingEvent} protocol.
     *
     * @param requestMessage The incoming message, examime {@link UpnpRequest.Method}
     *                       to determine the protocol.
     * @return        The appropriate protocol that handles the messages.
     * @throws ProtocolCreationException If no protocol could be found for the message.
     */
	ReceivingSync<?, ?> createReceivingSync(StreamRequestMessage requestMessage) throws ProtocolCreationException;

    /**
     * Called by the {@link Registry}, creates a protocol for announcing local devices.
     */
	<T> SendingNotificationAlive createSendingNotificationAlive(LocalDevice<T> localDevice);

    /**
     * Called by the {@link Registry}, creates a protocol for announcing local devices.
     */
	<T> SendingNotificationByebye createSendingNotificationByebye(LocalDevice<T> localDevice);

    /**
     * Called by the {@link ControlPoint}, creates a protocol for a multicast search.
     */
	SendingSearch createSendingSearch(UpnpHeader<?> searchTarget, int mxSeconds);

    /**
     * Called by the {@link ControlPoint}, creates a protocol for executing an action.
     */
	SendingAction createSendingAction(ActionInvocation<?> actionInvocation, URL controlURL);

    /**
     * Called by the {@link ControlPoint}, creates a protocol for GENA subscription.
     */
	SendingSubscribe createSendingSubscribe(RemoteGENASubscription subscription) throws ProtocolCreationException;

    /**
     * Called by the {@link ControlPoint}, creates a protocol for GENA renewal.
     */
	SendingRenewal createSendingRenewal(RemoteGENASubscription subscription);

    /**
     * Called by the {@link ControlPoint}, creates a protocol for GENA unsubscription.
     */
	SendingUnsubscribe createSendingUnsubscribe(RemoteGENASubscription subscription);

    /**
     * Called by the {@link GENASubscription}, creates a protocol for sending GENA events.
     */
	SendingEvent createSendingEvent(LocalGENASubscription<?> subscription);
}
