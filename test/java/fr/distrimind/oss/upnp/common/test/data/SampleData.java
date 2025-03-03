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

package fr.distrimind.oss.upnp.common.test.data;

import fr.distrimind.oss.upnp.common.DefaultUpnpServiceConfiguration;
import fr.distrimind.oss.upnp.common.binding.LocalServiceBinder;
import fr.distrimind.oss.upnp.common.binding.annotations.AnnotationLocalServiceBinder;
import fr.distrimind.oss.upnp.common.model.DefaultServiceManager;
import fr.distrimind.oss.upnp.common.model.message.OutgoingDatagramMessage;
import fr.distrimind.oss.upnp.common.model.meta.*;
import fr.distrimind.oss.upnp.common.model.types.DeviceType;
import fr.distrimind.oss.upnp.common.model.types.ServiceId;
import fr.distrimind.oss.upnp.common.model.types.ServiceType;
import fr.distrimind.oss.upnp.common.transport.impl.NetworkAddressFactoryImpl;
import fr.distrimind.oss.upnp.common.transport.spi.DatagramProcessor;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.*;
import java.util.Collection;

import fr.distrimind.oss.upnp.common.model.profile.DeviceDetailsProvider;
import org.testng.Assert;


public class SampleData {


    /* ###################################################################################### */

