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

import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;
import java.util.regex.Matcher;

/**
 * Represents a service identifier, for example <code>urn:my-domain-namespace:serviceId:MyService123</code>
 *
 * @author Christian Bauer
 */
public class ServiceId {

    final private static DMLogger log = Log.getLogger(ServiceId.class);

    public static final String UNKNOWN = "UNKNOWN";

    private final String namespace;
    private final String id;

    public ServiceId(String namespace, String id) {
        if (namespace != null && !Constants.getPatternNamespace().matcher(namespace).matches()) {
            throw new IllegalArgumentException("Service ID namespace contains illegal characters");
        }
        this.namespace = namespace;

        if (id != null && !Constants.getPatternId().matcher(id).matches()) {
            throw new IllegalArgumentException("Service ID suffix too long (64) or contains illegal characters");
        }
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getId() {
        return id;
    }

    public static ServiceId valueOf(String s) throws InvalidValueException {

        ServiceId serviceId = null;

        // First try UDAServiceId parse
        try {
            serviceId = UDAServiceId.valueOf(s);
        } catch (Exception ignored) {
            // Ignore
        }

        if (serviceId != null)
            return serviceId;

        // Now try a generic ServiceId parse
        Matcher matcher = Constants.getPatternServiceId().matcher(s);
        if (matcher.matches() && matcher.groupCount() >= 2) {
            return new ServiceId(matcher.group(1), matcher.group(2));
        }

        matcher = Constants.getPatternBrokenServiceId().matcher(s);
        if (matcher.matches() && matcher.groupCount() >= 2) {
            return new ServiceId(matcher.group(1), matcher.group(2));
        }

        // TODO: UPNP VIOLATION: Kodak Media Server doesn't provide any service ID token
        // urn:upnp-org:serviceId:
        matcher = Constants.getPatternServiceIdKodakMediaServer().matcher(s);
        if (matcher.matches() && matcher.groupCount() >= 1) {
            if (log.isWarnEnabled()) log.warn("UPnP specification violation, no service ID token, defaulting to " + UNKNOWN + ": " + s);
            return new ServiceId(matcher.group(1), UNKNOWN);
        }

        // TODO: UPNP VIOLATION: PS Audio Bridge has invalid service IDs
        String[] tokens = s.split("[:]");
        if (tokens.length == 4) {
            if (log.isWarnEnabled()) log.warn("UPnP specification violation, trying a simple colon-split of: " + s);
            return new ServiceId(tokens[1], tokens[3]);
        }

        throw new InvalidValueException("Can't parse service ID string (namespace/id): " + s);
    }

    @Override
    public String toString() {
        return "urn:" + getNamespace() + ":serviceId:" + getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceId)) return false;

        ServiceId serviceId = (ServiceId) o;

        if (!id.equals(serviceId.id)) return false;
		return namespace.equals(serviceId.namespace);
	}

    @Override
    public int hashCode() {
        int result = namespace.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}
