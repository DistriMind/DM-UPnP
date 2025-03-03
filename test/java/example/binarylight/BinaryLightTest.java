package example.binarylight;

import fr.distrimind.oss.upnp.common.mock.MockUpnpService;
import fr.distrimind.oss.upnp.common.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Christian Bauer
 */
public class BinaryLightTest {

    @Test
    public void testServer() throws Exception {
        LocalDevice<SwitchPower> binaryLight = new BinaryLightServer().createDevice();
        assertEquals(binaryLight.getServices().iterator().next().getAction("SetTarget").getName(), "SetTarget");
    }

    @Test
    public void testClient() throws Exception {
        // Well we can't really test the listener easily, but the action invocation should work on a local device

        MockUpnpService upnpService = new MockUpnpService();

        BinaryLightClient client = new BinaryLightClient();
        LocalDevice<SwitchPower> binaryLight = new BinaryLightServer().createDevice();

        LocalService<SwitchPower> service = binaryLight.getServices().iterator().next();
        client.executeAction(upnpService, binaryLight.getServices().iterator().next());
        Thread.sleep(100);
		assertTrue(service.getManager().getImplementation().getStatus());
    }

}
