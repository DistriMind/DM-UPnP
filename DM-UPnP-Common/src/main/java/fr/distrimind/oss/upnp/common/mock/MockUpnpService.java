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

package fr.distrimind.oss.upnp.common.mock;

import fr.distrimind.oss.upnp.common.controlpoint.ControlPoint;
import fr.distrimind.oss.upnp.common.controlpoint.ControlPointImpl;
import fr.distrimind.oss.upnp.common.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.common.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.common.protocol.ProtocolFactory;
import fr.distrimind.oss.upnp.common.protocol.ProtocolFactoryImpl;
import fr.distrimind.oss.upnp.common.protocol.async.SendingNotificationAlive;
import fr.distrimind.oss.upnp.common.protocol.async.SendingSearch;
import fr.distrimind.oss.upnp.common.registry.Registry;
import fr.distrimind.oss.upnp.common.registry.RegistryImpl;
import fr.distrimind.oss.upnp.common.registry.RegistryMaintainer;
import fr.distrimind.oss.upnp.common.transport.RouterException;
import fr.distrimind.oss.upnp.common.transport.spi.NetworkAddressFactory;
import fr.distrimind.oss.upnp.common.UpnpService;
import fr.distrimind.oss.upnp.common.UpnpServiceConfiguration;

import java.io.IOException;

import jakarta.enterprise.inject.Alternative;

/**
 * Simplifies testing of core and non-core modules.
 * <p>
 * It uses the {@link MockUpnpService.MockProtocolFactory}.
 * </p>
 *
 * @author Christian Bauer
 */
@Alternative
public class MockUpnpService implements UpnpService {

    protected final UpnpServiceConfiguration configuration;
    protected final ControlPoint controlPoint;
    protected final ProtocolFactory protocolFactory;
    protected final Registry registry;
    protected final MockRouter router;

    protected final NetworkAddressFactory networkAddressFactory;

    /**
     * Single-thread of execution for the whole UPnP stack, no ALIVE messages or registry maintenance.
     */
    public MockUpnpService() throws IOException {
        this(false, new MockUpnpServiceConfiguration(false, false));
    }

    /**
     * No ALIVE messages.
     */
    public MockUpnpService(MockUpnpServiceConfiguration configuration) throws IOException {
        this(false, configuration);
    }

    /**
     * Single-thread of execution for the whole UPnP stack, except one background registry maintenance thread.
     */
    public MockUpnpService(final boolean sendsAlive, final boolean maintainsRegistry) throws IOException {
        this(sendsAlive, new MockUpnpServiceConfiguration(maintainsRegistry, false));
    }

    public MockUpnpService(final boolean sendsAlive, final boolean maintainsRegistry, final boolean multiThreaded) throws IOException {
        this(sendsAlive, new MockUpnpServiceConfiguration(maintainsRegistry, multiThreaded));
    }

    public MockUpnpService(final boolean sendsAlive, final MockUpnpServiceConfiguration configuration) throws IOException {

        this.configuration = configuration;

        this.protocolFactory = createProtocolFactory(this, sendsAlive);

        this.registry = new RegistryImpl(this) {
            @Override
            protected RegistryMaintainer createRegistryMaintainer() {
                return configuration.isMaintainsRegistry() ? super.createRegistryMaintainer() : null;
            }
        };

        this.networkAddressFactory = this.configuration.createNetworkAddressFactory();

        this.router = createRouter();

        this.controlPoint = new ControlPointImpl(configuration, protocolFactory, registry);
    }

    protected ProtocolFactory createProtocolFactory(UpnpService service, boolean sendsAlive) {
        return new MockProtocolFactory(service, sendsAlive);
    }

    protected MockRouter createRouter() {
        return new MockRouter(getConfiguration(), getProtocolFactory());
    }

    /**
     * This factory customizes several protocols.
     * <p>
     * The {@link SendingNotificationAlive} protocol
     * only sends messages if this feature is enabled when instantiating the factory.
   
     * <p>
     * The {@link SendingSearch} protocol doesn't wait between
     * sending search message bulks, this speeds up testing.
   
     */
    public static class MockProtocolFactory extends ProtocolFactoryImpl {

        private final boolean sendsAlive;

        public MockProtocolFactory(UpnpService upnpService, boolean sendsAlive) {
            super(upnpService);
            this.sendsAlive = sendsAlive;
        }

        @Override
        public <T> SendingNotificationAlive createSendingNotificationAlive(LocalDevice<T> localDevice) {
            return new SendingNotificationAlive(getUpnpService(), localDevice) {
                @Override
                protected void execute() throws RouterException {
                    if (sendsAlive) super.execute();
                }
            };
        }

        @Override
        public SendingSearch createSendingSearch(UpnpHeader<?> searchTarget, int mxSeconds) {
            return new SendingSearch(getUpnpService(), searchTarget, mxSeconds) {
                @Override
                public int getBulkIntervalMilliseconds() {
                    return 0; // Don't wait
                }
            };
        }
    }

    @Override
	public UpnpServiceConfiguration getConfiguration() {
        return configuration;
    }

    @Override
	public ControlPoint getControlPoint() {
        return controlPoint;
    }

    @Override
	public ProtocolFactory getProtocolFactory() {
        return protocolFactory;
    }

    @Override
	public Registry getRegistry() {
        return registry;
    }

    @Override
	public MockRouter getRouter() {
        return router;
    }

    @Override
	public void shutdown() {
        getRegistry().shutdown();
        getConfiguration().shutdown();
    }
}
