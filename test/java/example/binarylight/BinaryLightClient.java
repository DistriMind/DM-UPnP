package example.binarylight;

import fr.distrimind.oss.flexilogxml.common.concurrent.ThreadType;
import fr.distrimind.oss.upnp.common.controlpoint.ActionCallback;
import fr.distrimind.oss.upnp.common.model.action.ActionInvocation;
import fr.distrimind.oss.upnp.common.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.common.model.message.header.STAllHeader;
import fr.distrimind.oss.upnp.common.model.meta.RemoteDevice;
import fr.distrimind.oss.upnp.common.model.meta.Service;
import fr.distrimind.oss.upnp.common.model.types.InvalidValueException;
import fr.distrimind.oss.upnp.common.model.types.ServiceId;
import fr.distrimind.oss.upnp.common.model.types.UDAServiceId;
import fr.distrimind.oss.upnp.common.registry.DefaultRegistryListener;
import fr.distrimind.oss.upnp.common.registry.Registry;
import fr.distrimind.oss.upnp.common.registry.RegistryListener;
import fr.distrimind.oss.upnp.common.UpnpService;
import fr.distrimind.oss.upnp.common.UpnpServiceImpl;

@SuppressWarnings({"PMD.SystemPrintln", "PMD.DoNotTerminateVM"})
public class BinaryLightClient implements Runnable {

    public static void main(String[] args) throws Exception {
        // Start a user thread that runs the UPnP stack
        Thread clientThread = ThreadType.VIRTUAL_THREAD_IF_AVAILABLE.startThread(new BinaryLightClient());
        clientThread.setDaemon(false);
        clientThread.start();

    }

    @Override
	public void run() {
        try {

            UpnpService upnpService = new UpnpServiceImpl();

            // Add a listener for device registration events
            upnpService.getRegistry().addListener(
                    createRegistryListener(upnpService)
            );

            // Broadcast a search message for all devices
            upnpService.getControlPoint().search(
                    new STAllHeader()
            );

        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            System.exit(1);
        }
    }

    // DOC: REGISTRYLISTENER
    RegistryListener createRegistryListener(final UpnpService upnpService) {
        return new DefaultRegistryListener() {

            final ServiceId serviceId = new UDAServiceId("SwitchPower");

            @Override
            public void remoteDeviceAdded(Registry registry, RemoteDevice device) {

                Service<?, ?, ?> switchPower;
                if ((switchPower = device.findService(serviceId)) != null) {

                    System.out.println("Service discovered: " + switchPower);
                    executeAction(upnpService, switchPower);

                }

            }

            @Override
            public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
                Service<?, ?, ?> switchPower;
                if ((switchPower = device.findService(serviceId)) != null) {
                    System.out.println("Service disappeared: " + switchPower);
                }
            }

        };
    }
    // DOC: REGISTRYLISTENER
    // DOC: EXECUTEACTION
    void executeAction(UpnpService upnpService, Service<?, ?, ?> switchPowerService) {

            ActionInvocation<?> setTargetInvocation =
                    new SetTargetActionInvocation(switchPowerService);

            // Executes asynchronous in the background
            upnpService.getControlPoint().execute(
                    new ActionCallback(setTargetInvocation) {

                        @Override
                        public void success(ActionInvocation<?> invocation) {
                            assert invocation.getOutput().isEmpty();
                            System.out.println("Successfully called action!");
                        }

                        @Override
                        public void failure(ActionInvocation<?> invocation,
                                            UpnpResponse operation,
                                            String defaultMsg) {
                            System.err.println(defaultMsg);
                        }
                    }
            );

    }

    static class SetTargetActionInvocation extends ActionInvocation<Service<?, ?, ?>> {

        SetTargetActionInvocation(Service<?, ?, ?> service) {
            super(service.getAction("SetTarget"));
            try {

                // Throws InvalidValueException if the value is of wrong type
                setInput("NewTargetValue", true);

            } catch (InvalidValueException ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        }
    }
    // DOC: EXECUTEACTION
}
