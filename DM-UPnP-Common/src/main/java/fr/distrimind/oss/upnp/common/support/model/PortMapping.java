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

import fr.distrimind.oss.upnp.common.model.ModelUtil;
import fr.distrimind.oss.upnp.common.model.action.ActionArgumentValue;
import fr.distrimind.oss.upnp.common.model.meta.Service;
import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerFourBytes;
import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerTwoBytes;

import java.util.Map;

/**
 * @author Christian Bauer
 */
public class PortMapping {

    public enum Protocol {
        UDP,
        TCP
    }

    private boolean enabled;
    private UnsignedIntegerFourBytes leaseDurationSeconds;
    private String remoteHost;
    private UnsignedIntegerTwoBytes externalPort;
    private UnsignedIntegerTwoBytes internalPort;
    private String internalClient;
    private Protocol protocol;
    private String description;

    public PortMapping() {
    }


    public PortMapping(Map<String, ? extends ActionArgumentValue<? extends Service<?, ?, ?>>> map) {
        this(
                (Boolean) map.get("NewEnabled").getValue(),
                (UnsignedIntegerFourBytes) map.get("NewLeaseDuration").getValue(),
                (String) map.get("NewRemoteHost").getValue(),
                (UnsignedIntegerTwoBytes) map.get("NewExternalPort").getValue(),
                (UnsignedIntegerTwoBytes) map.get("NewInternalPort").getValue(),
                (String) map.get("NewInternalClient").getValue(),
                Protocol.valueOf(map.get("NewProtocol").toString()),
                (String) map.get("NewPortMappingDescription").getValue()
        );
    }

    public PortMapping(int port, String internalClient, Protocol protocol) {
        this(
                true,
                new UnsignedIntegerFourBytes(0),
                null,
                new UnsignedIntegerTwoBytes(port),
                new UnsignedIntegerTwoBytes(port),
                internalClient,
                protocol,
                null
        );
    }

    public PortMapping(int port, String internalClient, Protocol protocol, String description) {
        this(
                true,
                new UnsignedIntegerFourBytes(0),
                null,
                new UnsignedIntegerTwoBytes(port),
                new UnsignedIntegerTwoBytes(port),
                internalClient,
                protocol,
                description
        );
    }

    public PortMapping(String remoteHost, UnsignedIntegerTwoBytes externalPort, Protocol protocol) {
        this(
                true,
                new UnsignedIntegerFourBytes(0),
                remoteHost,
                externalPort,
                null,
                null,
                protocol,
                null
        );
    }
    private static String getString(String s)
    {
        return s == null || "-".equals(s) || ModelUtil.isTrimLengthEmpty(s) ? null : s;
    }
    public PortMapping(boolean enabled, UnsignedIntegerFourBytes leaseDurationSeconds, String remoteHost, UnsignedIntegerTwoBytes externalPort,
                       UnsignedIntegerTwoBytes internalPort, String internalClient, Protocol protocol, String description) {
        this.enabled = enabled;
        this.leaseDurationSeconds = leaseDurationSeconds;
        this.remoteHost = getString(remoteHost);
        this.externalPort = externalPort;
        this.internalPort = internalPort;
        this.internalClient = getString(internalClient);
        this.protocol = protocol;
        this.description = getString(description);

        checkInternalData();
    }
    public boolean isInternalDataValid()
    {
        try {
            checkInternalData();
            return true;
        }
        catch (NullPointerException | IllegalArgumentException e)
        {
            return false;
        }
    }
    public boolean isInternalDataValidForPortAdd()
    {
        return isInternalDataValid() && internalPort!=null;
    }
    private void checkInternalData() throws NullPointerException
    {
        if (protocol==null)
            throw new NullPointerException();
        if (externalPort==null)
            throw new NullPointerException();
        if (this.internalPort!=null)
        {
            if (this.internalClient==null)
                throw new NullPointerException();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public UnsignedIntegerFourBytes getLeaseDurationSeconds() {
        return leaseDurationSeconds;
    }

    public void setLeaseDurationSeconds(UnsignedIntegerFourBytes leaseDurationSeconds) {
        this.leaseDurationSeconds = leaseDurationSeconds;
    }

    public boolean hasRemoteHost() {
        return remoteHost != null && !remoteHost.isEmpty();
    }

    public String getRemoteHost() {
        return remoteHost == null ? "-" : remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = getString(remoteHost);
    }

    public UnsignedIntegerTwoBytes getExternalPort() {
        return externalPort;
    }

    public void setExternalPort(UnsignedIntegerTwoBytes externalPort) {
        this.externalPort = externalPort;
    }

    public UnsignedIntegerTwoBytes getInternalPort() {
        return internalPort;
    }

    public void setInternalPort(UnsignedIntegerTwoBytes internalPort) {
        this.internalPort = internalPort;
    }

    public String getInternalClient() {
        return internalClient;
    }

    public void setInternalClient(String internalClient) {
        this.internalClient = getString(internalClient);
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public String getDescription() {
        return description == null ? "-" : description;
    }

    public void setDescription(String description) {
        this.description = getString(description);
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") Protocol: " + getProtocol() + ", " + getExternalPort() + " => " + getInternalClient();
    }
}
