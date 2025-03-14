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

package fr.distrimind.oss.upnp.common.model;

import fr.distrimind.oss.upnp.common.util.Reflections;

import java.lang.reflect.Method;
import java.net.*;
import java.util.*;

/**
 * Shared trivial procedures.
 *
 * @author Christian Bauer
 */
public class ModelUtil {

    /**
     * True if this class is executing on an Android runtime
     */
    final public static boolean ANDROID_RUNTIME;
    static {
        boolean foundAndroid = false;
        try {
            Class<?> androidBuild = Thread.currentThread().getContextClassLoader().loadClass("android.os.Build");
            foundAndroid = androidBuild.getField("ID").get(null) != null;
        } catch (Exception ignored) {
            // Ignore
        }
        ANDROID_RUNTIME = foundAndroid;
    }

    /**
     * True if this class is executing on an Android emulator runtime.
     */
    final public static boolean ANDROID_EMULATOR;
    static {
        boolean foundEmulator = false;
        try {
            Class<?> androidBuild = Thread.currentThread().getContextClassLoader().loadClass("android.os.Build");
            String product = (String)androidBuild.getField("PRODUCT").get(null);
            if ("google_sdk".equals(product) || ("sdk".equals(product)))
                foundEmulator = true;
        } catch (Exception ignored) {
            // Ignore
        }
        ANDROID_EMULATOR = foundEmulator;
    }

