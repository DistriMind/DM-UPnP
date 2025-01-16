/*
 * Copyright (C) 2013 4th Line GmbH, Switzerland
 *
 * The contents of this file are subject to the terms of either the GNU
 * Lesser General Public License Version 2 or later ("LGPL") or the
 * Common Development and Distribution License Version 1 or later
 * ("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. See LICENSE.txt for more
 * information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package com.distrimind.upnp_igd.protocol.async;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import com.distrimind.flexilogxml.log.DMLogger;
import com.distrimind.upnp_igd.Log;

import com.distrimind.upnp_igd.protocol.ReceivingAsync;
import com.distrimind.upnp_igd.registry.Registry;
import com.distrimind.upnp_igd.transport.RouterException;
import com.distrimind.upnp_igd.UpnpService;
import com.distrimind.upnp_igd.model.DiscoveryOptions;
import com.distrimind.upnp_igd.model.Location;
import com.distrimind.upnp_igd.model.NetworkAddress;
import com.distrimind.upnp_igd.model.message.IncomingDatagramMessage;
import com.distrimind.upnp_igd.model.message.UpnpRequest;
import com.distrimind.upnp_igd.model.message.discovery.IncomingSearchRequest;
import com.distrimind.upnp_igd.model.message.discovery.OutgoingSearchResponse;
import com.distrimind.upnp_igd.model.message.discovery.OutgoingSearchResponseDeviceType;
import com.distrimind.upnp_igd.model.message.discovery.OutgoingSearchResponseRootDevice;
import com.distrimind.upnp_igd.model.message.discovery.OutgoingSearchResponseServiceType;
import com.distrimind.upnp_igd.model.message.discovery.OutgoingSearchResponseUDN;
import com.distrimind.upnp_igd.model.message.header.DeviceTypeHeader;
import com.distrimind.upnp_igd.model.message.header.MXHeader;
import com.distrimind.upnp_igd.model.message.header.RootDeviceHeader;
import com.distrimind.upnp_igd.model.message.header.STAllHeader;
import com.distrimind.upnp_igd.model.message.header.ServiceTypeHeader;
import com.distrimind.upnp_igd.model.message.header.UDNHeader;
import com.distrimind.upnp_igd.model.message.header.UpnpHeader;
import com.distrimind.upnp_igd.model.meta.Device;
import com.distrimind.upnp_igd.model.meta.LocalDevice;
import com.distrimind.upnp_igd.model.types.DeviceType;
import com.distrimind.upnp_igd.model.types.ServiceType;
import com.distrimind.upnp_igd.model.types.UDN;

/**
 * Handles reception of search requests, responds for local registered devices.
 * <p>
 * Waits a random time between 0 and the requested <em>MX</em> (maximum 120 seconds)
 * before executing. Only waits if there are actually any registered local devices.
 * </p>
 * <p>
 * Extracts the <em>search target</em>, builds and sends the dozens of messages
 * required by the UPnP specification, depending on the search target and what
 * local devices and services are found in the {@link Registry}.
 * </p>
 *
 * @author Christian Bauer
 */
public class ReceivingSearch extends ReceivingAsync<IncomingSearchRequest> {

    final private static DMLogger log = Log.getLogger(ReceivingSearch.class);

    private static final boolean LOG_ENABLED = log.isDebugEnabled();

    final protected Random randomGenerator = new Random();

    public ReceivingSearch(UpnpService upnpService, IncomingDatagramMessage<UpnpRequest> inputMessage) {
        super(upnpService, new IncomingSearchRequest(inputMessage));
    }

    @Override
	protected void execute() throws RouterException {
        if (getUpnpService().getRouter() == null) {
            // TODO: http://mailinglists.945824.n3.nabble.com/rare-NPE-on-start-tp3078213p3142767.html
            log.debug("Router hasn't completed initialization, ignoring received search message");
            return;
        }

        if (!getInputMessage().isMANSSDPDiscover()) {
			if (log.isDebugEnabled()) {
				log.debug("Invalid search request, no or invalid MAN ssdp:discover header: " + getInputMessage());
			}
			return;
        }

        UpnpHeader<?> searchTarget = getInputMessage().getSearchTarget();

        if (searchTarget == null) {
			if (log.isDebugEnabled()) {
				log.debug("Invalid search request, did not contain ST header: " + getInputMessage());
			}
			return;
        }

        List<NetworkAddress> activeStreamServers =
            getUpnpService().getRouter().getActiveStreamServers(getInputMessage().getLocalAddress());
        if (activeStreamServers.isEmpty()) {
            log.debug("Aborting search response, no active stream servers found (network disabled?)");
            return;
        }

        for (NetworkAddress activeStreamServer : activeStreamServers) {
            sendResponses(searchTarget, activeStreamServer);
        }
    }

