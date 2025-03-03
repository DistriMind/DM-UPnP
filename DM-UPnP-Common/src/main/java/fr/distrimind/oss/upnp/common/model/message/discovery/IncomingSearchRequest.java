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

package fr.distrimind.oss.upnp.common.model.message.discovery;

import fr.distrimind.oss.upnp.common.model.message.IncomingDatagramMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpRequest;
import fr.distrimind.oss.upnp.common.model.message.header.MANHeader;
import fr.distrimind.oss.upnp.common.model.message.header.MXHeader;
import fr.distrimind.oss.upnp.common.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.common.model.types.NotificationSubtype;

/**
 * @author Christian Bauer
 */
public class IncomingSearchRequest extends IncomingDatagramMessage<UpnpRequest> {

    public IncomingSearchRequest(IncomingDatagramMessage<UpnpRequest> source) {
        super(source);
    }

    public UpnpHeader<?> getSearchTarget() {
        return getHeaders().getFirstHeader(UpnpHeader.Type.ST);
    }

    public Integer getMX() {
        MXHeader header = getHeaders().getFirstHeader(UpnpHeader.Type.MX, MXHeader.class);
        if (header != null) {
            return header.getValue();
        }
        return null;
    }

    /**
     * @return <code>true</code> if this message has a MAN with
     *         value {@link NotificationSubtype#DISCOVER}.
     */
    public boolean isMANSSDPDiscover() {
        MANHeader header = getHeaders().getFirstHeader(UpnpHeader.Type.MAN, MANHeader.class);
        return header != null && header.getValue().equals(NotificationSubtype.DISCOVER.getHeaderString());
    }

}
