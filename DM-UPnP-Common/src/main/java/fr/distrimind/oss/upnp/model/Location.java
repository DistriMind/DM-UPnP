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

package fr.distrimind.oss.upnp.model;

import java.net.InetAddress;
import java.net.URL;

/**
 * The IP address/port, MAC address, and URI path of a (network) location.
 * <p>
 * Used when sending messages about local devices and services to
 * other UPnP participants on the network, such as where our device/service
 * descriptors can be found or what callback address to use for event message
 * delivery. We also let them know our MAC hardware address so they
 * can wake us up from sleep with Wake-On-LAN if necessary.
 * </p>
 *
 * @author Christian Bauer
 */
public class Location {

    protected final NetworkAddress networkAddress;
    protected final String path;
    protected final URL url;

    public Location(NetworkAddress networkAddress, String path) {
        this.networkAddress = networkAddress;
        this.path = path;
        this.url = createAbsoluteURL(networkAddress.getAddress(), networkAddress.getPort(), path);
    }

    public NetworkAddress getNetworkAddress() {
        return networkAddress;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (!networkAddress.equals(location.networkAddress)) return false;
		return path.equals(location.path);
	}

    @Override
    public int hashCode() {
        int result = networkAddress.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }

    /**
     * @return An HTTP URL with the address, port, and path of this location.
     */
    public URL getURL() {
        return url;
    }

    // Performance optimization on Android
    private static URL createAbsoluteURL(InetAddress address, int localStreamPort, String path) {
        try {
            return new URL("http", address.getHostAddress(), localStreamPort, path);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Address, port, and URI can not be converted to URL", ex);
        }
    }
}
