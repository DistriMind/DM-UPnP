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

package fr.distrimind.oss.upnp.test.ssdp;

import fr.distrimind.oss.upnp.UpnpService;
import fr.distrimind.oss.upnp.mock.MockUpnpService;
import fr.distrimind.oss.upnp.model.Constants;
import fr.distrimind.oss.upnp.model.message.IncomingDatagramMessage;
import fr.distrimind.oss.upnp.model.message.UpnpRequest;
import fr.distrimind.oss.upnp.model.message.discovery.IncomingNotificationRequest;
import fr.distrimind.oss.upnp.model.message.header.HostHeader;
import fr.distrimind.oss.upnp.model.message.header.LocationHeader;
import fr.distrimind.oss.upnp.model.message.header.MaxAgeHeader;
import fr.distrimind.oss.upnp.model.message.header.NTSHeader;
import fr.distrimind.oss.upnp.model.message.header.RootDeviceHeader;
import fr.distrimind.oss.upnp.model.message.header.UDNHeader;
import fr.distrimind.oss.upnp.model.message.header.USNRootDeviceHeader;
import fr.distrimind.oss.upnp.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.model.meta.RemoteDevice;
import fr.distrimind.oss.upnp.model.types.NotificationSubtype;
import fr.distrimind.oss.upnp.test.data.SampleData;
import fr.distrimind.oss.upnp.test.data.SampleDeviceRoot;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.testng.Assert.assertEquals;

public class NotifyTest {

