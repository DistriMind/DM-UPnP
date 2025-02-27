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

import fr.distrimind.oss.upnp.model.message.header.InvalidHeaderException;
import fr.distrimind.oss.upnp.model.types.BytesRange;
import fr.distrimind.oss.upnp.model.types.InvalidValueException;
import fr.distrimind.oss.upnp.support.model.dlna.types.NormalPlayTimeRange;
import fr.distrimind.oss.upnp.support.model.dlna.types.TimeSeekRangeType;

/**
 * @author Mario Franco
 */
public class TimeSeekRangeHeader extends DLNAHeader<TimeSeekRangeType> {

    public TimeSeekRangeHeader() {
    }

    public TimeSeekRangeHeader(TimeSeekRangeType timeSeekRange) {
        setValue(timeSeekRange);
    }
    @Override
    public void setString(String s) throws InvalidHeaderException {
        if (!s.isEmpty()) {
            String[] params = s.split(" ");
            if (params.length>0) {
                try {
                    TimeSeekRangeType t = new TimeSeekRangeType(NormalPlayTimeRange.valueOf(params[0]));
                    if (params.length > 1) {
                        t.setBytesRange(BytesRange.valueOf(params[1]));
                    }
                    setValue(t);
                    return;
                } catch (InvalidValueException invalidValueException) {
                    throw new InvalidHeaderException("Invalid TimeSeekRange header value: " + s + "; "+invalidValueException.getMessage());
                }
            }
        }
        throw new InvalidHeaderException("Invalid TimeSeekRange header value: " + s);
    }

    @Override
    public String getString() {
        TimeSeekRangeType t = getValue();
        String s = t.getNormalPlayTimeRange().getString();
        if (t.getBytesRange()!=null) s+= " "+t.getBytesRange().getString(true);
        return s;
    }
    
}
