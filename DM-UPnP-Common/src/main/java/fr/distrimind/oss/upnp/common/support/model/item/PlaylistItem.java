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

import fr.distrimind.oss.upnp.common.support.model.PersonWithRole;
import fr.distrimind.oss.upnp.common.support.model.Res;
import fr.distrimind.oss.upnp.common.support.model.StorageMedium;
import fr.distrimind.oss.upnp.common.support.model.container.Container;

import java.util.Arrays;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class PlaylistItem extends Item {

    public static final Class CLASS = new Class("object.item.playlistItem");

    public PlaylistItem() {
        setClazz(CLASS);
    }

    public PlaylistItem(Item other) {
        super(other);
    }

    public PlaylistItem(String id, Container parent, String title, String creator, Res... resource) {
        this(id, parent.getId(), title, creator, resource);
    }

    public PlaylistItem(String id, String parentID, String title, String creator, Res... resource) {
        super(id, parentID, title, creator, CLASS);
        if (resource != null) {
            getResources().addAll(Arrays.asList(resource));
        }
    }

    public PersonWithRole getFirstArtist() {
        return getFirstPropertyValue(Property.UPNP.ARTIST.class);
    }

    public List<PersonWithRole> getArtists() {
        return getPropertyValues(Property.UPNP.ARTIST.class);
    }

    public PlaylistItem setArtists(List<PersonWithRole> artists) {
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

    public PlaylistItem setGenres(List<String> genres) {
        removeProperties(Property.UPNP.GENRE.class);
        for (String genre : genres) {
            addProperty(new Property.UPNP.GENRE(genre));
        }
        return this;
    }

    public String getDescription() {
        return getFirstPropertyValue(Property.DC.DESCRIPTION.class);
    }

    public PlaylistItem setDescription(String description) {
        replaceFirstProperty(new Property.DC.DESCRIPTION(description));
        return this;
    }

    public String getLongDescription() {
        return getFirstPropertyValue(Property.UPNP.LONG_DESCRIPTION.class);
    }

    public PlaylistItem setLongDescription(String description) {
        replaceFirstProperty(new Property.UPNP.LONG_DESCRIPTION(description));
        return this;
    }

    public String getLanguage() {
        return getFirstPropertyValue(Property.DC.LANGUAGE.class);
    }

    public PlaylistItem setLanguage(String language) {
        replaceFirstProperty(new Property.DC.LANGUAGE(language));
        return this;
    }

    public StorageMedium getStorageMedium() {
        return getFirstPropertyValue(Property.UPNP.STORAGE_MEDIUM.class);
    }

    public PlaylistItem setStorageMedium(StorageMedium storageMedium) {
        replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }

    public String getDate() {
        return getFirstPropertyValue(Property.DC.DATE.class);
    }

    public PlaylistItem setDate(String date) {
        replaceFirstProperty(new Property.DC.DATE(date));
        return this;
    }

}
