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

package fr.distrimind.oss.upnp.common.support.model.item;

import fr.distrimind.oss.upnp.common.support.model.DIDLObject;
import fr.distrimind.oss.upnp.common.support.model.Res;

/**
 * @author Christian Bauer
 */
public class AudioBroadcast extends AudioItem {

    public static final Class CLASS = new Class("object.item.audioItem.audioBroadcast");

    public AudioBroadcast() {
        setClazz(CLASS);
    }

    public AudioBroadcast(Item other) {
        super(other);
    }

    public AudioBroadcast(String id, String parentID, String title, String creator, Res... resource) {
        super(id, parentID, title, creator, resource);
        setClazz(CLASS);
    }

    public String getRegion() {
        return getFirstPropertyValue(DIDLObject.Property.UPNP.REGION.class);
    }

    public AudioBroadcast setRegion(String region) {
        replaceFirstProperty(new DIDLObject.Property.UPNP.REGION(region));
        return this;
    }

    public String getRadioCallSign() {
        return getFirstPropertyValue(DIDLObject.Property.UPNP.RADIO_CALL_SIGN.class);
    }

    public AudioBroadcast setRadioCallSign(String radioCallSign) {
        replaceFirstProperty(new DIDLObject.Property.UPNP.RADIO_CALL_SIGN(radioCallSign));
        return this;
    }

    public String getRadioStationID() {
        return getFirstPropertyValue(DIDLObject.Property.UPNP.RADIO_STATION_ID.class);
    }

    public AudioBroadcast setRadioStationID(String radioStationID) {
        replaceFirstProperty(new DIDLObject.Property.UPNP.RADIO_STATION_ID(radioStationID));
        return this;
    }

    public String getRadioBand() {
        return getFirstPropertyValue(DIDLObject.Property.UPNP.RADIO_BAND.class);
    }

    public AudioBroadcast setRadioBand(String radioBand) {
        replaceFirstProperty(new DIDLObject.Property.UPNP.RADIO_BAND(radioBand));
        return this;
    }

    public Integer getChannelNr() {
        return getFirstPropertyValue(DIDLObject.Property.UPNP.CHANNEL_NR.class);
    }

    public AudioBroadcast setChannelNr(Integer channelNr) {
        replaceFirstProperty(new DIDLObject.Property.UPNP.CHANNEL_NR(channelNr));
        return this;
    }
}
