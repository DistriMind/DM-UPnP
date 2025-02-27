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

package fr.distrimind.oss.upnp.support.lastchange;

import fr.distrimind.oss.upnp.model.ModelUtil;
import fr.distrimind.oss.upnp.model.types.Datatype;
import fr.distrimind.oss.upnp.model.types.InvalidValueException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Bauer
 */
public abstract class EventedValueEnumArray<E extends Enum<?>> extends EventedValue<List<E>> {

    public EventedValueEnumArray(List<E> e) {
        super(e);
    }

    public EventedValueEnumArray(Collection<Map.Entry<String, String>> attributes) {
        super(attributes);
    }

    @Override
    protected List<E> valueOf(String s) throws InvalidValueException {
        return enumValueOf(ModelUtil.fromCommaSeparatedList(s));
    }

    protected abstract List<E> enumValueOf(String[] names);

    @Override
    public String toString() {
        return ModelUtil.toCommaSeparatedList(getValue());
    }

    @Override
    protected Datatype<List<E>> getDatatype() {
        return null;
    }
}
