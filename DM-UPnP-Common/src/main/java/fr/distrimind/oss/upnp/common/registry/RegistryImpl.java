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

import fr.distrimind.oss.upnp.common.UpnpService;
import fr.distrimind.oss.upnp.common.UpnpServiceConfiguration;
import fr.distrimind.oss.upnp.common.model.DiscoveryOptions;
import fr.distrimind.oss.upnp.common.model.ExpirationDetails;
import fr.distrimind.oss.upnp.common.model.ServiceReference;
import fr.distrimind.oss.upnp.common.model.gena.LocalGENASubscription;
import fr.distrimind.oss.upnp.common.model.gena.RemoteGENASubscription;
import fr.distrimind.oss.upnp.common.model.meta.Device;
import fr.distrimind.oss.upnp.common.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.common.model.meta.RemoteDevice;
import fr.distrimind.oss.upnp.common.model.meta.RemoteDeviceIdentity;
import fr.distrimind.oss.upnp.common.model.meta.Service;
import fr.distrimind.oss.upnp.common.model.resource.Resource;
import fr.distrimind.oss.upnp.common.model.types.DeviceType;
import fr.distrimind.oss.upnp.common.model.types.ServiceType;
import fr.distrimind.oss.upnp.common.model.types.UDN;
import fr.distrimind.oss.upnp.common.protocol.ProtocolFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Default implementation of {@link Registry}.
 *
 * @author Christian Bauer
 */
@ApplicationScoped
public class RegistryImpl implements Registry {

    final private static DMLogger log = Log.getLogger(RegistryImpl.class);

    protected UpnpService upnpService;
    protected RegistryMaintainer registryMaintainer;
    protected final Set<RemoteGENASubscription> pendingSubscriptionsLock = new HashSet<>();

    public RegistryImpl() {
    }

    /**
     * Starts background maintenance immediately.
     */
    @Inject
    public RegistryImpl(UpnpService upnpService) {
		if (log.isDebugEnabled()) {
            log.debug("Creating Registry: " + getClass().getName());
		}

		this.upnpService = upnpService;

        log.debug("Starting registry background maintenance...");
        registryMaintainer = createRegistryMaintainer();
        if (registryMaintainer != null) {
            getConfiguration().getRegistryMaintainerExecutor().execute(registryMaintainer);
        }
    }

    @Override
	public UpnpService getUpnpService() {
        return upnpService;
    }

    @Override
	public UpnpServiceConfiguration getConfiguration() {
        return getUpnpService().getConfiguration();
    }

    @Override
	public ProtocolFactory getProtocolFactory() {
        return getUpnpService().getProtocolFactory();
    }

    protected RegistryMaintainer createRegistryMaintainer() {
        return new RegistryMaintainer(
                this,
                getConfiguration().getRegistryMaintenanceIntervalMillis()
        );
    }

    // #################################################################################################

    protected final Set<RegistryListener> registryListeners = new HashSet<>();
    final Set<RegistryItem<URI, Resource<?>>> resourceItems = new HashSet<>();
    protected final List<Runnable> pendingExecutions = new ArrayList<>();

    final RemoteItems remoteItems = new RemoteItems(this);
    final LocalItems localItems = new LocalItems(this);

    // #################################################################################################

    @Override
	synchronized public void addListener(RegistryListener listener) {
        registryListeners.add(listener);
    }

    @Override
	synchronized public void removeListener(RegistryListener listener) {
        registryListeners.remove(listener);
    }

    @Override
	synchronized public Collection<RegistryListener> getListeners() {
        return Collections.unmodifiableCollection(registryListeners);
    }

    @Override
	synchronized public boolean notifyDiscoveryStart(final RemoteDevice device) {
        // Exit if we have it already, this is atomic inside this method, finally
		if (device.getIdentity()!=null && getUpnpService()!=null && getUpnpService().getRegistry()!=null && getUpnpService().getRegistry().getRemoteDevice(device.getIdentity().getUdn(), true) != null)
			return false;
        if (getUpnpService().getRegistry().getRemoteDevice(device.getIdentity().getUdn(), true) != null) {
			if (log.isTraceEnabled()) {
				log.trace("Not notifying listeners, already registered: " + device);
			}
			return false;
        }
        for (final RegistryListener listener : getListeners()) {
            getConfiguration().getRegistryListenerExecutor().execute(
					() -> listener.remoteDeviceDiscoveryStarted(RegistryImpl.this, device)
			);
        }
        return true;
    }

