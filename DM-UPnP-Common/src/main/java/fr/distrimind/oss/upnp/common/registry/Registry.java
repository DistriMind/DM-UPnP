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

import fr.distrimind.oss.upnp.common.model.gena.CancelReason;
import fr.distrimind.oss.upnp.common.UpnpService;
import fr.distrimind.oss.upnp.common.UpnpServiceConfiguration;
import fr.distrimind.oss.upnp.common.model.DiscoveryOptions;
import fr.distrimind.oss.upnp.common.model.resource.Resource;
import fr.distrimind.oss.upnp.common.model.ServiceReference;
import fr.distrimind.oss.upnp.common.model.meta.Device;
import fr.distrimind.oss.upnp.common.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.common.model.meta.RemoteDevice;
import fr.distrimind.oss.upnp.common.model.gena.LocalGENASubscription;
import fr.distrimind.oss.upnp.common.model.gena.RemoteGENASubscription;
import fr.distrimind.oss.upnp.common.model.meta.RemoteDeviceIdentity;
import fr.distrimind.oss.upnp.common.model.meta.Service;
import fr.distrimind.oss.upnp.common.model.types.DeviceType;
import fr.distrimind.oss.upnp.common.model.types.ServiceType;
import fr.distrimind.oss.upnp.common.model.types.UDN;
import fr.distrimind.oss.upnp.common.protocol.ProtocolFactory;

import java.net.URI;
import java.util.Collection;

/**
 * The core of the UPnP stack, keeping track of known devices and resources.
 * <p>
 * A running UPnP stack has one <code>Registry</code>. Any discovered device is added
 * to this registry, as well as any exposed local device. The registry then maintains
 * these devices continuously (see {@link RegistryMaintainer}) and when needed refreshes
 * their announcements on the network or removes them when they have expired. The registry
 * also keeps track of GENA event subscriptions.
 * </p>
 * <p>
 * UPnP client applications typically monitor activity of the registry
 * via {@link RegistryListener}, they are inherently asynchronous.
 * </p>
 * <p>
 * The registry has to be {@link #shutdown()} properly, so it can notify all participants
 * on the network that local devices will no longer be available and cancel all
 * GENA subscriptions.
 * <p>
 * An implementation has to be thread-safe.
 * </p>
 *
 * @author Christian Bauer
 */
public interface Registry {

    UpnpService getUpnpService();
    UpnpServiceConfiguration getConfiguration();
    ProtocolFactory getProtocolFactory();

    // #################################################################################################

    /**
     * Typically called internally when the UPnP stack is stopping.
     * <p>
     * Unsubscribe all local devices and GENA subscriptions.
   
     */
	void shutdown();

    /**
     * Stops background maintenance (thread) of registered items.
     * <p>
     * When paused, the registry will no longer remove expired remote devices if their
     * discovery announcements stop for some reason (device was turned off). Your local
     * control point will now see potentially unavailable remote devices. Outbound
     * GENA subscriptions from your local control point to remote services will not
     * be renewed automatically anymore, a remote service might drop your subscriptions
     * if you don't resume maintenance within the subscription's expiration timeout.
   
     * <p>
     * Local devices and services will not be announced periodically anymore to remote
     * control points, only when they are manually added are removed from the registry.
     * The registry will also no longer remove expired inbound GENA subscriptions to
     * local service from remote control points, if that control point for some reason
     * stops sending subscription renewal messages.
   
     */
	void pause();

    /**
     * Resumes background maintenance (thread) of registered items.
     * <p>
     * A local control point has to handle the following situations when resuming
     * registry maintenance:
     * <p>
     * A remote device registration might have expired. This is the case when the remote
     * device stopped sending announcements while the registry was paused (maybe because
     * the device was switched off) and the registry was paused longer than the device
     * advertisement's maximum age. The registry will not know if the device is still
     * available when it resumes maintenance. However, it will simply assume that the
     * remote device is still available and restart its expiration check cycle. That means
     * a device will finally be removed from the registry, if no further announcements
     * from the device are received, when the maximum age of the device has elapsed
     * after the registry resumed operation.
   
     * <p>
     * Secondly, a remote device registration might not have expired but some of your
     * outbound GENA subscriptions to its services have not been renewed within the expected renewal
     * period. Therefore, your outbound subscriptions might be invalid, because the remote
     * service can drop subscriptions when you don't renew them. On resume, the registry
     * will attempt to send renewals for all outbound GENA subscriptions that require
     * renewal, on devices that still haven't expired. If renewal fails, your subscription will
     * end with {@link CancelReason#RENEWAL_FAILED}. Although
     * you then might conclude that the remote device is no longer available, a GENA renewal
     * can also fail for other reasons. The remote device will be kept and maintained in the
     * registry until it announces itself, or it expires, even after a failed GENA renewal.
   
     * <p>
     * If you are providing local devices and services, resuming registry maintenance has
     * the following effects:
   
     * <p>
     * Local devices and their services are announced again immediately if the registry
     * has been paused for longer than half of the device's maximum age. Remote control
     * points will either see this as a new device advertisement (if they have dropped
     * your device while you paused maintenance) or as a regular update if you didn't
     * pause longer than the device's maximum age/expiration timeout.
   
     * <p>
     * Inbound GENA subscriptions to your local services are active, even in
     * paused state - remote control points should continue renewing the subscription.
     * If a remote control point stopped renewing a subscription without unsubscribing
     * (hard power off), an outdated inbound subscription will be detected when you
     * resume maintenance. This subscription will be cleaned up immediately on resume.
   
     */
	void resume();

