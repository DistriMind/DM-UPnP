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

package fr.distrimind.oss.upnp.binding.annotations;

import fr.distrimind.oss.upnp.binding.AllowedValueProvider;
import fr.distrimind.oss.upnp.binding.AllowedValueRangeProvider;
import fr.distrimind.oss.upnp.binding.LocalServiceBindingException;
import fr.distrimind.oss.upnp.model.ModelUtil;
import fr.distrimind.oss.upnp.model.meta.StateVariable;
import fr.distrimind.oss.upnp.model.meta.StateVariableAllowedValueRange;
import fr.distrimind.oss.upnp.model.meta.StateVariableEventDetails;
import fr.distrimind.oss.upnp.model.meta.StateVariableTypeDetails;
import fr.distrimind.oss.upnp.model.state.StateVariableAccessor;
import fr.distrimind.oss.upnp.model.types.Datatype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.Log;

/**
 * @author Christian Bauer
 */
public class AnnotationStateVariableBinder {

    final private static DMLogger log = Log.getLogger(AnnotationStateVariableBinder.class);

    protected UpnpStateVariable annotation;
    protected String name;
    protected StateVariableAccessor accessor;
    protected Set<Class<?>> stringConvertibleTypes;

    public AnnotationStateVariableBinder(UpnpStateVariable annotation, String name,
                                         StateVariableAccessor accessor, Set<Class<?>> stringConvertibleTypes) {
        this.annotation = annotation;
        this.name = name;
        this.accessor = accessor;
        this.stringConvertibleTypes = stringConvertibleTypes;
    }

    public UpnpStateVariable getAnnotation() {
        return annotation;
    }

    public String getName() {
        return name;
    }

    public StateVariableAccessor getAccessor() {
        return accessor;
    }

    public Set<Class<?>> getStringConvertibleTypes() {
        return stringConvertibleTypes;
    }

