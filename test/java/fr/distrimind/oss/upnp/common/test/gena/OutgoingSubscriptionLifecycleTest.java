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

package fr.distrimind.oss.upnp.common.test.gena;

import fr.distrimind.oss.upnp.common.UpnpService;
import fr.distrimind.oss.upnp.common.controlpoint.SubscriptionCallback;
import fr.distrimind.oss.upnp.common.mock.MockRouter;
import fr.distrimind.oss.upnp.common.mock.MockUpnpService;
import fr.distrimind.oss.upnp.common.model.UnsupportedDataException;
import fr.distrimind.oss.upnp.common.model.gena.CancelReason;
import fr.distrimind.oss.upnp.common.model.gena.GENASubscription;
import fr.distrimind.oss.upnp.common.model.gena.RemoteGENASubscription;
import fr.distrimind.oss.upnp.common.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpRequest;
import fr.distrimind.oss.upnp.common.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.common.model.message.gena.IncomingEventRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.gena.OutgoingEventRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.header.CallbackHeader;
import fr.distrimind.oss.upnp.common.model.message.header.SubscriptionIdHeader;
import fr.distrimind.oss.upnp.common.model.message.header.TimeoutHeader;
import fr.distrimind.oss.upnp.common.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.common.model.meta.RemoteDevice;
import fr.distrimind.oss.upnp.common.model.meta.RemoteService;
import fr.distrimind.oss.upnp.common.model.state.StateVariableValue;
import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerFourBytes;
import fr.distrimind.oss.upnp.common.protocol.ProtocolCreationException;
import fr.distrimind.oss.upnp.common.test.data.SampleData;
import fr.distrimind.oss.upnp.common.util.URIUtil;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import static org.testng.Assert.*;

public class OutgoingSubscriptionLifecycleTest {

    public static final String STATUS = "Status";
    public static final String TARGET = "Target";
    public static final String UUID_1234 = "uuid:1234";

    @Test
    public void subscriptionLifecycle() throws Exception {

        MockUpnpService upnpService = new MockUpnpService() {
            @Override
            protected MockRouter createRouter() {
                return new MockRouter(getConfiguration(), getProtocolFactory()) {
                @Override
                public List<StreamResponseMessage> getStreamResponseMessages() {

                    return List.of(
                            createSubscribeResponseMessage(),
                            createUnsubscribeResponseMessage()

                    );
                }
                };
            }
        };

        final List<Boolean> testAssertions = new ArrayList<>();

        // Register remote device and its service
        RemoteDevice device = SampleData.createRemoteDevice();
        upnpService.getRegistry().addDevice(device);

        RemoteService service = SampleData.getFirstService(device);

        SubscriptionCallback callback = new SubscriptionCallback(service) {

            @Override
            protected void failed(GENASubscription<?> subscription,
                                  UpnpResponse responseStatus,
                                  Exception exception,
                                  String defaultMsg) {
                testAssertions.add(false);
            }

            @Override
            public void established(GENASubscription<?> subscription) {
                testAssertions.add(true);
            }

            @Override
            public void ended(GENASubscription<?> subscription, CancelReason reason, UpnpResponse responseStatus) {
                assertNull(reason);
                assertEquals(responseStatus.getStatusCode(), UpnpResponse.Status.OK.getStatusCode());
                testAssertions.add(true);
            }

            @Override
			public void eventReceived(GENASubscription<?> subscription) {
                assertEquals(subscription.getCurrentValues().get(STATUS).toString(), "0");
                assertEquals(subscription.getCurrentValues().get(TARGET).toString(), "1");
                testAssertions.add(true);
            }

            @Override
			public void eventsMissed(GENASubscription<?> subscription, int numberOfMissedEvents) {
                testAssertions.add(false);
            }

        };

        upnpService.getControlPoint().execute(callback);

        // Subscription process OK?
        for (Boolean testAssertion : testAssertions) {
            assertTrue(testAssertion);
        }

        // Simulate received event
        upnpService.getProtocolFactory().createReceivingSync(
                createEventRequestMessage(upnpService, callback)
        ).run();

        assertEquals(callback.getSubscription().getCurrentSequence().getValue(), Long.valueOf(0));
        assertEquals(callback.getSubscription().getSubscriptionId(), UUID_1234);
        assertEquals(callback.getSubscription().getActualDurationSeconds(), 180);

        List<URL> callbackURLs = ((RemoteGENASubscription) callback.getSubscription())
                .getEventCallbackURLs(upnpService.getRouter().getActiveStreamServers(null), upnpService.getConfiguration().getNamespace());

        callback.end();

        assertNull(callback.getSubscription());

        assertEquals(testAssertions.size(), 3);
        for (Boolean testAssertion : testAssertions) {
            assertTrue(testAssertion);
        }

        List<StreamRequestMessage> sentMessages = upnpService.getRouter().getSentStreamRequestMessages();
        assertEquals(sentMessages.size(), 2);
        assertEquals(
                (sentMessages.get(0).getOperation()).getMethod(),
                UpnpRequest.Method.SUBSCRIBE
        );
        assertEquals(
            sentMessages.get(0).getHeaders().getFirstHeader(UpnpHeader.Type.TIMEOUT, TimeoutHeader.class).getValue(),
                Integer.valueOf(1800)
        );

        assertEquals(callbackURLs.size(), 1);
        assertEquals(
            sentMessages.get(0).getHeaders().getFirstHeader(UpnpHeader.Type.CALLBACK, CallbackHeader.class).getValue().get(0),
                callbackURLs.get(0)
        );

        assertEquals(
                (sentMessages.get(1).getOperation()).getMethod(),
                UpnpRequest.Method.UNSUBSCRIBE
        );
        assertEquals(
            sentMessages.get(1).getHeaders().getFirstHeader(UpnpHeader.Type.SID, SubscriptionIdHeader.class).getValue(),
                UUID_1234
        );
    }

