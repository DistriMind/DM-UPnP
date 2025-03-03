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

package fr.distrimind.oss.upnp.common.binding.staging;

import fr.distrimind.oss.upnp.common.model.meta.*;
import fr.distrimind.oss.upnp.common.model.types.Datatype;

import java.util.List;

/**
 * @author Christian Bauer
 */
public class MutableStateVariable<S extends Service<?, ?, ?>> {

    public String name;
    public Datatype<?> dataType;
    public String defaultValue;
    public List<String> allowedValues;
    public MutableAllowedValueRange allowedValueRange;
    public StateVariableEventDetails eventDetails;

    public StateVariable<S> build() {
        return new StateVariable<>(
                name,
                new StateVariableTypeDetails(
                        dataType,
                        defaultValue,
                        allowedValues == null || allowedValues.isEmpty()
                                ? null
                                : allowedValues,
                        allowedValueRange == null
                                ? null :
                                new StateVariableAllowedValueRange(
                                        allowedValueRange.minimum,
                                        allowedValueRange.maximum,
                                        allowedValueRange.step
                                )
                ),
                eventDetails
        );
    }
}
