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


import fr.distrimind.oss.upnp.common.model.ModelUtil;
import fr.distrimind.oss.upnp.common.model.Validatable;
import fr.distrimind.oss.upnp.common.model.ValidationError;
import fr.distrimind.oss.upnp.common.model.types.Datatype;

import java.util.List;
import java.util.ArrayList;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * The metadata of a named state variable.
 *
 * @author Christian Bauer
 */
public class StateVariable<S extends Service<?, ?, ?>> implements Validatable {

    final private static DMLogger log = Log.getLogger(StateVariable.class);

    final private String name;
    final private StateVariableTypeDetails type;
    final private StateVariableEventDetails eventDetails;

    // Package mutable state
    private S service;

    public StateVariable(String name, StateVariableTypeDetails type) {
        this(name, type, new StateVariableEventDetails());
    }

    public StateVariable(String name, StateVariableTypeDetails type, StateVariableEventDetails eventDetails) {
        this.name = name;
        this.type = type;
        this.eventDetails = eventDetails;
    }

    public String getName() {
        return name;
    }

    public StateVariableTypeDetails getTypeDetails() {
        return type;
    }

    public StateVariableEventDetails getEventDetails() {
        return eventDetails;
    }

    public S getService() {
        return service;
    }

    void setService(S service) {
        if (this.service != null)
            throw new IllegalStateException("Final value has been set already, model is immutable");
        this.service = service;
    }

    @Override
	public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (getName() == null || getName().isEmpty()) {
            errors.add(new ValidationError(
                    getClass(),
                    "name",
                    "StateVariable without name of: " + getService()
            ));
        } else if (!ModelUtil.isValidUDAName(getName())) {
            if (log.isWarnEnabled()) {
                log.warn(Icon.UPN_P_SPECIFICATION_VIOLATION_OF + getService().getDevice());
                log.warn("Invalid state variable name: " + this);
            }
        }

        errors.addAll(getTypeDetails().validate());

        return errors;
    }

    public boolean isModeratedNumericType() {
        return Datatype.Builtin.isNumeric(
                getTypeDetails().getDatatype().getBuiltin()
        ) && getEventDetails().getEventMinimumDelta() > 0;
    }

    public StateVariable<S> deepCopy() {
        return new StateVariable<>(
				getName(),
				getTypeDetails(),
				getEventDetails()
		);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(getClass().getSimpleName());
        sb.append(", Name: ").append(getName());
        sb.append(", Type: ").append(getTypeDetails().getDatatype().getDisplayString()).append(")");
        if (!getEventDetails().isSendEvents()) {
            sb.append(" (No Events)");
        }
        if (getTypeDetails().getDefaultValue() != null) {
            sb.append(" Default Value: ").append("'").append(getTypeDetails().getDefaultValue()).append("'");
        }
        if (getTypeDetails().getAllowedValues() != null) {
            sb.append(" Allowed Values: ");
            for (String s : getTypeDetails().getAllowedValues()) {
                sb.append(s).append("|");
            }
        }
        return sb.toString();
    }
}
