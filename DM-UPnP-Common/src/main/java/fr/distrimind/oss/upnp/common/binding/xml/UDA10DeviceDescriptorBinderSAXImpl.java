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

package fr.distrimind.oss.upnp.common.binding.xml;

import fr.distrimind.oss.upnp.common.binding.staging.MutableDevice;
import fr.distrimind.oss.upnp.common.binding.staging.MutableIcon;
import fr.distrimind.oss.upnp.common.binding.staging.MutableService;
import fr.distrimind.oss.upnp.common.binding.staging.MutableUDAVersion;
import fr.distrimind.oss.upnp.common.model.ModelUtil;
import fr.distrimind.oss.upnp.common.model.ValidationException;
import fr.distrimind.oss.upnp.common.model.meta.Device;
import fr.distrimind.oss.upnp.common.model.meta.Service;
import fr.distrimind.oss.upnp.common.model.types.DLNACaps;
import fr.distrimind.oss.upnp.common.model.types.DLNADoc;
import fr.distrimind.oss.upnp.common.model.types.InvalidValueException;
import fr.distrimind.oss.upnp.common.model.types.ServiceId;
import fr.distrimind.oss.upnp.common.model.types.ServiceType;
import fr.distrimind.oss.upnp.common.model.types.UDN;
import fr.distrimind.oss.upnp.common.transport.spi.NetworkAddressFactory;
import fr.distrimind.oss.upnp.common.util.MimeType;
import fr.distrimind.oss.upnp.common.xml.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

import static fr.distrimind.oss.upnp.common.binding.xml.Descriptor.Device.ELEMENT;

/**
 * A JAXP SAX parser implementation, which is actually slower than the DOM implementation (on desktop and on Android)!
 *
 * @author Christian Bauer
 */
public class UDA10DeviceDescriptorBinderSAXImpl extends UDA10DeviceDescriptorBinderImpl {

    final private static DMLogger log = Log.getLogger(UDA10DeviceDescriptorBinderSAXImpl.class);

    public UDA10DeviceDescriptorBinderSAXImpl(NetworkAddressFactory networkAddressFactory) {
        super(networkAddressFactory);
    }

    @Override
    public <D extends Device<?, D, S>, S extends Service<?, D, S>> D describe(D undescribedDevice, String descriptorXml) throws DescriptorBindingException, ValidationException {

        if (ModelUtil.checkDescriptionXMLNotValid(descriptorXml)) {
            throw new DescriptorBindingException("Null or empty descriptor");
        }

        try {
            if (log.isDebugEnabled())
                log.debug("Populating device from XML descriptor: " + undescribedDevice);

            // Read the XML into a mutable descriptor graph

            SAXParser parser = new SAXParser();

            MutableDevice<D, S> descriptor = new MutableDevice<>();
            new RootHandler<>(descriptor, parser);

            parser.parse(
                    new InputSource(
                            // TODO: UPNP VIOLATION: Virgin Media Superhub sends trailing spaces/newlines after last XML element, need to trim()
                            new StringReader(descriptorXml.trim())
                    )
            );

            // Build the immutable descriptor graph
            return descriptor.build(undescribedDevice);

        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw DescriptorBindingException.getDescriptorBindingException("Could not parse device descriptor: " + ex, ex);
        }
    }

    protected static class RootHandler<D extends Device<?, D, S>, S extends Service<?, D, S>> extends DeviceDescriptorHandler<MutableDevice<D, S>> {

        public RootHandler(MutableDevice<D, S> instance, SAXParser parser) {
            super(instance, parser);
        }

        @Override
        public void startElement(ELEMENT element, Attributes attributes) throws SAXException {

            if (element.equals(SpecVersionHandler.EL)) {
                MutableUDAVersion udaVersion = new MutableUDAVersion();
                getInstance().udaVersion = udaVersion;
                new SpecVersionHandler(udaVersion, this);
            }

            if (element.equals(DeviceHandler.EL)) {
                new DeviceHandler<>(getInstance(), this);
            }

        }

        @Override
        public void endElement(ELEMENT element) throws SAXException {
			if (Objects.requireNonNull(element) == ELEMENT.URLBase) {
				try {
					String urlString = getCharacters();
					if (urlString != null && !urlString.isEmpty()) {
						// We hope it's  RFC 2396 and RFC 2732 compliant
						getInstance().baseURL = new URL(urlString);
					}
				} catch (Exception ex) {
					throw new SAXException("Invalid URLBase: " + ex);
				}
			}
        }
    }

    protected static class SpecVersionHandler extends DeviceDescriptorHandler<MutableUDAVersion> {

        public static final ELEMENT EL = ELEMENT.specVersion;

        public SpecVersionHandler(MutableUDAVersion instance, DeviceDescriptorHandler<?> parent) {
            super(instance, parent);
        }

