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
import fr.distrimind.oss.upnp.model.meta.DeviceIdentity;
import fr.distrimind.oss.upnp.model.meta.Icon;
import fr.distrimind.oss.upnp.model.meta.Service;
import fr.distrimind.oss.upnp.model.profile.DeviceDetailsProvider;
import fr.distrimind.oss.upnp.model.types.DeviceType;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * @author Christian Bauer
 */
public abstract class SampleDevice<D extends Device<?, D, S>, S extends Service<?, D, S>> {

    public DeviceIdentity identity;
    public S service;
    public D embeddedDevice;

    protected SampleDevice(DeviceIdentity identity, S service, D embeddedDevice) {
        this.identity = identity;
        this.service = service;
        this.embeddedDevice = embeddedDevice;
    }

    public DeviceIdentity getIdentity() {
        return identity;
    }

    public S getService() {
        return service;
    }

    public D getEmbeddedDevice() {
        return embeddedDevice;
    }

    public abstract DeviceType getDeviceType();
    public abstract DeviceDetails getDeviceDetails();
    public abstract DeviceDetailsProvider getDeviceDetailsProvider();
    public abstract List<Icon> getIcons();

    public D newInstance(Constructor<D> deviceConstructor) {
        return newInstance(deviceConstructor, false);
    }

    @SuppressWarnings("unchecked")
	public D newInstance(Constructor<?> deviceConstructor, boolean useProvider) {
        try {
            if (useProvider) {
                return (D)deviceConstructor.newInstance(
                        getIdentity(), getDeviceType(), getDeviceDetailsProvider(),
                        getIcons(), getService(), getEmbeddedDevice()
                );
            }
            return (D)deviceConstructor.newInstance(
                    getIdentity(), getDeviceType(), getDeviceDetails(),
                    getIcons(), getService(), getEmbeddedDevice()
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
