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

package fr.distrimind.oss.upnp.common.protocol.async;

import fr.distrimind.oss.upnp.common.protocol.ReceivingAsync;
import fr.distrimind.oss.upnp.common.protocol.RetrieveRemoteDescriptors;
import fr.distrimind.oss.upnp.common.transport.RouterException;
import fr.distrimind.oss.upnp.common.UpnpService;
import fr.distrimind.oss.upnp.common.model.ValidationError;
import fr.distrimind.oss.upnp.common.model.ValidationException;
import fr.distrimind.oss.upnp.common.model.message.IncomingDatagramMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpRequest;
import fr.distrimind.oss.upnp.common.model.message.discovery.IncomingNotificationRequest;
import fr.distrimind.oss.upnp.common.model.meta.RemoteDevice;
import fr.distrimind.oss.upnp.common.model.meta.RemoteDeviceIdentity;
import fr.distrimind.oss.upnp.common.model.types.UDN;

import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Handles reception of notification messages.
 * <p>
 * First, the UDN is created from the received message.
 * </p>
 * <p>
 * If an <em>ALIVE</em> message has been received, a new background process will be started
 * running {@link RetrieveRemoteDescriptors}.
 * </p>
 * <p>
 * If a <em>BYEBYE</em> message has been received, the device will be removed from the registry
 * directly.
 * </p>
 * <p>
 * The following was added to the UDA 1.1 spec (in 1.3), clarifying the handling of messages:
 * </p>
 * <p>
 * "If a control point has received at least one 'byebye' message of a root device, embedded device, or
 * service, then the control point can assume that all are no longer available."
 * </p>
 * <p>
 * Of course, they contradict this a little later:
 * </p>
 * <p>
 * "Only when all original advertisements of a root device, embedded device, and services have
 * expired can a control point assume that they are no longer available."
 * </p>
 * <p>
 * This could mean that even if we get 'byeby'e for the root device, we still have to assume that its services
 * are available. That clearly makes no sense at all and I think it's just badly worded and relates to the
 * previous sentence wich says "if you don't get byebye's, rely on the expiration timeout". It does not
 * imply that a service or embedded device lives beyond its root device. It actually reinforces that we are
 * free to ignore anything that happens as long as the root device is not gone with 'byebye' or has expired.
 * In other words: There is no reason at all why SSDP sends dozens of messages for all embedded devices and
 * services. The composite is the root device and the composite defines the lifecycle of all.
 * </p>
 *
 * @author Christian Bauer
 */
public class ReceivingNotification extends ReceivingAsync<IncomingNotificationRequest> {

    final private static DMLogger log = Log.getLogger(ReceivingNotification.class);

    public ReceivingNotification(UpnpService upnpService, IncomingDatagramMessage<UpnpRequest> inputMessage) {
        super(upnpService, new IncomingNotificationRequest(inputMessage));
    }

    @Override
	protected void execute() throws RouterException {

        UDN udn = getInputMessage().getUDN();
        if (udn == null) {
			if (log.isDebugEnabled()) {
				log.debug("Ignoring notification message without UDN: " + getInputMessage());
			}
			return;
        }

        RemoteDeviceIdentity rdIdentity = new RemoteDeviceIdentity(getInputMessage());
		if (log.isDebugEnabled()) {
            log.debug("Received device notification: " + rdIdentity);
		}

		RemoteDevice rd;
        try {
            rd = new RemoteDevice(rdIdentity);
        } catch (ValidationException ex) {
			if (log.isWarnEnabled()) log.warn("Validation errors of device during discovery: " + rdIdentity);
            for (ValidationError validationError : ex.getErrors()) {
				if (log.isWarnEnabled()) log.warn(validationError.toString());
            }
            return;
        }

        if (getInputMessage().isAliveMessage()) {

			if (log.isDebugEnabled()) {
				log.debug("Received device ALIVE advertisement, descriptor location is: " + rdIdentity.getDescriptorURL());
			}

			if (rdIdentity.getDescriptorURL() == null) {
				if (log.isTraceEnabled()) {
					log.trace("Ignoring message without location URL header: " + getInputMessage());
				}
				return;
            }

            if (rdIdentity.getMaxAgeSeconds() == null) {
				if (log.isTraceEnabled()) {
					log.trace("Ignoring message without max-age header: " + getInputMessage());
				}
				return;
            }

            if (getUpnpService().getRegistry().update(rdIdentity)) {
				if (log.isTraceEnabled()) {
					log.trace("Remote device was already known: " + udn);
				}
				return;
            }

            // Unfortunately, we always have to retrieve the descriptor because at this point we
            // have no idea if it's a root or embedded device
            getUpnpService().getConfiguration().getAsyncProtocolExecutor().execute(
                    new RetrieveRemoteDescriptors(getUpnpService(), rd)
            );

        } else if (getInputMessage().isByeByeMessage()) {

            log.debug("Received device BYEBYE advertisement");
            boolean removed = getUpnpService().getRegistry().removeDevice(rd);
            if (removed) {
				if (log.isDebugEnabled()) {
					log.debug("Removed remote device from registry: " + rd);
				}
			}

        } else {
			if (log.isTraceEnabled()) {
				log.trace("Ignoring unknown notification message: " + getInputMessage());
			}
		}

    }


}
