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

package fr.distrimind.oss.upnp.common.registry;

import fr.distrimind.oss.upnp.common.model.meta.Device;
import fr.distrimind.oss.upnp.common.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.common.model.meta.RemoteDevice;

/**
 * Convenience class, provides empty implementations of all methods.
 * <p>
 * Also unifies local and remote device additions and removals with
 * {@link #deviceAdded(Registry, Device)} and
 * {@link #deviceRemoved(Registry, Device)} methods.
 * </p>
 *
 * @author Christian Bauer
 */
public class DefaultRegistryListener implements RegistryListener {

    @Override
	public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {

    }

    @Override
	public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {

    }

    /**
     * Calls the {@link #deviceAdded(Registry, Device)} method.
     *
     * @param registry The DM-UPnP registry of all devices and services know to the local UPnP stack.
     * @param device   A validated and hydrated device metadata graph, with complete service metadata.
     */
    @Override
	public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        deviceAdded(registry, device);
    }

    @Override
	public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {

    }

    /**
     * Calls the {@link #deviceRemoved(Registry, Device)} method.
     *
     * @param registry The DM-UPnP registry of all devices and services know to the local UPnP stack.
     * @param device   A validated and hydrated device metadata graph, with complete service metadata.
     */
    @Override
	public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        deviceRemoved(registry, device);
    }

    /**
     * Calls the {@link #deviceAdded(Registry, Device)} method.
     *
     * @param registry The DM-UPnP registry of all devices and services know to the local UPnP stack.
     * @param device   The local device added to the {@link Registry}.
     */
    @Override
	public void localDeviceAdded(Registry registry, LocalDevice<?> device) {
        deviceAdded(registry, device);
    }

    /**
     * Calls the {@link #deviceRemoved(Registry, Device)} method.
     *
     * @param registry The DM-UPnP registry of all devices and services know to the local UPnP stack.
     * @param device   The local device removed from the {@link Registry}.
     */
    @Override
	public void localDeviceRemoved(Registry registry, LocalDevice<?> device) {
        deviceRemoved(registry, device);
    }

    public void deviceAdded(Registry registry, Device<?, ?, ?> device) {
        
    }

    public void deviceRemoved(Registry registry, Device<?, ?, ?> device) {

    }

    @Override
	public void beforeShutdown(Registry registry) {

    }

    @Override
	public void afterShutdown() {

    }
}
