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
public class MusicTrack extends AudioItem {

    public static final Class CLASS = new Class("object.item.audioItem.musicTrack");

    public MusicTrack() {
        setClazz(CLASS);
    }

    public MusicTrack(Item other) {
        super(other);
    }

    public MusicTrack(String id, Container parent, String title, String creator, String album, String artist, Res... resource) {
        this(id, parent.getId(), title, creator, album, artist, resource);
    }

    public MusicTrack(String id, Container parent, String title, String creator, String album, PersonWithRole artist, Res... resource) {
        this(id, parent.getId(), title, creator, album, artist, resource);
    }

    public MusicTrack(String id, String parentID, String title, String creator, String album, String artist, Res... resource) {
        this(id, parentID, title, creator, album, artist == null ? null : new PersonWithRole(artist), resource);
    }

    public MusicTrack(String id, String parentID, String title, String creator, String album, PersonWithRole artist, Res... resource) {
        super(id, parentID, title, creator, resource);
        setClazz(CLASS);
        if (album != null)
            setAlbum(album);
        if (artist != null)
            addProperty(new Property.UPNP.ARTIST(artist));
    }

    public PersonWithRole getFirstArtist() {
        return getFirstPropertyValue(Property.UPNP.ARTIST.class);
    }

    public List<PersonWithRole> getArtists() {
        return getPropertyValues(Property.UPNP.ARTIST.class);
    }

    public MusicTrack setArtists(PersonWithRole[] artists) {
        removeProperties(Property.UPNP.ARTIST.class);
        for (PersonWithRole artist : artists) {
            addProperty(new Property.UPNP.ARTIST(artist));
        }
        return this;
    }

    public String getAlbum() {
        return getFirstPropertyValue(Property.UPNP.ALBUM.class);
    }

    public MusicTrack setAlbum(String album) {
        replaceFirstProperty(new Property.UPNP.ALBUM(album));
        return this;
    }

    public Integer getOriginalTrackNumber() {
        return getFirstPropertyValue(Property.UPNP.ORIGINAL_TRACK_NUMBER.class);
    }

    public MusicTrack setOriginalTrackNumber(Integer number) {
        replaceFirstProperty(new Property.UPNP.ORIGINAL_TRACK_NUMBER(number));
        return this;
    }

    public String getFirstPlaylist() {
        return getFirstPropertyValue(Property.UPNP.PLAYLIST.class);
    }

    public List<String> getPlaylists() {
        return getPropertyValues(Property.UPNP.PLAYLIST.class);
    }

    public MusicTrack setPlaylists(String[] playlists) {
        removeProperties(Property.UPNP.PLAYLIST.class);
        for (String s : playlists) {
            addProperty(new Property.UPNP.PLAYLIST(s));
        }
        return this;
    }

    public StorageMedium getStorageMedium() {
        return getFirstPropertyValue(Property.UPNP.STORAGE_MEDIUM.class);
    }

    public MusicTrack setStorageMedium(StorageMedium storageMedium) {
        replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }

    public Person getFirstContributor() {
        return getFirstPropertyValue(Property.DC.CONTRIBUTOR.class);
    }

    public List<Person> getContributors() {
        return getPropertyValues(Property.DC.CONTRIBUTOR.class);
    }

    public MusicTrack setContributors(Person[] contributors) {
        removeProperties(Property.DC.CONTRIBUTOR.class);
        for (Person p : contributors) {
            addProperty(new Property.DC.CONTRIBUTOR(p));
        }
        return this;
    }

    public String getDate() {
        return getFirstPropertyValue(Property.DC.DATE.class);
    }

    public MusicTrack setDate(String date) {
        replaceFirstProperty(new Property.DC.DATE(date));
        return this;
    }

}
