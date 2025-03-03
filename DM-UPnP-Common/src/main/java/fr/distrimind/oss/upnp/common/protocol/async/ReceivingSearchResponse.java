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
import fr.distrimind.oss.upnp.common.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.common.model.message.discovery.IncomingSearchResponse;
import fr.distrimind.oss.upnp.common.model.meta.RemoteDevice;
import fr.distrimind.oss.upnp.common.model.meta.RemoteDeviceIdentity;
import fr.distrimind.oss.upnp.common.model.types.UDN;

import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Handles reception of search response messages.
 * <p>
 * This protocol implementation is basically the same as
 * the {@link ReceivingNotification} protocol for
 * an <em>ALIVE</em> message.
 * </p>
 *
 * @author Christian Bauer
 */
public class ReceivingSearchResponse extends ReceivingAsync<IncomingSearchResponse> {

    final private static DMLogger log = Log.getLogger(ReceivingSearchResponse.class);

    public ReceivingSearchResponse(UpnpService upnpService, IncomingDatagramMessage<UpnpResponse> inputMessage) {
        super(upnpService, new IncomingSearchResponse(inputMessage));
    }

    @Override
	protected void execute() throws RouterException {

        if (!getInputMessage().isSearchResponseMessage()) {
			if (log.isDebugEnabled()) {
				log.debug("Ignoring invalid search response message: " + getInputMessage());
			}
			return;
        }

        UDN udn = getInputMessage().getRootDeviceUDN();
        if (udn == null) {
			if (log.isDebugEnabled()) {
				log.debug("Ignoring search response message without UDN: " + getInputMessage());
			}
			return;
        }

        RemoteDeviceIdentity rdIdentity = new RemoteDeviceIdentity(getInputMessage());
		if (log.isDebugEnabled()) {
            log.debug("Received device search response: " + rdIdentity);
		}

		if (getUpnpService().getRegistry().update(rdIdentity)) {
			if (log.isDebugEnabled()) {
				log.debug("Remote device was already known: " + udn);
			}
			return;
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

        // Unfortunately, we always have to retrieve the descriptor because at this point we
        // have no idea if it's a root or embedded device
        getUpnpService().getConfiguration().getAsyncProtocolExecutor().execute(
                new RetrieveRemoteDescriptors(getUpnpService(), rd)
        );

    }

}
