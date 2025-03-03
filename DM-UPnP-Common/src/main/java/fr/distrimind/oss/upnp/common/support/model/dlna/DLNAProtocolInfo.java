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
package fr.distrimind.oss.upnp.common.support.model.dlna;

import fr.distrimind.oss.upnp.common.support.model.Protocol;
import fr.distrimind.oss.upnp.common.support.model.ProtocolInfo;
import fr.distrimind.oss.upnp.common.model.types.InvalidValueException;
import fr.distrimind.oss.upnp.common.util.MimeType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Encaspulates a MIME type (content format) and transport, protocol, additional information.
 * <p></p>
 * Parses DLNA attributes in the additional information.
 *
 * @author Mario Franco
 */
public class DLNAProtocolInfo extends ProtocolInfo {

    protected final Map<DLNAAttribute.Type, DLNAAttribute<?>> attributes = new EnumMap<>(DLNAAttribute.Type.class);

    public DLNAProtocolInfo(String s) throws InvalidValueException {
        super(s);
        parseAdditionalInfo();
    }

    public DLNAProtocolInfo(MimeType contentFormatMimeType) {
        super(contentFormatMimeType);
    }

    public DLNAProtocolInfo(DLNAProfiles profile) {
        super(MimeType.valueOf(profile.getContentFormat()));
        this.attributes.put(DLNAAttribute.Type.DLNA_ORG_PN, new DLNAProfileAttribute(profile));
        this.additionalInfo = this.getAttributesString();
    }
    @SuppressWarnings("PMD.LooseCoupling")
    public DLNAProtocolInfo(DLNAProfiles profile, EnumMap<DLNAAttribute.Type, DLNAAttribute<?>> attributes) {
        super(MimeType.valueOf(profile.getContentFormat()));
        this.attributes.putAll(attributes);
        this.attributes.put(DLNAAttribute.Type.DLNA_ORG_PN, new DLNAProfileAttribute(profile));
        this.additionalInfo = this.getAttributesString();
    }
    
    public DLNAProtocolInfo(Protocol protocol, String network, String contentFormat, String additionalInfo) {
        super(protocol, network, contentFormat, additionalInfo);
        parseAdditionalInfo();
    }

    @SuppressWarnings("PMD.LooseCoupling")
    public DLNAProtocolInfo(Protocol protocol, String network, String contentFormat, EnumMap<DLNAAttribute.Type, DLNAAttribute<?>> attributes) {
        super(protocol, network, contentFormat, "");
        this.attributes.putAll(attributes);
        this.additionalInfo = this.getAttributesString();
    }

    public DLNAProtocolInfo(ProtocolInfo template) {
        this(template.getProtocol(),
             template.getNetwork(),
             template.getContentFormat(),
             template.getAdditionalInfo()
        );
    }

    public boolean contains(DLNAAttribute.Type type) {
        return attributes.containsKey(type);
    }

    public DLNAAttribute<?> getAttribute(DLNAAttribute.Type type) {
        return attributes.get(type);
    }

    public Map<DLNAAttribute.Type, DLNAAttribute<?>> getAttributes() {
        return attributes;
    }

    protected String getAttributesString() {
        StringBuilder s = new StringBuilder();
        for (DLNAAttribute.Type type : DLNAAttribute.Type.values() ) {
            String value = attributes.containsKey(type)?attributes.get(type).getString():null;
            if (value!=null && !value.isEmpty())
                s.append((s.length() == 0) ? "" : ";").append(type.getAttributeName()).append("=").append(value);
        }
        return s.toString();
    }

    protected void parseAdditionalInfo() {
        if (additionalInfo != null) {
            String[] atts = additionalInfo.split(";");
            for (String att : atts) {
                String[] attNameValue = att.split("=");
                if (attNameValue.length == 2) {
                    DLNAAttribute.Type type =
                            DLNAAttribute.Type.valueOfAttributeName(attNameValue[0]);
                    if (type != null) {
                        DLNAAttribute<?> dlnaAttrinute =
                                DLNAAttribute.newInstance(type, attNameValue[1], this.getContentFormat());
                        attributes.put(type, dlnaAttrinute);
                    }
                }
            }
        }
    }

}