    @Override
	synchronized public void notifyDiscoveryFailure(final RemoteDevice device, final Exception ex) {
        for (final RegistryListener listener : getListeners()) {
            getConfiguration().getRegistryListenerExecutor().execute(
					() -> listener.remoteDeviceDiscoveryFailed(RegistryImpl.this, device, ex)
			);
        }
    }

    // #################################################################################################

    @Override
	synchronized public void addDevice(LocalDevice<?> localDevice) {
        localItems.add(localDevice);
    }

    @Override
	synchronized public void addDevice(LocalDevice<?> localDevice, DiscoveryOptions options) {
        localItems.add(localDevice, options);
    }

    @Override
	synchronized public void setDiscoveryOptions(UDN udn, DiscoveryOptions options) {
        localItems.setDiscoveryOptions(udn, options);
    }

    @Override
	synchronized public DiscoveryOptions getDiscoveryOptions(UDN udn) {
        return localItems.getDiscoveryOptions(udn);
    }

    @Override
	synchronized public void addDevice(RemoteDevice remoteDevice) {
        remoteItems.add(remoteDevice);
    }

    @Override
	synchronized public boolean update(RemoteDeviceIdentity rdIdentity) {
        return remoteItems.update(rdIdentity);
    }

    @Override
	synchronized public boolean removeDevice(LocalDevice<?> localDevice) {
        return localItems.remove(localDevice);
    }

    @Override
	synchronized public boolean removeDevice(RemoteDevice remoteDevice) {
        return remoteItems.remove(remoteDevice);
    }

    @Override
	synchronized public void removeAllLocalDevices() {
        localItems.removeAll();
    }

    @Override
	synchronized public void removeAllRemoteDevices() {
        remoteItems.removeAll();
    }

	@Override
	synchronized public boolean removeDevice(UDN udn) {
        Device<?, ?, ?> device = getDevice(udn, true);
        if (device instanceof LocalDevice)
            return removeDevice((LocalDevice<?>) device);
        if (device instanceof RemoteDevice)
            return removeDevice((RemoteDevice) device);
        return false;
    }

    @Override
	synchronized public Device<?, ?, ?> getDevice(UDN udn, boolean rootOnly) {
        Device<?, ?, ?> device;
        if ((device = localItems.get(udn, rootOnly)) != null) return device;
        if ((device = remoteItems.get(udn, rootOnly)) != null) return device;
        return null;
    }

    @Override
	synchronized public LocalDevice<?> getLocalDevice(UDN udn, boolean rootOnly) {
        return localItems.get(udn, rootOnly);
    }

    @Override
	synchronized public RemoteDevice getRemoteDevice(UDN udn, boolean rootOnly) {
        return remoteItems.get(udn, rootOnly);
    }

    @Override
	synchronized public Collection<LocalDevice<?>> getLocalDevices() {
        return Collections.unmodifiableCollection(localItems.get());
    }

    @Override
	synchronized public Collection<RemoteDevice> getRemoteDevices() {
        return Collections.unmodifiableCollection(remoteItems.get());
    }

    @Override
	synchronized public Collection<Device<?, ?, ?>> getDevices() {
        Set<Device<?, ?, ?>> all = new HashSet<>();
        all.addAll(localItems.get());
        all.addAll(remoteItems.get());
        return Collections.unmodifiableCollection(all);
    }

    @Override
	synchronized public Collection<Device<?, ?, ?>> getDevices(DeviceType deviceType) {
        Collection<Device<?, ?, ?>> devices = new HashSet<>();

        devices.addAll(localItems.get(deviceType));
        devices.addAll(remoteItems.get(deviceType));

        return Collections.unmodifiableCollection(devices);
    }

    @Override
	synchronized public Collection<Device<?, ?, ?>> getDevices(ServiceType serviceType) {
        Collection<Device<?, ?, ?>> devices = new HashSet<>();

        devices.addAll(localItems.get(serviceType));
        devices.addAll(remoteItems.get(serviceType));

        return Collections.unmodifiableCollection(devices);
    }

    @Override
	synchronized public Service<?, ?, ?> getService(ServiceReference serviceReference) {
        Device<?, ?, ?> device;
        if ((device = getDevice(serviceReference.getUdn(), false)) != null) {
            return device.findService(serviceReference.getServiceId());
        }
        return null;
    }

    // #################################################################################################