    @Override
    protected boolean waitBeforeExecution() throws InterruptedException {

        Integer mx = getInputMessage().getMX();

        if (mx == null) {
			if (log.isDebugEnabled()) {
				log.debug("Invalid search request, did not contain MX header: " + getInputMessage());
			}
			return false;
        }

        // Spec says we should assume "less" if it's 120 or more
        // From the spec, MX should be "greater than or equal to 1"
        // Prevent negative MX to make nextInt() throw IllegalArgumentException below
        if (mx > 120 || mx <= 0) mx = MXHeader.DEFAULT_VALUE;

        // Only wait if there is something to wait for
        if (!getUpnpService().getRegistry().getLocalDevices().isEmpty()) {
            int sleepTime = randomGenerator.nextInt(mx * 1000);
			if (log.isDebugEnabled()) {
				log.debug("Sleeping " + sleepTime + " milliseconds to avoid flooding with search responses");
			}
			Thread.sleep(sleepTime);
        }

        return true;
    }
    
    protected void sendResponses(UpnpHeader<?> searchTarget, NetworkAddress activeStreamServer) throws RouterException {
        if (searchTarget instanceof STAllHeader) {

            sendSearchResponseAll(activeStreamServer);

        } else if (searchTarget instanceof RootDeviceHeader) {

            sendSearchResponseRootDevices(activeStreamServer);

        } else if (searchTarget instanceof UDNHeader) {

            sendSearchResponseUDN((UDN) searchTarget.getValue(), activeStreamServer);

        } else if (searchTarget instanceof DeviceTypeHeader) {

            sendSearchResponseDeviceType((DeviceType) searchTarget.getValue(), activeStreamServer);

        } else if (searchTarget instanceof ServiceTypeHeader) {

            sendSearchResponseServiceType((ServiceType) searchTarget.getValue(), activeStreamServer);

        } else {
			if (log.isWarnEnabled()) log.warn("Non-implemented search request target: " + searchTarget.getClass());
        }
    }

    protected void sendSearchResponseAll(NetworkAddress activeStreamServer) throws RouterException {
        if (LOG_ENABLED) {
			if (log.isDebugEnabled()) log.debug("Responding to 'all' search with advertisement messages for all local devices");
        }
        for (LocalDevice<?> localDevice : getUpnpService().getRegistry().getLocalDevices()) {

            if (isAdvertisementDisabled(localDevice))
                continue;

            // We are re-using the regular notification messages here but override the NT with the ST header
            if (LOG_ENABLED) {
				if (log.isTraceEnabled()) {
					log.trace("Sending root device messages: " + localDevice);
				}
			}
            List<OutgoingSearchResponse> rootDeviceMsgs =
                    createDeviceMessages(localDevice, activeStreamServer);
            for (OutgoingSearchResponse upnpMessage : rootDeviceMsgs) {
                getUpnpService().getRouter().send(upnpMessage);
            }

            if (localDevice.hasEmbeddedDevices()) {
                for (LocalDevice<?> embeddedDevice : localDevice.findEmbeddedDevices()) {
                    if (LOG_ENABLED) {
						if (log.isTraceEnabled()) {
							log.trace("Sending embedded device messages: " + embeddedDevice);
						}
					}
                    List<OutgoingSearchResponse> embeddedDeviceMsgs =
                            createDeviceMessages(embeddedDevice, activeStreamServer);
                    for (OutgoingSearchResponse upnpMessage : embeddedDeviceMsgs) {
                        getUpnpService().getRouter().send(upnpMessage);
                    }
                }
            }

            List<OutgoingSearchResponse> serviceTypeMsgs =
                    createServiceTypeMessages(localDevice, activeStreamServer);
            if (!serviceTypeMsgs.isEmpty()) {
                if (LOG_ENABLED) {
                    log.trace("Sending service type messages");
                }
                for (OutgoingSearchResponse upnpMessage : serviceTypeMsgs) {
                    getUpnpService().getRouter().send(upnpMessage);
                }
            }

        }
    }

    protected List<OutgoingSearchResponse> createDeviceMessages(LocalDevice<?> device,
                                                                NetworkAddress activeStreamServer) {
        List<OutgoingSearchResponse> msgs = new ArrayList<>();

        // See the tables in UDA 1.0 section 1.1.2

        if (device.isRoot()) {
            msgs.add(
                    new OutgoingSearchResponseRootDevice(
                            getInputMessage(),
                            getDescriptorLocation(activeStreamServer, device),
                            device
                    )
            );
        }

        msgs.add(
                new OutgoingSearchResponseUDN(
                        getInputMessage(),
                        getDescriptorLocation(activeStreamServer, device),
                        device
                )
        );

        msgs.add(
                new OutgoingSearchResponseDeviceType(
                        getInputMessage(),
                        getDescriptorLocation(activeStreamServer, device),
                        device
                )
        );

        for (OutgoingSearchResponse msg : msgs) {
            prepareOutgoingSearchResponse(msg);
        }

        return msgs;
    }

