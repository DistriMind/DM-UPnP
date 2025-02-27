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

module DM_UPnP_Common {

	requires org.jsoup;
	requires static jakarta.cdi;
	requires static java.desktop;

	requires FlexiLogXML_Common;


	exports fr.distrimind.oss.upnp;
	exports fr.distrimind.oss.upnp.binding;
	exports fr.distrimind.oss.upnp.binding.xml;
	exports fr.distrimind.oss.upnp.binding.annotations;
	exports fr.distrimind.oss.upnp.binding.staging;
	exports fr.distrimind.oss.upnp.controlpoint;
	exports fr.distrimind.oss.upnp.controlpoint.event;
	exports fr.distrimind.oss.upnp.http;
	exports fr.distrimind.oss.upnp.mock;
	exports fr.distrimind.oss.upnp.model;
	exports fr.distrimind.oss.upnp.model.action;
	exports fr.distrimind.oss.upnp.model.gena;
	exports fr.distrimind.oss.upnp.model.message;
	exports fr.distrimind.oss.upnp.model.message.gena;
	exports fr.distrimind.oss.upnp.model.message.header;
	exports fr.distrimind.oss.upnp.model.message.control;
	exports fr.distrimind.oss.upnp.model.message.discovery;
	exports fr.distrimind.oss.upnp.model.meta;
	exports fr.distrimind.oss.upnp.model.profile;
	exports fr.distrimind.oss.upnp.model.resource;
	exports fr.distrimind.oss.upnp.model.state;
	exports fr.distrimind.oss.upnp.model.types;
	exports fr.distrimind.oss.upnp.model.types.csv;
	exports fr.distrimind.oss.upnp.protocol;
	exports fr.distrimind.oss.upnp.protocol.async;
	exports fr.distrimind.oss.upnp.protocol.sync;
	exports fr.distrimind.oss.upnp.registry;
	exports fr.distrimind.oss.upnp.registry.event;
	exports fr.distrimind.oss.upnp.statemachine;
	exports fr.distrimind.oss.upnp.support.model;
	exports fr.distrimind.oss.upnp.support.avtransport;
	exports fr.distrimind.oss.upnp.support.avtransport.lastchange;
	exports fr.distrimind.oss.upnp.support.avtransport.impl;
	exports fr.distrimind.oss.upnp.support.avtransport.impl.state;
	exports fr.distrimind.oss.upnp.support.avtransport.callback;
	exports fr.distrimind.oss.upnp.support.contentdirectory;
	exports fr.distrimind.oss.upnp.support.contentdirectory.callback;
	exports fr.distrimind.oss.upnp.support.contentdirectory.ui;
	exports fr.distrimind.oss.upnp.support.igd;
	exports fr.distrimind.oss.upnp.support.igd.callback;
	exports fr.distrimind.oss.upnp.support.lastchange;
	exports fr.distrimind.oss.upnp.support.model.container;
	exports fr.distrimind.oss.upnp.support.model.dlna;
	exports fr.distrimind.oss.upnp.support.model.item;
	exports fr.distrimind.oss.upnp.support.shared;
	exports fr.distrimind.oss.upnp.support.shared.log;
	exports fr.distrimind.oss.upnp.swing;
	exports fr.distrimind.oss.upnp.swing.logging;
	exports fr.distrimind.oss.upnp.transport;
	exports fr.distrimind.oss.upnp.transport.impl;
	exports fr.distrimind.oss.upnp.transport.spi;
	exports fr.distrimind.oss.upnp.util;
	exports fr.distrimind.oss.upnp.util.io;
	exports fr.distrimind.oss.upnp.xml;
	exports fr.distrimind.oss.upnp.platform;
}

