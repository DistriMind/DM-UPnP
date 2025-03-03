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

package fr.distrimind.oss.upnp.common.support.model;

import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerFourBytes;

/**
 * @author Alessio Gaeta
 * @author Christian Bauer
 */
public class BrowseResult {

    protected String result;
    protected UnsignedIntegerFourBytes count;
    protected UnsignedIntegerFourBytes totalMatches;
    protected UnsignedIntegerFourBytes containerUpdateID;

    public BrowseResult(String result, UnsignedIntegerFourBytes count,
                        UnsignedIntegerFourBytes totalMatches,
                        UnsignedIntegerFourBytes containerUpdateID) {
        this.result = result;
        this.count = count;
        this.totalMatches = totalMatches;
        this.containerUpdateID = containerUpdateID;
    }

    public BrowseResult(String result, long count, long totalMatches) {
        this(result, count, totalMatches, 0);
    }

    public BrowseResult(String result, long count, long totalMatches, long updatedId) {
        this(
                result,
                new UnsignedIntegerFourBytes(count),
                new UnsignedIntegerFourBytes(totalMatches),
                new UnsignedIntegerFourBytes(updatedId)
        );
    }

    public String getResult() {
        return result;
    }

    public UnsignedIntegerFourBytes getCount() {
        return count;
    }

    public long getCountLong() {
        return count.getValue();
    }

    public UnsignedIntegerFourBytes getTotalMatches() {
        return totalMatches;
    }

    public long getTotalMatchesLong() {
        return totalMatches.getValue();
    }

    public UnsignedIntegerFourBytes getContainerUpdateID() {
        return containerUpdateID;
    }

    public long getContainerUpdateIDLong() {
        return containerUpdateID.getValue();
    }


}
