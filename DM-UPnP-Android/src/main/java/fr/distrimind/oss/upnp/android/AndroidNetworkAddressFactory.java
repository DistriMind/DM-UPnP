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

package fr.distrimind.oss.upnp.android;

import fr.distrimind.oss.upnp.transport.impl.NetworkAddressFactoryImpl;
import fr.distrimind.oss.upnp.transport.spi.InitializationException;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.Log;

/**
 * This factory tries to work around and patch some Android bugs.
 *
 * @author Michael Pujos
 * @author Christian Bauer
 */
public class AndroidNetworkAddressFactory extends NetworkAddressFactoryImpl {

    final private static DMLogger log = Log.getLogger(AndroidNetworkAddressFactory.class);

    public AndroidNetworkAddressFactory(int streamListenPort, int multicastPort) {
        super(streamListenPort, multicastPort);
    }

    @Override
    protected boolean requiresNetworkInterface() {
        return false;
    }

    @Override
	@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    protected boolean isUsableAddress(NetworkInterface networkInterface, InetAddress address) {
		boolean result = super.isUsableAddress(networkInterface, address);
		if (result) {
			// TODO: Workaround Android DNS reverse lookup issue, still a problem on ICS+?
			// http://4thline.org/projects/mailinglists.html#nabble-td3011461
			String hostName = address.getHostAddress();

			Field field0;
			Object target;

			try {

				try {
					field0 = InetAddress.class.getDeclaredField("holder");
					field0.setAccessible(true);
					target = field0.get(address);
					field0 = target.getClass().getDeclaredField("hostName");
				} catch (NoSuchFieldException | InaccessibleObjectException e) {
					// Let's try the non-OpenJDK variant
					field0 = InetAddress.class.getDeclaredField("hostName");
					target = address;
				}

				if (target != null && hostName != null) {
					field0.setAccessible(true);
					field0.set(target, hostName);
				} else {
					return false;
				}

			}
			catch (NoSuchFieldException | InaccessibleObjectException ignored) {

			}
			catch (Exception ex) {
				if (log.isErrorEnabled())
					log.error(
							"Failed injecting hostName to work around Android InetAddress DNS bug: " + address,
							ex
					);
				return false;
			}
		}
		return result;
	}

    @Override
    public InetAddress getLocalAddress(NetworkInterface networkInterface, boolean isIPv6, InetAddress remoteAddress) {
        // TODO: This is totally random because we can't access low level InterfaceAddress on Android!
		for (NetworkInterface ni : (networkInterface==null?networkInterfaces: Collections.singletonList(networkInterface))) {
			for (InetAddress localAddress : getInetAddresses(ni)) {
				if (isIPv6 && localAddress instanceof Inet6Address)
					return localAddress;
				if (!isIPv6 && localAddress instanceof Inet4Address)
					return localAddress;
			}
		}
        throw new IllegalStateException("Can't find any IPv4 or IPv6 address on interface: " + (networkInterface==null?"null":networkInterface.getDisplayName()));
    }

    @Override
    protected void discoverNetworkInterfaces() throws InitializationException {
        try {
            super.discoverNetworkInterfaces();
        } catch (Exception ex) {
            // TODO: ICS bug on some models with network interface disappearing while enumerated
            // http://code.google.com/p/android/issues/detail?id=33661
			if (log.isWarnEnabled())
            	log.warn("Exception while enumerating network interfaces, trying once more: ", ex);
            super.discoverNetworkInterfaces();
        }
    }
}
