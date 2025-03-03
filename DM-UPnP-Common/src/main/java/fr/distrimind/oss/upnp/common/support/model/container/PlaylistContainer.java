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

package fr.distrimind.oss.upnp.common.support.model.container;

import fr.distrimind.oss.upnp.common.support.model.DIDLObject;
import fr.distrimind.oss.upnp.common.support.model.Person;
import fr.distrimind.oss.upnp.common.support.model.PersonWithRole;
import fr.distrimind.oss.upnp.common.support.model.StorageMedium;

import java.util.List;

/**
 * @author Christian Bauer
 */
public class PlaylistContainer extends Container {

    public static final Class CLASS = new Class("object.container.playlistContainer");

    public PlaylistContainer() {
        setClazz(CLASS);
    }

    public PlaylistContainer(Container other) {
        super(other);
    }

    public PlaylistContainer(String id, Container parent, String title, String creator, Integer childCount) {
        this(id, parent.getId(), title, creator, childCount);
    }

    public PlaylistContainer(String id, String parentID, String title, String creator, Integer childCount) {
        super(id, parentID, title, creator, CLASS, childCount);
    }

    public PersonWithRole getFirstArtist() {
        return getFirstPropertyValue(DIDLObject.Property.UPNP.ARTIST.class);
    }

    public List<PersonWithRole> getArtists() {
        return getPropertyValues(DIDLObject.Property.UPNP.ARTIST.class);
    }

    public PlaylistContainer setArtists(List<PersonWithRole> artists) {
        removeProperties(DIDLObject.Property.UPNP.ARTIST.class);
        for (PersonWithRole artist : artists) {
            addProperty(new DIDLObject.Property.UPNP.ARTIST(artist));
        }
        return this;
    }

    public String getFirstGenre() {
        return getFirstPropertyValue(DIDLObject.Property.UPNP.GENRE.class);
    }

    public List<String> getGenres() {
        return getPropertyValues(DIDLObject.Property.UPNP.GENRE.class);
    }

    public PlaylistContainer setGenres(List<String> genres) {
        removeProperties(DIDLObject.Property.UPNP.GENRE.class);
        for (String genre : genres) {
            addProperty(new DIDLObject.Property.UPNP.GENRE(genre));
        }
        return this;
    }

    public String getDescription() {
        return getFirstPropertyValue(DIDLObject.Property.DC.DESCRIPTION.class);
    }

    public PlaylistContainer setDescription(String description) {
        replaceFirstProperty(new DIDLObject.Property.DC.DESCRIPTION(description));
        return this;
    }

    public String getLongDescription() {
        return getFirstPropertyValue(DIDLObject.Property.UPNP.LONG_DESCRIPTION.class);
    }

    public PlaylistContainer setLongDescription(String description) {
        replaceFirstProperty(new DIDLObject.Property.UPNP.LONG_DESCRIPTION(description));
        return this;
    }

    public Person getFirstProducer() {
        return getFirstPropertyValue(DIDLObject.Property.UPNP.PRODUCER.class);
    }

    public List<Person> getProducers() {
        return getPropertyValues(DIDLObject.Property.UPNP.PRODUCER.class);
    }

    public PlaylistContainer setProducers(List<Person> persons) {
        removeProperties(DIDLObject.Property.UPNP.PRODUCER.class);
        for (Person p : persons) {
            addProperty(new DIDLObject.Property.UPNP.PRODUCER(p));
        }
        return this;
    }

    public StorageMedium getStorageMedium() {
        return getFirstPropertyValue(DIDLObject.Property.UPNP.STORAGE_MEDIUM.class);
    }

    public PlaylistContainer setStorageMedium(StorageMedium storageMedium) {
        replaceFirstProperty(new DIDLObject.Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }

    public String getDate() {
        return getFirstPropertyValue(DIDLObject.Property.DC.DATE.class);
    }

    public PlaylistContainer setDate(String date) {
        replaceFirstProperty(new DIDLObject.Property.DC.DATE(date));
        return this;
    }

    public String getFirstRights() {
        return getFirstPropertyValue(DIDLObject.Property.DC.RIGHTS.class);
    }

    public List<String> getRights() {
        return getPropertyValues(DIDLObject.Property.DC.RIGHTS.class);
    }

    public PlaylistContainer setRights(List<String> rights) {
        removeProperties(DIDLObject.Property.DC.RIGHTS.class);
        for (String right : rights) {
            addProperty(new DIDLObject.Property.DC.RIGHTS(right));
        }
        return this;
    }

    public Person getFirstContributor() {
        return getFirstPropertyValue(DIDLObject.Property.DC.CONTRIBUTOR.class);
    }

    public List<Person> getContributors() {
        return getPropertyValues(DIDLObject.Property.DC.CONTRIBUTOR.class);
    }

    public PlaylistContainer setContributors(List<Person> contributors) {
        removeProperties(DIDLObject.Property.DC.CONTRIBUTOR.class);
        for (Person p : contributors) {
            addProperty(new DIDLObject.Property.DC.CONTRIBUTOR(p));
        }
        return this;
    }

    public String getLanguage() {
        return getFirstPropertyValue(DIDLObject.Property.DC.LANGUAGE.class);
    }

    public PlaylistContainer setLanguage(String language) {
        replaceFirstProperty(new DIDLObject.Property.DC.LANGUAGE(language));
        return this;
    }

}
