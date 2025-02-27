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

package fr.distrimind.oss.upnp.model.message.discovery;

import fr.distrimind.oss.upnp.model.Constants;
import fr.distrimind.oss.upnp.model.Location;
import fr.distrimind.oss.upnp.model.message.IncomingDatagramMessage;
import fr.distrimind.oss.upnp.model.message.OutgoingDatagramMessage;
import fr.distrimind.oss.upnp.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.model.message.header.EXTHeader;
import fr.distrimind.oss.upnp.model.message.header.InterfaceMacHeader;
import fr.distrimind.oss.upnp.model.message.header.LocationHeader;
import fr.distrimind.oss.upnp.model.message.header.MaxAgeHeader;
import fr.distrimind.oss.upnp.model.message.header.ServerHeader;
import fr.distrimind.oss.upnp.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.model.meta.LocalDevice;

/**
 * @author Christian Bauer
 */
public class OutgoingSearchResponse extends OutgoingDatagramMessage<UpnpResponse> {

    public OutgoingSearchResponse(IncomingDatagramMessage<?> request,
								  Location location,
								  LocalDevice<?> device) {

        super(new UpnpResponse(UpnpResponse.Status.OK), request.getSourceAddress(), request.getSourcePort());

        getHeaders().add(UpnpHeader.Type.MAX_AGE, new MaxAgeHeader(device.getIdentity().getMaxAgeSeconds()));
        getHeaders().add(UpnpHeader.Type.LOCATION, new LocationHeader(location.getURL()));
        getHeaders().add(UpnpHeader.Type.SERVER, new ServerHeader());
        getHeaders().add(UpnpHeader.Type.EXT, new EXTHeader());

        if ("true".equals(System.getProperty(Constants.SYSTEM_PROPERTY_ANNOUNCE_MAC_ADDRESS))
            && location.getNetworkAddress().getHardwareAddress() != null) {
            getHeaders().add(
                UpnpHeader.Type.EXT_IFACE_MAC,
                new InterfaceMacHeader(location.getNetworkAddress().getHardwareAddress())
            );
        }
    }

}
