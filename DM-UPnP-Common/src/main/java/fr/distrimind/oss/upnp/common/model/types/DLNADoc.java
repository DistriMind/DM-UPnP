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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Representing the DLNA document and its version.
 * <p>
 * Someone ignored the device and service type construct of UPnP
 * and invented a new and of course much better device type/version  * construct.
 * </p>
 *
 * @author Christian Bauer
 */
public class DLNADoc {

    public static final Pattern PATTERN = Pattern.compile("(.+?)[ -]([0-9].[0-9]{2})");

    public enum Version {
        V1_0("1.00"),
        V1_5("1.50");

        final String s;

        Version(String s) {
            this.s = s;
        }


        @Override
        public String toString() {
            return s;
        }
    }

    final private String devClass;
    final private String version;

    public DLNADoc(String devClass, String version) {
        this.devClass = devClass;
        this.version = version;
    }

    public DLNADoc(String devClass, Version version) {
        this.devClass = devClass;
        this.version = version.s;
    }

    public String getDevClass() {
        return devClass;
    }

    public String getVersion() {
        return version;
    }

    public static DLNADoc valueOf(String s) throws InvalidValueException {
        Matcher matcher = PATTERN.matcher(s);
        if (matcher.matches()) {
            return new DLNADoc(matcher.group(1), matcher.group(2));
        } else {
            throw new InvalidValueException("Can't parse DLNADoc: " + s);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DLNADoc dlnaDoc = (DLNADoc) o;

        if (!devClass.equals(dlnaDoc.devClass)) return false;
		return version.equals(dlnaDoc.version);
	}

    @Override
    public int hashCode() {
        int result = devClass.hashCode();
        result = 31 * result + version.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return getDevClass() + "-" + getVersion();
    }
}
