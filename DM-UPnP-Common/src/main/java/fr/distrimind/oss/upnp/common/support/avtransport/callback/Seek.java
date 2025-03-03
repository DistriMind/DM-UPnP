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

import fr.distrimind.oss.upnp.common.Log;
import fr.distrimind.oss.upnp.common.controlpoint.ActionCallback;
import fr.distrimind.oss.upnp.common.model.action.ActionInvocation;
import fr.distrimind.oss.upnp.common.model.meta.Service;
import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerFourBytes;
import fr.distrimind.oss.upnp.common.support.model.SeekMode;

import fr.distrimind.oss.flexilogxml.log.DMLogger;

/**
 *
 * @author Christian Bauer
 */
public abstract class Seek extends ActionCallback {

    final private static DMLogger log = Log.getLogger(Seek.class);

    public Seek(Service<?, ?, ?> service, String relativeTimeTarget) {
        this(new UnsignedIntegerFourBytes(0), service, SeekMode.REL_TIME, relativeTimeTarget);
    }

    public Seek(UnsignedIntegerFourBytes instanceId, Service<?, ?, ?> service, String relativeTimeTarget) {
        this(instanceId, service, SeekMode.REL_TIME, relativeTimeTarget);
    }

    public Seek(Service<?, ?, ?> service, SeekMode mode, String target) {
        this(new UnsignedIntegerFourBytes(0), service, mode, target);
    }

    public Seek(UnsignedIntegerFourBytes instanceId, Service<?, ?, ?> service, SeekMode mode, String target) {
        super(new ActionInvocation<>(service.getAction("Seek")));
        getActionInvocation().setInput("InstanceID", instanceId);
        getActionInvocation().setInput("Unit", mode.name());
        getActionInvocation().setInput("Target", target);
    }

    @Override
    public void success(ActionInvocation<?> invocation) {
        log.debug("Execution successful");
    }
}