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

package fr.distrimind.oss.upnp.support.avtransport.callback;

import fr.distrimind.oss.upnp.controlpoint.ActionCallback;
import fr.distrimind.oss.upnp.model.action.ActionInvocation;
import fr.distrimind.oss.upnp.model.meta.Service;
import fr.distrimind.oss.upnp.model.types.UnsignedIntegerFourBytes;
import fr.distrimind.oss.upnp.support.model.TransportInfo;

/**
 *
 * @author Christian Bauer
 */
public abstract class GetTransportInfo extends ActionCallback {


    public GetTransportInfo(Service<?, ?, ?> service) {
        this(new UnsignedIntegerFourBytes(0), service);
    }

    public GetTransportInfo(UnsignedIntegerFourBytes instanceId, Service<?, ?, ?> service) {
        super(new ActionInvocation<>(service.getAction("GetTransportInfo")));
        getActionInvocation().setInput("InstanceID", instanceId);
    }

    @Override
	public void success(ActionInvocation<?> invocation) {
        TransportInfo transportInfo = new TransportInfo(invocation.getOutputMap());
        received(invocation, transportInfo);
    }

    public abstract void received(ActionInvocation<?> invocation, TransportInfo transportInfo);

}