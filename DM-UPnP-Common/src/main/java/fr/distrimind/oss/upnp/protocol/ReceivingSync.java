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

package fr.distrimind.oss.upnp.protocol;

import fr.distrimind.oss.upnp.transport.RouterException;
import fr.distrimind.oss.upnp.UpnpService;
import fr.distrimind.oss.upnp.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.model.profile.RemoteClientInfo;

import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.Log;

/**
 * Supertype for all synchronously executing protocols, handling reception of UPnP messages and return a response.
 * <p>
 * After instantiation by the {@link ProtocolFactory}, this protocol <code>run()</code>s and
 * calls its own {@link #waitBeforeExecution()} method. By default, the protocol does not wait
 * before then proceeding with {@link #executeSync()}.
 * </p>
 * <p>
 * The returned response will be available to the client of this protocol. The
 * client will then call either {@link #responseSent(StreamResponseMessage)}
 * or {@link #responseException(Throwable)}, depending on whether the response was successfully
 * delivered. The protocol can override these methods to decide if the whole procedure it is
 * implementing was successful or not, including not only creation but also delivery of the response.
 * </p>
 *
 * @param <IN> The type of incoming UPnP message handled by this protocol.
 * @param <OUT> The type of response UPnP message created by this protocol.
 *
 * @author Christian Bauer
 */
public abstract class ReceivingSync<IN extends StreamRequestMessage, OUT extends StreamResponseMessage> extends ReceivingAsync<IN> {

    final private static DMLogger log = Log.getLogger(ReceivingSync.class);

    final protected RemoteClientInfo remoteClientInfo;
    protected OUT outputMessage;

    protected ReceivingSync(UpnpService upnpService, IN inputMessage) {
        super(upnpService, inputMessage);
        this.remoteClientInfo = new RemoteClientInfo(inputMessage);
    }

    public OUT getOutputMessage() {
        return outputMessage;
    }

    @Override
	final protected void execute() throws RouterException {
        outputMessage = executeSync();

        if (outputMessage != null && !getRemoteClientInfo().getExtraResponseHeaders().isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("Setting extra headers on response message: " + getRemoteClientInfo().getExtraResponseHeaders().size());
			}
			outputMessage.getHeaders().putAll(getRemoteClientInfo().getExtraResponseHeaders());
        }
    }

    protected abstract OUT executeSync() throws RouterException;

    /**
     * Called by the client of this protocol after the returned response has been successfully delivered.
     * <p>
     * NOOP by default.
   
     */
    public void responseSent(StreamResponseMessage responseMessage) {
    }

    /**
     * Called by the client of this protocol if the returned response was not delivered.
     * <p>
     * NOOP by default.
   
     *
     * @param t The reason why the response wasn't delivered.
     */
    public void responseException(Throwable t) {
    }

    public RemoteClientInfo getRemoteClientInfo() {
        return remoteClientInfo;
    }


}
