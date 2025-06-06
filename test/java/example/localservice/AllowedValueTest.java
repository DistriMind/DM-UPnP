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

package example.localservice;

import fr.distrimind.oss.upnp.common.binding.LocalServiceBinder;
import fr.distrimind.oss.upnp.common.binding.annotations.AnnotationLocalServiceBinder;
import fr.distrimind.oss.upnp.common.model.DefaultServiceManager;
import fr.distrimind.oss.upnp.common.model.meta.DeviceDetails;
import fr.distrimind.oss.upnp.common.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;
import fr.distrimind.oss.upnp.common.model.types.Datatype;
import fr.distrimind.oss.upnp.common.model.types.DeviceType;
import fr.distrimind.oss.upnp.common.test.data.SampleData;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Exclusive list of string values
 * <p>
 * If you have a static list of legal string values, set it directly on the annotation
 * of your state variable's field:
 * </p>
 * <a class="citation" href="javacode://example.localservice.MyServiceWithAllowedValues" style="include: VAR"/>
 * <p>
 * Alternatively, if your allowed values have to be determined dynamically when
 * your service is being bound, you can implement a class with the
 * <code>AllowedValueProvider</code> interface:
 * </p>
 * <a class="citation" href="javacode://example.localservice.MyServiceWithAllowedValueProvider" style="include: PROVIDER"/>
 * <p>
 * Then, instead of specifying a static list of string values in your state variable declaration,
 * name the provider class:
 * </p>
 * <a class="citation" id="MyServiceWithAllowedValueProvider-VAR" href="javacode://example.localservice.MyServiceWithAllowedValueProvider" style="include: VAR"/>
 * <p>
 * Note that this provider will only be queried when your annotations are being processed,
 * once when your service is bound in DM-UPnP.
 * </p>
 */
public class AllowedValueTest {

    public <T> LocalDevice<T> createTestDevice(Class<T> serviceClass) throws Exception {

        LocalServiceBinder binder = new AnnotationLocalServiceBinder();
        LocalService<T> svc = binder.read(serviceClass);
        svc.setManager(new DefaultServiceManager<>(svc, serviceClass));

        return new LocalDevice<>(
            SampleData.createLocalDeviceIdentity(),
            new DeviceType("mydomain", "CustomDevice", 1),
            new DeviceDetails("A Custom Device"),
            svc
        );
    }

    @DataProvider(name = "devices")
    public Object[][] getDevices() {
        try {
            return new LocalDevice[][]{
                {createTestDevice(MyServiceWithAllowedValues.class)},
                {createTestDevice(MyServiceWithAllowedValueProvider.class)},
            };
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            // Damn testng swallows exceptions in provider/factory methods
            throw new RuntimeException(ex);
        }
    }

    @Test(dataProvider = "devices")
    public void validateBinding(LocalDevice<?> device) {
        LocalService<?> svc = device.getServices().iterator().next();
        assertEquals(svc.getStateVariables().size(), 1);
        assertEquals(svc.getStateVariables().iterator().next().getTypeDetails().getDatatype().getBuiltin(), Datatype.Builtin.STRING);
        assertEquals(svc.getStateVariables().iterator().next().getTypeDetails().getAllowedValues().size(), 3);
        assertEquals(svc.getStateVariables().iterator().next().getTypeDetails().getAllowedValues().get(0), "Foo");
        assertEquals(svc.getStateVariables().iterator().next().getTypeDetails().getAllowedValues().get(1), "Bar");
        assertEquals(svc.getStateVariables().iterator().next().getTypeDetails().getAllowedValues().get(2), "Baz");
    }

}
