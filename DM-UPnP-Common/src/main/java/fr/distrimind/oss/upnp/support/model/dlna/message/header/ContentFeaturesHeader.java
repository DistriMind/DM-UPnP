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
package fr.distrimind.oss.upnp.support.model.dlna.message.header;

import java.util.EnumMap;

import fr.distrimind.oss.upnp.support.model.dlna.DLNAAttribute;
import fr.distrimind.oss.upnp.model.message.header.InvalidHeaderException;

/**
 * @author Mario Franco
 */
@SuppressWarnings("PMD.LooseCoupling")
public class ContentFeaturesHeader extends DLNAHeader<EnumMap<DLNAAttribute.Type, DLNAAttribute<?>>> {

    public ContentFeaturesHeader() {
        setValue(new EnumMap<>(DLNAAttribute.Type.class));
    }

    @Override
    public void setString(String s) throws InvalidHeaderException {
        if (!s.isEmpty()) {
            String[] atts = s.split(";");
            for (String att : atts) {
                String[] attNameValue = att.split("=");
                if (attNameValue.length == 2) {
                    DLNAAttribute.Type type = DLNAAttribute.Type.valueOfAttributeName(attNameValue[0]);
                    if (type != null) {
                        DLNAAttribute<?> dlnaAttrinute = DLNAAttribute.newInstance(type, attNameValue[1], "");
                        getValue().put(type, dlnaAttrinute);
                    }
                }
            }
        }
    }

    @Override
    public String getString() {
        StringBuilder s = new StringBuilder();
        for (DLNAAttribute.Type type : DLNAAttribute.Type.values()) {
            String value = getValue().containsKey(type) ? getValue().get(type).getString() : null;
            if (value != null && !value.isEmpty()) {
                s.append((s.length() == 0) ? "" : ";").append(type.getAttributeName()).append("=").append(value);
            }
        }
        return s.toString();
    }
}