    @Test
    public void receivedByeBye() throws Exception {

        UpnpService upnpService = new MockUpnpService();

        RemoteDevice rd = SampleData.createRemoteDevice();
        upnpService.getRegistry().addDevice(rd);
        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        IncomingNotificationRequest msg = createRequestMessage();
        msg.getHeaders().add(UpnpHeader.Type.NT, new RootDeviceHeader());
        msg.getHeaders().add(UpnpHeader.Type.NTS, new NTSHeader(NotificationSubtype.BYEBYE));
        msg.getHeaders().add(UpnpHeader.Type.USN, new USNRootDeviceHeader(rd.getIdentity().getUdn()));

        upnpService.getProtocolFactory().createReceivingAsync(msg).run();

        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 0);
    }

    @Test
    public void receivedNoUDN() throws Exception {

        UpnpService upnpService = new MockUpnpService();

        RemoteDevice rd = SampleData.createRemoteDevice();
        upnpService.getRegistry().addDevice(rd);

        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        IncomingNotificationRequest msg = createRequestMessage();
        msg.getHeaders().add(UpnpHeader.Type.NT, new RootDeviceHeader());
        msg.getHeaders().add(UpnpHeader.Type.NTS, new NTSHeader(NotificationSubtype.BYEBYE));
        // This is what we are testing, the missing header!
        // msg.getHeaders().add(UpnpHeader.Type.USN, new USNRootDeviceHeader(rd.getIdentity().getUdn()));

        upnpService.getProtocolFactory().createReceivingAsync(msg).run();

        // This should be unchanged from earlier state
        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);
    }

    @Test
    public void receivedNoLocation() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        RemoteDevice rd = SampleData.createRemoteDevice();

        IncomingNotificationRequest msg = createRequestMessage();
        msg.getHeaders().add(UpnpHeader.Type.NTS, new NTSHeader(NotificationSubtype.ALIVE));
        msg.getHeaders().add(UpnpHeader.Type.NT, new RootDeviceHeader());
        msg.getHeaders().add(UpnpHeader.Type.USN, new USNRootDeviceHeader(rd.getIdentity().getUdn()));
        msg.getHeaders().add(UpnpHeader.Type.MAX_AGE, new MaxAgeHeader(rd.getIdentity().getMaxAgeSeconds()));
        // We test the missing header
        //msg.getHeaders().add(UpnpHeader.Type.LOCATION, new LocationHeader(SampleDeviceRoot.getDeviceDescriptorURL()));

        upnpService.getProtocolFactory().createReceivingAsync(msg).run();

        Thread.sleep(100);
        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 0);
    }

    @Test
    public void receivedNoMaxAge() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        RemoteDevice rd = SampleData.createRemoteDevice();

        IncomingNotificationRequest msg = createRequestMessage();
        msg.getHeaders().add(UpnpHeader.Type.NTS, new NTSHeader(NotificationSubtype.ALIVE));
        msg.getHeaders().add(UpnpHeader.Type.NT, new RootDeviceHeader());
        msg.getHeaders().add(UpnpHeader.Type.USN, new USNRootDeviceHeader(rd.getIdentity().getUdn()));
        msg.getHeaders().add(UpnpHeader.Type.LOCATION, new LocationHeader(SampleDeviceRoot.getDeviceDescriptorURL()));
        // We test the missing header
        //msg.getHeaders().add(UpnpHeader.Type.MAX_AGE, new MaxAgeHeader(rd.getIdentity().getMaxAgeSeconds()));

        upnpService.getProtocolFactory().createReceivingAsync(msg).run();

        Thread.sleep(100);
        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 0);
    }

    @Test
    public void receivedAlreadyKnownLocalUDN() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        LocalDevice<?> localDevice = SampleData.createLocalDevice();
        upnpService.getRegistry().addDevice(localDevice);

        RemoteDevice rd = SampleData.createRemoteDevice();

        IncomingNotificationRequest msg = createRequestMessage();
        msg.getHeaders().add(UpnpHeader.Type.NTS, new NTSHeader(NotificationSubtype.ALIVE));
        msg.getHeaders().add(UpnpHeader.Type.NT, new RootDeviceHeader());
        msg.getHeaders().add(UpnpHeader.Type.USN, new USNRootDeviceHeader(rd.getIdentity().getUdn()));
        msg.getHeaders().add(UpnpHeader.Type.LOCATION, new LocationHeader(SampleDeviceRoot.getDeviceDescriptorURL()));
        msg.getHeaders().add(UpnpHeader.Type.MAX_AGE, new MaxAgeHeader(rd.getIdentity().getMaxAgeSeconds()));

        upnpService.getProtocolFactory().createReceivingAsync(msg).run();

        Thread.sleep(100);
        assertEquals(upnpService.getRouter().getSentStreamRequestMessages().size(), 0);
    }

    @Test
    public void receiveEmbeddedTriggersUpdate() throws Exception {

        UpnpService upnpService = new MockUpnpService(false, true);

        RemoteDevice rd = SampleData.createRemoteDevice(
                SampleData.createRemoteDeviceIdentity(2)
        );
        RemoteDevice embedded = rd.getEmbeddedDevices().iterator().next();

        upnpService.getRegistry().addDevice(rd);

        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        IncomingNotificationRequest msg = createRequestMessage();
        msg.getHeaders().add(UpnpHeader.Type.NTS, new NTSHeader(NotificationSubtype.ALIVE));
        msg.getHeaders().add(UpnpHeader.Type.NT, new UDNHeader(embedded.getIdentity().getUdn()));
        msg.getHeaders().add(UpnpHeader.Type.USN, new UDNHeader(embedded.getIdentity().getUdn()));
        msg.getHeaders().add(UpnpHeader.Type.LOCATION, new LocationHeader(SampleDeviceRoot.getDeviceDescriptorURL()));
        msg.getHeaders().add(UpnpHeader.Type.MAX_AGE, new MaxAgeHeader(rd.getIdentity().getMaxAgeSeconds()));

        Thread.sleep(1000);
        upnpService.getProtocolFactory().createReceivingAsync(msg).run();

        Thread.sleep(1000);
        upnpService.getProtocolFactory().createReceivingAsync(msg).run();

        Thread.sleep(1000);
        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        upnpService.shutdown();
    }

    protected IncomingNotificationRequest createRequestMessage() throws UnknownHostException {
        IncomingNotificationRequest msg = new IncomingNotificationRequest(
                new IncomingDatagramMessage<>(
                        new UpnpRequest(UpnpRequest.Method.NOTIFY),
                        InetAddress.getByName("127.0.0.1"),
                        Constants.UPNP_MULTICAST_PORT,
                        InetAddress.getByName("127.0.0.1")
                )
        );

        msg.getHeaders().add(UpnpHeader.Type.HOST, new HostHeader());
        return msg;

    }

}