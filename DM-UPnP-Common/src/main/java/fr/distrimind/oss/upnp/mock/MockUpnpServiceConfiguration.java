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

import fr.distrimind.oss.upnp.binding.xml.DeviceDescriptorBinder;
import fr.distrimind.oss.upnp.binding.xml.ServiceDescriptorBinder;
import fr.distrimind.oss.upnp.platform.DefaultPlatformUpnpServiceConfiguration;
import fr.distrimind.oss.upnp.platform.Platform;
import fr.distrimind.oss.upnp.platform.PlatformUpnpServiceConfiguration;
import fr.distrimind.oss.upnp.transport.spi.NetworkAddressFactory;
import fr.distrimind.oss.upnp.DefaultUpnpServiceConfiguration;
import fr.distrimind.oss.upnp.transport.spi.SOAPActionProcessor;

import jakarta.enterprise.inject.Alternative;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * @author Christian Bauer
 */
@Alternative
public class MockUpnpServiceConfiguration extends DefaultUpnpServiceConfiguration {

    final protected boolean maintainsRegistry;
    final protected boolean multiThreaded;
    private final static DefaultPlatformUpnpServiceConfiguration desktopPlatformUpnpServiceConfiguration=new DefaultPlatformUpnpServiceConfiguration();
    /**
     * Does not maintain registry, single threaded execution.
     */
    public MockUpnpServiceConfiguration() throws IOException {
        this(Platform.getDefault(), false, false);
    }

    /**
     * Single threaded execution.
     */
    public MockUpnpServiceConfiguration(boolean maintainsRegistry) throws IOException {
        this(Platform.getDefault(), maintainsRegistry, false);
    }

    public MockUpnpServiceConfiguration(boolean maintainsRegistry, boolean multiThreaded) throws IOException {
        this(Platform.getDefault(), maintainsRegistry, multiThreaded);
    }
    /**
     * Does not maintain registry, single threaded execution.
     */
    public MockUpnpServiceConfiguration(Platform platform) throws IOException {
        this(platform, false, false);
    }

    /**
     * Single threaded execution.
     */
    public MockUpnpServiceConfiguration(Platform platform, boolean maintainsRegistry) throws IOException {
        this(platform, maintainsRegistry, false);
    }

    public MockUpnpServiceConfiguration(Platform platform, boolean maintainsRegistry, boolean multiThreaded) throws IOException {
        super(platform, false);
        this.maintainsRegistry = maintainsRegistry;
        this.multiThreaded = multiThreaded;
    }

    public boolean isMaintainsRegistry() {
        return maintainsRegistry;
    }

    public boolean isMultiThreaded() {
        return multiThreaded;
    }

    @Override
    protected NetworkAddressFactory createNetworkAddressFactory(int streamListenPort, int multiCastPort) {
        return platformUpnpServiceConfiguration.createMockNetworkAddressFactory(streamListenPort, multiCastPort);
    }

    @Override
    public Executor getRegistryMaintainerExecutor() {
        if (isMaintainsRegistry()) {
            return this::startThread;
        }
        return getDefaultExecutorService();
    }

    @Override
    protected ExecutorService getDefaultExecutorService()  {
        if (isMultiThreaded()) {
            return super.getDefaultExecutorService();
        }
        else
            return platformUpnpServiceConfiguration.createMockDefaultExecutorService();
    }

    @Override
    protected SOAPActionProcessor createSOAPActionProcessor() {
        return desktopPlatformUpnpServiceConfiguration.createSOAPActionProcessor();
    }

    @Override
    protected DeviceDescriptorBinder createDeviceDescriptorBinderUDA10() {
        return desktopPlatformUpnpServiceConfiguration.createDeviceDescriptorBinderUDA10(getNetworkAddressFactory());
    }

    @Override
    protected ServiceDescriptorBinder createServiceDescriptorBinderUDA10() {
        return desktopPlatformUpnpServiceConfiguration.createServiceDescriptorBinderUDA10(getNetworkAddressFactory());
    }

    protected PlatformUpnpServiceConfiguration getDesktopPlatformUpnpServiceConfiguration()
    {
        return desktopPlatformUpnpServiceConfiguration;
    }
}
