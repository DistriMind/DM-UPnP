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

package fr.distrimind.oss.upnp.common.model.message.header;

import fr.distrimind.oss.upnp.common.model.Constants;
import fr.distrimind.oss.upnp.common.model.types.HostPort;

/**
 * @author Christian Bauer
 */
public class HostHeader extends UpnpHeader<HostPort> {

    int port = Constants.UPNP_MULTICAST_PORT;
    String group = Constants.IPV4_UPNP_MULTICAST_GROUP;

    public HostHeader() {
        setValue(new HostPort(group, port));
    }

    public HostHeader(int port) {
        setValue(new HostPort(group, port));
    }

    public HostHeader(String host, int port) {
        setValue(new HostPort(host, port));
    }

    @Override
	public void setString(String s) throws InvalidHeaderException {
        // UDA 1.1/1.0 section 1.2.2
        if (s.contains(":")) {
            // We have a port in the header, so we have to use that instead of the UDA default
            try {
                this.port = Integer.parseInt(s.substring(s.indexOf(":")+1));
                this.group = s.substring(0, s.indexOf(":"));
                setValue(new HostPort(group, port));
            } catch (NumberFormatException ex) {
                throw new InvalidHeaderException("Invalid HOST header value, can't parse port: " + s + " - " + ex.getMessage());
            }
        } else {
            this.group = s;
            setValue(new HostPort(group, port));
        }
    }

    @Override
	public String getString() {
        return getValue().toString();
    }
}