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

package fr.distrimind.oss.upnp.model.types;

/**
 * Combines a {@link UDN} with a {@link DeviceType}, string representation
 * is separated by double-colon.
 *
 * @author Christian Bauer
 */
public class NamedDeviceType {

    private final UDN udn;
    private final DeviceType deviceType;

    public NamedDeviceType(UDN udn, DeviceType deviceType) {
        this.udn = udn;
        this.deviceType = deviceType;
    }

    public UDN getUdn() {
        return udn;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public static NamedDeviceType valueOf(String s) throws InvalidValueException {
        String[] strings = s.split("::");
        if (strings.length != 2) {
            throw new InvalidValueException("Can't parse UDN::DeviceType from: " + s);
        }

        UDN udn;
        try {
            udn = UDN.valueOf(strings[0]);
        } catch (Exception ex) {
            throw new InvalidValueException("Can't parse UDN: " + strings[0]);
        }

        DeviceType deviceType = DeviceType.valueOf(strings[1]);
        return new NamedDeviceType(udn, deviceType);
    }

    @Override
    public String toString() {
        return getUdn().toString() + "::" + getDeviceType().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamedDeviceType)) return false;

        NamedDeviceType that = (NamedDeviceType) o;

        if (!deviceType.equals(that.deviceType)) return false;
		return udn.equals(that.udn);
	}

    @Override
    public int hashCode() {
        int result = udn.hashCode();
        result = 31 * result + deviceType.hashCode();
        return result;
    }
}
