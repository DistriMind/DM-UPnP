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
import fr.distrimind.oss.upnp.common.binding.xml.ServiceDescriptorBinder;
import fr.distrimind.oss.upnp.common.binding.xml.UDA10ServiceDescriptorBinderImpl;
import fr.distrimind.oss.upnp.common.mock.MockUpnpService;
import fr.distrimind.oss.upnp.common.mock.MockUpnpServiceConfiguration;
import fr.distrimind.oss.upnp.common.model.UnsupportedDataException;
import fr.distrimind.oss.upnp.common.model.action.ActionInvocation;
import fr.distrimind.oss.upnp.common.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpRequest;
import fr.distrimind.oss.upnp.common.model.message.control.IncomingActionRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.control.IncomingActionResponseMessage;
import fr.distrimind.oss.upnp.common.model.message.header.ContentTypeHeader;
import fr.distrimind.oss.upnp.common.model.message.header.SoapActionHeader;
import fr.distrimind.oss.upnp.common.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.common.model.meta.Action;
import fr.distrimind.oss.upnp.common.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;
import fr.distrimind.oss.upnp.common.model.meta.RemoteService;
import fr.distrimind.oss.upnp.common.model.types.SoapActionType;
import fr.distrimind.oss.upnp.common.transport.impl.NetworkAddressFactoryImpl;
import fr.distrimind.oss.upnp.common.test.data.SampleData;
import fr.distrimind.oss.upnp.common.transport.impl.PullSOAPActionProcessorImpl;
import fr.distrimind.oss.upnp.common.transport.impl.RecoveringSOAPActionProcessorImpl;
import fr.distrimind.oss.upnp.common.transport.spi.SOAPActionProcessor;
import fr.distrimind.oss.upnp.common.util.io.IO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertEquals;

/**
 * @author Christian Bauer
 */
public class InvalidActionXMLProcessingTest {

    @DataProvider(name = "invalidXMLFile")
    public String[][] getInvalidXMLFile() throws Exception {
        return new String[][]{
            {"/invalidxml/control/request_missing_envelope.xml"},
            {"/invalidxml/control/request_missing_action_namespace.xml"},
            {"/invalidxml/control/request_invalid_action_namespace.xml"},
        };
    }

    @DataProvider(name = "invalidRecoverableXMLFile")
    public String[][] getInvalidRecoverableXMLFile() throws Exception {
        return new String[][]{
            {"/invalidxml/control/request_no_entityencoding.xml"},
            {"/invalidxml/control/request_wrong_termination.xml"},
        };
    }

    @DataProvider(name = "invalidUnrecoverableXMLFile")
    public String[][] getInvalidUnrecoverableXMLFile() throws Exception {
        return new String[][]{
            {"/invalidxml/control/unrecoverable/naim_unity.xml"},
        };
    }

    /* ############################## TEST FAILURE ############################ */

    @Test(dataProvider = "invalidXMLFile", expectedExceptions = UnsupportedDataException.class)
    public void readRequestDefaultFailure(String invalidXMLFile) throws Exception {
        // This should always fail!
        readRequest(invalidXMLFile, new MockUpnpService());
    }

    @Test(dataProvider = "invalidRecoverableXMLFile", expectedExceptions = UnsupportedDataException.class)
    public void readRequestRecoverableFailure(String invalidXMLFile) throws Exception {
        // This should always fail!
        readRequest(invalidXMLFile, new MockUpnpService());
    }

    @Test(dataProvider = "invalidUnrecoverableXMLFile", expectedExceptions = Exception.class)
    public void readRequestRecoveringFailure(String invalidXMLFile) throws Exception {
        // This should always fail!
        readRequest(
            invalidXMLFile,
            new MockUpnpService(new MockUpnpServiceConfiguration() {
                @Override
                public SOAPActionProcessor getSoapActionProcessor() {
                    return new RecoveringSOAPActionProcessorImpl();
                }
            })
        );
    }

    /* ############################## TEST SUCCESS ############################ */

    @Test(dataProvider = "invalidXMLFile")
    public void readRequestPull(String invalidXMLFile) throws Exception {
        readRequest(
            invalidXMLFile,
            new MockUpnpService(new MockUpnpServiceConfiguration() {
                @Override
                public SOAPActionProcessor getSoapActionProcessor() {
                    return new PullSOAPActionProcessorImpl();
                }
            })
        );
    }

    @Test(dataProvider = "invalidRecoverableXMLFile")
    public void readRequestRecovering(String invalidXMLFile) throws Exception {
        readRequest(
            invalidXMLFile,
            new MockUpnpService(new MockUpnpServiceConfiguration() {
                @Override
                public SOAPActionProcessor getSoapActionProcessor() {
                    return new RecoveringSOAPActionProcessorImpl();
                }
            })
        );
    }

    @Test
   	public void uppercaseOutputArguments() throws Exception {
   		SOAPActionProcessor processor = new RecoveringSOAPActionProcessorImpl();
   		ServiceDescriptorBinder binder = new UDA10ServiceDescriptorBinderImpl(new NetworkAddressFactoryImpl());

   		RemoteService service = SampleData.createUndescribedRemoteService();
   		service = binder.describe(
               service,
               IO.readLines(getClass().getResourceAsStream("/descriptors/service/uda10_connectionmanager.xml"))
           );

   		Action<?> action = service.getAction("GetProtocolInfo");

   		ActionInvocation<?> actionInvocation = new ActionInvocation<>(action);
   		StreamResponseMessage response = new StreamResponseMessage(
               IO.readLines(getClass().getResourceAsStream("/invalidxml/control/response_uppercase_args.xml"))
           );

   		processor.readBody(new IncomingActionResponseMessage(response), actionInvocation);
   	}

    protected void readRequest(String invalidXMLFile, UpnpService upnpService) throws Exception {
        LocalDevice<?> ld = ActionSampleData.createTestDevice(ActionSampleData.LocalTestServiceExtended.class);
        LocalService<?> svc = ld.getServices().iterator().next();

        Action<?> action = svc.getAction("SetSomeValue");
        ActionInvocation<?> actionInvocation = new ActionInvocation<>(action);

        StreamRequestMessage message = createRequestMessage(action, invalidXMLFile);
        IncomingActionRequestMessage request = new IncomingActionRequestMessage(message, svc);

        upnpService.getConfiguration().getSoapActionProcessor().readBody(request, actionInvocation);

        assertEquals(actionInvocation.getInput().iterator().next().toString(), "foo&bar");
    }

    public StreamRequestMessage createRequestMessage(Action<?> action, String xmlFile) throws Exception {
        StreamRequestMessage message =
            new StreamRequestMessage(UpnpRequest.Method.POST, URI.create("https://some.uri"));

        message.getHeaders().add(
            UpnpHeader.Type.CONTENT_TYPE,
            new ContentTypeHeader(ContentTypeHeader.DEFAULT_CONTENT_TYPE_UTF8)
        );
        message.getHeaders().add(
            UpnpHeader.Type.SOAPACTION,
            new SoapActionHeader(
                new SoapActionType(
                    action.getService().getServiceType(),
                    action.getName()
                )
            )
        );
        message.setBody(IO.readLines(getClass().getResourceAsStream(xmlFile)));
        return message;
    }
}
