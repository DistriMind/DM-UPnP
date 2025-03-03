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

package fr.distrimind.oss.upnp.common.model.meta;

import fr.distrimind.oss.upnp.common.model.Namespace;
import fr.distrimind.oss.upnp.common.model.ValidationError;
import fr.distrimind.oss.upnp.common.model.ValidationException;
import fr.distrimind.oss.upnp.common.model.profile.DeviceDetailsProvider;
import fr.distrimind.oss.upnp.common.model.profile.RemoteClientInfo;
import fr.distrimind.oss.upnp.common.model.types.DeviceType;
import fr.distrimind.oss.upnp.common.model.types.ServiceId;
import fr.distrimind.oss.upnp.common.model.types.ServiceType;
import fr.distrimind.oss.upnp.common.model.types.UDN;
import fr.distrimind.oss.upnp.common.model.resource.DeviceDescriptorResource;
import fr.distrimind.oss.upnp.common.model.resource.IconResource;
import fr.distrimind.oss.upnp.common.model.resource.ServiceControlResource;
import fr.distrimind.oss.upnp.common.model.resource.ServiceDescriptorResource;
import fr.distrimind.oss.upnp.common.model.resource.ServiceEventSubscriptionResource;
import fr.distrimind.oss.upnp.common.model.resource.Resource;

import java.net.URI;
import java.util.*;

/**
 * The metadata of a device created on this host, by application code.
 *
 * @author Christian Bauer
 */
public class LocalDevice<T> extends Device<DeviceIdentity, LocalDevice<T>, LocalService<T>> {

    final private DeviceDetailsProvider deviceDetailsProvider;

