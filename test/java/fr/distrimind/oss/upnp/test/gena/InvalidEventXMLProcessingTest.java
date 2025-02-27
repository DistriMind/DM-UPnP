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

package fr.distrimind.oss.upnp.test.gena;

import fr.distrimind.oss.upnp.UpnpService;
import fr.distrimind.oss.upnp.binding.xml.ServiceDescriptorBinder;
import fr.distrimind.oss.upnp.binding.xml.UDA10ServiceDescriptorBinderImpl;
import fr.distrimind.oss.upnp.mock.MockUpnpService;
import fr.distrimind.oss.upnp.mock.MockUpnpServiceConfiguration;
import fr.distrimind.oss.upnp.model.UnsupportedDataException;
import fr.distrimind.oss.upnp.model.gena.CancelReason;
import fr.distrimind.oss.upnp.model.gena.RemoteGENASubscription;
import fr.distrimind.oss.upnp.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.model.message.UpnpMessage.BodyType;
import fr.distrimind.oss.upnp.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.model.message.gena.IncomingEventRequestMessage;
import fr.distrimind.oss.upnp.model.message.gena.OutgoingEventRequestMessage;
import fr.distrimind.oss.upnp.model.meta.RemoteService;
import fr.distrimind.oss.upnp.model.state.StateVariableValue;
import fr.distrimind.oss.upnp.model.types.UnsignedIntegerFourBytes;
import fr.distrimind.oss.upnp.transport.impl.NetworkAddressFactoryImpl;
import fr.distrimind.oss.upnp.test.data.SampleData;
import fr.distrimind.oss.upnp.transport.impl.PullGENAEventProcessorImpl;
import fr.distrimind.oss.upnp.transport.impl.RecoveringGENAEventProcessorImpl;
import fr.distrimind.oss.upnp.transport.spi.GENAEventProcessor;
import fr.distrimind.oss.upnp.util.io.IO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class InvalidEventXMLProcessingTest {

    @DataProvider(name = "invalidXMLFile")
    public String[][] getInvalidXMLFile() throws Exception {
        return new String[][]{
            {"/invalidxml/event/invalid_root_element.xml"},
        };
    }

    @DataProvider(name = "invalidRecoverableXMLFile")
    public String[][] getInvalidRecoverableXMLFile() throws Exception {
        return new String[][]{
            {"/invalidxml/event/truncated.xml"},
            {"/invalidxml/event/orange_liveradio.xml"},
        };
    }

    // TODO: Shouldn't these be failures of the LastChangeParser?
    // The GENA parser does the right thing for most of them, no?
    @DataProvider(name = "invalidUnrecoverableXMLFile")
    public String[][] getInvalidUnrecoverableXMLFile() throws Exception {
        return new String[][]{//TODO the commented tests now pass with JSoup but should not ?
            //{"/invalidxml/event/unrecoverable/denon_avr4306.xml"},
            //{"/invalidxml/event/unrecoverable/philips_np2900.xml"},
            //{"/invalidxml/event/unrecoverable/philips_sla5220.xml"},
            //{"/invalidxml/event/unrecoverable/terratec_noxon2.xml"},
            //{"/invalidxml/event/unrecoverable/marantz_mcr603.xml"},
            {"/invalidxml/event/unrecoverable/teac_wap4500.xml"},
            {"/invalidxml/event/unrecoverable/technisat_digi_hd8+.xml"},
        };
    }

    /* ############################## TEST FAILURE ############################ */

    @Test(dataProvider = "invalidXMLFile", expectedExceptions = UnsupportedDataException.class)
    public void readDefaultFailure(String invalidXMLFile) throws Exception {
        // This should always fail!
        read(invalidXMLFile,new MockUpnpService());
    }

    @Test(dataProvider = "invalidRecoverableXMLFile", expectedExceptions = UnsupportedDataException.class)
    public void readRecoverableFailure(String invalidXMLFile) throws Exception {
        // This should always fail!
        read(invalidXMLFile,new MockUpnpService());
    }

    @Test(dataProvider = "invalidUnrecoverableXMLFile", expectedExceptions = Exception.class)
    public void readRecoveringFailure(String invalidXMLFile) throws Exception {
        // This should always fail!
        read(
            invalidXMLFile,
            new MockUpnpService(new MockUpnpServiceConfiguration() {
                @Override
                public GENAEventProcessor getGenaEventProcessor() {
                    return new RecoveringGENAEventProcessorImpl();
                }
            })
        );
    }

    /* ############################## TEST SUCCESS ############################ */

    @Test(dataProvider = "invalidXMLFile")
    public void readPull(String invalidXMLFile) throws Exception {
        read(
            invalidXMLFile,
            new MockUpnpService(new MockUpnpServiceConfiguration() {
                @Override
                public GENAEventProcessor getGenaEventProcessor() {
                    return new PullGENAEventProcessorImpl();
                }
            })
        );
    }

    @Test(dataProvider = "invalidRecoverableXMLFile")
    public void readRecovering(String invalidXMLFile) throws Exception {
        read(
            invalidXMLFile,
            new MockUpnpService(new MockUpnpServiceConfiguration() {
                @Override
                public GENAEventProcessor getGenaEventProcessor() {
                    return new RecoveringGENAEventProcessorImpl();
                }
            })
        );
    }

    protected void read(String invalidXMLFile, UpnpService upnpService) throws Exception {
        ServiceDescriptorBinder binder = new UDA10ServiceDescriptorBinderImpl(new NetworkAddressFactoryImpl());
        RemoteService service = SampleData.createUndescribedRemoteService();
        service = binder.describe(service, IO.readLines(
            getClass().getResourceAsStream("/descriptors/service/uda10_avtransport.xml"))
        );

        RemoteGENASubscription subscription = new RemoteGENASubscription(service, 1800) {
            @Override
			public void failed(UpnpResponse responseStatus) {
            }

            @Override
			public void ended(CancelReason reason, UpnpResponse responseStatus) {
            }

            @Override
			public void eventsMissed(int numberOfMissedEvents) {
            }

            @Override
			public void established() {
            }

            @Override
			public void eventReceived() {
            }

            @Override
			public void invalidMessage(UnsupportedDataException ex) {
            }
        };
        subscription.receive(new UnsignedIntegerFourBytes(0), new ArrayList<>());

        OutgoingEventRequestMessage outgoingCall =
            new OutgoingEventRequestMessage(subscription, SampleData.getLocalBaseURL());

        upnpService.getConfiguration().getGenaEventProcessor().writeBody(outgoingCall);

        StreamRequestMessage incomingStream = new StreamRequestMessage(outgoingCall);

        IncomingEventRequestMessage message = new IncomingEventRequestMessage(incomingStream, service);
        message.setBody(BodyType.STRING, IO.readLines(getClass().getResourceAsStream(invalidXMLFile)));

        upnpService.getConfiguration().getGenaEventProcessor().readBody(message);

        // All of the messages must have a LastChange state variable, and we should be able to parse
        // the XML value of that state variable
        boolean found = false;
        for (StateVariableValue<RemoteService> stateVariableValue : message.getStateVariableValues()) {
            if ("LastChange".equals(stateVariableValue.getStateVariable().getName())
                && stateVariableValue.getValue() != null) {
                found = true;
                String lastChange = (String) stateVariableValue.getValue();
                Map<String, String> lastChangeValues = parseLastChangeXML(lastChange);
                assertFalse(lastChangeValues.isEmpty());
                break;
            }
        }

        assertTrue(found);
    }


    @SuppressWarnings("PMD")
    public static void parseLastChangeXML(Element e, Map<String, String> m) throws ParserConfigurationException {
        Elements nl=e.children();
        if (!nl.isEmpty())
        {
            for (Element e2 : nl)
                parseLastChangeXML(e2, m);
        }
        else {
            String att="val";
            if (e.hasAttr(att))
                m.put(e.tagName(), e.attr(att));
        }
    }
    public static Map<String, String> parseLastChangeXML(String text) throws ParserConfigurationException, IOException, SAXException {
        Document d= Jsoup.parse(text, "", Parser.xmlParser());
        Map<String, String> r=new HashMap<>();
        parseLastChangeXML(d, r);
        return r;
    }
}
