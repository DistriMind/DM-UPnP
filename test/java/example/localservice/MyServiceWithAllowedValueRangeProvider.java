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

import fr.distrimind.oss.upnp.common.binding.AllowedValueRangeProvider;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpAction;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpInputArgument;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpOutputArgument;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpService;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpServiceId;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpServiceType;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpStateVariable;

@UpnpService(
        serviceId = @UpnpServiceId("MyService"),
        serviceType = @UpnpServiceType(namespace = "mydomain", value = "MyService")
)
public class MyServiceWithAllowedValueRangeProvider {

    // DOC:PROVIDER
    public static class MyAllowedValueProvider implements AllowedValueRangeProvider {
        @Override
        public long getMinimum() {
            return 10;
        }

        @Override
        public long getMaximum() {
            return 100;
        }

        @Override
        public long getStep() {
            return 5;
        }
    }
    // DOC:PROVIDER

    // DOC:VAR
    @UpnpStateVariable(
        allowedValueRangeProvider = MyAllowedValueProvider.class
    )
    private int restricted;
    // DOC:VAR

    @UpnpAction(out = @UpnpOutputArgument(name = "Out"))
    public int getRestricted() {
        return restricted;
    }

    @UpnpAction
    public void setRestricted(@UpnpInputArgument(name = "In") int restricted) {
        this.restricted = restricted;
    }
}

