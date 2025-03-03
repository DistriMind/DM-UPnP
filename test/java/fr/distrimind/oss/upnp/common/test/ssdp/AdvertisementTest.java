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

package fr.distrimind.oss.upnp.common.test.ssdp;

import fr.distrimind.oss.upnp.common.mock.MockUpnpService;
import fr.distrimind.oss.upnp.common.model.ServerClientTokens;
import fr.distrimind.oss.upnp.common.model.message.OutgoingDatagramMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpMessage;
import fr.distrimind.oss.upnp.common.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.common.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.common.model.types.NotificationSubtype;
import fr.distrimind.oss.upnp.common.protocol.async.SendingNotificationAlive;
import fr.distrimind.oss.upnp.common.protocol.async.SendingNotificationByebye;
import fr.distrimind.oss.upnp.common.test.data.SampleData;
import fr.distrimind.oss.upnp.common.test.data.SampleDeviceRoot;
import fr.distrimind.oss.upnp.common.test.data.SampleUSNHeaders;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;


public class AdvertisementTest {

    @Test
    public void sendAliveMessages() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        LocalDevice<?> rootDevice = SampleData.createLocalDevice();
        LocalDevice<?> embeddedDevice = rootDevice.getEmbeddedDevices().iterator().next();

        SendingNotificationAlive prot = new SendingNotificationAlive(upnpService, rootDevice);
        prot.run();

        for (OutgoingDatagramMessage<?> msg : upnpService.getRouter().getOutgoingDatagramMessages()) {
            assertAliveMsgBasics(msg);
            //SampleData.debugMsg(msg);
        }

        SampleUSNHeaders.assertUSNHeaders(
            upnpService.getRouter().getOutgoingDatagramMessages(),
            rootDevice, embeddedDevice, UpnpHeader.Type.NT);
    }

    @Test
    public void sendByebyeMessages() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        LocalDevice<?> rootDevice = SampleData.createLocalDevice();
        LocalDevice<?> embeddedDevice = rootDevice.getEmbeddedDevices().iterator().next();

        SendingNotificationByebye prot = new SendingNotificationByebye(upnpService, rootDevice);
        prot.run();

        for (OutgoingDatagramMessage<?> msg : upnpService.getRouter().getOutgoingDatagramMessages()) {
            assertByebyeMsgBasics(msg);
            //SampleData.debugMsg(msg);
        }

        SampleUSNHeaders.assertUSNHeaders(
            upnpService.getRouter().getOutgoingDatagramMessages(),
            rootDevice, embeddedDevice, UpnpHeader.Type.NT);
    }

    protected void assertAliveMsgBasics(UpnpMessage<?> msg) {
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.NTS).getValue(), NotificationSubtype.ALIVE);
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.LOCATION).getValue().toString(), SampleDeviceRoot.getDeviceDescriptorURL().toString());
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.MAX_AGE).getValue(), 1800);
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.SERVER).getValue(), new ServerClientTokens());
    }

    protected void assertByebyeMsgBasics(UpnpMessage<?> msg) {
        assertEquals(msg.getHeaders().getFirstHeader(UpnpHeader.Type.NTS).getValue(), NotificationSubtype.BYEBYE);
    }

}