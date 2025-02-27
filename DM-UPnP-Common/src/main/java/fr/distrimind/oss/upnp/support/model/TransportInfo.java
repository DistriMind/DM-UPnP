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

package fr.distrimind.oss.upnp.support.model;

import fr.distrimind.oss.upnp.model.action.ActionArgumentValue;

import java.util.Map;

/**
 *
 */
public class TransportInfo {

    private TransportState currentTransportState = TransportState.NO_MEDIA_PRESENT;
    private TransportStatus currentTransportStatus = TransportStatus.OK;
    private String currentSpeed = "1";

    public TransportInfo() {
    }

    public TransportInfo(Map<String, ? extends ActionArgumentValue<?>> args) {
        this(
                TransportState.valueOrCustomOf((String) args.get("CurrentTransportState").getValue()),
                TransportStatus.valueOrCustomOf((String) args.get("CurrentTransportStatus").getValue()),
                (String) args.get("CurrentSpeed").getValue()
        );
    }

    public TransportInfo(TransportState currentTransportState) {
        this.currentTransportState = currentTransportState;
    }

    public TransportInfo(TransportState currentTransportState, String currentSpeed) {
        this.currentTransportState = currentTransportState;
        this.currentSpeed = currentSpeed;
    }

    public TransportInfo(TransportState currentTransportState, TransportStatus currentTransportStatus) {
        this.currentTransportState = currentTransportState;
        this.currentTransportStatus = currentTransportStatus;
    }

    public TransportInfo(TransportState currentTransportState, TransportStatus currentTransportStatus, String currentSpeed) {
        this.currentTransportState = currentTransportState;
        this.currentTransportStatus = currentTransportStatus;
        this.currentSpeed = currentSpeed;
    }

    public TransportState getCurrentTransportState() {
        return currentTransportState;
    }

    public TransportStatus getCurrentTransportStatus() {
        return currentTransportStatus;
    }

    public String getCurrentSpeed() {
        return currentSpeed;
    }
}
