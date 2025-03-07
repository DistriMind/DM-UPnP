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

import fr.distrimind.oss.upnp.common.support.model.StorageMedium;

/**
 * @author Christian Bauer
 */
public class StorageVolume extends Container {
    
    public static final Class CLASS = new Class("object.container.storageVolume");

    public StorageVolume() {
        setClazz(CLASS);
    }

    public StorageVolume(Container other) {
        super(other);
    }

    public StorageVolume(String id, Container parent, String title, String creator, Integer childCount,
                         Long storageTotal, Long storageUsed, Long storageFree, StorageMedium storageMedium) {
        this(id, parent.getId(), title, creator, childCount, storageTotal, storageUsed, storageFree, storageMedium);
    }

    public StorageVolume(String id, String parentID, String title, String creator, Integer childCount,
                         Long storageTotal, Long storageUsed, Long storageFree, StorageMedium storageMedium) {
        super(id, parentID, title, creator, CLASS, childCount);
        if (storageTotal != null)
            setStorageTotal(storageTotal);
        if (storageUsed!= null)
            setStorageUsed(storageUsed);
        if (storageFree != null)
            setStorageFree(storageFree);
        if (storageMedium != null)
            setStorageMedium(storageMedium);
    }

    public Long getStorageTotal() {
        return getFirstPropertyValue(Property.UPNP.STORAGE_TOTAL.class);
    }

    public StorageVolume setStorageTotal(Long l) {
        replaceFirstProperty(new Property.UPNP.STORAGE_TOTAL(l));
        return this;
    }

    public Long getStorageUsed() {
        return getFirstPropertyValue(Property.UPNP.STORAGE_USED.class);
    }

    public StorageVolume setStorageUsed(Long l) {
        replaceFirstProperty(new Property.UPNP.STORAGE_USED(l));
        return this;
    }

    public Long getStorageFree() {
        return getFirstPropertyValue(Property.UPNP.STORAGE_FREE.class);
    }

    public StorageVolume setStorageFree(Long l) {
        replaceFirstProperty(new Property.UPNP.STORAGE_FREE(l));
        return this;
    }

    public StorageMedium getStorageMedium() {
        return getFirstPropertyValue(Property.UPNP.STORAGE_MEDIUM.class);
    }

    public StorageVolume setStorageMedium(StorageMedium storageMedium) {
        replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }
    
}
