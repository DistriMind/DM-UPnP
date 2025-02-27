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

package fr.distrimind.oss.upnp.protocol;

import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.Log;
import fr.distrimind.oss.upnp.transport.RouterException;
import fr.distrimind.oss.upnp.UpnpService;
import fr.distrimind.oss.upnp.model.Namespace;
import fr.distrimind.oss.upnp.model.NetworkAddress;
import fr.distrimind.oss.upnp.model.action.ActionInvocation;
import fr.distrimind.oss.upnp.model.gena.LocalGENASubscription;
import fr.distrimind.oss.upnp.model.gena.RemoteGENASubscription;
import fr.distrimind.oss.upnp.model.message.IncomingDatagramMessage;
import fr.distrimind.oss.upnp.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.model.message.UpnpRequest;
import fr.distrimind.oss.upnp.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.model.types.InvalidValueException;
import fr.distrimind.oss.upnp.model.types.NamedServiceType;
import fr.distrimind.oss.upnp.model.types.NotificationSubtype;
import fr.distrimind.oss.upnp.model.types.ServiceType;
import fr.distrimind.oss.upnp.protocol.async.ReceivingNotification;
import fr.distrimind.oss.upnp.protocol.async.ReceivingSearch;
import fr.distrimind.oss.upnp.protocol.async.ReceivingSearchResponse;
import fr.distrimind.oss.upnp.protocol.async.SendingNotificationAlive;
import fr.distrimind.oss.upnp.protocol.async.SendingNotificationByebye;
import fr.distrimind.oss.upnp.protocol.async.SendingSearch;
import fr.distrimind.oss.upnp.protocol.sync.ReceivingAction;
import fr.distrimind.oss.upnp.protocol.sync.ReceivingEvent;
import fr.distrimind.oss.upnp.protocol.sync.ReceivingRetrieval;
import fr.distrimind.oss.upnp.protocol.sync.ReceivingSubscribe;
import fr.distrimind.oss.upnp.protocol.sync.ReceivingUnsubscribe;
import fr.distrimind.oss.upnp.protocol.sync.SendingAction;
import fr.distrimind.oss.upnp.protocol.sync.SendingEvent;
import fr.distrimind.oss.upnp.protocol.sync.SendingRenewal;
import fr.distrimind.oss.upnp.protocol.sync.SendingSubscribe;
import fr.distrimind.oss.upnp.protocol.sync.SendingUnsubscribe;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * Default implementation, directly instantiates the appropriate protocols.
 *
 * @author Christian Bauer
 */
@ApplicationScoped
public class ProtocolFactoryImpl implements ProtocolFactory {

    final private static DMLogger log = Log.getLogger(ProtocolFactoryImpl.class);

    protected final UpnpService upnpService;

    protected ProtocolFactoryImpl() {
        upnpService = null;
    }

    @Inject
    public ProtocolFactoryImpl(UpnpService upnpService) {
		if (log.isDebugEnabled()) {
            log.debug("Creating ProtocolFactory: " + getClass().getName());
		}
		this.upnpService = upnpService;
    }

    @Override
	public UpnpService getUpnpService() {
        return upnpService;
    }

    @Override
	@SuppressWarnings("unchecked")
	public ReceivingAsync<?> createReceivingAsync(IncomingDatagramMessage<?> message) throws ProtocolCreationException {
        if (log.isDebugEnabled()) {
            log.debug("Creating protocol for incoming asynchronous: " + message);
        }

        if (message.getOperation() instanceof UpnpRequest) {
            IncomingDatagramMessage<UpnpRequest> incomingRequest = (IncomingDatagramMessage<UpnpRequest>)message;

            switch (incomingRequest.getOperation().getMethod()) {
                case NOTIFY:
                    return isByeBye(incomingRequest) || isSupportedServiceAdvertisement(incomingRequest)
                        ? createReceivingNotification(incomingRequest) : null;
                case MSEARCH:
                    return createReceivingSearch(incomingRequest);
            }

        } else if (message.getOperation() instanceof UpnpResponse) {
            IncomingDatagramMessage<UpnpResponse> incomingResponse = (IncomingDatagramMessage<UpnpResponse>)message;

            return isSupportedServiceAdvertisement(incomingResponse)
                ? createReceivingSearchResponse(incomingResponse) : null;
        }

        throw new ProtocolCreationException("Protocol for incoming datagram message not found: " + message);
    }

