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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An arbitrary list of comma-separated elements, representing DLNA capabilities (whatever that is).
 *
 * @author Christian Bauer
 */
public class DLNACaps {

    final List<String> caps;

    public DLNACaps(List<String> caps) {
        this.caps = caps;
    }

    public List<String> getCaps() {
        return caps;
    }

    static public DLNACaps valueOf(String s) throws InvalidValueException {
        if (s == null || s.isEmpty()) return new DLNACaps(Collections.emptyList());
        String[] caps = s.split(",");
        List<String> trimmed = new ArrayList<>(caps.length);
		for (String cap : caps) {
			trimmed.add(cap.trim());
		}
        return new DLNACaps(trimmed);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DLNACaps dlnaCaps = (DLNACaps) o;

		return caps.equals(dlnaCaps.caps);
	}

    @Override
    public int hashCode() {
        return caps.size();
    }

    @Override
    public String toString() {
        return ModelUtil.toCommaSeparatedList(getCaps());
    }
}
