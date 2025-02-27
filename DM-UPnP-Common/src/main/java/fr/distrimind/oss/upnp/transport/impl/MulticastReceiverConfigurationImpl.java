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

package fr.distrimind.oss.upnp.transport.impl;

import fr.distrimind.oss.upnp.model.Constants;
import fr.distrimind.oss.upnp.transport.spi.MulticastReceiverConfiguration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Settings for the default implementation.
 * 
 * @author Christian Bauer
 */
public class MulticastReceiverConfigurationImpl implements MulticastReceiverConfiguration {

    private InetAddress group;
    private int port;
    private int maxDatagramBytes;

    public MulticastReceiverConfigurationImpl(InetAddress group, int port, int maxDatagramBytes) {
        this.group = group;
        this.port = port;
        this.maxDatagramBytes = maxDatagramBytes;
    }

    /**
     * Defaults to maximum datagram size of 640 bytes (512 per UDA 1.0, 128 byte header).
     */
    public MulticastReceiverConfigurationImpl(InetAddress group, int port) {
        this(group, port, Constants.MAX_HEADER_LENGTH_IN_BYTES);
    }

    public MulticastReceiverConfigurationImpl(String group, int port, int maxDatagramBytes) throws UnknownHostException {
        this(InetAddress.getByName(group), port, maxDatagramBytes);
    }

    /**
     * Defaults to maximum datagram size of 640 bytes (512 per UDA 1.0, 128 byte header).
     */
    public MulticastReceiverConfigurationImpl(String group, int port) throws UnknownHostException {
        this(InetAddress.getByName(group), port, Constants.MAX_HEADER_LENGTH_IN_BYTES);
    }

    @Override
	public InetAddress getGroup() {
        return group;
    }

    public void setGroup(InetAddress group) {
        this.group = group;
    }

    @Override
	public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
	public int getMaxDatagramBytes() {
        return maxDatagramBytes;
    }

    public void setMaxDatagramBytes(int maxDatagramBytes) {
        this.maxDatagramBytes = maxDatagramBytes;
    }

}
