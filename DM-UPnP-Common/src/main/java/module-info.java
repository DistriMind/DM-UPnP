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

module fr.distrimind.oss.upnp.common {

	requires org.jsoup;
	requires static jakarta.cdi;
	requires static java.desktop;

	requires FlexiLogXML_Common;


	exports fr.distrimind.oss.upnp.common;
	exports fr.distrimind.oss.upnp.common.binding;
	exports fr.distrimind.oss.upnp.common.binding.xml;
	exports fr.distrimind.oss.upnp.common.binding.annotations;
	exports fr.distrimind.oss.upnp.common.binding.staging;
	exports fr.distrimind.oss.upnp.common.controlpoint;
	exports fr.distrimind.oss.upnp.common.controlpoint.event;
	exports fr.distrimind.oss.upnp.common.http;
	exports fr.distrimind.oss.upnp.common.mock;
	exports fr.distrimind.oss.upnp.common.model;
	exports fr.distrimind.oss.upnp.common.model.action;
	exports fr.distrimind.oss.upnp.common.model.gena;
	exports fr.distrimind.oss.upnp.common.model.message;
	exports fr.distrimind.oss.upnp.common.model.message.gena;
	exports fr.distrimind.oss.upnp.common.model.message.header;
	exports fr.distrimind.oss.upnp.common.model.message.control;
	exports fr.distrimind.oss.upnp.common.model.message.discovery;
	exports fr.distrimind.oss.upnp.common.model.meta;
	exports fr.distrimind.oss.upnp.common.model.profile;
	exports fr.distrimind.oss.upnp.common.model.resource;
	exports fr.distrimind.oss.upnp.common.model.state;
	exports fr.distrimind.oss.upnp.common.model.types;
	exports fr.distrimind.oss.upnp.common.model.types.csv;
	exports fr.distrimind.oss.upnp.common.protocol;
	exports fr.distrimind.oss.upnp.common.protocol.async;
	exports fr.distrimind.oss.upnp.common.protocol.sync;
	exports fr.distrimind.oss.upnp.common.registry;
	exports fr.distrimind.oss.upnp.common.registry.event;
	exports fr.distrimind.oss.upnp.common.statemachine;
	exports fr.distrimind.oss.upnp.common.support.model;
	exports fr.distrimind.oss.upnp.common.support.avtransport;
	exports fr.distrimind.oss.upnp.common.support.avtransport.lastchange;
	exports fr.distrimind.oss.upnp.common.support.avtransport.impl;
	exports fr.distrimind.oss.upnp.common.support.avtransport.impl.state;
	exports fr.distrimind.oss.upnp.common.support.avtransport.callback;
	exports fr.distrimind.oss.upnp.common.support.contentdirectory;
	exports fr.distrimind.oss.upnp.common.support.contentdirectory.callback;
	exports fr.distrimind.oss.upnp.common.support.contentdirectory.ui;
	exports fr.distrimind.oss.upnp.common.support.igd;
	exports fr.distrimind.oss.upnp.common.support.igd.callback;
	exports fr.distrimind.oss.upnp.common.support.lastchange;
	exports fr.distrimind.oss.upnp.common.support.model.container;
	exports fr.distrimind.oss.upnp.common.support.model.dlna;
	exports fr.distrimind.oss.upnp.common.support.model.item;
	exports fr.distrimind.oss.upnp.common.support.shared;
	exports fr.distrimind.oss.upnp.common.support.shared.log;
	exports fr.distrimind.oss.upnp.common.swing;
	exports fr.distrimind.oss.upnp.common.swing.logging;
	exports fr.distrimind.oss.upnp.common.transport;
	exports fr.distrimind.oss.upnp.common.transport.impl;
	exports fr.distrimind.oss.upnp.common.transport.spi;
	exports fr.distrimind.oss.upnp.common.util;
	exports fr.distrimind.oss.upnp.common.util.io;
	exports fr.distrimind.oss.upnp.common.xml;
	exports fr.distrimind.oss.upnp.common.platform;
}

