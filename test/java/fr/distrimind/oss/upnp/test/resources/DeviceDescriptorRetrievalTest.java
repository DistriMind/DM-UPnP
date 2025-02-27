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

package fr.distrimind.oss.upnp.test.resources;

import fr.distrimind.oss.upnp.binding.xml.DeviceDescriptorBinder;
import fr.distrimind.oss.upnp.mock.MockUpnpService;
import fr.distrimind.oss.upnp.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.model.meta.RemoteDevice;
import fr.distrimind.oss.upnp.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.model.message.UpnpRequest;
import fr.distrimind.oss.upnp.model.message.header.ContentTypeHeader;
import fr.distrimind.oss.upnp.model.message.header.HostHeader;
import fr.distrimind.oss.upnp.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.protocol.sync.ReceivingRetrieval;
import fr.distrimind.oss.upnp.test.data.SampleData;
import fr.distrimind.oss.upnp.test.data.SampleDeviceRoot;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class DeviceDescriptorRetrievalTest {

    @Test
    public void registerAndRetrieveDescriptor() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        // Register a device
        LocalDevice<?> localDevice = SampleData.createLocalDevice();
        upnpService.getRegistry().addDevice(localDevice);

        // Retrieve the descriptor
        StreamRequestMessage descRetrievalMessage = new StreamRequestMessage(UpnpRequest.Method.GET, SampleDeviceRoot.getDeviceDescriptorURI());
        descRetrievalMessage.getHeaders().add(UpnpHeader.Type.HOST, new HostHeader("localhost", 1234));
        ReceivingRetrieval prot = new ReceivingRetrieval(upnpService, descRetrievalMessage);
        prot.run();
        StreamResponseMessage descriptorMessage = prot.getOutputMessage();

        // UDA 1.0 spec days this musst be 'text/xml'
        assertEquals(
                descriptorMessage.getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE).getValue(),
                ContentTypeHeader.DEFAULT_CONTENT_TYPE
        );

        // Read the response and compare the returned device descriptor (test with LocalDevice for correct assertions)
        DeviceDescriptorBinder binder = upnpService.getConfiguration().getDeviceDescriptorBinderUDA10();

        RemoteDevice returnedDevice =
                new RemoteDevice(SampleData.createRemoteDeviceIdentity());
        returnedDevice = binder.describe(returnedDevice, descriptorMessage.getBodyString());

        SampleDeviceRoot.assertLocalResourcesMatch(
                upnpService.getConfiguration().getNamespace().getResources(returnedDevice)
        );
    }

    @Test
    public void retrieveNonExistentDescriptor() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        // Retrieve the descriptor
        StreamRequestMessage descRetrievalMessage = new StreamRequestMessage(UpnpRequest.Method.GET, SampleDeviceRoot.getDeviceDescriptorURI());
        descRetrievalMessage.getHeaders().add(UpnpHeader.Type.HOST, new HostHeader("localhost", 1234));
        ReceivingRetrieval prot = new ReceivingRetrieval(upnpService, descRetrievalMessage);
        prot.run();
        StreamResponseMessage descriptorMessage = prot.getOutputMessage();

        assertNull(descriptorMessage);
    }

}
