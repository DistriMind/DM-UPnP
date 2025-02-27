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
package fr.distrimind.oss.upnp.mock;

import fr.distrimind.oss.upnp.model.action.ActionInvocation;
import fr.distrimind.oss.upnp.model.gena.LocalGENASubscription;
import fr.distrimind.oss.upnp.model.gena.RemoteGENASubscription;
import fr.distrimind.oss.upnp.model.message.IncomingDatagramMessage;
import fr.distrimind.oss.upnp.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.protocol.ProtocolCreationException;
import fr.distrimind.oss.upnp.protocol.ProtocolFactory;
import fr.distrimind.oss.upnp.protocol.ReceivingAsync;
import fr.distrimind.oss.upnp.protocol.ReceivingSync;
import fr.distrimind.oss.upnp.protocol.async.SendingNotificationAlive;
import fr.distrimind.oss.upnp.protocol.async.SendingNotificationByebye;
import fr.distrimind.oss.upnp.protocol.async.SendingSearch;
import fr.distrimind.oss.upnp.UpnpService;
import fr.distrimind.oss.upnp.protocol.sync.SendingAction;
import fr.distrimind.oss.upnp.protocol.sync.SendingEvent;
import fr.distrimind.oss.upnp.protocol.sync.SendingRenewal;
import fr.distrimind.oss.upnp.protocol.sync.SendingSubscribe;
import fr.distrimind.oss.upnp.protocol.sync.SendingUnsubscribe;

import jakarta.enterprise.inject.Alternative;
import java.net.URL;

/**
 * @author Christian Bauer
 */
@Alternative
public class MockProtocolFactory implements ProtocolFactory {

    @Override
    public UpnpService getUpnpService() {
        return null;
    }

    @Override
    public ReceivingAsync<?> createReceivingAsync(IncomingDatagramMessage<?> message) throws ProtocolCreationException {
        return null;
    }

    @Override
    public ReceivingSync<?, ?> createReceivingSync(StreamRequestMessage requestMessage) throws ProtocolCreationException {
        return null;
    }

    @Override
    public <T> SendingNotificationAlive createSendingNotificationAlive(LocalDevice<T> localDevice) {
        return null;
    }

    @Override
    public <T> SendingNotificationByebye createSendingNotificationByebye(LocalDevice<T> localDevice) {
        return null;
    }

    @Override
    public SendingSearch createSendingSearch(UpnpHeader<?> searchTarget, int mxSeconds) {
        return null;
    }

    @Override
    public SendingAction createSendingAction(ActionInvocation<?> actionInvocation, URL controlURL) {
        return null;
    }

    @Override
    public SendingSubscribe createSendingSubscribe(RemoteGENASubscription subscription) {
        return null;
    }

    @Override
    public SendingRenewal createSendingRenewal(RemoteGENASubscription subscription) {
        return null;
    }

    @Override
    public SendingUnsubscribe createSendingUnsubscribe(RemoteGENASubscription subscription) {
        return null;
    }

    @Override
    public SendingEvent createSendingEvent(LocalGENASubscription<?> subscription) {
        return null;
    }
}
