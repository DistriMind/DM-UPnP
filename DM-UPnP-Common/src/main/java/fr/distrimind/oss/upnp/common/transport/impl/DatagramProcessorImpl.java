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

package fr.distrimind.oss.upnp.common.transport.impl;

import java.nio.charset.StandardCharsets;

import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;
import fr.distrimind.oss.upnp.common.model.Constants;
import fr.distrimind.oss.upnp.common.model.message.*;
import fr.distrimind.oss.upnp.common.transport.spi.DatagramProcessor;
import fr.distrimind.oss.upnp.common.model.UnsupportedDataException;
import fr.distrimind.oss.upnp.common.http.Headers;

import java.io.ByteArrayInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Locale;

/**
 * Default implementation.
 * 
 * @author Christian Bauer
 */
public class DatagramProcessorImpl implements DatagramProcessor {

    final private static DMLogger log = Log.getLogger(DatagramProcessorImpl.class);

    @Override
	public IncomingDatagramMessage<?> read(InetAddress receivedOnAddress, DatagramPacket datagram) throws UnsupportedDataException {
        if (datagram.getLength()> Constants.MAX_HEADER_LENGTH_IN_BYTES)
        {
            throw new UnsupportedDataException("Datagram length is higher than "+Constants.MAX_HEADER_LENGTH_IN_BYTES+" bytes");
        }
        try {

            if (log.isTraceEnabled()) {
				log.trace("===================================== DATAGRAM BEGIN ============================================");
                log.trace(new String(datagram.getData(), StandardCharsets.UTF_8));
                log.trace("-===================================== DATAGRAM END =============================================");
            }

            ByteArrayInputStream is = new ByteArrayInputStream(datagram.getData());

            String[] startLine = Headers.readLine(is).split(" ");
            if (startLine[0].startsWith("HTTP/1.")) {
                return readResponseMessage(receivedOnAddress, datagram, is, Integer.parseInt(startLine[1]), startLine[2], startLine[0]);
            } else {
                return readRequestMessage(receivedOnAddress, datagram, is, startLine[0], startLine[2]);
            }

        } catch (Exception ex) {
            throw new UnsupportedDataException("Could not parse headers: " + ex, ex, datagram.getData());
        }
    }

    @Override
	public DatagramPacket write(OutgoingDatagramMessage<?> message) throws UnsupportedDataException {

        StringBuilder statusLine = new StringBuilder();

        UpnpOperation operation = message.getOperation();

        if (operation instanceof UpnpRequest) {

            UpnpRequest requestOperation = (UpnpRequest) operation;
            statusLine.append(requestOperation.getHttpMethodName()).append(" * ");
            statusLine.append("HTTP/1.").append(operation.getHttpMinorVersion()).append("\r\n");

        } else if (operation instanceof UpnpResponse) {
            UpnpResponse responseOperation = (UpnpResponse) operation;
            statusLine.append("HTTP/1.").append(operation.getHttpMinorVersion()).append(" ");
            statusLine.append(responseOperation.getStatusCode()).append(" ").append(responseOperation.getStatusMessage());
            statusLine.append("\r\n");
        } else {
            throw new UnsupportedDataException(
                    "Message operation is not request or response, don't know how to process: " + message
            );
        }

        // UDA 1.0, 1.1.2: Nobody but message must have a blank line after header
        StringBuilder messageData = new StringBuilder();
        messageData.append(statusLine);

        messageData.append(message.getHeaders().toString()).append("\r\n");

        if (log.isTraceEnabled()) {
            log.trace("Writing message data for: " + message);
            log.trace("---------------------------------------------------------------------------------");
            log.trace(messageData.substring(0, messageData.length() - 2)); // Don't print the blank lines
            log.trace("---------------------------------------------------------------------------------");
        }

		// According to HTTP 1.0 RFC, headers and their values are US-ASCII
		// TODO: Probably should look into escaping rules, too
		byte[] data = messageData.toString().getBytes(StandardCharsets.US_ASCII);

		if (log.isDebugEnabled()) {
            log.debug("Writing new datagram packet with " + data.length + " bytes for: " + message);
		}
		return new DatagramPacket(data, data.length, message.getDestinationAddress(), message.getDestinationPort());

	}

    protected IncomingDatagramMessage<UpnpRequest> readRequestMessage(InetAddress receivedOnAddress,
                                                         DatagramPacket datagram,
                                                         ByteArrayInputStream is,
                                                         String requestMethod,
                                                         String httpProtocol) throws Exception {

        // Headers
        IUpnpHeaders headers = new UpnpHeaders(is);

        // Assemble message
        IncomingDatagramMessage<UpnpRequest> requestMessage;
        UpnpRequest upnpRequest = new UpnpRequest(UpnpRequest.Method.getByHttpName(requestMethod));
        upnpRequest.setHttpMinorVersion("HTTP/1.1".equals(httpProtocol.toUpperCase(Locale.ROOT)) ? 1 : 0);
        requestMessage = new IncomingDatagramMessage<>(upnpRequest, datagram.getAddress(), datagram.getPort(), receivedOnAddress);

        requestMessage.setHeaders(headers);

        return requestMessage;
    }

    protected IncomingDatagramMessage<?> readResponseMessage(InetAddress receivedOnAddress,
                                                          DatagramPacket datagram,
                                                          ByteArrayInputStream is,
                                                          int statusCode,
                                                          String statusMessage,
                                                          String httpProtocol) throws Exception {

        // Headers
        IUpnpHeaders headers = new UpnpHeaders(is);

        // Assemble the message
        IncomingDatagramMessage<?> responseMessage;
        UpnpResponse upnpResponse = new UpnpResponse(statusCode, statusMessage);
        upnpResponse.setHttpMinorVersion("HTTP/1.1".equals(httpProtocol.toUpperCase(Locale.ROOT)) ? 1 : 0);
        responseMessage = new IncomingDatagramMessage<>(upnpResponse, datagram.getAddress(), datagram.getPort(), receivedOnAddress);

        responseMessage.setHeaders(headers);

        return responseMessage;
    }


}
