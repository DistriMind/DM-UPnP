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
package fr.distrimind.oss.upnp.common.transport.spi;

import fr.distrimind.oss.flexilogxml.common.FlexiLogXML;
import fr.distrimind.oss.upnp.common.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.common.util.Exceptions;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

import fr.distrimind.oss.flexilogxml.common.log.Level;

/**
 * Implements the timeout/callback processing and unifies exception handling.

 * @author Christian Bauer
 */
public abstract class AbstractStreamClient<C extends StreamClientConfiguration, REQUEST> implements StreamClient<C> {

    final private static DMLogger log = Log.getLogger(AbstractStreamClient.class);

    @Override
    public StreamResponseMessage sendRequest(StreamRequestMessage requestMessage) throws InterruptedException {

        if (log.isDebugEnabled())
            log.debug("Preparing HTTP request: " + requestMessage);

        REQUEST request = createRequest(requestMessage);
        if (request == null)
            return null;

        Callable<StreamResponseMessage> callable = createCallable(requestMessage, request);

        // We want to track how long it takes
        long start = System.currentTimeMillis();

        // Execute the request on a new thread
        Future<StreamResponseMessage> future =
            getConfiguration().getRequestExecutorService().submit(callable);

        // Wait on the current thread for completion
        try {
            if (log.isDebugEnabled())
                log.debug(
                    "Waiting " + getConfiguration().getTimeoutSeconds()
                    + " seconds for HTTP request to complete: " + requestMessage
                );
            StreamResponseMessage response =
                future.get(getConfiguration().getTimeoutSeconds(), TimeUnit.SECONDS);

            // Log a warning if it took too long
            long elapsed = System.currentTimeMillis() - start;
            if (log.isTraceEnabled())
                log.trace("Got HTTP response in " + elapsed + "ms: " + requestMessage);
            if (getConfiguration().getLogWarningSeconds() > 0
                && elapsed > getConfiguration().getLogWarningSeconds()* 1000L) {
                if (log.isWarnEnabled()) log.warn("HTTP request took a long time (" + elapsed + "ms): " + requestMessage);
            }

            return response;

        } catch (InterruptedException ex) {

            if (log.isDebugEnabled())
                log.debug("Interruption, aborting request: " + requestMessage);
            abort(request);
            throw new InterruptedException("HTTP request interrupted and aborted");

        } catch (TimeoutException ex) {

            if (log.isInfoEnabled()) log.info(
                "Timeout of " + getConfiguration().getTimeoutSeconds()
                + " seconds while waiting for HTTP request to complete, aborting: " + requestMessage
                );
            abort(request);
            return null;

        } catch (ExecutionException ex) {
            FlexiLogXML.log(Level.ERROR, ex);
            Throwable cause = ex.getCause();
            if (!logExecutionException(cause)) {
                if (log.isWarnEnabled()) log.warn("HTTP request failed: " + requestMessage, Exceptions.unwrap(cause));
            }
            return null;
        } finally {
            onFinally(request);
        }
    }

    /**
     * Create a proprietary representation of this request, log warnings and
     * return <code>null</code> if creation fails.
     */
    abstract protected REQUEST createRequest(StreamRequestMessage requestMessage);

    /**
     * Create a callable procedure that will execute the request.
     */
    abstract protected Callable<StreamResponseMessage> createCallable(StreamRequestMessage requestMessage,
                                                                      REQUEST request);

    /**
     * Cancel and abort the request immediately, with the proprietary API.
     */
    abstract protected void abort(REQUEST request);

    /**
     * @return <code>true</code> if no more logging of this exception should be done.
     */
    abstract protected boolean logExecutionException(Throwable t);

    protected void onFinally(REQUEST request) {
        // Do nothing
    }

}
