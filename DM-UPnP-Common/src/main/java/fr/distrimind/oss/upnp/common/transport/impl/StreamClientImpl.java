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

import fr.distrimind.oss.upnp.common.http.IHeaders;
import fr.distrimind.oss.upnp.common.model.profile.RemoteClientInfo;
import fr.distrimind.oss.upnp.common.model.ModelUtil;
import fr.distrimind.oss.upnp.common.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpHeaders;
import fr.distrimind.oss.upnp.common.model.message.UpnpMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpRequest;
import fr.distrimind.oss.upnp.common.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.common.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.common.transport.spi.InitializationException;
import fr.distrimind.oss.upnp.common.transport.spi.StreamClient;
import fr.distrimind.oss.upnp.common.util.Exceptions;
import fr.distrimind.oss.upnp.common.util.URIUtil;
import fr.distrimind.oss.upnp.common.util.io.IO;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Default implementation based on the JDK's <code>HttpURLConnection</code>.
 * <p>
 * This class works around a serious design issue in the SUN JDK, so it will not work on any JDK that
 * doesn't offer the <code>sun.net.www.protocol.http.HttpURLConnection </code> implementation.
 * </p>
 * <p>
 * This implementation <em>DOES NOT WORK</em> on Android. Read the DM-UPnP manual for
 * alternatives for Android.
 * </p>
 * <p>
 * This implementation <em>DOES NOT</em> support DM-UPnP's server-side heartbeat for connection checking.
 * Any data returned by a server has to be "valid HTTP", checked in Sun's HttpClient with:
 * </p>
 * {@code ret = b[0] == 'H' && b[1] == 'T' && b[2] == 'T' && b[3] == 'P' && b[4] == '/' && b[5] == '1' && b[6] == '.';}
 * <p>
 * Hence, if you are using this client, don't call DM-UPnP's
 * {@link RemoteClientInfo#isRequestCancelled()} function on your
 * server to send a heartbeat to the client!
 * </p>
 *
 * @author Christian Bauer
 */
public class StreamClientImpl implements StreamClient<StreamClientConfigurationImpl> {

    final static String HACK_STREAM_HANDLER_SYSTEM_PROPERTY = "hackStreamHandlerProperty";

    final private static DMLogger log = Log.getLogger(StreamClientImpl.class);

    final protected StreamClientConfigurationImpl configuration;

    public StreamClientImpl(StreamClientConfigurationImpl configuration) throws InitializationException {
        this.configuration = configuration;

        if (ModelUtil.ANDROID_EMULATOR || ModelUtil.ANDROID_RUNTIME) {
            /*
            See the fantastic PERMITTED_USER_METHODS here:

            https://android.googlesource.com/platform/libcore/+/android-4.0.1_r1.2/luni/src/main/java/java/net/HttpURLConnection.java

            We'd have to basically copy the whole Android code, and have a dependency on
            libcore.*, and do much more hacking to allow more HTTP methods. This is the same
            problem we are hacking below for the JDK but at least there we don't have a
            dependency issue for compiling DM-UPnP. These guys all suck, there is no list
            of "permitted" HTTP methods. HttpURLConnection and the whole stream handler
            factory stuff is the worst Java API ever created.
            */
            throw new InitializationException(
                "This client does not work on Android. The design of HttpURLConnection is broken, we "
                    + "can not add additional 'permitted' HTTP methods. Read the DM-UPnP manual."
            );
        }

		if (log.isDebugEnabled()) {
            log.debug("Using persistent HTTP stream client connections: " + configuration.isUsePersistentConnections());
		}
		System.setProperty("http.keepAlive", Boolean.toString(configuration.isUsePersistentConnections()));

        // Hack the environment to allow additional HTTP methods
        /*if (System.getProperty(HACK_STREAM_HANDLER_SYSTEM_PROPERTY) == null) {
            log.debug("Setting custom static URLStreamHandlerFactory to work around bad JDK defaults");
            try {
                // Use reflection to avoid dependency on sun.net package so this class at least
                // loads on Android, even if it doesn't work...
                URL.setURLStreamHandlerFactory(
                    (URLStreamHandlerFactory) Class.forName(
							"fr.distrimind.oss.upnp.common.transport.impl.FixedSunURLStreamHandler"
                    ).newInstance()
                );
            } catch (Throwable t) {
                throw new InitializationException(
                    "Failed to set modified URLStreamHandlerFactory in this environment."
                        + " Can't use bundled default client based on HTTPURLConnection, see manual."
                );
            }
            System.setProperty(HACK_STREAM_HANDLER_SYSTEM_PROPERTY, "alreadyWorkedAroundTheEvilJDK");
        }*/
    }

    @Override
    public StreamClientConfigurationImpl getConfiguration() {
        return configuration;
    }

    @Override
	@SuppressWarnings("PMD.AvoidInstanceofChecksInCatchClause")
    public StreamResponseMessage sendRequest(StreamRequestMessage requestMessage) {

        final UpnpRequest requestOperation = requestMessage.getOperation();
		if (log.isDebugEnabled()) {
            log.debug("Preparing HTTP request message with method '" + requestOperation.getHttpMethodName() + "': " + requestMessage);
		}

		URL url = URIUtil.toURL(requestOperation.getURI());

        HttpURLConnection urlConnection = null;
        InputStream inputStream;
        try {

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod(requestOperation.getHttpMethodName());

            // Use the built-in expiration, we can't cancel HttpURLConnection
            urlConnection.setReadTimeout(configuration.getTimeoutSeconds() * 1000);
            urlConnection.setConnectTimeout(configuration.getTimeoutSeconds() * 1000);

            applyRequestProperties(urlConnection, requestMessage);
            applyRequestBody(urlConnection, requestMessage);

			if (log.isDebugEnabled()) {
				log.debug("Sending HTTP request: " + requestMessage);
			}
			inputStream = urlConnection.getInputStream();
            return createResponse(urlConnection, inputStream);

        } catch (ProtocolException ex) {
			if (log.isWarnEnabled()) log.warn("HTTP request failed: " + requestMessage, Exceptions.unwrap(ex));
            return null;
        }
		catch (IOException ex) {

            if (urlConnection == null) {
				if (log.isWarnEnabled()) log.warn("HTTP request failed: " + requestMessage, Exceptions.unwrap(ex));
                return null;
            }

            if (ex instanceof SocketTimeoutException) {
				if (log.isInfoEnabled()) log.info(
                    "Timeout of " + getConfiguration().getTimeoutSeconds()
                        + " seconds while waiting for HTTP request to complete, aborting: " + requestMessage
                	);
                return null;
            }

            if (log.isDebugEnabled())
                log.debug("Exception occurred, trying to read the error stream: ", Exceptions.unwrap(ex));
            try {
                inputStream = urlConnection.getErrorStream();
                return createResponse(urlConnection, inputStream);
            } catch (Exception errorEx) {
                if (log.isDebugEnabled())
                    log.debug("Could not read error stream: " + errorEx);
                return null;
            }
        } catch (Exception ex) {
			if (log.isWarnEnabled()) log.warn("HTTP request failed: " + requestMessage, Exceptions.unwrap(ex));
            return null;

        } finally {

            if (urlConnection != null) {
                // Release any idle persistent connection, or "indicate that we don't want to use this server for a while"
                urlConnection.disconnect();
            }
        }
    }

    @Override
    public void stop() {
        // NOOP
    }

    protected void applyRequestProperties(HttpURLConnection urlConnection, StreamRequestMessage requestMessage) {

        urlConnection.setInstanceFollowRedirects(false); // Defaults to true but not needed here

        // HttpURLConnection always adds a "Host" header

        // HttpURLConnection always adds an "Accept" header (not needed but shouldn't hurt)

        // Add the default user agent if not already set on the message
        if (!requestMessage.getHeaders().containsKey(UpnpHeader.Type.USER_AGENT)) {
            urlConnection.setRequestProperty(
                UpnpHeader.Type.USER_AGENT.getHttpName(),
                getConfiguration().getUserAgentValue(requestMessage.getUdaMajorVersion(), requestMessage.getUdaMinorVersion())
            );
        }

        // Other headers
        applyHeaders(urlConnection, requestMessage.getHeaders());
    }

    protected void applyHeaders(HttpURLConnection urlConnection, IHeaders headers) {
		if (log.isDebugEnabled()) {
            log.debug("Writing headers on HttpURLConnection: " + headers.size());
		}
		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (String v : entry.getValue()) {
                String headerName = entry.getKey();
				if (log.isDebugEnabled()) {
					log.debug("Setting header '" + headerName + "': " + v);
				}
				urlConnection.setRequestProperty(headerName, v);
            }
        }
    }

    protected void applyRequestBody(HttpURLConnection urlConnection, StreamRequestMessage requestMessage) throws IOException {

        if (requestMessage.hasBody()) {
            urlConnection.setDoOutput(true);
        } else {
            urlConnection.setDoOutput(false);
            return;
        }

        if (requestMessage.getBodyType().equals(UpnpMessage.BodyType.STRING)) {
            IO.writeUTF8(urlConnection.getOutputStream(), requestMessage.getBodyString());
        } else if (requestMessage.getBodyType().equals(UpnpMessage.BodyType.BYTES)) {
            IO.writeBytes(urlConnection.getOutputStream(), requestMessage.getBodyBytes());
        }
        urlConnection.getOutputStream().flush();
    }

    protected StreamResponseMessage createResponse(HttpURLConnection urlConnection, InputStream inputStream) throws Exception {

        if (urlConnection.getResponseCode() == -1) {
			if (log.isWarnEnabled()) {
				log.warn("Received an invalid HTTP response: " + urlConnection.getURL());
				log.warn("Is your DM-UPnP-based server sending connection heartbeats with " +
						"RemoteClientInfo#isRequestCancelled? This client can't handle " +
						"heartbeats, read the manual.");
			}
            return null;
        }

        // Status
        UpnpResponse responseOperation = new UpnpResponse(urlConnection.getResponseCode(), urlConnection.getResponseMessage());

		if (log.isDebugEnabled()) {
            log.debug("Received response: " + responseOperation);
		}

		// Message
        StreamResponseMessage responseMessage = new StreamResponseMessage(responseOperation);

        // Headers
        responseMessage.setHeaders(new UpnpHeaders(urlConnection.getHeaderFields()));

        // Body
        byte[] bodyBytes = null;
		try (InputStream is = inputStream) {
			if (inputStream != null) bodyBytes = IO.readBytes(is);
		}

        if (bodyBytes != null && bodyBytes.length > 0 && responseMessage.isContentTypeMissingOrText()) {

            log.debug("Response contains textual entity body, converting then setting string on message");
            responseMessage.setBodyCharacters(bodyBytes);

        } else if (bodyBytes != null && bodyBytes.length > 0) {

            log.debug("Response contains binary entity body, setting bytes on message");
            responseMessage.setBody(UpnpMessage.BodyType.BYTES, bodyBytes);

        } else {
            log.debug("Response did not contain entity body");
        }

		if (log.isDebugEnabled()) {
            log.debug("Response message complete: " + responseMessage);
		}
		return responseMessage;
    }

}


