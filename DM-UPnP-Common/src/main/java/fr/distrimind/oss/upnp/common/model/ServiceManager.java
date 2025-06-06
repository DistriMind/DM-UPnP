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

package fr.distrimind.oss.upnp.common.model;

import fr.distrimind.oss.upnp.common.model.action.ActionExecutor;
import fr.distrimind.oss.upnp.common.model.action.MethodActionExecutor;
import fr.distrimind.oss.upnp.common.model.state.FieldStateVariableAccessor;
import fr.distrimind.oss.upnp.common.model.state.StateVariableAccessor;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;
import fr.distrimind.oss.upnp.common.model.state.StateVariableValue;

import java.beans.PropertyChangeSupport;
import java.util.Collection;

/**
 * Binds the metadata of a service to a service implementation, unified interface for accessing local services.
 * <p>
 * The UPnP core will always access a local service implementation through
 * this manager, available with {@link LocalService#getManager()}:
 * </p>
 * <ul>
 * <li>
 * The {@link ActionExecutor}s use the manager to process
 * UPnP control invocations. It's the service manager's job to translate
 * such an action invocation into an actual method invocation, or any other procedure
 * that satisfies the requirements. The {@link ActionExecutor}
 * works together with the manager, for example, the
 * {@link MethodActionExecutor} expects that an action
 * method can be invoked through reflection on the instance returned by the manager's
 * {@link #getImplementation()} method. This is possible with the
 * the {@link DefaultServiceManager}. A different service manager
 * might require a different set of action executors, and vice versa.
 * </li>
 * <li>
 * The {@link StateVariableAccessor}s use the manager
 * to process UPnP state variable queries and GENA eventing. It's the service manager's
 * job to return an actual value when a state variable has to be read. The
 * {@link StateVariableAccessor} works together with
 * the service manager, for example, the {@link FieldStateVariableAccessor}
 * expects that a state variable value can be read through reflection on a field, of
 * the instance returned by {@link #getImplementation()}. This is possible with the
 * {@link DefaultServiceManager}. A different service manager
 * might require a different set of state variable accessors, and vice versa.
 * </li>
 * <li>
 * A service manager has to notify the UPnP core, and especially the GENA eventing system,
 * whenever the state of any evented UPnP state variable changes. For new subscriptions
 * GENA also has to read the current state of the service manually, when the subscription
 * has been established and an initial event message has to be send to the subscriber.
 * </li>
 * </ul>
 * <p>
 * A service manager can implement these concerns in any way imaginable. It has to
 * be thread-safe.
 * </p>
 *
 * @param <T> The interface expected by the
 *            bound {@link ActionExecutor}s
 *            and {@link StateVariableAccessor}s.
 *
 * @author Christian Bauer
 */
public interface ServiceManager<T> {

    /**
     * Use this property name when propagating change events that affect any evented UPnP
     * state variable. This name is detected by the GENA subsystem.
     */
    String EVENTED_STATE_VARIABLES = "_EventedStateVariables";

    /**
     * @return The metadata of the service to which this manager is assigned.
     */
    LocalService<T> getService();

    /**
     * @return An instance with the interface expected by the
     *         bound {@link ActionExecutor}s
    *          and {@link StateVariableAccessor}s.
     */
    T getImplementation();

    /**
     * Double-dispatch of arbitrary commands, used by action executors and state variable accessors.
     * <p>
     * The service manager will execute the given {@link Command} and it
     * might decorate the execution, for example, by locking/unlocking access to a shared service
     * implementation before and after the execution.
   
     * @param cmd The command to execute.
     * @throws Exception Any exception, without wrapping, as thrown by {@link Command#execute(ServiceManager)}
     */
    void execute(Command<T> cmd) throws Exception;

    /**
     * Provides the capability to monitor the service for state changes.
     * <p>
     * The GENA subsystem expects that this adapter will notify its listeners whenever
     * <em>any</em> evented UPnP state variable of the service has changed its state. The
     * following change event is expected:
   
     * <ul>
     * <li>The property name is the constant {@link #EVENTED_STATE_VARIABLES}.</li>
     * <li>The "old value" can be <code>null</code>, only the current state has to be included.</li>
     * <li>The "new value" is a <code>Collection</code> of {@link StateVariableValue},
     *     representing the current state of the service after the change.</li>
     * </ul>
     * <p>
     * The collection has to include values for <em>all</em> state variables, no
     * matter what state variable was updated. Any other event is ignored (e.g. individual property
     * changes).
   
     *
     * @return An adapter that will notify its listeners whenever any evented state variable changes.
     */
    PropertyChangeSupport getPropertyChangeSupport();

    /**
     * Reading the state of a service manually.
     *
     * @return A <code>Collection</code> of {@link StateVariableValue}, representing
     *         the current state of the service, that is, all evented state variable values.
     * @throws Exception Any error that occurred when the service's state was accessed.
     */
    Collection<StateVariableValue<LocalService<T>>> getCurrentState() throws Exception;

}