    @Test
    public void subscriptionLifecycleNotifyBeforeResponse() throws Exception {

        final RemoteDevice device = SampleData.createRemoteDevice();
        final RemoteService service = SampleData.getFirstService(device);

        final StreamResponseMessage subscribeResponseMessage = createSubscribeResponseMessage();
        final Semaphore subscribeResponseSemaphore = new Semaphore(0);

        final MockUpnpService upnpService = new MockUpnpService() {
            @Override
            protected MockRouter createRouter() {
                return new MockRouter(getConfiguration(), getProtocolFactory()) {
                    @Override
                    public StreamResponseMessage getStreamResponseMessage(StreamRequestMessage request) {
                        try {
                            // bloc subscription response until the first notification
                            subscribeResponseSemaphore.acquire();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        return subscribeResponseMessage;
                    }
                };
            }
        };

        final List<Boolean> testAssertions = new ArrayList<>();
        final List<Boolean> notificationCalled = new ArrayList<>();

        // Register remote device and its service
        upnpService.getRegistry().addDevice(device);

        // send subscription request
        final SubscriptionCallback callback = new SubscriptionCallback(service) {

            @Override
            protected void failed(GENASubscription<?> subscription,
                                  UpnpResponse responseStatus,
                                  Exception exception,
                                  String defaultMsg) {
                testAssertions.add(false);
            }

            @Override
            public void established(GENASubscription<?> subscription) {
                testAssertions.add(true);
            }

            @Override
            public void ended(GENASubscription<?> subscription, CancelReason reason, UpnpResponse responseStatus) {
            }

            @Override
			public void eventReceived(GENASubscription<?> subscription) {
                assertEquals(subscription.getCurrentValues().get(STATUS).toString(), "0");
                assertEquals(subscription.getCurrentValues().get(TARGET).toString(), "1");
                testAssertions.add(true);
                notificationCalled.add(true);
            }

            @Override
			public void eventsMissed(GENASubscription<?> subscription, int numberOfMissedEvents) {
                testAssertions.add(false);
            }

        };

        // send subscription request is a separate thread
        Thread subscribeThread = upnpService.getConfiguration().startThread(() -> upnpService.getControlPoint().execute(callback));

        // generate notification in a separate thread
        // use a dummy GENASubscription for that to have a valid subscriptionId
        Thread notifyThread = getThread(service, upnpService, testAssertions);

        // give time to process notification
        Thread.sleep(1000);

        // notification should not have been received before the subscribe response
        assertEquals(notificationCalled.size(), 0);

        // unlock subscription response
        subscribeResponseSemaphore.release();

        subscribeThread.join();
        notifyThread.join();

        // notification should have been received after the subscribe response
        assertEquals(notificationCalled.size(), 1);

        for (Boolean testAssertion : testAssertions) {
            assertTrue(testAssertion);
        }
    }

    private Thread getThread(RemoteService service, MockUpnpService upnpService, List<Boolean> testAssertions) {
        final GENASubscription<?> subscription = getGenaSubscription(service);

        Thread notifyThread = upnpService.getConfiguration().startThread(() -> {
            // Simulate received event before the subscription response
            try {
                upnpService.getProtocolFactory().createReceivingSync(
                        createEventRequestMessage(upnpService, service, subscription)
                ).run();
            } catch (ProtocolCreationException e) {
                testAssertions.add(false);
            }

        });
        return notifyThread;
    }

    private static GENASubscription<?> getGenaSubscription(RemoteService service) {
        final GENASubscription<?> subscription = new RemoteGENASubscription(service, 180) {
             @Override
             public void established() {
             }
             @Override
             public void eventReceived() {
             }
             @Override
             public void invalidMessage(UnsupportedDataException ex) {
             }
             @Override
             public void failed(UpnpResponse responseStatus) {
             }
             @Override
             public void ended(CancelReason reason, UpnpResponse responseStatus) {

             }
             @Override
             public void eventsMissed(int numberOfMissedEvents) {
             }
         };
        subscription.setSubscriptionId(UUID_1234);
        return subscription;
    }


    protected StreamResponseMessage createSubscribeResponseMessage() {
        StreamResponseMessage msg = new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.OK));
        msg.getHeaders().add(
                UpnpHeader.Type.SID, new SubscriptionIdHeader(UUID_1234)
        );
        msg.getHeaders().add(
                UpnpHeader.Type.TIMEOUT, new TimeoutHeader(180)
        );
        return msg;
    }

    protected StreamResponseMessage createUnsubscribeResponseMessage() {
        return new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.OK));
    }

    protected IncomingEventRequestMessage createEventRequestMessage(final UpnpService upnpService, final SubscriptionCallback callback) {

        List<StateVariableValue<?>> values = new ArrayList<>();
        values.add(
                new StateVariableValue<>(callback.getService().getStateVariable(STATUS), false)
        );
        values.add(
                new StateVariableValue<>(callback.getService().getStateVariable(TARGET), true)
        );

        OutgoingEventRequestMessage outgoing = new OutgoingEventRequestMessage(
                callback.getSubscription(),
                URIUtil.toURL(URI.create("http://10.0.0.123/this/is/ignored/anyway")),
                new UnsignedIntegerFourBytes(0),
                values
        );
        outgoing.getOperation().setUri(
                upnpService.getConfiguration().getNamespace().getEventCallbackPath(callback.getService())
        );

        upnpService.getConfiguration().getGenaEventProcessor().writeBody(outgoing);

        return new IncomingEventRequestMessage(outgoing, ((RemoteGENASubscription)callback.getSubscription()).getService());
    }


    protected IncomingEventRequestMessage createEventRequestMessage(final UpnpService upnpService, final RemoteService service, final GENASubscription<?> subscription) {

        List<StateVariableValue<?>> values = new ArrayList<>();
        values.add(
                new StateVariableValue<>(service.getStateVariable(STATUS), false)
        );
        values.add(
                new StateVariableValue<>(service.getStateVariable(TARGET), true)
        );

        OutgoingEventRequestMessage outgoing = new OutgoingEventRequestMessage(
                subscription,
                URIUtil.toURL(URI.create("http://10.0.0.123/this/is/ignored/anyway")),
                new UnsignedIntegerFourBytes(0),
                values
        );
        outgoing.getOperation().setUri(
                upnpService.getConfiguration().getNamespace().getEventCallbackPath(service)
        );

        upnpService.getConfiguration().getGenaEventProcessor().writeBody(outgoing);

        return new IncomingEventRequestMessage(outgoing, service);
    }

 }
