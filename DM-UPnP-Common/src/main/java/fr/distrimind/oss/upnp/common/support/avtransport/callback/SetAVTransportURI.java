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

package fr.distrimind.oss.upnp.common.support.avtransport.callback;

import fr.distrimind.oss.upnp.common.controlpoint.ActionCallback;
import fr.distrimind.oss.upnp.common.model.action.ActionInvocation;
import fr.distrimind.oss.upnp.common.model.meta.Service;
import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerFourBytes;

import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * @author Christian Bauer
 */
public abstract class SetAVTransportURI extends ActionCallback {

    final private static DMLogger log = Log.getLogger(SetAVTransportURI.class);

    public SetAVTransportURI(Service<?, ?, ?> service, String uri) {
        this(new UnsignedIntegerFourBytes(0), service, uri, null);
    }

    public SetAVTransportURI(Service<?, ?, ?> service, String uri, String metadata) {
        this(new UnsignedIntegerFourBytes(0), service, uri, metadata);
    }

    public SetAVTransportURI(UnsignedIntegerFourBytes instanceId, Service<?, ?, ?> service, String uri) {
        this(instanceId, service, uri, null);
    }

    public SetAVTransportURI(UnsignedIntegerFourBytes instanceId, Service<?, ?, ?> service, String uri, String metadata) {
        super(new ActionInvocation<>(service.getAction("SetAVTransportURI")));
		if (log.isDebugEnabled()) {
            log.debug("Creating SetAVTransportURI action for URI: " + uri);
		}
		getActionInvocation().setInput("InstanceID", instanceId);
        getActionInvocation().setInput("CurrentURI", uri);
        getActionInvocation().setInput("CurrentURIMetaData", metadata);
    }

    @Override
    public void success(ActionInvocation<?> invocation) {
        log.debug("Execution successful");
    }
}