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

import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

import fr.distrimind.oss.upnp.common.model.types.Datatype;
import fr.distrimind.oss.upnp.common.model.types.InvalidValueException;

/**
 * Encapsulates a variable or argument value, validates and transforms it from/to a string representaion.
 *
 * @author Christian Bauer
 */
public class VariableValue {

    final private static DMLogger log = Log.getLogger(VariableValue.class);

    final private Datatype<?> datatype;
    final private Object value;

    /**
     * Creates and validates a variable value.
     * <p>
     * If the given value is a <code>String</code>, it will be converted
     * with {@link Datatype#valueOf(String)}. Any
     * other value will be checked, whether it matches the datatype and if its
     * string representation is valid in XML documents (unicode character test).
   
     * <p>
     * Note that for performance reasons, validation of a non-string value
     * argument is skipped if executed on an Android runtime!
   
     *
     * @param datatype The type of the variable.
     * @param value The value of the variable.
     * @throws InvalidValueException If the value is invalid for the given datatype, or if
     *         its string representation is invalid in XML.
     */
    public VariableValue(Datatype<?> datatype, Object value) throws InvalidValueException {
        this.datatype = datatype;
        this.value = value instanceof String ? datatype.valueOf((String) value) : value;

		if (ModelUtil.ANDROID_RUNTIME) return; // Skipping validation on Android

        // We can skip this validation because we can catch invalid values
        // of any remote service (action invocation, event value) before, they are
        // strings. The datatype's valueOf() will take care of that. The validations
        // are really only used when a developer prepares input arguments for an action
        // invocation or when a local service returns a wrong value.

        // In the first case the developer will get an exception when executing the
        // action, if his action input argument value was of the wrong type. Or,
        // an XML processing error will occur as soon as the SOAP message is handled,
        // if the value contained invalid characters.

        // The second case indicates a bug in the local service, either metadata (state
        // variable type) or implementation (action method return value). This will
        // most likely be caught by the metadata/annotation binder when the service is
        // created.

        if (!getDatatype().isValidObject(getValue()))
            throw new InvalidValueException("Invalid value for " + getDatatype() +": " + getValue());
        
        logInvalidXML(toString());
    }

    public Datatype<?> getDatatype() {
        return datatype;
    }

    public Object getValue() {
        return value;
    }

    protected void logInvalidXML(String s) {
        // Just display warnings. PS3 Media server sends null char in DIDL-Lite
        // http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char
        int cp;
        int i = 0;
        while (i < s.length()) {
            cp = s.codePointAt(i);
            if (!(cp == 0x9 || cp == 0xA || cp == 0xD ||
                    (cp >= 0x20 && cp <= 0xD7FF) ||
                    (cp >= 0xE000 && cp <= 0xFFFD) ||
                    (cp >= 0x10000 && cp <= 0x10FFFF))) {
                if (log.isWarnEnabled()) log.warn("Found invalid XML char code: " + cp);
            }
            i += Character.charCount(cp);
        }
    }

    @Override
    public String toString() {
        return getDatatype().getObjectString(getValue());
    }

}