    /**
     * @return <code>true</code> if the registry has currently no running background
     *         maintenance (thread).
     */
	boolean isPaused();

    // #################################################################################################

    void addListener(RegistryListener listener);

    void removeListener(RegistryListener listener);

    Collection<RegistryListener> getListeners();

    /**
     * Called internally by the UPnP stack when the discovery protocol starts.
     * <p>
     * The registry will notify all registered listeners of this event, unless the
     * given device was already in the registry.
   
     *
     * @param device The half-hydrated (without services) metadata of the discovered device.
     * @return <code>false</code> if the device was already registered.
     */
	boolean notifyDiscoveryStart(RemoteDevice device);

    /**
     * Called internally by the UPnP stack when the discovery protocol stopped abnormally.
     * <p>
     * The registry will notify all registered listeners of this event.
   
     *
     * @param device The half-hydrated (without services) metadata of the discovered device.
     * @param ex The cause for the interruption of the discovery protocol.
     */
	void notifyDiscoveryFailure(RemoteDevice device, Exception ex);

    // #################################################################################################

    /**
     * Call this method to add your local device metadata.
     *
     * @param localDevice The device to add and maintain.
     * @throws RegistrationException If a conflict with an already registered device was detected.
     */
	void addDevice(LocalDevice<?> localDevice) throws RegistrationException;

    /**
     * Call this method to add your local device metadata.
     *
     * @param localDevice The device to add and maintain.
     * @param options Immediately effective when this device is registered.
     * @throws RegistrationException If a conflict with an already registered device was detected.
     */
	void addDevice(LocalDevice<?> localDevice, DiscoveryOptions options) throws RegistrationException;

    /**
     * Change the active {@link DiscoveryOptions} for the given (local device) UDN.
     *
     * @param options Set to <code>null</code> to disable any options.
     */
	void setDiscoveryOptions(UDN udn, DiscoveryOptions options);

    /**
     * Get the currently active {@link DiscoveryOptions} for the given (local device) UDN.
     *
     * @return <code>null</code> if there are no active discovery options for the given UDN.
     */
	DiscoveryOptions getDiscoveryOptions(UDN udn);

    /**
     * Called internally by the UPnP discovery protocol.
     *
     * @throws RegistrationException If a conflict with an already registered device was detected.
     */
	void addDevice(RemoteDevice remoteDevice) throws RegistrationException;

    /**
     * Called internally by the UPnP discovery protocol.
     */
	boolean update(RemoteDeviceIdentity rdIdentity);

    /**
     * Call this to remove your local device metadata.
     *
     * @return <code>true</code> if the device was registered and has been removed.
     */
	boolean removeDevice(LocalDevice<?> localDevice);

    /**
     * Called internally by the UPnP discovery protocol.
     */
	boolean removeDevice(RemoteDevice remoteDevice);

    /**
     * Call this to remove any device metadata with the given UDN.
     *
     * @return <code>true</code> if the device was registered and has been removed.
     */
	boolean removeDevice(UDN udn);

    /**
     * Clear the registry of all locally registered device metadata.
     */
	void removeAllLocalDevices();

    /**
     * Clear the registry of all discovered remote device metadata.
     */
	void removeAllRemoteDevices();

    /**
     * @param udn The device name to lookup.
     * @param rootOnly If <code>true</code>, only matches of root devices are returned.
     * @return The registered root or embedded device metadata, or <code>null</code>.
     */
	Device<?, ?, ?> getDevice(UDN udn, boolean rootOnly);

    /**
     * @param udn The device name to lookup.
     * @param rootOnly If <code>true</code>, only matches of root devices are returned.
     * @return The registered root or embedded device metadata, or <code>null</code>.
     */
	LocalDevice<?> getLocalDevice(UDN udn, boolean rootOnly);

    /**
     * @param udn The device name to lookup.
     * @param rootOnly If <code>true</code>, only matches of root devices are returned.
     * @return The registered root or embedded device metadata, or <code>null</code>.
     */
	RemoteDevice getRemoteDevice(UDN udn, boolean rootOnly);

