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

import fr.distrimind.oss.flexilogxml.common.exceptions.XMLStreamException;
import fr.distrimind.oss.upnp.common.model.ModelUtil;
import fr.distrimind.oss.upnp.common.model.ValidationException;
import fr.distrimind.oss.upnp.common.model.meta.Device;
import fr.distrimind.oss.upnp.common.model.meta.Service;
import fr.distrimind.oss.upnp.common.transport.spi.NetworkAddressFactory;
import fr.distrimind.oss.upnp.common.util.Exceptions;
import fr.distrimind.oss.upnp.common.xml.ParserException;
import fr.distrimind.oss.upnp.common.xml.XmlPullParserUtils;

import java.util.Locale;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Pujos
 */
public class RecoveringUDA10DeviceDescriptorBinderImpl extends UDA10DeviceDescriptorBinderImpl {

    final private static DMLogger log = Log.getLogger(RecoveringUDA10DeviceDescriptorBinderImpl.class);
    static final String endRootTag = "</root>";
    public RecoveringUDA10DeviceDescriptorBinderImpl(NetworkAddressFactory networkAddressFactory) {
        super(networkAddressFactory);
    }

    @Override
    public <D extends Device<?, D, S>, S extends Service<?, D, S>> D describe(D undescribedDevice, String _descriptorXml) throws DescriptorBindingException, ValidationException {

        D device = null;
        DescriptorBindingException originalException;
        String descriptorXml=null;
        if (_descriptorXml != null)
            descriptorXml = _descriptorXml.trim(); // Always trim whitespace
        try {

            try {

                device = super.describe(undescribedDevice, descriptorXml);
                return device;
            } catch (DescriptorBindingException ex) {
                if (log.isWarnEnabled())
                    log.warn("Regular parsing failed: ", Exceptions.unwrap(ex).getMessage());
                originalException = ex;
            }

            String fixedXml;
            // The following modifications are not cumulative!

            fixedXml = fixGarbageLeadingChars(descriptorXml);
            if (fixedXml != null) {
                try {
                    device = super.describe(undescribedDevice, fixedXml);
                    return device;
                } catch (DescriptorBindingException ex) {
                    if (log.isWarnEnabled())
                        log.warn("Removing leading garbage didn't work: ", Exceptions.unwrap(ex));
                }
            }

            fixedXml = fixGarbageTrailingChars(descriptorXml, originalException);
            if (fixedXml != null) {
                try {
                    device = super.describe(undescribedDevice, fixedXml);
                    return device;
                } catch (DescriptorBindingException ex) {
                    if (log.isWarnEnabled())
                        log.warn("Removing trailing garbage didn't work: ", Exceptions.unwrap(ex));
                }
            }

            // Try to fix "up to five" missing namespace declarations
            DescriptorBindingException lastException = originalException;
            fixedXml = descriptorXml;
            for (int retryCount = 0; retryCount < 5; retryCount++) {
                fixedXml = fixMissingNamespaces(fixedXml, lastException);
                if (fixedXml != null) {
                    try {
                        device = super.describe(undescribedDevice, fixedXml);
                        return device;
                    } catch (DescriptorBindingException ex) {
                        if (log.isWarnEnabled())
                            log.warn("Fixing namespace prefix didn't work: ", Exceptions.unwrap(ex));
                        lastException = ex;
                    }
                } else {
                    break; // We can stop, no more namespace fixing can be done
                }
            }

            fixedXml = XmlPullParserUtils.fixXMLEntities(descriptorXml);
            if(fixedXml==null || !fixedXml.equals(descriptorXml)) {
                try {
                    device = super.describe(undescribedDevice, fixedXml);
                    return device;
                } catch (DescriptorBindingException ex) {
                    if (log.isWarnEnabled())
                        log.warn("Fixing XML entities didn't work: ", Exceptions.unwrap(ex));
                }
            }

            handleInvalidDescriptor(descriptorXml, originalException);

        } catch (ValidationException ex) {
            device = handleInvalidDevice(descriptorXml, device, ex);
            if (device != null)
                return device;
        }
        throw new IllegalStateException("No device produced, did you swallow exceptions in your subclass?");
    }

    private String fixGarbageLeadingChars(String descriptorXml) {
        if (ModelUtil.checkDescriptionXMLNotValid(descriptorXml))
            return null;
    		/* Recover this:

    		HTTP/1.1 200 OK
    		Content-Length: 4268
    		Content-Type: text/xml; charset="utf-8"
    		Server: Microsoft-Windows/6.2 UPnP/1.0 UPnP-Device-Host/1.0 Microsoft-HTTPAPI/2.0
    		Date: Sun, 07 Apr 2013 02:11:30 GMT

    		@7:5 in java.io.StringReader@407f6b00) : HTTP/1.1 200 OK
    		Content-Length: 4268
    		Content-Type: text/xml; charset="utf-8"
    		Server: Microsoft-Windows/6.2 UPnP/1.0 UPnP-Device-Host/1.0 Microsoft-HTTPAPI/2.0
    		Date: Sun, 07 Apr 2013 02:11:30 GMT

    		<?xml version="1.0"?>...
    	    */

        int index = descriptorXml.indexOf("<?xml");
        if (index == -1) return descriptorXml;
        return descriptorXml.substring(index);
    }

