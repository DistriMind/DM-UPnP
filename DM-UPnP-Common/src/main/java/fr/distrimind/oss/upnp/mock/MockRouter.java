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
package fr.distrimind.oss.upnp.mock;

import fr.distrimind.oss.upnp.model.NetworkAddress;
import fr.distrimind.oss.upnp.model.message.IncomingDatagramMessage;
import fr.distrimind.oss.upnp.model.message.OutgoingDatagramMessage;
import fr.distrimind.oss.upnp.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.protocol.ProtocolFactory;
import fr.distrimind.oss.upnp.transport.Router;
import fr.distrimind.oss.upnp.transport.RouterException;
import fr.distrimind.oss.upnp.transport.impl.NetworkAddressFactoryImpl;
import fr.distrimind.oss.upnp.transport.spi.InitializationException;
import fr.distrimind.oss.upnp.transport.spi.UpnpStream;
import fr.distrimind.oss.upnp.UpnpServiceConfiguration;

import jakarta.enterprise.inject.Alternative;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * This is not a real network transport layer, it collects all messages instead and makes
 * them available for testing with {@link #getOutgoingDatagramMessages()},
 * {@link #getSentStreamRequestMessages()}, etc. Mock responses for TCP (HTTP) stream requests
 * can be returned by overriding {@link #getStreamResponseMessage(StreamRequestMessage)}
 * or {@link #getStreamResponseMessages()} if you know the order of requests.
 * </p>
 *
 * @author Christian Bauer
 */
@Alternative
public class MockRouter implements Router {

    public int counter = -1;
    public List<IncomingDatagramMessage<?>> incomingDatagramMessages = new ArrayList<>();
    public List<OutgoingDatagramMessage<?>> outgoingDatagramMessages = new ArrayList<>();
    public List<UpnpStream> receivedUpnpStreams = new ArrayList<>();
    public List<StreamRequestMessage> sentStreamRequestMessages = new ArrayList<>();
    public List<byte[]> broadcastedBytes = new ArrayList<>();

    protected UpnpServiceConfiguration configuration;
    protected ProtocolFactory protocolFactory;

    public MockRouter(UpnpServiceConfiguration configuration,
                      ProtocolFactory protocolFactory) {
        this.configuration = configuration;
        this.protocolFactory = protocolFactory;
    }

    @Override
    public UpnpServiceConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public ProtocolFactory getProtocolFactory() {
        return protocolFactory;
    }

    @Override
    public boolean enable() throws RouterException {
        return false;
    }

    @Override
    public boolean disable() throws RouterException {
        return false;
    }

    @Override
    public void shutdown() throws RouterException {
    }

    @Override
    public boolean isEnabled() throws RouterException {
        return false;
    }

    @Override
    public void handleStartFailure(InitializationException ex) throws InitializationException {
    }

    @Override
    public List<NetworkAddress> getActiveStreamServers(InetAddress preferredAddress) throws RouterException {
        // Simulate an active stream server, otherwise the notification/search response
        // protocols won't even run
        try {
            return List.of(
					new NetworkAddress(
							InetAddress.getByName("127.0.0.1"),
							NetworkAddressFactoryImpl.DEFAULT_TCP_HTTP_LISTEN_PORT
					)
			);
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
	public void received(IncomingDatagramMessage<?> msg) {
        incomingDatagramMessages.add(msg);
    }

    @Override
	public void received(UpnpStream stream) {
        receivedUpnpStreams.add(stream);
    }

    @Override
	public void send(OutgoingDatagramMessage<?> msg) throws RouterException {
        outgoingDatagramMessages.add(msg);
    }

    @Override
	public StreamResponseMessage send(StreamRequestMessage msg) throws RouterException {
        sentStreamRequestMessages.add(msg);
        counter++;
        List<StreamResponseMessage> l=getStreamResponseMessages();
        return l.isEmpty()
                ?getStreamResponseMessage(msg)
                :l.get(counter);
    }

    @Override
	public void broadcast(byte[] bytes) {
        broadcastedBytes.add(bytes);
    }

    public void resetStreamRequestMessageCounter() {
        counter = -1;
    }

    public List<IncomingDatagramMessage<?>> getIncomingDatagramMessages() {
        return incomingDatagramMessages;
    }

    public List<OutgoingDatagramMessage<?>> getOutgoingDatagramMessages() {
        return outgoingDatagramMessages;
    }

    public List<UpnpStream> getReceivedUpnpStreams() {
        return receivedUpnpStreams;
    }

    public List<StreamRequestMessage> getSentStreamRequestMessages() {
        return sentStreamRequestMessages;
    }

    public List<byte[]> getBroadcastedBytes() {
        return broadcastedBytes;
    }

    public List<StreamResponseMessage> getStreamResponseMessages() {
        return Collections.emptyList();
    }

    public StreamResponseMessage getStreamResponseMessage(StreamRequestMessage request) {
        return null;
    }

}
