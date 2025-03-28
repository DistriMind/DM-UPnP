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

import java.util.Date;

/**
 * @author Christian Bauer
 */
public class ExpirationDetails {

    public static final int UNLIMITED_AGE = 0;

    private int maxAgeSeconds = UNLIMITED_AGE;
    private long lastRefreshTimestampSeconds = getCurrentTimestampSeconds();

    public ExpirationDetails() {
    }

    public ExpirationDetails(int maxAgeSeconds) {
        this.maxAgeSeconds = maxAgeSeconds;
    }

    public int getMaxAgeSeconds() {
        return maxAgeSeconds;
    }

    public long getLastRefreshTimestampSeconds() {
        return lastRefreshTimestampSeconds;
    }

    public void setLastRefreshTimestampSeconds(long lastRefreshTimestampSeconds) {
        this.lastRefreshTimestampSeconds = lastRefreshTimestampSeconds;
    }

    public void stampLastRefresh() {
        setLastRefreshTimestampSeconds(getCurrentTimestampSeconds());
    }

    public boolean hasExpired() {
        return hasExpired(false);
    }

    /**
     * @param halfTime If <code>true</code> then half maximum age is used to determine expiration.
     * @return <code>true</code> if the maximum age has been reached.
     */
    public boolean hasExpired(boolean halfTime) {
        // Note: Uses direct field access for performance reasons on Android
        return maxAgeSeconds != UNLIMITED_AGE &&
                (lastRefreshTimestampSeconds + (maxAgeSeconds/(halfTime ? 2 : 1))) < getCurrentTimestampSeconds();
    }

    public long getSecondsUntilExpiration() {
        // Note: Uses direct field access for performance reasons on Android
        return maxAgeSeconds == UNLIMITED_AGE
                ? Integer.MAX_VALUE
                : (lastRefreshTimestampSeconds + maxAgeSeconds) - getCurrentTimestampSeconds();
    }

    protected long getCurrentTimestampSeconds() {
        return new Date().getTime()/1000;
    }

    // Performance optimization on Android
    private static final String simpleName = ExpirationDetails.class.getSimpleName();
	@Override
    public String toString() {
        return "(" + simpleName + ")" + " MAX AGE: " + maxAgeSeconds;
    }
}
