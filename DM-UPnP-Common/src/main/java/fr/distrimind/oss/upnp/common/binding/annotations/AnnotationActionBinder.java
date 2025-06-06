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

package fr.distrimind.oss.upnp.common.binding.annotations;

import fr.distrimind.oss.upnp.common.binding.LocalServiceBindingException;
import fr.distrimind.oss.upnp.common.model.Constants;
import fr.distrimind.oss.upnp.common.model.ModelUtil;
import fr.distrimind.oss.upnp.common.model.action.ActionExecutor;
import fr.distrimind.oss.upnp.common.model.action.MethodActionExecutor;
import fr.distrimind.oss.upnp.common.model.meta.Action;
import fr.distrimind.oss.upnp.common.model.meta.ActionArgument;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;
import fr.distrimind.oss.upnp.common.model.meta.StateVariable;
import fr.distrimind.oss.upnp.common.model.profile.RemoteClientInfo;
import fr.distrimind.oss.upnp.common.model.state.GetterStateVariableAccessor;
import fr.distrimind.oss.upnp.common.model.state.StateVariableAccessor;
import fr.distrimind.oss.upnp.common.model.types.Datatype;
import fr.distrimind.oss.upnp.common.util.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * @author Christian Bauer
 */
public class AnnotationActionBinder<T> {

    final private static DMLogger log = Log.getLogger(AnnotationActionBinder.class);

    protected UpnpAction annotation;
    protected Method method;
    protected Map<StateVariable<LocalService<T>>, StateVariableAccessor> stateVariables;
    protected Set<Class<?>> stringConvertibleTypes;

    public AnnotationActionBinder(Method method, Map<StateVariable<LocalService<T>>, StateVariableAccessor> stateVariables, Set<Class<?>> stringConvertibleTypes) {
        this.annotation = method.getAnnotation(UpnpAction.class);
        this.stateVariables = stateVariables;
        this.method = method;
        this.stringConvertibleTypes = stringConvertibleTypes;
    }

    public UpnpAction getAnnotation() {
        return annotation;
    }

    public Map<StateVariable<LocalService<T>>, StateVariableAccessor> getStateVariables() {
        return stateVariables;
    }

    public Method getMethod() {
        return method;
    }

    public Set<Class<?>> getStringConvertibleTypes() {
        return stringConvertibleTypes;
    }

    public Action<?> appendAction(Map<Action<LocalService<T>>, ActionExecutor> actions) throws LocalServiceBindingException {

        String name;
        if (!getAnnotation().name().isEmpty()) {
            name = getAnnotation().name();
        } else {
            name = AnnotationLocalServiceBinder.toUpnpActionName(getMethod().getName());
        }
        if (log.isDebugEnabled())
            log.debug("Creating action and executor: " + name);

        List<ActionArgument<LocalService<T>>> inputArguments = createInputArguments();
        Map<ActionArgument<LocalService<T>>, StateVariableAccessor> outputArguments = createOutputArguments();

        inputArguments.addAll(outputArguments.keySet());


        Action<LocalService<T>> action = new Action<>(name, inputArguments);
        ActionExecutor executor = createExecutor(outputArguments);

        actions.put(action, executor);
        return action;
    }

    protected ActionExecutor createExecutor(Map<ActionArgument<LocalService<T>>, StateVariableAccessor> outputArguments) {
        // TODO: Invent an annotation for this configuration
        return new MethodActionExecutor(outputArguments, getMethod());
    }

