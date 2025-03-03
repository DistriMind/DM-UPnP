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

package fr.distrimind.oss.upnp.common.protocol.async;

import fr.distrimind.oss.upnp.common.transport.RouterException;
import fr.distrimind.oss.upnp.common.UpnpService;
import fr.distrimind.oss.upnp.common.model.Location;
import fr.distrimind.oss.upnp.common.model.NetworkAddress;
import fr.distrimind.oss.upnp.common.model.message.discovery.OutgoingNotificationRequest;
import fr.distrimind.oss.upnp.common.model.message.discovery.OutgoingNotificationRequestDeviceType;
import fr.distrimind.oss.upnp.common.model.message.discovery.OutgoingNotificationRequestRootDevice;
import fr.distrimind.oss.upnp.common.model.message.discovery.OutgoingNotificationRequestServiceType;
import fr.distrimind.oss.upnp.common.model.message.discovery.OutgoingNotificationRequestUDN;
import fr.distrimind.oss.upnp.common.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.common.model.types.NotificationSubtype;
import fr.distrimind.oss.upnp.common.model.types.ServiceType;
import fr.distrimind.oss.upnp.common.protocol.SendingAsync;

import java.util.ArrayList;
import java.util.List;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Sending notification messages for a registered local device.
 * <p>
 * Sends all required (dozens) of messages three times, waits between 0 and 150
 * milliseconds between each bulk sending procedure.
 * </p>
 *
 * @author Christian Bauer
 */
public abstract class SendingNotification extends SendingAsync {

    final private static DMLogger log = Log.getLogger(SendingNotification.class);

    private final LocalDevice<?> device;

    public SendingNotification(UpnpService upnpService, LocalDevice<?> device) {
        super(upnpService);
        this.device = device;
    }

    public LocalDevice<?> getDevice() {
        return device;
    }

    @Override
	protected void execute() throws RouterException {

        List<NetworkAddress> activeStreamServers =
            getUpnpService().getRouter().getActiveStreamServers(null);
        if (activeStreamServers.isEmpty()) {
            log.debug("Aborting notifications, no active stream servers found (network disabled?)");
            return;
        }

        // Prepare it once, it's the same for each repetition
        List<Location> descriptorLocations = new ArrayList<>();
        for (NetworkAddress activeStreamServer : activeStreamServers) {
            descriptorLocations.add(
                    new Location(
                            activeStreamServer,
                            getUpnpService().getConfiguration().getNamespace().getDescriptorPathString(getDevice())
                    )
            );
        }

        for (int i = 0; i < getBulkRepeat(); i++) {
            try {

                for (Location descriptorLocation : descriptorLocations) {
                    sendMessages(descriptorLocation);
                }

                // UDA 1.0 is silent about this but UDA 1.1 recomments "a few hundred milliseconds"
				if (log.isTraceEnabled()) {
					log.trace("Sleeping " + getBulkIntervalMilliseconds() + " milliseconds");
				}
				Thread.sleep(getBulkIntervalMilliseconds());

            } catch (InterruptedException ex) {
                if (log.isWarnEnabled()) log.warn("Advertisement thread was interrupted: ", ex);
            }
        }
    }

    protected int getBulkRepeat() {
        return 3; // UDA 1.0 says maximum 3 times for alive messages, let's just do it for all
    }

    protected int getBulkIntervalMilliseconds() {
        return 150;
    }

    public void sendMessages(Location descriptorLocation) throws RouterException {
		if (log.isTraceEnabled()) {
			log.trace("Sending root device messages: " + getDevice());
		}
		List<OutgoingNotificationRequest> rootDeviceMsgs =
                createDeviceMessages(getDevice(), descriptorLocation);
        for (OutgoingNotificationRequest upnpMessage : rootDeviceMsgs) {
            getUpnpService().getRouter().send(upnpMessage);
        }

        if (getDevice().hasEmbeddedDevices()) {
            for (LocalDevice<?> embeddedDevice : getDevice().findEmbeddedDevices()) {
				if (log.isTraceEnabled()) {
					log.trace("Sending embedded device messages: " + embeddedDevice);
				}
				List<OutgoingNotificationRequest> embeddedDeviceMsgs =
                        createDeviceMessages(embeddedDevice, descriptorLocation);
                for (OutgoingNotificationRequest upnpMessage : embeddedDeviceMsgs) {
                    getUpnpService().getRouter().send(upnpMessage);
                }
            }
        }

        List<OutgoingNotificationRequest> serviceTypeMsgs =
                createServiceTypeMessages(getDevice(), descriptorLocation);
        if (!serviceTypeMsgs.isEmpty()) {
            log.trace("Sending service type messages");
            for (OutgoingNotificationRequest upnpMessage : serviceTypeMsgs) {
                getUpnpService().getRouter().send(upnpMessage);
            }
        }
    }

    protected List<OutgoingNotificationRequest> createDeviceMessages(LocalDevice<?> device,
                                                                     Location descriptorLocation) {
        List<OutgoingNotificationRequest> msgs = new ArrayList<>();

        // See the tables in UDA 1.0 section 1.1.2

        if (device.isRoot()) {
            msgs.add(
                    new OutgoingNotificationRequestRootDevice(
                            descriptorLocation,
                            device,
                            getNotificationSubtype()
                    )
            );
        }

        msgs.add(
                new OutgoingNotificationRequestUDN(
                        descriptorLocation, device, getNotificationSubtype()
                )
        );
        msgs.add(
                new OutgoingNotificationRequestDeviceType(
                        descriptorLocation, device, getNotificationSubtype()
                )
        );

        return msgs;
    }

    protected List<OutgoingNotificationRequest> createServiceTypeMessages(LocalDevice<?> device,
                                                                          Location descriptorLocation) {
        List<OutgoingNotificationRequest> msgs = new ArrayList<>();

        for (ServiceType serviceType : device.findServiceTypes()) {
            msgs.add(
                    new OutgoingNotificationRequestServiceType(
                            descriptorLocation, device,
                            getNotificationSubtype(), serviceType
                    )
            );
        }

        return msgs;
    }

    protected abstract NotificationSubtype getNotificationSubtype();

}
