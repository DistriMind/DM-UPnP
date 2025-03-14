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
import fr.distrimind.oss.upnp.common.binding.LocalServiceBinder;
import fr.distrimind.oss.upnp.common.model.ValidationError;
import fr.distrimind.oss.upnp.common.model.ValidationException;
import fr.distrimind.oss.upnp.common.model.action.ActionExecutor;
import fr.distrimind.oss.upnp.common.model.action.QueryStateVariableExecutor;
import fr.distrimind.oss.upnp.common.model.meta.Action;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;
import fr.distrimind.oss.upnp.common.model.meta.QueryStateVariableAction;
import fr.distrimind.oss.upnp.common.model.meta.StateVariable;
import fr.distrimind.oss.upnp.common.model.state.FieldStateVariableAccessor;
import fr.distrimind.oss.upnp.common.model.state.GetterStateVariableAccessor;
import fr.distrimind.oss.upnp.common.model.state.StateVariableAccessor;
import fr.distrimind.oss.upnp.common.model.types.ServiceId;
import fr.distrimind.oss.upnp.common.model.types.ServiceType;
import fr.distrimind.oss.upnp.common.model.types.UDAServiceId;
import fr.distrimind.oss.upnp.common.model.types.UDAServiceType;
import fr.distrimind.oss.upnp.common.model.types.csv.CSV;
import fr.distrimind.oss.upnp.common.util.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.util.*;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Reads {@link LocalService} metadata from annotations.
 *
 * @author Christian Bauer
 */
public class AnnotationLocalServiceBinder implements LocalServiceBinder {

    final private static DMLogger log = Log.getLogger(AnnotationLocalServiceBinder.class);

    @Override
    public <T> LocalService<T> read(Class<T> clazz) throws LocalServiceBindingException {
        if (log.isDebugEnabled())
            log.debug("Reading and binding annotations of service implementation class: " + clazz);

        // Read the service ID and service type from the annotation
        if (clazz.isAnnotationPresent(UpnpService.class)) {

            UpnpService annotation = clazz.getAnnotation(UpnpService.class);
            UpnpServiceId idAnnotation = annotation.serviceId();
            UpnpServiceType typeAnnotation = annotation.serviceType();

            ServiceId serviceId = UDAServiceId.DEFAULT_NAMESPACE.equals(idAnnotation.namespace())
                    ? new UDAServiceId(idAnnotation.value())
                    : new ServiceId(idAnnotation.namespace(), idAnnotation.value());

            ServiceType serviceType = UDAServiceType.DEFAULT_NAMESPACE.equals(typeAnnotation.namespace())
                    ? new UDAServiceType(typeAnnotation.value(), typeAnnotation.version())
                    : new ServiceType(typeAnnotation.namespace(), typeAnnotation.value(), typeAnnotation.version());

            boolean supportsQueryStateVariables = annotation.supportsQueryStateVariables();

            Set<Class<?>> stringConvertibleTypes = readStringConvertibleTypes(List.of(annotation.stringConvertibleTypes()));

            return read(clazz, serviceId, serviceType, supportsQueryStateVariables, stringConvertibleTypes);
        } else {
            throw new LocalServiceBindingException("Given class is not an @UpnpService");
        }
    }
    public <T> LocalService<T> read(Class<T> clazz, ServiceId id, ServiceType type,
                                    boolean supportsQueryStateVariables, List<Class<?>> stringConvertibleTypes)
    {
		Set<Class<?>> set = new HashSet<>(stringConvertibleTypes);
        return read(clazz, id, type, supportsQueryStateVariables, set);
    }
    @Override
    public <T> LocalService<T> read(Class<T> clazz, ServiceId id, ServiceType type,
                                   boolean supportsQueryStateVariables, Set<Class<?>> stringConvertibleTypes)
            throws LocalServiceBindingException {

        Map<StateVariable<LocalService<T>>, StateVariableAccessor> stateVariables = readStateVariables(clazz, stringConvertibleTypes);
        Map<Action<LocalService<T>>, ActionExecutor> actions = readActions(clazz, stateVariables, stringConvertibleTypes);

        // Special treatment of the state variable querying action
        if (supportsQueryStateVariables) {
            actions.put(new QueryStateVariableAction<>(), new QueryStateVariableExecutor());
        }

        try {
            return new LocalService<>(type, id, actions, stateVariables, stringConvertibleTypes, supportsQueryStateVariables);

        } catch (ValidationException ex) {
            if (log.isErrorEnabled())
                log.error("Could not validate device model: ", ex);
            for (ValidationError validationError : ex.getErrors()) {
                if (log.isErrorEnabled())
                    log.error(validationError.toString());
            }
            throw new LocalServiceBindingException("Validation of model failed, check the log");
        }
    }