    public LocalDevice(DeviceIdentity identity) throws ValidationException {
        super(identity);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
					   LocalService<T> service) throws ValidationException {
        super(identity, type, details, null, Collections.singleton(service));
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetailsProvider deviceDetailsProvider,
                       LocalService<T> service) throws ValidationException {
        super(identity, type, null, null, Collections.singleton(service));
        this.deviceDetailsProvider = deviceDetailsProvider;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetailsProvider deviceDetailsProvider,
                       LocalService<T> service, LocalDevice<T> embeddedDevice) throws ValidationException {
        super(identity, type, null, null, Collections.singleton(service), Collections.singletonList(embeddedDevice));
        this.deviceDetailsProvider = deviceDetailsProvider;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       LocalService<T> service, LocalDevice<T> embeddedDevice) throws ValidationException {
        super(identity, type, details, null, Collections.singleton(service), Collections.singletonList(embeddedDevice));
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Collection<LocalService<T>> services) throws ValidationException {
        super(identity, type, details, null, services);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type,
                       Collection<LocalService<T>> services, DeviceDetails details, List<LocalDevice<T>> embeddedDevices) throws ValidationException {
        super(identity, type, details, null, services, embeddedDevices);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Icon icon, LocalService<T> service) throws ValidationException {
        super(identity, type, details, Collections.singleton(icon), Collections.singleton(service));
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Icon icon, LocalService<T> service, LocalDevice<T> embeddedDevice) throws ValidationException {
        super(identity, type, details, Collections.singleton(icon), Collections.singleton(service), Collections.singletonList(embeddedDevice));
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Icon icon, Collection<LocalService<T>> services) throws ValidationException {
        super(identity, type, details, Collections.singleton(icon), services);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetailsProvider deviceDetailsProvider,
                       Icon icon, Collection<LocalService<T>> services) throws ValidationException {
        super(identity, type, null, Collections.singleton(icon), services);
        this.deviceDetailsProvider = deviceDetailsProvider;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Icon icon, Collection<LocalService<T>> services, List<LocalDevice<T>> embeddedDevices) throws ValidationException {
        super(identity, type, details, Collections.singleton(icon), services, embeddedDevices);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Collection<Icon> icons, LocalService<T> service) throws ValidationException {
        super(identity, type, details, icons, Collections.singleton(service));
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Collection<Icon> icons, LocalService<T> service, LocalDevice<T> embeddedDevice) throws ValidationException {
        super(identity, type, details, icons, Collections.singleton(service), Collections.singletonList(embeddedDevice));
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetailsProvider deviceDetailsProvider,
                       Collection<Icon> icons, LocalService<T> service, LocalDevice<T> embeddedDevice) throws ValidationException {
        super(identity, type, null, icons, Collections.singleton(service), Collections.singletonList(embeddedDevice));
        this.deviceDetailsProvider = deviceDetailsProvider;
    }

    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Collection<Icon> icons, Collection<LocalService<T>> services) throws ValidationException {
        super(identity, type, details, icons, services);
        this.deviceDetailsProvider = null;
    }
    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Collection<LocalService<T>> services, List<LocalDevice<T>> embeddedDevices) throws ValidationException {
        super(identity, type, details, null, services, embeddedDevices);
        this.deviceDetailsProvider = null;
    }
    public LocalDevice(DeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Collection<Icon> icons, Collection<LocalService<T>> services, List<LocalDevice<T>> embeddedDevices) throws ValidationException {
        super(identity, type, details, icons, services, embeddedDevices);
        this.deviceDetailsProvider = null;
    }

    public LocalDevice(DeviceIdentity identity, UDAVersion version, DeviceType type, DeviceDetails details,
                       Collection<Icon> icons, Collection<LocalService<T>> services, List<LocalDevice<T>> embeddedDevices) throws ValidationException {
        super(identity, version, type, details, icons, services, embeddedDevices);
        this.deviceDetailsProvider = null;
    }
    public LocalDevice(DeviceIdentity identity, UDAVersion version, DeviceType type, DeviceDetailsProvider deviceDetailsProvider,
                       Collection<LocalService<T>> services, List<LocalDevice<T>> embeddedDevices) throws ValidationException {
        super(identity, version, type, null, null, services, embeddedDevices);
        this.deviceDetailsProvider = deviceDetailsProvider;
    }
    public LocalDevice(DeviceIdentity identity, UDAVersion version, DeviceType type, DeviceDetailsProvider deviceDetailsProvider,
                       Collection<Icon> icons, Collection<LocalService<T>> services, List<LocalDevice<T>> embeddedDevices) throws ValidationException {
        super(identity, version, type, null, icons, services, embeddedDevices);
        this.deviceDetailsProvider = deviceDetailsProvider;
    }

    public DeviceDetailsProvider getDeviceDetailsProvider() {
        return deviceDetailsProvider;
    }

    @Override
    public DeviceDetails getDetails(RemoteClientInfo info) {
        if (getDeviceDetailsProvider() != null) {
            return getDeviceDetailsProvider().provide(info);
        }
        return this.getDetails();
    }




    @Override
    public LocalDevice<T> newInstance(UDN udn, UDAVersion version, DeviceType type, DeviceDetails details,
								   Collection<Icon> icons, Collection<LocalService<T>> services, List<LocalDevice<T>> embeddedDevices)
            throws ValidationException {
        return new LocalDevice<>(
                new DeviceIdentity(udn, getIdentity().getMaxAgeSeconds()),
                version, type, details, icons,
                services,
				embeddedDevices.isEmpty() ? null:embeddedDevices
        );
    }

    public LocalService<T> newInstanceWithGeneric(ServiceType serviceType, ServiceId serviceId,
                                           URI descriptorURI, URI controlURI, URI eventSubscriptionURI,
                                           Collection<Action<LocalService<T>>> actions, List<StateVariable<LocalService<T>>> stateVariables) throws ValidationException {
        return new LocalService<>(
                serviceType, serviceId,
                actions, stateVariables
        );
    }
    @Override
    public LocalService<T> newInstance(ServiceType serviceType, ServiceId serviceId,
									URI descriptorURI, URI controlURI, URI eventSubscriptionURI,
									Collection<Action<LocalService<T>>> actions, List<StateVariable<LocalService<T>>> stateVariables) throws ValidationException {
        return new LocalService<>(
                serviceType, serviceId,
                actions, stateVariables
        );
    }



    @Override
    public List<ValidationError> validate() {
		List<ValidationError> errors = new ArrayList<>(super.validate());

        // We have special rules for local icons, the URI must always be a relative path which will
        // be added to the device base URI!
        if (hasIcons()) {
            for (Icon icon : getIcons()) {
                if (icon.getUri().isAbsolute()) {
                    errors.add(new ValidationError(
                            getClass(),
                            "icons",
                            "Local icon URI can not be absolute: " + icon.getUri()
                    ));
                }
                if (icon.getUri().toString().contains("../")) {
                    errors.add(new ValidationError(
                            getClass(),
                            "icons",
                            "Local icon URI must not contain '../': " + icon.getUri()
                    ));
                }
                if (icon.getUri().toString().startsWith("/")) {
                    errors.add(new ValidationError(
                            getClass(),
                            "icons",
                            "Local icon URI must not start with '/': " + icon.getUri()
                    ));
                }
            }
        }

        return errors;
    }

    @Override
    public Collection<Resource<?>> discoverResources(Namespace namespace) {
        List<Resource<?>> discovered = new ArrayList<>();

        // Device
        if (isRoot()) {
            // This should guarantee that each logical local device tree (with all its embedded devices) has only
            // one device descriptor resource - because only one device in the tree isRoot().
            discovered.add(new DeviceDescriptorResource<>(namespace.getDescriptorPath(this), this));
        }

        // Services
        for (LocalService<T> service : getServices()) {

            discovered.add(
                    new ServiceDescriptorResource<>(namespace.getDescriptorPath(service), service)
            );

            // Control
            discovered.add(
                    new ServiceControlResource<>(namespace.getControlPath(service), service)
            );

            // Event subscription
            discovered.add(
                    new ServiceEventSubscriptionResource<>(namespace.getEventSubscriptionPath(service), service)
            );

        }

        // Icons
        for (Icon icon : getIcons()) {
            discovered.add(new IconResource(namespace.prefixIfRelative(this, icon.getUri()), icon));
        }

        // Embedded devices
        if (hasEmbeddedDevices()) {
            for (LocalDevice<T> embeddedDevice : getEmbeddedDevices()) {
                discovered.addAll(embeddedDevice.discoverResources(namespace));
            }
        }
        return Collections.unmodifiableCollection(discovered);
    }

    @Override
    public LocalDevice<T> getRoot() {
        if (isRoot()) return this;
        LocalDevice<T> current = this;
        while (current.getParentDevice() != null) {
            current = current.getParentDevice();
        }
        return current;
    }

    @Override
    public LocalDevice<T> findDevice(UDN udn) {
        return find(udn, this);
    }

}
