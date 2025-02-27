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

package fr.distrimind.oss.upnp.registry;

import fr.distrimind.oss.upnp.model.resource.Resource;
import fr.distrimind.oss.upnp.model.gena.CancelReason;
import fr.distrimind.oss.upnp.model.gena.RemoteGENASubscription;
import fr.distrimind.oss.upnp.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.model.meta.RemoteDevice;
import fr.distrimind.oss.upnp.model.meta.RemoteDeviceIdentity;
import fr.distrimind.oss.upnp.model.types.UDN;

import java.util.*;
import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.Log;

/**
 * Internal class, required by {@link RegistryImpl}.
 *
 * @author Christian Bauer
 */
class RemoteItems extends RegistryItems<RemoteDevice, RemoteGENASubscription> {

    final private static DMLogger log = Log.getLogger(RemoteItems.class);

    RemoteItems(RegistryImpl registry) {
        super(registry);
    }

    /**
     * Adds the given remote device to the registry, or udpates its expiration timestamp.
     * <p>
     * This method first checks if there is a remote device with the same UDN already registered. If so, it
     * updates the expiration timestamp of the remote device without notifying any registry listeners. If the
     * device is truly new, all its resources are tested for conflicts with existing resources in the registry's
     * namespace, then it is added to the registry and listeners are notified that a new fully described remote
     * device is now available.
   
     *
     * @param device The remote device to be added
     */
	@Override
	void add(final RemoteDevice device) {

        if (update(device.getIdentity())) {
			if (log.isDebugEnabled()) {
				log.debug("Ignoring addition, device already registered: " + device);
			}
			return;
        }
        Collection<Resource<?>> r=getResources(device);
        for (Resource<?> deviceResource : r) {
			if (log.isDebugEnabled()) {
				log.debug("Validating remote device resource; " + deviceResource);
			}
			if (registry.getResource(deviceResource.getPathQuery()) != null) {
                throw new RegistrationException("URI namespace conflict with already registered resource: " + deviceResource);
            }
        }

        for (Resource<?> validatedResource : r) {
            registry.addResource(validatedResource);
			if (log.isDebugEnabled()) {
				log.debug("Added remote device resource: " + validatedResource);
			}
		}

        // Override the device's maximum age if configured (systems without multicast support)
        RegistryItem<UDN, RemoteDevice> item = new RegistryItem<>(
                device.getIdentity().getUdn(),
                device,
                registry.getConfiguration().getRemoteDeviceMaxAgeSeconds() != null
                        ? registry.getConfiguration().getRemoteDeviceMaxAgeSeconds()
                        : device.getIdentity().getMaxAgeSeconds()
        );
		if (log.isDebugEnabled()) {
            log.debug("Adding hydrated remote device to registry with "
							 + item.getExpirationDetails().getMaxAgeSeconds() + " seconds expiration: " + device);
		}
		getDeviceItems().add(item);

        if (log.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append("-------------------------- START Registry Namespace -----------------------------------\n");
            for (Resource<?> resource : registry.getResources()) {
                sb.append(resource).append("\n");
            }
            sb.append("-------------------------- END Registry Namespace -----------------------------------");
            log.trace(sb.toString());
        }

        // Only notify the listeners when the device is fully usable
		if (log.isDebugEnabled()) {
            log.debug("Completely hydrated remote device graph available, calling listeners: " + device);
		}
		for (final RegistryListener listener : registry.getListeners()) {
            registry.getConfiguration().getRegistryListenerExecutor().execute(
					() -> listener.remoteDeviceAdded(registry, device)
			);
        }

    }

    boolean update(RemoteDeviceIdentity rdIdentity) {

        for (LocalDevice<?> localDevice : registry.getLocalDevices()) {
            if (localDevice.findDevice(rdIdentity.getUdn()) != null) {
                log.debug("Ignoring update, a local device graph contains UDN");
                return true;
            }
        }

        RemoteDevice registeredRemoteDevice = get(rdIdentity.getUdn(), false);
        if (registeredRemoteDevice != null) {

            if (!registeredRemoteDevice.isRoot()) {
				if (log.isDebugEnabled()) {
					log.debug("Updating root device of embedded: " + registeredRemoteDevice);
				}
				registeredRemoteDevice = registeredRemoteDevice.getRoot();
            }

            // Override the device's maximum age if configured (systems without multicast support)
            final RegistryItem<UDN, RemoteDevice> item = new RegistryItem<>(
                    registeredRemoteDevice.getIdentity().getUdn(),
                    registeredRemoteDevice,
                    registry.getConfiguration().getRemoteDeviceMaxAgeSeconds() != null
                            ? registry.getConfiguration().getRemoteDeviceMaxAgeSeconds()
                            : rdIdentity.getMaxAgeSeconds()
            );

			if (log.isDebugEnabled()) {
				log.debug("Updating expiration of: " + registeredRemoteDevice);
			}
			getDeviceItems().remove(item);
            getDeviceItems().add(item);

			if (log.isDebugEnabled()) {
				log.debug("Remote device updated, calling listeners: " + registeredRemoteDevice);
			}
			for (final RegistryListener listener : registry.getListeners()) {
                registry.getConfiguration().getRegistryListenerExecutor().execute(
						() -> listener.remoteDeviceUpdated(registry, item.getItem())
				);
            }

            return true;

        }
        return false;
    }

