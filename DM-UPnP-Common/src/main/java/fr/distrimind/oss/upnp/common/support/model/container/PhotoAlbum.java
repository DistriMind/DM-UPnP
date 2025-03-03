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

import fr.distrimind.oss.upnp.common.support.model.item.Item;
import fr.distrimind.oss.upnp.common.support.model.item.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class PhotoAlbum extends Album {

    public static final Class CLASS = new Class("object.container.album.photoAlbum");

    public PhotoAlbum() {
        setClazz(CLASS);
    }

    public PhotoAlbum(Container other) {
        super(other);
    }

    public PhotoAlbum(String id, Container parent, String title, String creator, Integer childCount) {
        this(id, parent.getId(), title, creator, childCount, new ArrayList<>());
    }

    public PhotoAlbum(String id, Container parent, String title, String creator, Integer childCount, List<Photo> photos) {
        this(id, parent.getId(), title, creator, childCount, photos);
    }

    public PhotoAlbum(String id, String parentID, String title, String creator, Integer childCount) {
        this(id, parentID, title, creator, childCount, new ArrayList<>());
    }

    public PhotoAlbum(String id, String parentID, String title, String creator, Integer childCount, List<Photo> photos) {
        super(id, parentID, title, creator, childCount);
        setClazz(CLASS);
        addPhotos(photos);
    }

    public List<Photo> getPhotos() {
        List<Photo> list = new ArrayList<>();
        for (Item item : getItems()) {
            if (item instanceof Photo) list.add((Photo)item);
        }
        return list;
    }


    public void addPhotos(List<Photo> photos) {
        if (photos != null) {
            for (Photo photo : photos) {
                photo.setAlbum(getTitle());
                addItem(photo);
            }
        }
    }

}
