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

package fr.distrimind.oss.upnp.common.support.lastchange;

import fr.distrimind.oss.upnp.common.model.DefaultServiceManager;
import fr.distrimind.oss.upnp.common.model.ServiceManager;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;
import fr.distrimind.oss.upnp.common.model.meta.StateVariable;
import fr.distrimind.oss.upnp.common.model.state.StateVariableValue;
import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerFourBytes;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Handles the "initial" event state for GENA subscriptions to services using LastChange.
 * <p>
 * When a GENA subscription is made on your AVTransport/RenderingControl service, you have to
 * read the initial state of the service. Ususally DM-UPnP would do this for you and simply
 * access all the state variables of your service behind the scenes. But the
 * AVTransport/RenderingControl service doesn't use regular UPnP state variable eventing
 * internally, they rely on the awful "LastChange" mechanism for their "logical" instances.
 * </p>
 * <p>
 * Use this {@link ServiceManager} instead of the default one for
 * these services.
 * </p>
 *
 * @author Christian Bauer
 */
public class LastChangeAwareServiceManager<T extends LastChangeDelegator> extends DefaultServiceManager<T> {

    final protected LastChangeParser lastChangeParser;

    public LastChangeAwareServiceManager(LocalService<T> localService,
                                         LastChangeParser lastChangeParser) {
        this(localService, null, lastChangeParser);
    }

    public LastChangeAwareServiceManager(LocalService<T> localService,
                                         Class<T> serviceClass,
                                         LastChangeParser lastChangeParser) {
        super(localService, serviceClass);
        this.lastChangeParser = lastChangeParser;
    }

    protected LastChangeParser getLastChangeParser() {
        return lastChangeParser;
    }

    /**
     * Call this method to propagate all accumulated "LastChange" values to GENA subscribers.
     */
    public void fireLastChange() {

        // We need to obtain locks in the right order to avoid deadlocks:
        // 1. The lock() of the DefaultServiceManager
        // 2. The monitor/synchronized of the LastChange.fire() method

    	lock();
    	try {
            getImplementation().getLastChange().fire(getPropertyChangeSupport());
    	} finally {
    		unlock();
    	}
    }

    @Override
    protected Collection<StateVariableValue<LocalService<T>>> readInitialEventedStateVariableValues() throws Exception {

        // We don't use the service's internal LastChange but a fresh new one just for
        // this initial event. Modifying the internal one would trigger event notification's
        // to other subscribers!
        LastChange lc = new LastChange(getLastChangeParser());

        // Get the current "logical" instances of the service
        UnsignedIntegerFourBytes[] ids = getImplementation().getCurrentInstanceIds();
        if (ids.length > 0) {
            for (UnsignedIntegerFourBytes instanceId : ids) {
                // Iterate through all "logical" instances and ask them what their state is
                getImplementation().appendCurrentState(lc, instanceId);
            }
        } else {
            // Use the default "logical" instance with ID 0
            getImplementation().appendCurrentState(lc, new UnsignedIntegerFourBytes(0));
        }

        // Sum it all up and return it in the initial event to the GENA subscriber
        StateVariable<LocalService<T>> variable = getService().getStateVariable("LastChange");
        Collection<StateVariableValue<LocalService<T>>> values = new ArrayList<>();
        values.add(new StateVariableValue<>(variable, lc.toString()));
        return values;
    }

}
