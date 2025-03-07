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

import fr.distrimind.oss.upnp.common.model.ValidationError;

import java.util.List;
import java.util.Collections;


/**
 * Describes a single action, the deprecated "query any state variable" action.
 * Note: This is already deprecated in UDA 1.0!
 *
 * @author Christian Bauer
 */
public class QueryStateVariableAction<S extends Service<?, ?, ?>> extends Action<S> {

    public static final String INPUT_ARG_VAR_NAME = "varName";
    public static final String OUTPUT_ARG_RETURN = "return";

    public static final String ACTION_NAME = "QueryStateVariable";
    public static final String VIRTUAL_STATEVARIABLE_INPUT = "VirtualQueryActionInput";
    public static final String VIRTUAL_STATEVARIABLE_OUTPUT = "VirtualQueryActionOutput";

    public QueryStateVariableAction() {
        this(null);
    }

    public QueryStateVariableAction(S service) {
        super(ACTION_NAME,
                List.of(new ActionArgument<>(INPUT_ARG_VAR_NAME, VIRTUAL_STATEVARIABLE_INPUT, ActionArgument.Direction.IN),
						new ActionArgument<>(OUTPUT_ARG_RETURN, VIRTUAL_STATEVARIABLE_OUTPUT, ActionArgument.Direction.OUT))
        );
        setService(service);
    }

    @Override
    public String getName() {
        return ACTION_NAME;
    }

    @Override
    public List<ValidationError> validate() {
        return Collections.emptyList();
    }
}