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

package fr.distrimind.oss.upnp.transport;

import fr.distrimind.oss.upnp.model.message.IncomingDatagramMessage;
import fr.distrimind.oss.upnp.model.message.header.CallbackHeader;
import fr.distrimind.oss.upnp.model.message.header.HostHeader;
import fr.distrimind.oss.upnp.model.message.header.LocationHeader;
import fr.distrimind.oss.upnp.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.transport.spi.NetworkAddressFactory;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Jason Mahdjoub
 * @version 1.0
 * @since MaDKitLanEdition 1.0.0
 */

@SuppressWarnings("PMD.UseEnumCollections")
public class Common {
	private static final Set<UpnpHeader.Type> allowedUpnpHeaders=new HashSet<>(Arrays.asList(UpnpHeader.Type.EXT, UpnpHeader.Type.ST, UpnpHeader.Type.SERVER, UpnpHeader.Type.USN, UpnpHeader.Type.LOCATION, UpnpHeader.Type.MAX_AGE));
	static boolean isNotValidRemoteAddress(URL u, NetworkAddressFactory networkAddressFactory)
	{
		if (u==null)
			return false;
		return isNotValidRemoteAddress(u.getHost(), networkAddressFactory);
	}
	public static boolean isNotValidRemoteAddress(String host, NetworkAddressFactory networkAddressFactory)
	{
		try {
			InetAddress ia = InetAddress.getByName(host);
			ia = networkAddressFactory.getLocalAddress(
					null,
					ia instanceof Inet6Address,
					ia
			);
			if (ia == null)
				return true;
		} catch (Exception ignored) {
			return true;
		}
		return false;
	}
	public static IncomingDatagramMessage<?> getValidIncomingDatagramMessage(IncomingDatagramMessage<?> idm, NetworkAddressFactory networkAddressFactory)
	{
		for (UpnpHeader.Type t : UpnpHeader.Type.values()) {
			if (allowedUpnpHeaders.contains(t))
				continue;
			if (idm.getHeaders().containsKey(t))
				return null;
		}
		List<UpnpHeader<?>> luh=idm.getHeaders().get(UpnpHeader.Type.CALLBACK);

		if (luh!=null) {
			for (UpnpHeader<?> uh : luh) {
				if (CallbackHeader.class.isAssignableFrom(uh.getClass())) {
					CallbackHeader ch = (CallbackHeader) uh;
					for (URL u : ch.getValue()) {
						if (isNotValidRemoteAddress(u, networkAddressFactory))
							return null;
					}
				}
			}
		}
		luh=idm.getHeaders().get(UpnpHeader.Type.HOST);
		if (luh!=null) {
			for (UpnpHeader<?> uh : luh) {
				if (HostHeader.class.isAssignableFrom(uh.getClass())) {
					HostHeader hh = (HostHeader) uh;
					if (isNotValidRemoteAddress(hh.getValue().getHost(), networkAddressFactory))
						return null;
				}
			}
		}
		luh=idm.getHeaders().get(UpnpHeader.Type.LOCATION);
		if (luh!=null) {
			for (UpnpHeader<?> uh : luh) {
				if (LocationHeader.class.isAssignableFrom(uh.getClass())) {
					LocationHeader hh = (LocationHeader) uh;
					if (isNotValidRemoteAddress(hh.getValue().getHost(), networkAddressFactory))
						return null;
				}
			}
		}
		return idm;
	}
}
