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

package fr.distrimind.oss.upnp.protocol.async;

import fr.distrimind.oss.upnp.transport.RouterException;
import fr.distrimind.oss.upnp.UpnpService;
import fr.distrimind.oss.upnp.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.model.types.NotificationSubtype;

import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.Log;

/**
 * Sending <em>BYEBYE</em> notification messages for a registered local device.
 *
 * @author Christian Bauer
 */
public class SendingNotificationByebye extends SendingNotification {

    final private static DMLogger log = Log.getLogger(SendingNotificationByebye.class);

    public SendingNotificationByebye(UpnpService upnpService, LocalDevice<?> device) {
        super(upnpService, device);
    }

    // The UDA 1.0 spec says "a message corresponding to /each/ of the ssd:alive messages" but
    // it's not clear if that means the "required" messages according to the tables only or if
    // it includes the triple (or whatever) repeated messages that have been sent to protect
    // against networking problems. It also says, a little later, that "each of the messages should
    // be sent more than once". So we are also sending them three times - hell, why not pollute the
    // network with useless stuff, that is going to make this more reliable for sure...

    // In other words: The superclass method is fine even for byebye.

    @Override
    protected void execute() throws RouterException {
		if (log.isDebugEnabled()) {
            log.debug("Sending byebye messages ("+getBulkRepeat()+" times) for: " + getDevice());
		}
		super.execute();
    }

    @Override
	protected NotificationSubtype getNotificationSubtype() {
        return NotificationSubtype.BYEBYE;
    }

}