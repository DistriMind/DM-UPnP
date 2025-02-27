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

package fr.distrimind.oss.upnp.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.Log;

import fr.distrimind.oss.upnp.model.meta.LocalService;
import fr.distrimind.oss.upnp.model.meta.StateVariable;
import fr.distrimind.oss.upnp.model.state.StateVariableAccessor;
import fr.distrimind.oss.upnp.model.state.StateVariableValue;
import fr.distrimind.oss.upnp.util.Exceptions;
import fr.distrimind.oss.upnp.util.Reflections;

/**
 * Default implementation, creates and manages a single instance of a plain Java bean.
 * <p>
 * Creates instance of the defined service class when it is first needed (acts as a factory),
 * manages the instance in a field (it's shared), and synchronizes (locks) all
 * multi-threaded access. A locking attempt will timeout after 500 milliseconds with
 * a runtime exception if another operation is already in progress. Override
 * {@link #getLockTimeoutMillis()} to customize this behavior, e.g. if your service
 * bean is slow and requires more time for typical action executions or state
 * variable reading.
 * </p>
 *
 * @author Christian Bauer
 */
public class DefaultServiceManager<T> implements ServiceManager<T> {

    final private static DMLogger log = Log.getLogger(DefaultServiceManager.class);

    final protected LocalService<T> service;
    final protected Class<T> serviceClass;
    final protected ReentrantLock reentrantLock = new ReentrantLock(true);

    // Locking!
    protected T serviceImpl;
    protected PropertyChangeSupport propertyChangeSupport;

    protected DefaultServiceManager(LocalService<T> service) {
        this(service, null);
    }

    public DefaultServiceManager(LocalService<T> service, Class<T> serviceClass) {
        this.service = service;
        this.serviceClass = serviceClass;
    }

    // The monitor entry and exit methods

