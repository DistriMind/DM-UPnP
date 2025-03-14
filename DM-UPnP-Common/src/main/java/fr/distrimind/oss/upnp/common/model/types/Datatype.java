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

package fr.distrimind.oss.upnp.common.model.types;

import fr.distrimind.oss.upnp.common.model.Constants;

import java.util.*;

/**
 * The type of state variable value, able to convert to/from string representation.
 *
 * @param <V> The Java type of the value handled by this datatype.
 * @author Christian Bauer
 */
public interface Datatype<V> {

    /**
     * Mapping from Java type to UPnP built-in type.
     * <p>
     * This map is used for service binding, when we have to figure out
     * the type of UPnP state variable by reflecting on a method or field of
     * a service class. From a known Java type we default to a UPnP built-in type.
     * This is just a list of default mappings between Java and UPnP types. There
     * might be more than this/more than one UPnP type that can handle a given
     * Java type.
   
     */
	enum Default {

        BOOLEAN(Boolean.class, Builtin.BOOLEAN),
        BOOLEAN_PRIMITIVE(Boolean.TYPE, Builtin.BOOLEAN),
        SHORT(Short.class, Builtin.I2_SHORT),
        SHORT_PRIMITIVE(Short.TYPE, Builtin.I2_SHORT),
        INTEGER(Integer.class, Builtin.I4),
        INTEGER_PRIMITIVE(Integer.TYPE, Builtin.I4),
        UNSIGNED_INTEGER_ONE_BYTE(UnsignedIntegerOneByte.class, Builtin.UI1),
        UNSIGNED_INTEGER_TWO_BYTES(UnsignedIntegerTwoBytes.class, Builtin.UI2),
        UNSIGNED_INTEGER_FOUR_BYTES(UnsignedIntegerFourBytes.class, Builtin.UI4),
        FLOAT(Float.class, Builtin.R4),
        FLOAT_PRIMITIVE(Float.TYPE, Builtin.R4),
        DOUBLE(Double.class, Builtin.FLOAT),
        DOUBLE_PRIMTIIVE(Double.TYPE, Builtin.FLOAT),
        CHAR(Character.class, Builtin.CHAR),
        CHAR_PRIMITIVE(Character.TYPE, Builtin.CHAR),
        STRING(String.class, Builtin.STRING),
        CALENDAR(Calendar.class, Builtin.DATETIME),
        BYTES(byte[].class, Builtin.BIN_BASE64),
        URI(java.net.URI.class, Builtin.URI);

        private final Class<?> javaType;
        private final Builtin builtinType;

        Default(Class<?> javaType, Builtin builtinType) {
            this.javaType = javaType;
            this.builtinType = builtinType;
        }

        public Class<?> getJavaType() {
            return javaType;
        }

        public Builtin getBuiltinType() {
            return builtinType;
        }

