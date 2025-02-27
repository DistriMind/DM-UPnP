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

package fr.distrimind.oss.upnp.model.state;

import fr.distrimind.oss.upnp.model.Command;
import fr.distrimind.oss.upnp.model.ServiceManager;
import fr.distrimind.oss.upnp.model.meta.LocalService;
import fr.distrimind.oss.upnp.model.meta.StateVariable;


/**
 * Reads the value of a state variable, given an instance that implements the service.
 * TODO: The design of this is not final, not happy with the relationship between ActionExecutor and this.
 *
 * @author Christian Bauer
 */
public abstract class StateVariableAccessor {

    public <T> StateVariableValue<LocalService<T>> read(final StateVariable<LocalService<T>> stateVariable, final Object serviceImpl) throws Exception {

        class AccessCommand implements Command<T> {
            Object result;
            @Override
			public void execute(ServiceManager<T> serviceManager) throws Exception {
                result = read(serviceImpl);
                if (stateVariable.getService().isStringConvertibleType(result)) {
                    result = result.toString();
                }
            }
        }

        AccessCommand cmd = new AccessCommand();
        stateVariable.getService().getManager().execute(cmd);
        return new StateVariableValue<>(stateVariable, cmd.result);
    }

    public abstract Class<?> getReturnType();

    // TODO: Especially this shouldn't be public
    public abstract Object read(Object serviceImpl) throws Exception;

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ")";
    }
}
