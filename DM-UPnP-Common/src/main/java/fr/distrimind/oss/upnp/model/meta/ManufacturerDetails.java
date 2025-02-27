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

import java.net.URI;

/**
 * Encpasulates optional metadata about a device's manufacturer.
 *
 * @author Christian Bauer
 */
public class ManufacturerDetails {

    private String manufacturer;
    private URI manufacturerURI;

    ManufacturerDetails() {
    }

    public ManufacturerDetails(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public ManufacturerDetails(URI manufacturerURI) {
        this.manufacturerURI = manufacturerURI;
    }

    public ManufacturerDetails(String manufacturer, URI manufacturerURI) {
        this.manufacturer = manufacturer;
        this.manufacturerURI = manufacturerURI;
    }

    public ManufacturerDetails(String manufacturer, String manufacturerURI) throws IllegalArgumentException {
        this.manufacturer = manufacturer;
        this.manufacturerURI = URI.create(manufacturerURI);
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public URI getManufacturerURI() {
        return manufacturerURI;
    }
}
