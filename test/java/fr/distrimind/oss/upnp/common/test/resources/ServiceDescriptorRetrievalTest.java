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

package fr.distrimind.oss.upnp.common.test.resources;

import fr.distrimind.oss.upnp.common.binding.xml.ServiceDescriptorBinder;
import fr.distrimind.oss.upnp.common.mock.MockUpnpService;
import fr.distrimind.oss.upnp.common.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;
import fr.distrimind.oss.upnp.common.model.meta.RemoteService;
import fr.distrimind.oss.upnp.common.model.meta.Service;
import fr.distrimind.oss.upnp.common.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpRequest;
import fr.distrimind.oss.upnp.common.model.message.header.ContentTypeHeader;
import fr.distrimind.oss.upnp.common.model.message.header.HostHeader;
import fr.distrimind.oss.upnp.common.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.common.protocol.sync.ReceivingRetrieval;
import fr.distrimind.oss.upnp.common.test.data.SampleData;
import fr.distrimind.oss.upnp.common.test.data.SampleServiceOne;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.*;


public class ServiceDescriptorRetrievalTest {

    @Test
    public void registerAndRetrieveDescriptor() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        // Register a device
        LocalDevice<?> localDevice = SampleData.createLocalDevice();
        LocalService<?> service = SampleData.getFirstService(localDevice);
        upnpService.getRegistry().addDevice(localDevice);

        // Retrieve the descriptor
        URI descriptorURI = upnpService.getConfiguration().getNamespace().getDescriptorPath(service);
        StreamRequestMessage descRetrievalMessage = new StreamRequestMessage(UpnpRequest.Method.GET, descriptorURI);
        descRetrievalMessage.getHeaders().add(UpnpHeader.Type.HOST, new HostHeader("localhost", 1234));
        ReceivingRetrieval prot = new ReceivingRetrieval(upnpService, descRetrievalMessage);
        prot.run();
        StreamResponseMessage descriptorMessage = prot.getOutputMessage();

        // UDA 1.0 spec days this musst be 'text/xml'
        assertEquals(
                descriptorMessage.getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE).getValue(),
                ContentTypeHeader.DEFAULT_CONTENT_TYPE
        );

        // Read the response and compare the returned device descriptor
        ServiceDescriptorBinder binder = upnpService.getConfiguration().getServiceDescriptorBinderUDA10();

        RemoteService remoteService = SampleData.createUndescribedRemoteService();

        remoteService = binder.describe(remoteService, descriptorMessage.getBodyString());
        SampleServiceOne.assertMatch(remoteService, service);
    }

    @Test
    public void retrieveNonExistentDescriptor() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        // Retrieve the descriptor
        LocalDevice<?> localDevice = SampleData.createLocalDevice();
        Service<?, ?, ?> service = SampleData.getFirstService(localDevice);

        URI descriptorURI = upnpService.getConfiguration().getNamespace().getDescriptorPath(service);
        StreamRequestMessage descRetrievalMessage =
                new StreamRequestMessage(UpnpRequest.Method.GET, descriptorURI);
        descRetrievalMessage.getHeaders().add(UpnpHeader.Type.HOST, new HostHeader("localhost", 1234));
        ReceivingRetrieval prot = new ReceivingRetrieval(upnpService, descRetrievalMessage);
        prot.run();
        StreamResponseMessage descriptorMessage = prot.getOutputMessage();

        // Should be null because it can't be found
        assertNull(descriptorMessage);

    }

}