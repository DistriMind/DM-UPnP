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

package fr.distrimind.oss.upnp.support.model.container;

import fr.distrimind.oss.upnp.support.model.Person;
import fr.distrimind.oss.upnp.support.model.PersonWithRole;
import fr.distrimind.oss.upnp.support.model.item.Item;
import fr.distrimind.oss.upnp.support.model.item.MusicTrack;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class MusicAlbum extends Album {

    public static final Class CLASS = new Class("object.container.album.musicAlbum");

    public MusicAlbum() {
        setClazz(CLASS);
    }

    public MusicAlbum(Container other) {
        super(other);
    }

    public MusicAlbum(String id, Container parent, String title, String creator, Integer childCount) {
        this(id, parent.getId(), title, creator, childCount, new ArrayList<>());
    }

    public MusicAlbum(String id, Container parent, String title, String creator, Integer childCount, List<MusicTrack> musicTracks) {
        this(id, parent.getId(), title, creator, childCount, musicTracks);
    }

    public MusicAlbum(String id, String parentID, String title, String creator, Integer childCount) {
        this(id, parentID, title, creator, childCount, new ArrayList<>());
    }

    public MusicAlbum(String id, String parentID, String title, String creator, Integer childCount, List<MusicTrack> musicTracks) {
        super(id, parentID, title, creator, childCount);
        setClazz(CLASS);
        addMusicTracks(musicTracks);
    }

    public PersonWithRole getFirstArtist() {
        return getFirstPropertyValue(Property.UPNP.ARTIST.class);
    }

    public List<PersonWithRole> getArtists() {
        return getPropertyValues(Property.UPNP.ARTIST.class);
    }

    public MusicAlbum setArtists(List<PersonWithRole> artists) {
        removeProperties(Property.UPNP.ARTIST.class);
        for (PersonWithRole artist : artists) {
            addProperty(new Property.UPNP.ARTIST(artist));
        }
        return this;
    }

    public String getFirstGenre() {
        return getFirstPropertyValue(Property.UPNP.GENRE.class);
    }

    public List<String> getGenres() {
        return getPropertyValues(Property.UPNP.GENRE.class);
    }

    public MusicAlbum setGenres(List<String> genres) {
        removeProperties(Property.UPNP.GENRE.class);
        for (String genre : genres) {
            addProperty(new Property.UPNP.GENRE(genre));
        }
        return this;
    }

    public Person getFirstProducer() {
        return getFirstPropertyValue(Property.UPNP.PRODUCER.class);
    }

    public List<Person> getProducers() {
        return getPropertyValues(Property.UPNP.PRODUCER.class);
    }

    public MusicAlbum setProducers(List<Person> persons) {
        removeProperties(Property.UPNP.PRODUCER.class);
        for (Person p : persons) {
            addProperty(new Property.UPNP.PRODUCER(p));
        }
        return this;
    }

    public URI getFirstAlbumArtURI() {
        return getFirstPropertyValue(Property.UPNP.ALBUM_ART_URI.class);
    }

    public List<URI> getAlbumArtURIs() {
        return getPropertyValues(Property.UPNP.ALBUM_ART_URI.class);
    }

    public MusicAlbum setAlbumArtURIs(List<URI> uris) {
        removeProperties(Property.UPNP.ALBUM_ART_URI.class);
        for (URI uri : uris) {
            addProperty(new Property.UPNP.ALBUM_ART_URI(uri));
        }
        return this;
    }

    public String getToc() {
        return getFirstPropertyValue(Property.UPNP.TOC.class);
    }

    public MusicAlbum setToc(String toc) {
        replaceFirstProperty(new Property.UPNP.TOC(toc));
        return this;
    }

    public List<MusicTrack> getMusicTracks() {
        List<MusicTrack> list = new ArrayList<>();
        for (Item item : getItems()) {
            if (item instanceof MusicTrack) list.add((MusicTrack)item);
        }
        return list;
    }


    public void addMusicTracks(List<MusicTrack> musicTracks) {
        if (musicTracks != null) {
            for (MusicTrack musicTrack : musicTracks) {
                musicTrack.setAlbum(getTitle());
                addItem(musicTrack);
            }
        }
    }

}
