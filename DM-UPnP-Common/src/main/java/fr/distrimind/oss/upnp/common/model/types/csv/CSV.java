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

package fr.distrimind.oss.upnp.common.model.types.csv;

import fr.distrimind.oss.upnp.common.model.ModelUtil;
import fr.distrimind.oss.upnp.common.model.types.Datatype;
import fr.distrimind.oss.upnp.common.model.types.InvalidValueException;
import fr.distrimind.oss.upnp.common.util.Reflections;

import java.util.ArrayList;
import java.util.List;

/**
 * Transforms a state variable value from/to strings of comma-separated elements.
 * <p>
 * A concrete implementation of this interface knows how to transform values of the
 * declared type into a string of comma-separated list of elements, and how to read
 * such strings back into individual values.
 * </p>
 * <p>
 * Your action method returns a <code>CSV&lt;...&lt;</code> instance as an output argument. It can
 * also accept a concrecte subclass of this type as an input argument, e.g. <code>CSVString</code>.
 * This type extends a regular <code>List</code>, so within your action method you can
 * handle the elements as usual.
 * </p>
 *
 * @author Christian Bauer
 */
public abstract class CSV<T> extends ArrayList<T> implements ICSV<T> {
    private static final long serialVersionUID = 1L;

    protected final Datatype.Builtin datatype;

    public CSV() {
        datatype = getBuiltinDatatype();
    }

    public CSV(String s) throws InvalidValueException {
        datatype = getBuiltinDatatype();
        addAll(parseString(s));
    }

    @SuppressWarnings("unchecked")
	protected List<T> parseString(String s) throws InvalidValueException {
        String[] strings = ModelUtil.fromCommaSeparatedList(s);
        List<T> values = new ArrayList<>();
        for (String string : strings) {
            values.add((T)datatype.getDatatype().valueOf(string));
        }
        return values;
    }

    protected Datatype.Builtin getBuiltinDatatype() throws InvalidValueException {
        Class<?> csvType = Reflections.getTypeArguments(ArrayList.class, getClass()).get(0);
        Datatype.Default defaultType = Datatype.Default.getByJavaType(csvType);
        if (defaultType == null) {
            throw new InvalidValueException("No built-in UPnP datatype for Java type of CSV: " + csvType);
        }
        return defaultType.getBuiltinType();
    }

    @Override
    public String toString() {
        List<String> stringValues = new ArrayList<>();
        for (T t : this) {
            stringValues.add(datatype.getDatatype().getObjectString(t));
        }
        return ModelUtil.toCommaSeparatedList(stringValues);
    }
}
