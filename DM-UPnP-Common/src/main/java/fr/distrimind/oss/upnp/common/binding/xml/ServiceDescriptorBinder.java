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

import fr.distrimind.oss.flexilogxml.common.xml.IXmlReader;
import fr.distrimind.oss.upnp.common.model.ValidationException;
import fr.distrimind.oss.upnp.common.model.meta.Device;
import fr.distrimind.oss.upnp.common.model.meta.Service;

/**
 * Reads and generates service descriptor XML metadata.
 *
 * @author Christian Bauer
 */
public interface ServiceDescriptorBinder {

    <D extends Device<?, D, S>, S extends Service<?, D, S>> S describe(S undescribedService, String descriptorXml)
            throws DescriptorBindingException, ValidationException;

    <D extends Device<?, D, S>, S extends Service<?, D, S>> S describe(S undescribedService, IXmlReader xmlReader)
            throws DescriptorBindingException, ValidationException;

    String generate(Service<?, ?, ?> service) throws DescriptorBindingException;

    String buildXMLString(Service<?, ?, ?> service) throws DescriptorBindingException;
}