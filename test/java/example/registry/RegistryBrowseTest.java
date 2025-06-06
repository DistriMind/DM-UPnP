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
package example.registry;

import fr.distrimind.oss.upnp.common.mock.MockUpnpService;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;
import fr.distrimind.oss.upnp.common.model.resource.DeviceDescriptorResource;
import fr.distrimind.oss.upnp.common.model.resource.Resource;
import fr.distrimind.oss.upnp.common.model.meta.Device;
import fr.distrimind.oss.upnp.common.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.common.model.meta.RemoteDevice;
import fr.distrimind.oss.upnp.common.model.types.DeviceType;
import fr.distrimind.oss.upnp.common.model.types.ServiceType;
import fr.distrimind.oss.upnp.common.model.types.UDADeviceType;
import fr.distrimind.oss.upnp.common.model.types.UDAServiceType;
import fr.distrimind.oss.upnp.common.model.types.UDN;
import fr.distrimind.oss.upnp.common.registry.Registry;
import fr.distrimind.oss.upnp.common.test.data.SampleData;
import fr.distrimind.oss.upnp.common.test.data.SampleDeviceRoot;
import fr.distrimind.oss.upnp.common.test.data.SampleDeviceRootLocal;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import static org.testng.Assert.*;

/**
 * Browsing the Registry
 * <p>
 * Although you typically create a <code>RegistryListener</code> to be notified of discovered and
 * disappearing UPnP devices on your network, sometimes you have to browse the <code>Registry</code>
 * manually.
 * </p>
 * <a class="citation" href="javadoc://this#findDevice" style="read-title: false"/>
 * <a class="citation" href="javadoc://this#findDeviceByType" style="read-title: false"/>
 */
public class RegistryBrowseTest {

    /**
     * <p>
     * The following call will return a device with the given unique device name, but
     * only a root device and not any embedded device. Set the second parameter of
     * <code>registry.getDevice()</code> to <code>false</code> if the device you are
     * looking for might be an embedded device.
   
     * <a class="citation" href="javacode://this" style="include: FIND_ROOT_UDN"/>
     * <p>
     * If you know that the device you need is a <code>LocalDevice</code> - or a
     * <code>RemoteDevice</code> - you can use the following operation:
   
     * <a class="citation" href="javacode://this" style="include: FIND_LOCAL_DEVICE" id="javacode_find_device_local"/>
     */
    @Test
    public void findDevice() throws Exception {
        MockUpnpService upnpService = new MockUpnpService();
        LocalDevice<?> device = SampleData.createLocalDevice();
        upnpService.getRegistry().addDevice(device);

        UDN udn = device.getIdentity().getUdn();

        Registry registry = upnpService.getRegistry();                          // DOC: FIND_ROOT_UDN
        Device<?, ?, ?> foundDevice = registry.getDevice(udn, true);

        assertEquals(foundDevice.getIdentity().getUdn(), udn);                  // DOC: FIND_ROOT_UDN

        LocalDevice<?> localDevice = registry.getLocalDevice(udn, true);           // DOC: FIND_LOCAL_DEVICE
        assertEquals(localDevice.getIdentity().getUdn(), udn);

        SampleDeviceRootLocal.assertLocalResourcesMatch(
                upnpService.getConfiguration().getNamespace().getResources(device)
        );
    }

    /**
     * <p>
     * Most of the time you need a device that is of a particular type or that implements
     * a particular service type, because this is what your control point can handle:
   
     * <a class="citation" href="javacode://this" style="include: FIND_DEV_TYPE"/>
     * <a class="citation" href="javacode://this" style="include: FIND_SERV_TYPE" id="javacode_find_serv_type"/>
     */
    @Test
    public void findDeviceByType() throws Exception {
        MockUpnpService upnpService = new MockUpnpService();
        LocalDevice<? extends LocalService<?>> device = SampleData.createLocalDevice();
        upnpService.getRegistry().addDevice(device);

        Registry registry = upnpService.getRegistry();

		DeviceType deviceType = new UDADeviceType("MY-DEVICE-TYPE", 1);         // DOC: FIND_DEV_TYPE
		Collection<Device<?, ?, ?>> devices = registry.getDevices(deviceType);           // DOC: FIND_DEV_TYPE
		assertEquals(devices.size(), 1);

		ServiceType serviceType = new UDAServiceType("MY-SERVICE-TYPE-ONE", 1); // DOC: FIND_SERV_TYPE
		devices = registry.getDevices(serviceType);          // DOC: FIND_SERV_TYPE
		assertEquals(devices.size(), 1);
	}


    @Test
    public void findLocalDevice() throws Exception {
        MockUpnpService upnpService = new MockUpnpService();

        LocalDevice<?> deviceOne = SampleData.createLocalDevice();
        upnpService.getRegistry().addDevice(deviceOne);

        DeviceDescriptorResource<?> resource =
                upnpService.getRegistry().getResource(
                        DeviceDescriptorResource.class,
                        SampleDeviceRoot.getDeviceDescriptorURI()
        );

        assertNotNull(resource);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void findLocalDeviceInvalidRelativePath() throws Exception {
        MockUpnpService upnpService = new MockUpnpService();

        LocalDevice<?> deviceOne = SampleData.createLocalDevice();
        upnpService.getRegistry().addDevice(deviceOne);

        upnpService.getRegistry().getResource(
                        DeviceDescriptorResource.class,
                        URI.create("http://host/invalid/absolute/URI")
        );
    }

    /* TODO: We for now just ignore duplicate devices because we need to test proxies
    @Test(expectedExceptions = RegistrationException.class)
    public void registerDuplicateDevices() throws Exception {
        MockUpnpService upnpService = new MockUpnpService();


        LocalDevice deviceOne = SampleData.createLocalDevice();
        upnpService.getRegistry().addDevice(deviceOne);

        LocalDevice deviceTwo = SampleData.createLocalDevice();
        upnpService.getRegistry().addDevice(deviceTwo);
    }
    */

    @Test
    public void cleanupRemoteDevice() throws IOException {
        MockUpnpService upnpService = new MockUpnpService();
        RemoteDevice rd = SampleData.createRemoteDevice();

        upnpService.getRegistry().addDevice(rd);

        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 1);

        Resource<?> resource = upnpService.getRegistry().getResource(
                URI.create("/dev/MY-DEVICE-123/svc/upnp-org/MY-SERVICE-123/event/cb")
        );
        assertNotNull(resource);

        upnpService.getRegistry().removeDevice(rd);

        assertEquals(upnpService.getRegistry().getRemoteDevices().size(), 0);

        resource = upnpService.getRegistry().getResource(
                URI.create("/dev/MY-DEVICE-123/svc/upnp-org/MY-SERVICE-123/event/cb")
        );
        assertNull(resource);
    }

/*
    public Device getDevice(UDN udn, boolean rootOnly);

    public LocalDevice getLocalDevice(UDN udn, boolean rootOnly);

    public RemoteDevice getRemoteDevice(UDN udn, boolean rootOnly);

    public Collection<LocalDevice> getLocalDevices();

    public Collection<RemoteDevice> getRemoteDevices();

    public Collection<Device> getDevices();

    public Collection<Device> getDevices(DeviceType deviceType);

    public Collection<Device> getDevices(ServiceType serviceType);

    public Service getService(ServiceReference serviceReference);

 */
}
