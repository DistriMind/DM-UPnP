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

import fr.distrimind.oss.upnp.common.binding.LocalServiceBinder;
import fr.distrimind.oss.upnp.common.binding.annotations.AnnotationLocalServiceBinder;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpAction;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpInputArgument;
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
import fr.distrimind.oss.upnp.common.test.control.ActionSampleData;
import fr.distrimind.oss.upnp.common.test.data.SampleData;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class LocalActionInvocationEnumTest {

    public static final String GET_TARGET = "GetTarget";
    public static final String NEW_TARGET_VALUE = "NewTargetValue";
    public static final String SWITCH_POWER = "SwitchPower";
    public static final String RET_TARGET_VALUE = "RetTargetValue";
    public static final String RESULT_STATUS = "ResultStatus";

    public <T> LocalDevice<T> createTestDevice(LocalService<T> service) throws Exception {
        return new LocalDevice<>(
                SampleData.createLocalDeviceIdentity(),
                new UDADeviceType("BinaryLight", 1),
                new DeviceDetails("Example Binary Light"),
                service
        );
    }

    @DataProvider(name = "devices")
    public Object[][] getDevices() throws Exception {
        LocalServiceBinder binder = new AnnotationLocalServiceBinder();
        return new LocalDevice[][]{
                {createTestDevice(SampleData.readService(binder, TestServiceOne.class))},
                {createTestDevice(SampleData.readService(binder,TestServiceTwo.class))},
                {createTestDevice(SampleData.readService(binder, TestServiceThree.class))},
        };
    }

    @Test(dataProvider = "devices")
    public void invokeActions(LocalDevice<?> device) throws Exception {

        LocalService<?> svc = SampleData.getFirstService(device);

        ActionInvocation<? extends LocalService<?>> checkTargetInvocation = new ActionInvocation<>(svc.getAction(GET_TARGET));
        svc.getExecutor(checkTargetInvocation.getAction()).executeWithUntypedGeneric(checkTargetInvocation);
		assertNull(checkTargetInvocation.getFailure());
        assertEquals(checkTargetInvocation.getOutput().size(), 1);
        assertEquals(checkTargetInvocation.getOutput().iterator().next().toString(), "UNKNOWN");

        ActionInvocation<? extends LocalService<?>> setTargetInvocation = new ActionInvocation<>(svc.getAction("SetTarget"));
        setTargetInvocation.setInput(NEW_TARGET_VALUE, "ON");
        svc.getExecutor(setTargetInvocation.getAction()).executeWithUntypedGeneric(setTargetInvocation);
		assertNull(setTargetInvocation.getFailure());
        assertEquals(setTargetInvocation.getOutput().size(), 0);

        ActionInvocation<? extends LocalService<?>> getTargetInvocation = new ActionInvocation<>(svc.getAction(GET_TARGET));
        svc.getExecutor(getTargetInvocation.getAction()).executeWithUntypedGeneric(getTargetInvocation);
		assertNull(getTargetInvocation.getFailure());
        assertEquals(getTargetInvocation.getOutput().size(), 1);
        assertEquals(getTargetInvocation.getOutput().iterator().next().toString(), "ON");

        ActionInvocation<? extends LocalService<?>> getStatusInvocation = new ActionInvocation<>(svc.getAction("GetStatus"));
        svc.getExecutor(getStatusInvocation.getAction()).executeWithUntypedGeneric(getStatusInvocation);
		assertNull(getStatusInvocation.getFailure());
        assertEquals(getStatusInvocation.getOutput().size(), 1);
        assertEquals(getStatusInvocation.getOutput().iterator().next().toString(), "1");

    }

    /* ####################################################################################################### */

    @UpnpService(
            serviceId = @UpnpServiceId(SWITCH_POWER),
            serviceType = @UpnpServiceType(value = ActionSampleData.SWITCH_POWER, version = 1)
    )
    public static class TestServiceOne {

        public enum Target {
            ON,
            OFF,
            UNKNOWN
        }

        @UpnpStateVariable(sendEvents = false)
        private Target target = Target.UNKNOWN;

        @UpnpStateVariable
        private boolean status = false;

        @UpnpAction
        public void setTarget(@UpnpInputArgument(name = NEW_TARGET_VALUE) String newTargetValue) {
            target = Target.valueOf(newTargetValue);

            status = target == Target.ON;
        }

        @UpnpAction(out = @UpnpOutputArgument(name = RET_TARGET_VALUE))
        public Target getTarget() {
            return target;
        }

        @UpnpAction(out = @UpnpOutputArgument(name = RESULT_STATUS))
        public boolean getStatus() {
            return status;
        }
    }


    /* ####################################################################################################### */

    @UpnpService(
            serviceId = @UpnpServiceId(ActionSampleData.SWITCH_POWER),
            serviceType = @UpnpServiceType(value = ActionSampleData.SWITCH_POWER, version = 1)
    )
    public static class TestServiceTwo {

        public enum Target {
            ON,
            OFF,
            UNKNOWN
        }

        @UpnpStateVariable(sendEvents = false)
        private Target target = Target.UNKNOWN;

        @UpnpStateVariable
        private boolean status = false;

        @UpnpAction
        public void setTarget(@UpnpInputArgument(name = NEW_TARGET_VALUE) String newTargetValue) {
            target = Target.valueOf(newTargetValue);

            status = target == Target.ON;
        }

        @UpnpAction(out = @UpnpOutputArgument(name = RET_TARGET_VALUE, stateVariable = "Target", getterName = "getRealTarget"))
        public void getTarget() {
        }

        public Target getRealTarget() {
            return target;
        }

        @UpnpAction(out = @UpnpOutputArgument(name = RESULT_STATUS))
        public boolean getStatus() {
            return status;
        }
    }

    /* ####################################################################################################### */

    @UpnpService(
            serviceId = @UpnpServiceId(ActionSampleData.SWITCH_POWER),
            serviceType = @UpnpServiceType(value = ActionSampleData.SWITCH_POWER, version = 1)
    )
    public static class TestServiceThree {

        public enum Target {
            ON,
            OFF,
            UNKNOWN
        }

        public static class TargetHolder {
            private final Target t;

            public TargetHolder(Target t) {
                this.t = t;
            }

            public Target getTarget() {
                return t;
            }
        }

        @UpnpStateVariable(sendEvents = false)
        private Target target = Target.UNKNOWN;

        @UpnpStateVariable
        private boolean status = false;

        @UpnpAction
        public void setTarget(@UpnpInputArgument(name = NEW_TARGET_VALUE) String newTargetValue) {
            target = Target.valueOf(newTargetValue);

            status = target == Target.ON;
        }

        @UpnpAction(name = GET_TARGET, out = @UpnpOutputArgument(name = RET_TARGET_VALUE, getterName = "getTarget"))
        public TargetHolder getTargetHolder() {
            return new TargetHolder(target);
        }

        @UpnpAction(out = @UpnpOutputArgument(name = RESULT_STATUS))
        public boolean getStatus() {
            return status;
        }
    }

    /* ####################################################################################################### */

    @UpnpService(
            serviceId = @UpnpServiceId(ActionSampleData.SWITCH_POWER),
            serviceType = @UpnpServiceType(value = ActionSampleData.SWITCH_POWER, version = 1)
    )
    public static class TestServiceFour {

        public enum Target {
            ON,
            OFF,
            UNKNOWN
        }

        public static class TargetHolder {
            private final Target t;

            public TargetHolder(Target t) {
                this.t = t;
            }

            public Target getT() {
                return t;
            }
        }

        @UpnpStateVariable(sendEvents = false)
        private Target target = Target.UNKNOWN;

        @UpnpStateVariable
        private boolean status = false;

        @UpnpAction
        public void setTarget(@UpnpInputArgument(name = NEW_TARGET_VALUE) String newTargetValue) {
            target = Target.valueOf(newTargetValue);

            status = target == Target.ON;
        }

        @UpnpAction(name = GET_TARGET, out = @UpnpOutputArgument(name = RET_TARGET_VALUE, stateVariable = "Target", getterName = "getT"))
        public TargetHolder getTargetHolder() {
            return new TargetHolder(target);
        }

        @UpnpAction(out = @UpnpOutputArgument(name = RESULT_STATUS))
        public boolean getStatus() {
            return status;
        }
    }

}