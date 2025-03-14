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

package fr.distrimind.oss.upnp.common.test.control;

import fr.distrimind.oss.upnp.common.UpnpService;
import fr.distrimind.oss.upnp.common.mock.MockUpnpService;
import fr.distrimind.oss.upnp.common.model.action.ActionException;
import fr.distrimind.oss.upnp.common.model.action.ActionInvocation;
import fr.distrimind.oss.upnp.common.model.message.Connection;
import fr.distrimind.oss.upnp.common.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpRequest;
import fr.distrimind.oss.upnp.common.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.common.model.message.control.IncomingActionResponseMessage;
import fr.distrimind.oss.upnp.common.model.message.header.ContentTypeHeader;
import fr.distrimind.oss.upnp.common.model.message.header.EXTHeader;
import fr.distrimind.oss.upnp.common.model.message.header.ServerHeader;
import fr.distrimind.oss.upnp.common.model.message.header.SoapActionHeader;
import fr.distrimind.oss.upnp.common.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.common.model.message.header.UserAgentHeader;
import fr.distrimind.oss.upnp.common.model.meta.Action;
import fr.distrimind.oss.upnp.common.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;
import fr.distrimind.oss.upnp.common.model.meta.QueryStateVariableAction;
import fr.distrimind.oss.upnp.common.model.meta.Service;
import fr.distrimind.oss.upnp.common.model.types.ErrorCode;
import fr.distrimind.oss.upnp.common.model.types.SoapActionType;
import fr.distrimind.oss.upnp.common.protocol.sync.ReceivingAction;
import fr.distrimind.oss.upnp.common.util.MimeType;
import org.testng.annotations.Test;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import static org.testng.Assert.*;

/**
 * @author Christian Bauer
 */
public class ActionInvokeIncomingTest {

    public static final String SET_REQUEST = "<?xml version=\"1.0\"?>\n" +
            " <s:Envelope\n" +
            "     xmlns:s=\"https://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "     s:encodingStyle=\"https://schemas.xmlsoap.org/soap/encoding/\">\n" +
            "   <s:Body>\n" +
            "     <u:SetTarget xmlns:u=\"urn:schemas-upnp-org:service:SwitchPower:1\">\n" +
            "       <NewTargetValue>1</NewTargetValue>\n" +
            "     </u:SetTarget>\n" +
            "   </s:Body>\n" +
            " </s:Envelope>";

    public static final String GET_REQUEST = "<?xml version=\"1.0\"?>\n" +
            " <s:Envelope\n" +
            "     xmlns:s=\"https://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "     s:encodingStyle=\"https://schemas.xmlsoap.org/soap/encoding/\">\n" +
            "   <s:Body>\n" +
            "     <u:GetTarget xmlns:u=\"urn:schemas-upnp-org:service:SwitchPower:1\"/>\n" +
            "   </s:Body>\n" +
            " </s:Envelope>";

    public static final String QUERY_STATE_VARIABLE_REQUEST = "<?xml version=\"1.0\"?>\n" +
            " <s:Envelope\n" +
            "     xmlns:s=\"https://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "     s:encodingStyle=\"https://schemas.xmlsoap.org/soap/encoding/\">\n" +
            "   <s:Body>\n" +
            "     <u:QueryStateVariable xmlns:u=\"urn:schemas-upnp-org:control-1-0\">\n" +
            "       <varName>Status</varName>\n" +
            "     </u:QueryStateVariable>\n" +
            "   </s:Body>\n" +
            " </s:Envelope>";

    @Test
    public void incomingRemoteCallGet() throws Exception {
        incomingRemoteCallGet(ActionSampleData.createTestDevice());
    }

    @Test
    public void incomingRemoteCallClientInfo() throws Exception {
        UpnpMessage<?> response =
                incomingRemoteCallGet(ActionSampleData.createTestDevice(ActionSampleData.LocalTestServiceWithClientInfo.class));

        assertEquals(response.getHeaders().size(), 4);
        assertEquals(response.getHeaders().getFirstHeader("X-MY-HEADER"), "foobar");
    }

