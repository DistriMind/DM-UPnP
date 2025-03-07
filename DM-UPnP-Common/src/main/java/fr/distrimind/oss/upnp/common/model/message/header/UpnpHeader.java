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

package fr.distrimind.oss.upnp.common.model.message.header;


import fr.distrimind.oss.upnp.common.util.Exceptions;

import java.util.*;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Transforms known and standardized UPnP/HTTP headers from/to string representation.
 * <p>
 * The {@link #newInstance(UpnpHeader.Type, String)} method
 * attempts to instantiate the best header subtype for a given header (name) and string value.
 * </p>
 *
 * @author Christian Bauer
 */
public abstract class UpnpHeader<T> {

    final private static DMLogger log = Log.getLogger(UpnpHeader.class);

    /**
     * Maps a standardized UPnP header to potential header subtypes.
     */
    public enum Type {

        USN("USN",
                USNRootDeviceHeader.class,
                DeviceUSNHeader.class,
                ServiceUSNHeader.class,
                UDNHeader.class
        ),
        NT("NT",
                RootDeviceHeader.class,
                UDADeviceTypeHeader.class,
                UDAServiceTypeHeader.class,
                DeviceTypeHeader.class,
                ServiceTypeHeader.class,
                UDNHeader.class,
                NTEventHeader.class
        ),
        NTS("NTS", NTSHeader.class),
        HOST("HOST", HostHeader.class),
        SERVER("SERVER", ServerHeader.class),
        LOCATION("LOCATION", LocationHeader.class),
        MAX_AGE("CACHE-CONTROL", MaxAgeHeader.class),
        USER_AGENT("USER-AGENT", UserAgentHeader.class),
        CONTENT_TYPE("CONTENT-TYPE", ContentTypeHeader.class),
        MAN("MAN", MANHeader.class),
        MX("MX", MXHeader.class),
        ST("ST",
                STAllHeader.class,
                RootDeviceHeader.class,
                UDADeviceTypeHeader.class,
                UDAServiceTypeHeader.class,
                DeviceTypeHeader.class,
                ServiceTypeHeader.class,
                UDNHeader.class
        ),
        EXT("EXT", EXTHeader.class),
        SOAPACTION("SOAPACTION", SoapActionHeader.class),
        TIMEOUT("TIMEOUT", TimeoutHeader.class),
        CALLBACK("CALLBACK", CallbackHeader.class),
        SID("SID", SubscriptionIdHeader.class),
        SEQ("SEQ", EventSequenceHeader.class),
        RANGE("RANGE", RangeHeader.class),
        CONTENT_RANGE("CONTENT-RANGE", ContentRangeHeader.class),
        PRAGMA("PRAGMA", PragmaHeader.class),
        
        EXT_IFACE_MAC("X-CLING-IFACE-MAC", InterfaceMacHeader.class),
        EXT_AV_CLIENT_INFO("X-AV-CLIENT-INFO", AVClientInfoHeader.class);

        private static final Map<String, Type> byName = new HashMap<>();
        static
        {
            for (Type t : Type.values()) {
                byName.put(t.getHttpName(), t);
            }
        }

        private final String httpName;
        private final List<Class<? extends UpnpHeader<?>>> headerTypes;

        @SafeVarargs
        Type(String httpName, Class<? extends UpnpHeader<?>>... headerClass) {
            this.httpName = httpName;
            this.headerTypes = List.of(headerClass);
        }

        public String getHttpName() {
            return httpName;
        }

        public List<Class<? extends UpnpHeader<?>>> getHeaderTypes() {
            return headerTypes;
        }

        @SuppressWarnings("rawtypes")
		public boolean isValidHeaderType(Class<? extends UpnpHeader> clazz) {
            for (Class<? extends UpnpHeader<?>> permissibleType : getHeaderTypes()) {
                if (permissibleType.isAssignableFrom(clazz)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * @param httpName A case-insensitive HTTP header name.
         */
        public static Type getByHttpName(String httpName) {
            if (httpName == null) return null;
        	return byName.get(httpName.toUpperCase(Locale.ROOT));
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
     * @param s This header's value as a string representation.
     * @throws InvalidHeaderException If the value is invalid for this UPnP header.
     */
    public abstract void setString(String s) throws InvalidHeaderException;

    /**
     * @return A string representing this header's value.
     */
    public abstract String getString();

    /**
     * Create a new instance of a {@link UpnpHeader} subtype that matches the given type and value.
     * <p>
     * This method iterates through all potential header subtype classes as declared in {@link Type}.
     * It creates a new instance of the subtype class and calls its {@link #setString(String)} method.
     * If no {@link InvalidHeaderException} is thrown, the subtype
     * instance is returned.
   
     *
     * @param type The type (or name) of the header.
     * @param headerValue The value of the header.
     * @return The best matching header subtype instance, or <code>null</code> if no subtype can be found.
     */
    public static UpnpHeader<?> newInstance(UpnpHeader.Type type, String headerValue) {

        // Try all the UPnP headers and see if one matches our value parsers
        UpnpHeader<?> upnpHeader;
        for (Class<? extends UpnpHeader<?>> headerClass : type.getHeaderTypes()) {
            try {
				if (log.isTraceEnabled()) {
					log.trace("Trying to parse '" + type + "' with class: " + headerClass.getSimpleName());
				}
				upnpHeader = headerClass.getConstructor().newInstance();
                if (headerValue != null && !headerValue.trim().isEmpty()) {
                    upnpHeader.setString(headerValue);
                }
                return upnpHeader;
            } catch (InvalidHeaderException ex) {
				if (log.isTraceEnabled()) {
					log.trace("Invalid header value for tested type: " + headerClass.getSimpleName() + " - ", ex);
				}
			} catch (Exception ex) {
                if (log.isErrorEnabled()) {
                    log.error("Error instantiating header of type '" + type + "' with value: " + headerValue);
                    log.error("Exception root cause: ", Exceptions.unwrap(ex));
                }
            }

        }
        return null;
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") '" + getValue() + "'";
    }
}
