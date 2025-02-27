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

package fr.distrimind.oss.upnp.model.meta;

import fr.distrimind.oss.upnp.model.Validatable;
import fr.distrimind.oss.upnp.model.ValidationError;
import fr.distrimind.oss.upnp.model.types.DLNACaps;
import fr.distrimind.oss.upnp.model.types.DLNADoc;

import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.Log;

/**
 * Encapsulates all optional metadata about a device.
 *
 * @author Christian Bauer
 */
public class DeviceDetails implements Validatable {

    final private static DMLogger log = Log.getLogger(DeviceDetails.class);

    final private URL baseURL;
    final private String friendlyName;
    final private ManufacturerDetails manufacturerDetails;
    final private ModelDetails modelDetails;
    final private String serialNumber;
    final private String upc;
    final private URI presentationURI;
    final private List<DLNADoc> dlnaDocs;
    final private DLNACaps dlnaCaps;
    final private DLNACaps secProductCaps; 

    public DeviceDetails(String friendlyName) {
        this(null, friendlyName, null, null, null, null, null);
    }

    public DeviceDetails(String friendlyName, List<DLNADoc> dlnaDocs, DLNACaps dlnaCaps) {
        this(null, friendlyName, null, null, null, null, null, dlnaDocs, dlnaCaps);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails) {
        this(null, friendlyName, manufacturerDetails, null, null, null, null);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails, List<DLNADoc> dlnaDocs, DLNACaps dlnaCaps) {
        this(null, friendlyName, manufacturerDetails, null, null, null, null, dlnaDocs, dlnaCaps);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails,
                         ModelDetails modelDetails) {
        this(null, friendlyName, manufacturerDetails, modelDetails, null, null, null);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails,
                         ModelDetails modelDetails, List<DLNADoc> dlnaDocs, DLNACaps dlnaCaps) {
        this(null, friendlyName, manufacturerDetails, modelDetails, null, null, null, dlnaDocs, dlnaCaps);
    }
    
    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails,
            ModelDetails modelDetails, List<DLNADoc> dlnaDocs, DLNACaps dlnaCaps, DLNACaps secProductCaps) {
    	this(null, friendlyName, manufacturerDetails, modelDetails, null, null, null, dlnaDocs, dlnaCaps, secProductCaps);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails, ModelDetails modelDetails,
                         String serialNumber, String upc) {
        this(null, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, null);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails, ModelDetails modelDetails,
                         String serialNumber, String upc, List<DLNADoc> dlnaDocs, DLNACaps dlnaCaps) {
        this(null, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, null, dlnaDocs, dlnaCaps);
    }

    public DeviceDetails(String friendlyName, URI presentationURI) {
        this(null, friendlyName, null, null, null, null, presentationURI);
    }

    public DeviceDetails(String friendlyName, URI presentationURI, List<DLNADoc> dlnaDocs, DLNACaps dlnaCaps) {
        this(null, friendlyName, null, null, null, null, presentationURI, dlnaDocs, dlnaCaps);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails,
                         ModelDetails modelDetails, URI presentationURI) {
        this(null, friendlyName, manufacturerDetails, modelDetails, null, null, presentationURI);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails,
                         ModelDetails modelDetails, URI presentationURI, List<DLNADoc> dlnaDocs, DLNACaps dlnaCaps) {
        this(null, friendlyName, manufacturerDetails, modelDetails, null, null, presentationURI, dlnaDocs, dlnaCaps);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails, ModelDetails modelDetails,
                         String serialNumber, String upc, URI presentationURI) {
        this(null, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, presentationURI);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails, ModelDetails modelDetails,
                         String serialNumber, String upc, URI presentationURI, List<DLNADoc> dlnaDocs, DLNACaps dlnaCaps) {
        this(null, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, presentationURI, dlnaDocs, dlnaCaps);
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails, ModelDetails modelDetails,
                         String serialNumber, String upc, String presentationURI)
            throws IllegalArgumentException {
        this(null, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, URI.create(presentationURI));
    }

    public DeviceDetails(String friendlyName, ManufacturerDetails manufacturerDetails, ModelDetails modelDetails,
                         String serialNumber, String upc, String presentationURI, List<DLNADoc> dlnaDocs, DLNACaps dlnaCaps)
            throws IllegalArgumentException {
        this(null, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, URI.create(presentationURI), dlnaDocs, dlnaCaps);
    }

    public DeviceDetails(URL baseURL, String friendlyName,
                         ManufacturerDetails manufacturerDetails, ModelDetails modelDetails,
                         String serialNumber, String upc,
                         URI presentationURI) {
        this(baseURL, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, presentationURI, null, null);
    }

    public DeviceDetails(URL baseURL, String friendlyName,
                         ManufacturerDetails manufacturerDetails, ModelDetails modelDetails,
                         String serialNumber, String upc,
                         URI presentationURI, List<DLNADoc> dlnaDocs, DLNACaps dlnaCaps) {
    	 this(baseURL, friendlyName, manufacturerDetails, modelDetails, serialNumber, upc, presentationURI, dlnaDocs, dlnaCaps, null);
    }
    
    public DeviceDetails(URL baseURL, String friendlyName,
                         ManufacturerDetails manufacturerDetails, ModelDetails modelDetails,
                         String serialNumber, String upc,
                         URI presentationURI, List<DLNADoc> dlnaDocs, DLNACaps dlnaCaps, DLNACaps secProductCaps) {
        this.baseURL = baseURL;
        this.friendlyName = friendlyName;
        this.manufacturerDetails = manufacturerDetails == null ? new ManufacturerDetails() : manufacturerDetails;
        this.modelDetails = modelDetails == null ? new ModelDetails() : modelDetails;
        this.serialNumber = serialNumber;
        this.upc = upc;
        this.presentationURI = presentationURI;
        this.dlnaDocs = dlnaDocs != null ? List.copyOf(dlnaDocs) : Collections.emptyList();
        this.dlnaCaps = dlnaCaps;
        this.secProductCaps = secProductCaps;
    }

    public URL getBaseURL() {
        return baseURL;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public ManufacturerDetails getManufacturerDetails() {
        return manufacturerDetails;
    }

    public ModelDetails getModelDetails() {
        return modelDetails;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getUpc() {
        return upc;
    }

    public URI getPresentationURI() {
        return presentationURI;
    }

    public List<DLNADoc> getDlnaDocs() {
        return dlnaDocs;
    }

    public DLNACaps getDlnaCaps() {
        return dlnaCaps;
    }
    
    public DLNACaps getSecProductCaps() {
        return secProductCaps;
    }

    @Override
	public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (getUpc() != null) {
            // This is broken in more than half of the devices I've tested, so let's not even bother with a warning
            if (getUpc().length() != 12) {
				if (log.isDebugEnabled()) {
					log.debug("UPnP specification violation, UPC must be 12 digits: " + getUpc());
				}
			} else {
                try {
                    Long.parseLong(getUpc());
                } catch (NumberFormatException ex) {
					if (log.isDebugEnabled()) {
						log.debug("UPnP specification violation, UPC must be 12 digits all-numeric: " + getUpc());
					}
				}
            }
        }

        return errors;
    }
}