    public <T> IncomingActionResponseMessage incomingRemoteCallGet(LocalDevice<T> ld) throws Exception {

        MockUpnpService upnpService = new MockUpnpService();
        LocalService<T> service = ld.getServices().iterator().next();
        upnpService.getRegistry().addDevice(ld);

        Action<LocalService<T>> action = service.getAction("GetTarget");

        URI controlURI = upnpService.getConfiguration().getNamespace().getControlPath(service);
        StreamRequestMessage request = getStreamRequestMessage(controlURI);
        addMandatoryRequestHeaders(service, action, request);
        request.setBody(UpnpMessage.BodyType.STRING, GET_REQUEST);

        ReceivingAction prot = new ReceivingAction(upnpService, request);

        prot.run();

        StreamResponseMessage response = prot.getOutputMessage();

        assertNotNull(response);
        assertFalse(response.getOperation().isFailed(), "response : "+response.getOperation());
        assertTrue(response.getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE, ContentTypeHeader.class).isUDACompliantXML());
        assertNotNull(response.getHeaders().getFirstHeader(UpnpHeader.Type.EXT, EXTHeader.class));
        assertEquals(
            response.getHeaders().getFirstHeader(UpnpHeader.Type.SERVER, ServerHeader.class).getValue(),
            new ServerHeader().getValue()
        );

        IncomingActionResponseMessage responseMessage = new IncomingActionResponseMessage(response);
        ActionInvocation<?> responseInvocation = new ActionInvocation<>(action);
        upnpService.getConfiguration().getSoapActionProcessor().readBody(responseMessage, responseInvocation);

