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

package fr.distrimind.oss.upnp.common.transport.spi;

import fr.distrimind.oss.upnp.common.model.message.IncomingDatagramMessage;
import fr.distrimind.oss.upnp.common.transport.Router;
import fr.distrimind.oss.upnp.common.model.message.OutgoingDatagramMessage;

import java.net.InetAddress;
import java.net.DatagramPacket;

/**
 * Service for receiving (unicast only) and sending UDP datagrams, one per bound IP address.
 * <p>
 * This service typically listens on a socket for UDP unicast datagrams, with
 * an ephemeral port.
 * </p>
 * <p>
 * This listening loop is started with the <code>run()</code> method,
 * this service is <code>Runnable</code>. Any received datagram is then converted into an
 * {@link IncomingDatagramMessage} and
 * handled by the
 * {@link Router#received(IncomingDatagramMessage)}
 * method. This conversion is the job of the {@link DatagramProcessor}.
 * </p>
 * <p>
 * Clients of this service use it to send UDP datagrams, either to a unicast
 * or multicast destination. Any {@link OutgoingDatagramMessage} can
 * be converted and written into a datagram with the {@link DatagramProcessor}.
 * </p>
 * <p>
 * An implementation has to be thread-safe.
 * </p>
 *
 * @param <C> The type of the service's configuration.
 *
 * @author Christian Bauer
 */
public interface DatagramIO<C extends DatagramIOConfiguration> extends Runnable {

    /**
     * Configures the service and starts any listening sockets.
     *
     * @param bindAddress The address to bind any sockets on.
     * @param router The router which handles received {@link IncomingDatagramMessage}s.
     * @param datagramProcessor Reads and writes datagrams.
     * @throws InitializationException If the service could not be initialized or started.
     */
	void init(NetworkAddressFactory networkAddressFactory, InetAddress bindAddress, Router router, DatagramProcessor datagramProcessor) throws InitializationException;

    /**
     * Stops the service, closes any listening sockets.
     */
	void stop();

    /**
     * @return This service's configuration.
     */
	C getConfiguration();

    /**
     * Sends a datagram after conversion with {@link DatagramProcessor#write(OutgoingDatagramMessage)}.
     *
     * @param message The message to send.
     */
	void send(OutgoingDatagramMessage<?> message);

    /**
     * The actual sending of a UDP datagram.
     * <p>
     * Recoverable errors should be logged, if appropriate only with debug level. Any
     * non-recoverable errors should be thrown as <code>RuntimeException</code>s.
   
     *
     * @param datagram The UDP datagram to send.
     */
	void send(DatagramPacket datagram);
}
