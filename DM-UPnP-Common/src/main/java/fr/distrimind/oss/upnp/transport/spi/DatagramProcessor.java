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

package fr.distrimind.oss.upnp.transport.spi;


import fr.distrimind.oss.upnp.model.message.UpnpRequest;
import fr.distrimind.oss.upnp.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.model.UnsupportedDataException;
import fr.distrimind.oss.upnp.model.message.IncomingDatagramMessage;
import fr.distrimind.oss.upnp.model.message.OutgoingDatagramMessage;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Reads and creates UDP datagrams from and into UPnP messages.
 * <p>
 * An implementation of this interface has to be thread-safe.
 * </p>
 *
 * @author Christian Bauer
 */
public interface DatagramProcessor {

    /**
     * Reads the datagram and instantiates a message.
     * <p>
     * The message is either a {@link UpnpRequest} or
     * a {@link UpnpResponse} operation type.
   
     *
     * @param receivedOnAddress The address of the socket on which this datagram was received.
     * @param datagram The received UDP datagram.
     * @return The populated instance.
     * @throws UnsupportedDataException If the datagram could not be read, or didn't contain required data.
     */
	IncomingDatagramMessage<?> read(InetAddress receivedOnAddress, DatagramPacket datagram) throws UnsupportedDataException;

    /**
     * Creates a UDP datagram with the content of a message.
     * <p>
     * The outgoing message might be a {@link UpnpRequest} or a
     * {@link UpnpResponse}.
   
     *
     * @param message The outgoing datagram message.
     * @return An actual UDP datagram.
     * @throws UnsupportedDataException If the datagram could not be created.
     */
	DatagramPacket write(OutgoingDatagramMessage<?> message) throws UnsupportedDataException;

}