        assertNotNull(responseInvocation.getOutput("RetTargetValue"));
        return responseMessage;
    }

    private static StreamRequestMessage getStreamRequestMessage(URI controlURI) {
        StreamRequestMessage request = new StreamRequestMessage(UpnpRequest.Method.POST, controlURI);
        request.setConnection(new Connection() {
            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public InetAddress getRemoteAddress() {
                try {
                    return InetAddress.getByName("10.0.0.1");
                } catch (UnknownHostException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public InetAddress getLocalAddress() {
                try {
                    return InetAddress.getByName("10.0.0.2");
                } catch (UnknownHostException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        return request;
    }

    @Test
    public void incomingRemoteCallGetConcurrent() throws Exception {

        // Register local device and its service
        MockUpnpService upnpService = new MockUpnpService(false, false, true);
        LocalDevice<ActionSampleData.LocalTestServiceThrowsException> ld = ActionSampleData.createTestDevice(ActionSampleData.LocalTestServiceThrowsException.class);
        LocalService<ActionSampleData.LocalTestServiceThrowsException> service = ld.getServices().iterator().next();
        upnpService.getRegistry().addDevice(ld);

        // TODO: Use a latch instead of waiting
        int i = 0;
        while (i < 10) {
            upnpService.getConfiguration().startThread(new ConcurrentGetTest(upnpService, service));
            i++;
        }

        // Wait for the threads to finish
        Thread.sleep(2000);
    }

    static class ConcurrentGetTest implements Runnable {
        private final UpnpService upnpService;
        private final LocalService<?> service;

        ConcurrentGetTest(UpnpService upnpService, LocalService<?> service) {
            this.upnpService = upnpService;
            this.service = service;
        }

        @Override
		public void run() {
            Action<?> action = service.getAction("GetTarget");

            URI controlURI = upnpService.getConfiguration().getNamespace().getControlPath(service);
            StreamRequestMessage request = new StreamRequestMessage(UpnpRequest.Method.POST, controlURI);
            request.getHeaders().add(
                    UpnpHeader.Type.CONTENT_TYPE,
                    new ContentTypeHeader(ContentTypeHeader.DEFAULT_CONTENT_TYPE_UTF8)
            );

            SoapActionType actionType = new SoapActionType(service.getServiceType(), action.getName());
            request.getHeaders().add(UpnpHeader.Type.SOAPACTION, new SoapActionHeader(actionType));
            request.setBody(UpnpMessage.BodyType.STRING, GET_REQUEST);

            ReceivingAction prot = new ReceivingAction(upnpService, request);

            prot.run();

            StreamResponseMessage response = prot.getOutputMessage();

            assertNotNull(response);
            assertFalse(response.getOperation().isFailed());
            assertTrue(response.getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE, ContentTypeHeader.class).isUDACompliantXML());
            assertNotNull(response.getHeaders().getFirstHeader(UpnpHeader.Type.EXT, EXTHeader.class));
            assertEquals(
                response.getHeaders().getFirstHeader(UpnpHeader.Type.SERVER, ServerHeader.class).getValue(),
                new ServerHeader().getValue()
            );

            IncomingActionResponseMessage responseMessage = new IncomingActionResponseMessage(response);
            ActionInvocation<?> responseInvocation = new ActionInvocation<>(action);
            upnpService.getConfiguration().getSoapActionProcessor().readBody(responseMessage, responseInvocation);

            assertNotNull(responseInvocation.getOutput("RetTargetValue"));
        }
    }


    @Test
    public void incomingRemoteCallSet() throws Exception {

        // Register local device and its service
        MockUpnpService upnpService = new MockUpnpService();
        LocalDevice<?> ld = ActionSampleData.createTestDevice();
        LocalService<?> service = ld.getServices().iterator().next();
        upnpService.getRegistry().addDevice(ld);

        Action<?> action = service.getAction("SetTarget");

        URI controlURI = upnpService.getConfiguration().getNamespace().getControlPath(service);
        StreamRequestMessage request = new StreamRequestMessage(UpnpRequest.Method.POST, controlURI);
        addMandatoryRequestHeaders(service, action, request);
        request.setBody(UpnpMessage.BodyType.STRING, SET_REQUEST);

        ReceivingAction prot = new ReceivingAction(upnpService, request);

        prot.run();

        StreamResponseMessage response = prot.getOutputMessage();

        assertNotNull(response);
        assertFalse(response.getOperation().isFailed());
        assertTrue(response.getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE, ContentTypeHeader.class).isUDACompliantXML());
        assertNotNull(response.getHeaders().getFirstHeader(UpnpHeader.Type.EXT, EXTHeader.class));
        assertEquals(
            response.getHeaders().getFirstHeader(UpnpHeader.Type.SERVER, ServerHeader.class).getValue(),
            new ServerHeader().getValue()
        );

        IncomingActionResponseMessage responseMessage = new IncomingActionResponseMessage(response);
        ActionInvocation<?> responseInvocation = new ActionInvocation<>(action);
        upnpService.getConfiguration().getSoapActionProcessor().readBody(responseMessage, responseInvocation);

        assertEquals(responseInvocation.getOutput().size(), 0);

    }

    @Test
    public void incomingRemoteCallControlURINotFound() throws Exception {

        // Register local device and its service
        MockUpnpService upnpService = new MockUpnpService();
        LocalDevice<?> ld = ActionSampleData.createTestDevice();
        LocalService<?> service = ld.getServices().iterator().next();
        upnpService.getRegistry().addDevice(ld);

        Action<?> action = service.getAction("SetTarget");

        StreamRequestMessage request = new StreamRequestMessage(UpnpRequest.Method.POST, URI.create("/some/random/123/uri"));
        addMandatoryRequestHeaders(service, action, request);
        request.setBody(UpnpMessage.BodyType.STRING, SET_REQUEST);

        ReceivingAction prot = new ReceivingAction(upnpService, request);

        prot.run();

        UpnpMessage<?> response = prot.getOutputMessage();

        assertNull(response);
        // The StreamServer will send a 404 response
    }

    @Test
    public void incomingRemoteCallMethodException() throws Exception {

        // Register local device and its service
        MockUpnpService upnpService = new MockUpnpService();
        LocalDevice<?> ld = ActionSampleData.createTestDevice(ActionSampleData.LocalTestServiceThrowsException.class);
        LocalService<?> service = ld.getServices().iterator().next();
        upnpService.getRegistry().addDevice(ld);

        Action<?> action = service.getAction("SetTarget");

        URI controlURI = upnpService.getConfiguration().getNamespace().getControlPath(service);
        StreamRequestMessage request = new StreamRequestMessage(UpnpRequest.Method.POST, controlURI);
        addMandatoryRequestHeaders(service, action, request);

        request.setBody(UpnpMessage.BodyType.STRING, SET_REQUEST);

        ReceivingAction prot = new ReceivingAction(upnpService, request);

        prot.run();

        StreamResponseMessage response = prot.getOutputMessage();

        assertNotNull(response);
        assertTrue(response.getOperation().isFailed());
        assertTrue(response.getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE, ContentTypeHeader.class).isUDACompliantXML());
        assertNotNull(response.getHeaders().getFirstHeader(UpnpHeader.Type.EXT, EXTHeader.class));
        assertEquals(
            response.getHeaders().getFirstHeader(UpnpHeader.Type.SERVER, ServerHeader.class).getValue(),
            new ServerHeader().getValue()
        );

        IncomingActionResponseMessage responseMessage = new IncomingActionResponseMessage(response);
        ActionInvocation<?> responseInvocation = new ActionInvocation<>(action);
        upnpService.getConfiguration().getSoapActionProcessor().readBody(responseMessage, responseInvocation);

        ActionException ex = responseInvocation.getFailure();
        assertNotNull(ex);

        assertEquals(ex.getMessage(), ErrorCode.ACTION_FAILED.getDescription() + ". Something is wrong.");

    }

    @Test
    public void incomingRemoteCallNoContentType() throws Exception {

        // Register local device and its service
        MockUpnpService upnpService = new MockUpnpService();
        LocalDevice<?> ld = ActionSampleData.createTestDevice();
        LocalService<?> service = ld.getServices().iterator().next();
        upnpService.getRegistry().addDevice(ld);

        Action<?> action = service.getAction("GetTarget");

        URI controlURI = upnpService.getConfiguration().getNamespace().getControlPath(service);
        StreamRequestMessage request = new StreamRequestMessage(UpnpRequest.Method.POST, controlURI);
        SoapActionType actionType = new SoapActionType(service.getServiceType(), action.getName());
        request.getHeaders().add(UpnpHeader.Type.SOAPACTION, new SoapActionHeader(actionType));
        // NO CONTENT TYPE!
        request.setBody(UpnpMessage.BodyType.STRING, GET_REQUEST);

        ReceivingAction prot = new ReceivingAction(upnpService, request);

        prot.run();

        StreamResponseMessage response = prot.getOutputMessage();

        assertNotNull(response);
        assertFalse(response.getOperation().isFailed());
        assertTrue(response.getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE, ContentTypeHeader.class).isUDACompliantXML());
        assertNotNull(response.getHeaders().getFirstHeader(UpnpHeader.Type.EXT, EXTHeader.class));
        assertEquals(
            response.getHeaders().getFirstHeader(UpnpHeader.Type.SERVER, ServerHeader.class).getValue(),
            new ServerHeader().getValue()
        );

        IncomingActionResponseMessage responseMessage = new IncomingActionResponseMessage(response);
        ActionInvocation<?> responseInvocation = new ActionInvocation<>(action);
        upnpService.getConfiguration().getSoapActionProcessor().readBody(responseMessage, responseInvocation);

        assertNotNull(responseInvocation.getOutput("RetTargetValue"));

    }

    @Test
    public void incomingRemoteCallWrongContentType() throws Exception {

        MockUpnpService upnpService = new MockUpnpService();

        StreamRequestMessage request = new StreamRequestMessage(UpnpRequest.Method.POST, URI.create("/some/random/123/uri"));
        request.getHeaders().add(
                UpnpHeader.Type.CONTENT_TYPE,
                new ContentTypeHeader(MimeType.valueOf("some/randomtype"))
        );
        request.setBody(UpnpMessage.BodyType.STRING, SET_REQUEST);

        ReceivingAction prot = new ReceivingAction(upnpService, request);

        prot.run();

        StreamResponseMessage response = prot.getOutputMessage();

        assertNotNull(response);
        assertEquals(response.getOperation().getStatusCode(), UpnpResponse.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode());
    }

    @Test
    public void incomingRemoteCallQueryStateVariable() throws Exception {

        // Register local device and its service
        MockUpnpService upnpService = new MockUpnpService();
        LocalDevice<?> ld = ActionSampleData.createTestDevice();
        LocalService<?> service = ld.getServices().iterator().next();
        upnpService.getRegistry().addDevice(ld);

        Action<?> action = service.getAction(QueryStateVariableAction.ACTION_NAME);

        URI controlURI = upnpService.getConfiguration().getNamespace().getControlPath(service);
        StreamRequestMessage request = new StreamRequestMessage(UpnpRequest.Method.POST, controlURI);
        request.getHeaders().add(
                UpnpHeader.Type.CONTENT_TYPE,
                new ContentTypeHeader(ContentTypeHeader.DEFAULT_CONTENT_TYPE_UTF8)
        );
        request.getHeaders().add(
                UpnpHeader.Type.SOAPACTION,
                new SoapActionHeader(
                        new SoapActionType(
                                SoapActionType.MAGIC_CONTROL_NS, SoapActionType.MAGIC_CONTROL_TYPE, null, action.getName()
                        )
                )

        );
        request.setBody(UpnpMessage.BodyType.STRING, QUERY_STATE_VARIABLE_REQUEST);

        ReceivingAction prot = new ReceivingAction(upnpService, request);

        prot.run();

        StreamResponseMessage response = prot.getOutputMessage();

        assertNotNull(response);
        assertFalse(response.getOperation().isFailed());
        assertTrue(response.getHeaders().getFirstHeader(UpnpHeader.Type.CONTENT_TYPE, ContentTypeHeader.class).isUDACompliantXML());
        assertNotNull(response.getHeaders().getFirstHeader(UpnpHeader.Type.EXT, EXTHeader.class));
        assertEquals(
            response.getHeaders().getFirstHeader(UpnpHeader.Type.SERVER, ServerHeader.class).getValue(),
            new ServerHeader().getValue()
        );

        IncomingActionResponseMessage responseMessage = new IncomingActionResponseMessage(response);
        ActionInvocation<?> responseInvocation = new ActionInvocation<>(action);
        upnpService.getConfiguration().getSoapActionProcessor().readBody(responseMessage, responseInvocation);

        assertEquals(responseInvocation.getOutput().iterator().next().getArgument().getName(), "return");
        assertEquals(responseInvocation.getOutput().iterator().next().toString(), "0");
    }


    protected void addMandatoryRequestHeaders(Service<?, ?, ?> service, Action<?> action, StreamRequestMessage request) {
        request.getHeaders().add(
                UpnpHeader.Type.CONTENT_TYPE,
                new ContentTypeHeader(ContentTypeHeader.DEFAULT_CONTENT_TYPE_UTF8)
        );

        SoapActionType actionType = new SoapActionType(service.getServiceType(), action.getName());
        request.getHeaders().add(UpnpHeader.Type.SOAPACTION, new SoapActionHeader(actionType));
        // Not mandatory but only for the tests
        request.getHeaders().add(UpnpHeader.Type.USER_AGENT, new UserAgentHeader("foo/bar"));
    }

}