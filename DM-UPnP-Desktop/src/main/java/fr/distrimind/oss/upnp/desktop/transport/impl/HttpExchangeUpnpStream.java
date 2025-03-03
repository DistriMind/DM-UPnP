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

package fr.distrimind.oss.upnp.desktop.transport.impl;

import com.sun.net.httpserver.HttpExchange;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.model.message.Connection;
import fr.distrimind.oss.upnp.common.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpHeaders;
import fr.distrimind.oss.upnp.common.model.message.UpnpMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpRequest;
import fr.distrimind.oss.upnp.common.protocol.ProtocolFactory;
import fr.distrimind.oss.upnp.common.transport.spi.UpnpStream;
import fr.distrimind.oss.upnp.common.util.Exceptions;
import fr.distrimind.oss.upnp.common.util.io.IO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Locale;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Default implementation based on the JDK 6.0 built-in HTTP Server.
 * <p>
 * Instantiated by a <code>com.sun.net.httpserver.HttpHandler</code>.
 * </p>
 *
 * @author Christian Bauer
 */
public abstract class HttpExchangeUpnpStream extends UpnpStream {

    final private static DMLogger log = Log.getLogger(HttpExchangeUpnpStream.class);

    private final HttpExchange httpExchange;

    public HttpExchangeUpnpStream(ProtocolFactory protocolFactory, HttpExchange httpExchange) {
        super(protocolFactory);
        this.httpExchange = httpExchange;
    }

    public HttpExchange getHttpExchange() {
        return httpExchange;
    }

    @Override
	public void run() {

        try {
			if (log.isDebugEnabled()) {
				log.debug("Processing HTTP request: " + getHttpExchange().getRequestMethod() + " " + getHttpExchange().getRequestURI());
			}

			// Status
            StreamRequestMessage requestMessage =
                    new StreamRequestMessage(
                            UpnpRequest.Method.getByHttpName(getHttpExchange().getRequestMethod()),
                            getHttpExchange().getRequestURI()
                    );

            if (requestMessage.getOperation().getMethod().equals(UpnpRequest.Method.UNKNOWN)) {
				if (log.isDebugEnabled()) {
					log.debug("Method not supported by UPnP stack: " + getHttpExchange().getRequestMethod());
				}
				throw new RuntimeException("Method not supported: " + getHttpExchange().getRequestMethod());
            }

            // Protocol
            requestMessage.getOperation().setHttpMinorVersion(
					"HTTP/1.1".equals(getHttpExchange().getProtocol().toUpperCase(Locale.ROOT)) ? 1 : 0
            );

			if (log.isDebugEnabled()) {
				log.debug("Created new request message: " + requestMessage);
			}

			// Connection wrapper
            requestMessage.setConnection(createConnection());

            // Headers
            requestMessage.setHeaders(new UpnpHeaders(getHttpExchange().getRequestHeaders()));

            // Body
            byte[] bodyBytes;
			try (InputStream is = getHttpExchange().getRequestBody()) {
				bodyBytes = IO.readBytes(is);
			}

			if (log.isDebugEnabled()) {
				log.debug("Reading request body bytes: " + bodyBytes.length);
			}

			if (bodyBytes.length > 0 && requestMessage.isContentTypeMissingOrText()) {

                log.debug("Request contains textual entity body, converting then setting string on message");
                requestMessage.setBodyCharacters(bodyBytes);

            } else if (bodyBytes.length > 0) {

                log.debug("Request contains binary entity body, setting bytes on message");
                requestMessage.setBody(UpnpMessage.BodyType.BYTES, bodyBytes);

            } else {
                log.debug("Request did not contain entity body");
            }

            // Process it
            StreamResponseMessage responseMessage = process(requestMessage);

            // Return the response
            if (responseMessage != null) {
				if (log.isDebugEnabled()) {
					log.debug("Preparing HTTP response message: " + responseMessage);
				}

				// Headers
                getHttpExchange().getResponseHeaders().putAll(
                        responseMessage.getHeaders()
                );

                // Body
                byte[] responseBodyBytes = responseMessage.hasBody() ? responseMessage.getBodyBytes() : null;
                int contentLength = responseBodyBytes != null ? responseBodyBytes.length : -1;

				if (log.isDebugEnabled()) {
					log.debug("Sending HTTP response message: " + responseMessage + " with content length: " + contentLength);
				}
				getHttpExchange().sendResponseHeaders(responseMessage.getOperation().getStatusCode(), contentLength);

                if (contentLength > 0) {
                    log.debug("Response message has body, writing bytes to stream...");
					try (OutputStream os = getHttpExchange().getResponseBody()) {
						IO.writeBytes(os, responseBodyBytes);
						os.flush();
					}
                }

            } else {
                // If it's null, it's 404, everything else needs a proper httpResponse
				if (log.isDebugEnabled()) log.debug("Sending HTTP response status: " + HttpURLConnection.HTTP_NOT_FOUND);
                getHttpExchange().sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, -1);
            }

            responseSent(responseMessage);

        } catch (Throwable t) {

            // You definitely want to catch all Exceptions here, otherwise the server will
            // simply close the socket, and you get an "unexpected end of file" on the client.
            // The same is true if you just rethrow an IOException - it is a mystery why it
            // is declared then on the HttpHandler interface if it isn't handled in any
            // way... so we always do error handling here.

            // TODO: We should only send an error if the problem was on our side
            // You don't have to catch Throwable unless, like we do here in unit tests,
            // you might run into Errors as well (assertions).
			if (log.isDebugEnabled()) {
				log.debug("Exception occured during UPnP stream processing: " + t);
			}
			if (log.isDebugEnabled()) {
                log.debug("Cause: ", Exceptions.unwrap(t));
            }
            try {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
            } catch (IOException ex) {
				if (log.isWarnEnabled()) log.warn("Couldn't send error response: ", ex);
            }

            responseException(t);
        }
    }

    abstract protected Connection createConnection();

}
