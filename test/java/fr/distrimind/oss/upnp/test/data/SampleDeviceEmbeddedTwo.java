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

package fr.distrimind.oss.upnp.test.data;

import fr.distrimind.oss.upnp.model.meta.Device;
import fr.distrimind.oss.upnp.model.meta.DeviceDetails;
import fr.distrimind.oss.upnp.model.profile.DeviceDetailsProvider;
import fr.distrimind.oss.upnp.model.meta.DeviceIdentity;
import fr.distrimind.oss.upnp.model.meta.Icon;
import fr.distrimind.oss.upnp.model.meta.ManufacturerDetails;
import fr.distrimind.oss.upnp.model.meta.ModelDetails;
import fr.distrimind.oss.upnp.model.meta.Service;
import fr.distrimind.oss.upnp.model.types.DeviceType;
import fr.distrimind.oss.upnp.model.types.UDADeviceType;
import fr.distrimind.oss.upnp.model.types.UDN;

import java.util.Collections;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class SampleDeviceEmbeddedTwo<D extends Device<?, D, S>, S extends Service<?, D, S>> extends SampleDevice<D, S> {

    public SampleDeviceEmbeddedTwo(DeviceIdentity identity, S service, D embeddedDevice) {
        super(identity, service, embeddedDevice);
    }

    @Override
    public DeviceType getDeviceType() {
        return new UDADeviceType("MY-DEVICE-TYPE-THREE", 3);
    }

    @Override
    public DeviceDetails getDeviceDetails() {
        return new DeviceDetails(
                "My Testdevice Third",
                new ManufacturerDetails("4th Line", "http://www.4thline.org/"),
                new ModelDetails("MYMODEL", "TEST Device", "ONE", "http://www.4thline.org/another_embedded_model"),
                "000da201238d",
                "100000000003",
                "http://www.4thline.org/some_third_user_interface");

    }

    @Override
    public DeviceDetailsProvider getDeviceDetailsProvider() {
        return info -> getDeviceDetails();
    }

    @Override
    public List<Icon> getIcons() {
        return Collections.emptyList();
    }

    public static UDN getEmbeddedTwoUDN() {
        return new UDN("MY-DEVICE-789");
    }

}
