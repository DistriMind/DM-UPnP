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

package fr.distrimind.oss.upnp.common.http;

/**
 * @author Christian Bauer
 * @author Michael Pujos
 */
public class RequestInfo {

	public static boolean isPS3Request(String userAgent, String avClientInfo) {
		return ((userAgent != null && userAgent.contains("PLAYSTATION 3")) ||
				(avClientInfo != null && avClientInfo.contains("PLAYSTATION 3")));
	}

	public static boolean isAndroidBubbleUPnPRequest(String userAgent) {
		return (userAgent != null && userAgent.contains("BubbleUPnP"));
	}

	public static boolean isJRiverRequest(String userAgent) {
		return userAgent != null && (userAgent.contains("J-River") || userAgent.contains("J. River"));
	}

	public static boolean isWMPRequest(String userAgent) {
		return userAgent != null && userAgent.contains("Windows-Media-Player") && !isJRiverRequest(userAgent);
	}

	public static boolean isXbox360Request(String userAgent, String server) {
		return (userAgent != null && (userAgent.contains("Xbox") || userAgent.contains("Xenon"))) ||
				(server != null && server.contains("Xbox"));
	}


}