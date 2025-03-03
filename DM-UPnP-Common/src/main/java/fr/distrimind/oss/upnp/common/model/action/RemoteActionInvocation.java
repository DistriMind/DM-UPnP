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

package fr.distrimind.oss.upnp.common.model.action;

import fr.distrimind.oss.upnp.common.model.meta.Service;
import fr.distrimind.oss.upnp.common.model.profile.RemoteClientInfo;
import fr.distrimind.oss.upnp.common.model.meta.Action;

import java.util.List;

/**
 * An action invocation by a remote control point.
 *
 * @author Christian Bauer
 */
public class RemoteActionInvocation<S extends Service<?, ?, ?>> extends ActionInvocation<S> {

    final protected RemoteClientInfo remoteClientInfo;

    public RemoteActionInvocation(Action<S> action,
                                  List<ActionArgumentValue<S>> input,
                                  List<ActionArgumentValue<S>> output,
                                  RemoteClientInfo remoteClientInfo) {
        super(action, input, output, null);
        this.remoteClientInfo = remoteClientInfo;
    }

    public RemoteActionInvocation(Action<S> action,
                                  RemoteClientInfo remoteClientInfo) {
        super(action);
        this.remoteClientInfo = remoteClientInfo;
    }

    public RemoteActionInvocation(ActionException failure,
                            RemoteClientInfo remoteClientInfo) {
        super(failure);
        this.remoteClientInfo = remoteClientInfo;
    }

    public RemoteClientInfo getRemoteClientInfo() {
        return remoteClientInfo;
    }

}
