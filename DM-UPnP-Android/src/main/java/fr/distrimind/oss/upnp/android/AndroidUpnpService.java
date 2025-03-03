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

package fr.distrimind.oss.upnp.android;

import fr.distrimind.oss.upnp.common.UpnpService;
import fr.distrimind.oss.upnp.common.UpnpServiceConfiguration;
import fr.distrimind.oss.upnp.common.controlpoint.ControlPoint;
import fr.distrimind.oss.upnp.common.registry.Registry;

/**
 * Interface of the Android UPnP application service component.
 * <p>
 * Usage example in an Android activity:
 * </p>
 * <pre>{@code
 *AndroidUpnpService upnpService;
 *
 *ServiceConnection serviceConnection = new ServiceConnection() {
 *     public void onServiceConnected(ComponentName className, IBinder service) {
 *         upnpService = (AndroidUpnpService) service;
 *     }
 *     public void onServiceDisconnected(ComponentName className) {
 *         upnpService = null;
 *     }
 *};
 *
 *public void onCreate(...) {
 * ...
 *     getApplicationContext().bindService(
 *         new Intent(this, AndroidUpnpServiceImpl.class),
 *         serviceConnection,
 *         Context.BIND_AUTO_CREATE
 *     );
 *}}</pre>
 *<p>
 * The default implementation requires permissions in <code>AndroidManifest.xml</code>:
 * </p>
 * <pre>{@code
 *<uses-permission android:name="android.permission.INTERNET"/>
 *<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
 *<uses-permission android:name="android.permission.CHANGE_WIFI_MULTICAST_STATE"/>
 *<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
 *<uses-permission android:name="android.permission.WAKE_LOCK"/>
 *}</pre>
 * <p>
 * You also have to add the application service component:
 * </p>
 * <pre>{@code
 *<application ...>
 *  ...
 *  <service android:name="fr.distrimind.oss.upnp.android.AndroidUpnpServiceImpl"/>
 *</application>
 * }</pre>
 *
 * @author Christian Bauer
 */
// DOC:CLASS
public interface AndroidUpnpService {

    /**
     * @return The actual main instance and interface of the UPnP service.
     */
	UpnpService get();

    /**
     * @return The configuration of the UPnP service.
     */
	UpnpServiceConfiguration getConfiguration();

    /**
     * @return The registry of the UPnP service.
     */
	Registry getRegistry();

    /**
     * @return The client API of the UPnP service.
     */
	ControlPoint getControlPoint();

}
// DOC:CLASS
