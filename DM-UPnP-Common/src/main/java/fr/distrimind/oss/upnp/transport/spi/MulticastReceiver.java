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

import fr.distrimind.oss.upnp.model.message.IncomingDatagramMessage;
import fr.distrimind.oss.upnp.transport.Router;

import java.net.NetworkInterface;

/**
 * Service for receiving multicast UDP datagrams, one per bound network interface.
 * <p>
 * This services typically listens on a socket for UDP datagrams, the socket has joined
 * the configured multicast group.
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
 * An implementation has to be thread-safe.
 * </p>
 * 
 * @param <C> The type of the service's configuration.
 *
 * @author Christian Bauer
 */
public interface MulticastReceiver<C extends MulticastReceiverConfiguration> extends Runnable {

    /**
     * Configures the service and starts any listening sockets.
     *
     * @param networkInterface The network interface on which to join the multicast group on.
     * @param router The router which handles received {@link IncomingDatagramMessage}s.
     * @param networkAddressFactory The network address factory to use for local address lookup given a local interface and a remote address.
     * @param datagramProcessor Reads and writes datagrams.
     * @throws InitializationException If the service could not be initialized or started.
     */
	void init(NetworkInterface networkInterface,
			  Router router,
			  NetworkAddressFactory networkAddressFactory,
			  DatagramProcessor datagramProcessor) throws InitializationException;

    /**
     * Stops the service, closes any listening sockets.
     */
	void stop();

    /**
     * @return This service's configuration.
     */
	C getConfiguration();

}