    protected Set<Class<?>> readStringConvertibleTypes(Collection<Class<?>> declaredTypes) throws LocalServiceBindingException {

        for (Class<?> stringConvertibleType : declaredTypes) {
            if (!Modifier.isPublic(stringConvertibleType.getModifiers())) {
                throw new LocalServiceBindingException(
                        "Declared string-convertible type must be public: " + stringConvertibleType
                );
            }
            try {
                stringConvertibleType.getConstructor(String.class);
            } catch (NoSuchMethodException ex) {
                throw new LocalServiceBindingException(
                        "Declared string-convertible type needs a public single-argument String constructor: " + stringConvertibleType
                );
            }
        }
        Set<Class<?>> stringConvertibleTypes = new HashSet<>(declaredTypes);

        // Some defaults
        stringConvertibleTypes.add(URI.class);
        stringConvertibleTypes.add(URL.class);
        stringConvertibleTypes.add(CSV.class);

        return stringConvertibleTypes;
    }

    protected <T> Map<StateVariable<LocalService<T>>, StateVariableAccessor> readStateVariables(Class<?> clazz, Set<Class<?>> stringConvertibleTypes)
            throws LocalServiceBindingException {

        Map<StateVariable<LocalService<T>>, StateVariableAccessor> map = new HashMap<>();

        // State variables declared on the class
        if (clazz.isAnnotationPresent(UpnpStateVariables.class)) {
            UpnpStateVariables variables = clazz.getAnnotation(UpnpStateVariables.class);
            for (UpnpStateVariable v : variables.value()) {

                if (v.name().isEmpty())
                    throw new LocalServiceBindingException("Class-level @UpnpStateVariable name attribute value required");

                String javaPropertyName = toJavaStateVariableName(v.name());

                Method getter = Reflections.getGetterMethod(clazz, javaPropertyName);
				Field field = null;
				try {
					field = Reflections.getField(clazz, javaPropertyName);
				} catch (NoSuchFieldException ignored) {

				}

				StateVariableAccessor accessor = null;
                if (getter != null && field != null) {
                    accessor = variables.preferFields() ?
                            new FieldStateVariableAccessor(field)
                            : new GetterStateVariableAccessor(getter);
                } else if (field != null) {
                    accessor = new FieldStateVariableAccessor(field);
                } else if (getter != null) {
                    accessor = new GetterStateVariableAccessor(getter);
                } else {
                    if (log.isTraceEnabled())
                        log.trace("No field or getter found for state variable, skipping accessor: " + v.name());
                }

                @SuppressWarnings("unchecked") StateVariable<LocalService<T>> stateVar =(StateVariable<LocalService<T>>)
                        new AnnotationStateVariableBinder(v, v.name(), accessor, stringConvertibleTypes)
                                .createStateVariable();

                map.put(stateVar, accessor);
            }
        }

        // State variables declared on fields
        for (Field field : Reflections.getFields(clazz, UpnpStateVariable.class)) {

            UpnpStateVariable svAnnotation = field.getAnnotation(UpnpStateVariable.class);

            StateVariableAccessor accessor = new FieldStateVariableAccessor(field);

            @SuppressWarnings("unchecked") StateVariable<LocalService<T>> stateVar = (StateVariable<LocalService<T>>)new AnnotationStateVariableBinder(
                    svAnnotation,
					svAnnotation.name().isEmpty()
                            ? toUpnpStateVariableName(field.getName())
                            : svAnnotation.name(),
                    accessor,
                    stringConvertibleTypes
            ).createStateVariable();

            map.put(stateVar, accessor);
        }

        // State variables declared on getters
        for (Method getter : Reflections.getMethods(clazz, UpnpStateVariable.class)) {

            String propertyName = Reflections.getMethodPropertyName(getter.getName());
            if (propertyName == null) {
                throw new LocalServiceBindingException(
                        "Annotated method is not a getter method (: " + getter
                );
            }

            if (getter.getParameterTypes().length > 0)
                throw new LocalServiceBindingException(
                        "Getter method defined as @UpnpStateVariable can not have parameters: " + getter
                );

            UpnpStateVariable svAnnotation = getter.getAnnotation(UpnpStateVariable.class);

            StateVariableAccessor accessor = new GetterStateVariableAccessor(getter);

            @SuppressWarnings("unchecked") StateVariable<LocalService<T>> stateVar = (StateVariable<LocalService<T>>)new AnnotationStateVariableBinder(
                    svAnnotation,
					svAnnotation.name().isEmpty()
                            ?
                            toUpnpStateVariableName(propertyName)
                            : svAnnotation.name(),
                    accessor,
                    stringConvertibleTypes
            ).createStateVariable();

            map.put(stateVar, accessor);
        }

        return map;
    }