        @Override
        @SuppressWarnings("PMD.SwitchStmtsShouldHaveDefault")
        public void endElement(ELEMENT element) {
            switch (element) {
                case major:
                    String majorVersion = getCharacters().trim();
                    if (!"1".equals(majorVersion)) {
                        if (log.isWarnEnabled()) log.warn("Unsupported UDA major version, ignoring: " + majorVersion);
                        majorVersion = "1";
                    }
                    getInstance().major = Integer.parseInt(majorVersion);
                    break;
                case minor:
                    String minorVersion = getCharacters().trim();
                    if (!"0".equals(minorVersion)) {
                        if (log.isWarnEnabled()) log.warn("Unsupported UDA minor version, ignoring: " + minorVersion);
                    }
                    getInstance().minor = 0;
                    break;
            }
        }

        @Override
        public boolean isLastElement(ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class DeviceHandler<D extends Device<?, D, S>, S extends Service<?, D, S>> extends DeviceDescriptorHandler<MutableDevice<D, S>> {

        public static final ELEMENT EL = ELEMENT.device;

        public DeviceHandler(MutableDevice<D, S> instance, DeviceDescriptorHandler<?> parent) {
            super(instance, parent);
        }

        @Override
        public void startElement(ELEMENT element, Attributes attributes) throws SAXException {

            if (element.equals(IconListHandler.EL)) {
                List<MutableIcon> icons = new ArrayList<>();
                getInstance().icons = icons;
                new IconListHandler(icons, this);
            }

            if (element.equals(ServiceListHandler.EL)) {
                List<MutableService<D, S>> services = new ArrayList<>();
                getInstance().services = services;
                new ServiceListHandler<>(services, this);
            }

            if (element.equals(DeviceListHandler.EL)) {
                List<MutableDevice<D, S>> devices = new ArrayList<>();
                getInstance().embeddedDevices = devices;
                new DeviceListHandler<>(devices, this);
            }
        }

        @Override
        public void endElement(ELEMENT element) {
            switch (element) {
                case deviceType:
                    getInstance().deviceType = getCharacters();
                    break;
                case friendlyName:
                    getInstance().friendlyName = getCharacters();
                    break;
                case manufacturer:
                    getInstance().manufacturer = getCharacters();
                    break;
                case manufacturerURL:
                    getInstance().manufacturerURI = parseURI(getCharacters());
                    break;
                case modelDescription:
                    getInstance().modelDescription = getCharacters();
                    break;
                case modelName:
                    getInstance().modelName = getCharacters();
                    break;
                case modelNumber:
                    getInstance().modelNumber = getCharacters();
                    break;
                case modelURL:
                    getInstance().modelURI = parseURI(getCharacters());
                    break;
                case presentationURL:
                    getInstance().presentationURI = parseURI(getCharacters());
                    break;
                case UPC:
                    getInstance().upc = getCharacters();
                    break;
                case serialNumber:
                    getInstance().serialNumber = getCharacters();
                    break;
                case UDN:
                    getInstance().udn = UDN.valueOf(getCharacters());
                    break;
                case X_DLNADOC:
                    String txt = getCharacters();
                    try {
                        getInstance().dlnaDocs.add(DLNADoc.valueOf(txt));
                    } catch (InvalidValueException ex) {
                        if (log.isInfoEnabled()) log.info("Invalid X_DLNADOC value, ignoring value: " + txt);
                    }
                    break;
                case X_DLNACAP:
                    getInstance().dlnaCaps = DLNACaps.valueOf(getCharacters());
                    break;
            }
        }

        @Override
        public boolean isLastElement(ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class IconListHandler extends DeviceDescriptorHandler<List<MutableIcon>> {

        public static final ELEMENT EL = ELEMENT.iconList;

        public IconListHandler(List<MutableIcon> instance, DeviceDescriptorHandler<?> parent) {
            super(instance, parent);
        }

        @Override
        public void startElement(ELEMENT element, Attributes attributes) throws SAXException {
            if (element.equals(IconHandler.EL)) {
                MutableIcon icon = new MutableIcon();
                getInstance().add(icon);
                new IconHandler(icon, this);
            }
        }

        @Override
        public boolean isLastElement(ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class IconHandler extends DeviceDescriptorHandler<MutableIcon> {

        public static final ELEMENT EL = ELEMENT.icon;

        public IconHandler(MutableIcon instance, DeviceDescriptorHandler<?> parent) {
            super(instance, parent);
        }

        @Override
        public void endElement(ELEMENT element)  {
            switch (element) {
                case width:
                    getInstance().width = Integer.parseInt(getCharacters());
                    break;
                case height:
                    getInstance().height = Integer.parseInt(getCharacters());
                    break;
                case depth:
                	try {
                		getInstance().depth = Integer.parseInt(getCharacters());
                	} catch(NumberFormatException ex) {
                        if (log.isWarnEnabled()) log.warn("Invalid icon depth '" + getCharacters() + "', using 16 as default: ", ex);
                		getInstance().depth = 16;
                	}
                    break;
                case url:
                    getInstance().uri = parseURI(getCharacters());
                    break;
                case mimetype:
                    try {
                        getInstance().mimeType = getCharacters();
                        MimeType.valueOf(getInstance().mimeType);
                    } catch(IllegalArgumentException ex) {
                        if (log.isWarnEnabled()) log.warn("Ignoring invalid icon mime type: " + getInstance().mimeType);
                        getInstance().mimeType = "";
                    }
                    break;
            }
        }

        @Override
        public boolean isLastElement(ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class ServiceListHandler<D extends Device<?, D, S>, S extends Service<?, D, S>> extends DeviceDescriptorHandler<List<MutableService<D, S>>> {

        public static final ELEMENT EL = ELEMENT.serviceList;

        public ServiceListHandler(List<MutableService<D, S>> instance, DeviceDescriptorHandler<?> parent) {
            super(instance, parent);
        }

        @Override
        public void startElement(ELEMENT element, Attributes attributes) throws SAXException {
            if (element.equals(ServiceHandler.EL)) {
                MutableService<D, S> service = new MutableService<>();
                getInstance().add(service);
                new ServiceHandler<>(service, this);
            }
        }

        @Override
        public boolean isLastElement(ELEMENT element) {
            boolean last = element.equals(EL);
            if (last) {
				getInstance().removeIf(service -> service.serviceType == null || service.serviceId == null);
            }
            return last;
        }
    }

    protected static class ServiceHandler<D extends Device<?, D, S>, S extends Service<?, D, S>> extends DeviceDescriptorHandler<MutableService<D, S>> {

        public static final ELEMENT EL = ELEMENT.service;

        public ServiceHandler(MutableService<D, S> instance, DeviceDescriptorHandler<?> parent) {
            super(instance, parent);
        }

        @Override
        public void endElement(ELEMENT element) {
            try {
                switch (element) {
                    case serviceType:
                        getInstance().serviceType = ServiceType.valueOf(getCharacters());
                        break;
                    case serviceId:
                        getInstance().serviceId = ServiceId.valueOf(getCharacters());
                        break;
                    case SCPDURL:
                        getInstance().descriptorURI = parseURI(getCharacters());
                        break;
                    case controlURL:
                        getInstance().controlURI = parseURI(getCharacters());
                        break;
                    case eventSubURL:
                        getInstance().eventSubscriptionURI = parseURI(getCharacters());
                        break;
                }
            } catch (InvalidValueException ex) {

                if (log.isWarnEnabled()) log.warn(
                    "UPnP specification violation, skipping invalid service declaration. ", ex
                );
            }
        }

        @Override
        public boolean isLastElement(ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class DeviceListHandler<D extends Device<?, D, S>, S extends Service<?, D, S>> extends DeviceDescriptorHandler<List<MutableDevice<D, S>>> {

        public static final ELEMENT EL = ELEMENT.deviceList;

        public DeviceListHandler(List<MutableDevice<D, S>> instance, DeviceDescriptorHandler<?> parent) {
            super(instance, parent);
        }

        @Override
        public void startElement(ELEMENT element, Attributes attributes) throws SAXException {
            if (element.equals(DeviceHandler.EL)) {
                MutableDevice<D,S> device = new MutableDevice<>();
                getInstance().add(device);
                new DeviceHandler<>(device, this);
            }
        }

        @Override
        public boolean isLastElement(ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class DeviceDescriptorHandler<I> extends SAXParser.Handler<I> {

        public DeviceDescriptorHandler(I instance) {
            super(instance);
        }

        public DeviceDescriptorHandler(I instance, SAXParser parser) {
            super(instance, parser);
        }

        public DeviceDescriptorHandler(I instance, DeviceDescriptorHandler<?> parent) {
            super(instance, parent);
        }

        public DeviceDescriptorHandler(I instance, SAXParser parser, DeviceDescriptorHandler<?> parent) {
            super(instance, parser, parent);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            ELEMENT el = ELEMENT.valueOrNullOf(localName);
            if (el == null) return;
            startElement(el, attributes);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            ELEMENT el = ELEMENT.valueOrNullOf(localName);
            if (el == null) return;
            endElement(el);
        }

        @Override
        protected boolean isLastElement(String uri, String localName, String qName) {
            ELEMENT el = ELEMENT.valueOrNullOf(localName);
            return el != null && isLastElement(el);
        }

        public void startElement(ELEMENT element, Attributes attributes) throws SAXException {

        }

        public void endElement(ELEMENT element) throws SAXException {

        }

        public boolean isLastElement(ELEMENT element) {
            return false;
        }
    }
}
