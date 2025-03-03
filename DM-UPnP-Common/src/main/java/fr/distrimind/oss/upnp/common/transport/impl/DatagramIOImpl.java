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
import fr.distrimind.oss.upnp.common.model.message.OutgoingDatagramMessage;
import fr.distrimind.oss.upnp.common.transport.spi.DatagramIO;
import fr.distrimind.oss.upnp.common.transport.spi.DatagramProcessor;
import fr.distrimind.oss.upnp.common.transport.spi.InitializationException;
import fr.distrimind.oss.upnp.common.model.UnsupportedDataException;
import fr.distrimind.oss.upnp.common.transport.spi.NetworkAddressFactory;

import java.net.*;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Default implementation based on a single shared (receive/send) UDP <code>MulticastSocket</code>.
 * <p>
 * Although we do not receive multicast datagrams with this service, sending multicast
 * datagrams with a configuration time-to-live requires a <code>MulticastSocket</code>.
 * </p>
 * <p>
 * Thread-safety is guaranteed through synchronization of methods of this service and
 * by the thread-safe underlying socket.
 * </p>
 * @author Christian Bauer
 */
public class DatagramIOImpl implements DatagramIO<DatagramIOConfigurationImpl> {

    final private static DMLogger log = Log.getLogger(DatagramIOImpl.class);

    /* Implementation notes for unicast/multicast UDP:

    http://forums.sun.com/thread.jspa?threadID=771852
    http://mail.openjdk.java.net/pipermail/net-dev/2008-December/000497.html
    https://jira.jboss.org/jira/browse/JGRP-978
    http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4701650

     */

    final protected DatagramIOConfigurationImpl configuration;

    protected Router router;
    protected DatagramProcessor datagramProcessor;
    protected NetworkAddressFactory networkAddressFactory;
    protected InetSocketAddress localAddress;
    protected MulticastSocket socket; // For sending unicast & multicast, and reveiving unicast

    public DatagramIOImpl(DatagramIOConfigurationImpl configuration) {
        this.configuration = configuration;
    }

    @Override
	public DatagramIOConfigurationImpl getConfiguration() {
        return configuration;
    }

    @Override
	synchronized public void init(NetworkAddressFactory networkAddressFactory, InetAddress bindAddress, Router router, DatagramProcessor datagramProcessor) throws InitializationException {

        this.router = router;
        this.networkAddressFactory = networkAddressFactory;
        this.datagramProcessor = datagramProcessor;

        try {

            // TODO: UPNP VIOLATION: The spec does not prohibit using the 1900 port here again, however, the
            // Netgear ReadyNAS miniDLNA implementation will no longer answer if it has to send search response
            // back via UDP unicast to port 1900... so we use an ephemeral port
			if (log.isInfoEnabled()) log.info("Creating bound socket (for datagram input/output) on: " + bindAddress);
            localAddress = new InetSocketAddress(bindAddress, 0);
            socket = new MulticastSocket(localAddress);
            socket.setTimeToLive(configuration.getTimeToLive());
            socket.setReceiveBufferSize(262144); // Keep a backlog of incoming datagrams if we are not fast enough
        } catch (Exception ex) {
            throw new InitializationException("Could not initialize " + getClass().getSimpleName() + ": " + ex);
        }
    }

    @Override
	synchronized public void stop() {
        if (socket != null && !socket.isClosed()) {
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
                                null,
                                datagram.getAddress() instanceof Inet6Address,
                                datagram.getAddress()
                        );
                if (receivedOnLocalAddress==null)
                    continue;
				if (log.isDebugEnabled()) {
					log.debug(
							"UDP datagram received from: "
									+ datagram.getAddress().getHostAddress()
									+ ":" + datagram.getPort()
									+ " on: " + localAddress
					);
				}


				IncomingDatagramMessage<?> idm= Common.getValidIncomingDatagramMessage(datagramProcessor.read(localAddress.getAddress(), datagram), networkAddressFactory);
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
                log.debug("Closing unicast socket");
                socket.close();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
	synchronized public void send(OutgoingDatagramMessage<?> message) {
        if (log.isDebugEnabled()) {
            log.debug("Sending message from address: " + localAddress);
        }
        DatagramPacket packet = datagramProcessor.write(message);

        if (log.isDebugEnabled()) {
            log.debug("Sending UDP datagram packet to: " + message.getDestinationAddress() + ":" + message.getDestinationPort());
        }
        
        send(packet);
    }

    @Override
	synchronized public void send(DatagramPacket datagram) {
        if (log.isDebugEnabled()) {
            log.debug("Sending message from address: " + localAddress);
        }
            
        try {
            socket.send(datagram);
        } catch (SocketException ex) {
			if (log.isDebugEnabled()) {
				log.debug("Socket closed, aborting datagram send to: " + datagram.getAddress());
			}
		} catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
			if (log.isErrorEnabled()) log.error("Exception sending datagram to: " + datagram.getAddress() + ": ", ex);
        }
    }
}