    protected StateVariable<?> createStateVariable() throws LocalServiceBindingException {
        if (log.isDebugEnabled())
            log.debug("Creating state variable '" + getName() + "' with accessor: " + getAccessor());

        // Datatype
        Datatype<?> datatype = createDatatype();

        // Default value
        String defaultValue = createDefaultValue(datatype);

        // Allowed values
        Collection<String> allowedValues = null;
        if (Datatype.Builtin.STRING.equals(datatype.getBuiltin())) {

            if (getAnnotation().allowedValueProvider() != void.class) {
                allowedValues = getAllowedValuesFromProvider();
            } else if (getAnnotation().allowedValues().length > 0) {
                allowedValues = List.of(getAnnotation().allowedValues());
            } else if (getAnnotation().allowedValuesEnum() != void.class) {
                allowedValues = getAllowedValues(getAnnotation().allowedValuesEnum());
            } else if (getAccessor() != null && getAccessor().getReturnType().isEnum()) {
                allowedValues = getAllowedValues(getAccessor().getReturnType());
            } else {
                if (log.isTraceEnabled())
                    log.trace("Not restricting allowed values (of string typed state var): " + getName());
            }

            if (allowedValues != null && defaultValue != null) {

                // Check if the default value is an allowed value
                boolean foundValue = false;
                for (String s : allowedValues) {
                    if (s.equals(defaultValue)) {
                        foundValue = true;
                        break;
                    }
                }
                if (!foundValue) {
                    throw new LocalServiceBindingException(
                            "Default value '" + defaultValue + "' is not in allowed values of: " + getName()
                    );
                }
            }
        }

        // Allowed value range
        StateVariableAllowedValueRange allowedValueRange = null;
        if (Datatype.Builtin.isNumeric(datatype.getBuiltin())) {

            if (getAnnotation().allowedValueRangeProvider() != void.class) {
                allowedValueRange = getAllowedRangeFromProvider();
            } else if (getAnnotation().allowedValueMinimum() > 0 || getAnnotation().allowedValueMaximum() > 0) {
                allowedValueRange = getAllowedValueRange(
                    getAnnotation().allowedValueMinimum(),
                    getAnnotation().allowedValueMaximum(),
                    getAnnotation().allowedValueStep()
                );
            } else {
                if (log.isTraceEnabled())
                    log.trace("Not restricting allowed value range (of numeric typed state var): " + getName());
            }

            // Check if the default value is an allowed value
            if (defaultValue != null && allowedValueRange != null) {

                long v;
                try {
                    v = Long.parseLong(defaultValue);
                } catch (Exception ex) {
                    throw new LocalServiceBindingException(
                        "Default value '" + defaultValue + "' is not numeric (for range checking) of: " + getName()
                    );
                }

                if (!allowedValueRange.isInRange(v)) {
                    throw new LocalServiceBindingException(
                        "Default value '" + defaultValue + "' is not in allowed range of: " + getName()
                    );
                }
            }
        }

        // Event details
        boolean sendEvents = getAnnotation().sendEvents();
        if (sendEvents && getAccessor() == null) {
            throw new LocalServiceBindingException(
                    "State variable sends events but has no accessor for field or getter: " + getName()
            );
        }

        int eventMaximumRateMillis = 0;
        int eventMinimumDelta = 0;
        if (sendEvents) {
            if (getAnnotation().eventMaximumRateMilliseconds() > 0) {
                if (log.isTraceEnabled())
                    log.trace("Moderating state variable events using maximum rate (milliseconds): " + getAnnotation().eventMaximumRateMilliseconds());
                eventMaximumRateMillis = getAnnotation().eventMaximumRateMilliseconds();
            }

            if (getAnnotation().eventMinimumDelta() > 0 && Datatype.Builtin.isNumeric(datatype.getBuiltin())) {
                // TODO: Doesn't consider floating point types!
                if (log.isTraceEnabled())
                    log.trace("Moderating state variable events using minimum delta: " + getAnnotation().eventMinimumDelta());
                eventMinimumDelta = getAnnotation().eventMinimumDelta();
            }
        }

        StateVariableTypeDetails typeDetails =
                new StateVariableTypeDetails(datatype, defaultValue, allowedValues, allowedValueRange);

        StateVariableEventDetails eventDetails =
                new StateVariableEventDetails(sendEvents, eventMaximumRateMillis, eventMinimumDelta);

        return new StateVariable<>(getName(), typeDetails, eventDetails);
    }

    protected Datatype<?> createDatatype() throws LocalServiceBindingException {

        String declaredDatatype = getAnnotation().datatype();

        if (declaredDatatype.isEmpty() && getAccessor() != null) {
            Class<?> returnType = getAccessor().getReturnType();
            if (log.isTraceEnabled())
                log.trace("Using accessor return type as state variable type: " + returnType);

            if (ModelUtil.isStringConvertibleType(getStringConvertibleTypes(), returnType)) {
                // Enums and toString() convertible types are always state variables with type STRING
                if (log.isTraceEnabled())
                    log.trace("Return type is string-convertible, using string datatype");
                return Datatype.Default.STRING.getBuiltinType().getDatatype();
            } else {
                Datatype.Default defaultDatatype = Datatype.Default.getByJavaType(returnType);
                if (defaultDatatype != null) {
                    if (log.isTraceEnabled())
                        log.trace("Return type has default UPnP datatype: " + defaultDatatype);
                    return defaultDatatype.getBuiltinType().getDatatype();
                }
            }
        }

        // We can also guess that if the allowed values are set then it's a string
        if (declaredDatatype.isEmpty() && (getAnnotation().allowedValues().length > 0 || getAnnotation().allowedValuesEnum() != void.class)) {
            log.trace("State variable has restricted allowed values, hence using 'string' datatype");
            declaredDatatype = "string";
        }

        // If we still don't have it, there is nothing more we can do
        if (declaredDatatype.isEmpty()) {
            throw new LocalServiceBindingException("Could not detect datatype of state variable: " + getName());
        }
        if (log.isTraceEnabled())
            log.trace("Trying to find built-in UPnP datatype for detected name: " + declaredDatatype);

        // Now try to find the actual UPnP datatype by mapping the Default to Builtin
        Datatype.Builtin builtin = Datatype.Builtin.getByDescriptorName(declaredDatatype);
        if (builtin != null) {
            if (log.isTraceEnabled())
                log.trace("Found built-in UPnP datatype: " + builtin);
            return builtin.getDatatype();
        } else {
            // TODO
            throw new LocalServiceBindingException("No built-in UPnP datatype found, using CustomDataType (TODO: NOT IMPLEMENTED)");
        }
    }

