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

package fr.distrimind.oss.upnp.desktop.transport.impl;

import fr.distrimind.oss.upnp.common.transport.Router;
import fr.distrimind.oss.upnp.common.transport.impl.StreamServerConfigurationImpl;
import fr.distrimind.oss.upnp.common.transport.spi.NetworkAddressFactory;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import fr.distrimind.oss.upnp.common.model.message.Connection;
import fr.distrimind.oss.upnp.common.transport.spi.InitializationException;
import fr.distrimind.oss.upnp.common.transport.spi.StreamServer;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Implementation based on the built-in SUN JDK 6.0 HTTP Server.
 * <p>
 * See <a href="http://download.oracle.com/javase/6/docs/jre/api/net/httpserver/spec/index.html?com/sun/net/httpserver/HttpServer.html">the
 * documentation of the SUN JDK 6.0 HTTP Server</a>.
 * </p>
 * <p>
 * This implementation <em>DOES NOT WORK</em> on Android. Read the DM-UPnP manual for
 * alternatives for Android.
 * </p>
 * <p>
 * This implementation does not support connection alive checking, as we can't send
 * heartbeats to the client. We don't have access to the raw socket with the Sun API.
 * </p>
 *
 * @author Christian Bauer
 */
public class StreamServerImpl implements StreamServer<StreamServerConfigurationImpl> {

    final private static DMLogger log = Log.getLogger(StreamServerImpl.class);

    final protected StreamServerConfigurationImpl configuration;
    protected HttpServer server;

    public StreamServerImpl(StreamServerConfigurationImpl configuration) {
        this.configuration = configuration;
    }

    @Override
	synchronized public void init(InetAddress bindAddress, Router router, NetworkAddressFactory networkAddressFactory) throws InitializationException {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(bindAddress, configuration.getListenPort());

            server = HttpServer.create(socketAddress, configuration.getTcpConnectionBacklog());
            server.createContext("/", new RequestHttpHandler(router, networkAddressFactory));

            if (log.isInfoEnabled()) log.info("Created server (for receiving TCP streams) on: " + server.getAddress());

        } catch (Exception ex) {
            throw new InitializationException("Could not initialize " + getClass().getSimpleName() + ": " + ex, ex);
        }
    }

    @Override
	synchronized public int getPort() {
        return server.getAddress().getPort();
    }

    @Override
	public StreamServerConfigurationImpl getConfiguration() {
        return configuration;
    }

    @Override
	synchronized public void run() {
        log.debug("Starting StreamServer...");
        // Starts a new thread but inherits the properties of the calling thread
        server.start();
    }

    @Override
	synchronized public void stop() {
        log.debug("Stopping StreamServer...");
        if (server != null) server.stop(1);
    }

    protected class RequestHttpHandler implements HttpHandler {

        private final Router router;
        private final NetworkAddressFactory networkAddressFactory;

        public RequestHttpHandler(Router router, NetworkAddressFactory networkAddressFactory) {
            this.router = router;
            this.networkAddressFactory=networkAddressFactory;
        }

        // This is executed in the request receiving thread!
        @Override
		public void handle(final HttpExchange httpExchange) throws IOException {
            InetSocketAddress isa=httpExchange.getRemoteAddress();
            if (isa==null)
                return;
            InetAddress receivedOnLocalAddress =
                    networkAddressFactory.getLocalAddress(
                            null,
                            isa.getAddress() instanceof Inet6Address,
                            isa.getAddress()
                    );
            if (receivedOnLocalAddress==null)
                return;
            // And we pass control to the service, which will (hopefully) start a new thread immediately, so we can
            // continue the receiving thread ASAP
			if (log.isDebugEnabled()) {
				log.debug("Received HTTP exchange: " + httpExchange.getRequestMethod() + " " + httpExchange.getRequestURI());
			}
			router.received(
                new HttpExchangeUpnpStream(router.getProtocolFactory(), httpExchange) {
                    @Override
                    protected Connection createConnection() {
                        return new HttpServerConnection(httpExchange);
                    }
                }
            );
        }
    }

    /**
     * Logs a warning and returns <code>true</code>, we can't access the socket using the awful JDK webserver API.
     * <p>
     * Override this method if you know how to do it.
   
     */
    protected boolean isConnectionOpen(HttpExchange exchange) {
        log.warn("Can't check client connection, socket access impossible on JDK webserver!");
        return true;
    }

    protected class HttpServerConnection implements Connection {

        protected HttpExchange exchange;

        public HttpServerConnection(HttpExchange exchange) {
            this.exchange = exchange;
        }

        @Override
        public boolean isOpen() {
            return isConnectionOpen(exchange);
        }

        @Override
        public InetAddress getRemoteAddress() {
            return exchange.getRemoteAddress() != null
                ? exchange.getRemoteAddress().getAddress()
                : null;
        }

        @Override
        public InetAddress getLocalAddress() {
            return exchange.getLocalAddress() != null
                ? exchange.getLocalAddress().getAddress()
                : null;
        }
    }
}