    protected List<OutgoingSearchResponse> createServiceTypeMessages(LocalDevice<?> device,
                                                                     NetworkAddress activeStreamServer) {
        List<OutgoingSearchResponse> msgs = new ArrayList<>();
        for (ServiceType serviceType : device.findServiceTypes()) {
            OutgoingSearchResponse message =
                new OutgoingSearchResponseServiceType(
                        getInputMessage(),
                        getDescriptorLocation(activeStreamServer, device),
                        device,
                        serviceType
                );
            prepareOutgoingSearchResponse(message);
            msgs.add(message);
        }
        return msgs;
    }

    protected void sendSearchResponseRootDevices(NetworkAddress activeStreamServer) throws RouterException {
        log.debug("Responding to root device search with advertisement messages for all local root devices");
        for (LocalDevice<?> device : getUpnpService().getRegistry().getLocalDevices()) {

            if (isAdvertisementDisabled(device))
                continue;

            OutgoingSearchResponse message =
                new OutgoingSearchResponseRootDevice(
                        getInputMessage(),
                        getDescriptorLocation(activeStreamServer, device),
                        device
                );
            prepareOutgoingSearchResponse(message);
            getUpnpService().getRouter().send(message);
        }
    }

    protected void sendSearchResponseUDN(UDN udn, NetworkAddress activeStreamServer) throws RouterException {
        Device<?, ?, ?> device = getUpnpService().getRegistry().getDevice(udn, false);
        if (device instanceof LocalDevice) {

            if (isAdvertisementDisabled((LocalDevice<?>)device))
                return;

			if (log.isDebugEnabled()) {
				log.debug("Responding to UDN device search: " + udn);
			}
			OutgoingSearchResponse message =
                new OutgoingSearchResponseUDN(
                        getInputMessage(),
                        getDescriptorLocation(activeStreamServer, (LocalDevice<?>) device),
                        (LocalDevice<?>) device
                );
            prepareOutgoingSearchResponse(message);
            getUpnpService().getRouter().send(message);
        }
    }

    protected void sendSearchResponseDeviceType(DeviceType deviceType, NetworkAddress activeStreamServer) throws RouterException{
		if (log.isDebugEnabled()) {
            log.debug("Responding to device type search: " + deviceType);
		}
		Collection<Device<?, ?, ?>> devices = getUpnpService().getRegistry().getDevices(deviceType);
        for (Device<?, ?, ?> device : devices) {
            if (device instanceof LocalDevice) {

                if (isAdvertisementDisabled((LocalDevice<?>)device))
                    continue;

				if (log.isTraceEnabled()) {
					log.trace("Sending matching device type search result for: " + device);
				}
				OutgoingSearchResponse message =
                    new OutgoingSearchResponseDeviceType(
                            getInputMessage(),
                            getDescriptorLocation(activeStreamServer, (LocalDevice<?>) device),
                            (LocalDevice<?>) device
                    );
                prepareOutgoingSearchResponse(message);
                getUpnpService().getRouter().send(message);
            }
        }
    }

    protected void sendSearchResponseServiceType(ServiceType serviceType, NetworkAddress activeStreamServer) throws RouterException {
		if (log.isDebugEnabled()) {
            log.debug("Responding to service type search: " + serviceType);
		}
		Collection<Device<?, ?, ?>> devices = getUpnpService().getRegistry().getDevices(serviceType);
        for (Device<?, ?, ?> device : devices) {
            if (device instanceof LocalDevice) {

                if (isAdvertisementDisabled((LocalDevice<?>)device))
                    continue;

				if (log.isTraceEnabled()) {
					log.trace("Sending matching service type search result: " + device);
				}
				OutgoingSearchResponse message =
                    new OutgoingSearchResponseServiceType(
                            getInputMessage(),
                            getDescriptorLocation(activeStreamServer, (LocalDevice<?>) device),
                            (LocalDevice<?>) device,
                            serviceType
                    );
                prepareOutgoingSearchResponse(message);
                getUpnpService().getRouter().send(message);
            }
        }
    }

    protected Location getDescriptorLocation(NetworkAddress activeStreamServer, LocalDevice<?> device) {
        return new Location(
                activeStreamServer,
                getUpnpService().getConfiguration().getNamespace().getDescriptorPathString(device)
        );
    }

    protected boolean isAdvertisementDisabled(LocalDevice<?> device) {
        DiscoveryOptions options =
            getUpnpService().getRegistry().getDiscoveryOptions(device.getIdentity().getUdn());
        return options != null && !options.isAdvertised();
    }

    /**
     * Override this to edit the outgoing message, e.g. by adding headers.
     */
    protected void prepareOutgoingSearchResponse(OutgoingSearchResponse message) {
    }

}
