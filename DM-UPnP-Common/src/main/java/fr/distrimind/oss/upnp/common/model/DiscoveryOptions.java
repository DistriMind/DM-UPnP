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

package fr.distrimind.oss.upnp.common.model;

import fr.distrimind.oss.upnp.common.registry.Registry;

/**
 * Options for discovery processing by the {@link Registry}.
 *
 * @author Christian Bauer
 */
public class DiscoveryOptions {

    protected boolean advertised;
    protected boolean byeByeBeforeFirstAlive;

    /**
     * @param advertised If <code>false</code>, no alive notifications will be announced for
     *                   this device, and it will not appear in search responses.
     */
    public DiscoveryOptions(boolean advertised) {
        this.advertised = advertised;
    }

    /**
     *
     * @param advertised If <code>false</code>, no alive notifications will be announced for
     *                   this device, and it will not appear in search responses.
     * @param byeByeBeforeFirstAlive If <code>true</code>, a byebye NOTIFY message will be sent before the
     *                               first alive NOTIFY message.
     */
    public DiscoveryOptions(boolean advertised, boolean byeByeBeforeFirstAlive) {
        this.advertised = advertised;
        this.byeByeBeforeFirstAlive = byeByeBeforeFirstAlive;
    }

    /**
     * @return true for regular advertisement with alive
     *         messages and in search responses.
     */
    public boolean isAdvertised() {
        return advertised;
    }

    /**
     * @return true if a byebye NOTIFY message will be sent before the
     *         first alive NOTIFY message.
     */
    public boolean isByeByeBeforeFirstAlive() {
        return byeByeBeforeFirstAlive;
    }

    // Performance optimization on Android
    private static final String simpleName = DiscoveryOptions.class.getSimpleName();
	@Override
    public String toString() {
        return "(" + simpleName + ")" + " advertised: " + isAdvertised() + " byebyeBeforeFirstAlive: " + isByeByeBeforeFirstAlive();
    }
}