    protected String createDefaultValue(Datatype<?> datatype) throws LocalServiceBindingException {

        // Next, the default value of the state variable, first the declared one
        if (!getAnnotation().defaultValue().isEmpty()) {
            // The declared default value needs to match the datatype
            try {
                datatype.valueOf(getAnnotation().defaultValue());
                if (log.isTraceEnabled())
                    log.trace("Found state variable default value: " + getAnnotation().defaultValue());
                return getAnnotation().defaultValue();
            } catch (Exception ex) {
                throw new LocalServiceBindingException(
                        "Default value doesn't match datatype of state variable '" + getName() + "': " + ex.getMessage()
                );
            }
        }

        return null;
    }

    protected Collection<String> getAllowedValues(Class<?> enumType) throws LocalServiceBindingException {

        if (!enumType.isEnum()) {
            throw new LocalServiceBindingException("Allowed values type is not an Enum: " + enumType);
        }
        if (log.isTraceEnabled())
            log.trace("Restricting allowed values of state variable to Enum: " + getName());
        Collection<String> allowedValueStrings = new ArrayList<>(enumType.getEnumConstants().length);
        for (int i = 0; i < enumType.getEnumConstants().length; i++) {
            Object o = enumType.getEnumConstants()[i];
            if (o.toString().length() > 32) {
                throw new LocalServiceBindingException(
                        "Allowed value string (that is, Enum constant name) is longer than 32 characters: " + o
                );
            }
            if (log.isTraceEnabled())
                log.trace("Adding allowed value (converted to string): " + o);
            allowedValueStrings.add( o.toString());
        }

        return allowedValueStrings;
    }

    protected StateVariableAllowedValueRange getAllowedValueRange(long min,
                                                                  long max,
                                                                  long step) throws LocalServiceBindingException {
        if (max < min) {
            throw new LocalServiceBindingException(
                    "Allowed value range maximum is smaller than minimum: " + getName()
            );
        }

        return new StateVariableAllowedValueRange(min, max, step);
    }

    @SuppressWarnings("unchecked")
	protected Collection<String> getAllowedValuesFromProvider() throws LocalServiceBindingException {
        Class<?> provider = getAnnotation().allowedValueProvider();
        if (!AllowedValueProvider.class.isAssignableFrom(provider))
            throw new LocalServiceBindingException(
                "Allowed value provider is not of type " + AllowedValueProvider.class + ": " + getName()
            );
        try {
            return ((Class<? extends AllowedValueProvider>) provider).getConstructor().newInstance().getValues();
        } catch (Exception ex) {
            throw new LocalServiceBindingException(
                "Allowed value provider can't be instantiated: " + getName(), ex
            );
        }
    }

    @SuppressWarnings("unchecked")
	protected StateVariableAllowedValueRange getAllowedRangeFromProvider() throws  LocalServiceBindingException {
        Class<?> provider = getAnnotation().allowedValueRangeProvider();
        if (!AllowedValueRangeProvider.class.isAssignableFrom(provider))
            throw new LocalServiceBindingException(
                "Allowed value range provider is not of type " + AllowedValueRangeProvider.class + ": " + getName()
            );
        try {
            AllowedValueRangeProvider providerInstance =
                ((Class<? extends AllowedValueRangeProvider>) provider).getConstructor().newInstance();
            return getAllowedValueRange(
                providerInstance.getMinimum(),
                providerInstance.getMaximum(),
                providerInstance.getStep()
            );
        } catch (Exception ex) {
            throw new LocalServiceBindingException(
                "Allowed value range provider can't be instantiated: " + getName(), ex
            );
        }
    }


}