    /**
     * Removes the given device from the registry and notifies registry listeners.
     *
     * @param remoteDevice The device to remove from the registry.
     * @return <code>true</code> if the given device was found and removed from the registry, false if it wasn't registered.
     */
	@Override
	boolean remove(final RemoteDevice remoteDevice) {
        return remove(remoteDevice, false);
    }

    boolean remove(final RemoteDevice remoteDevice, boolean shuttingDown) throws RegistrationException {
        final RemoteDevice registeredDevice = get(remoteDevice.getIdentity().getUdn(), true);
        if (registeredDevice != null) {

			if (log.isDebugEnabled()) {
				log.debug("Removing remote device from registry: " + remoteDevice);
			}

			// Resources
            for (Resource<?> deviceResource : getResources(registeredDevice)) {
                if (registry.removeResource(deviceResource)) {
					if (log.isDebugEnabled()) {
						log.debug("Unregistered resource: " + deviceResource);
					}
				}
            }

            // Active subscriptions
            Iterator<RegistryItem<String, RemoteGENASubscription>> it = getSubscriptionItems().iterator();
            while (it.hasNext()) {
                final RegistryItem<String, RemoteGENASubscription> outgoingSubscription = it.next();

                UDN subscriptionForUDN =
                        outgoingSubscription.getItem().getService().getDevice().getIdentity().getUdn();

                if (subscriptionForUDN.equals(registeredDevice.getIdentity().getUdn())) {
					if (log.isDebugEnabled()) {
						log.debug("Removing outgoing subscription: " + outgoingSubscription.getKey());
					}
					it.remove();
                    if (!shuttingDown) {
                        registry.getConfiguration().getRegistryListenerExecutor().execute(
								() -> outgoingSubscription.getItem().end(CancelReason.DEVICE_WAS_REMOVED, null)
						);
                    }
                }
            }

            // Only notify listeners if we are NOT in the process of shutting down the registry
            if (!shuttingDown) {
                for (final RegistryListener listener : registry.getListeners()) {
                    registry.getConfiguration().getRegistryListenerExecutor().execute(
							() -> listener.remoteDeviceRemoved(registry, registeredDevice)
					);
                }
            }

            // Finally, remove the device from the registry
            getDeviceItems().remove(new RegistryItem<UDN, RemoteDevice>(registeredDevice.getIdentity().getUdn()));

            return true;
        }

        return false;
    }

    @Override
	void removeAll() {
        removeAll(false);
    }

    void removeAll(boolean shuttingDown) {
        for (RemoteDevice device : get()) {
            remove(device, shuttingDown);
        }
    }

    /* ############################################################################################################ */

    void start() {
        // Noop
    }

    @Override
	void maintain() {

        if (getDeviceItems().isEmpty()) return;

        // Remove expired remote devices
        Map<UDN, RemoteDevice> expiredRemoteDevices = new HashMap<>();
        for (RegistryItem<UDN, RemoteDevice> remoteItem : getDeviceItems()) {
            if (log.isTraceEnabled())
                log.trace("Device '" + remoteItem.getItem() + "' expires in seconds: "
                                   + remoteItem.getExpirationDetails().getSecondsUntilExpiration());
            if (remoteItem.getExpirationDetails().hasExpired(false)) {
                expiredRemoteDevices.put(remoteItem.getKey(), remoteItem.getItem());
            }
        }
        for (RemoteDevice remoteDevice : expiredRemoteDevices.values()) {
            if (log.isDebugEnabled())
                log.debug("Removing expired: " + remoteDevice);
            remove(remoteDevice);
        }

        // Renew outgoing subscriptions
        Set<RemoteGENASubscription> expiredOutgoingSubscriptions = new HashSet<>();
        for (RegistryItem<String, RemoteGENASubscription> item : getSubscriptionItems()) {
            if (item.getExpirationDetails().hasExpired(true)) {
                expiredOutgoingSubscriptions.add(item.getItem());
            }
        }
        for (RemoteGENASubscription subscription : expiredOutgoingSubscriptions) {
            if (log.isDebugEnabled())
                log.debug("Renewing outgoing subscription: " + subscription);
            renewOutgoingSubscription(subscription);
        }
    }

    public void resume() {
        log.debug("Updating remote device expiration timestamps on resume");
        List<RemoteDeviceIdentity> toUpdate = new ArrayList<>();
        for (RegistryItem<UDN, RemoteDevice> remoteItem : getDeviceItems()) {
            toUpdate.add(remoteItem.getItem().getIdentity());
        }
        for (RemoteDeviceIdentity identity : toUpdate) {
            update(identity);
        }
    }

    @Override
	void shutdown() {
        log.debug("Cancelling all outgoing subscriptions to remote devices during shutdown");
        List<RemoteGENASubscription> remoteSubscriptions = new ArrayList<>();
        for (RegistryItem<String, RemoteGENASubscription> item : getSubscriptionItems()) {
            remoteSubscriptions.add(item.getItem());
        }
        for (RemoteGENASubscription remoteSubscription : remoteSubscriptions) {
            // This will remove the active subscription from the registry!
            registry.getProtocolFactory()
                    .createSendingUnsubscribe(remoteSubscription)
                    .run();
        }

        log.debug("Removing all remote devices from registry during shutdown");
        removeAll(true);
    }

    /* ############################################################################################################ */

    protected void renewOutgoingSubscription(final RemoteGENASubscription subscription) {
        registry.executeAsyncProtocol(
                registry.getProtocolFactory().createSendingRenewal(subscription)
        );
    }
}