    protected ReceivingAsync<?> createReceivingNotification(IncomingDatagramMessage<UpnpRequest> incomingRequest) {
        return new ReceivingNotification(getUpnpService(), incomingRequest);
    }

    protected ReceivingAsync<?> createReceivingSearch(IncomingDatagramMessage<UpnpRequest> incomingRequest) {
        return new ReceivingSearch(getUpnpService(), incomingRequest);
    }

    protected ReceivingAsync<?> createReceivingSearchResponse(IncomingDatagramMessage<UpnpResponse> incomingResponse) {
        return new ReceivingSearchResponse(getUpnpService(), incomingResponse);
    }

    // DO NOT USE THE PARSED/TYPED MSG HEADERS! THIS WOULD DEFEAT THE PURPOSE OF THIS OPTIMIZATION!

    protected boolean isByeBye(IncomingDatagramMessage<?> message) {
        String ntsHeader = message.getHeaders().getFirstHeader(UpnpHeader.Type.NTS.getHttpName());
        return ntsHeader != null && ntsHeader.equals(NotificationSubtype.BYEBYE.getHeaderString());
    }

    protected boolean isSupportedServiceAdvertisement(IncomingDatagramMessage<?> message) {
        ServiceType[] exclusiveServiceTypes = getUpnpService().getConfiguration().getExclusiveServiceTypes();
        if (exclusiveServiceTypes == null) return false; // Discovery is disabled
        if (exclusiveServiceTypes.length == 0) return true; // Any advertisement is fine

        String usnHeader = message.getHeaders().getFirstHeader(UpnpHeader.Type.USN.getHttpName());
        if (usnHeader == null) return false; // Not a service advertisement, drop it

        try {
            NamedServiceType nst = NamedServiceType.valueOf(usnHeader);
            for (ServiceType exclusiveServiceType : exclusiveServiceTypes) {
                if (nst.getServiceType().implementsVersion(exclusiveServiceType))
                    return true;
            }
        } catch (InvalidValueException ex) {
			if (log.isTraceEnabled()) {
                log.trace("Not a named service type header value: " + usnHeader);
			}
		}
		if (log.isDebugEnabled()) {
            log.debug("Service advertisement not supported, dropping it: " + usnHeader);
		}
		return false;
    }

    @Override
	public ReceivingSync<?, ?> createReceivingSync(StreamRequestMessage message) throws ProtocolCreationException {
		if (log.isDebugEnabled()) {
            log.debug("Creating protocol for incoming synchronous: " + message);
		}

		if (message.getOperation().getMethod().equals(UpnpRequest.Method.GET)) {

            return createReceivingRetrieval(message);

        } else if (getUpnpService().getConfiguration().getNamespace().isControlPath(message.getUri())) {

            if (message.getOperation().getMethod().equals(UpnpRequest.Method.POST))
                return createReceivingAction(message);

        } else if (getUpnpService().getConfiguration().getNamespace().isEventSubscriptionPath(message.getUri())) {

            if (message.getOperation().getMethod().equals(UpnpRequest.Method.SUBSCRIBE)) {
                return createReceivingSubscribe(message);
            } else if (message.getOperation().getMethod().equals(UpnpRequest.Method.UNSUBSCRIBE)) {
                return createReceivingUnsubscribe(message);
            }

        } else if (getUpnpService().getConfiguration().getNamespace().isEventCallbackPath(message.getUri())) {

            if (message.getOperation().getMethod().equals(UpnpRequest.Method.NOTIFY))
                return createReceivingEvent(message);

        } else {

            // TODO: UPNP VIOLATION: Onkyo devices send event messages with trailing garbage characters
            // /dev/9bb022aa-e922-aab9-682b-aa09e9b9e059/svc/upnp-org/RenderingControl/event/cb192%2e168%2e10%2e38
            // TODO: UPNP VIOLATION: Yamaha does the same
            // /dev/9ab0c000-f668-11de-9976-00a0de870fd4/svc/upnp-org/RenderingControl/event/cb><http://10.189.150.197:42082/dev/9ab0c000-f668-11de-9976-00a0de870fd4/svc/upnp-org/RenderingControl/event/cb
            if (message.getUri().getPath().contains(Namespace.EVENTS + Namespace.CALLBACK_FILE)) {
                if (log.isWarnEnabled()) log.warn("Fixing trailing garbage in event message path: " + message.getUri().getPath());
                String invalid = message.getUri().toString();
                message.setUri(
                    URI.create(invalid.substring(
                        0, invalid.indexOf(Namespace.CALLBACK_FILE) + Namespace.CALLBACK_FILE.length()
                    ))
                );
                if (getUpnpService().getConfiguration().getNamespace().isEventCallbackPath(message.getUri())
                    && message.getOperation().getMethod().equals(UpnpRequest.Method.NOTIFY))
                    return createReceivingEvent(message);
            }

        }

        throw new ProtocolCreationException("Protocol for message type not found: " + message);
    }

