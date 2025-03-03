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

package fr.distrimind.oss.upnp.common.support.model.dlna.message.header;

import fr.distrimind.oss.upnp.common.util.Exceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;
import fr.distrimind.oss.upnp.common.model.message.header.InvalidHeaderException;
import fr.distrimind.oss.upnp.common.model.message.header.UpnpHeader;

/**
 * Transforms known and standardized DLNA/HTTP headers from/to string representation.
 * <p>
 * The {@link #newInstance(Type, String)} method
 * attempts to instantiate the best header subtype for a given header (name) and string value.
 * </p>
 *
 * @author Mario Franco
 * @author Christian Bauer
 */
public abstract class DLNAHeader<T> extends UpnpHeader<T> {

    final private static DMLogger log = Log.getLogger(DLNAHeader.class);

    /**
     * Maps a standardized DLNA header to potential header subtypes.
     */
    public enum Type {

        TimeSeekRange("TimeSeekRange.dlna.org", TimeSeekRangeHeader.class),
        XSeekRange("X-Seek-Range", TimeSeekRangeHeader.class),
        PlaySpeed("PlaySpeed.dlna.org", PlaySpeedHeader.class),
        AvailableSeekRange("availableSeekRange.dlna.org", AvailableSeekRangeHeader.class),
        GetAvailableSeekRange("getAvailableSeekRange.dlna.org", GetAvailableSeekRangeHeader.class),
        GetContentFeatures("getcontentFeatures.dlna.org", GetContentFeaturesHeader.class),
        ContentFeatures("contentFeatures.dlna.org", ContentFeaturesHeader.class),
        TransferMode("transferMode.dlna.org", TransferModeHeader.class),
        FriendlyName("friendlyName.dlna.org", FriendlyNameHeader.class),
        PeerManager("peerManager.dlna.org", PeerManagerHeader.class),
        AvailableRange("Available-Range.dlna.org", AvailableRangeHeader.class),
        SCID("scid.dlna.org", SCIDHeader.class),
        RealTimeInfo("realTimeInfo.dlna.org", RealTimeInfoHeader.class),
        ScmsFlag("scmsFlag.dlna.org", ScmsFlagHeader.class),
        WCT("WCT.dlna.org", WCTHeader.class),
        MaxPrate("Max-Prate.dlna.org", MaxPrateHeader.class),
        EventType("Event-Type.dlna.org", EventTypeHeader.class),
        Supported("Supported", SupportedHeader.class),
        BufferInfo("Buffer-Info.dlna.org", BufferInfoHeader.class),
        RTPH264DeInterleaving("rtp-h264-deint-buf-cap.dlna.org", BufferBytesHeader.class),
        RTPAACDeInterleaving("rtp-aac-deint-buf-cap.dlna.org", BufferBytesHeader.class),
        RTPAMRDeInterleaving("rtp-amr-deint-buf-cap.dlna.org", BufferBytesHeader.class),
        RTPAMRWBPlusDeInterleaving("rtp-amrwbplus-deint-buf-cap.dlna.org", BufferBytesHeader.class),
        PRAGMA("PRAGMA", PragmaHeader.class);
            
        private static final Map<String, Type> byName = new HashMap<>() {
            private static final long serialVersionUID = 1L;
            {
			for (Type t : Type.values()) {
				put(t.getHttpName(), t);
			}
		}};

        private final String httpName;
        private final List<Class<? extends DLNAHeader<?>>> headerTypes;

        @SafeVarargs
		Type(String httpName, Class<? extends DLNAHeader<?>>... headerClass) {
            this.httpName = httpName;
            this.headerTypes = List.of(headerClass);
        }

        public String getHttpName() {
            return httpName;
        }

        public List<Class<? extends DLNAHeader<?>>> getHeaderTypes() {
            return headerTypes;
        }

        public boolean isValidHeaderType(Class<? extends DLNAHeader<?>> clazz) {
            for (Class<? extends DLNAHeader<?>> permissibleType : getHeaderTypes()) {
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
        	return byName.get(httpName);
        }
    }



    /**
     * Create a new instance of a {@link DLNAHeader} subtype that matches the given type and value.
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
    public static DLNAHeader<?> newInstance(Type type, String headerValue) {

        // Try all the UPnP headers and see if one matches our value parsers
        DLNAHeader<?> upnpHeader = null;
        for (int i = 0; i < type.getHeaderTypes().size() && upnpHeader == null; i++) {
            Class<? extends DLNAHeader<?>> headerClass = type.getHeaderTypes().get(i);
            try {
				if (log.isTraceEnabled()) {
					log.trace("Trying to parse '" + type + "' with class: " + headerClass.getSimpleName());
				}
				upnpHeader = headerClass.getConstructor().newInstance();
                if (headerValue != null) {
                    upnpHeader.setString(headerValue);
                }
            } catch (InvalidHeaderException ex) {
				if (log.isTraceEnabled()) {
					log.trace("Invalid header value for tested type: " + headerClass.getSimpleName() + " - ", ex.getMessage());
				}
				upnpHeader = null;
            } catch (Exception ex) {
                if (log.isErrorEnabled()) {
                    log.error("Error instantiating header of type '" + type + "' with value: " + headerValue);
                    log.error("Exception root cause: ", Exceptions.unwrap(ex));
                }

            }

        }
        return upnpHeader;
    }
}
