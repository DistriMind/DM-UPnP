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
package fr.distrimind.oss.upnp.support.model.dlna;

import fr.distrimind.oss.upnp.model.types.InvalidValueException;
import fr.distrimind.oss.upnp.support.avtransport.lastchange.AVTransportVariable.TransportPlaySpeed;

/**
 * @author Mario Franco
 */
public class DLNAPlaySpeedAttribute extends DLNAAttribute<TransportPlaySpeed[]> {
    
    public DLNAPlaySpeedAttribute() {
        setValue(new TransportPlaySpeed[] {});
    }

    public DLNAPlaySpeedAttribute(TransportPlaySpeed[] speeds) {
        setValue(speeds);
    }
    
    public DLNAPlaySpeedAttribute(String[] speeds) {
        TransportPlaySpeed[] sp = new TransportPlaySpeed[speeds.length]; 
        try {
            for (int i = 0; i < speeds.length; i++) {
                sp[i] = new TransportPlaySpeed(speeds[i]);
            }
        } catch (InvalidValueException invalidValueException) {
            throw new InvalidDLNAProtocolAttributeException("Can't parse DLNA play speeds.");
        }
        setValue(sp);
    }

    @Override
	public void setString(String s, String cf) throws InvalidDLNAProtocolAttributeException {
        TransportPlaySpeed[] value = null;
        if (s != null && !s.isEmpty()) {
            String[] speeds = s.split(",");
            try {
                value = new TransportPlaySpeed[speeds.length]; 
                for (int i = 0; i < speeds.length; i++) {
                    value[i] = new TransportPlaySpeed(speeds[i]);
                }
            } catch (InvalidValueException invalidValueException) {
                value = null;
            }
        }
        if (value == null) {
            throw new InvalidDLNAProtocolAttributeException("Can't parse DLNA play speeds from: " + s);
        }
        setValue(value);
    }

    @Override
	public String getString() {
        StringBuilder s = new StringBuilder();
        for (TransportPlaySpeed speed : getValue()) {
            if ("1".equals(speed.getValue()))
                continue;
            s.append(s.length() == 0 ? "" : ",").append(speed);
        }
        return s.toString();
    }
    
}