    protected List<ActionArgument<LocalService<T>>> createInputArguments() throws LocalServiceBindingException {

        List<ActionArgument<LocalService<T>>> list = new ArrayList<>();

        // Input arguments are always method parameters
        int annotatedParams = 0;
        Annotation[][] params = getMethod().getParameterAnnotations();
        for (int i = 0; i < params.length; i++) {
            Annotation[] param = params[i];
            for (Annotation paramAnnotation : param) {
                if (paramAnnotation instanceof UpnpInputArgument) {
                    UpnpInputArgument inputArgumentAnnotation = (UpnpInputArgument) paramAnnotation;
                    annotatedParams++;

                    String argumentName =
                            inputArgumentAnnotation.name();

                    StateVariable<LocalService<T>> stateVariable =
                            findRelatedStateVariable(
                                    inputArgumentAnnotation.stateVariable(),
                                    argumentName,
                                    getMethod().getName()
                            );

                    if (stateVariable == null) {
                        throw new LocalServiceBindingException(
                                "Could not detected related state variable of argument: " + argumentName
                        );
                    }

                    validateType(stateVariable, getMethod().getParameterTypes()[i]);

                    ActionArgument<LocalService<T>> inputArgument = new ActionArgument<>(
                            argumentName,
                            inputArgumentAnnotation.aliases(),
                            stateVariable.getName(),
                            ActionArgument.Direction.IN
                    );

                    list.add(inputArgument);
                }
            }
        }
        // A method can't have any parameters that are not annotated with @UpnpInputArgument - we wouldn't know what
        // value to pass when we invoke it later on... unless the last parameter is of type RemoteClientInfo
        if (annotatedParams < getMethod().getParameterTypes().length
            && !RemoteClientInfo.class.isAssignableFrom(method.getParameterTypes()[method.getParameterTypes().length-1])) {
            throw new LocalServiceBindingException("Method has parameters that are not input arguments: " + getMethod().getName());
        }

        return list;
    }

    protected Map<ActionArgument<LocalService<T>>, StateVariableAccessor> createOutputArguments() throws LocalServiceBindingException {

        Map<ActionArgument<LocalService<T>>, StateVariableAccessor> map = new LinkedHashMap<>(); // !!! Insertion order!

        UpnpAction actionAnnotation = getMethod().getAnnotation(UpnpAction.class);
        if (actionAnnotation.out().length == 0) return map;

        boolean hasMultipleOutputArguments = actionAnnotation.out().length > 1;

        for (UpnpOutputArgument outputArgumentAnnotation : actionAnnotation.out()) {

            String argumentName = outputArgumentAnnotation.name();

            StateVariable<LocalService<T>> stateVariable = findRelatedStateVariable(
                    outputArgumentAnnotation.stateVariable(),
                    argumentName,
                    getMethod().getName()
            );

            // Might-just-work attempt, try the name of the getter
            if (stateVariable == null && !outputArgumentAnnotation.getterName().isEmpty()) {
                stateVariable = findRelatedStateVariable(null, null, outputArgumentAnnotation.getterName());
            }

            if (stateVariable == null) {
                throw new LocalServiceBindingException(
                        "Related state variable not found for output argument: " + argumentName
                );
            }

            StateVariableAccessor accessor = findOutputArgumentAccessor(
                    stateVariable,
                    outputArgumentAnnotation.getterName(),
                    hasMultipleOutputArguments
            );
            if (log.isTraceEnabled())
                log.trace("Found related state variable for output argument '" + argumentName + "': " + stateVariable);

            ActionArgument<LocalService<T>> outputArgument = new ActionArgument<>(
                    argumentName,
                    stateVariable.getName(),
                    ActionArgument.Direction.OUT,
                    !hasMultipleOutputArguments
            );

            map.put(outputArgument, accessor);
        }

        return map;
    }

    protected StateVariableAccessor findOutputArgumentAccessor(StateVariable<LocalService<T>> stateVariable, String getterName, boolean multipleArguments)
            throws LocalServiceBindingException {

        boolean isVoid = getMethod().getReturnType().equals(Void.TYPE);

        if (isVoid) {

            if (getterName != null && !getterName.isEmpty()) {
                if (log.isTraceEnabled())
                    log.trace("Action method is void, will use getter method named: " + getterName);

                // Use the same class as the action method
				Method getter = null;
				try {
					getter = Reflections.getMethod(getMethod().getDeclaringClass(), getterName);
				} catch (NoSuchMethodException ignored) {

				}

				validateType(stateVariable, getter.getReturnType());

                return new GetterStateVariableAccessor(getter);

            } else {
                if (log.isTraceEnabled())
                    log.trace("Action method is void, trying to find existing accessor of related: " + stateVariable);
                return getStateVariables().get(stateVariable);
            }


        } else if (getterName != null && !getterName.isEmpty()) {
            if (log.isTraceEnabled())
                log.trace("Action method is not void, will use getter method on returned instance: " + getterName);

            // Use the returned class
			Method getter = null;
			try {
				getter = Reflections.getMethod(getMethod().getReturnType(), getterName);
			} catch (NoSuchMethodException ignored) {

			}

			validateType(stateVariable, getter.getReturnType());

            return new GetterStateVariableAccessor(getter);

        } else if (!multipleArguments) {
            if (log.isTraceEnabled())
                log.trace("Action method is not void, will use the returned instance: " + getMethod().getReturnType());
            validateType(stateVariable, getMethod().getReturnType());
        }

        return null;
    }

