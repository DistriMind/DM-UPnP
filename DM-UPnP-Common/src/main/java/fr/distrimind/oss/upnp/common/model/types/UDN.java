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

package fr.distrimind.oss.upnp.common.model.types;

import fr.distrimind.oss.upnp.common.model.ModelUtil;


import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.security.MessageDigest;
import java.math.BigInteger;

/**
 * A unique device name.
 * <p>
 * UDA 1.0 does not specify a UUID format, however, UDA 1.1 specifies a format that is compatible
 * with <code>java.util.UUID</code> variant 4. You can use any identifier string you like.
 * </p>
 * <p>
 * You'll most likely need the {@link #uniqueSystemIdentifier(String)} method sooner or later.
 * </p>
 *
 * @author Christian Bauer
 */
public class UDN {


    public static final String PREFIX = "uuid:";

    private final String identifierString;

    /**
     * @param identifierString The identifier string without the "uuid:" prefix.
     */
    public UDN(String identifierString) {
        this.identifierString = identifierString;
    }

    public UDN(UUID uuid) {
        this.identifierString = uuid.toString();
    }

    public boolean isUDA11Compliant() {
        try {
            UUID.fromString(identifierString);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public String getIdentifierString() {
        return identifierString;
    }

    public static UDN valueOf(String udnString) {
        return new UDN(udnString.startsWith(PREFIX) ? udnString.substring(PREFIX.length()) : udnString);
    }
    private static final Class<?> secureClass;
    private static final Class<?> contentResolverClass;
    private static final Class<?> contextClass;

    private static final java.lang.reflect.Method getStringMethod;
    private static final java.lang.reflect.Method getContentResolverMethod;

    private static final String androidIdField;
    static
    {
        try {

            if (ModelUtil.ANDROID_RUNTIME) {
                secureClass = Class.forName("android.provider.Settings$Secure");
                contentResolverClass = Class.forName("android.content.ContentResolver");
                contextClass = Class.forName("android.content.Context");

                getStringMethod = secureClass.getMethod("getString", contentResolverClass, String.class);
                getContentResolverMethod = contextClass.getMethod("getContentResolver");

                androidIdField = (String) secureClass.getField("ANDROID_ID").get(null);
            } else {
                secureClass = null;
                contentResolverClass = null;
                contextClass = null;

                getStringMethod = null;
                getContentResolverMethod = null;

                androidIdField = null;
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a global unique identifier that is the same every time this method is invoked on the same machine with
     * the same argument.
     * <p>
     * This method combines the first non-loopback network interface's MAC address with given salt to generate a
     * globally unique identifier. In other words, every time you call this method with the same salt on the same
     * machine, you get the same identifier. If you use the same salt on a different machine, a different identifier
     * will be generated.
     * </p>
     * <p>
     * Note for Android users: This method does not generate unique identifiers on Android devices and will
     * throw an exception. We can't get details such as the hostname or MAC address on Android. Instead,
     * construct a UDN with <code>new UDN(UUID)</code>. When your application is first started, generate all
     * UUIDs needed for your UPnP devices and store them in your Android preferences. Then, use the stored
     * UUID to create a UDN every time your application starts.
     * </p>
     * <p>
     * Control points can remember your device's identifier, it will and should be the same every time
     * your device is powered up.
     * </p>
     *
     * @param salt An arbitrary string that uniquely identifies the device on the current system, e.g. "MyMediaServer".
     * @return A global unique identifier, stable for the current system and salt.
     */
    public static UDN uniqueSystemIdentifier(String salt) {
        return uniqueSystemIdentifier(salt, ModelUtil.getAndroidContext());
    }

    /**
     * Generates a global unique identifier that is the same every time this method is invoked on the same machine with
     * the same argument.
     * <p>
     * This method combines the first non-loopback network interface's MAC address with given salt to generate a
     * globally unique identifier. In other words, every time you call this method with the same salt on the same
     * machine, you get the same identifier. If you use the same salt on a different machine, a different identifier
     * will be generated.
     * </p>
     * <p>
     * Note for Android users: This method cannot access to MAC Address under Android.
     * It uses Android ID instead. To work, the android context must be given as parameter.
     * </p>
     * <p>
     * Control points can remember your device's identifier, it will and should be the same every time
     * your device is powered up.
     * </p>
     *
     * @param salt An arbitrary string that uniquely identifies the device on the current system, e.g. "MyMediaServer".
     * @param androidContext the Android context when using Android. Should be null if the application is not used under Android.
     * @return A global unique identifier, stable for the current system and salt.
     */
    public static UDN uniqueSystemIdentifier(String salt, Object androidContext) {
        StringBuilder systemSalt = new StringBuilder();

        // Bug: On Android, NetworkInterface.isLoopback() isn't implemented
        if (!ModelUtil.ANDROID_RUNTIME) {
            systemSalt.append(new String(ModelUtil.getFirstNetworkInterfaceHardwareAddress(), StandardCharsets.UTF_8));
        } else {
            if (androidContext==null)
                throw new RuntimeException(
                        "This method does not create a unique identifier on Android without Android context, see the Javadoc and "
                                + "use new UDN(UUID) instead!");
            else {
                try {

                    Object contentResolver = getContentResolverMethod.invoke(androidContext);
                    @SuppressWarnings("JavaReflectionInvocation") String androidId = (String) getStringMethod.invoke(null, contentResolver, androidIdField);

                    systemSalt.append(androidId);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        }

        try {
            byte[] hash = MessageDigest.getInstance("SHA-512")
                    .digest(systemSalt.toString().getBytes(StandardCharsets.UTF_8));
            return new UDN(new UUID(new BigInteger(-1, hash).longValue(), salt.hashCode()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String toString() {
        return PREFIX + getIdentifierString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UDN)) {
            return false;
        }
        UDN udn = (UDN) o;
        return identifierString.equals(udn.identifierString);
    }

    @Override
    public int hashCode() {
        return identifierString.hashCode();
    }
}