    @Override
	synchronized public Resource<?> getResource(URI pathQuery) throws IllegalArgumentException {
        if (pathQuery.isAbsolute()) {
            throw new IllegalArgumentException("Resource URI can not be absolute, only path and query:" + pathQuery);
        }

        // Note: Uses field access on resourceItems for performance reasons

		for (RegistryItem<URI, Resource<?>> resourceItem : resourceItems) {
        	Resource<?> resource = resourceItem.getItem();
        	if (resource.matches(pathQuery)) {
                return resource;
            }
        }

        // TODO: UPNP VIOLATION: Fuppes on my ReadyNAS thinks it's a cool idea to add a slash at the end of the callback URI...
        // It also cuts off any query parameters in the callback URL - nice!
        if (pathQuery.getPath().endsWith("/")) {
            URI pathQueryWithoutSlash = URI.create(pathQuery.toString().substring(0, pathQuery.toString().length() - 1));

 			for (RegistryItem<URI, Resource<?>> resourceItem : resourceItems) {
            	Resource<?> resource = resourceItem.getItem();
            	if (resource.matches(pathQueryWithoutSlash)) {
                    return resource;
                }
            }
        }

        return null;
    }

    @Override
	@SuppressWarnings("unchecked")
	synchronized public <T extends Resource<?>> T getResource(Class<T> resourceType, URI pathQuery) throws IllegalArgumentException {
        Resource<?> resource = getResource(pathQuery);
        if (resource != null && resourceType.isAssignableFrom(resource.getClass())) {
            return (T) resource;
        }
        return null;
    }

    @Override
	synchronized public Collection<Resource<?>> getResources() {
        Collection<Resource<?>> s = new HashSet<>();
        for (RegistryItem<URI, Resource<?>> resourceItem : resourceItems) {
            s.add(resourceItem.getItem());
        }
        return s;
    }

    @Override
	@SuppressWarnings("unchecked")
	synchronized public <T extends Resource<?>> Collection<T> getResources(Class<T> resourceType) {
        Collection<T> s = new HashSet<>();
        for (RegistryItem<URI, Resource<?>> resourceItem : resourceItems) {
            if (resourceType.isAssignableFrom(resourceItem.getItem().getClass()))
                s.add((T) resourceItem.getItem());
        }
        return s;
    }

    @Override
	synchronized public void addResource(Resource<?> resource) {
        addResource(resource, ExpirationDetails.UNLIMITED_AGE);
    }

    @Override
	synchronized public void addResource(Resource<?> resource, int maxAgeSeconds) {
        RegistryItem<URI, Resource<?>> resourceItem = new RegistryItem<>(resource.getPathQuery(), resource, maxAgeSeconds);
        resourceItems.remove(resourceItem);
        resourceItems.add(resourceItem);
    }

    @Override
	synchronized public boolean removeResource(Resource<?> resource) {
        return resourceItems.remove(new RegistryItem<URI, Resource<?>>(resource.getPathQuery()));
    }

    // #################################################################################################

    @Override
	synchronized public void addLocalSubscription(LocalGENASubscription<?> subscription) {
        localItems.addSubscription(subscription);
    }

    @Override
	synchronized public LocalGENASubscription<?> getLocalSubscription(String subscriptionId) {
        return localItems.getSubscription(subscriptionId);
    }

    @Override
	synchronized public boolean updateLocalSubscription(LocalGENASubscription<?> subscription) {
        return localItems.updateSubscription(subscription);
    }

    @Override
	synchronized public boolean removeLocalSubscription(LocalGENASubscription<?> subscription) {
        return localItems.removeSubscription(subscription);
    }

    @Override
	synchronized public void addRemoteSubscription(RemoteGENASubscription subscription) {
        remoteItems.addSubscription(subscription);
    }

    @Override
	synchronized public RemoteGENASubscription getRemoteSubscription(String subscriptionId) {
        return remoteItems.getSubscription(subscriptionId);
    }

    @Override
	synchronized public void updateRemoteSubscription(RemoteGENASubscription subscription) {
        remoteItems.updateSubscription(subscription);
    }

    @Override
	synchronized public void removeRemoteSubscription(RemoteGENASubscription subscription) {
        remoteItems.removeSubscription(subscription);
    }

    /* ############################################################################################################ */

   	@Override
	synchronized public void advertiseLocalDevices() {
   		localItems.advertiseLocalDevices();
   	}

    /* ############################################################################################################ */