    /**
     * @return All locally registered device metadata, in no particular order, or an empty collection.
     */
	Collection<LocalDevice<?>> getLocalDevices();

    /**
     * @return All discovered remote device metadata, in no particular order, or an empty collection.
     */
	Collection<RemoteDevice> getRemoteDevices();

    /**
     * @return All device metadata, in no particular order, or an empty collection.
     */
	Collection<Device<?, ?, ?>> getDevices();

    /**
     * @return All device metadata of devices which implement the given type, in no particular order,
     *         or an empty collection.
     */
	Collection<Device<?, ?, ?>> getDevices(DeviceType deviceType);

    /**
     * @return All device metadata of devices which have a service that implements the given type,
     *         in no particular order, or an empty collection.
     */
	Collection<Device<?, ?, ?>> getDevices(ServiceType serviceType);

    /**
     * @return Complete service metadata for a service reference or <code>null</code> if no service
     *         for the given reference has been registered.
     */
	Service<?, ?, ?> getService(ServiceReference serviceReference);

    // #################################################################################################

    /**
     * Stores an arbitrary resource in the registry.
     *
     * @param resource The resource to maintain indefinitely (until it is manually removed).
     */
	void addResource(Resource<?> resource);

    /**
     * Stores an arbitrary resource in the registry.
     * <p>
     * Call this method repeatedly to refresh and prevent expiration of the resource.
   
     *
     * @param resource The resource to maintain.
     * @param maxAgeSeconds The time after which the registry will automatically remove the resource.
     */
	void addResource(Resource<?> resource, int maxAgeSeconds);

    /**
     * Removes a resource from the registry.
     *
     * @param resource The resource to remove.
     * @return <code>true</code> if the resource was registered and has been removed.
     */
	boolean removeResource(Resource<?> resource);

    /**
     * @param pathQuery The path and optional query string of the resource's
     *                  registration URI (e.g. <code>/dev/somefile.xml?param=value</code>)
     * @return Any registered resource that matches the given URI path.
     * @throws IllegalArgumentException If the given URI was absolute, only path and query are allowed.
     */
	Resource<?> getResource(URI pathQuery) throws IllegalArgumentException;

    /**
     * @param <T> The required subtype of the {@link Resource}.
     * @param pathQuery The path and optional query string of the resource's
     *                  registration URI (e.g. <code>/dev/somefile.xml?param=value</code>)
     * @param resourceType The required subtype of the {@link Resource}.
     * @return Any registered resource that matches the given URI path and subtype.
     * @throws IllegalArgumentException If the given URI was absolute, only path and query are allowed.
     */
	<T extends Resource<?>> T getResource(Class<T> resourceType, URI pathQuery) throws IllegalArgumentException;

    /**
     * @return All registered resources, in no particular order, or an empty collection.
     */
	Collection<Resource<?>> getResources();

    /**
     * @param <T> The required subtype of the {@link Resource}.
     * @param resourceType The required subtype of the {@link Resource}.
     * @return Any registered resource that matches the given subtype.
     */
	<T extends Resource<?>> Collection<T> getResources(Class<T> resourceType);

    // #################################################################################################

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     */
	void addLocalSubscription(LocalGENASubscription<?> subscription);

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     */
	LocalGENASubscription<?> getLocalSubscription(String subscriptionId);

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     */
	boolean updateLocalSubscription(LocalGENASubscription<?> subscription);

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     */
	boolean removeLocalSubscription(LocalGENASubscription<?> subscription);

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     */
	void addRemoteSubscription(RemoteGENASubscription subscription);

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     */
	RemoteGENASubscription getRemoteSubscription(String subscriptionId);

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     */
	void updateRemoteSubscription(RemoteGENASubscription subscription);

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     */
	void removeRemoteSubscription(RemoteGENASubscription subscription);

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     * <p>
     * When subscribing with a remote host, the remote host might send the
     * initial event message faster than the response for the subscription
     * request. This method register that the subscription procedure is
     * executing.
   
     */
	void registerPendingRemoteSubscription(RemoteGENASubscription subscription);

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     * <p>
     * Notify that the subscription procedure has terminated.
   
     */
	void unregisterPendingRemoteSubscription(RemoteGENASubscription subscription);

    /**
     * Called internally by the UPnP stack, during GENA protocol execution.
     * <p>
     * Get a remote subscription from its subscriptionId. If the subscription can't be found,
     * wait for one of the pending remote subscription procedures from the registry background
     * maintainer to terminate, until the subscription has been found or until there are no
     * more pending subscription procedures.
   
     */
	RemoteGENASubscription getWaitRemoteSubscription(String subscriptionId);

    // #################################################################################################

    /**
     * Manually trigger advertisement messages for all local devices.
     * <p>
     * No messages will be send for devices with disabled advertisements, see
     * {@link DiscoveryOptions}!
   
     */
	void advertiseLocalDevices();

}