    protected String fixGarbageTrailingChars(String descriptorXml, DescriptorBindingException ex) {
        if (ModelUtil.checkDescriptionXMLNotValid(descriptorXml))
            return null;

        int index = descriptorXml.indexOf(endRootTag);
        if (index == -1) {
            if (log.isWarnEnabled())
                log.warn("No closing </root> element in descriptor");
            return null;
        }
        if (descriptorXml.length() != index + endRootTag.length()) {
            if (log.isWarnEnabled())
                log.warn("Detected garbage characters after <root> node, removing"+(ex==null?"":ex.getMessage()));
            return descriptorXml.substring(0, index) + endRootTag;
        }
        return null;
    }
    private static final Pattern patternPrefix = Pattern.compile("www.w3.org/TR/1999/REC-xml-names-19990114#ElementPrefixUnbound\\?(.+)&.+:.+");
    private static final Pattern patternUndefinedPrefix = Pattern.compile("undefined prefix: ([^ ]+)");
    private static final Pattern patternRoot = Pattern.compile("<root([^>]*)");
    private static final Pattern patternRootEndRoot = Pattern.compile("<root[^>]*>(.*)</root>", Pattern.DOTALL);
    protected String fixMissingNamespaces(String descriptorXml, DescriptorBindingException ex) {
        // Windows: DescriptorBindingException: Could not parse device descriptor: org.seamless.xml.ParserException: org.xml.sax.SAXParseException: The prefix "dlna" for element "dlna:X_DLNADOC" is not bound.
        // Android: org.xmlpull.v1.XmlPullParserException: undefined prefix: dlna (position:START_TAG <{null}dlna:X_DLNADOC>@19:17 in java.io.StringReader@406dff48)

        // We can only handle certain exceptions, depending on their type and message
        if (ModelUtil.checkDescriptionXMLNotValid(descriptorXml)) {
            return null;
        }
        Throwable cause = ex.getCause();
        if (cause instanceof XMLStreamException)
        {
            Throwable t=cause.getCause();
            if ("org.xmlpull.v1.XmlPullParserException".equals(t.getClass().getName()))
                cause=t;
        }
        else if (!(cause instanceof ParserException)) {
            return null;
        }
        String message = cause.getMessage();
        if (message == null)
            return null;

        Matcher matcher = patternPrefix.matcher(message);

        if (!matcher.find() || matcher.groupCount() != 1) {
            matcher = patternUndefinedPrefix.matcher(message);
            if (!matcher.find() || matcher.groupCount() != 1) {
                return null;
            }
        }

        String missingNS = matcher.group(1);
        if (log.isWarnEnabled())
            log.warn("Fixing missing namespace declaration for: " + missingNS);

        // Extract <root> attributes
        matcher = patternRoot.matcher(descriptorXml);
        if (!matcher.find() || matcher.groupCount() != 1) {
            if (log.isDebugEnabled())
                log.debug("Could not find <root> element attributes");
            return null;
        }

        String rootAttributes = matcher.group(1);
        if (log.isDebugEnabled())
            log.debug("Preserving existing <root> element attributes/namespace declarations: " + matcher.group(0));

        // Extract <root> body
        matcher = patternRootEndRoot.matcher(descriptorXml);
        if (!matcher.find() || matcher.groupCount() != 1) {
            log.debug("Could not extract body of <root> element");
            return null;
        }

        String rootBody = matcher.group(1);

        // Add missing namespace, it only matters that it is defined, not that it is correct
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
            + "\t<root "
            + String.format(Locale.ROOT, "xmlns:%s=\"urn:schemas-dlna-org:device-1-0\"", missingNS) + rootAttributes + ">"
            + rootBody
            + endRootTag;

        // TODO: Should we match different undeclared prefixes with their correct namespace?
        // So if it's "dlna" we use "urn:schemas-dlna-org:device-1-0" etc.
    }

    /**
     * Handle processing errors while reading XML descriptors.
     *
     * <p>
     * Typically, you want to log this problem or create an error report, and in any
     * case, throw a {@link DescriptorBindingException} to notify the caller of the
     * binder of this failure. The default implementation simply rethrows the
     * given exception.
   
     *
     * @param xml       The original XML causing the parsing failure.
     * @param exception The original exception while parsing the XML.
     */
    protected void handleInvalidDescriptor(String xml, DescriptorBindingException exception)
        throws DescriptorBindingException {
        throw exception;
    }

    /**
     * Handle processing errors while binding XML descriptors.
     *
     * <p>
     * Typically, you want to log this problem or create an error report. You
     * should throw a {@link ValidationException} to notify the caller of the
     * binder of failure. The default implementation simply rethrows the
     * given exception.
   
     * <p>
     * This method gives you a final chance to fix the problem, instead of
     * throwing an exception, you could try to create valid {@link Device}
     * model and return it.
   
     *
     * @param xml       The original XML causing the binding failure.
     * @param device    The unfinished {@link Device} that failed validation
     * @param exception The errors found when validating the {@link Device} model.
     * @return Device A "fixed" {@link Device} model, instead of throwing an exception.
     */
    protected <D extends Device<?, D, ?>> D handleInvalidDevice(String xml, D device, ValidationException exception)
        throws ValidationException {
        throw exception;
    }
}
