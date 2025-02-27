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

import fr.distrimind.oss.upnp.UpnpService;
import fr.distrimind.oss.upnp.mock.MockUpnpService;
import fr.distrimind.oss.upnp.model.action.ActionInvocation;
import fr.distrimind.oss.upnp.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.model.meta.DeviceIdentity;
import fr.distrimind.oss.upnp.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.model.meta.LocalService;
import fr.distrimind.oss.upnp.model.meta.Service;
import fr.distrimind.oss.upnp.model.types.UDAServiceId;
import fr.distrimind.oss.upnp.support.igd.PortMappingListener;
import fr.distrimind.oss.upnp.support.igd.callback.GetExternalIP;
import fr.distrimind.oss.upnp.support.model.Connection;
import fr.distrimind.oss.upnp.support.igd.callback.GetStatusInfo;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Getting connection information
 * <p>
 * The current connection information, including status, uptime, and last error message can be
 * retrieved from a <em>WAN*Connection</em> service with the following callback:
 * </p>
 * <a class="citation" href="javacode://this#testStatusInfo" style="include: DOC1; exclude: EXC1"/>
 * <p>
 * Additionally, a callback for obtaining the external IP address of a connection is available:
 * </p>
 * <a class="citation" href="javacode://this#testIPAddress" style="include: DOC1; exclude: EXC1"/>
 */
public class ConnectionInfoTest {

    @SuppressWarnings({"SingleStatementInBlock", "unchecked", "CastCanBeRemovedNarrowingVariableType"})
	@Test
    public void testStatusInfo() throws Exception {

        final boolean[] tests = new boolean[1];

        UpnpService upnpService = new MockUpnpService();

        LocalDevice<TestConnection> device = IGDSampleData.createIGDevice(TestConnection.class);
        upnpService.getRegistry().addDevice(device);

        Service<DeviceIdentity, LocalDevice<TestConnection>, ?> service = device.findService(new UDAServiceId(PortMappingListener.WANIP_CONNECTION));         // DOC: DOC1

        upnpService.getControlPoint().execute(
            new GetStatusInfo(service) {

                @Override
                protected void success(Connection.StatusInfo statusInfo) {
                    assertEquals(statusInfo.getStatus(), Connection.Status.Connected);
                    assertEquals(statusInfo.getUptimeSeconds(), 1000);
                    assertEquals(statusInfo.getLastError(), Connection.Error.ERROR_NONE);
                    tests[0] = true;                                                        // DOC: EXC1
                }

                @Override
                public void failure(ActionInvocation<?> invocation,
                                    UpnpResponse operation,
                                    String defaultMsg) {
                    // Something is wrong
                }
            }
        );                                                                                      // DOC: DOC1

        for (boolean test : tests) {
            assert test;
        }
		for (boolean test : ((LocalService<TestConnection>) service).getManager().getImplementation().tests) {
            assert test;
        }

    }

    @SuppressWarnings({"CastCanBeRemovedNarrowingVariableType", "unchecked"})
	@Test
    public void testIPAddress() throws Exception {

        final boolean[] tests = new boolean[1];

        UpnpService upnpService = new MockUpnpService();

        LocalDevice<TestConnection> device = IGDSampleData.createIGDevice(TestConnection.class);
        upnpService.getRegistry().addDevice(device);

        Service<DeviceIdentity, LocalDevice<TestConnection>, ?> service = device.findService(new UDAServiceId(PortMappingListener.WANIP_CONNECTION));         // DOC: DOC1

        upnpService.getControlPoint().execute(
            new GetExternalIP(service) {

                @Override
                protected void success(String externalIPAddress) {
                    assertEquals(externalIPAddress, "123.123.123.123");
                    tests[0] = true;                                                        // DOC: EXC1
                }

                @Override
                public void failure(ActionInvocation<?> invocation,
                                    UpnpResponse operation,
                                    String defaultMsg) {
                    // Something is wrong
                }
            }
        );                                                                                      // DOC: DOC1

        for (boolean test : tests) {
            assert test;
        }
		for (boolean test : ((LocalService<TestConnection>) service).getManager().getImplementation().tests) {
            assert test;
        }

    }
    public static class TestConnection extends IGDSampleData.WANIPConnectionService {

        boolean[] tests = new boolean[1];

        @Override
        public Connection.StatusInfo getStatusInfo() {
            tests[0] = true;
            return new Connection.StatusInfo(
                    Connection.Status.Connected,
                    1000,
                    Connection.Error.ERROR_NONE
            );
        }

        @Override
        public String getExternalIPAddress() {
            tests[0] = true;
            return "123.123.123.123";
        }
    }

}
