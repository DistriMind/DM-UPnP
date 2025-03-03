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

package fr.distrimind.oss.upnp.common.binding.staging;

import fr.distrimind.oss.upnp.common.model.ValidationException;
import fr.distrimind.oss.upnp.common.model.meta.Action;
import fr.distrimind.oss.upnp.common.model.meta.Device;
import fr.distrimind.oss.upnp.common.model.meta.Service;
import fr.distrimind.oss.upnp.common.model.meta.StateVariable;
import fr.distrimind.oss.upnp.common.model.types.ServiceId;
import fr.distrimind.oss.upnp.common.model.types.ServiceType;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class MutableService<D extends Device<?, D, S>, S extends Service<?, D, S>> {

    public ServiceType serviceType;
    public ServiceId serviceId;
    public URI descriptorURI;
    public URI controlURI;
    public URI eventSubscriptionURI;

    public List<MutableAction<S>> actions = new ArrayList<>();
    public List<MutableStateVariable<S>> stateVariables = new ArrayList<>();

    public S build(D prototype) throws ValidationException {
        return prototype.newInstance(
                serviceType, serviceId,
                descriptorURI, controlURI, eventSubscriptionURI,
                createActions(),
                createStateVariables()
        );
    }

    public Collection<Action<S>> createActions() {
        Collection<Action<S>> array = new ArrayList<>(actions.size());
        for (MutableAction<S> action : actions) {
            array.add(action.build());
        }
        return array;
    }

    public List<StateVariable<S>> createStateVariables() {
        List<StateVariable<S>> array = new ArrayList<>(stateVariables.size());
        for (MutableStateVariable<S> stateVariable : stateVariables) {
            array.add(stateVariable.build());
        }
        return array;
    }

}
