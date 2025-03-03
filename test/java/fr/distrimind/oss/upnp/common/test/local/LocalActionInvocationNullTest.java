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
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpInputArgument;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpService;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpServiceId;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpServiceType;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpStateVariable;
import fr.distrimind.oss.upnp.common.model.action.ActionInvocation;
import fr.distrimind.oss.upnp.common.model.meta.DeviceDetails;
import fr.distrimind.oss.upnp.common.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;
import fr.distrimind.oss.upnp.common.model.types.ErrorCode;
import fr.distrimind.oss.upnp.common.model.types.UDADeviceType;
import fr.distrimind.oss.upnp.common.test.data.SampleData;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author Christian Bauer
 */
public class LocalActionInvocationNullTest {

    public static final String ONE = "One";
    public static final String FOO = "foo";
    public static final String THREE = "Three";

    @Test
    public void invokeActions() throws Exception {

        LocalDevice<LocalTestServiceOne> device = new LocalDevice<>(
                SampleData.createLocalDeviceIdentity(),
                new UDADeviceType("SomeDevice", 1),
                new DeviceDetails("Some Device"),
                SampleData.readService(LocalTestServiceOne.class)
        );
        LocalService<LocalTestServiceOne> svc = SampleData.getFirstService(device);

        ActionInvocation<LocalService<LocalTestServiceOne>> invocation;

        // This succeeds
        invocation = new ActionInvocation<>(svc.getAction("SetSomeValues"));
        invocation.setInput(ONE, FOO);
        invocation.setInput("Two", "bar");
        invocation.setInput(THREE, "baz");
        svc.getExecutor(invocation.getAction()).execute(invocation);
		assertNull(invocation.getFailure());
        assertEquals(svc.getManager().getImplementation().one, FOO);
        assertEquals(svc.getManager().getImplementation().two, "bar");
        assertEquals(svc.getManager().getImplementation().three.toString(), "baz");

        // Empty string is fine, will be converted into "null"
        invocation = new ActionInvocation<>(svc.getAction("SetSomeValues"));
        invocation.setInput(ONE, FOO);
        invocation.setInput("Two", "");
        invocation.setInput(THREE, null);
        svc.getExecutor(invocation.getAction()).execute(invocation);
		assertNull(invocation.getFailure());
        assertEquals(svc.getManager().getImplementation().one, FOO);
		assertNull(svc.getManager().getImplementation().two);
		assertNull(svc.getManager().getImplementation().three);

        // Null is not fine for primitive input arguments
        invocation = new ActionInvocation<>(svc.getAction("SetPrimitive"));
        invocation.setInput("Primitive", "");
        svc.getExecutor(invocation.getAction()).execute(invocation);
        assertEquals(invocation.getFailure().getErrorCode(), ErrorCode.ARGUMENT_VALUE_INVALID.getCode());
        assertEquals(
                invocation.getFailure().getMessage(),
                "The argument value is invalid. Primitive action method argument 'Primitive' requires input value, can't be null or empty string."
        );

        // We forgot to set one and it's a local invocation (no string conversion)
        invocation = new ActionInvocation<>(svc.getAction("SetSomeValues"));
        invocation.setInput(ONE, null);
        // OOPS! invocation.setInput("Two", null);
        invocation.setInput(THREE, null);
        svc.getExecutor(invocation.getAction()).execute(invocation);
		assertNull(invocation.getFailure());
		assertNull(svc.getManager().getImplementation().one);
		assertNull(svc.getManager().getImplementation().two);
		assertNull(svc.getManager().getImplementation().three);

    }

    @UpnpService(
            serviceId = @UpnpServiceId("SomeService"),
            serviceType = @UpnpServiceType(value = "SomeService", version = 1),
            supportsQueryStateVariables = false,
            stringConvertibleTypes = MyString.class
    )
    public static class LocalTestServiceOne {

        @UpnpStateVariable(name = "A_ARG_TYPE_One", sendEvents = false)
        private String one;

        @UpnpStateVariable(name = "A_ARG_TYPE_Two", sendEvents = false)
        private String two;

        @UpnpStateVariable(name = "A_ARG_TYPE_Three", sendEvents = false)
        private MyString three;

        @UpnpStateVariable(sendEvents = false)
        private boolean primitive;

        @UpnpAction
        public void setSomeValues(@UpnpInputArgument(name = ONE) String one,
                                  @UpnpInputArgument(name = "Two") String two,
                                  @UpnpInputArgument(name = THREE) MyString three) {
            this.one = one;
            this.two = two;
            this.three = three;
        }

        @UpnpAction
        public void setPrimitive(@UpnpInputArgument(name = "Primitive") boolean b) {
            this.primitive = b;
        }
    }

    public static class MyString {
        private final String s;

        public MyString(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return s;
        }
    }
}