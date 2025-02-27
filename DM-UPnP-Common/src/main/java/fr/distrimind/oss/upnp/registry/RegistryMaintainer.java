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

package fr.distrimind.oss.upnp.registry;

import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.Log;

/**
 * Runs periodically and calls {@link RegistryImpl#maintain()}.
 *
 * @author Christian Bauer
 */
public class RegistryMaintainer implements Runnable {

    final private static DMLogger log = Log.getLogger(RegistryMaintainer.class);

    final private RegistryImpl registry;
    final private int sleepIntervalMillis;

    private volatile boolean stopped = false;

    public RegistryMaintainer(RegistryImpl registry, int sleepIntervalMillis) {
        this.registry = registry;
        this.sleepIntervalMillis = sleepIntervalMillis;
    }

    public void stop() {
        if (log.isDebugEnabled())
            log.debug("Setting stopped status on thread");
        stopped = true;
    }

    @Override
	public void run() {
        stopped = false;
        if (log.isDebugEnabled())
            log.debug("Running registry maintenance loop every milliseconds: " + sleepIntervalMillis);
        while (!stopped) {

            try {
                registry.maintain();
                Thread.sleep(sleepIntervalMillis);
            } catch (InterruptedException ex) {
                stopped = true;
            }

        }
        log.debug("Stopped status on thread received, ending maintenance loop");
    }

}