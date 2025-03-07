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

package fr.distrimind.oss.upnp.common.test.local;

import fr.distrimind.oss.upnp.common.binding.annotations.UpnpAction;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpOutputArgument;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpService;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpServiceId;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpServiceType;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpStateVariable;
import fr.distrimind.oss.upnp.common.model.action.ActionInvocation;
import fr.distrimind.oss.upnp.common.model.meta.DeviceDetails;
import fr.distrimind.oss.upnp.common.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;
import fr.distrimind.oss.upnp.common.model.types.UDADeviceType;
import fr.distrimind.oss.upnp.common.test.data.SampleData;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author Christian Bauer
 */
public class LocalActionInvocationDatatypesTest {

    public static final String FOUR = "four";

    @Test
    public void invokeActions() throws Exception {

        LocalDevice<LocalTestServiceOne> device = new LocalDevice<>(
                SampleData.createLocalDeviceIdentity(),
                new UDADeviceType("SomeDevice", 1),
                new DeviceDetails("Some Device"),
                SampleData.readService(LocalTestServiceOne.class)
        );
        LocalService<LocalTestServiceOne> svc = SampleData.getFirstService(device);

        ActionInvocation<LocalService<LocalTestServiceOne>> getDataInvocation = new ActionInvocation<>(svc.getAction("GetData"));
        svc.getExecutor(getDataInvocation.getAction()).execute(getDataInvocation);
		assertNull(getDataInvocation.getFailure());
        assertEquals(getDataInvocation.getOutput().size(), 1);
        assertEquals(((byte[]) getDataInvocation.getOutput().iterator().next().getValue()).length, 512);

        // This fails, we can't put arbitrary bytes into a String and hope it will be valid unicode characters!
        /* TODO: This now only logs a warning!
        ActionInvocation getStringDataInvocation = new ActionInvocation(svc.getAction("GetDataString"));
        svc.getExecutor(getStringDataInvocation.getAction()).execute(getStringDataInvocation);
        assertEquals(getStringDataInvocation.getFailure().getErrorCode(), ErrorCode.ARGUMENT_VALUE_INVALID.getCode());
        assertEquals(
                getStringDataInvocation.getFailure().getMessage(),
                "The argument value is invalid. Wrong type or invalid value for 'RandomDataString': " +
                        "Invalid characters in string value (XML 1.0, section 2.2) produced by (StringDatatype)."
        );
        */

        ActionInvocation<LocalService<LocalTestServiceOne>> invocation = new ActionInvocation<>(svc.getAction("GetStrings"));
        svc.getExecutor(invocation.getAction()).execute(invocation);
		assertNull(invocation.getFailure());
        assertEquals(invocation.getOutput().size(), 2);
        assertEquals(invocation.getOutput("One").toString(), "foo");
        assertEquals(invocation.getOutput("Two").toString(), "bar");

        invocation = new ActionInvocation<>(svc.getAction("GetThree"));
        assertEquals(svc.getAction("GetThree").getOutputArguments().iterator().next().getDatatype().getBuiltin().getDescriptorName(), "i2");
        svc.getExecutor(invocation.getAction()).execute(invocation);
		assertNull(invocation.getFailure());
        assertEquals(invocation.getOutput().size(), 1);
        assertEquals(invocation.getOutput("three").toString(), "123");

        invocation = new ActionInvocation<>(svc.getAction("GetFour"));
        assertEquals(svc.getAction("GetFour").getOutputArguments().iterator().next().getDatatype().getBuiltin().getDescriptorName(), "int");
        svc.getExecutor(invocation.getAction()).execute(invocation);
		assertNull(invocation.getFailure());
        assertEquals(invocation.getOutput().size(), 1);
        assertEquals(invocation.getOutput(FOUR).toString(), "456");

        invocation = new ActionInvocation<>(svc.getAction("GetFive"));
        assertEquals(svc.getAction("GetFive").getOutputArguments().iterator().next().getDatatype().getBuiltin().getDescriptorName(), "int");
        svc.getExecutor(invocation.getAction()).execute(invocation);
		assertNull(invocation.getFailure());
        assertEquals(invocation.getOutput().size(), 1);
        assertEquals(invocation.getOutput("five").toString(), "456");
    }

    @UpnpService(
            serviceId = @UpnpServiceId("SomeService"),
            serviceType = @UpnpServiceType(value = "SomeService", version = 1),
            supportsQueryStateVariables = false
    )
    public static class LocalTestServiceOne {

        @UpnpStateVariable(sendEvents = false)
        private final byte[] data;

        @UpnpStateVariable(sendEvents = false, datatype = "string")
        private final String dataString;

        @UpnpStateVariable(sendEvents = false)
        private String one;

        @UpnpStateVariable(sendEvents = false)
        private String two;

        @UpnpStateVariable(sendEvents = false)
        private short three;

        @UpnpStateVariable(sendEvents = false, name = FOUR, datatype = "int")
        private int four;

        public LocalTestServiceOne() {
            data = new byte[512];
            new Random().nextBytes(data);

            try {
                dataString = new String(data, StandardCharsets.UTF_8);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        // This works and the byte[] should not interfere with any Object[] handling in the executors
        @UpnpAction(out = @UpnpOutputArgument(name = "RandomData"))
        public byte[] getData() {
            return data==null?null:data.clone();
        }

        // This fails, we can't just put random data into a string
        @UpnpAction(out = @UpnpOutputArgument(name = "RandomDataString"))
        public String getDataString() {
            return dataString;
        }

        // We are testing _several_ output arguments returned in a bean, access through getters
        @UpnpAction(out = {
                @UpnpOutputArgument(name = "One", getterName = "getOne"),
                @UpnpOutputArgument(name = "Two", getterName = "getTwo")
        })
        public StringsHolder getStrings() {
            return new StringsHolder();
        }

        // Conversion of short into integer/UPnP "i2" datatype
        @UpnpAction(out = @UpnpOutputArgument(name = "three"))
        public short getThree() {
            return 123;
        }

        // Conversion of int into integer/UPnP "int" datatype
        @UpnpAction(out = @UpnpOutputArgument(name = FOUR))
        public Integer getFour() {
            return 456;
        }

        @UpnpAction(out = @UpnpOutputArgument(name = "five", stateVariable = FOUR))
        public int getFive() {
            return 456;
        }
    }

    public static class StringsHolder {
        String one = "foo";
        String two = "bar";

        public String getOne() {
            return one;
        }

        public String getTwo() {
            return two;
        }
    }
}
