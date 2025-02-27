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

import fr.distrimind.oss.upnp.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.model.message.StreamResponseMessage;

/**
 * Service for sending TCP (HTTP) stream request messages.
 * 
 * <p>
 * An implementation has to be thread-safe.
 * Its constructor may throw {@link InitializationException}.
 * </p>
 *
 * @param <C> The type of the service's configuration.
 *
 * @author Christian Bauer
 */
public interface StreamClient<C extends StreamClientConfiguration> {

    /**
     * Sends the given request via TCP (HTTP) and returns the response.
     *
     * <p>
     * This method must implement expiration of timed out requests using the
     * {@link StreamClientConfiguration} settings. When a request expires, a
     * <code>null</code> response will be returned.
   
     * <p>
     * This method will always try to complete execution without throwing an exception. It will
     * return <code>null</code> if an error occurs, and optionally log any exception messages.
   
     * <p>
     * The rules for logging are:
   
     * <ul>
     *     <li>If the caller interrupts the calling thread, log at <code>FINE</code>.</li>
     *     <li>If the request expires because the timeout has been reached, log at <code>INFO</code> level.</li>
     *     <li>If another error occurs, log at <code>WARNING</code> level</li>
     * </ul>
     * <p>
     * This method <strong>is required</strong> to add a <code>Host</code> HTTP header to the
     * outgoing HTTP request, even if the given
     * {@link StreamRequestMessage} does not contain such a header.
   
     * <p>
     * This method will add the <code>User-Agent</code> HTTP header to the outgoing HTTP request if
     * the given message did not already contain such a header. You can set this default value in your
     * {@link StreamClientConfiguration}.
   
     *
     * @param message The message to send.
     * @return The response or <code>null</code> if no response has been received or an error occurred.
     * @throws InterruptedException if you interrupt the calling thread.
     */
	StreamResponseMessage sendRequest(StreamRequestMessage message) throws InterruptedException;

    /**
     * Stops the service, closes any connection pools etc.
     */
	void stop();

    /**
     * @return This service's configuration.
     */
	C getConfiguration();

}
