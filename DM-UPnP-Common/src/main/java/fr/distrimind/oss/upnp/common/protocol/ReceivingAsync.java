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

package fr.distrimind.oss.upnp.common.protocol;

import fr.distrimind.oss.upnp.common.transport.RouterException;
import fr.distrimind.oss.upnp.common.UpnpService;
import fr.distrimind.oss.upnp.common.model.message.UpnpMessage;
import fr.distrimind.oss.upnp.common.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.common.util.Exceptions;

import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Supertype for all asynchronously executing protocols, handling reception of UPnP messages.
 * <p>
 * After instantiation by the {@link ProtocolFactory}, this protocol <code>run()</code>s and
 * calls its own {@link #waitBeforeExecution()} method. By default, the protocol does not wait
 * before then proceeding with {@link #execute()}.
 * </p>
 *
 * @param <M> The type of UPnP message handled by this protocol.
 *
 * @author Christian Bauer
 */
public abstract class ReceivingAsync<M extends UpnpMessage<?>> implements Runnable {

    final private static DMLogger log = Log.getLogger(ReceivingAsync.class);

    private final UpnpService upnpService;

    private final M inputMessage;

    protected ReceivingAsync(UpnpService upnpService, M inputMessage) {
        this.upnpService = upnpService;
        this.inputMessage = inputMessage;
    }

    public UpnpService getUpnpService() {
        return upnpService;
    }

    public M getInputMessage() {
        return inputMessage;
    }

    @Override
	public void run() {
        boolean proceed;
        try {
            proceed = waitBeforeExecution();
        } catch (InterruptedException ex) {
            if (log.isInfoEnabled()) log.info("Protocol wait before execution interrupted (on shutdown?): " + getClass().getSimpleName());
            proceed = false;
        }

        if (proceed) {
            try {
                execute();
            } catch (Exception ex) {
                Throwable cause = Exceptions.unwrap(ex);
                if (cause instanceof InterruptedException) {
                    if (log.isInfoEnabled()) log.info("Interrupted protocol '" + getClass().getSimpleName() + "': " + ex, cause);
                } else {
                    throw new RuntimeException(
                        "Fatal error while executing protocol '" + getClass().getSimpleName() + "': ", ex
                    );
                }
            }
        }
    }

    /**
     * Provides an opportunity to pause before executing the protocol.
     *
     * @return <code>true</code> (default) if execution should continue after waiting.
     *
     * @throws InterruptedException If waiting has been interrupted, which also stops execution.
     */
    protected boolean waitBeforeExecution() throws InterruptedException {
        // Don't wait by default
        return true;
    }

    protected abstract void execute() throws RouterException;

    protected <H extends UpnpHeader<?>> H getFirstHeader(UpnpHeader.Type headerType, Class<H> subtype) {
        return getInputMessage().getHeaders().getFirstHeader(headerType, subtype);
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ")";
    }

}
