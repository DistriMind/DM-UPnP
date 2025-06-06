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

package fr.distrimind.oss.upnp.common.protocol;

import java.net.URL;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

import fr.distrimind.oss.upnp.common.model.message.IUpnpHeaders;
import fr.distrimind.oss.upnp.common.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.common.model.message.UpnpRequest;
import fr.distrimind.oss.upnp.common.model.meta.RemoteDeviceIdentity;
import fr.distrimind.oss.upnp.common.registry.RegistrationException;
import fr.distrimind.oss.upnp.common.registry.Registry;
import fr.distrimind.oss.upnp.common.transport.RouterException;
import fr.distrimind.oss.upnp.common.UpnpService;
import fr.distrimind.oss.upnp.common.binding.xml.DescriptorBindingException;
import fr.distrimind.oss.upnp.common.binding.xml.DeviceDescriptorBinder;
import fr.distrimind.oss.upnp.common.binding.xml.ServiceDescriptorBinder;
import fr.distrimind.oss.upnp.common.model.ValidationError;
import fr.distrimind.oss.upnp.common.model.ValidationException;
import fr.distrimind.oss.upnp.common.model.meta.Icon;
import fr.distrimind.oss.upnp.common.model.meta.RemoteDevice;
import fr.distrimind.oss.upnp.common.model.meta.RemoteService;
import fr.distrimind.oss.upnp.common.model.types.ServiceType;
import fr.distrimind.oss.upnp.common.model.types.UDN;
import fr.distrimind.oss.upnp.common.util.Exceptions;

/**
 * Retrieves all remote device XML descriptors, parses them, creates an immutable device and service metadata graph.
 * <p>
 * This implementation encapsulates all steps which are necessary to create a fully usable and populated
 * device metadata graph of a particular UPnP device. It starts with an unhydrated and typically just
 * discovered {@link RemoteDevice}, the only property that has to be available is
 * its {@link RemoteDeviceIdentity}.
 * </p>
 * <p>
 * This protocol implementation will then retrieve the device's XML descriptor, parse it, and retrieve and
 * parse all service descriptors until all device and service metadata has been retrieved. The fully
 * hydrated device is then added to the {@link Registry}.
 * </p>
 * <p>
 * Any descriptor retrieval, parsing, or validation error of the metadata will abort this protocol
 * with a warning message in the log.
 * </p>
 *
 * @author Christian Bauer
 */
public class RetrieveRemoteDescriptors implements Runnable {

    final private static DMLogger log = Log.getLogger(RetrieveRemoteDescriptors.class);

    private final UpnpService upnpService;
    private final RemoteDevice rd;

    private static final List<URL> activeRetrievals = new CopyOnWriteArrayList<>();
    protected List<UDN> errorsAlreadyLogged = new ArrayList<>();

    public RetrieveRemoteDescriptors(UpnpService upnpService, RemoteDevice rd) {
        this.upnpService = upnpService;
        this.rd = rd;
    }

    public UpnpService getUpnpService() {
        return upnpService;
    }

    @Override
	public void run() {

        URL deviceURL = rd.getIdentity().getDescriptorURL();

        // Performance optimization, try to avoid concurrent GET requests for device descriptor,
        // if we retrieve it once, we have the hydrated device. There is no different outcome
        // processing this several times concurrently.

        if (activeRetrievals.contains(deviceURL)) {
			if (log.isTraceEnabled()) {
				log.trace("Exiting early, active retrieval for URL already in progress: " + deviceURL);
			}
			return;
        }

        // Exit if it has been discovered already, could be we have been waiting in the executor queue too long
        if (getUpnpService().getRegistry().getRemoteDevice(rd.getIdentity().getUdn(), true) != null) {
			if (log.isTraceEnabled()) {
				log.trace("Exiting early, already discovered: " + deviceURL);
			}
			return;
        }

        try {
            activeRetrievals.add(deviceURL);
            describe();
        } catch (RouterException ex) {
			if (log.isWarnEnabled()) log.warn(
                "Descriptor retrieval failed: " + deviceURL,
                ex
            );
        } finally {
            activeRetrievals.remove(deviceURL);
        }
    }
	@SuppressWarnings("PMD.LooseCoupling")
    protected void describe() throws RouterException {

        // All the following is a very expensive and time-consuming procedure, thanks to the
        // braindead design of UPnP. Several GET requests, several descriptors, several XML parsing
        // steps - all of this could be done with one and it wouldn't make a difference. So every
        // call of this method has to be really necessary and rare.

    	if(getUpnpService().getRouter() == null) {
    		log.warn("Router not yet initialized");
    		return ;
    	}

    	StreamRequestMessage deviceDescRetrievalMsg;
    	StreamResponseMessage deviceDescMsg;

    	try {

    		deviceDescRetrievalMsg =
                new StreamRequestMessage(UpnpRequest.Method.GET, rd.getIdentity().getDescriptorURL());

            // Extra headers
			IUpnpHeaders headers =
                getUpnpService().getConfiguration().getDescriptorRetrievalHeaders(rd.getIdentity());
            if (headers != null)
                deviceDescRetrievalMsg.getHeaders().putAll(headers);

			if (log.isDebugEnabled()) {
				log.debug("Sending device descriptor retrieval message: " + deviceDescRetrievalMsg);
			}
			deviceDescMsg = getUpnpService().getRouter().send(deviceDescRetrievalMsg);

    	} catch(IllegalArgumentException ex) {
    		// UpnpRequest constructor can throw IllegalArgumentException on invalid URI
    		// IllegalArgumentException can also be thrown by Apache HttpClient on blank URI in send()
			if (log.isWarnEnabled()) log.warn(
                "Device descriptor retrieval failed: "
                + rd.getIdentity().getDescriptorURL()
                + ", possibly invalid URL: " + ex);
            return ;
        }

        if (deviceDescMsg == null) {
			if (log.isWarnEnabled()) log.warn(
                "Device descriptor retrieval failed, no response: " + rd.getIdentity().getDescriptorURL()
            );
            return;
        }

        if (deviceDescMsg.getOperation().isFailed()) {
			if (log.isWarnEnabled()) log.warn(
                    "Device descriptor retrieval failed: "
                            + rd.getIdentity().getDescriptorURL() +
                            ", "
                            + deviceDescMsg.getOperation().getResponseDetails()
            );
            return;
        }

        if (!deviceDescMsg.isContentTypeTextUDA()) {
			if (log.isDebugEnabled()) {
				log.debug(
					"Received device descriptor without or with invalid Content-Type: "
						+ rd.getIdentity().getDescriptorURL());
			}
			// We continue despite the invalid UPnP message because we can still hope to convert the content
        }

        String descriptorContent = deviceDescMsg.getBodyString();
        if (descriptorContent == null || descriptorContent.isEmpty()) {
			if (log.isWarnEnabled()) log.warn("Received empty device descriptor:" + rd.getIdentity().getDescriptorURL());
            return;
        }

		if (log.isDebugEnabled()) {
            log.debug("Received root device descriptor: " + deviceDescMsg);
		}
		describe(descriptorContent);
    }

