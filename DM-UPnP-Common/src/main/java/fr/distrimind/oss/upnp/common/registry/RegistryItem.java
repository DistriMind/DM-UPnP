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

package fr.distrimind.oss.upnp.common.registry;

import fr.distrimind.oss.upnp.common.model.ExpirationDetails;

/**
 * Internal class, required by {@link RegistryImpl}.
 *
 * @author Christian Bauer
 */
class RegistryItem<K, I> {

    private final K key;
    private I item;
    private ExpirationDetails expirationDetails = new ExpirationDetails();

    RegistryItem(K key) {
        if (key==null)
            throw new NullPointerException();
        this.key = key;
    }

    RegistryItem(K key, I item, int maxAgeSeconds) {
        if (key==null)
            throw new NullPointerException();
        this.key = key;
        this.item = item;
        this.expirationDetails = new ExpirationDetails(maxAgeSeconds);
    }

    public K getKey() {
        return key;
    }

    public I getItem() {
        return item;
    }

    public ExpirationDetails getExpirationDetails() {
        return expirationDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegistryItem<?, ?> that = (RegistryItem<?, ?>) o;

        return key.equals(that.key);
    }
    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "("+getClass().getSimpleName()+") " + getExpirationDetails() + " KEY: " + getKey() + " ITEM: " + getItem();
    }
}
