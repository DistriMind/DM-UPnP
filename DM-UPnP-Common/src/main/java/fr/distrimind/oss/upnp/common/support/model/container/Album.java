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
import fr.distrimind.oss.upnp.common.support.model.StorageMedium;

import java.net.URI;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class Album extends Container {

    public static final Class CLASS = new Class("object.container.album");

    public Album() {
        setClazz(CLASS);
    }

    public Album(Container other) {
        super(other);
    }

    public Album(String id, Container parent, String title, String creator, Integer childCount) {
        this(id, parent.getId(), title, creator, childCount);
    }

    public Album(String id, String parentID, String title, String creator, Integer childCount) {
        super(id, parentID, title, creator, CLASS, childCount);
    }

    public String getDescription() {
        return getFirstPropertyValue(DIDLObject.Property.DC.DESCRIPTION.class);
    }

    public Album setDescription(String description) {
        replaceFirstProperty(new DIDLObject.Property.DC.DESCRIPTION(description));
        return this;
    }

    public String getLongDescription() {
        return getFirstPropertyValue(DIDLObject.Property.UPNP.LONG_DESCRIPTION.class);
    }

    public Album setLongDescription(String description) {
        replaceFirstProperty(new DIDLObject.Property.UPNP.LONG_DESCRIPTION(description));
        return this;
    }

    public StorageMedium getStorageMedium() {
        return getFirstPropertyValue(DIDLObject.Property.UPNP.STORAGE_MEDIUM.class);
    }

    public Album setStorageMedium(StorageMedium storageMedium) {
        replaceFirstProperty(new DIDLObject.Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }

    public String getDate() {
        return getFirstPropertyValue(DIDLObject.Property.DC.DATE.class);
    }

    public Album setDate(String date) {
        replaceFirstProperty(new DIDLObject.Property.DC.DATE(date));
        return this;
    }

    public URI getFirstRelation() {
        return getFirstPropertyValue(DIDLObject.Property.DC.RELATION.class);
    }

    public List<URI> getRelations() {
		return getPropertyValues(Property.DC.RELATION.class);
    }

    public Album setRelations(List<URI> relations) {
        removeProperties(DIDLObject.Property.DC.RELATION.class);
        for (URI relation : relations) {
            addProperty(new DIDLObject.Property.DC.RELATION(relation));
        }
        return this;
    }

    public String getFirstRights() {
        return getFirstPropertyValue(DIDLObject.Property.DC.RIGHTS.class);
    }

    public List<String> getRights() {
		return getPropertyValues(Property.DC.RIGHTS.class);
    }

    public Album setRights(List<String> rights) {
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

    public Album setContributors(List<Person> contributors) {
        removeProperties(DIDLObject.Property.DC.CONTRIBUTOR.class);
        for (Person p : contributors) {
            addProperty(new DIDLObject.Property.DC.CONTRIBUTOR(p));
        }
        return this;
    }

    public Person getFirstPublisher() {
        return getFirstPropertyValue(DIDLObject.Property.DC.PUBLISHER.class);
    }

    public List<Person> getPublishers() {
        return getPropertyValues(DIDLObject.Property.DC.PUBLISHER.class);
    }

    public Album setPublishers(List<Person> publishers) {
        removeProperties(DIDLObject.Property.DC.PUBLISHER.class);
        for (Person publisher : publishers) {
            addProperty(new DIDLObject.Property.DC.PUBLISHER(publisher));
        }
        return this;
    }

}