    // When you call this, make sure you have the Router lock before this lock is obtained!
    @Override
	synchronized public void shutdown() {
        log.debug("Shutting down registry...");

        if (registryMaintainer != null)
            registryMaintainer.stop();
        
        // Final cleanup run to flush out pending executions which might
        // not have been caught by the maintainer before it stopped
		if (log.isTraceEnabled()) {
			log.trace("Executing final pending operations on shutdown: " + pendingExecutions.size());
		}
		runPendingExecutions(false);

        for (RegistryListener listener : registryListeners) {
            listener.beforeShutdown(this);
        }

        for (RegistryItem<URI, Resource<?>> resourceItem : resourceItems) {
            resourceItem.getItem().shutdown();
        }

        remoteItems.shutdown();
        localItems.shutdown();

        for (RegistryListener listener : registryListeners) {
            listener.afterShutdown();
        }
    }

    @Override
	synchronized public void pause() {
        if (registryMaintainer != null) {
            log.debug("Pausing registry maintenance");
            runPendingExecutions(true);
            registryMaintainer.stop();
            registryMaintainer = null;
        }
    }

    @Override
	synchronized public void resume() {
        if (registryMaintainer == null) {
            log.debug("Resuming registry maintenance");
            remoteItems.resume();
            registryMaintainer = createRegistryMaintainer();
            if (registryMaintainer != null) {
                getConfiguration().getRegistryMaintainerExecutor().execute(registryMaintainer);
            }
        }
    }

    @Override
	synchronized public boolean isPaused() {
        return registryMaintainer == null;
    }

    /* ############################################################################################################ */

    synchronized void maintain() {

        if (log.isTraceEnabled())
            log.trace("Maintaining registry...");

        // Remove expired resources
        Iterator<RegistryItem<URI, Resource<?>>> it = resourceItems.iterator();
        while (it.hasNext()) {
            RegistryItem<URI, Resource<?>> item = it.next();
            if (item.getExpirationDetails().hasExpired()) {
                if (log.isTraceEnabled())
                    log.trace("Removing expired resource: " + item);
                it.remove();
            }
        }

        // Let each resource do its own maintenance
        for (RegistryItem<URI, Resource<?>> resourceItem : resourceItems) {
            resourceItem.getItem().maintain(
                    pendingExecutions,
                    resourceItem.getExpirationDetails()
            );
        }

        // These add all their operations to the pendingExecutions queue
        remoteItems.maintain();
        localItems.maintain();

        // We now run the queue asynchronously so the maintenance thread can continue its loop undisturbed
        runPendingExecutions(true);
    }

    synchronized void executeAsyncProtocol(Runnable runnable) {
        pendingExecutions.add(runnable);
    }

    synchronized void runPendingExecutions(boolean async) {
        if (log.isTraceEnabled())
            log.trace("Executing pending operations: " + pendingExecutions.size());
        for (Runnable pendingExecution : pendingExecutions) {
            if (async)
                getConfiguration().getAsyncProtocolExecutor().execute(pendingExecution);
            else
                pendingExecution.run();
        }
        if (!pendingExecutions.isEmpty()) {
            pendingExecutions.clear();
        }
    }

    /* ############################################################################################################ */

    public void printDebugLog() {
        if (log.isDebugEnabled()) {
            log.debug("====================================    REMOTE   ================================================");

            for (RemoteDevice remoteDevice : remoteItems.get()) {
                log.debug(remoteDevice.toString());
            }

            log.debug("====================================    LOCAL    ================================================");

            for (LocalDevice<?> localDevice : localItems.get()) {
                log.debug(localDevice.toString());
            }

            log.debug("====================================  RESOURCES  ================================================");

            for (RegistryItem<URI, Resource<?>> resourceItem : resourceItems) {
                log.debug(resourceItem.toString());
            }

            log.debug("=================================================================================================");

        }

    }

 	@Override
	public void registerPendingRemoteSubscription(RemoteGENASubscription subscription) {
		synchronized (pendingSubscriptionsLock) {
            pendingSubscriptionsLock.add(subscription);
        }
	}
	
	@Override
	public void unregisterPendingRemoteSubscription(RemoteGENASubscription subscription) {
        synchronized (pendingSubscriptionsLock) {
            if(pendingSubscriptionsLock.remove(subscription)) {
                pendingSubscriptionsLock.notifyAll();
            }
        }
	}

    @Override
    public RemoteGENASubscription getWaitRemoteSubscription(String subscriptionId) {
        synchronized (pendingSubscriptionsLock) {
            RemoteGENASubscription subscription = getRemoteSubscription(subscriptionId);
            while (subscription == null && !pendingSubscriptionsLock.isEmpty()) {
                try {
                    log.trace("Subscription not found, waiting for pending subscription procedure to terminate.");
                    pendingSubscriptionsLock.wait();
                } catch (InterruptedException ignored) {
                }
                subscription = getRemoteSubscription(subscriptionId);
            }
            return subscription;
        }
    }

}
