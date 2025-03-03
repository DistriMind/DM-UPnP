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

import fr.distrimind.oss.upnp.common.model.DiscoveryOptions;
import fr.distrimind.oss.upnp.common.model.resource.Resource;
import fr.distrimind.oss.upnp.common.model.gena.CancelReason;
import fr.distrimind.oss.upnp.common.model.gena.LocalGENASubscription;
import fr.distrimind.oss.upnp.common.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.common.model.types.UDN;
import fr.distrimind.oss.upnp.common.protocol.SendingAsync;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Internal class, required by {@link RegistryImpl}.
 *
 * @author Christian Bauer
 */
class LocalItems extends RegistryItems<LocalDevice<?>, LocalGENASubscription<?>> {

    final private static DMLogger log = Log.getLogger(LocalItems.class);
    
    protected Map<UDN, DiscoveryOptions> discoveryOptions = new HashMap<>();
    protected long lastAliveIntervalTimestamp = 0;

    LocalItems(RegistryImpl registry) {
        super(registry);
    }

    protected void setDiscoveryOptions(UDN udn, DiscoveryOptions options) {
        if (options != null)
            this.discoveryOptions.put(udn, options);
        else
            this.discoveryOptions.remove(udn);
    }

    protected DiscoveryOptions getDiscoveryOptions(UDN udn) {
        return this.discoveryOptions.get(udn);
    }

    protected boolean isAdvertised(UDN udn) {
        // Defaults to true
        return getDiscoveryOptions(udn) == null || getDiscoveryOptions(udn).isAdvertised();
    }

    protected boolean isByeByeBeforeFirstAlive(UDN udn) {
        // Defaults to false
        return getDiscoveryOptions(udn) != null && getDiscoveryOptions(udn).isByeByeBeforeFirstAlive();
    }

    @Override
	void add(LocalDevice<?> localDevice) throws RegistrationException {
        add(localDevice, null);
    }

    void add(final LocalDevice<?> localDevice, DiscoveryOptions options) throws RegistrationException {

        // Always set/override the options, even if we don't end up adding the device
        setDiscoveryOptions(localDevice.getIdentity().getUdn(), options);

        if (registry.getDevice(localDevice.getIdentity().getUdn(), false) != null) {
			if (log.isDebugEnabled()) {
				log.debug("Ignoring addition, device already registered: " + localDevice);
			}
			return;
        }

		if (log.isDebugEnabled()) {
            log.debug("Adding local device to registry: " + localDevice);
		}

		for (Resource<?> deviceResource : getResources(localDevice)) {

            if (registry.getResource(deviceResource.getPathQuery()) != null) {
                throw new RegistrationException("URI namespace conflict with already registered resource: " + deviceResource);
            }

            registry.addResource(deviceResource);
			if (log.isDebugEnabled()) {
				log.debug("Registered resource: " + deviceResource);
			}

		}

		if (log.isDebugEnabled()) {
            log.debug("Adding item to registry with expiration in seconds: " + localDevice.getIdentity().getMaxAgeSeconds());
		}

		RegistryItem<UDN, LocalDevice<?>> localItem = new RegistryItem<>(
                localDevice.getIdentity().getUdn(),
                localDevice,
                localDevice.getIdentity().getMaxAgeSeconds()
        );

        getDeviceItems().add(localItem);
		if (log.isDebugEnabled()) {
            log.debug("Registered local device: " + localItem);
		}

		if (isByeByeBeforeFirstAlive(localItem.getKey()))
            advertiseByebye(localDevice, true);

        if (isAdvertised(localItem.getKey()))
             advertiseAlive(localDevice);

        for (final RegistryListener listener : registry.getListeners()) {
            registry.getConfiguration().getRegistryListenerExecutor().execute(
					() -> listener.localDeviceAdded(registry, localDevice)
			);
        }

    }

    @Override
	Collection<LocalDevice<?>> get() {
        Set<LocalDevice<?>> c = new HashSet<>();
        for (RegistryItem<UDN, LocalDevice<?>> item : getDeviceItems()) {
            c.add(item.getItem());
        }
        return Collections.unmodifiableCollection(c);
    }
    @Override
    boolean remove(final LocalDevice<?> localDevice) throws RegistrationException {
        return remove(localDevice, false);
    }

    boolean remove(final LocalDevice<?> localDevice, boolean shuttingDown) throws RegistrationException {

        LocalDevice<?> registeredDevice = get(localDevice.getIdentity().getUdn(), true);
        if (registeredDevice != null) {

			if (log.isDebugEnabled()) {
				log.debug("Removing local device from registry: " + localDevice);
			}

			setDiscoveryOptions(localDevice.getIdentity().getUdn(), null);
            getDeviceItems().remove(new RegistryItem<UDN, LocalDevice<?>>(localDevice.getIdentity().getUdn()));

            for (Resource<?> deviceResource : getResources(localDevice)) {
                if (registry.removeResource(deviceResource)) {
					if (log.isDebugEnabled()) {
						log.debug("Unregistered resource: " + deviceResource);
					}
				}
            }

            // Active subscriptions
            Iterator<RegistryItem<String, LocalGENASubscription<?>>> it = getSubscriptionItems().iterator();
            while (it.hasNext()) {
                final RegistryItem<String, LocalGENASubscription<?>> incomingSubscription = it.next();

                UDN subscriptionForUDN =
                        incomingSubscription.getItem().getService().getDevice().getIdentity().getUdn();

                if (subscriptionForUDN.equals(registeredDevice.getIdentity().getUdn())) {
					if (log.isDebugEnabled()) {
						log.debug("Removing incoming subscription: " + incomingSubscription.getKey());
					}
					it.remove();
                    if (!shuttingDown) {
                        registry.getConfiguration().getRegistryListenerExecutor().execute(
								() -> incomingSubscription.getItem().end(CancelReason.DEVICE_WAS_REMOVED)
						);
                    }
                }
            }

            if (isAdvertised(localDevice.getIdentity().getUdn()))
         		advertiseByebye(localDevice, !shuttingDown);

            if (!shuttingDown) {
                for (final RegistryListener listener : registry.getListeners()) {
                    registry.getConfiguration().getRegistryListenerExecutor().execute(
							() -> listener.localDeviceRemoved(registry, localDevice)
					);
                }
            }

            return true;
        }

        return false;
    }

