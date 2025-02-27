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
 * Combines a {@link UDN} with a {@link ServiceType}, string representation
 * is separated by double-colon.
 *
 * @author Christian Bauer
 */
public class NamedServiceType {

    private final UDN udn;
    private final ServiceType serviceType;

    public NamedServiceType(UDN udn, ServiceType serviceType) {
        this.udn = udn;
        this.serviceType = serviceType;
    }

    public UDN getUdn() {
        return udn;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public static NamedServiceType valueOf(String s) throws InvalidValueException {
        String[] strings = s.split("::");
        if (strings.length != 2) {
            throw new InvalidValueException("Can't parse UDN::ServiceType from: " + s);
        }

        UDN udn;
        try {
            udn = UDN.valueOf(strings[0]);
        } catch (Exception ex) {
            throw new InvalidValueException("Can't parse UDN: " + strings[0]);
        }

        ServiceType serviceType = ServiceType.valueOf(strings[1]);
        return new NamedServiceType(udn, serviceType);
    }

    @Override
    public String toString() {
        return getUdn().toString() + "::" + getServiceType().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamedServiceType)) return false;

        NamedServiceType that = (NamedServiceType) o;

        if (!serviceType.equals(that.serviceType)) return false;
		return udn.equals(that.udn);
	}

    @Override
    public int hashCode() {
        int result = udn.hashCode();
        result = 31 * result + serviceType.hashCode();
        return result;
    }
}