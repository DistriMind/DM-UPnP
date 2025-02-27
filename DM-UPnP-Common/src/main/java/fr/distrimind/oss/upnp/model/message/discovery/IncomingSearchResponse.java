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

import fr.distrimind.oss.upnp.model.message.IncomingDatagramMessage;
import fr.distrimind.oss.upnp.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.model.message.header.DeviceUSNHeader;
import fr.distrimind.oss.upnp.model.message.header.InterfaceMacHeader;
import fr.distrimind.oss.upnp.model.message.header.LocationHeader;
import fr.distrimind.oss.upnp.model.message.header.MaxAgeHeader;
import fr.distrimind.oss.upnp.model.message.header.ServiceUSNHeader;
import fr.distrimind.oss.upnp.model.message.header.UDNHeader;
import fr.distrimind.oss.upnp.model.message.header.USNRootDeviceHeader;
import fr.distrimind.oss.upnp.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.model.types.NamedDeviceType;
import fr.distrimind.oss.upnp.model.types.NamedServiceType;
import fr.distrimind.oss.upnp.model.types.UDN;

import java.net.URL;

/**
 * @author Christian Bauer
 */
public class IncomingSearchResponse extends IncomingDatagramMessage<UpnpResponse> {

    public IncomingSearchResponse(IncomingDatagramMessage<UpnpResponse> source) {
        super(source);
    }

    public boolean isSearchResponseMessage() {
        UpnpHeader<?> st = getHeaders().getFirstHeader(UpnpHeader.Type.ST);
        UpnpHeader<?> usn = getHeaders().getFirstHeader(UpnpHeader.Type.USN);
        UpnpHeader<?> ext = getHeaders().getFirstHeader(UpnpHeader.Type.EXT); // Has no value!
        return st != null && st.getValue() != null && usn != null && usn.getValue() != null && ext != null;
    }

    public UDN getRootDeviceUDN() {
        // This processes the headers as specified in UDA 1.0, tables in section 1.1.12

        UpnpHeader<UDN> udnHeader = getHeaders().getFirstHeader(UpnpHeader.Type.USN, USNRootDeviceHeader.class);
        if (udnHeader != null) return udnHeader.getValue();

        udnHeader = getHeaders().getFirstHeader(UpnpHeader.Type.USN, UDNHeader.class);
        if (udnHeader != null) return udnHeader.getValue();

        UpnpHeader<NamedDeviceType> deviceTypeHeader = getHeaders().getFirstHeader(UpnpHeader.Type.USN, DeviceUSNHeader.class);
        if (deviceTypeHeader != null) return deviceTypeHeader.getValue().getUdn();

        UpnpHeader<NamedServiceType> serviceTypeHeader = getHeaders().getFirstHeader(UpnpHeader.Type.USN, ServiceUSNHeader.class);
        if (serviceTypeHeader != null) return serviceTypeHeader.getValue().getUdn();

        return null;
    }

    public URL getLocationURL() {
        LocationHeader header = getHeaders().getFirstHeader(UpnpHeader.Type.LOCATION, LocationHeader.class);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }

    public Integer getMaxAge() {
        MaxAgeHeader header = getHeaders().getFirstHeader(UpnpHeader.Type.MAX_AGE, MaxAgeHeader.class);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }
    @SuppressWarnings("PMD.ReturnEmptyCollectionRatherThanNull")
    public byte[] getInterfaceMacHeader() {
        InterfaceMacHeader header = getHeaders().getFirstHeader(UpnpHeader.Type.EXT_IFACE_MAC, InterfaceMacHeader.class);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }

}
