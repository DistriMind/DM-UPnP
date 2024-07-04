/*
 * Copyright (C) 2013 4th Line GmbH, Switzerland
 *
 * The contents of this file are subject to the terms of either the GNU
 * Lesser General Public License Version 2 or later ("LGPL") or the
 * Common Development and Distribution License Version 1 or later
 * ("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. See LICENSE.txt for more
 * information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package com.distrimind.upnp_igd.transport.impl.jetty;


import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import com.distrimind.upnp_igd.transport.spi.ServletContainerAdapter;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A singleton wrapper of a <code>org.eclipse.jetty.server.Server</code>.
 * <p>
 * This {@link ServletContainerAdapter} starts
 * a Jetty 8 instance on its own and stops it. Only one single context and servlet
 * is registered, to handle UPnP requests.
 * </p>
 * <p>
 * This implementation works on Android, dependencies are <code>jetty-server</code>
 * and <code>jetty-servlet</code> Maven modules.
 * </p>
 *
 * @author Christian Bauer
 */
public class JettyServletContainer implements ServletContainerAdapter {

    final private static Logger log = Logger.getLogger(JettyServletContainer.class.getName());

    // Singleton
    public static final JettyServletContainer INSTANCE = new JettyServletContainer();
    private ThreadPoolExecutor poolExecutor;
    private JettyServletContainer() {
        resetServer();
    }

    protected Server server;

    @Override
    synchronized public void setExecutorService(ExecutorService executorService) {
        this.poolExecutor=(ThreadPoolExecutor) executorService;
        server=null;
        resetServer();
        /*if (INSTANCE.server.getThreadPool() == null) {
            server=new Server(new ExecutorThreadPool((ThreadPoolExecutor) executorService) {
                @Override
                protected void doStop() throws Exception {
                    // Do nothing, don't shut down the Cling ExecutorService when Jetty stops!
                }
            });
            /*INSTANCE.server.setThreadPool(new ExecutorThreadPool(executorService) {
                @Override
                protected void doStop() throws Exception {
                    // Do nothing, don't shut down the Cling ExecutorService when Jetty stops!
                }
            });
        }*/
    }

    @Override
    synchronized public int addConnector(String host, int port) throws IOException {
        ServerConnector connector = new ServerConnector(server);
        connector.setHost(host);
        connector.setPort(port);

        // Open immediately so we can get the assigned local port
        connector.open();

        // Only add if open() succeeded
        server.addConnector(connector);

        // stats the connector if the server is started (server starts all connectors when started)
        if (server.isStarted()) {
            try {
                connector.start();
            } catch (Exception ex) {
                log.severe("Couldn't start connector: " + connector + " " + ex);
                throw new RuntimeException(ex);
            }
        }
        return connector.getLocalPort();
    }

    @Override
    synchronized public void removeConnector(String host, int port)  {
        Connector[] connectors = server.getConnectors();
        for (Connector connector : connectors) {
            if (connector instanceof ServerConnector) {
                ServerConnector sc=(ServerConnector)connector;
                if (sc.getHost().equals(host) && sc.getLocalPort() == port) {
                    if (connector.isStarted() || connector.isStarting()) {
                        try {
                            connector.stop();
                        } catch (Exception ex) {
                            log.severe("Couldn't stop connector: " + connector + " " + ex);
                            throw new RuntimeException(ex);
                        }
                    }
                    server.removeConnector(connector);
                    if (connectors.length == 1) {
                        log.info("No more connectors, stopping Jetty server");
                        stopIfRunning();
                    }
                    break;
                }
            }
        }
    }

    @Override
    synchronized public void registerServlet(String contextPath, Servlet servlet) {
        if (server.getHandler() != null) {
            return;
        }
        log.info("Registering UPnP servlet under context path: " + contextPath);
        ServletContextHandler servletHandler =
            new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        if (contextPath != null && !contextPath.isEmpty())
            servletHandler.setContextPath(contextPath);
        ServletHolder s = new ServletHolder(servlet);
        servletHandler.addServlet(s, "/*");
        server.setHandler(servletHandler);
    }

    @Override
    synchronized public void startIfNotRunning() {
        if (!server.isStarted() && !server.isStarting()) {
            log.info("Starting Jetty server... ");
            try {
                server.start();
            } catch (Exception ex) {
                log.severe("Couldn't start Jetty server: " + ex);
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    synchronized public void stopIfRunning() {
        if (!server.isStopped() && !server.isStopping()) {
            log.info("Stopping Jetty server...");
            try {
                server.stop();
            } catch (Exception ex) {
                log.severe("Couldn't stop Jetty server: " + ex);
                throw new RuntimeException(ex);
            } finally {
                resetServer();
            }
        }
    }

    protected void resetServer() {
        if (poolExecutor!=null) {
            server = new Server(new ExecutorThreadPool(poolExecutor) {
                @Override
                protected void doStop() throws Exception {
                    // Do nothing, don't shut down the Cling ExecutorService when Jetty stops!
                }
            }); // Has its own QueuedThreadPool
            //server.setGracefulShutdown(1000); // Let's wait a second for ongoing transfers to complete
        }
    }

    /**
     * Casts the request to a Jetty API and tries to write a space character to the output stream of the socket.
     * <p>
     * This space character might confuse the HTTP client. The Cling transports for Jetty Client and
     * Apache HttpClient have been tested to work with space characters. Unfortunately, Sun JDK's
     * HttpURLConnection does not gracefully handle any garbage in the HTTP request!
   
     */
    public static boolean isConnectionOpen(HttpServletRequest request) {
        return isConnectionOpen(request, " ".getBytes());
    }

    public static boolean isConnectionOpen(HttpServletRequest request, byte[] heartbeat) {
        Request jettyRequest = (Request)request;
        HttpChannel connection=jettyRequest.getHttpChannel();
        //AbstractHttpConnection connection = jettyRequest.getConnection();
        Socket socket = (Socket)connection.getEndPoint().getTransport();
        if (log.isLoggable(Level.FINE))
            log.fine("Checking if client connection is still open: " + socket.getRemoteSocketAddress());
        try {
            socket.getOutputStream().write(heartbeat);
            socket.getOutputStream().flush();
            return true;
        } catch (IOException ex) {
            if (log.isLoggable(Level.FINE))
                log.fine("Client connection has been closed: " + socket.getRemoteSocketAddress());
            return false;
        }
    }

}
