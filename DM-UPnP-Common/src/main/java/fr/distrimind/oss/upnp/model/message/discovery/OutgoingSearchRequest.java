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
import fr.distrimind.oss.upnp.model.ModelUtil;
import fr.distrimind.oss.upnp.model.message.OutgoingDatagramMessage;
import fr.distrimind.oss.upnp.model.message.UpnpRequest;
import fr.distrimind.oss.upnp.model.message.header.HostHeader;
import fr.distrimind.oss.upnp.model.message.header.MANHeader;
import fr.distrimind.oss.upnp.model.message.header.MXHeader;
import fr.distrimind.oss.upnp.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.model.types.NotificationSubtype;

/**
 * @author Christian Bauer
 */
public class OutgoingSearchRequest extends OutgoingDatagramMessage<UpnpRequest> {

    private final UpnpHeader<?> searchTarget;

    public OutgoingSearchRequest(UpnpHeader<?> searchTarget, int mxSeconds) {
        super(
                new UpnpRequest(UpnpRequest.Method.MSEARCH),
                ModelUtil.getInetAddressByName(Constants.IPV4_UPNP_MULTICAST_GROUP),
                Constants.UPNP_MULTICAST_PORT
        );

        this.searchTarget = searchTarget;

        getHeaders().add(UpnpHeader.Type.MAN, new MANHeader(NotificationSubtype.DISCOVER.getHeaderString()));
        getHeaders().add(UpnpHeader.Type.MX, new MXHeader(mxSeconds));
        getHeaders().add(UpnpHeader.Type.ST, searchTarget);
        getHeaders().add(UpnpHeader.Type.HOST, new HostHeader());
    }

    public UpnpHeader<?> getSearchTarget() {
        return searchTarget;
    }
}