    protected <T> Map<Action<LocalService<T>>, ActionExecutor> readActions(Class<?> clazz,
                                                            Map<StateVariable<LocalService<T>>, StateVariableAccessor> stateVariables,
                                                      Set<Class<?>> stringConvertibleTypes)
            throws LocalServiceBindingException {

        Map<Action<LocalService<T>>, ActionExecutor> map = new HashMap<>();

        for (Method method : Reflections.getMethods(clazz, UpnpAction.class)) {
            AnnotationActionBinder<T> actionBinder =
                    new AnnotationActionBinder<>(method, stateVariables, stringConvertibleTypes);
            Action<?> action = actionBinder.appendAction(map);
            if(isActionExcluded(action)) {
            	map.remove(action);
            }
        }

        return map;
    }

    /**
     * Override this method to exclude action/methods after they have been discovered.
     */
    protected  boolean isActionExcluded(Action<?> action) {
    	return false;
    }
    
    // TODO: I don't like the exceptions much, user has no idea what to do

    static String toUpnpStateVariableName(String javaName) {
        if (javaName.isEmpty()) {
            throw new IllegalArgumentException("Variable name must be at least 1 character long");
        }
        return javaName.substring(0, 1).toUpperCase(Locale.ROOT) + javaName.substring(1);
    }

    static String toJavaStateVariableName(String upnpName) {
        if (upnpName.isEmpty()) {
            throw new IllegalArgumentException("Variable name must be at least 1 character long");
        }
        return upnpName.substring(0, 1).toLowerCase(Locale.ROOT) + upnpName.substring(1);
    }


    static String toUpnpActionName(String javaName) {
        if (javaName.isEmpty()) {
            throw new IllegalArgumentException("Action name must be at least 1 character long");
        }
        return javaName.substring(0, 1).toUpperCase(Locale.ROOT) + javaName.substring(1);
    }

    static String toJavaActionName(String upnpName) {
        if (upnpName.isEmpty()) {
            throw new IllegalArgumentException("Variable name must be at least 1 character long");
        }
        return upnpName.substring(0, 1).toLowerCase(Locale.ROOT) + upnpName.substring(1);
    }

}
