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
package example.igd;

import fr.distrimind.oss.upnp.common.binding.annotations.UpnpAction;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpInputArgument;
import fr.distrimind.oss.upnp.common.binding.annotations.AnnotationLocalServiceBinder;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpOutputArgument;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpService;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpServiceId;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpServiceType;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpStateVariable;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpStateVariables;
import fr.distrimind.oss.upnp.common.model.DefaultServiceManager;
import fr.distrimind.oss.upnp.common.model.action.ActionException;
import fr.distrimind.oss.upnp.common.model.meta.DeviceDetails;
import fr.distrimind.oss.upnp.common.model.meta.DeviceIdentity;
import fr.distrimind.oss.upnp.common.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;
import fr.distrimind.oss.upnp.common.model.types.ErrorCode;
import fr.distrimind.oss.upnp.common.model.types.UDADeviceType;
import fr.distrimind.oss.upnp.common.model.types.UDN;
import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerFourBytes;
import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerTwoBytes;
import fr.distrimind.oss.upnp.common.support.igd.PortMappingListener;
import fr.distrimind.oss.upnp.common.support.model.Connection;
import fr.distrimind.oss.upnp.common.support.model.PortMapping;

import java.util.Collection;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class IGDSampleData {

    public static final String REMOTE_HOST = "RemoteHost";
    public static final String STRING = "string";
    public static final String EXTERNAL_PORT = "ExternalPort";
    public static final String PORT_MAPPING_PROTOCOL = "PortMappingProtocol";

    public static <T> LocalService<T> readService(Class<T> serviceClass) throws Exception {
        LocalService<T> service = new AnnotationLocalServiceBinder().read(serviceClass);
        service.setManager(
                new DefaultServiceManager<>(service, serviceClass)
        );
        return service;
    }

    public static <T> LocalDevice<T> createIGDevice(Class<T> serviceClass) throws Exception {
        return createIGDevice(
                null,
				List.of(
						createWANDevice(
								null,
								List.of(
										createWANConnectionDevice(List.of(readService(serviceClass)), null)
								)
						)
				));
    }

    public static <T> LocalDevice<T> createIGDevice(Collection<LocalService<T>> services, List<LocalDevice<T>> embedded) throws Exception {
        return new LocalDevice<>(
                new DeviceIdentity(new UDN("1111")),
                new UDADeviceType(PortMappingListener.INTERNET_GATEWAY_DEVICE, 2),
                new DeviceDetails("Example Router"),
                services,
                embedded
        );
    }

    public static <T> LocalDevice<T> createWANDevice(Collection<LocalService<T>> services, List<LocalDevice<T>> embedded) throws Exception {
        return new LocalDevice<>(
                new DeviceIdentity(new UDN("2222")),
                new UDADeviceType("WANDevice", 1),
                new DeviceDetails("Example WAN Device"),
                services,
                embedded
        );
    }

    public static <T> LocalDevice<T> createWANConnectionDevice(Collection<LocalService<T>> services, List<LocalDevice<T>> embedded) throws Exception {
        return new LocalDevice<>(
                new DeviceIdentity(new UDN("3333")),
                new UDADeviceType(PortMappingListener.WAN_CONNECTION_DEVICE, 1),
                new DeviceDetails("Example WAN Connection Device"),
                services,
                embedded
        );
    }

    @UpnpService(
            serviceId = @UpnpServiceId(PortMappingListener.WANIP_CONNECTION),
            serviceType = @UpnpServiceType(PortMappingListener.WANIP_CONNECTION)
    )
    @UpnpStateVariables({
            @UpnpStateVariable(name = REMOTE_HOST, datatype = STRING, sendEvents = false),
            @UpnpStateVariable(name = EXTERNAL_PORT, datatype = "ui2", sendEvents = false),
            @UpnpStateVariable(name = PORT_MAPPING_PROTOCOL, datatype = STRING, sendEvents = false, allowedValuesEnum = PortMapping.Protocol.class),
            @UpnpStateVariable(name = "InternalPort", datatype = "ui2", sendEvents = false),
            @UpnpStateVariable(name = "InternalClient", datatype = STRING, sendEvents = false),
            @UpnpStateVariable(name = "PortMappingEnabled", datatype = "boolean", sendEvents = false),
            @UpnpStateVariable(name = "PortMappingDescription", datatype = STRING, sendEvents = false),
            @UpnpStateVariable(name = "PortMappingLeaseDuration", datatype = "ui4", sendEvents = false),
            @UpnpStateVariable(name = "ConnectionStatus", datatype = STRING, sendEvents = false),
            @UpnpStateVariable(name = "LastConnectionError", datatype = STRING, sendEvents = false),
            @UpnpStateVariable(name = "Uptime", datatype = "ui4", sendEvents = false),
            @UpnpStateVariable(name = "ExternalIPAddress", datatype = STRING, sendEvents = false),
            @UpnpStateVariable(name = "PortMappingIndex", datatype = "ui2", sendEvents = false)

    })
    public static class WANIPConnectionService {

        @UpnpAction
        public void addPortMapping(
                @UpnpInputArgument(name = "NewRemoteHost", stateVariable = REMOTE_HOST) String remoteHost,
                @UpnpInputArgument(name = "NewExternalPort", stateVariable = EXTERNAL_PORT) UnsignedIntegerTwoBytes externalPort,
                @UpnpInputArgument(name = "NewProtocol", stateVariable = PORT_MAPPING_PROTOCOL) String protocol,
                @UpnpInputArgument(name = "NewInternalPort", stateVariable = "InternalPort") UnsignedIntegerTwoBytes internalPort,
                @UpnpInputArgument(name = "NewInternalClient", stateVariable = "InternalClient") String internalClient,
                @UpnpInputArgument(name = "NewEnabled", stateVariable = "PortMappingEnabled") Boolean enabled,
                @UpnpInputArgument(name = "NewPortMappingDescription", stateVariable = "PortMappingDescription") String description,
                @UpnpInputArgument(name = "NewLeaseDuration", stateVariable = "PortMappingLeaseDuration") UnsignedIntegerFourBytes leaseDuration
        ) throws ActionException {
            try {
                addPortMapping(new PortMapping(
                        enabled,
                        leaseDuration,
                        remoteHost,
                        externalPort,
                        internalPort,
                        internalClient,
                        PortMapping.Protocol.valueOf(protocol),
                        description
                ));
            } catch (Exception ex) {
                throw new ActionException(ErrorCode.ACTION_FAILED, "Can't convert port mapping: " + ex.toString(), ex);
            }
        }

        @UpnpAction
        public void deletePortMapping(
                @UpnpInputArgument(name = "NewRemoteHost", stateVariable = REMOTE_HOST) String remoteHost,
                @UpnpInputArgument(name = "NewExternalPort", stateVariable = EXTERNAL_PORT) UnsignedIntegerTwoBytes externalPort,
                @UpnpInputArgument(name = "NewProtocol", stateVariable = PORT_MAPPING_PROTOCOL) String protocol
        ) throws ActionException {
            try {
                deletePortMapping(new PortMapping(
                        remoteHost,
                        externalPort,
                        PortMapping.Protocol.valueOf(protocol)
                ));
            } catch (Exception ex) {
                throw new ActionException(ErrorCode.ACTION_FAILED, "Can't convert port mapping: " + ex.toString(), ex);
            }
        }

        @UpnpAction(out = {
                @UpnpOutputArgument(name = "NewRemoteHost", stateVariable = REMOTE_HOST, getterName = "getRemoteHost"),
                @UpnpOutputArgument(name = "NewExternalPort", stateVariable = EXTERNAL_PORT, getterName = "getExternalPort"),
                @UpnpOutputArgument(name = "NewProtocol", stateVariable = PORT_MAPPING_PROTOCOL, getterName = "getProtocol"),
                @UpnpOutputArgument(name = "NewInternalPort", stateVariable = "InternalPort", getterName = "getInternalPort"),
                @UpnpOutputArgument(name = "NewInternalClient", stateVariable = "InternalClient", getterName = "getInternalClient"),
                @UpnpOutputArgument(name = "NewEnabled", stateVariable = "PortMappingEnabled", getterName = "isEnabled"),
                @UpnpOutputArgument(name = "NewPortMappingDescription", stateVariable = "PortMappingDescription", getterName = "getDescription"),
                @UpnpOutputArgument(name = "NewLeaseDuration", stateVariable = "PortMappingLeaseDuration", getterName = "getLeaseDurationSeconds")
        })
        public PortMapping getGenericPortMappingEntry(
                @UpnpInputArgument(name = "NewPortMappingIndex", stateVariable = "PortMappingIndex") UnsignedIntegerTwoBytes index
        ) throws ActionException {
            return null;
        }

        protected void addPortMapping(PortMapping portMapping) {
        }

        protected void deletePortMapping(PortMapping portMapping) {
        }

        @UpnpAction(out = {
                @UpnpOutputArgument(name = "NewConnectionStatus", stateVariable = "ConnectionStatus", getterName = "getStatus"),
                @UpnpOutputArgument(name = "NewLastConnectionError", stateVariable = "LastConnectionError", getterName = "getLastError"),
                @UpnpOutputArgument(name = "NewUptime", stateVariable = "Uptime", getterName = "getUptime")
        })
        public Connection.StatusInfo getStatusInfo() {
            return null;
        }

        @UpnpAction(out = {
                @UpnpOutputArgument(name = "NewExternalIPAddress", stateVariable = "ExternalIPAddress")
        })
        public String getExternalIPAddress() {
            return null;
        }

    }

}
