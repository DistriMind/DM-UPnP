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

import fr.distrimind.oss.upnp.common.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.common.model.meta.RemoteDevice;
import fr.distrimind.oss.upnp.common.model.meta.Service;

/**
 * Notification of discovered device additions, removals, updates.
 * <p>
 * Add an instance of this interface to the registry to be notified when a device is
 * discovered on your UPnP network, or when it is updated, or when it disappears.
 * </p>
 * <p>
 * Implementations will be called concurrently by several threads, they should be thread-safe.
 * </p>
 * <p>
 * Listener methods are called in a separate thread, so you can execute
 * expensive procedures without spawning a new thread. The {@link #beforeShutdown(Registry)}
 * and {@link #afterShutdown()} methods are however called in the thread that is stopping
 * the registry and should not be blocking, unless you want to delay the shutdown procedure.
 * </p>
 *
 * @author Christian Bauer
 */
public interface RegistryListener {

    /**
     * Called as soon as possible after a device has been discovered.
     * <p>
     * This method will be called after SSDP notification datagrams of a new alive
     * UPnP device have been received and processed. The announced device XML descriptor
     * will be retrieved and parsed. The given {@link RemoteDevice} metadata
     * is validated and partial {@link Service} metadata is available. The
     * services are unhydrated, they have no actions or state variable metadata because the
     * service descriptors of the device model have not been retrieved at this point.
   
     * <p>
     * You typically do not use this method on a regular machine, this is an optimization
     * for slower UPnP hosts (such as Android handsets).
   
     *
     * @param registry The DM-UPnP registry of all devices and services know to the local UPnP stack.
     * @param device   A validated and hydrated device metadata graph, with anemic service metadata.
     */
	void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device);

    /**
     * Called when service metadata couldn't be initialized.
     * <p>
     * If you override the {@link #remoteDeviceDiscoveryStarted(Registry, RemoteDevice)}
     * method, you might want to override this method as well.
   
     *
     * @param registry The DM-UPnP registry of all devices and services know to the local UPnP stack.
     * @param device   A validated and hydrated device metadata graph, with anemic service metadata.
     * @param ex       The reason why service metadata could not be initialized, or <code>null</code> if service
     *                 descriptors couldn't be retrieved at all.
     */
	void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex);

    /**
     * Called when complete metadata of a newly discovered device is available.
     *
     * @param registry The DM-UPnP registry of all devices and services know to the local UPnP stack.
     * @param device   A validated and hydrated device metadata graph, with complete service metadata.
     */
	void remoteDeviceAdded(Registry registry, RemoteDevice device);

    /**
     * Called when a discovered device's expiration timestamp is updated.
     * <p>
     * This is a signal that a device is still alive, and you typically don't have to react to this
     * event. You will be notified when a device disappears through timeout.
   
     *
     * @param registry The DM-UPnP registry of all devices and services know to the local UPnP stack.
     * @param device   A validated and hydrated device metadata graph, with complete service metadata.
     */
	void remoteDeviceUpdated(Registry registry, RemoteDevice device);

    /**
     * Called when a previously discovered device disappears.
     * <p>
     * This method will also be called when a discovered device did not update its expiration timeout
     * and has been removed automatically by the local registry. This method will not be called
     * when the UPnP stack is shutting down.
   
     *
     * @param registry The DM-UPnP registry of all devices and services know to the local UPnP stack.
     * @param device   A validated and hydrated device metadata graph, with complete service metadata.
     */
	void remoteDeviceRemoved(Registry registry, RemoteDevice device);

    /**
     * Called after you add your own device to the {@link Registry}.
     *
     * @param registry The DM-UPnP registry of all devices and services know to the local UPnP stack.
     * @param device   The local device added to the {@link Registry}.
     */
	void localDeviceAdded(Registry registry, LocalDevice<?> device);

    /**
     * Called after you remove your own device from the {@link Registry}.
     * <p>
     * This method will not be called when the UPnP stack is shutting down.
   
     * @param registry The DM-UPnP registry of all devices and services know to the local UPnP stack.
     * @param device   The local device removed from the {@link Registry}.
     */
	void localDeviceRemoved(Registry registry, LocalDevice<?> device);

    /**
     * Called after registry maintenance stops but before the registry is cleared.
     * <p>
     * This method should typically not block, it executes in the thread that shuts down the UPnP stack.
   
     * @param registry The DM-UPnP registry of all devices and services know to the local UPnP stack.
     */
	void beforeShutdown(Registry registry);

    /**
     * Called after the registry has been cleared on shutdown.
     * <p>
     * This method should typically not block, it executes in the thread that shuts down the UPnP stack.
   
     */
	void afterShutdown();

}
