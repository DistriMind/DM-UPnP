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

import fr.distrimind.oss.upnp.binding.xml.ServiceDescriptorBinder;
import fr.distrimind.oss.upnp.binding.xml.UDA10ServiceDescriptorBinderImpl;
import fr.distrimind.oss.upnp.binding.xml.UDA10ServiceDescriptorBinderSAXImpl;
import fr.distrimind.oss.upnp.model.meta.RemoteDevice;
import fr.distrimind.oss.upnp.model.meta.RemoteService;
import fr.distrimind.oss.upnp.transport.impl.NetworkAddressFactoryImpl;
import fr.distrimind.oss.upnp.test.data.SampleData;
import fr.distrimind.oss.upnp.test.data.SampleServiceOne;
import fr.distrimind.oss.upnp.util.io.IO;
import org.testng.annotations.Test;


public class UDA10ServiceDescriptorParsingTest {

    @Test
    public void readUDA10DescriptorDOM() throws Exception {

        ServiceDescriptorBinder binder = new UDA10ServiceDescriptorBinderImpl(new NetworkAddressFactoryImpl());

        RemoteService service = SampleData.createUndescribedRemoteService();

        service = binder.describe(service, IO.readLines(getClass().getResourceAsStream("/descriptors/service/uda10.xml")));

        SampleServiceOne.assertMatch(service, SampleData.getFirstService(SampleData.createRemoteDevice()));
    }

    @Test
    public void readUDA10DescriptorSAX() throws Exception {

        ServiceDescriptorBinder binder = new UDA10ServiceDescriptorBinderSAXImpl(new NetworkAddressFactoryImpl());

        RemoteService service = SampleData.createUndescribedRemoteService();

        service = binder.describe(service, IO.readLines(getClass().getResourceAsStream("/descriptors/service/uda10.xml")));

        SampleServiceOne.assertMatch(service, SampleData.getFirstService(SampleData.createRemoteDevice()));
    }

    @Test
    public void writeUDA10Descriptor() throws Exception {

        ServiceDescriptorBinder binder = new UDA10ServiceDescriptorBinderImpl(new NetworkAddressFactoryImpl());

        RemoteDevice rd = SampleData.createRemoteDevice();
        String descriptorXml = binder.generate(SampleData.getFirstService(rd));

/*
        System.out.println("#######################################################################################");
        System.out.println(descriptorXml);
        System.out.println("#######################################################################################");

*/

        RemoteService service = SampleData.createUndescribedRemoteService();
        service = binder.describe(service, descriptorXml);
        SampleServiceOne.assertMatch(service, SampleData.getFirstService(rd));
    }

}
