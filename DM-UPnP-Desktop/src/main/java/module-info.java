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

module fr.distrimind.oss.upnp.desktop {
	requires fr.distrimind.oss.upnp.common;
	requires fr.distrimind.oss.flexilogxml.desktop;
	requires static jdk.httpserver;
	requires fr.distrimind.oss.flexilogxml.common;
	exports fr.distrimind.oss.upnp.desktop.platform;
	exports fr.distrimind.oss.upnp.desktop.transport.impl;
}

