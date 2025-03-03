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

package fr.distrimind.oss.upnp.common.model.message.gena;

import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;
import fr.distrimind.oss.upnp.common.model.ModelUtil;
import fr.distrimind.oss.upnp.common.model.message.IUpnpHeaders;
import fr.distrimind.oss.upnp.common.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.header.CallbackHeader;
import fr.distrimind.oss.upnp.common.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.common.model.message.header.NTEventHeader;
import fr.distrimind.oss.upnp.common.model.message.header.TimeoutHeader;
import fr.distrimind.oss.upnp.common.model.message.header.SubscriptionIdHeader;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class IncomingSubscribeRequestMessage extends StreamRequestMessage {
    final private static DMLogger log = Log.getLogger(IncomingSubscribeRequestMessage.class);

    final private LocalService<?> service;
    private List<URL> callbackURLs=null;

    public IncomingSubscribeRequestMessage(StreamRequestMessage source, LocalService<?>  service) {
        super(source);
        this.service = service;
    }

    public LocalService<?> getService() {
        return service;
    }

    static List<URL> generateCallbackURLs(CallbackHeader header)
    {
        List<URL> callbackURLs=new ArrayList<>();
        if (header != null)
        {
            for (URL url : header.getValue()) {
                try {
                    InetAddress ia = InetAddress.getByName(url.getHost());
                    if (ia != null) {
                        if (!ModelUtil.isLocalAddressReachableFromThisMachine(ia)) {
                            log.debug("Host not accepted in IncomingSubscribeRequestMessage class");
                        } else {
                            callbackURLs.add(url);
                        }
                    }
                } catch (UnknownHostException ignored) {
                    log.debug("URL not found in IncomingSubscribeRequestMessage class");
                } catch (SocketException e) {
                    log.debug("Cannot parse network interfaces", e);
                }
            }
        }
        return callbackURLs;
    }

    public List<URL> getCallbackURLs() {
        if (callbackURLs==null) {
            callbackURLs=generateCallbackURLs(getHeaders().getFirstHeader(UpnpHeader.Type.CALLBACK, CallbackHeader.class));
        }
        return callbackURLs;
    }


    public boolean hasNotificationHeader() {
        return getHeaders().getFirstHeader(UpnpHeader.Type.NT, NTEventHeader.class) != null;
    }

    public Integer getRequestedTimeoutSeconds() {
        TimeoutHeader timeoutHeader = getHeaders().getFirstHeader(UpnpHeader.Type.TIMEOUT, TimeoutHeader.class);
        return timeoutHeader != null ? timeoutHeader.getValue() : null;
    }

    public String getSubscriptionId() {
        SubscriptionIdHeader header = getHeaders().getFirstHeader(UpnpHeader.Type.SID, SubscriptionIdHeader.class);
        return header != null ? header.getValue() : null;
    }

    @Override
    public void setHeaders(IUpnpHeaders headers) {
        super.setHeaders(headers);
        callbackURLs=null;
    }
}
