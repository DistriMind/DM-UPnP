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

import java.net.InetAddress;
import java.util.Arrays;

/**
 * IP address, port, and optional interface hardware address (MAC) of a network service.
 *
 * @author Christian Bauer
 */
public class NetworkAddress {

    protected InetAddress address;
    protected int port;
    protected byte[] hardwareAddress;

    public NetworkAddress(InetAddress address, int port) {
        this(address, port, null);
    }

    public NetworkAddress(InetAddress address, int port, byte[] hardwareAddress) {
        this.address = address;
        this.port = port;
        this.hardwareAddress = hardwareAddress==null?null:hardwareAddress.clone();
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public byte[] getHardwareAddress() {
        return hardwareAddress==null?null:hardwareAddress.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworkAddress that = (NetworkAddress) o;

        if (port != that.port) return false;
        if (!address.equals(that.address)) return false;
		return Arrays.equals(hardwareAddress, that.hardwareAddress);
	}

    @Override
    public int hashCode() {
        int result = address.hashCode();
        result = 31 * result + port;
        result = 31 * result + (hardwareAddress != null ? Arrays.hashCode(hardwareAddress) : 0);
        return result;
    }
}
