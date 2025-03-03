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

package fr.distrimind.oss.upnp.common.transport;

import fr.distrimind.oss.upnp.common.UpnpServiceConfiguration;
import fr.distrimind.oss.upnp.common.model.NetworkAddress;
import fr.distrimind.oss.upnp.common.model.message.IncomingDatagramMessage;
import fr.distrimind.oss.upnp.common.model.message.OutgoingDatagramMessage;
import fr.distrimind.oss.upnp.common.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.common.protocol.ProtocolFactory;
import fr.distrimind.oss.upnp.common.transport.spi.InitializationException;
import fr.distrimind.oss.upnp.common.transport.spi.UpnpStream;

import java.net.InetAddress;
import java.util.List;

/**
 * Interface of the network transport layer.
 * <p>
 * Encapsulates the transport layer and provides methods to the upper layers for
 * sending UPnP stream (HTTP) {@link StreamRequestMessage}s,
 * sending (UDP) datagram {@link OutgoingDatagramMessage}s,
 * as well as broadcasting bytes to all LAN participants.
 * </p>
 * <p>
 * A router also maintains listening sockets and services, for incoming UDP unicast/multicast
 * {@link IncomingDatagramMessage} and TCP
 * {@link UpnpStream}s. An implementation of this interface
 * handles these messages, e.g. by selecting and executing the right protocol.
 * </p>
 * <p>
 * An implementation must be thread-safe, and can be accessed concurrently. If the Router is
 * disabled, it doesn't listen on the network for incoming messages and does not send outgoing
 * messages.
 * </p>
 *
 * @see ProtocolFactory
 *
 * @author Christian Bauer
 */
public interface Router {

    /**
     * @return The configuration used by this router.
     */
	UpnpServiceConfiguration getConfiguration();

    /**
     * @return The protocol factory used by this router.
     */
	ProtocolFactory getProtocolFactory();

    /**
     * Starts all sockets and listening threads for datagrams and streams.
     *
     * @return <code>true</code> if the router was enabled. <code>false</code> if it's already running.
     */
    boolean enable() throws RouterException;

    /**
     * Unbinds all sockets and stops all listening threads for datagrams and streams.
     *
     * @return <code>true</code> if the router was disabled. <code>false</code> if it wasn't running.
     */
    boolean disable() throws RouterException;

    /**
     * Disables the router and releases all other resources.
     */
    void shutdown() throws RouterException ;

    /**
     *
     * @return <code>true</code> if the router is currently enabled.
     */
    boolean isEnabled() throws RouterException;

    /**
     * Called by the {@link #enable()} method before it returns.
     *
     * @param ex The cause of the failure.
     * @throws InitializationException if the exception was not recoverable.
     */
    void handleStartFailure(InitializationException ex) throws InitializationException;

    /**
     * @param preferredAddress A preferred stream server bound address or <code>null</code>.
     * @return An empty list if no stream server is currently active, otherwise a single network
     *         address if the preferred address is active, or a list of all active bound
     *         stream servers.
     */
	List<NetworkAddress> getActiveStreamServers(InetAddress preferredAddress) throws RouterException;

    /**
     * <p>
     * This method is called internally by the transport layer when a datagram, either unicast or
     * multicast, has been received. An implementation of this interface has to handle the received
     * message, e.g. selecting and executing a UPnP protocol. This method should not block until
     * the execution completes, the calling thread should be free to handle the next reception as
     * soon as possible.
   
     * @param msg The received datagram message.
     */
	void received(IncomingDatagramMessage<?> msg);

    /**
     * <p>
     * This method is called internally by the transport layer when a TCP stream connection has
     * been made and a response has to be returned to the sender. An implementation of this interface
     * has to handle the received stream connection and return a response, e.g. selecting and executing
     * a UPnP protocol. This method should not block until the execution completes, the calling thread
     * should be free to process the next reception as soon as possible. Typically this means starting
     * a new thread of execution in this method.
   
     *
     */
	void received(UpnpStream stream);

    /**
     * <p>
     * Call this method to send a UDP datagram message.
   
     * @param msg The UDP datagram message to send.
     * @throws RouterException if a recoverable error, such as thread interruption, occurs.
     */
	void send(OutgoingDatagramMessage<?> msg) throws RouterException;

    /**
     * <p>
     * Call this method to send a TCP (HTTP) stream message.
   
     * @param msg The TCP (HTTP) stream message to send.
     * @return The response received from the server.
     * @throws RouterException if a recoverable error, such as thread interruption, occurs.
     */
	StreamResponseMessage send(StreamRequestMessage msg) throws RouterException;

    /**
     * <p>
     * Call this method to broadcast a UDP message to all hosts on the network.
   
     * @param bytes The byte payload of the UDP datagram.
     * @throws RouterException if a recoverable error, such as thread interruption, occurs.
     */
	void broadcast(byte[] bytes) throws RouterException;

}
