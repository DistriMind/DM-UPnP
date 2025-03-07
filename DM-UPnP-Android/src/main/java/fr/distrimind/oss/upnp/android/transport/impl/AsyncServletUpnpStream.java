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

package fr.distrimind.oss.upnp.android.transport.impl;

import fr.distrimind.oss.upnp.common.model.message.*;
import fr.distrimind.oss.upnp.common.protocol.ProtocolFactory;
import fr.distrimind.oss.upnp.common.transport.spi.UpnpStream;
import fr.distrimind.oss.upnp.common.util.Exceptions;
import fr.distrimind.oss.upnp.common.util.io.IO;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Implementation based on Servlet 3.0 API.
 * <p>
 * Concrete implementations must provide a connection wrapper, as this wrapper most likely has
 * to access proprietary APIs to implement connection checking.
 * </p>
 *
 * @author Christian Bauer
 */
public abstract class AsyncServletUpnpStream extends UpnpStream implements AsyncListener {

    final private static DMLogger log = Log.getLogger(AsyncServletUpnpStream.class);

    final protected AsyncContext asyncContext;
    final protected HttpServletRequest request;

    protected StreamResponseMessage responseMessage;

    public AsyncServletUpnpStream(ProtocolFactory protocolFactory,
                                  AsyncContext asyncContext,
                                  HttpServletRequest request) {
        super(protocolFactory);
        this.asyncContext = asyncContext;
        this.request = request;
        asyncContext.addListener(this);
    }

    protected HttpServletRequest getRequest() {
        return request;
    }

    protected HttpServletResponse getResponse() {
        ServletResponse response;
        if ((response = asyncContext.getResponse()) == null) {
            throw new IllegalStateException(
                "Couldn't get response from asynchronous context, already timed out"
            );
        }
        return (HttpServletResponse) response;
    }

    protected void complete() {
        try {
            asyncContext.complete();
        } catch (IllegalStateException ex) {
            // If Jetty's connection, for whatever reason, is in an illegal state, this will be thrown,
            // and we can "probably" ignore it. The request is complete, no matter how it ended.
            if (log.isInfoEnabled()) log.info("Error calling servlet container's AsyncContext#complete() method: ", ex);
        }
    }

    @Override
    public void run() {
        try {
            StreamRequestMessage requestMessage = readRequestMessage();
            if (log.isTraceEnabled())
                log.trace("Processing new request message: " + requestMessage);

            responseMessage = process(requestMessage);

            if (responseMessage != null) {
                if (log.isTraceEnabled())
                    log.trace("Preparing HTTP response message: " + responseMessage);
                writeResponseMessage(responseMessage);
            } else {
                // If it's null, it's 404
                if (log.isTraceEnabled())
                    log.trace("Sending HTTP response status: " + HttpURLConnection.HTTP_NOT_FOUND);
                getResponse().setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

        } catch (Throwable t) {
            if (log.isInfoEnabled()) log.info("Exception occurred during UPnP stream processing: " + t);
            if (log.isTraceEnabled()) {
                log.trace("Cause: ", Exceptions.unwrap(t));
            }
            if (!getResponse().isCommitted()) {
                if (log.isTraceEnabled()) log.trace("Response hasn't been committed, returning INTERNAL SERVER ERROR to client");
                getResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } else {
                if (log.isInfoEnabled()) log.info("Could not return INTERNAL SERVER ERROR to client, response was already committed");
            }
            responseException(t);
        } finally {
            complete();
        }
    }

    @Override
    public void onStartAsync(AsyncEvent event) throws IOException {
        // This is a completely useless callback, it will only be called on request.startAsync() which
        // then immediately removes the listener... what were they thinking.
    }

    @Override
    public void onComplete(AsyncEvent event) throws IOException {
        if (log.isTraceEnabled())
            log.trace("Completed asynchronous processing of HTTP request: " + event.getSuppliedRequest());
        responseSent(responseMessage);
    }

    @Override
    public void onTimeout(AsyncEvent event) throws IOException {
        if (log.isTraceEnabled())
            log.trace("Asynchronous processing of HTTP request timed out: " + event.getSuppliedRequest());
        responseException(new Exception("Asynchronous request timed out"));
    }

    @Override
    public void onError(AsyncEvent event) throws IOException {
        if (log.isTraceEnabled())
            log.trace("Asynchronous processing of HTTP request error: " + event.getThrowable());
        responseException(event.getThrowable());
    }

    protected StreamRequestMessage readRequestMessage() throws IOException {
        // Extract what we need from the HTTP httpRequest
        String requestMethod = getRequest().getMethod();
        String requestURI = getRequest().getRequestURI();

        if (log.isTraceEnabled())
            log.trace("Processing HTTP request: " + requestMethod + " " + requestURI);

        StreamRequestMessage requestMessage;
        try {
            requestMessage =
                new StreamRequestMessage(
                    UpnpRequest.Method.getByHttpName(requestMethod),
                    URI.create(requestURI)
                );
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid request URI: " + requestURI, ex);
        }

        if (requestMessage.getOperation().getMethod().equals(UpnpRequest.Method.UNKNOWN)) {
            throw new RuntimeException("Method not supported: " + requestMethod);
        }

        // Connection wrapper
        requestMessage.setConnection(createConnection());

        // Headers
        IUpnpHeaders headers = new UpnpHeaders();
        Enumeration<String> headerNames = getRequest().getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> headerValues = getRequest().getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                headers.add(headerName, headerValue);
            }
        }
        requestMessage.setHeaders(headers);

        // Body
        byte[] bodyBytes;
		try (InputStream is = getRequest().getInputStream()) {
			bodyBytes = IO.readBytes(is);
		}
        if (log.isTraceEnabled())
            log.trace("Reading request body bytes: " + bodyBytes.length);

        if (bodyBytes.length > 0 && requestMessage.isContentTypeMissingOrText()) {

            if (log.isTraceEnabled())
                log.trace("Request contains textual entity body, converting then setting string on message");
            requestMessage.setBodyCharacters(bodyBytes);

        } else if (bodyBytes.length > 0) {

            if (log.isTraceEnabled())
                log.trace("Request contains binary entity body, setting bytes on message");
            requestMessage.setBody(UpnpMessage.BodyType.BYTES, bodyBytes);

        } else {
            if (log.isTraceEnabled())
                log.trace("Request did not contain entity body");
        }

        return requestMessage;
    }

    protected void writeResponseMessage(StreamResponseMessage responseMessage) throws IOException {
        if (log.isTraceEnabled())
            log.trace("Sending HTTP response status: " + responseMessage.getOperation().getStatusCode());

        getResponse().setStatus(responseMessage.getOperation().getStatusCode());

        // Headers
        for (Map.Entry<String, List<String>> entry : responseMessage.getHeaders().entrySet()) {
            for (String value : entry.getValue()) {
                getResponse().addHeader(entry.getKey(), value);
            }
        }
        // The Date header is recommended in UDA
        getResponse().setDateHeader("Date", System.currentTimeMillis());

        // Body
        byte[] responseBodyBytes = responseMessage.hasBody() ? responseMessage.getBodyBytes() : null;
        int contentLength = responseBodyBytes != null ? responseBodyBytes.length : -1;

        if (contentLength > 0) {
            getResponse().setContentLength(contentLength);
            log.trace("Response message has body, writing bytes to stream...");
            IO.writeBytes(getResponse().getOutputStream(), responseBodyBytes);
        }
    }

    abstract protected Connection createConnection();

}
