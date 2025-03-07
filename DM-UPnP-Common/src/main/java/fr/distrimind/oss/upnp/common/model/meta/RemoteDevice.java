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
import fr.distrimind.oss.upnp.common.model.ValidationException;
import fr.distrimind.oss.upnp.common.model.resource.Resource;
import fr.distrimind.oss.upnp.common.model.resource.ServiceEventCallbackResource;
import fr.distrimind.oss.upnp.common.model.types.DeviceType;
import fr.distrimind.oss.upnp.common.model.types.ServiceId;
import fr.distrimind.oss.upnp.common.model.types.ServiceType;
import fr.distrimind.oss.upnp.common.model.types.UDN;
import fr.distrimind.oss.upnp.common.util.URIUtil;

import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * The metadata of a device that was discovered on the network.
 *
 * @author Christian Bauer
 */
public class RemoteDevice extends Device<RemoteDeviceIdentity, RemoteDevice, RemoteService> {

    public RemoteDevice(RemoteDeviceIdentity identity) throws ValidationException {
        super(identity);
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details,
						RemoteService service) throws ValidationException {
        super(identity, type, details, null, Collections.singleton(service));
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details,
                       RemoteService service, RemoteDevice embeddedDevice) throws ValidationException {
        super(identity, type, details, null, Collections.singleton(service), Collections.singletonList(embeddedDevice));
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Collection<RemoteService> services) throws ValidationException {
        super(identity, type, details, null, services);
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type,
                        Collection<RemoteService> services, DeviceDetails details, List<RemoteDevice> embeddedDevices) throws ValidationException {
        super(identity, type, details, null, services, embeddedDevices);
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Icon icon, RemoteService service) throws ValidationException {
        super(identity, type, details, Collections.singleton(icon), Collections.singleton(service));
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Icon icon, RemoteService service, RemoteDevice embeddedDevice) throws ValidationException {
        super(identity, type, details, Collections.singleton(icon), Collections.singleton(service), Collections.singletonList(embeddedDevice));
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Icon icon, Collection<RemoteService> services) throws ValidationException {
        super(identity, type, details, Collections.singleton(icon), services);
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details,
                       Icon icon, Collection<RemoteService> services, List<RemoteDevice> embeddedDevices) throws ValidationException {
        super(identity, type, details, Collections.singleton(icon), services, embeddedDevices);
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details,
                        Collection<Icon> icons, RemoteService service) throws ValidationException {
        super(identity, type, details, icons, Collections.singleton(service));
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details,
                        Collection<Icon> icons, RemoteService service, RemoteDevice embeddedDevice) throws ValidationException {
        super(identity, type, details, icons, Collections.singleton(service), Collections.singletonList(embeddedDevice));
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details,
                        Collection<Icon> icons, Collection<RemoteService> services) throws ValidationException {
        super(identity, type, details, icons, services);
    }

    public RemoteDevice(RemoteDeviceIdentity identity, DeviceType type, DeviceDetails details,
                        Collection<Icon> icons, Collection<RemoteService> services, List<RemoteDevice> embeddedDevices) throws ValidationException {
        super(identity, type, details, icons, services, embeddedDevices);
    }

    public RemoteDevice(RemoteDeviceIdentity identity, UDAVersion version, DeviceType type, DeviceDetails details,
                       Collection<Icon> icons, Collection<RemoteService> services, List<RemoteDevice> embeddedDevices) throws ValidationException {
        super(identity, version, type, details, icons, services, embeddedDevices);
    }


    public URL normalizeURI(URI relativeOrAbsoluteURI) {

        // TODO: I have one device (Netgear 834DG DSL Router) that sends a <URLBase>, and even that is wrong (port)!
        // This can be fixed by "re-enabling" UPnP in the upnpService after a reboot, it will then use the right port...
        // return URIUtil.createAbsoluteURL(getDescriptorURL(), relativeOrAbsoluteURI);

        if (getDetails() != null && getDetails().getBaseURL() != null) {
            // If we have an <URLBase>, all URIs are relative to it
            return URIUtil.createAbsoluteURL(getDetails().getBaseURL(), relativeOrAbsoluteURI);
        } else {
            // Otherwise, they are relative to the descriptor location
            return URIUtil.createAbsoluteURL(getIdentity().getDescriptorURL(), relativeOrAbsoluteURI);
        }

    }

    @Override
    public RemoteDevice newInstance(UDN udn, UDAVersion version, DeviceType type, DeviceDetails details,
									Collection<Icon> icons, Collection<RemoteService> services,
									List<RemoteDevice> embeddedDevices) throws ValidationException {
        return new RemoteDevice(
                new RemoteDeviceIdentity(udn, getIdentity()),
                version, type, details, icons,
                services,
                embeddedDevices
        );
    }

    @Override
    public RemoteService newInstance(ServiceType serviceType, ServiceId serviceId,
									 URI descriptorURI, URI controlURI, URI eventSubscriptionURI,
									 Collection<Action<RemoteService>> actions, List<StateVariable<RemoteService>> stateVariables) throws ValidationException {
        return new RemoteService(
                serviceType, serviceId,
                descriptorURI, controlURI, eventSubscriptionURI,
                actions, stateVariables
        );
    }



    @Override
    public Collection<Resource<?>> discoverResources(Namespace namespace) {
        List<Resource<?>> discovered = new ArrayList<>();

        // Services
        for (RemoteService service : getServices()) {
        	if(service == null) continue;
            discovered.add(new ServiceEventCallbackResource(namespace.getEventCallbackPath(service), service));
        }

        // Embedded devices
        if (hasEmbeddedDevices()) {
            for (RemoteDevice embeddedDevice : getEmbeddedDevices()) {
				if(embeddedDevice == null) continue;
                discovered.addAll(embeddedDevice.discoverResources(namespace));
            }
        }

        return discovered;
    }

    @Override
    public RemoteDevice getRoot() {
        if (isRoot()) return this;
        RemoteDevice current = this;
        while (current.getParentDevice() != null) {
            current = current.getParentDevice();
        }
        return current;
    }

    @Override
    public RemoteDevice findDevice(UDN udn) {
        return find(udn, this);
    }

}