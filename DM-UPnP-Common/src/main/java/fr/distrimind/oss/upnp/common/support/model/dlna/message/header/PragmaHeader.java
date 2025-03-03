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

import java.util.ArrayList;
import java.util.List;
import fr.distrimind.oss.upnp.common.model.message.header.InvalidHeaderException;
import fr.distrimind.oss.upnp.common.model.types.PragmaType;

/**
 * DLNA Pragma tokens:
 *  - getIfoFileURI.dlna.org
 *  - ifoFileURI.dlna.org
 * 
 * @author Mario Franco
 */
public class PragmaHeader extends DLNAHeader<List<PragmaType>> {
    
    public PragmaHeader() {
        setValue(new ArrayList<>());
    }
    
    @Override
    public void setString(String _s) throws InvalidHeaderException {
        String s=_s;
        if (!s.isEmpty()) {
            if (s.endsWith(";")) {
                s = s.substring(0, s.length() - 1);
            }
            String[] list = s.split("\\s*;\\s*");
            List<PragmaType> value = new ArrayList<>();
            for (String pragma : list) {
                value.add(PragmaType.valueOf(pragma));
            }
            setValue(value);
            return;
        }
        throw new InvalidHeaderException("Invalid Pragma header value: " + s);
    }
    
    @Override
    public String getString() {
        List<PragmaType> v = getValue();
        StringBuilder r = new StringBuilder();
        for (PragmaType pragma : v) {
            r.append((r.length() == 0) ? "" : ",").append(pragma.getString());
        }
        return r.toString();
    }
}
