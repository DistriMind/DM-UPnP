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

import java.util.regex.Pattern;
import fr.distrimind.oss.upnp.model.message.header.InvalidHeaderException;
import fr.distrimind.oss.upnp.support.model.dlna.types.ScmsFlagType;

/**
 * @author Mario Franco
 */
public class ScmsFlagHeader extends DLNAHeader<ScmsFlagType> {

    final static Pattern pattern = Pattern.compile("^[01]{2}$", Pattern.CASE_INSENSITIVE);
    
    public ScmsFlagHeader() {
    }

    @Override
    public void setString(String s) throws InvalidHeaderException {
        if (pattern.matcher(s).matches()) {
          setValue(new ScmsFlagType(s.charAt(0) == '0', s.charAt(1) == '0'));
          return;
        }
        throw new InvalidHeaderException("Invalid ScmsFlag header value: " + s);
    }

    @Override
    public String getString() {
        ScmsFlagType v = getValue();
        return (v.isCopyright()?"0":"1") + (v.isOriginal()?"0":"1");
    }
}
