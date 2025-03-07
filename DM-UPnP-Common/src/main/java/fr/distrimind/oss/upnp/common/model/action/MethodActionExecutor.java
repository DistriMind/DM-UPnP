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

package fr.distrimind.oss.upnp.common.model.action;

import fr.distrimind.oss.upnp.common.model.profile.RemoteClientInfo;
import fr.distrimind.oss.upnp.common.model.meta.ActionArgument;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;
import fr.distrimind.oss.upnp.common.model.state.StateVariableAccessor;
import fr.distrimind.oss.upnp.common.model.types.ErrorCode;
import fr.distrimind.oss.upnp.common.util.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Invokes methods on a service implementation instance with reflection.
 *
 * <p>
 * If the method has an additional last parameter of type
 * {@link RemoteClientInfo}, the details
 * of the control point client will be provided to the action method. You can use this
 * to get the client's address and request headers, and to provide extra response headers.
 * </p>
 *
 * @author Christian Bauer
 */
public class MethodActionExecutor extends AbstractActionExecutor {

    final private static DMLogger log = Log.getLogger(MethodActionExecutor.class);

    protected Method method;

    public MethodActionExecutor(Method method) {
        this.method = method;
    }

    public MethodActionExecutor(Map<? extends ActionArgument<? extends LocalService<?>>, StateVariableAccessor> outputArgumentAccessors, Method method) {
        super(outputArgumentAccessors);
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    protected <T> void execute(ActionInvocation<LocalService<T>> actionInvocation, Object serviceImpl) throws Exception {

        // Find the "real" parameters of the method we want to call, and create arguments
        Object[] inputArgumentValues = createInputArgumentValues(actionInvocation, method);

        // Simple case: no output arguments
        if (!actionInvocation.getAction().hasOutputArguments()) {
			if (log.isDebugEnabled()) {
				log.debug("Calling local service method with no output arguments: " + method);
			}
			Reflections.invoke(method, serviceImpl, inputArgumentValues);
            return;
        }

        boolean isVoid = method.getReturnType().equals(Void.TYPE);

		if (log.isDebugEnabled()) {
            log.debug("Calling local service method with output arguments: " + method);
		}
		Object result;
        boolean isArrayResultProcessed = true;
        if (isVoid) {

            log.debug("Action method is void, calling declared accessors(s) on service instance to retrieve ouput argument(s)");
            Reflections.invoke(method, serviceImpl, inputArgumentValues);
            result = readOutputArgumentValues(actionInvocation.getAction(), serviceImpl);

        } else if (isUseOutputArgumentAccessors(actionInvocation)) {

            log.debug("Action method is not void, calling declared accessor(s) on returned instance to retrieve ouput argument(s)");
            Object returnedInstance = Reflections.invoke(method, serviceImpl, inputArgumentValues);
            result = readOutputArgumentValues(actionInvocation.getAction(), returnedInstance);

        } else {

            log.debug("Action method is not void, using returned value as (single) output argument");
            result = Reflections.invoke(method, serviceImpl, inputArgumentValues);
            isArrayResultProcessed = false; // We never want to process e.g. byte[] as individual variable values
        }

        List<ActionArgument<LocalService<T>>> outputArgs = actionInvocation.getAction().getOutputArguments();

        if (isArrayResultProcessed && result instanceof List) {
            @SuppressWarnings("unchecked") List<Object> results = (List<Object>) result;
			if (log.isDebugEnabled()) {
				log.debug("Accessors returned Object[], setting output argument values: " + results.size());
			}
			for (int i = 0; i < outputArgs.size(); i++) {
                setOutputArgumentValue(actionInvocation, outputArgs.get(i), results.get(i));
            }
        } else if (outputArgs.size() == 1) {
            setOutputArgumentValue(actionInvocation, outputArgs.iterator().next(), result);
        } else {
            throw new ActionException(
                    ErrorCode.ACTION_FAILED,
                    "Method return does not match required number of output arguments: " + outputArgs.size()
            );
        }

    }

    protected <T> boolean isUseOutputArgumentAccessors(ActionInvocation<LocalService<T>> actionInvocation) {
        for (ActionArgument<LocalService<T>> argument : actionInvocation.getAction().getOutputArguments()) {
            // If there is one output argument for which we have an accessor, all arguments need accessors
            if (getOutputArgumentAccessors().get(argument) != null) {
                return true;
            }
        }
        return false;
    }

    protected <T> Object[] createInputArgumentValues(ActionInvocation<LocalService<T>> actionInvocation, Method method) throws ActionException {

        LocalService<T> service = actionInvocation.getAction().getService();

        List<Object> values = new ArrayList<>();
        int i = 0;
        for (ActionArgument<LocalService<T>> argument : actionInvocation.getAction().getInputArguments()) {

            Class<?> methodParameterType = method.getParameterTypes()[i];

            ActionArgumentValue<LocalService<T>> inputValue = actionInvocation.getInput(argument);

            // If it's a primitive argument, we need a value
            if (methodParameterType.isPrimitive() && (inputValue == null || inputValue.toString().isEmpty()))
                throw new ActionException(
                        ErrorCode.ARGUMENT_VALUE_INVALID,
                        "Primitive action method argument '" + argument.getName() + "' requires input value, can't be null or empty string"
                );

            // It's not primitive, and we have no value, that's fine too
            if (inputValue == null) {
                values.add(i++, null);
                continue;
            }

            // If it's not null, maybe it was a string-convertible type, if so, try to instantiate it
            String inputCallValueString = inputValue.toString();
            // Empty string means null and we can't instantiate Enums!
            if (!inputCallValueString.isEmpty() && service.isStringConvertibleType(methodParameterType) && !methodParameterType.isEnum()) {
                try {
                    Constructor<?> ctor = methodParameterType.getConstructor(String.class);
					if (log.isTraceEnabled()) {
						log.trace("Creating new input argument value instance with String.class constructor of type: " + methodParameterType);
					}
					Object o = ctor.newInstance(inputCallValueString);
                    values.add(i++, o);
                } catch (Exception ex) {
                    if (log.isWarnEnabled()) {
                        log.warn("Error preparing action method call: " + method);
                        log.warn("Can't convert input argument string to desired type of '" + argument.getName() + "': ", ex);
                    }
                    throw new ActionException(
                            ErrorCode.ARGUMENT_VALUE_INVALID, "Can't convert input argument string to desired type of '" + argument.getName() + "': " + ex
                    );
                }
            } else {
                // Or if it wasn't, just use the value without any conversion
                values.add(i++, inputValue.getValue());
            }
        }

        if (method.getParameterTypes().length > 0
            && RemoteClientInfo.class.isAssignableFrom(method.getParameterTypes()[method.getParameterTypes().length-1])) {
            if (actionInvocation instanceof RemoteActionInvocation &&
                ((RemoteActionInvocation<?>)actionInvocation).getRemoteClientInfo() != null) {
				if (log.isTraceEnabled()) {
					log.trace("Providing remote client info as last action method input argument: " + method);
				}
				values.add(i, ((RemoteActionInvocation<?>)actionInvocation).getRemoteClientInfo());
            } else {
                // Local call, no client info available
                values.add(i, null);
            }
        }

        return values.toArray();
    }

}
