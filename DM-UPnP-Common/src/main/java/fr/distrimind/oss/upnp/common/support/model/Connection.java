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

package fr.distrimind.oss.upnp.common.support.model;

import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerFourBytes;

/**
 * @author Christian Bauer
 */
public class Connection {

    static public class StatusInfo {

        private final Status status;
        private final long uptimeSeconds;
        private final Error lastError;

        public StatusInfo(Status status, UnsignedIntegerFourBytes uptime, Error lastError) {
            this(status, uptime.getValue(), lastError);
        }

        public StatusInfo(Status status, long uptimeSeconds, Error lastError) {
            this.status = status;
            this.uptimeSeconds = uptimeSeconds;
            this.lastError = lastError;
        }

        public Status getStatus() {
            return status;
        }

        public long getUptimeSeconds() {
            return uptimeSeconds;
        }

        public UnsignedIntegerFourBytes getUptime() {
            return new UnsignedIntegerFourBytes(getUptimeSeconds());
        }

        public Error getLastError() {
            return lastError;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StatusInfo that = (StatusInfo) o;

            if (uptimeSeconds != that.uptimeSeconds) return false;
            if (lastError != that.lastError) return false;
			return status == that.status;
		}

        @Override
        public int hashCode() {
            int result = status.hashCode();
            result = 31 * result + Long.hashCode(uptimeSeconds);
            result = 31 * result + lastError.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "(" + getClass().getSimpleName() + ") " + getStatus();
        }
    }

    public enum Type {
        /**
         * Valid connection types cannot be identified.
         */
        Unconfigured,

        /**
         * The Internet Gateway is an IP router between the LAN and the WAN connection.
         */
        IP_Routed,

        /**
         * The Internet Gateway is an Ethernet bridge between the LAN and the WAN connection.
         */
        IP_Bridged
    }

    public enum Status {
        /**
         * This value indicates that other variables in the service table are
         * uninitialized or in an invalid state.
         */
        Unconfigured,

        /**
         * The WANConnectionDevice is in the process of initiating a connection
         * for the first time after the connection became disconnected.
         */
        Connecting,

        /**
         * At least one client has successfully
         * initiated an Internet connection using this instance.
         */
        Connected,

        /**
         * The connection is active (packets are allowed to flow
         * through), but will transition to Disconnecting state after a certain period.
         */
        PendingDisconnect,

        /**
         * The WANConnectionDevice is in the process of terminating a connection.
         * On successful termination, ConnectionStatus transitions to Disconnected.
         */
        Disconnecting,

        /**
         * No ISP connection is active (or being activated) from this connection
         * instance. No packets are transiting the gateway.
         */
        Disconnected
    }

    public enum Error {
        ERROR_NONE,
        ERROR_COMMAND_ABORTED,
        ERROR_NOT_ENABLED_FOR_INTERNET,
        ERROR_USER_DISCONNECT,
        ERROR_ISP_DISCONNECT,
        ERROR_IDLE_DISCONNECT,
        ERROR_FORCED_DISCONNECT,
        ERROR_NO_CARRIER,
        ERROR_IP_CONFIGURATION,
        ERROR_UNKNOWN
    }
}
