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
package fr.distrimind.oss.upnp.common.support.model.dlna.message.header;

import fr.distrimind.oss.upnp.common.model.message.header.InvalidHeaderException;
import fr.distrimind.oss.upnp.common.support.model.dlna.types.NormalPlayTime;

/**
 * @author Mario Franco
 */
public class RealTimeInfoHeader extends DLNAHeader<NormalPlayTime> {

    public static final String PREFIX = "DLNA.ORG_TLAG=";
    
    public RealTimeInfoHeader() {
    }

    @Override
    public void setString(String _s) throws InvalidHeaderException {
        String s=_s;
        if (s.startsWith(PREFIX)) {
            try {
                s = s.substring(PREFIX.length());
                setValue("*".equals(s) ? null : NormalPlayTime.valueOf(s) );
                return;
            } catch (Exception ignored) {}
        }
        throw new InvalidHeaderException("Invalid RealTimeInfo header value: " + s);
    }

    @Override
    public String getString() {
        NormalPlayTime v = getValue();
        if (v == null)
            return PREFIX+"*";
        return PREFIX+v.getString();
    }
}
