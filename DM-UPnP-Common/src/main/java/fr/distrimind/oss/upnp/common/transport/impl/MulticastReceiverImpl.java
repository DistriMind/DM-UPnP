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

package fr.distrimind.oss.upnp.common.transport.impl;

import fr.distrimind.oss.upnp.common.model.message.IncomingDatagramMessage;
import fr.distrimind.oss.upnp.common.transport.Common;
import fr.distrimind.oss.upnp.common.transport.Router;
import fr.distrimind.oss.upnp.common.transport.spi.DatagramProcessor;
import fr.distrimind.oss.upnp.common.transport.spi.InitializationException;
import fr.distrimind.oss.upnp.common.transport.spi.MulticastReceiver;
import fr.distrimind.oss.upnp.common.model.UnsupportedDataException;
import fr.distrimind.oss.upnp.common.transport.spi.NetworkAddressFactory;

import java.net.DatagramPacket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Default implementation based on a UDP <code>MulticastSocket</code>.
 * <p>
 * Thread-safety is guaranteed through synchronization of methods of this service and
 * by the thread-safe underlying socket.
 * </p>
 * @author Christian Bauer
 */
public class MulticastReceiverImpl implements MulticastReceiver<MulticastReceiverConfigurationImpl> {

    final private static DMLogger log = Log.getLogger(MulticastReceiverImpl.class);

    final protected MulticastReceiverConfigurationImpl configuration;

    protected Router router;
    protected NetworkAddressFactory networkAddressFactory;
    protected DatagramProcessor datagramProcessor;

    protected NetworkInterface multicastInterface;
    protected InetSocketAddress multicastAddress;
    protected MulticastSocket socket;

    public MulticastReceiverImpl(MulticastReceiverConfigurationImpl configuration) {
        this.configuration = configuration;
    }

    @Override
	public MulticastReceiverConfigurationImpl getConfiguration() {
        return configuration;
    }

    @Override
	synchronized public void init(NetworkInterface networkInterface,
								  Router router,
								  NetworkAddressFactory networkAddressFactory,
								  DatagramProcessor datagramProcessor) throws InitializationException {

        this.router = router;
        this.networkAddressFactory = networkAddressFactory;
        this.datagramProcessor = datagramProcessor;
        this.multicastInterface = networkInterface;

        try {

			if (log.isInfoEnabled()) log.info("Creating wildcard socket (for receiving multicast datagrams) on port: " + configuration.getPort());
            multicastAddress = new InetSocketAddress(configuration.getGroup(), configuration.getPort());

            socket = new MulticastSocket(configuration.getPort());
            socket.setReuseAddress(true);
            socket.setReceiveBufferSize(32768); // Keep a backlog of incoming datagrams if we are not fast enough

			if (log.isInfoEnabled()) log.info("Joining multicast group: " + multicastAddress + " on network interface: " + multicastInterface.getDisplayName());
            socket.joinGroup(multicastAddress, multicastInterface);

        } catch (Exception ex) {
            throw new InitializationException("Could not initialize " + getClass().getSimpleName() + ": " + ex);
        }
    }

    @Override
	synchronized public void stop() {
        if (socket != null && !socket.isClosed()) {
            try {
                log.debug("Leaving multicast group");
                socket.leaveGroup(multicastAddress, multicastInterface);
                // Well this doesn't work and I have no idea why I get "java.net.SocketException: Can't assign requested address"
            } catch (Exception ex) {
				if (log.isDebugEnabled()) {
					log.debug("Could not leave multicast group: ", ex);
				}
			}
            // So... just close it and ignore the log messages
            socket.close();
        }
    }

    @Override
	public void run() {

		if (log.isDebugEnabled()) {
            log.debug("Entering blocking receiving loop, listening for UDP datagrams on: " + socket.getLocalAddress());
		}
		while (true) {

            try {
                byte[] buf = new byte[getConfiguration().getMaxDatagramBytes()];
                DatagramPacket datagram = new DatagramPacket(buf, buf.length);

                socket.receive(datagram);

                InetAddress receivedOnLocalAddress =
                        networkAddressFactory.getLocalAddress(
                            multicastInterface,
                            multicastAddress.getAddress() instanceof Inet6Address,
                            datagram.getAddress()
                        );
                if (receivedOnLocalAddress==null)
                    continue;
				if (log.isDebugEnabled()) {
					log.debug(
							"UDP datagram received from: " + datagram.getAddress().getHostAddress()
									+ ":" + datagram.getPort()
									+ " on local interface: " + multicastInterface.getDisplayName()
									+ " and address: " + receivedOnLocalAddress.getHostAddress()
					);
				}

				IncomingDatagramMessage<?> idm=Common.getValidIncomingDatagramMessage(datagramProcessor.read(receivedOnLocalAddress, datagram),networkAddressFactory);
                if (idm==null)
                    continue;
                router.received(idm);

            } catch (SocketException ex) {
                log.debug("Socket closed");
                break;
            } catch (UnsupportedDataException ex) {
				if (log.isInfoEnabled()) log.info("Could not read datagram: ", ex);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        try {
            if (!socket.isClosed()) {
                log.debug("Closing multicast socket");
                socket.close();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


}

