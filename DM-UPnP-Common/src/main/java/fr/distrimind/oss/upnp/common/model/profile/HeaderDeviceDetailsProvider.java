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

package fr.distrimind.oss.upnp.common.model.profile;

import fr.distrimind.oss.upnp.common.model.meta.DeviceDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Selects device details based on a regex and the client's HTTP headers.
 * <p>
 * This provider will lookup and match a {@link DeviceDetails} entry in a
 * given map that is keyed by HTTP header name and a regular expression pattern.
 * If the control point sent an HTTP header that matches an entry's name,
 * and the value of the control points header matches the pattern of the entry,
 * the value of the entry is applied. This is a case-insensitive pattern match.
 * </p>
 *
 * @author Mario Franco
 * @author Christian Bauer
 */
public class HeaderDeviceDetailsProvider implements DeviceDetailsProvider {

    public static class Key {

        final String headerName;
        final String valuePattern;
        final Pattern pattern;

        public Key(String headerName, String valuePattern) {
            this.headerName = headerName;
            this.valuePattern = valuePattern;
            this.pattern = Pattern.compile(valuePattern, Pattern.CASE_INSENSITIVE);
        }

        public String getHeaderName() {
            return headerName;
        }

        public String getValuePattern() {
            return valuePattern;
        }

        public boolean isValuePatternMatch(String value) {
            return pattern.matcher(value).matches();
        }
    }


    final private DeviceDetails defaultDeviceDetails;
    final private Map<Key, DeviceDetails> headerDetails;

    public HeaderDeviceDetailsProvider(DeviceDetails defaultDeviceDetails) {
        this(defaultDeviceDetails, null);
    }

    public HeaderDeviceDetailsProvider(DeviceDetails defaultDeviceDetails,
                                       Map<Key, DeviceDetails> headerDetails) {
        this.defaultDeviceDetails = defaultDeviceDetails;
        this.headerDetails = headerDetails != null ? headerDetails : new HashMap<>();
    }

    public DeviceDetails getDefaultDeviceDetails() {
        return defaultDeviceDetails;
    }

    public Map<Key, DeviceDetails> getHeaderDetails() {
        return headerDetails;
    }

    @Override
	public DeviceDetails provide(RemoteClientInfo info) {
        if (info == null || info.getRequestHeaders().isEmpty()) return getDefaultDeviceDetails();

        for (Key key : getHeaderDetails().keySet()) {
            List<String> headerValues;
            if ((headerValues = info.getRequestHeaders().get(key.getHeaderName())) == null) continue;
            for (String headerValue : headerValues) {
                if (key.isValuePatternMatch(headerValue))
                    return getHeaderDetails().get(key);
            }
        }
        return getDefaultDeviceDetails();
    }

}
