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

package fr.distrimind.oss.upnp.android.transport;

import fr.distrimind.oss.flexilogxml.common.FlexiLogXML;
import fr.distrimind.oss.flexilogxml.common.log.Level;
import fr.distrimind.oss.upnp.common.Log;
import fr.distrimind.oss.upnp.common.UpnpServiceConfiguration;
import fr.distrimind.oss.upnp.common.mock.MockProtocolFactory;
import fr.distrimind.oss.upnp.common.mock.MockRouter;
import fr.distrimind.oss.upnp.common.mock.MockUpnpServiceConfiguration;
import fr.distrimind.oss.upnp.common.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpRequest;
import fr.distrimind.oss.upnp.common.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.common.platform.Platform;
import fr.distrimind.oss.upnp.common.protocol.ProtocolCreationException;
import fr.distrimind.oss.upnp.common.protocol.ReceivingSync;
import fr.distrimind.oss.upnp.common.transport.spi.StreamClient;
import fr.distrimind.oss.upnp.common.transport.spi.StreamServer;
import fr.distrimind.oss.upnp.common.transport.spi.UpnpStream;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;

import static org.testng.Assert.*;


abstract public class StreamServerClientTest {

    final private static DMLogger log = Log.getLogger(StreamServerClientTest.class);

    public static final String TEST_HOST = "127.0.0.1";
    public int testPort = 0;

    public UpnpServiceConfiguration configurationServer;
    public UpnpServiceConfiguration configurationClient;

    public MockProtocolFactory protocolFactory;

    public MockRouter router;

    public StreamServer<?> server;
    public StreamClient<?> client;
    public TestProtocol lastExecutedServerProtocol;

    protected StreamServerClientTest(Platform platformServer, Platform platformClient) throws IOException {
        this.configurationServer=new MockUpnpServiceConfiguration(platformServer);
        this.configurationClient=new MockUpnpServiceConfiguration(platformClient);
        protocolFactory= new MockProtocolFactory() {

            @Override
            public TestProtocol createReceivingSync(StreamRequestMessage requestMessage) throws ProtocolCreationException {
                String path = requestMessage.getUri().getPath();
                if (path.endsWith(OKEmptyResponse.PATH)) {
                    lastExecutedServerProtocol = new OKEmptyResponse(requestMessage);
                } else if (path.endsWith(OKBodyResponse.PATH)) {
                    lastExecutedServerProtocol = new OKBodyResponse(requestMessage);
                } else if (path.endsWith(NoResponse.PATH)) {
                    lastExecutedServerProtocol = new NoResponse(requestMessage);
                } else if (path.endsWith(DelayedResponse.PATH)) {
                    lastExecutedServerProtocol = new DelayedResponse(requestMessage);
                } else if (path.endsWith(TooLongResponse.PATH)) {
                    lastExecutedServerProtocol = new TooLongResponse(requestMessage);
                } else if (path.endsWith(CheckAliveResponse.PATH)) {
                    lastExecutedServerProtocol = new CheckAliveResponse(requestMessage);
                } else if (path.endsWith(CheckAliveLongResponse.PATH)) {
                    lastExecutedServerProtocol = new CheckAliveLongResponse(requestMessage);
                } else {
                    throw new ProtocolCreationException("Invalid test path: " + path);
                }
                return lastExecutedServerProtocol;
            }
        };
        router=new MockRouter(configurationServer, protocolFactory) {
            @Override
            public void received(UpnpStream stream) {
                stream.run();
            }
        };
    }


    @BeforeClass
    public void start() {
        try {
            testPort=8081;

            server = configurationServer.createStreamServer(testPort);

            server.init(InetAddress.getByName(TEST_HOST), router, configurationServer.createNetworkAddressFactory());
            configurationServer.getStreamServerExecutorService().execute(server);
            Thread.sleep(2000);
            testPort =server.getPort();
            client = configurationClient.createStreamClient(3);

        }
        catch (Throwable e)
        {
            FlexiLogXML.log(Level.ERROR, e);
            Assert.fail();
        }
    }

