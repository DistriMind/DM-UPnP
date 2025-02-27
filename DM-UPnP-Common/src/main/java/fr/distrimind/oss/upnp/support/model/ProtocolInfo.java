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

import fr.distrimind.oss.upnp.model.types.InvalidValueException;
import fr.distrimind.oss.upnp.util.MimeType;

/**
 * Encaspulates a MIME type (content format) and transport, protocol, additional information.
 *
 * @author Christian Bauer
 */
public class ProtocolInfo {

    public static final String WILDCARD = "*";

    protected Protocol protocol;
    protected String network = WILDCARD;
    protected String contentFormat;
    protected String additionalInfo = WILDCARD;

    @SuppressWarnings("PMD.AvoidReassigningParameters")
    public ProtocolInfo(String s) throws InvalidValueException {
        if (s == null) throw new NullPointerException();
        s = s.trim();
        String[] split = s.split(":");
        if (split.length != 4) {
            throw new InvalidValueException("Can't parse ProtocolInfo string: " + s);
        }
        this.protocol = Protocol.value(split[0]);
        this.network = split[1];
        this.contentFormat = split[2];
        this.additionalInfo = split[3];
    }

    public ProtocolInfo(MimeType contentFormatMimeType) {
        this.protocol = Protocol.HTTP_GET;
        this.contentFormat = contentFormatMimeType.toString();
    }

    public ProtocolInfo(Protocol protocol, String network, String contentFormat, String additionalInfo) {
        this.protocol = protocol;
        this.network = network;
        this.contentFormat = contentFormat;
        this.additionalInfo = additionalInfo;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public String getNetwork() {
        return network;
    }

    public String getContentFormat() {
        return contentFormat;
    }

    public MimeType getContentFormatMimeType() throws IllegalArgumentException {
        return MimeType.valueOf(contentFormat);
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProtocolInfo that = (ProtocolInfo) o;

        if (!additionalInfo.equals(that.additionalInfo)) return false;
        if (!contentFormat.equals(that.contentFormat)) return false;
        if (!network.equals(that.network)) return false;
		return protocol == that.protocol;
	}

    @Override
    public int hashCode() {
        int result = protocol.hashCode();
        result = 31 * result + network.hashCode();
        result = 31 * result + contentFormat.hashCode();
        result = 31 * result + additionalInfo.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return protocol.toString() + ":" +
                network + ":" +
                contentFormat + ":" +
                additionalInfo;

    }
}
