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

import fr.distrimind.oss.upnp.support.model.DIDLObject;

/**
 * @author Christian Bauer
 */
public class StorageFolder extends Container {

    public static final Class CLASS = new Class("object.container.storageFolder");

    public StorageFolder() {
        setClazz(CLASS);
    }

    public StorageFolder(Container other) {
        super(other);
    }

    public StorageFolder(String id, Container parent, String title, String creator, Integer childCount,
                         Long storageUsed) {
        this(id, parent.getId(), title, creator, childCount, storageUsed);
    }

    public StorageFolder(String id, String parentID, String title, String creator, Integer childCount,
                         Long storageUsed) {
        super(id, parentID, title, creator, CLASS, childCount);
        if (storageUsed!= null)
            setStorageUsed(storageUsed);
    }

    public Long getStorageUsed() {
        return getFirstPropertyValue(DIDLObject.Property.UPNP.STORAGE_USED.class);
    }

    public StorageFolder setStorageUsed(Long l) {
        replaceFirstProperty(new DIDLObject.Property.UPNP.STORAGE_USED(l));
        return this;
    }


}