    /**
     * @param stringConvertibleTypes A collection of interfaces.
     * @param clazz An interface to test.
     * @return <code>true</code> if the given interface is an Enum, or if the collection contains a super-interface.
     */
    public static boolean isStringConvertibleType(Set<Class<?>> stringConvertibleTypes, Class<?> clazz) {
        if (clazz.isEnum()) return true;
        for (Class<?> toStringOutputType : stringConvertibleTypes) {
            if (toStringOutputType.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param name A UPnP device architecture "name" string.
     * @return <code>true</code> if the name is not empty, doesn't start with "xml", and
     *         matches {@link Constants#getPatternUDAName()}.
     */
    public static boolean isValidUDAName(String name) {
        return name != null && !name.isEmpty() && !name.toLowerCase(Locale.ROOT).startsWith("xml") && Constants.getPatternUDAName().matcher(name).matches();
    }

    /**
     * Wraps the checked exception in a runtime exception.
     */
    public static InetAddress getInetAddressByName(String name) {
        try {
            return InetAddress.getByName(name);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Converts the given instances into comma-separated elements of a string,
     * escaping commas with backslashes.
     */
    public static String toCommaSeparatedList(List<?> o) {
        return toCommaSeparatedList(o, true, false);
    }

    /**
     * Converts the given instances into comma-separated elements of a string,
     * optionally escapes commas and double quotes with backslahses.
     */
    public static String toCommaSeparatedList(List<?> o, boolean escapeCommas, boolean escapeDoubleQuotes) {
        if (o == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object obj : o) {
            String objString = obj.toString();
            objString = objString.replaceAll("\\\\", "\\\\\\\\"); // Replace one backslash with two (nice, eh?)
            if (escapeCommas) {
                objString = objString.replaceAll(",", "\\\\,");
            }
            if (escapeDoubleQuotes) {
                objString = objString.replaceAll("\"", "\\\"");
            }
            sb.append(objString).append(",");
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();

    }

    /**
     * Converts the comma-separated elements of a string into an array of strings,
     * unescaping backslashed commas.
     */
    public static String[] fromCommaSeparatedList(String s) {
        return fromCommaSeparatedList(s, true);
    }

    /**
     * Converts the comma-separated elements of a string into an array of strings,
     * optionally unescaping backslashed commas.
     */
    @SuppressWarnings("PMD.ReturnEmptyCollectionRatherThanNull")
    public static String[] fromCommaSeparatedList(String _s, boolean unescapeCommas) {
        if (_s == null || _s.isEmpty()) {
            return null;
        }

        final String QUOTED_COMMA_PLACEHOLDER = "XXX1122334455XXX";
        String s;
        if (unescapeCommas) {
            s = _s.replaceAll("\\\\,", QUOTED_COMMA_PLACEHOLDER);
        }
        else
            s=_s;

        String[] split = s.split(",");
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].replaceAll(QUOTED_COMMA_PLACEHOLDER, ",");
            split[i] = split[i].replaceAll("\\\\\\\\", "\\\\");
        }
        return split;
    }

    /**
     * @param seconds The number of seconds to convert.
     * @return A string representing hours, minutes, seconds, e.g. <code>11:23:44</code>
     */
    public static String toTimeString(long seconds) {
        long hours = seconds / 3600,
                remainder = seconds % 3600,
                minutes = remainder / 60,
                secs = remainder % 60;

        return ((hours < 10 ? "0" : "") + hours
                + ":" + (minutes < 10 ? "0" : "") + minutes
                + ":" + (secs < 10 ? "0" : "") + secs);
    }

    /**
     * @param _s A string representing hours, minutes, seconds, e.g. <code>11:23:44</code>
     * @return The converted number of seconds.
     */
    public static long fromTimeString(String _s) {
        // Handle "00:00:00.000" pattern, drop the milliseconds
        String s;
        if (_s.lastIndexOf(".") != -1)
            s = _s.substring(0, _s.lastIndexOf("."));
        else
            s=_s;
        String[] split = s.split(":");
        if (split.length != 3)
            throw new IllegalArgumentException("Can't parse time string: " + s);
        return (Long.parseLong(split[0]) * 3600) +
                (Long.parseLong(split[1]) * 60) +
                (Long.parseLong(split[2]));
    }

    /**
     * @param s A string with commas.
     * @return The same string, a newline appended after every comma.
     */
    public static String commaToNewline(String s) {
        StringBuilder sb = new StringBuilder();
        String[] split = s.split(",");
        for (String splitString : split) {
            sb.append(splitString).append(",").append("\n");
        }
        if (sb.length() > 2) {
            sb.deleteCharAt(sb.length() - 2);
        }
        return sb.toString();
    }

    /**
     * DNS reverse name lookup.
     *
     * @param includeDomain <code>true</code> if the whole FQDN should be returned, instead of just the first (host) part.
     * @return The resolved host (and domain-) name, or "UNKNOWN HOST" if resolution failed.
     */
    public static String getLocalHostName(boolean includeDomain) {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            return includeDomain
                    ? hostname
                    : hostname.contains(".") ? hostname.substring(0, hostname.indexOf(".")) : hostname;

        } catch (Exception ex) {
            // Return a dummy String
            return "UNKNOWN HOST";
        }
    }

    /**
     * @return The MAC hardware address of the first network interface of this host.
     */
    public static byte[] getFirstNetworkInterfaceHardwareAddress() {
        try {
            Enumeration<NetworkInterface> interfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface iface : Collections.list(interfaceEnumeration)) {
                if (isValidInterface(iface)) {
                    return iface.getHardwareAddress();
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not discover first network interface hardware address");
        }
        throw new RuntimeException("Could not discover first network interface hardware address");
    }
    private static boolean isValidInterface(NetworkInterface ni) throws SocketException {
        return !ni.isLoopback() && ni.isUp() && ni.getHardwareAddress()!=null;
    }
    public static boolean isLocalAddress(InetAddress inetAddress)
    {
        return inetAddress.isLoopbackAddress() || inetAddress.isLinkLocalAddress() || inetAddress.isSiteLocalAddress();
    }
    public static boolean isLocalAddressReachableFromThisMachine(InetAddress inetAddress) throws SocketException {
        if (!isLocalAddress(inetAddress))
            return false;

        Enumeration<NetworkInterface> e=NetworkInterface.getNetworkInterfaces();
        boolean lp=inetAddress.isLoopbackAddress();
        while (e.hasMoreElements())
        {
            NetworkInterface ni=e.nextElement();
            if (lp && ni.isLoopback() && ni.isUp())
                return true;
            else if (isValidInterface(ni))
            {
                for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses())
                {
                    if ((interfaceAddress.getAddress() instanceof Inet4Address && inetAddress instanceof Inet4Address)
                            ||
                            (interfaceAddress.getAddress() instanceof Inet6Address && inetAddress instanceof Inet6Address)) {
                        if (isSameLocalNetwork(interfaceAddress.getAddress().getAddress(), inetAddress.getAddress(), interfaceAddress.getNetworkPrefixLength()))
                            return true;
                    }
                }
            }

        }
        return false;
    }
    public static boolean isSameLocalNetwork(byte[] addr1, byte[] addr2, short network_prefix_length) {
        int length = network_prefix_length / 8;
        for (int i = 0; i < length; i++) {
            if (addr1[i] != addr2[i])
                return false;
        }

        int mod = network_prefix_length % 8;
        if (mod != 0) {
            int b1 = ((int) addr1[length]) & 0xff;
            int b2 = ((int) addr2[length]) & 0xff;
            int filter = (1 << (8 - mod)) - 1;
            return (b1 | filter) == (b2 | filter);
        } else
            return true;

    }
    public static int getTrimLength(String s)
    {
        int end = s.length();
        int start = 0;
        while ((start < end) && (s.charAt(start) <= ' ')) {
            start++;
        }
        while ((start < end) && (s.charAt(end - 1) <= ' ')) {
            end--;
        }
        return end-start;
    }
    public static boolean isTrimLengthEmpty(String s)
    {
        int end = s.length();
        int start = 0;
        while ((start < end) && (s.charAt(start) <= ' ')) {
            start++;
        }
        return end-start==0;
    }

    public static boolean checkDescriptionXMLNotValid(String descriptorXml)
    {
        if (descriptorXml==null)
            return true;
        if (descriptorXml.isEmpty())
            return true;
		if (descriptorXml.length() > Constants.MAX_DESCRIPTOR_LENGTH)
            return true;
        return getTrimLength(descriptorXml)<=0;
	}
    public static boolean checkBodyValid(byte[] body)
    {
        if (body==null)
            return false;
        if (body.length==0)
            return false;
        return body.length <= Constants.MAX_BODY_LENGTH;
    }
    private final static Method getApplicationContextMethod;
    static {

        Method m;
        try {
            Class<?> c=Reflections.classForName("fr.distrimind.oss.flexilogxml.android.ContextProvider");
            m=Reflections.getMethod(c, "getApplicationContext");
        } catch (ClassNotFoundException | NoSuchMethodException | NoClassDefFoundError e) {
            m=null;
        }
        getApplicationContextMethod=m;
    }
    public static Object getAndroidContext()
    {
        if (getApplicationContextMethod==null)
            return null;
        else {
            try {
                return Reflections.invoke(getApplicationContextMethod, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