    protected void describe(String descriptorXML) throws RouterException {

        boolean notifiedStart = false;
        RemoteDevice describedDevice = null;
        try {

            DeviceDescriptorBinder deviceDescriptorBinder =
                    getUpnpService().getConfiguration().getDeviceDescriptorBinderUDA10();

            describedDevice = deviceDescriptorBinder.describe(
                    rd,
                    descriptorXML
            );
			if (describedDevice==null) {
				log.debug("Remote device not describable");
				return;
			}
			if (log.isDebugEnabled()) {
				log.debug("Remote device described (without services) notifying listeners: " + describedDevice);
			}
			notifiedStart = getUpnpService().getRegistry().notifyDiscoveryStart(describedDevice);

			if (log.isDebugEnabled()) {
				log.debug("Hydrating described device's services: " + describedDevice);
			}
			RemoteDevice hydratedDevice = describeServices(describedDevice);
            if (hydratedDevice == null) {
            	if(!errorsAlreadyLogged.contains(rd.getIdentity().getUdn())) {
            		errorsAlreadyLogged.add(rd.getIdentity().getUdn());
					if (log.isWarnEnabled()) log.warn("Device service description failed: " + rd);
            	}
                if (notifiedStart)
                    getUpnpService().getRegistry().notifyDiscoveryFailure(
                            describedDevice,
                            new DescriptorBindingException("Device service description failed: " + rd)
                    );
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Adding fully hydrated remote device to registry: " + hydratedDevice);
				}
				// The registry will do the right thing: A new root device is going to be added, if it's
                // already present or we just received the descriptor again (because we got an embedded
                // devices' notification), it will simply update the expiration timestamp of the root
                // device.
                getUpnpService().getRegistry().addDevice(hydratedDevice);
            }

        } catch (ValidationException ex) {
    		// Avoid error log spam each time device is discovered, errors are logged once per device.
        	if(!errorsAlreadyLogged.contains(rd.getIdentity().getUdn())) {
        		errorsAlreadyLogged.add(rd.getIdentity().getUdn());
				if (log.isWarnEnabled()) log.warn("Could not validate device model: " + rd);
        		for (ValidationError validationError : ex.getErrors()) {
					if (log.isWarnEnabled()) log.warn(validationError.toString());
        		}
                if (describedDevice != null && notifiedStart)
                    getUpnpService().getRegistry().notifyDiscoveryFailure(describedDevice, ex);
        	}

        } catch (DescriptorBindingException ex) {
			if (log.isWarnEnabled()) {
				log.warn("Could not hydrate device or its services from descriptor: " + rd);
				log.warn("Cause was: ", Exceptions.unwrap(ex));
			}
            if (describedDevice != null && notifiedStart)
                getUpnpService().getRegistry().notifyDiscoveryFailure(describedDevice, ex);

        } catch (RegistrationException ex) {
			if (log.isWarnEnabled()) {
				log.warn("Adding hydrated device to registry failed: " + rd);
				log.warn("Cause was: ", ex);
			}
            if (describedDevice != null && notifiedStart)
                getUpnpService().getRegistry().notifyDiscoveryFailure(describedDevice, ex);
        }
    }

    protected RemoteDevice describeServices(RemoteDevice currentDevice)
            throws RouterException, DescriptorBindingException, ValidationException {

        List<RemoteService> describedServices = new ArrayList<>();
        if (currentDevice.hasServices()) {
            List<RemoteService> filteredServices = filterExclusiveServices(currentDevice.getServices());
            for (RemoteService service : filteredServices) {
                RemoteService svc = describeService(service);
                 // Skip invalid services (yes, we can continue with only some services available)
                if (svc != null)
                    describedServices.add(svc);
                else if (log.isWarnEnabled())
					log.warn("Skipping invalid service '" + service + "' of: " + currentDevice);
            }
        }

        List<RemoteDevice> describedEmbeddedDevices = new ArrayList<>();
        if (currentDevice.hasEmbeddedDevices()) {
            for (RemoteDevice embeddedDevice : currentDevice.getEmbeddedDevices()) {
                 // Skip invalid embedded device
                if (embeddedDevice == null)
                    continue;
                RemoteDevice describedEmbeddedDevice = describeServices(embeddedDevice);
                 // Skip invalid embedded services
                if (describedEmbeddedDevice != null)
                    describedEmbeddedDevices.add(describedEmbeddedDevice);
            }
        }

        List<Icon> iconDupes = new ArrayList<>(currentDevice.getIcons().size());
        for (Icon icon : currentDevice.getIcons()) {
            iconDupes.add(icon.deepCopy());
        }

        // Yes, we create a completely new immutable graph here
        return currentDevice.newInstance(
                currentDevice.getIdentity().getUdn(),
                currentDevice.getVersion(),
                currentDevice.getType(),
                currentDevice.getDetails(),
                iconDupes,
                describedServices,
                describedEmbeddedDevices
        );
    }

    protected RemoteService describeService(RemoteService service)
            throws RouterException, DescriptorBindingException, ValidationException {

    	URL descriptorURL;
    	try {
    		descriptorURL = service.getDevice().normalizeURI(service.getDescriptorURI());
    	}  catch(IllegalArgumentException e) {
			if (log.isWarnEnabled()) log.warn("Could not normalize service descriptor URL: " + service.getDescriptorURI());
    		return null;
    	}

        StreamRequestMessage serviceDescRetrievalMsg = new StreamRequestMessage(UpnpRequest.Method.GET, descriptorURL);

        // Extra headers
		IUpnpHeaders headers =
            getUpnpService().getConfiguration().getDescriptorRetrievalHeaders(service.getDevice().getIdentity());
        if (headers != null)
            serviceDescRetrievalMsg.getHeaders().putAll(headers);

		if (log.isDebugEnabled()) {
            log.debug("Sending service descriptor retrieval message: " + serviceDescRetrievalMsg);
		}
		StreamResponseMessage serviceDescMsg = getUpnpService().getRouter().send(serviceDescRetrievalMsg);

        if (serviceDescMsg == null) {
			if (log.isWarnEnabled()) log.warn("Could not retrieve service descriptor, no response: " + service);
            return null;
        }

        if (serviceDescMsg.getOperation().isFailed()) {
			if (log.isWarnEnabled()) log.warn("Service descriptor retrieval failed: "
                                + descriptorURL
                                + ", "
                                + serviceDescMsg.getOperation().getResponseDetails());
            return null;
        }

        if (!serviceDescMsg.isContentTypeTextUDA()) {
			if (log.isDebugEnabled()) {
				log.debug("Received service descriptor without or with invalid Content-Type: " + descriptorURL);
			}
			// We continue despite the invalid UPnP message because we can still hope to convert the content
        }

        String descriptorContent = serviceDescMsg.getBodyString();
        if (descriptorContent == null || descriptorContent.isEmpty()) {
			if (log.isWarnEnabled()) log.warn("Received empty service descriptor:" + descriptorURL);
            return null;
        }

		if (log.isDebugEnabled()) {
            log.debug("Received service descriptor, hydrating service model: " + serviceDescMsg);
		}
		ServiceDescriptorBinder serviceDescriptorBinder =
                getUpnpService().getConfiguration().getServiceDescriptorBinderUDA10();

        return serviceDescriptorBinder.describe(service, descriptorContent);
    }

    protected List<RemoteService> filterExclusiveServices(Collection<RemoteService> services) {
        ServiceType[] exclusiveTypes = getUpnpService().getConfiguration().getExclusiveServiceTypes();

        if (exclusiveTypes == null || exclusiveTypes.length == 0)
            return new ArrayList<>(services);

        List<RemoteService> exclusiveServices = new ArrayList<>();
        for (RemoteService discoveredService : services) {
            for (ServiceType exclusiveType : exclusiveTypes) {
                if (discoveredService.getServiceType().implementsVersion(exclusiveType)) {
					if (log.isDebugEnabled()) {
						log.debug("Including exclusive service: " + discoveredService);
					}
					exclusiveServices.add(discoveredService);
                } else {
					if (log.isDebugEnabled()) {
						log.debug("Excluding unwanted service: " + exclusiveType);
					}
				}
            }
        }
        return exclusiveServices;
    }

}
