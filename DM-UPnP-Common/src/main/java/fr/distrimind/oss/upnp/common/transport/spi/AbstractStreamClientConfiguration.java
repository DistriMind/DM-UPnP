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

import fr.distrimind.oss.upnp.common.model.ServerClientTokens;

import java.util.concurrent.ExecutorService;

/**
 * @author Christian Bauer
 */
public abstract class AbstractStreamClientConfiguration implements StreamClientConfiguration {

    protected ExecutorService requestExecutorService;
    protected int timeoutSeconds = 60;
    protected int logWarningSeconds = 5;

    protected AbstractStreamClientConfiguration(ExecutorService requestExecutorService) {
        this(requestExecutorService, 60, 5);
    }

    protected AbstractStreamClientConfiguration(ExecutorService requestExecutorService, int timeoutSeconds) {
        this(requestExecutorService, timeoutSeconds, 5);
    }

    protected AbstractStreamClientConfiguration(ExecutorService requestExecutorService, int timeoutSeconds, int logWarningSeconds) {
        this.requestExecutorService = requestExecutorService;
        this.timeoutSeconds = timeoutSeconds<1?60:timeoutSeconds;
        this.logWarningSeconds = logWarningSeconds;
    }

    @Override
	public ExecutorService getRequestExecutorService() {
        return requestExecutorService;
    }

    public void setRequestExecutorService(ExecutorService requestExecutorService) {
        this.requestExecutorService = requestExecutorService;
    }

    /**
     * @return Configured value or default of 60 seconds.
     */
    @Override
	public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    /**
     * @return Configured value or default of 5 seconds.
     */
    @Override
	public int getLogWarningSeconds() {
        return logWarningSeconds;
    }

    public void setLogWarningSeconds(int logWarningSeconds) {
        this.logWarningSeconds = logWarningSeconds;
    }

    /**
     * @return Defaults to string value of {@link ServerClientTokens}.
     */
    @Override
	public String getUserAgentValue(int majorVersion, int minorVersion) {
        return new ServerClientTokens(majorVersion, minorVersion).toString();
    }
}
