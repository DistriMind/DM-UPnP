package example.binarylight;

import fr.distrimind.oss.upnp.common.binding.annotations.AnnotationLocalServiceBinder;
import fr.distrimind.oss.upnp.common.model.meta.DeviceDetails;
import fr.distrimind.oss.upnp.common.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;
import fr.distrimind.oss.upnp.common.model.types.UDADeviceType;
import fr.distrimind.oss.upnp.common.test.data.SampleData;

/**
 * @author Christian Bauer
 */
public class BinaryLightSampleData {

    public static <T> LocalDevice<T> createDevice(Class<T> serviceClass) throws Exception {
        return createDevice(
                SampleData.readService(
                        new AnnotationLocalServiceBinder(),
                        serviceClass
                )
        );
    }

    public static <T> LocalDevice<T> createDevice(LocalService<T> service) throws Exception {
        return new LocalDevice<>(
                SampleData.createLocalDeviceIdentity(),
                new UDADeviceType("BinaryLight", 1),
                new DeviceDetails("Example Binary Light"),
                service
        );
    }

}
