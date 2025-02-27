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

package fr.distrimind.oss.upnp.model.action;

import fr.distrimind.oss.upnp.model.Command;
import fr.distrimind.oss.upnp.model.ServiceManager;
import fr.distrimind.oss.upnp.model.meta.Action;
import fr.distrimind.oss.upnp.model.meta.ActionArgument;
import fr.distrimind.oss.upnp.model.meta.LocalService;
import fr.distrimind.oss.upnp.model.state.StateVariableAccessor;
import fr.distrimind.oss.upnp.model.types.ErrorCode;
import fr.distrimind.oss.upnp.model.types.InvalidValueException;
import fr.distrimind.oss.upnp.util.Exceptions;

import java.util.*;
import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.Log;

/**
 * Shared procedures for action executors based on an actual service implementation instance.
 *
 * @author Christian Bauer
 */
public abstract class AbstractActionExecutor implements ActionExecutor {

    final private static DMLogger log = Log.getLogger(AbstractActionExecutor.class);

    protected Map<? extends ActionArgument<? extends LocalService<?>>, StateVariableAccessor> outputArgumentAccessors =
        new HashMap<>();

    protected AbstractActionExecutor() {
    }

    protected AbstractActionExecutor(Map<? extends ActionArgument<? extends LocalService<?>>, StateVariableAccessor> outputArgumentAccessors) {
        this.outputArgumentAccessors = outputArgumentAccessors;
    }

    public Map<? extends ActionArgument<? extends LocalService<?>>, StateVariableAccessor> getOutputArgumentAccessors() {
        return outputArgumentAccessors;
    }

    /**
     * Obtains the service implementation instance from the {@link ServiceManager}, handles exceptions.
     */
    @Override
	public <T> void execute(final ActionInvocation<LocalService<T>> actionInvocation) {

		if (log.isDebugEnabled()) {
            log.debug("Invoking on local service: " + actionInvocation);
		}

		final LocalService<T> service = actionInvocation.getAction().getService();

        try {

            if (service.getManager() == null) {
                throw new IllegalStateException("Service has no implementation factory, can't get service instance");
            }

            service.getManager().execute(new Command<>() {
                @Override
				public void execute(ServiceManager<T> serviceManager) throws Exception {
                    AbstractActionExecutor.this.execute(
                            actionInvocation,
                            serviceManager.getImplementation()
                    );
                }

                @Override
                public String toString() {
                    return "Action invocation: " + actionInvocation.getAction();
                }
            });

        } catch (ActionException ex) {
            if (log.isDebugEnabled()) {
                log.debug("ActionException thrown by service, wrapping in invocation and returning: ", ex);
                log.debug("Exception root cause: ", Exceptions.unwrap(ex));
            }
            actionInvocation.setFailure(ex);
        } catch (InterruptedException ex) {
            if (log.isDebugEnabled()) {
                log.debug("InterruptedException thrown by service, wrapping in invocation and returning: ", ex);
                log.debug("Exception root cause: ", Exceptions.unwrap(ex));
            }
            actionInvocation.setFailure(new ActionCancelledException(ex));
        } catch (Throwable t) {
            Throwable rootCause = Exceptions.unwrap(t);
            if (log.isDebugEnabled()) {
                log.debug("Execution has thrown, wrapping root cause in ActionException and returning: " + t);
                log.debug("Exception root cause: ", rootCause);
            }
            actionInvocation.setFailure(
                new ActionException(
                    ErrorCode.ACTION_FAILED,
                    (rootCause.getMessage() != null ? rootCause.getMessage() : rootCause.toString()),
                    rootCause
                )
            );
        }
    }

    protected abstract <T> void execute(ActionInvocation<LocalService<T>> actionInvocation, Object serviceImpl) throws Exception;

    /**
     * Reads the output arguments after an action execution using accessors.
     *
     * @param action The action of which the output arguments are read.
     * @param instance The instance on which the accessors will be invoked.
     * @return <code>null</code> if the action has no output arguments, a single instance if it has one, an
     *         <code>Object[]</code> otherwise.
     * @throws Exception if a problem occurs
     */
    protected <T> Object readOutputArgumentValues(Action<LocalService<T>> action, Object instance) throws Exception {
        List<Object> results = new ArrayList<>(action.getOutputArguments().size());
		if (log.isDebugEnabled()) {
            log.debug("Attempting to retrieve output argument values using accessor: " + action.getOutputArguments().size());
		}

		for (ActionArgument<LocalService<T>> outputArgument : action.getOutputArguments()) {
			if (log.isTraceEnabled()) {
				log.trace("Calling accessor method for: " + outputArgument);
			}

			StateVariableAccessor accessor = getOutputArgumentAccessors().get(outputArgument);
            if (accessor != null) {
				if (log.isDebugEnabled()) {
					log.debug("Calling accessor to read output argument value: " + accessor);
				}
				results.add(accessor.read(instance));
            } else {
                throw new IllegalStateException("No accessor bound for: " + outputArgument);
            }
        }

        if (results.size() == 1) {
            return results.iterator().next();
        }
        return !results.isEmpty() ? results : null;
    }

    /**
     * Sets the output argument value on the {@link ActionInvocation}, considers string conversion.
     */
    protected <T> void setOutputArgumentValue(ActionInvocation<LocalService<T>> actionInvocation, ActionArgument<LocalService<T>> argument, Object result)
            throws ActionException {

        LocalService<?> service = actionInvocation.getAction().getService();

        if (result != null) {
            try {
                if (service.isStringConvertibleType(result)) {
                    log.debug("Result of invocation matches convertible type, setting toString() single output argument value");
                    actionInvocation.setOutput(new ActionArgumentValue<>(argument, result.toString()));
                } else {
                    log.debug("Result of invocation is Object, setting single output argument value");
                    actionInvocation.setOutput(new ActionArgumentValue<>(argument, result));
                }
            } catch (InvalidValueException ex) {
                throw new ActionException(
                        ErrorCode.ARGUMENT_VALUE_INVALID,
                        "Wrong type or invalid value for '" + argument.getName() + "': " + ex.getMessage(),
                        ex
                );
            }
        } else {

            log.debug("Result of invocation is null, not setting any output argument value(s)");
        }

    }

}
