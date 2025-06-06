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
package fr.distrimind.oss.upnp.common.support.model.dlna;

import fr.distrimind.oss.upnp.common.util.Exceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Transforms known and standardized DLNA attributes from/to string representation.
 * <p>
 * The {@link #newInstance(Type, String, String)}
 * method attempts to instantiate the best header subtype for a given header (name) and string value.
 * </p>
 *
 * @author Christian Bauer
 * @author Mario Franco
 */
public abstract class DLNAAttribute<T> {

    final private static DMLogger log = Log.getLogger(DLNAAttribute.class);

    /**
     * Maps a standardized DLNA attribute to potential attribute subtypes.
     */
    public enum Type {

        /**
         * Order is important for DLNAProtocolInfo
         */ 
        DLNA_ORG_PN("DLNA.ORG_PN", DLNAProfileAttribute.class),
        DLNA_ORG_OP("DLNA.ORG_OP", DLNAOperationsAttribute.class),
        DLNA_ORG_PS("DLNA.ORG_PS", DLNAPlaySpeedAttribute.class),
        DLNA_ORG_CI("DLNA.ORG_CI", DLNAConversionIndicatorAttribute.class),
        DLNA_ORG_FLAGS("DLNA.ORG_FLAGS", DLNAFlagsAttribute.class);
    
        private static final Map<String, Type> byName = new HashMap<>() {
            private static final long serialVersionUID = 1L;
			{
				for (Type t : Type.values()) {
					put(t.getAttributeName().toUpperCase(Locale.ROOT), t);
				}
			}
		};

        private final String attributeName;
        private final List<Class<? extends DLNAAttribute<?>>> attributeTypes;

        @SafeVarargs
		Type(String attributeName, Class<? extends DLNAAttribute<?>>... attributeClass) {
            this.attributeName = attributeName;
            this.attributeTypes = List.of(attributeClass);
        }

        public String getAttributeName() {
            return attributeName;
        }

        public List<Class<? extends DLNAAttribute<?>>> getAttributeTypes() {
            return attributeTypes;
        }

        public static Type valueOfAttributeName(String attributeName) {
            if (attributeName == null) {
                return null;
            }
            return byName.get(attributeName.toUpperCase(Locale.ROOT));
        }
    }

    private T value;

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    /**
     * @param s  This attribute's value as a string representation.
     * @param cf This attribute's mime type as a string representation, optional.
     * @throws InvalidDLNAProtocolAttributeException
     *          If the value is invalid for this DLNA attribute.
     */
    public abstract void setString(String s, String cf) throws InvalidDLNAProtocolAttributeException;

    /**
     * @return A string representing this attribute's value.
     */
    public abstract String getString();

    /**
     * Create a new instance of a {@link DLNAAttribute} subtype that matches the given type and value.
     * <p>
     * This method iterates through all potential attribute subtype classes as declared in {@link Type}.
     * It creates a new instance of the subtype class and calls its {@link #setString(String, String)} method.
     * If no {@link InvalidDLNAProtocolAttributeException} is thrown,
     * the subtype instance is returned.
   
     *
     * @param type           The type of the attribute.
     * @param attributeValue The value of the attribute.
     * @param contentFormat  The DLNA mime type of the attribute, optional.
     * @return The best matching attribute subtype instance, or <code>null</code> if no subtype can be found.
     */
    public static DLNAAttribute<?> newInstance(Type type, String attributeValue, String contentFormat) {

        DLNAAttribute<?> attr = null;
        for (int i = 0; i < type.getAttributeTypes().size() && attr == null; i++) {
            Class<? extends DLNAAttribute<?>> attributeClass = type.getAttributeTypes().get(i);
            try {
				if (log.isTraceEnabled()) {
					log.trace("Trying to parse DLNA '" + type + "' with class: " + attributeClass.getSimpleName());
				}
				attr = attributeClass.getConstructor().newInstance();
                if (attributeValue != null) {
                    attr.setString(attributeValue, contentFormat);
                }
            } catch (InvalidDLNAProtocolAttributeException ex) {
				if (log.isTraceEnabled()) {
					log.trace("Invalid DLNA attribute value for tested type: " + attributeClass.getSimpleName() + " - ", ex.getMessage());
				}
				attr = null;
            } catch (Exception ex) {
                if (log.isErrorEnabled()) {
                    log.error("Error instantiating DLNA attribute of type '" + type + "' with value: " + attributeValue);
                    log.error("Exception root cause: ", Exceptions.unwrap(ex));
                }
            }
        }
        return attr;
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") '" + getValue() + "'";
    }
}
