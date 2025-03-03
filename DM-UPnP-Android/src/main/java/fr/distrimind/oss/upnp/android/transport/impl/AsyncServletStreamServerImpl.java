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

package fr.distrimind.oss.upnp.android.transport.impl;

import fr.distrimind.oss.upnp.common.model.message.Connection;
import fr.distrimind.oss.upnp.common.transport.Router;
import fr.distrimind.oss.upnp.common.transport.spi.InitializationException;
import fr.distrimind.oss.upnp.common.transport.spi.NetworkAddressFactory;
import fr.distrimind.oss.upnp.common.transport.spi.StreamServer;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Implementation based on Servlet 3.0 API.
 *
 * @author Christian Bauer
 */
public class AsyncServletStreamServerImpl implements StreamServer<AsyncServletStreamServerConfigurationImpl> {

    final private static DMLogger log = Log.getLogger(AsyncServletStreamServerImpl.class);

    final protected AsyncServletStreamServerConfigurationImpl configuration;
    protected int localPort;
    protected String hostAddress;

    public AsyncServletStreamServerImpl(AsyncServletStreamServerConfigurationImpl configuration) {
        this.configuration = configuration;
    }

    @Override
	public AsyncServletStreamServerConfigurationImpl getConfiguration() {
        return configuration;
    }

    @Override
	synchronized public void init(InetAddress bindAddress, final Router router, NetworkAddressFactory networkAddressFactory) throws InitializationException {
        try {
            if (log.isDebugEnabled())
                log.debug("Setting executor service on servlet container adapter");
            getConfiguration().getServletContainerAdapter().setExecutorService(
                router.getConfiguration().getStreamServerExecutorService()
            );

            if (log.isDebugEnabled())
                log.debug("Adding connector: " + bindAddress + ":" + getConfiguration().getListenPort());
            hostAddress = bindAddress.getHostAddress();
            if (hostAddress==null)
                throw new InitializationException("");
            InetSocketAddress isa=new InetSocketAddress(hostAddress, getConfiguration().getListenPort());
            InetAddress receivedOnLocalAddress =
                    networkAddressFactory.getLocalAddress(
                            null,
                            isa.getAddress() instanceof Inet6Address,
                            isa.getAddress()
                    );
            if (receivedOnLocalAddress==null)
                throw new InitializationException("");
            localPort = getConfiguration().getServletContainerAdapter().addConnector(
                hostAddress,
                getConfiguration().getListenPort()
            );

            String contextPath = router.getConfiguration().getNamespace().getBasePath().getPath();
            getConfiguration().getServletContainerAdapter().registerServlet(contextPath, createServlet(router));

        } catch (Exception ex) {
            throw new InitializationException("Could not initialize " + getClass().getSimpleName() + ": " + ex, ex);
        }
    }

    @Override
	synchronized public int getPort() {
        return this.localPort;
    }

    @Override
	synchronized public void stop() {
        getConfiguration().getServletContainerAdapter().removeConnector(hostAddress, localPort);
    }

    @Override
	public void run() {
        getConfiguration().getServletContainerAdapter().startIfNotRunning();
    }

    private int mCounter = 0;

    protected Servlet createServlet(final Router router) {
        return new HttpServlet() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

            	final long startTime = System.currentTimeMillis();
            	final int counter = mCounter++;
                if (log.isDebugEnabled())
                	log.debug(String.format("HttpServlet.service(): id: %3d, request URI: %s", counter, req.getRequestURI()));

                AsyncContext async = req.startAsync();
                async.setTimeout(getConfiguration().getAsyncTimeoutSeconds()* 1000L);

                async.addListener(new AsyncListener() {

                    @Override
                    public void onTimeout(AsyncEvent arg0) throws IOException {
                        long duration = System.currentTimeMillis() - startTime;
                        if (log.isDebugEnabled())
                            log.debug(String.format("AsyncListener.onTimeout(): id: %3d, duration: %,4d, request: %s", counter, duration, arg0.getSuppliedRequest()));
                    }


                    @Override
                    public void onStartAsync(AsyncEvent arg0) throws IOException {
                        if (log.isDebugEnabled())
                            log.debug(String.format("AsyncListener.onStartAsync(): id: %3d, request: %s", counter, arg0.getSuppliedRequest()));
                    }


                    @Override
                    public void onError(AsyncEvent arg0) throws IOException {
                        long duration = System.currentTimeMillis() - startTime;
                        if (log.isDebugEnabled())
                            log.debug(String.format("AsyncListener.onError(): id: %3d, duration: %,4d, response: %s", counter, duration, arg0.getSuppliedResponse()));
                    }


                    @Override
                    public void onComplete(AsyncEvent arg0) throws IOException {
                        long duration = System.currentTimeMillis() - startTime;
                        if (log.isDebugEnabled())
                            log.debug(String.format("AsyncListener.onComplete(): id: %3d, duration: %,4d, response: %s", counter, duration, arg0.getSuppliedResponse()));
                    }

                });

                AsyncServletUpnpStream stream =
                    new AsyncServletUpnpStream(router.getProtocolFactory(), async, req) {
                        @Override
                        protected Connection createConnection() {
                            return new AsyncServletConnection(getRequest());
                        }
                    };

                router.received(stream);
            }
        };
    }

    /**
     * Override this method if you can check, at a low level, if the client connection is still open
     * for the given request. This will likely require access to proprietary APIs of your servlet
     * container to obtain the socket/channel for the given request.
     *
     * @return By default <code>true</code>.
     */
    protected boolean isConnectionOpen(HttpServletRequest request) {
        return true;
    }

    protected class AsyncServletConnection implements Connection {

        protected HttpServletRequest request;

        public AsyncServletConnection(HttpServletRequest request) {
            this.request = request;
        }

        public HttpServletRequest getRequest() {
            return request;
        }

        @Override
        public boolean isOpen() {
            return AsyncServletStreamServerImpl.this.isConnectionOpen(getRequest());
        }

        @Override
        public InetAddress getRemoteAddress() {
            try {
                return InetAddress.getByName(getRequest().getRemoteAddr());
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public InetAddress getLocalAddress() {
            try {
                return InetAddress.getByName(getRequest().getLocalAddr());
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
