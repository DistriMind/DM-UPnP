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
package com.distrimind.upnp_igd.support.model.dlna.message.header;

import com.distrimind.upnp_igd.model.message.header.InvalidHeaderException;

/**
 * @author Mario Franco
 */
public class GetAvailableSeekRangeHeader extends DLNAHeader<Integer> {

    public GetAvailableSeekRangeHeader() {
        setValue(1);
    }

    @Override
    public void setString(String s) throws InvalidHeaderException {
        if (!s.isEmpty()) {
            try {
                int t = Integer.parseInt(s);
                if (t==1)
                    return;
            } catch (Exception ignored) {}
        }
        throw new InvalidHeaderException("Invalid GetAvailableSeekRange header value: " + s);
    }

    @Override
    public String getString() {
        return getValue().toString();
    }
}
