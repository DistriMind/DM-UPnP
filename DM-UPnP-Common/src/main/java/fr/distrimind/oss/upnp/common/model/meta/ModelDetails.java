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

package fr.distrimind.oss.upnp.common.model.meta;

import java.net.URI;

/**
 * Encpasulates optional metadata about the model of a device.
 *
 * @author Christian Bauer
 */
public class ModelDetails {

    private String modelName;
    private String modelDescription;
    private String modelNumber;
    private URI modelURI;

    ModelDetails() {
    }

    public ModelDetails(String modelName) {
        this.modelName = modelName;
    }

    public ModelDetails(String modelName, String modelDescription) {
        this.modelName = modelName;
        this.modelDescription = modelDescription;
    }

    public ModelDetails(String modelName, String modelDescription, String modelNumber) {
        this.modelName = modelName;
        this.modelDescription = modelDescription;
        this.modelNumber = modelNumber;
    }

    public ModelDetails(String modelName, String modelDescription, String modelNumber, URI modelURI) {
        this.modelName = modelName;
        this.modelDescription = modelDescription;
        this.modelNumber = modelNumber;
        this.modelURI = modelURI;
    }

    public ModelDetails(String modelName, String modelDescription, String modelNumber, String modelURI) throws IllegalArgumentException {
        this.modelName = modelName;
        this.modelDescription = modelDescription;
        this.modelNumber = modelNumber;
        this.modelURI = URI.create(modelURI);
    }

    public String getModelName() {
        return modelName;
    }

    public String getModelDescription() {
        return modelDescription;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public URI getModelURI() {
        return modelURI;
    }
}
