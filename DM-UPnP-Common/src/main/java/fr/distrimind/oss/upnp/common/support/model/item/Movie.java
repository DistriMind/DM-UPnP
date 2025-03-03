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

import fr.distrimind.oss.upnp.common.support.model.Res;
import fr.distrimind.oss.upnp.common.support.model.StorageMedium;
import fr.distrimind.oss.upnp.common.support.model.container.Container;

import java.util.List;

/**
 * @author Christian Bauer
 */
public class Movie extends VideoItem {

    public static final Class CLASS = new Class("object.item.videoItem.movie");

    public Movie() {
        setClazz(CLASS);
    }

    public Movie(Item other) {
        super(other);
    }

    public Movie(String id, Container parent, String title, String creator, Res... resource) {
        this(id, parent.getId(), title, creator, resource);
    }

    public Movie(String id, String parentID, String title, String creator, Res... resource) {
        super(id, parentID, title, creator, resource);
        setClazz(CLASS);
    }

    public StorageMedium getStorageMedium() {
        return getFirstPropertyValue(Property.UPNP.STORAGE_MEDIUM.class);
    }

    public Movie setStorageMedium(StorageMedium storageMedium) {
        replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }

    public Integer getDVDRegionCode() {
        return getFirstPropertyValue(Property.UPNP.DVD_REGION_CODE.class);
    }

    public Movie setDVDRegionCode(Integer DVDRegionCode) {
        replaceFirstProperty(new Property.UPNP.DVD_REGION_CODE(DVDRegionCode));
        return this;
    }

    public String getChannelName() {
        return getFirstPropertyValue(Property.UPNP.CHANNEL_NAME.class);
    }

    public Movie setChannelName(String channelName) {
        replaceFirstProperty(new Property.UPNP.CHANNEL_NAME(channelName));
        return this;
    }

    public String getFirstScheduledStartTime() {
        return getFirstPropertyValue(Property.UPNP.SCHEDULED_START_TIME.class);
    }

    public List<String> getScheduledStartTimes() {
        return getPropertyValues(Property.UPNP.SCHEDULED_START_TIME.class);
    }

    public Movie setScheduledStartTimes(String[] strings) {
        removeProperties(Property.UPNP.SCHEDULED_START_TIME.class);
        for (String s : strings) {
            addProperty(new Property.UPNP.SCHEDULED_START_TIME(s));
        }
        return this;
    }

    public String getFirstScheduledEndTime() {
        return getFirstPropertyValue(Property.UPNP.SCHEDULED_END_TIME.class);
    }

    public List<String> getScheduledEndTimes() {
        return getPropertyValues(Property.UPNP.SCHEDULED_END_TIME.class);
    }

    public Movie setScheduledEndTimes(String[] strings) {
        removeProperties(Property.UPNP.SCHEDULED_END_TIME.class);
        for (String s : strings) {
            addProperty(new Property.UPNP.SCHEDULED_END_TIME(s));
        }
        return this;
    }

}
