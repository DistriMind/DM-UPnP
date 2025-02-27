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

import fr.distrimind.oss.upnp.transport.Router;
import fr.distrimind.oss.upnp.android.transport.spi.ServletContainerAdapter;
import fr.distrimind.oss.upnp.transport.spi.StreamServerConfiguration;
import fr.distrimind.oss.upnp.transport.spi.UpnpStream;

/**
 * Settings for the async Servlet 3.0 implementation.
 * <p>
 * If you are trying to integrate DM-UPnP with an existing/running servlet
 * container, implement {@link ServletContainerAdapter}.
 * </p>
 *
 * @author Christian Bauer
 */
public class AsyncServletStreamServerConfigurationImpl implements StreamServerConfiguration {

    protected ServletContainerAdapter servletContainerAdapter;
    protected int listenPort = 0;
    protected int asyncTimeoutSeconds = 60;

    /**
     * Defaults to port '0', ephemeral.
     */
    public AsyncServletStreamServerConfigurationImpl(ServletContainerAdapter servletContainerAdapter) {
        this.servletContainerAdapter = servletContainerAdapter;
    }

    public AsyncServletStreamServerConfigurationImpl(ServletContainerAdapter servletContainerAdapter,
													 int listenPort) {
        this.servletContainerAdapter = servletContainerAdapter;
        this.listenPort = listenPort;
    }

    public AsyncServletStreamServerConfigurationImpl(ServletContainerAdapter servletContainerAdapter,
													 int listenPort,
													 int asyncTimeoutSeconds) {
        this.servletContainerAdapter = servletContainerAdapter;
        this.listenPort = listenPort;
        this.asyncTimeoutSeconds = asyncTimeoutSeconds;
    }

    /**
     * @return Defaults to <code>0</code>.
     */
    @Override
	public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    /**
     * The time in seconds this server wait for the {@link Router}
     * to execute a {@link UpnpStream}.
     *
     * @return The default of 60 seconds.
     */
    public int getAsyncTimeoutSeconds() {
        return asyncTimeoutSeconds;
    }

    public void setAsyncTimeoutSeconds(int asyncTimeoutSeconds) {
        this.asyncTimeoutSeconds = asyncTimeoutSeconds;
    }

    public ServletContainerAdapter getServletContainerAdapter() {
        return servletContainerAdapter;
    }

    public void setServletContainerAdapter(ServletContainerAdapter servletContainerAdapter) {
        this.servletContainerAdapter = servletContainerAdapter;
    }
}
