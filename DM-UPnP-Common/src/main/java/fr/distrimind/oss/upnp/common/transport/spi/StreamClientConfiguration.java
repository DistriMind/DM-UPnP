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

import fr.distrimind.oss.upnp.common.UpnpServiceConfiguration;

import java.util.concurrent.ExecutorService;

/**
 * Collection of typically needed configuration settings.
 *
 * @author Christian Bauer
 */
public interface StreamClientConfiguration {

    /**
     * Used to execute the actual HTTP request, the StreamClient waits on the "current" thread for
     * completion or timeout. You probably want to use the same executor service for both, so usually
     * this is {@link UpnpServiceConfiguration#getSyncProtocolExecutorService()}.
     *
     * @return The <code>ExecutorService</code> to use for actual sending of HTTP requests.
     */
	ExecutorService getRequestExecutorService();

    /**
     * @return The number of seconds to wait for a request to expire, spanning connect and data-reads.
     */
	int getTimeoutSeconds();

    /**
     * @return If the request completion takes longer than this, a warning will be logged (<code>0</code> to disable)
     */
	int getLogWarningSeconds();

    /**
     * Used for outgoing HTTP requests if no other value was already set on messages.
     *
     * @param majorVersion The UPnP UDA major version.
     * @param minorVersion The UPnP UDA minor version.
     * @return The HTTP user agent value.
     */
	String getUserAgentValue(int majorVersion, int minorVersion);

}