    @Override
	public <T> SendingNotificationAlive createSendingNotificationAlive(LocalDevice<T> localDevice) {
        return new SendingNotificationAlive(getUpnpService(), localDevice);
    }

    @Override
	public <T> SendingNotificationByebye createSendingNotificationByebye(LocalDevice<T> localDevice) {
        return new SendingNotificationByebye(getUpnpService(), localDevice);
    }

    @Override
	public SendingSearch createSendingSearch(UpnpHeader<?> searchTarget, int mxSeconds) {
        return new SendingSearch(getUpnpService(), searchTarget, mxSeconds);
    }

    @Override
	public SendingAction createSendingAction(ActionInvocation<?> actionInvocation, URL controlURL) {
        return new SendingAction(getUpnpService(), actionInvocation, controlURL);
    }

    @Override
	public SendingSubscribe createSendingSubscribe(RemoteGENASubscription subscription) throws ProtocolCreationException {
        try {
            List<NetworkAddress> activeStreamServers =
                getUpnpService().getRouter().getActiveStreamServers(
                    subscription.getService().getDevice().getIdentity().getDiscoveredOnLocalAddress()
                );
            return new SendingSubscribe(getUpnpService(), subscription, activeStreamServers);
        } catch (RouterException ex) {
            throw new ProtocolCreationException(
                "Failed to obtain local stream servers (for event callback URL creation) from router",
                ex
            );
        }
    }

    @Override
	public SendingRenewal createSendingRenewal(RemoteGENASubscription subscription) {
        return new SendingRenewal(getUpnpService(), subscription);
    }

    @Override
	public SendingUnsubscribe createSendingUnsubscribe(RemoteGENASubscription subscription) {
        return new SendingUnsubscribe(getUpnpService(), subscription);
    }

    @Override
	public SendingEvent createSendingEvent(LocalGENASubscription<?> subscription) {
        return new SendingEvent(getUpnpService(), subscription);
    }

    protected ReceivingRetrieval createReceivingRetrieval(StreamRequestMessage message) {
        return new ReceivingRetrieval(getUpnpService(), message);
    }

    protected ReceivingAction createReceivingAction(StreamRequestMessage message) {
        return new ReceivingAction(getUpnpService(), message);
    }

    protected ReceivingSubscribe createReceivingSubscribe(StreamRequestMessage message) {
        return new ReceivingSubscribe(getUpnpService(), message);
    }

    protected ReceivingUnsubscribe createReceivingUnsubscribe(StreamRequestMessage message) {
        return new ReceivingUnsubscribe(getUpnpService(), message);
    }

    protected ReceivingEvent createReceivingEvent(StreamRequestMessage message) {
        return new ReceivingEvent(getUpnpService(), message);
    }
}
