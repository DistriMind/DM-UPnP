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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import fr.distrimind.oss.upnp.common.Log;
import fr.distrimind.oss.upnp.common.model.ModelUtil;

import fr.distrimind.oss.flexilogxml.log.DMLogger;

/**
 * Android network helpers.
 *
 * @author Michael Pujos
 */
public class NetworkUtils {

    final private static DMLogger log = Log.getLogger(NetworkUtils.class);

    static public NetworkInfo getConnectedNetworkInfo(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            return networkInfo;
        }

        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) return networkInfo;

        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) return networkInfo;

        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) return networkInfo;

        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) return networkInfo;

        log.info("Could not find any connected network...");

        return null;
    }

    static public boolean isEthernet(NetworkInfo networkInfo) {
        return isNetworkType(networkInfo, ConnectivityManager.TYPE_ETHERNET);
    }

    static public boolean isWifi(NetworkInfo networkInfo) {
        return isNetworkType(networkInfo, ConnectivityManager.TYPE_WIFI) || ModelUtil.ANDROID_EMULATOR;
    }

    static public boolean isMobile(NetworkInfo networkInfo) {
        return isNetworkType(networkInfo, ConnectivityManager.TYPE_MOBILE) || isNetworkType(networkInfo, ConnectivityManager.TYPE_WIMAX);
    }

    static public boolean isNetworkType(NetworkInfo networkInfo, int type) {
        return networkInfo != null && networkInfo.getType() == type;
    }

    static public boolean isSSDPAwareNetwork(NetworkInfo networkInfo) {
        return isWifi(networkInfo) || isEthernet(networkInfo);
    }

}