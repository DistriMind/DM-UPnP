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

package fr.distrimind.oss.upnp.support.avtransport.impl.state;

import fr.distrimind.oss.upnp.Log;
import fr.distrimind.oss.upnp.support.avtransport.lastchange.AVTransportVariable;
import fr.distrimind.oss.upnp.support.model.AVTransport;
import fr.distrimind.oss.upnp.support.model.TransportAction;
import fr.distrimind.oss.upnp.support.model.TransportInfo;
import fr.distrimind.oss.upnp.support.model.TransportState;

import java.net.URI;
import java.util.List;
import fr.distrimind.oss.flexilogxml.log.DMLogger;

/**
 * @author Christian Bauer
 */
public abstract class NoMediaPresent<T extends AVTransport> extends AbstractState<T> {

    final private static DMLogger log = Log.getLogger(NoMediaPresent.class);

    public NoMediaPresent(T transport) {
        super(transport);
    }

    public void onEntry() {
        log.debug("Setting transport state to NO_MEDIA_PRESENT");
        getTransport().setTransportInfo(
                new TransportInfo(
                        TransportState.NO_MEDIA_PRESENT,
                        getTransport().getTransportInfo().getCurrentTransportStatus(),
                        getTransport().getTransportInfo().getCurrentSpeed()
                )
        );
        getTransport().getLastChange().setEventedValue(
                getTransport().getInstanceId(),
                new AVTransportVariable.TransportState(TransportState.NO_MEDIA_PRESENT),
                new AVTransportVariable.CurrentTransportActions(getCurrentTransportActions())
        );
    }

    public abstract Class<? extends AbstractState<?>> setTransportURI(URI uri, String metaData);

    @Override
	public List<TransportAction> getCurrentTransportActions() {
        return List.of(
                TransportAction.Stop
        );
    }
}
