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

import java.util.Locale;

/**
 * @author Christian Bauer
 */
public class BooleanDatatype extends AbstractDatatype<Boolean> {

    public BooleanDatatype() {
    }

    @Override
    public boolean isHandlingJavaType(Class<?> type) {
        return type == Boolean.TYPE || Boolean.class.isAssignableFrom(type);
    }

    @Override
	public Boolean valueOf(String s) throws InvalidValueException {
        if (s.isEmpty()) return null;
        if ("1".equals(s) || "YES".equals(s.toUpperCase(Locale.ROOT)) || "TRUE".equals(s.toUpperCase(Locale.ROOT))) {
            return true;
        } else if ("0".equals(s) || "NO".equals(s.toUpperCase(Locale.ROOT)) || "FALSE".equals(s.toUpperCase(Locale.ROOT))) {
            return false;
        } else {
            throw new InvalidValueException("Invalid boolean value string: " + s);
        }
    }

    @Override
	public String getString(Boolean value) throws InvalidValueException {
        if (value == null) return "";
        return value ? "1" : "0";
    }

}
