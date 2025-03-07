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

import fr.distrimind.oss.upnp.common.support.model.Person;
import fr.distrimind.oss.upnp.common.support.model.PersonWithRole;
import fr.distrimind.oss.upnp.common.support.model.Res;
import fr.distrimind.oss.upnp.common.support.model.StorageMedium;
import fr.distrimind.oss.upnp.common.support.model.container.Container;

import java.util.List;

/**
 * @author Christian Bauer
 */
public class MusicVideoClip extends VideoItem {

    public static final Class CLASS = new Class("object.item.videoItem.musicVideoClip");

    public MusicVideoClip() {
        setClazz(CLASS);
    }

    public MusicVideoClip(Item other) {
        super(other);
    }

    public MusicVideoClip(String id, Container parent, String title, String creator, Res... resource) {
        this(id, parent.getId(), title, creator, resource);
    }

    public MusicVideoClip(String id, String parentID, String title, String creator, Res... resource) {
        super(id, parentID, title, creator, resource);
        setClazz(CLASS);
    }

    public PersonWithRole getFirstArtist() {
        return getFirstPropertyValue(Property.UPNP.ARTIST.class);
    }

    public List<PersonWithRole> getArtists() {
        return getPropertyValues(Property.UPNP.ARTIST.class);
    }

    public MusicVideoClip setArtists(List<PersonWithRole> artists) {
        removeProperties(Property.UPNP.ARTIST.class);
        for (PersonWithRole artist : artists) {
            addProperty(new Property.UPNP.ARTIST(artist));
        }
        return this;
    }

    public StorageMedium getStorageMedium() {
        return getFirstPropertyValue(Property.UPNP.STORAGE_MEDIUM.class);
    }

    public MusicVideoClip setStorageMedium(StorageMedium storageMedium) {
        replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }

    public String getAlbum() {
        return getFirstPropertyValue(Property.UPNP.ALBUM.class);
    }

    public MusicVideoClip setAlbum(String album) {
        replaceFirstProperty(new Property.UPNP.ALBUM(album));
        return this;
    }

    public String getFirstScheduledStartTime() {
        return getFirstPropertyValue(Property.UPNP.SCHEDULED_START_TIME.class);
    }

    public List<String> getScheduledStartTimes() {
        return getPropertyValues(Property.UPNP.SCHEDULED_START_TIME.class);
    }

    public MusicVideoClip setScheduledStartTimes(List<String> strings) {
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

    public MusicVideoClip setScheduledEndTimes(List<String> strings) {
        removeProperties(Property.UPNP.SCHEDULED_END_TIME.class);
        for (String s : strings) {
            addProperty(new Property.UPNP.SCHEDULED_END_TIME(s));
        }
        return this;
    }

    public Person getFirstContributor() {
        return getFirstPropertyValue(Property.DC.CONTRIBUTOR.class);
    }

    public List<Person> getContributors() {
        return getPropertyValues(Property.DC.CONTRIBUTOR.class);
    }

    public MusicVideoClip setContributors(List<Person> contributors) {
        removeProperties(Property.DC.CONTRIBUTOR.class);
        for (Person p : contributors) {
            addProperty(new Property.DC.CONTRIBUTOR(p));
        }
        return this;
    }

    public String getDate() {
        return getFirstPropertyValue(Property.DC.DATE.class);
    }

    public MusicVideoClip setDate(String date) {
        replaceFirstProperty(new Property.DC.DATE(date));
        return this;
    }


}