    @AfterClass
    public void stop() {
        try {
            server.stop();
            client.stop();
            Thread.sleep(1000);
        }
        catch (Throwable e)
        {
            FlexiLogXML.log(Level.ERROR, e);
            Assert.fail();
        }
        finally {
            server=null;
            client=null;
        }
    }

    @BeforeMethod
    public void clearLastProtocol() {
        lastExecutedServerProtocol = null;
    }

    @Test
    public void basic() throws Exception {
        StreamResponseMessage responseMessage;

        responseMessage = client.sendRequest(createRequestMessage(OKEmptyResponse.PATH));
        assertEquals(responseMessage.getOperation().getStatusCode(), 200);
        assertFalse(responseMessage.hasBody());
        assertTrue(lastExecutedServerProtocol.isComplete);

        lastExecutedServerProtocol = null;
        responseMessage = client.sendRequest(createRequestMessage(OKBodyResponse.PATH));
        assertEquals(responseMessage.getOperation().getStatusCode(), 200);
        assertTrue(responseMessage.hasBody());
        assertEquals(responseMessage.getBodyString(), "foo");
        assertTrue(lastExecutedServerProtocol.isComplete);

        lastExecutedServerProtocol = null;
        responseMessage = client.sendRequest(createRequestMessage(NoResponse.PATH));
        assertEquals(responseMessage.getOperation().getStatusCode(), 404);
        assertFalse(responseMessage.hasBody());
        assertFalse(lastExecutedServerProtocol.isComplete);
    }

    @Test
    public void cancelled() throws Exception {
        final boolean[] tests = new boolean[1];

        final Thread requestThread = configurationClient.startThread(() -> {
			try {
				client.sendRequest(createRequestMessage(DelayedResponse.PATH));
			} catch (InterruptedException ex) {
				// We expect this thread to be interrupted
				tests[0] = true;
			}
		});


        // Cancel the request after 250ms
        configurationServer.startThread(() -> {
			try {
				Thread.sleep(250);
			} catch (InterruptedException ignored) {
				// Ignore
			}
			requestThread.interrupt();
		});

        Thread.sleep(3000);
        for (boolean test : tests) {
            assertTrue(test);
        }
        // The server doesn't check if the connection is still alive, so it will complete
        assertTrue(lastExecutedServerProtocol.isComplete);
    }

    @Test
    public void expired() throws Exception {
        StreamResponseMessage responseMessage = client.sendRequest(createRequestMessage(TooLongResponse.PATH));
        assertNull(responseMessage);
        assertFalse(lastExecutedServerProtocol.isComplete);
        // The client expires the HTTP connection but the server doesn't check if
        // it's alive, so the server will complete the request after a while
        Thread.sleep(3000);
        assertTrue(lastExecutedServerProtocol.isComplete);
    }

    @Test
    public void checkAlive() throws Exception {
        StreamResponseMessage responseMessage = client.sendRequest(createRequestMessage(CheckAliveResponse.PATH));
        assertEquals(responseMessage.getOperation().getStatusCode(), 200);
        assertFalse(responseMessage.hasBody());
        assertTrue(lastExecutedServerProtocol.isComplete);
    }

    @Test
    public void checkAliveExpired() throws Exception {
        StreamResponseMessage responseMessage = client.sendRequest(createRequestMessage(CheckAliveLongResponse.PATH));
        assertNull(responseMessage);
        // The client expires the HTTP connection and the server checks if the
        // connection is still alive, it will abort the request
        Thread.sleep(3000);
        assertFalse(lastExecutedServerProtocol.isComplete);
    }

