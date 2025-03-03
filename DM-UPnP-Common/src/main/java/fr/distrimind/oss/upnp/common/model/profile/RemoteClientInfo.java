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

package fr.distrimind.oss.upnp.common.model.profile;

import fr.distrimind.oss.upnp.common.model.message.IUpnpHeaders;
import fr.distrimind.oss.upnp.common.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.common.transport.spi.StreamServer;
import fr.distrimind.oss.upnp.common.model.message.Connection;
import fr.distrimind.oss.upnp.common.model.message.UpnpHeaders;
import fr.distrimind.oss.upnp.common.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.common.model.message.header.UserAgentHeader;
import fr.distrimind.oss.upnp.common.http.RequestInfo;

import java.net.InetAddress;

/**
 * Encapsulates information about a remote control point, the client.
 *
 * <p>
 * The {@link #getExtraResponseHeaders()} method offers modifiable HTTP headers which will
 * be added to the responses and returned to the client.
 * </p>
 *
 * @author Christian Bauer
 */
public class RemoteClientInfo extends ClientInfo {

    final protected Connection connection;
    final protected IUpnpHeaders extraResponseHeaders = new UpnpHeaders();

    public RemoteClientInfo() {
        this(null);
    }

    public RemoteClientInfo(StreamRequestMessage requestMessage) {
        this(requestMessage != null ? requestMessage.getConnection() : null,
            requestMessage != null ? requestMessage.getHeaders() : new UpnpHeaders());
    }

    public RemoteClientInfo(Connection connection, IUpnpHeaders requestHeaders) {
        super(requestHeaders);
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * <p>
     * Check if the remote client's connection is still open.
   
     * <p>
     * How connection checking is actually performed is transport-implementation dependent. Usually,
     * the {@link StreamServer} will send meaningless heartbeat
     * data to the client on its (open) socket. If that fails, the client's connection has been
     * closed. Note that some HTTP clients can <em>NOT</em> handle such garbage data in HTTP
     * responses, hence calling this method might cause compatibility issues.
   
     * @return <code>true</code> if the remote client's connection was closed.
     */
    public boolean isRequestCancelled() {
        return !getConnection().isOpen();
    }

    /**
     * @throws InterruptedException if {@link #isRequestCancelled()} returns <code>true</code>.
     */
    public void throwIfRequestCancelled() throws InterruptedException{
        if(isRequestCancelled())
             throw new InterruptedException("Client's request cancelled");
    }

    public InetAddress getRemoteAddress() {
        return getConnection().getRemoteAddress();
    }

    public InetAddress getLocalAddress() {
        return getConnection().getLocalAddress();
    }

    public IUpnpHeaders getExtraResponseHeaders() {
        return extraResponseHeaders;
    }

    public void setResponseUserAgent(String userAgent) {
        setResponseUserAgent(new UserAgentHeader(userAgent));
    }

    public void setResponseUserAgent(UserAgentHeader userAgentHeader) {
        getExtraResponseHeaders().add(
            UpnpHeader.Type.USER_AGENT,
            userAgentHeader
        );
    }

    // TODO: Remove this once we know how ClientProfile will look like
    public boolean isWMPRequest() {
        return RequestInfo.isWMPRequest(getRequestUserAgent());
    }

    public boolean isXbox360Request() {
        return RequestInfo.isXbox360Request(
            getRequestUserAgent(),
            getRequestHeaders().getFirstHeaderString(UpnpHeader.Type.SERVER)
        );
    }

    public boolean isPS3Request() {
    	return RequestInfo.isPS3Request(
            getRequestUserAgent(),
            getRequestHeaders().getFirstHeaderString(UpnpHeader.Type.EXT_AV_CLIENT_INFO)
        );
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") Remote Address: " + getRemoteAddress();
    }
}
