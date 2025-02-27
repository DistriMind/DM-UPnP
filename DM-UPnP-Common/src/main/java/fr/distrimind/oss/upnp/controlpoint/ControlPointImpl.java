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

package fr.distrimind.oss.upnp.controlpoint;

import fr.distrimind.oss.upnp.UpnpServiceConfiguration;
import fr.distrimind.oss.upnp.controlpoint.event.ExecuteAction;
import fr.distrimind.oss.upnp.controlpoint.event.Search;
import fr.distrimind.oss.upnp.model.message.header.MXHeader;
import fr.distrimind.oss.upnp.model.message.header.STAllHeader;
import fr.distrimind.oss.upnp.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.protocol.ProtocolFactory;
import fr.distrimind.oss.upnp.registry.Registry;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.Log;

/**
 * Default implementation.
 * <p>
 * This implementation uses the executor returned by
 * {@link UpnpServiceConfiguration#getSyncProtocolExecutorService()}.
 * </p>
 *
 * @author Christian Bauer
 */
@ApplicationScoped
public class ControlPointImpl implements ControlPoint {

    final private static DMLogger log = Log.getLogger(ControlPointImpl.class);

    protected UpnpServiceConfiguration configuration;
    protected ProtocolFactory protocolFactory;
    protected Registry registry;

    protected ControlPointImpl() {
    }

    @Inject
    public ControlPointImpl(UpnpServiceConfiguration configuration, ProtocolFactory protocolFactory, Registry registry) {
		if (log.isDebugEnabled()) {
            log.debug("Creating ControlPoint: " + getClass().getName());
		}

		this.configuration = configuration;
        this.protocolFactory = protocolFactory;
        this.registry = registry;
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
	public Registry getRegistry() {
        return registry;
    }

    public void search(@Observes Search search) {
        search(search.getSearchType(), search.getMxSeconds());
    }

    @Override
	public void search() {
        search(new STAllHeader(), MXHeader.DEFAULT_VALUE);
    }

    @Override
	public void search(UpnpHeader<?> searchType) {
        search(searchType, MXHeader.DEFAULT_VALUE);
    }

    @Override
	public void search(int mxSeconds) {
        search(new STAllHeader(), mxSeconds);
    }

    @Override
	public void search(UpnpHeader<?> searchType, int mxSeconds) {
		if (log.isDebugEnabled()) {
            log.debug("Sending asynchronous search for: " + searchType.getString());
		}
		getConfiguration().getAsyncProtocolExecutor().execute(
                getProtocolFactory().createSendingSearch(searchType, mxSeconds)
        );
    }

    public void execute(ExecuteAction executeAction) {
        execute(executeAction.getCallback());
    }

    @Override
	@SuppressWarnings("PMD.CloseResource")
	public Future<?> execute(ActionCallback callback) {
		if (log.isDebugEnabled()) {
            log.debug("Invoking action in background: " + callback);
		}
		callback.setControlPoint(this);
        ExecutorService executor = getConfiguration().getSyncProtocolExecutorService();
        return executor.submit(callback);
    }

    @Override
	public void execute(SubscriptionCallback callback) {
		if (log.isDebugEnabled()) {
            log.debug("Invoking subscription in background: " + callback);
		}
		callback.setControlPoint(this);
        getConfiguration().getSyncProtocolExecutorService().execute(callback);
    }
}