    @Test
    public void checkAliveCancelled() throws Exception {
        final boolean[] tests = new boolean[1];

        final Thread requestThread = configurationClient.startThread(() -> {
			try {
				client.sendRequest(createRequestMessage(CheckAliveResponse.PATH));
			} catch (InterruptedException ex) {
				// We expect this thread to be interrupted
				tests[0] = true;
			}
		});

        // Cancel the request after 1 second
        configurationServer.startThread(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ignored) {
				// Ignore
			}
			requestThread.interrupt();
		});

        Thread.sleep(3000);
        for (boolean test : tests) {
            assertTrue(test);
        }
        assertFalse(lastExecutedServerProtocol.isComplete);
    }

    protected StreamRequestMessage createRequestMessage(String path) {
        return new StreamRequestMessage(
            UpnpRequest.Method.GET,
            URI.create("http://" + TEST_HOST + ":" + testPort + path)
        );
    }

    public abstract static class TestProtocol extends ReceivingSync<StreamRequestMessage, StreamResponseMessage> {
        volatile public boolean isComplete;

        public TestProtocol(StreamRequestMessage inputMessage) {
            super(null, inputMessage);
        }
    }

    public static class OKEmptyResponse extends TestProtocol {

        public static final String PATH = "/ok";

        public OKEmptyResponse(StreamRequestMessage inputMessage) {
            super(inputMessage);
        }

        @Override
        protected StreamResponseMessage executeSync() {
            isComplete = true;
            return new StreamResponseMessage(UpnpResponse.Status.OK);
        }
    }

    public static class OKBodyResponse extends TestProtocol{

        public static final String PATH = "/okbody";

        public OKBodyResponse(StreamRequestMessage inputMessage) {
            super(inputMessage);
        }

        @Override
        protected StreamResponseMessage executeSync() {
            isComplete = true;
            return new StreamResponseMessage("foo");
        }
    }

    public static class NoResponse extends TestProtocol {

        public static final String PATH = "/noresponse";

        public NoResponse(StreamRequestMessage inputMessage) {
            super(inputMessage);
        }

        @Override
        protected StreamResponseMessage executeSync() {
            return null;
        }
    }

    public static class DelayedResponse extends TestProtocol {

        public static final String PATH = "/delayed";

        public DelayedResponse(StreamRequestMessage inputMessage) {
            super(inputMessage);
        }

        @Override
        protected StreamResponseMessage executeSync() {
            try {
                log.info("Sleeping for 2 seconds before completion...");
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            isComplete = true;
            return new StreamResponseMessage(UpnpResponse.Status.OK);
        }
    }

    public static class TooLongResponse extends TestProtocol {

        public static final String PATH = "/toolong";

        public TooLongResponse(StreamRequestMessage inputMessage) {
            super(inputMessage);
        }

        @Override
        protected StreamResponseMessage executeSync() {
            try {
                log.info("Sleeping for 4 seconds before completion...");
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            isComplete = true;
            return new StreamResponseMessage(UpnpResponse.Status.OK);
        }
    }

    public static class CheckAliveResponse extends TestProtocol {

        public static final String PATH = "/checkalive";

        public CheckAliveResponse(StreamRequestMessage inputMessage) {
            super(inputMessage);
        }

        @Override
        protected StreamResponseMessage executeSync() {
            // Return OK response after 2 seconds, check if client connection every 500ms
            int i = 0;
            while (i < 4) {
                try {
                    log.info("Sleeping for 500ms before checking connection...");
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    return null;
                }
                if (getRemoteClientInfo().isRequestCancelled()) {
                    return null;
                }
                i++;
            }
            isComplete = true;
            return new StreamResponseMessage(UpnpResponse.Status.OK);
        }
    }

    public static class CheckAliveLongResponse extends TestProtocol {

        public static final String PATH = "/checkalivelong";

        public CheckAliveLongResponse(StreamRequestMessage inputMessage) {
            super(inputMessage);
        }

        @Override
        protected StreamResponseMessage executeSync() {
            // Return OK response after 5 seconds, check if client connection every 500ms
            int i = 0;
            while (i < 10) {
                try {
                    log.info("Sleeping for 500ms before checking connection...");
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    return null;
                }
                if (getRemoteClientInfo().isRequestCancelled()) {
                    return null;
                }
                i++;
            }
            isComplete = true;
            return new StreamResponseMessage(UpnpResponse.Status.OK);
        }
    }

}