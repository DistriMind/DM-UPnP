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
package fr.distrimind.oss.upnp.common.support.model.dlna.types;

import fr.distrimind.oss.upnp.common.model.types.BytesRange;

/**
 *
 * @author Mario Franco
 */
public class TimeSeekRangeType {

    private final NormalPlayTimeRange normalPlayTimeRange;
    private BytesRange bytesRange;
    

    public TimeSeekRangeType(NormalPlayTimeRange nptRange) {
        this.normalPlayTimeRange = nptRange;
    }
    
    public TimeSeekRangeType(NormalPlayTimeRange nptRange, BytesRange byteRange) {
        this.normalPlayTimeRange = nptRange;
        this.bytesRange = byteRange;
    }

    /**
     * @return the normalPlayTimeRange
     */
    public NormalPlayTimeRange getNormalPlayTimeRange() {
        return normalPlayTimeRange;
    }

    /**
     * @return the bytesRange
     */
    public BytesRange getBytesRange() {
        return bytesRange;
    }

    /**
     * @param bytesRange the bytesRange to set
     */
    public void setBytesRange(BytesRange bytesRange) {
        this.bytesRange = bytesRange;
    }


}