    protected StateVariable<LocalService<T>> findRelatedStateVariable(String declaredName, String argumentName, String methodName)
            throws LocalServiceBindingException {

        StateVariable<LocalService<T>> relatedStateVariable = null;

        if (declaredName != null && !declaredName.isEmpty()) {
            relatedStateVariable = getStateVariable(declaredName);
        }

        if (relatedStateVariable == null && argumentName != null && !argumentName.isEmpty()) {
            String actualName = AnnotationLocalServiceBinder.toUpnpStateVariableName(argumentName);
            if (log.isTraceEnabled())
                log.trace("Finding related state variable with argument name (converted to UPnP name): " + actualName);
            relatedStateVariable = getStateVariable(argumentName);
        }

        if (relatedStateVariable == null && argumentName != null && !argumentName.isEmpty()) {
            // Try with A_ARG_TYPE prefix
            String actualName = AnnotationLocalServiceBinder.toUpnpStateVariableName(argumentName);
            actualName = Constants.ARG_TYPE_PREFIX + actualName;
            if (log.isTraceEnabled())
                log.trace("Finding related state variable with prefixed argument name (converted to UPnP name): " + actualName);
            relatedStateVariable = getStateVariable(actualName);
        }

        if (relatedStateVariable == null && methodName != null && !methodName.isEmpty()) {
            // TODO: Well, this is often a nice shortcut but sometimes might have false positives
            String methodPropertyName = Reflections.getMethodPropertyName(methodName);
            if (methodPropertyName != null) {
                if (log.isTraceEnabled())
                    log.trace("Finding related state variable with method property name: " + methodPropertyName);
                relatedStateVariable =
                        getStateVariable(
                                AnnotationLocalServiceBinder.toUpnpStateVariableName(methodPropertyName)
                        );
            }
        }

        return relatedStateVariable;
    }

    protected void validateType(StateVariable<LocalService<T>> stateVariable, Class<?> type) throws LocalServiceBindingException {

        // Validate datatype as good as we can
        // (for enums and other convertible types, the state variable type should be STRING)

        Datatype.Default expectedDefaultMapping =
                ModelUtil.isStringConvertibleType(getStringConvertibleTypes(), type)
                        ? Datatype.Default.STRING
                        : Datatype.Default.getByJavaType(type);
        if (log.isTraceEnabled())
            log.trace("Expecting '" + stateVariable + "' to match default mapping: " + expectedDefaultMapping);

        if (expectedDefaultMapping != null &&
                !stateVariable.getTypeDetails().getDatatype().isHandlingJavaType(expectedDefaultMapping.getJavaType())) {

            // TODO: Consider custom types?!
            throw new LocalServiceBindingException(
                    "State variable '" + stateVariable + "' datatype can't handle action " +
                            "argument's Java type (change one): " + expectedDefaultMapping.getJavaType()
            );

        } else if (expectedDefaultMapping == null && stateVariable.getTypeDetails().getDatatype().getBuiltin() != null) {
            throw new LocalServiceBindingException(
                    "State variable '" + stateVariable  + "' should be custom datatype " +
                            "(action argument type is unknown Java type): " + type.getSimpleName()
            );
        }

        log.trace("State variable matches required argument datatype (or can't be validated because it is custom)");
    }

    protected StateVariable<LocalService<T>> getStateVariable(String name) {
        for (StateVariable<LocalService<T>> stateVariable : getStateVariables().keySet()) {
            if (stateVariable.getName().equals(name)) {
                return stateVariable;
            }
        }
        return null;
    }

}
