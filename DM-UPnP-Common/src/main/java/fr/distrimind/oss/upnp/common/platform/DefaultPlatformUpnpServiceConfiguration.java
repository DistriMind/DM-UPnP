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

package fr.distrimind.oss.upnp.common.platform;

import fr.distrimind.oss.upnp.common.binding.xml.DeviceDescriptorBinder;
import fr.distrimind.oss.upnp.common.binding.xml.UDA10DeviceDescriptorBinderImpl;
import fr.distrimind.oss.upnp.common.model.Namespace;
import fr.distrimind.oss.upnp.common.transport.impl.*;
import fr.distrimind.oss.upnp.common.transport.spi.*;

import java.util.concurrent.ExecutorService;
/**
 * @author Jason Mahdjoub
 * @since 1.3.0
 */
public class DefaultPlatformUpnpServiceConfiguration extends PlatformUpnpServiceConfiguration {

	public DefaultPlatformUpnpServiceConfiguration() {
	}


	@Override
	public DeviceDescriptorBinder createDeviceDescriptorBinderUDA10(NetworkAddressFactory networkAddressFactory) {
		return new UDA10DeviceDescriptorBinderImpl(networkAddressFactory);
	}


	@Override
	public Namespace createNamespace() {
		return new Namespace();
	}

	@Override
	public SOAPActionProcessor createSOAPActionProcessor() {
		return new SOAPActionProcessorImpl();
	}

	@Override
	public GENAEventProcessor createGENAEventProcessor() {
		return new GENAEventProcessorImpl();
	}

	@Override
	public StreamClient<?> createStreamClient(ExecutorService syncProtocolExecutorService, int timeoutSeconds) {
		return new StreamClientImpl(
				new StreamClientConfigurationImpl(
						syncProtocolExecutorService,
						timeoutSeconds
				)
		);
	}

	@Override
	public StreamServer<?> createStreamServer(int streamServerPort) {
		return null;
	}
	@Override
	public int getRegistryMaintenanceIntervalMillis() {
		return 1000;
	}
	@Override
	public NetworkAddressFactory createNetworkAddressFactory(int streamListenPort, int multicastPort) {
		return new NetworkAddressFactoryImpl(streamListenPort, multicastPort);
	}
	@Override
	public Platform getPlatformType() {
		return Platform.DESKTOP;
	}
}