        public static Default getByJavaType(Class<?> javaType) {
            for (Default d : Default.values()) {
                if (d.getJavaType().equals(javaType)) {
                    return d;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return getJavaType() + " => " + getBuiltinType();
        }
    }

    /**
     * Mapping from UPnP built-in standardized type to actual subtype of {@link Datatype}.
     */
    enum Builtin {

        UI1("ui1", new UnsignedIntegerOneByteDatatype()),
        UI2("ui2", new UnsignedIntegerTwoBytesDatatype()),
        UI4("ui4", new UnsignedIntegerFourBytesDatatype()),
        I1("i1", new IntegerDatatype(1)),
        I2("i2", new IntegerDatatype(2)),
        I2_SHORT("i2", new ShortDatatype()),
        I4("i4", new IntegerDatatype(4)),
        INT("int", new IntegerDatatype(4)),
        R4("r4", new FloatDatatype()),
        R8("r8", new DoubleDatatype()),
        NUMBER("number", new DoubleDatatype()),
        FIXED144("fixed.14.4", new DoubleDatatype()),
        FLOAT("float", new DoubleDatatype()), // TODO: Is that Double or Float?
        CHAR("char", new CharacterDatatype()),
        STRING("string", new StringDatatype()),
        DATE("date", new DateTimeDatatype(
                List.of(Constants.DATE_FORMAT),
                Constants.DATE_FORMAT
        )),
        DATETIME("dateTime", new DateTimeDatatype(
                List.of(Constants.DATE_FORMAT, "yyyy-MM-dd'T'HH:mm:ss"),
                "yyyy-MM-dd'T'HH:mm:ss"
        )),
        DATETIME_TZ("dateTime.tz", new DateTimeDatatype(
                List.of(Constants.DATE_FORMAT, "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ssZ"),
                "yyyy-MM-dd'T'HH:mm:ssZ"
        )),
        TIME("time", new DateTimeDatatype(
                List.of("HH:mm:ss"),
                "HH:mm:ss"
        )),
        TIME_TZ("time.tz", new DateTimeDatatype(
                List.of("HH:mm:ssZ", "HH:mm:ss"),
                "HH:mm:ssZ"
        )),
        BOOLEAN("boolean", new BooleanDatatype()),
        BIN_BASE64("bin.base64", new Base64Datatype()),
        BIN_HEX("bin.hex", new BinHexDatatype()),
        URI("uri", new URIDatatype()),
        UUID("uuid", new StringDatatype());

        private final static Map<String, Builtin> byName = new HashMap<>() {
            private static final long serialVersionUID = 1L;
            {
			for (Builtin b : Builtin.values()) {
				// Lowercase descriptor name!
				if (containsKey(b.getDescriptorName().toLowerCase(Locale.ROOT)))
					continue; // Ignore double-declarations, take first one only
				put(b.getDescriptorName().toLowerCase(Locale.ROOT), b);
			}
		}};

        private final String descriptorName;
        private final Datatype<?> type;

        <VT> Builtin(String descriptorName, AbstractDatatype<VT> datatype) {
            datatype.setBuiltin(this); // Protected, we actually want this to be immutable
            this.descriptorName = descriptorName;
            this.type = datatype;
        }

        public String getDescriptorName() {
            return descriptorName;
        }

        public Datatype<?> getDatatype() {
            return type;
        }

        public static Builtin getByDescriptorName(String descriptorName) {
            // The UPnP spec clearly says "must be one of these values", so I'm assuming
            // they are case-sensitive. But we want to work with broken devices, which of
            // course produce mixed upper/lowercase values.
            if (descriptorName == null) return null;
            return byName.get(descriptorName.toLowerCase(Locale.ROOT));
        }

        public static boolean isNumeric(Builtin builtin) {
            return builtin != null &&
                    (builtin.equals(UI1) ||
                            builtin.equals(UI2) ||
                            builtin.equals(UI4) ||
                            builtin.equals(I1) ||
                            builtin.equals(I2) ||
                            builtin.equals(I4) ||
                            builtin.equals(INT));
        }
    }

    /**
     * @return <code>true</code> if this datatype can handle values of the given Java type.
     */
    boolean isHandlingJavaType(Class<?> type);

    /**
     * @return The built-in UPnP standardized type this datatype is mapped to or
     *         <code>null</code> if this is a custom datatype.
     */
    Builtin getBuiltin();

    /**
     * @param value The value to validate or <code>null</code>.
     * @return Returns <code>true</code> if the value was <code>null</code>, validation result otherwise.
     */
    boolean isValid(V value);
    @SuppressWarnings("unchecked")
	default boolean isValidObject(Object value)
    {
        return isValid((V)value);
    }

    /**
     * Transforms a value supported by this datatype into a string representation.
     * <p>
     * This method calls {@link #isValid(Object)} before converting the value, it throws
     * an exception if validation fails.
   
     *
     * @param value The value to transform.
     * @return The transformed value as a string, or an empty string when the value is null, never returns <code>null</code>.
     * @throws InvalidValueException if problem occurs
     */
    String getString(V value) throws InvalidValueException;
    @SuppressWarnings("unchecked")
	default String getObjectString(Object value) throws InvalidValueException
    {
        return getString((V)value);
    }

    /**
     * Transforms a string representation into a value of the supported type.
     *
     * @param s The string representation of a value.
     * @return The converted value or <code>null</code> if the string was <code>null</code> or empty.
     * @throws InvalidValueException If the string couldn't be parsed.
     */
    V valueOf(String s) throws InvalidValueException;

    /**
     * @return Metadata about this datatype, a nice string for display that describes
     *         this datatype (e.g. concrete class name).
     */
    String getDisplayString();


}
