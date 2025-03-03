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

import fr.distrimind.oss.upnp.common.model.resource.Resource;
import fr.distrimind.oss.upnp.common.model.ValidationException;
import fr.distrimind.oss.upnp.common.model.meta.Device;
import fr.distrimind.oss.upnp.common.model.gena.GENASubscription;
import fr.distrimind.oss.upnp.common.model.types.DeviceType;
import fr.distrimind.oss.upnp.common.model.types.ServiceType;
import fr.distrimind.oss.upnp.common.model.types.UDN;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Internal class, required by {@link RegistryImpl}.
 *
 * @author Christian Bauer
 */
abstract class RegistryItems<D extends Device<?, ?, ?>, S extends GENASubscription<?>> {

    protected final RegistryImpl registry;

    protected final Set<RegistryItem<UDN, D>> deviceItems = new HashSet<>();
    protected final Set<RegistryItem<String, S>> subscriptionItems = new HashSet<>();

    RegistryItems(RegistryImpl registry) {
        this.registry = registry;
    }

    Set<RegistryItem<UDN, D>> getDeviceItems() {
        return deviceItems;
    }

    Set<RegistryItem<String, S>> getSubscriptionItems() {
        return subscriptionItems;
    }

    abstract void add(D device);
    abstract boolean remove(final D device);
    abstract void removeAll();

    abstract void maintain();
    abstract void shutdown();

    /**
     * Returns root and embedded devices registered under the given UDN.
     *
     * @param udn A unique device name.
     * @param rootOnly Set to true if only root devices (no embedded) should be searched
     * @return Any registered root or embedded device under the given UDN, <code>null</code> if
     *         no device with the given UDN has been registered.
     */
    D get(UDN udn, boolean rootOnly) {
        for (RegistryItem<UDN, D> item : deviceItems) {
            D device = item.getItem();
            if (device.getIdentity().getUdn().equals(udn)) {
                return device;
            }
            if (!rootOnly) {
                @SuppressWarnings("unchecked") D foundDevice = (D)item.getItem().findDevice(udn);
                if (foundDevice != null) return foundDevice;
            }
        }
        return null;
    }

    /**
     * Returns all devices (root or embedded) with a compatible type.
     * <p>
     * This routine will check compatible versions, as described by the UDA.
   
     *
     * @param deviceType The minimum device type required.
     * @return Any registered root or embedded device with a compatible type.
     */
    Collection<D> get(DeviceType deviceType) {
        Collection<D> devices = new HashSet<>();
        for (RegistryItem<UDN, D> item : deviceItems) {
            @SuppressWarnings("unchecked") Collection<D> d = (Collection<D>) item.getItem().findDevices(deviceType);
            if (d != null) {
                devices.addAll(d);
            }
        }
        return devices;
    }

    /**
     * Returns all devices (root or embedded) which have at least one matching service.
     *
     * @param serviceType The type of service to search for.
     * @return Any registered root or embedded device with at least one matching service.
     */
    Collection<D> get(ServiceType serviceType) {
        Collection<D> devices = new HashSet<>();
        for (RegistryItem<UDN, D> item : deviceItems) {
            @SuppressWarnings("unchecked") Collection<D> d = (Collection<D>) item.getItem().findDevices(serviceType);
            if (d != null) {
                devices.addAll(d);
            }
        }
        return devices;
    }

    Collection<D> get() {
        Collection<D> devices = new HashSet<>();
        for (RegistryItem<UDN, D> item : deviceItems) {
            devices.add(item.getItem());
        }
        return devices;
    }

    boolean contains(D device) {
        return contains(device.getIdentity().getUdn());
    }

    boolean contains(UDN udn) {
        return deviceItems.contains(new RegistryItem<UDN, D>(udn));
    }

    void addSubscription(S subscription) {

        RegistryItem<String, S> subscriptionItem =
                new RegistryItem<>(
                        subscription.getSubscriptionId(),
                        subscription,
                        subscription.getActualDurationSeconds()
                );

        subscriptionItems.add(subscriptionItem);
    }

    boolean updateSubscription(S subscription) {
        if (removeSubscription(subscription)) {
            addSubscription(subscription);
            return true;
        }
        return false;
    }

    boolean removeSubscription(S subscription) {
        return subscriptionItems.remove(new RegistryItem<String, S>(subscription.getSubscriptionId()));
    }

    S getSubscription(String subscriptionId) {
        for (RegistryItem<String, S> registryItem : subscriptionItems) {
            if (registryItem.getKey().equals(subscriptionId)) {
                return registryItem.getItem();
            }
        }
        return null;
    }

    Collection<Resource<?>> getResources(Device<?, ?, ?> device) throws RegistrationException {
        try {
            return registry.getConfiguration().getNamespace().getResources(device);
        } catch (ValidationException ex) {
            throw new RegistrationException("Resource discover error: " + ex, ex);
        }
    }
}
