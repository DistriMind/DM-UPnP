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

import fr.distrimind.oss.upnp.common.model.meta.Service;
import fr.distrimind.oss.upnp.common.registry.Registry;
import fr.distrimind.oss.upnp.common.model.types.ServiceId;
import fr.distrimind.oss.upnp.common.model.types.UDN;

/**
 * Combines a {@link UDN} and a {@link ServiceId}.
 * <p>
 * A service reference is useful to remember a service. For example, if a control point has accessed
 * a service once, it can remember the service with {@link Service#getReference()}.
 * Before every action invocation, it can now resolve the reference to an actually registered service with
 * {@link Registry#getService(ServiceReference)}. If the registry doesn't return
 * a service for the given reference, the service is currently not available.
 * </p>
 * <p>
 * This simplifies implementing disconnect/reconnect behavior in a control point.
 * </p>
 * 
 * @author Christian Bauer
 */
public class ServiceReference {

    public static final String DELIMITER = "/";

    final private UDN udn;
    final private ServiceId serviceId;

    public ServiceReference(String s) {
        String[] split = s.split("/");
        if (split.length == 2) {
            this.udn =  UDN.valueOf(split[0]);
            this.serviceId = ServiceId.valueOf(split[1]);
        } else {
            this.udn = null;
            this.serviceId = null;
        }
    }

    public ServiceReference(UDN udn, ServiceId serviceId) {
        this.udn = udn;
        this.serviceId = serviceId;
    }

    public UDN getUdn() {
        return udn;
    }

    public ServiceId getServiceId() {
        return serviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceReference that = (ServiceReference) o;

        if (!serviceId.equals(that.serviceId)) return false;
		return udn.equals(that.udn);
	}

    @Override
    public int hashCode() {
        int result = udn.hashCode();
        result = 31 * result + serviceId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return udn == null || serviceId == null ? "" : udn + DELIMITER + serviceId;
    }

}