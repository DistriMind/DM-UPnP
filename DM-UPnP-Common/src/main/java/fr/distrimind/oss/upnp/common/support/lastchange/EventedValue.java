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

package fr.distrimind.oss.upnp.common.support.lastchange;

import fr.distrimind.oss.upnp.common.model.types.Datatype;
import fr.distrimind.oss.upnp.common.model.types.InvalidValueException;
import fr.distrimind.oss.upnp.common.support.shared.AbstractMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class EventedValue<V> {

    final protected V value;

    public EventedValue(V value) {
        this.value = value;
    }

    public EventedValue(Collection<Map.Entry<String,String>> attributes) {
        try {
            this.value = valueOf(attributes);
        } catch (InvalidValueException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public V getValue() {
        return value;
    }

    public List<Map.Entry<String, String>> getAttributes() {
        return List.of(
            new AbstractMap.SimpleEntry<>("val", toString())
        );
    }

    protected V valueOf(Collection<Map.Entry<String,String>> attributes) throws InvalidValueException {
        V v = null;
        for (Map.Entry<String, String> attribute : attributes) {
            if ("val".equals(attribute.getKey())) v = valueOf(attribute.getValue());
        }
        return v;
    }

	protected V valueOf(String s) throws InvalidValueException {
        return getDatatype().valueOf(s);
    }

	@Override
    public String toString() {
        return getDatatype().getString(getValue());
    }

    abstract protected Datatype<V> getDatatype();
}
