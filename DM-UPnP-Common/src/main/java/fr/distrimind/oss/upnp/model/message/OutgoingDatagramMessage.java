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

package fr.distrimind.oss.upnp.model.message;

import java.net.InetAddress;

/**
 * A UDP datagram request or response message for sending, with destination address and port.
 *
 * @author Christian Bauer
 */
@SuppressWarnings("PMD.LooseCoupling")
public abstract class OutgoingDatagramMessage<O extends UpnpOperation> extends UpnpMessage<O> {

    private final InetAddress destinationAddress;
    private final int destinationPort;
    // For performance reasons, headers of this message are not normalized
    private final UpnpHeaders headers = new UpnpHeaders(false);

    protected OutgoingDatagramMessage(O operation, InetAddress destinationAddress, int destinationPort) {
        super(operation);
        this.destinationAddress = destinationAddress;
        this.destinationPort = destinationPort;
    }

    protected OutgoingDatagramMessage(O operation, BodyType bodyType, Object body, InetAddress destinationAddress, int destinationPort) {
        super(operation, bodyType, body);
        this.destinationAddress = destinationAddress;
        this.destinationPort = destinationPort;
    }

    public InetAddress getDestinationAddress() {
        return destinationAddress;
    }

    public int getDestinationPort() {
        return destinationPort;
    }
    
    @Override
    public IUpnpHeaders getHeaders() {
        return this.headers;
    }
}