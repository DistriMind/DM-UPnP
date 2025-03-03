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

package fr.distrimind.oss.upnp.common.support.avtransport.lastchange;

import fr.distrimind.oss.upnp.common.model.ModelUtil;
import fr.distrimind.oss.upnp.common.support.lastchange.EventedValue;
import fr.distrimind.oss.upnp.common.support.lastchange.LastChangeParser;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.util.Set;

/**
 * @author Christian Bauer
 */
public class AVTransportLastChangeParser extends LastChangeParser {

    public static final String NAMESPACE_URI = "urn:schemas-upnp-org:metadata-1-0/AVT/";
    public static final String SCHEMA_RESOURCE = "fr/distrimind/oss/upnp/support/avtransport/metadata-1.01-avt.xsd";

    @Override
    protected String getNamespace() {
        return NAMESPACE_URI;
    }

    @Override
    @SuppressWarnings("PMD.ReturnEmptyCollectionRatherThanNull")
    protected Source[] getSchemaSources() {
        // TODO: Android 2.2 has a broken SchemaFactory, we can't validate
        // http://code.google.com/p/android/issues/detail?id=9491&q=schemafactory&colspec=ID%20Type%20Status%20Owner%20Summary%20Stars
        if (!ModelUtil.ANDROID_RUNTIME) {
            return new Source[]{new StreamSource(
                    Thread.currentThread().getContextClassLoader().getResourceAsStream(SCHEMA_RESOURCE)
            )};
        }
        return null;
    }

    @Override
    protected Set<Class<? extends EventedValue<?>>> getEventedVariables() {
        return AVTransportVariable.ALL;
    }
}
