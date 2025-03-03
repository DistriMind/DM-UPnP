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

package fr.distrimind.oss.upnp.common.support.igd.callback;

import fr.distrimind.oss.upnp.common.controlpoint.ActionCallback;
import fr.distrimind.oss.upnp.common.controlpoint.ControlPoint;
import fr.distrimind.oss.upnp.common.model.action.ActionArgumentValue;
import fr.distrimind.oss.upnp.common.model.action.ActionException;
import fr.distrimind.oss.upnp.common.model.action.ActionInvocation;
import fr.distrimind.oss.upnp.common.model.meta.Service;
import fr.distrimind.oss.upnp.common.model.types.ErrorCode;
import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerTwoBytes;
import fr.distrimind.oss.upnp.common.support.model.PortMapping;

import java.util.Map;

public abstract class PortMappingEntryGet extends ActionCallback {

    public PortMappingEntryGet(Service<?, ?, ?> service, long index) {
        this(service, null, index);
    }

    protected PortMappingEntryGet(Service<?, ?, ?> service, ControlPoint controlPoint, long index) {
        super(new ActionInvocation<>(service.getAction("GetGenericPortMappingEntry")), controlPoint);

        getActionInvocation().setInput("NewPortMappingIndex", new UnsignedIntegerTwoBytes(index));
    }

    @Override
    public void success(ActionInvocation<?> invocation) {
        try {
            Map<String, ? extends ActionArgumentValue<? extends Service<?, ?, ?>>> outputMap = invocation.getOutputMap();
            success(new PortMapping(outputMap));
        }
        catch (Exception ex) {
            invocation.setFailure(
                    new ActionException(
                            ErrorCode.ARGUMENT_VALUE_INVALID,
                            "Invalid status or last error string: " + ex,
                            ex
                    )
            );
            failure(invocation, null);
        }
    }

    protected abstract void success(PortMapping portMapping);
}