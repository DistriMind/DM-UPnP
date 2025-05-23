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

import fr.distrimind.oss.upnp.common.transport.Router;
import fr.distrimind.oss.upnp.common.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.common.protocol.ProtocolCreationException;
import fr.distrimind.oss.upnp.common.protocol.ProtocolFactory;
import fr.distrimind.oss.upnp.common.protocol.ReceivingSync;
import fr.distrimind.oss.upnp.common.util.Exceptions;

import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * A runnable representation of a single HTTP request/response procedure.
 * <p>
 * Instantiated by the {@link StreamServer}, executed by the
 * {@link Router}. See the pseudo-code example
 * in the documentation of {@link StreamServer}. An implementation's
 * <code>run()</code> method has to call the {@link #process(StreamRequestMessage)},
 * {@link #responseSent(StreamResponseMessage)} and
 * {@link #responseException(Throwable)} methods.
 * </p>
 * <p>
 * An implementation does not have to be thread-safe.
 * </p>
 * @author Christian Bauer
 */
public abstract class UpnpStream implements Runnable {

    final private static DMLogger log = Log.getLogger(UpnpStream.class);

    protected final ProtocolFactory protocolFactory;
    protected ReceivingSync<?, ?> syncProtocol;

    protected UpnpStream(ProtocolFactory protocolFactory) {
        this.protocolFactory = protocolFactory;
    }

    public ProtocolFactory getProtocolFactory() {
        return protocolFactory;
    }

    /**
     * Selects a UPnP protocol, runs it within the calling thread, returns the response.
     * <p>
     * This method will return <code>null</code> if the UPnP protocol returned <code>null</code>.
     * The HTTP response in this case is always <em>404 NOT FOUND</em>. Any other (HTTP) error
     * condition will be encapsulated in the returned response message and has to be
     * passed to the HTTP client as it is.
   
     * @param requestMsg The TCP (HTTP) stream request message.
     * @return The TCP (HTTP) stream response message, or <code>null</code> if a 404 should be sent to the client.
     */
    public StreamResponseMessage process(StreamRequestMessage requestMsg) {
		if (log.isDebugEnabled()) {
            log.debug("Processing stream request message: " + requestMsg);
		}

		try {
            // Try to get a protocol implementation that matches the request message
            syncProtocol = getProtocolFactory().createReceivingSync(requestMsg);
        } catch (ProtocolCreationException ex) {
			if (log.isWarnEnabled()) log.warn("Processing stream request failed - ", Exceptions.unwrap(ex));
            return new StreamResponseMessage(UpnpResponse.Status.NOT_IMPLEMENTED);
        }

        // Run it
		if (log.isDebugEnabled()) {
            log.debug("Running protocol for synchronous message processing: " + syncProtocol);
		}
		syncProtocol.run();

        // ... then grab the response
        StreamResponseMessage responseMsg = syncProtocol.getOutputMessage();

        if (responseMsg == null) {
            // That's ok, the caller is supposed to handle this properly (e.g. convert it to HTTP 404)
            log.trace("Protocol did not return any response message");
            return null;
        }
		if (log.isTraceEnabled()) {
			log.trace("Protocol returned response: " + responseMsg);
		}
		return responseMsg;
    }

    /**
     * Must be called by a subclass after the response has been successfully sent to the client.
     *
     * @param responseMessage The response message successfully sent to the client.
     */
    protected void responseSent(StreamResponseMessage responseMessage) {
        if (syncProtocol != null)
            syncProtocol.responseSent(responseMessage);
    }

    /**
     * Must be called by a subclass if the response was not delivered to the client.
     *
     * @param t The reason why the response wasn't delivered.
     */
    protected void responseException(Throwable t) {
        if (syncProtocol != null)
            syncProtocol.responseException(t);
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ")";
    }
}
