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

package fr.distrimind.oss.upnp.binding.staging;

import fr.distrimind.oss.upnp.model.ValidationException;
import fr.distrimind.oss.upnp.model.meta.Device;
import fr.distrimind.oss.upnp.model.meta.DeviceDetails;
import fr.distrimind.oss.upnp.model.meta.Icon;
import fr.distrimind.oss.upnp.model.meta.ManufacturerDetails;
import fr.distrimind.oss.upnp.model.meta.ModelDetails;
import fr.distrimind.oss.upnp.model.meta.Service;
import fr.distrimind.oss.upnp.model.meta.UDAVersion;
import fr.distrimind.oss.upnp.model.types.DLNACaps;
import fr.distrimind.oss.upnp.model.types.DLNADoc;
import fr.distrimind.oss.upnp.model.types.DeviceType;
import fr.distrimind.oss.upnp.model.types.UDN;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class MutableDevice<D extends Device<?, D, S>, S extends Service<?, D, S>> {

    public UDN udn;
    public MutableUDAVersion udaVersion = new MutableUDAVersion();
    public URL baseURL;
    public String deviceType;
    public String friendlyName;
    public String manufacturer;
    public URI manufacturerURI;
    public String modelName;
    public String modelDescription;
    public String modelNumber;
    public URI modelURI;
    public String serialNumber;
    public String upc;
    public URI presentationURI;
    public List<DLNADoc> dlnaDocs = new ArrayList<>();
    public DLNACaps dlnaCaps;
    public List<MutableIcon> icons = new ArrayList<>();
    public List<MutableService<D,S>> services = new ArrayList<>();
    public List<MutableDevice<D, S>> embeddedDevices = new ArrayList<>();
    public MutableDevice<D, S> parentDevice;

    public D build(D prototype) throws ValidationException {
        // Note how all embedded devices inherit the version and baseURL of the root!
        return build(prototype, createDeviceVersion(), baseURL);
    }

    public D build(D prototype, UDAVersion deviceVersion, URL baseURL) throws ValidationException {
        if (udn==null)
            return null;
        List<D> embeddedDevicesList = new ArrayList<>();
        for (MutableDevice<D, S> embeddedDevice : embeddedDevices) {
            embeddedDevicesList.add(embeddedDevice.build(prototype, deviceVersion, baseURL));
        }
        return prototype.newInstance(
                udn,
                deviceVersion,
                createDeviceType(),
                createDeviceDetails(baseURL),
                createIcons(),
                createServices(prototype),
                embeddedDevicesList
        );
    }

    public UDAVersion createDeviceVersion() {
        return new UDAVersion(udaVersion.major, udaVersion.minor);
    }

    public DeviceType createDeviceType() {
        return deviceType==null?null:DeviceType.valueOf(deviceType);
    }

    public DeviceDetails createDeviceDetails(URL baseURL) {
        return new DeviceDetails(
                baseURL,
                friendlyName,
                new ManufacturerDetails(manufacturer, manufacturerURI),
                new ModelDetails(modelName, modelDescription, modelNumber, modelURI),
                serialNumber, upc, presentationURI, dlnaDocs, dlnaCaps
        );
    }

    public List<Icon> createIcons() {
        List<Icon> iconArray = new ArrayList<>(icons.size());
        for (MutableIcon icon : icons) {
            iconArray.add(icon.build());
        }
        return iconArray;
    }

    public Collection<S> createServices(D prototype) throws ValidationException {
        Collection<S> services = new ArrayList<>(this.services.size());
        for (MutableService<D, S> service : this.services) {
            services.add(service.build(prototype));
        }
        return services;
    }

}
