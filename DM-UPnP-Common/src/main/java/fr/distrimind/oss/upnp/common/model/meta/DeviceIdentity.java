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

package fr.distrimind.oss.upnp.common.model.meta;

import java.util.ArrayList;
import java.util.List;

import fr.distrimind.oss.upnp.common.model.Constants;
import fr.distrimind.oss.upnp.common.model.Validatable;
import fr.distrimind.oss.upnp.common.model.ValidationError;
import fr.distrimind.oss.upnp.common.model.types.UDN;

/**
 * Unique device name, received and offered during discovery with SSDP.
 *
 * @author Christian Bauer
 */
public class DeviceIdentity implements Validatable {

    final private UDN udn;
    final private Integer maxAgeSeconds;

    public DeviceIdentity(UDN udn, DeviceIdentity template) {
        this.udn = udn;
        this.maxAgeSeconds = template.getMaxAgeSeconds();
    }

    public DeviceIdentity(UDN udn) {
        this.udn = udn;
        this.maxAgeSeconds = Constants.MIN_ADVERTISEMENT_AGE_SECONDS;
    }

    public DeviceIdentity(UDN udn, Integer maxAgeSeconds) {
        this.udn = udn;
        this.maxAgeSeconds = maxAgeSeconds;
    }

    public UDN getUdn() {
        return udn;
    }

    public Integer getMaxAgeSeconds() {
        return maxAgeSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceIdentity that = (DeviceIdentity) o;

		return udn.equals(that.udn);
	}

    @Override
    public int hashCode() {
        return udn.hashCode();
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") UDN: " + getUdn();
    }

    @Override
    public List<ValidationError> validate() {
    	List<ValidationError> errors = new ArrayList<>();

    	if (getUdn() == null) {
    		errors.add(new ValidationError(
    				getClass(),
    				"major",
    				"Device has no UDN"
    				));
    	}

    	return errors;
    }
}
