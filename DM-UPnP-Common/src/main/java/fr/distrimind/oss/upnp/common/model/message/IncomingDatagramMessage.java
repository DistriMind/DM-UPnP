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

package fr.distrimind.oss.upnp.common.model.message;

import java.net.InetAddress;

/**
 * A received UDP datagram request or response message, with source address and port.
 * <p>
 * Additionally, holds a local address that is reachable from the source
 * address (in the same subnet):
 * </p>
 * <ul>
 * <li>When an M-SEARCH is received, we send a LOCATION header back with a
 *     reachable (by the remote control point) local address.</li>
 * <li>When a NOTIFY discovery message (can be a search response) is received we
       need to memorize on which local address it was received, so that the we can
       later give the remote device a reachable (from its point of view) local
       GENA callback address.</li>
 * </ul>
 *
 * @author Christian Bauer
 */
public class IncomingDatagramMessage<O extends UpnpOperation> extends UpnpMessage<O> {

    private final InetAddress sourceAddress;
    private final int sourcePort;
    private final InetAddress localAddress;

    public IncomingDatagramMessage(O operation, InetAddress sourceAddress, int sourcePort, InetAddress localAddress) {
        super(operation);
        this.sourceAddress = sourceAddress;
        this.sourcePort = sourcePort;
        this.localAddress = localAddress;
    }

    protected IncomingDatagramMessage(IncomingDatagramMessage<O> source) {
        super(source);
        this.sourceAddress = source.getSourceAddress();
        this.sourcePort = source.getSourcePort();
        this.localAddress = source.getLocalAddress();
    }

    public InetAddress getSourceAddress() {
        return sourceAddress;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public InetAddress getLocalAddress() {
        return localAddress;
    }

}
