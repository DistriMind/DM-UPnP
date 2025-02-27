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

package fr.distrimind.oss.upnp.model.message.gena;

import fr.distrimind.oss.upnp.model.message.IUpnpHeaders;
import fr.distrimind.oss.upnp.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.model.message.UpnpRequest;
import fr.distrimind.oss.upnp.model.message.header.CallbackHeader;
import fr.distrimind.oss.upnp.model.message.header.NTEventHeader;
import fr.distrimind.oss.upnp.model.message.header.TimeoutHeader;
import fr.distrimind.oss.upnp.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.model.gena.RemoteGENASubscription;

import java.net.URL;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class OutgoingSubscribeRequestMessage extends StreamRequestMessage {

    public OutgoingSubscribeRequestMessage(RemoteGENASubscription subscription,
                                           List<URL> callbackURLs,
                                           IUpnpHeaders extraHeaders) {

        super(UpnpRequest.Method.SUBSCRIBE, subscription.getEventSubscriptionURL());

        getHeaders().add(
                UpnpHeader.Type.CALLBACK,
                new CallbackHeader(callbackURLs)
        );

        getHeaders().add(
                UpnpHeader.Type.NT,
                new NTEventHeader()
        );

        getHeaders().add(
                UpnpHeader.Type.TIMEOUT,
                new TimeoutHeader(subscription.getRequestedDurationSeconds())
        );

        if (extraHeaders != null)
            getHeaders().putAll(extraHeaders);
    }

    public boolean hasCallbackURLs() {
        CallbackHeader callbackHeader =
                getHeaders().getFirstHeader(UpnpHeader.Type.CALLBACK, CallbackHeader.class);
        return !callbackHeader.getValue().isEmpty();
    }

}