    public static InetAddress getLocalBaseAddress() {
        try {
            return InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static URL getLocalBaseURL() {
        try {
            return new URI("http://127.0.0.1:" + NetworkAddressFactoryImpl.DEFAULT_TCP_HTTP_LISTEN_PORT + "/").toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /* ###################################################################################### */

    public static DeviceIdentity createLocalDeviceIdentity() {
        return createLocalDeviceIdentity(1800);
    }

    public static DeviceIdentity createLocalDeviceIdentity(int maxAgeSeconds) {
        return new DeviceIdentity(SampleDeviceRoot.getRootUDN(), maxAgeSeconds);
    }

    public static <T> LocalDevice<T> createLocalDevice() {
        return createLocalDevice(false);
    }

    public static <T> LocalDevice<T> createLocalDevice(boolean useProvider) {
        return createLocalDevice(createLocalDeviceIdentity(), useProvider);
    }

    @SuppressWarnings("unchecked")
	public static <T> Constructor<T> getLocalDeviceConstructor() {
        try {
            return (Constructor<T>) LocalDevice.class.getConstructor(
                    DeviceIdentity.class, DeviceType.class, DeviceDetails.class,
                    Collection.class, LocalService.class, LocalDevice.class
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("unchecked")
	public static <T> Constructor<T> getLocalDeviceWithProviderConstructor() {
        try {
            return (Constructor<T>) LocalDevice.class.getConstructor(
                    DeviceIdentity.class, DeviceType.class, DeviceDetailsProvider.class,
                    Collection.class, LocalService.class, LocalDevice.class
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

	@SuppressWarnings("rawtypes")
	public static Constructor<LocalService> getLocalServiceConstructor() {
        try {
            return LocalService.class.getConstructor(
                    ServiceType.class, ServiceId.class,
                    Collection.class, Collection.class
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static LocalDevice<? extends LocalService<?>> createLocalDevice(DeviceIdentity identity) {
        return createLocalDevice(identity, false);
    }

	@SuppressWarnings("unchecked")
	public static <T> LocalDevice<T> createLocalDevice(DeviceIdentity identity, boolean useProvider) {
        try {

            Constructor<T> ctor =
                    useProvider
                            ? getLocalDeviceWithProviderConstructor()
                            : getLocalDeviceConstructor();

            @SuppressWarnings("rawtypes") final Constructor<LocalService> serviceConstructor = getLocalServiceConstructor();
            Assert.assertNotNull(serviceConstructor);

            return new SampleDeviceRootLocal<>(
                    identity,
                    (LocalService<T>)new SampleServiceOne().newInstanceLocal(serviceConstructor),
                    new SampleDeviceEmbeddedOne<>(
                            new DeviceIdentity(SampleDeviceEmbeddedOne.getEmbeddedOneUDN(), identity),
                            (LocalService<T>)new SampleServiceTwo().newInstanceLocal(serviceConstructor),
							new SampleDeviceEmbeddedTwo<>(
									new DeviceIdentity(SampleDeviceEmbeddedTwo.getEmbeddedTwoUDN(), identity),
                                    (LocalService<T>)new SampleServiceThree().newInstanceLocal(serviceConstructor),
									null
							).newInstance(ctor, useProvider)
                    ).newInstance(ctor, useProvider)
            ).newInstance(ctor, useProvider);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> LocalService<T> getFirstService(LocalDevice<T> device) {
        return device.getServices().iterator().next();
    }

    /* ###################################################################################### */

    public static RemoteDeviceIdentity createRemoteDeviceIdentity() {
        return createRemoteDeviceIdentity(1800);
    }

    public static RemoteDeviceIdentity createRemoteDeviceIdentity(int maxAgeSeconds) {
        return new RemoteDeviceIdentity(
                SampleDeviceRoot.getRootUDN(),
                maxAgeSeconds,
                SampleDeviceRoot.getDeviceDescriptorURL(),
                null,
                getLocalBaseAddress()
        );
    }

    public static RemoteDevice createRemoteDevice() {
        return createRemoteDevice(createRemoteDeviceIdentity());
    }

    public static Constructor<RemoteDevice> getRemoteDeviceConstructor() {
        try {
            return RemoteDevice.class.getConstructor(
                    RemoteDeviceIdentity.class, DeviceType.class, DeviceDetails.class,
                    Collection.class, RemoteService.class, RemoteDevice.class
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Constructor<RemoteService> getRemoteServiceConstructor() {
        try {
            return RemoteService.class.getConstructor(
                    ServiceType.class, ServiceId.class,
                    URI.class, URI.class, URI.class,
                    Collection.class, Collection.class
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static RemoteDevice createRemoteDevice(RemoteDeviceIdentity identity) {
        try {

            Constructor<RemoteDevice> ctor = getRemoteDeviceConstructor();
            Constructor<RemoteService> serviceConstructor = getRemoteServiceConstructor();

            return new SampleDeviceRoot<>(
					identity,
					new SampleServiceOne().newInstanceRemote(serviceConstructor),
					new SampleDeviceEmbeddedOne<>(
							new RemoteDeviceIdentity(SampleDeviceEmbeddedOne.getEmbeddedOneUDN(), identity),
							new SampleServiceTwo().newInstanceRemote(serviceConstructor),
							new SampleDeviceEmbeddedTwo<>(
									new RemoteDeviceIdentity(SampleDeviceEmbeddedTwo.getEmbeddedTwoUDN(), identity),
									new SampleServiceThree().newInstanceRemote(serviceConstructor),
									null
							).newInstance(ctor)
					).newInstance(ctor)
			).newInstance(ctor);

        } catch (Exception e) {
/*
            Throwable cause = Exceptions.unwrap(e);
            if (cause instanceof ValidationException) {
                ValidationException ex = (ValidationException) cause;
                for (ValidationError validationError : ex.getErrors()) {
                    log.error(validationError.toString());
                }
            }
*/
            throw new RuntimeException(e);
        }
    }

    public static RemoteService getFirstService(RemoteDevice device) {
        return device.getServices().iterator().next();
    }

    public static RemoteService createUndescribedRemoteService() {
        RemoteService service =
                new SampleServiceOneUndescribed().newInstanceRemote(SampleData.getRemoteServiceConstructor());
		new SampleDeviceRoot<>(
				SampleData.createRemoteDeviceIdentity(),
				service,
				null
		).newInstance(SampleData.getRemoteDeviceConstructor());
        return service;
    }

    /* ###################################################################################### */

    public static <T> LocalService<T> readService(Class<T> clazz) {
        return readService(new AnnotationLocalServiceBinder().read(clazz), clazz);
    }

    public static <T> LocalService<T> readService(LocalServiceBinder binder, Class<T> clazz) {
        return readService(binder.read(clazz), clazz);
    }

    public static <T> LocalService<T> readService(LocalService<T> service, Class<T> clazz) {
        service.setManager(
                new DefaultServiceManager<>(service, clazz)
        );
        return service;
    }

    /* ###################################################################################### */

    /*   public static void assertTestDataMatchServiceTwo(Service svc) {

            Service<DeviceService> service = svc;

            Device sampleDevice = (service.getDeviceService().getDevice().isLocal()) ? getLocalDevice() : getRemoteDevice();
            Service<DeviceService> sampleService = getServiceTwo((Device) sampleDevice.getEmbeddedDevices().get(0));

            assertEquals(service.getActions().size(), sampleService.getActions().size());

            assertEquals(service.getActions().get("GetFoo").getName(), sampleService.getActions().get("GetFoo").getName());
            assertEquals(service.getActions().get("GetFoo").getArguments().size(), sampleService.getActions().get("GetFoo").getArguments().size());
            assertEquals(service.getActions().get("GetFoo").getArguments().get(0).getName(), service.getActions().get("GetFoo").getArguments().get(0).getName());
            assertEquals(service.getActions().get("GetFoo").getArguments().get(0).getDirection(), sampleService.getActions().get("GetFoo").getArguments().get(0).getDirection());
            assertEquals(service.getActions().get("GetFoo").getArguments().get(0).getRelatedStateVariableName(), sampleService.getActions().get("GetFoo").getArguments().get(0).getRelatedStateVariableName());

            assertEquals(service.getStateVariables().size(), sampleService.getStateVariables().size());
            assertTrue(service.getStateVariables().containsKey("Foo"));

            assertEquals(service.getStateVariables().get("Foo").getName(), "Foo");
            assertTrue(service.getStateVariables().get("Foo").isSendEvents());
            assertEquals(service.getStateVariables().get("Foo").getDatatype(), Datatype.Builtin.BOOLEAN.getDatatype());

        }

        public static void assertTestDataMatchServiceThree(Service svc) {
            assertTestDataMatchServiceTwo(svc);
        }
    */

    public static void debugMsg(OutgoingDatagramMessage<?> msg) throws IOException {
        DatagramProcessor proc = new DefaultUpnpServiceConfiguration().getDatagramProcessor();
        proc.write(msg);
    }


}