    protected void lock() {
        try {
            if (reentrantLock.tryLock(getLockTimeoutMillis(), TimeUnit.MILLISECONDS)) {
                if (log.isTraceEnabled())
                    log.trace("Acquired lock");
            } else {
                throw new RuntimeException("Failed to acquire lock in milliseconds: " + getLockTimeoutMillis());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to acquire lock:" + e);
        }
    }

    protected void unlock() {
        if (log.isTraceEnabled())
            log.trace("Releasing lock");
        reentrantLock.unlock();
    }

    protected int getLockTimeoutMillis() {
        return 500;
    }

    @Override
	public LocalService<T> getService() {
        return service;
    }

    @Override
	public T getImplementation() {
        lock();
        try {
            if (serviceImpl == null) {
                init();
            }
            return serviceImpl;
        } finally {
            unlock();
        }
    }

    @Override
	public PropertyChangeSupport getPropertyChangeSupport() {
        lock();
        try {
            if (propertyChangeSupport == null) {
                init();
            }
            return propertyChangeSupport;
        } finally {
            unlock();
        }
    }

    @Override
	public void execute(Command<T> cmd) throws Exception {
        lock();
        try {
            cmd.execute(this);
        } finally {
            unlock();
        }
    }

    @Override
    public Collection<StateVariableValue<LocalService<T>>> getCurrentState() throws Exception {
        lock();
        try {
            Collection<StateVariableValue<LocalService<T>>> values = readInitialEventedStateVariableValues();
            if (values != null) {
                log.debug("Obtained initial state variable values for event, skipping individual state variable accessors");
                return values;
            }
            values = new ArrayList<>();
            for (StateVariable<LocalService<T>> stateVariable : getService().getStateVariables()) {
                if (stateVariable.getEventDetails().isSendEvents()) {
                    StateVariableAccessor accessor = getService().getAccessor(stateVariable);
                    if (accessor == null)
                        throw new IllegalStateException("No accessor for evented state variable");
                    values.add(accessor.read(stateVariable, getImplementation()));
                }
            }
            return values;
        } finally {
            unlock();
        }
    }

    protected Collection<StateVariableValue<LocalService<T>>> getCurrentState(String[] variableNames) throws Exception {
        lock();
        try {
            Collection<StateVariableValue<LocalService<T>>> values = new ArrayList<>();
            for (String vn : variableNames) {
                String variableName = vn.trim();

                StateVariable<LocalService<T>> stateVariable = getService().getStateVariable(variableName);
                if (stateVariable == null || !stateVariable.getEventDetails().isSendEvents()) {
					if (log.isDebugEnabled()) {
						log.debug("Ignoring unknown or non-evented state variable: " + variableName);
					}
					continue;
                }

                StateVariableAccessor accessor = getService().getAccessor(stateVariable);
                if (accessor == null) {
                    if (log.isWarnEnabled()) log.warn("Ignoring evented state variable without accessor: " + variableName);
                    continue;
                }
                values.add(accessor.read(stateVariable, getImplementation()));
            }
            return values;
        } finally {
            unlock();
        }
    }

    protected void init() {
        log.debug("No service implementation instance available, initializing...");
        try {
            // The actual instance we were going to use and hold a reference to (1:1 instance for manager)
            serviceImpl = createServiceInstance();

            // How the implementation instance will tell us about property changes
            propertyChangeSupport = createPropertyChangeSupport(serviceImpl);
            propertyChangeSupport.addPropertyChangeListener(createPropertyChangeListener(serviceImpl));

        } catch (Exception ex) {
            throw new RuntimeException("Could not initialize implementation: " + ex, ex);
        }
    }

    protected T createServiceInstance() throws Exception {
        if (serviceClass == null) {
            throw new IllegalStateException("Subclass has to provide service class or override createServiceInstance()");
        }
        try {
            // Use this constructor if possible
            return serviceClass.getConstructor(LocalService.class).newInstance(getService());
        } catch (NoSuchMethodException ex) {
			if (log.isDebugEnabled()) {
				log.debug("Creating new service implementation instance with no-arg constructor: " + serviceClass.getName());
			}
			return serviceClass.getConstructor().newInstance();
        }
    }

    protected PropertyChangeSupport createPropertyChangeSupport(T serviceImpl) throws Exception {
        Method m;
        if ((m = Reflections.getGetterMethod(serviceImpl.getClass(), "propertyChangeSupport")) != null &&
            PropertyChangeSupport.class.isAssignableFrom(m.getReturnType())) {
			if (log.isDebugEnabled()) {
				log.debug("Service implementation instance offers PropertyChangeSupport, using that: " + serviceImpl.getClass().getName());
			}
			return (PropertyChangeSupport) m.invoke(serviceImpl);
        }
		if (log.isDebugEnabled()) {
            log.debug("Creating new PropertyChangeSupport for service implementation: " + serviceImpl.getClass().getName());
		}
		return new PropertyChangeSupport(serviceImpl);
    }

    protected PropertyChangeListener createPropertyChangeListener(T serviceImpl) throws Exception {
        return new DefaultPropertyChangeListener();
    }

    @SuppressWarnings("PMD.ReturnEmptyCollectionRatherThanNull")
    protected Collection<StateVariableValue<LocalService<T>>> readInitialEventedStateVariableValues() throws Exception {
        return null;
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") Implementation: " + serviceImpl;
    }

    protected class DefaultPropertyChangeListener implements PropertyChangeListener {

        @Override
		public void propertyChange(PropertyChangeEvent e) {
			if (log.isTraceEnabled()) {
				log.trace("Property change event on local service: " + e.getPropertyName());
			}

			// Prevent recursion
            if (EVENTED_STATE_VARIABLES.equals(e.getPropertyName())) return;

            String[] variableNames = ModelUtil.fromCommaSeparatedList(e.getPropertyName());
			if (log.isDebugEnabled()) {
				log.debug("Changed variable names: " + Arrays.toString(variableNames));
			}

			try {
                Collection<StateVariableValue<LocalService<T>>> currentValues = getCurrentState(variableNames);

                if (!currentValues.isEmpty()) {
                    getPropertyChangeSupport().firePropertyChange(
                        EVENTED_STATE_VARIABLES,
                        null,
                        currentValues
                    );
                }

            } catch (Exception ex) {
                // TODO: Is it OK to only log this error? It means we keep running although we couldn't send events?
                if (log.isErrorEnabled()) log.error(
                        "Error reading state of service after state variable update event: ", Exceptions.unwrap(ex),
                        ex
                    );
            }
        }
    }
}
