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
import fr.distrimind.oss.upnp.util.Exceptions;

import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.Log;

/**
 * Supertype for all synchronously executing protocols, sending UPnP messages.
 * <p>
 * After instantiation by the {@link ProtocolFactory}, this protocol <code>run()</code>s and
 * calls its {@link #execute()} method.
 * </p>
 * <p>
 * A {@link RouterException} during execution will be wrapped in a fatal <code>RuntimeException</code>,
 * unless its cause is an <code>InterruptedException</code>, in which case an INFO message will be logged.
 * </p>
 *
 * @author Christian Bauer
 */
public abstract class SendingAsync implements Runnable {

    final private static DMLogger log = Log.getLogger(SendingAsync.class);

    private final UpnpService upnpService;

    protected SendingAsync(UpnpService upnpService) {
        this.upnpService = upnpService;
    }

    public UpnpService getUpnpService() {
        return upnpService;
    }

    @Override
	public void run() {
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

    protected abstract void execute() throws RouterException;

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ")";
    }

}