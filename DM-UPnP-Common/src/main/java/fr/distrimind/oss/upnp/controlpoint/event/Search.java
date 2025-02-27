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

package fr.distrimind.oss.upnp.controlpoint.event;

import fr.distrimind.oss.upnp.model.message.header.MXHeader;
import fr.distrimind.oss.upnp.model.message.header.STAllHeader;
import fr.distrimind.oss.upnp.model.message.header.UpnpHeader;

/**
 * @author Christian Bauer
 */
public class Search {

    protected UpnpHeader<?> searchType = new STAllHeader();
    protected int mxSeconds = MXHeader.DEFAULT_VALUE;

    public Search() {
    }

    public Search(UpnpHeader<?> searchType) {
        this.searchType = searchType;
    }

    public Search(UpnpHeader<?> searchType, int mxSeconds) {
        this.searchType = searchType;
        this.mxSeconds = mxSeconds;
    }

    public Search(int mxSeconds) {
        this.mxSeconds = mxSeconds;
    }

    public UpnpHeader<?> getSearchType() {
        return searchType;
    }

    public int getMxSeconds() {
        return mxSeconds;
    }
}
