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

import fr.distrimind.oss.upnp.common.binding.AllowedValueProvider;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpAction;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpInputArgument;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpOutputArgument;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpService;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpServiceId;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpServiceType;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpStateVariable;

import java.util.Collection;
import java.util.List;

@UpnpService(
        serviceId = @UpnpServiceId("MyService"),
        serviceType = @UpnpServiceType(namespace = "mydomain", value = "MyService")
)
public class MyServiceWithAllowedValueProvider {

    // DOC:PROVIDER
    public static class MyAllowedValueProvider implements AllowedValueProvider {
        @Override
        public Collection<String> getValues() {
            return List.of("Foo", "Bar", "Baz");
        }
    }
    // DOC:PROVIDER

    // DOC:VAR
    @UpnpStateVariable(
        allowedValueProvider= MyAllowedValueProvider.class
    )
    private String restricted;
    // DOC:VAR

    @UpnpAction(out = @UpnpOutputArgument(name = "Out"))
    public String getRestricted() {
        return restricted;
    }

    @UpnpAction
    public void setRestricted(@UpnpInputArgument(name = "In") String restricted) {
        this.restricted = restricted;
    }
}

