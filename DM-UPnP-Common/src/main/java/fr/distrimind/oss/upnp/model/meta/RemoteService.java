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

package fr.distrimind.oss.upnp.model.meta;

import fr.distrimind.oss.upnp.model.ValidationError;
import fr.distrimind.oss.upnp.model.ValidationException;
import fr.distrimind.oss.upnp.model.types.ServiceId;
import fr.distrimind.oss.upnp.model.types.ServiceType;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The metadata of a service discovered on a remote device.
 * <p>
 * Includes the URI's for getting the service's descriptor, calling its
 * actions, and subscribing to events.
 * </p>
 * 
 * @author Christian Bauer
 */
public class RemoteService extends Service<RemoteDeviceIdentity, RemoteDevice, RemoteService> {

    final private URI descriptorURI;
    final private URI controlURI;
    final private URI eventSubscriptionURI;

    public RemoteService(ServiceType serviceType, ServiceId serviceId,
						 URI descriptorURI, URI controlURI, URI eventSubscriptionURI) throws ValidationException {
        this(serviceType, serviceId, descriptorURI, controlURI, eventSubscriptionURI, null, null);
    }

    public RemoteService(ServiceType serviceType, ServiceId serviceId,
                         URI descriptorURI, URI controlURI, URI eventSubscriptionURI,
                         Collection<Action<RemoteService>> actions, Collection<StateVariable<RemoteService>> stateVariables) throws ValidationException {
        super(serviceType, serviceId, actions, stateVariables);

        this.descriptorURI = descriptorURI;
        this.controlURI = controlURI;
        this.eventSubscriptionURI = eventSubscriptionURI;

        List<ValidationError> errors = validateThis();
        if (!errors.isEmpty()) {
            throw new ValidationException("Validation of device graph failed, call getErrors() on exception", errors);
        }
    }

    @Override
    public Action<RemoteService> getQueryStateVariableAction() {
        return new QueryStateVariableAction<>(this);
    }

    public URI getDescriptorURI() {
        return descriptorURI;
    }

    public URI getControlURI() {
        return controlURI;
    }

    public URI getEventSubscriptionURI() {
        return eventSubscriptionURI;
    }

    public List<ValidationError> validateThis() {
        List<ValidationError> errors = new ArrayList<>();

        if (getDescriptorURI() == null) {
            errors.add(new ValidationError(
                    getClass(),
                    "descriptorURI",
                    "Descriptor location (SCPDURL) is required"
            ));
        }

        if (getControlURI() == null) {
            errors.add(new ValidationError(
                    getClass(),
                    "controlURI",
                    "Control URL is required"
            ));
        }

        if (getEventSubscriptionURI() == null) {
            errors.add(new ValidationError(
                    getClass(),
                    "eventSubscriptionURI",
                    "Event subscription URL is required"
            ));
        }

        return errors;
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") Descriptor: " + getDescriptorURI();
    }

}