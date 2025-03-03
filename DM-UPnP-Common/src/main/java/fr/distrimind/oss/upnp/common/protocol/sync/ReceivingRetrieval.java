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

package fr.distrimind.oss.upnp.common.protocol.sync;

import fr.distrimind.oss.upnp.common.protocol.ReceivingSync;
import fr.distrimind.oss.upnp.common.registry.Registry;
import fr.distrimind.oss.upnp.common.transport.RouterException;
import fr.distrimind.oss.upnp.common.UpnpService;
import fr.distrimind.oss.upnp.common.binding.xml.DescriptorBindingException;
import fr.distrimind.oss.upnp.common.binding.xml.DeviceDescriptorBinder;
import fr.distrimind.oss.upnp.common.binding.xml.ServiceDescriptorBinder;
import fr.distrimind.oss.upnp.common.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpResponse;
import fr.distrimind.oss.upnp.common.model.message.header.ContentTypeHeader;
import fr.distrimind.oss.upnp.common.model.message.header.ServerHeader;
import fr.distrimind.oss.upnp.common.model.message.header.UpnpHeader;
import fr.distrimind.oss.upnp.common.model.meta.Icon;
import fr.distrimind.oss.upnp.common.model.meta.LocalDevice;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;
import fr.distrimind.oss.upnp.common.model.resource.DeviceDescriptorResource;
import fr.distrimind.oss.upnp.common.model.resource.IconResource;
import fr.distrimind.oss.upnp.common.model.resource.Resource;
import fr.distrimind.oss.upnp.common.model.resource.ServiceDescriptorResource;
import fr.distrimind.oss.upnp.common.util.Exceptions;

import java.net.URI;
import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Handles reception of device/service descriptor and icon retrieval messages.
 *
 * <p>
 * Requested device and service XML descriptors are generated on-the-fly for every request.
 * </p>
 * <p>
 * Descriptor XML is dynamically generated depending on the control point - some control
 * points require different metadata than others for the same device and services.
 * </p>
 *
 * @author Christian Bauer
 */
public class ReceivingRetrieval extends ReceivingSync<StreamRequestMessage, StreamResponseMessage> {

    final private static DMLogger log = Log.getLogger(ReceivingRetrieval.class);

    public ReceivingRetrieval(UpnpService upnpService, StreamRequestMessage inputMessage) {
        super(upnpService, inputMessage);
    }

    @Override
	protected StreamResponseMessage executeSync() throws RouterException {

        if (!getInputMessage().hasHostHeader()) {
			if (log.isDebugEnabled()) {
				log.debug("Ignoring message, missing HOST header: " + getInputMessage());
			}
			return new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.PRECONDITION_FAILED));
        }

        URI requestedURI = getInputMessage().getOperation().getURI();

        Resource<?> foundResource = getUpnpService().getRegistry().getResource(requestedURI);

        if (foundResource == null) {
            foundResource = onResourceNotFound(requestedURI);
            if (foundResource == null) {
				if (log.isDebugEnabled()) {
					log.debug("No local resource found: " + getInputMessage());
				}
				return null;
            }
        }

        return createResponse(requestedURI, foundResource);
    }

    protected StreamResponseMessage createResponse(URI requestedURI, Resource<?> resource) {

        StreamResponseMessage response;

        try {

            if (DeviceDescriptorResource.class.isAssignableFrom(resource.getClass())) {

				if (log.isDebugEnabled()) {
					log.debug("Found local device matching relative request URI: " + requestedURI);
				}
				LocalDevice<?> device = (LocalDevice<?>) resource.getModel();

                DeviceDescriptorBinder deviceDescriptorBinder =
                        getUpnpService().getConfiguration().getDeviceDescriptorBinderUDA10();
                String deviceDescriptor = deviceDescriptorBinder.generate(
                        device,
                        getRemoteClientInfo(),
                        getUpnpService().getConfiguration().getNamespace()
                );
                response = new StreamResponseMessage(
                        deviceDescriptor,
                        new ContentTypeHeader(ContentTypeHeader.DEFAULT_CONTENT_TYPE)
                );
            } else if (ServiceDescriptorResource.class.isAssignableFrom(resource.getClass())) {


				if (log.isDebugEnabled()) {
					log.debug("Found local service matching relative request URI: " + requestedURI);
				}
				LocalService<?> service = (LocalService<?>) resource.getModel();

                ServiceDescriptorBinder serviceDescriptorBinder =
                        getUpnpService().getConfiguration().getServiceDescriptorBinderUDA10();
                String serviceDescriptor = serviceDescriptorBinder.generate(service);
                response = new StreamResponseMessage(
                        serviceDescriptor,
                        new ContentTypeHeader(ContentTypeHeader.DEFAULT_CONTENT_TYPE)
                );

            } else if (IconResource.class.isAssignableFrom(resource.getClass())) {

				if (log.isDebugEnabled()) {
					log.debug("Found local icon matching relative request URI: " + requestedURI);
				}
				Icon icon = (Icon) resource.getModel();
                response = new StreamResponseMessage(icon.getData(), icon.getMimeType());

            } else {

				if (log.isDebugEnabled()) {
					log.debug("Ignoring GET for found local resource: " + resource);
				}
				return null;
            }

        } catch (DescriptorBindingException ex) {
			if (log.isWarnEnabled()) {
				log.warn("Error generating requested device/service descriptor: ", ex);
				log.warn("Exception root cause: ", Exceptions.unwrap(ex));
			}
            response = new StreamResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR);
        }
        
        response.getHeaders().add(UpnpHeader.Type.SERVER, new ServerHeader());

        return response;
    }

    /**
     * Called if the {@link Registry} had no result.
     *
     * @param requestedURIPath The requested URI path
     * @return <code>null</code> or your own {@link Resource}
     */
    protected Resource<?> onResourceNotFound(URI requestedURIPath) {
        return null;
    }
}