    @Override
	void removeAll() {
        removeAll(false);
    }

    void removeAll(boolean shuttingDown) {
        for (LocalDevice<?> device : get()) {
            remove(device, shuttingDown);
        }
    }

    /* ############################################################################################################ */

    public void advertiseLocalDevices() {
        for (RegistryItem<UDN, LocalDevice<?>> localItem : deviceItems) {
            if (isAdvertised(localItem.getKey()))
                advertiseAlive(localItem.getItem());
        }
    }

    /* ############################################################################################################ */
    
    @Override
	void maintain() {

    	if(getDeviceItems().isEmpty()) return ;

        Set<RegistryItem<UDN, LocalDevice<?>>> expiredLocalItems = new HashSet<>();

        // "Flooding" is enabled, check if we need to send advertisements for all devices
        int aliveIntervalMillis = registry.getConfiguration().getAliveIntervalMillis();
        if(aliveIntervalMillis > 0) {
        	long now = System.currentTimeMillis();
        	if(now - lastAliveIntervalTimestamp > aliveIntervalMillis) {
        		lastAliveIntervalTimestamp = now;
                for (RegistryItem<UDN, LocalDevice<?>> localItem : getDeviceItems()) {
                    if (isAdvertised(localItem.getKey())) {
						if (log.isTraceEnabled()) {
							log.trace("Flooding advertisement of local item: " + localItem);
						}
						expiredLocalItems.add(localItem);
                    }
                }
        	}
        } else {
            // Reset, the configuration might dynamically switch the alive interval
            lastAliveIntervalTimestamp = 0;

            // Alive interval is not enabled, regular expiration check of all devices
            for (RegistryItem<UDN, LocalDevice<?>> localItem : getDeviceItems()) {
                if (isAdvertised(localItem.getKey()) && localItem.getExpirationDetails().hasExpired(true)) {
					if (log.isTraceEnabled()) {
						log.trace("Local item has expired: " + localItem);
					}
					expiredLocalItems.add(localItem);
                }
            }
        }

        // Now execute the advertisements
        for (RegistryItem<UDN, LocalDevice<?>> expiredLocalItem : expiredLocalItems) {
			if (log.isDebugEnabled()) {
				log.debug("Refreshing local device advertisement: " + expiredLocalItem.getItem());
			}
			advertiseAlive(expiredLocalItem.getItem());
            expiredLocalItem.getExpirationDetails().stampLastRefresh();
        }

        // Expire incoming subscriptions
        Set<RegistryItem<String, LocalGENASubscription<?>>> expiredIncomingSubscriptions = new HashSet<>();
        for (RegistryItem<String, LocalGENASubscription<?>> item : getSubscriptionItems()) {
            if (item.getExpirationDetails().hasExpired(false)) {
                expiredIncomingSubscriptions.add(item);
            }
        }
        for (RegistryItem<String, LocalGENASubscription<?>> subscription : expiredIncomingSubscriptions) {
			if (log.isDebugEnabled()) {
				log.debug("Removing expired: " + subscription);
			}
			removeSubscription(subscription.getItem());
            subscription.getItem().end(CancelReason.EXPIRED);
        }

    }

    @Override
	void shutdown() {
        log.debug("Clearing all registered subscriptions to local devices during shutdown");
        getSubscriptionItems().clear();

        log.debug("Removing all local devices from registry during shutdown");
        removeAll(true);
    }

    /* ############################################################################################################ */

    protected Random randomGenerator = new Random();

    protected void advertiseAlive(final LocalDevice<?> localDevice) {
        registry.executeAsyncProtocol(() -> {
			try {
				log.trace("Sleeping some milliseconds to avoid flooding the network with ALIVE msgs");
				Thread.sleep(randomGenerator.nextInt(100));
			} catch (InterruptedException ex) {
				if (log.isErrorEnabled()) log.error("Background execution interrupted: ",  ex);
			}
			registry.getProtocolFactory().createSendingNotificationAlive(localDevice).run();
		});
    }

    protected void advertiseByebye(final LocalDevice<?> localDevice, boolean asynchronous) {
        final SendingAsync prot = registry.getProtocolFactory().createSendingNotificationByebye(localDevice);
        if (asynchronous) {
            registry.executeAsyncProtocol(prot);
        } else {
            prot.run();
        }
    }

}
