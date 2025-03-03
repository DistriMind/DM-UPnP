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

package fr.distrimind.oss.upnp.common.test.protocol;

import fr.distrimind.oss.upnp.common.protocol.ProtocolCreationException;
import fr.distrimind.oss.upnp.common.mock.MockUpnpService;
import fr.distrimind.oss.upnp.common.model.Namespace;
import fr.distrimind.oss.upnp.common.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpRequest;
import fr.distrimind.oss.upnp.common.protocol.ReceivingSync;
import fr.distrimind.oss.upnp.common.protocol.sync.ReceivingEvent;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.*;

/**
 * @author Christian Bauer
 */
public class ProtocolFactoryTest {

    @Test(expectedExceptions = ProtocolCreationException.class)
    public void noSyncProtocol() throws Exception {
        MockUpnpService upnpService = new MockUpnpService();

        upnpService.getProtocolFactory().createReceivingSync(
            new StreamRequestMessage(
                UpnpRequest.Method.NOTIFY,
                URI.create("/dev/1234/upnp-org/SwitchPower/invalid"),
                ""
            )
        );
    }

    @Test
    public void receivingEvent() throws Exception {
        MockUpnpService upnpService = new MockUpnpService();

        StreamRequestMessage message = new StreamRequestMessage(
            UpnpRequest.Method.NOTIFY,
            URI.create("/dev/1234/upnp-org/SwitchPower" + Namespace.EVENTS + Namespace.CALLBACK_FILE),
            ""
        );
        ReceivingSync<?, ?> protocol = upnpService.getProtocolFactory().createReceivingSync(message);
        assertTrue(protocol instanceof ReceivingEvent);

        // TODO: UPNP VIOLATION: Onkyo devices send event messages with trailing garbage characters
        // dev/1234/svc/upnp-org/MyService/event/callback192%2e168%2e10%2e38
        message = new StreamRequestMessage(
            UpnpRequest.Method.NOTIFY,
            URI.create("/dev/1234/upnp-org/SwitchPower" + Namespace.EVENTS + Namespace.CALLBACK_FILE + "192%2e168%2e10%2e38"),
            ""
        );
        protocol = upnpService.getProtocolFactory().createReceivingSync(message);
        assertTrue(protocol instanceof ReceivingEvent);

    }
}