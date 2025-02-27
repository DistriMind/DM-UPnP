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

package fr.distrimind.oss.upnp.support.model.item;

import fr.distrimind.oss.upnp.support.model.Person;
import fr.distrimind.oss.upnp.support.model.Res;
import fr.distrimind.oss.upnp.support.model.StorageMedium;
import fr.distrimind.oss.upnp.support.model.container.Container;

import java.util.List;

/**
 * @author Christian Bauer
 */
public class AudioBook extends AudioItem {

    public static final Class CLASS = new Class("object.item.audioItem.audioBook");

    public AudioBook() {
        setClazz(CLASS);
    }

    public AudioBook(Item other) {
        super(other);
    }

    public AudioBook(String id, Container parent, String title, String creator, Res... resource) {
        this(id, parent.getId(), title, creator, null, null, null, resource);
    }

    public AudioBook(String id, Container parent, String title, String creator, String producer, String contributor, String date, Res... resource) {
        this(id, parent.getId(), title, creator, new Person(producer), new Person(contributor), date, resource);
    }

    public AudioBook(String id, String parentID, String title, String creator, Person producer, Person contributor, String date, Res... resource) {
        super(id, parentID, title, creator, resource);
        setClazz(CLASS);
        if (producer != null)
            addProperty(new Property.UPNP.PRODUCER(producer));
        if (contributor != null)
            addProperty(new Property.DC.CONTRIBUTOR(contributor));
        if (date != null)
            setDate(date);
    }
    
    public StorageMedium getStorageMedium() {
        return getFirstPropertyValue(Property.UPNP.STORAGE_MEDIUM.class);
    }

    public AudioBook setStorageMedium(StorageMedium storageMedium) {
        replaceFirstProperty(new Property.UPNP.STORAGE_MEDIUM(storageMedium));
        return this;
    }

    public Person getFirstProducer() {
        return getFirstPropertyValue(Property.UPNP.PRODUCER.class);
    }

    public List<Person> getProducers() {
        return getPropertyValues(Property.UPNP.PRODUCER.class);
    }

    public AudioBook setProducers(Person[] persons) {
        removeProperties(Property.UPNP.PRODUCER.class);
        for (Person p : persons) {
            addProperty(new Property.UPNP.PRODUCER(p));
        }
        return this;
    }

    public Person getFirstContributor() {
        return getFirstPropertyValue(Property.DC.CONTRIBUTOR.class);
    }

    public List<Person> getContributors() {
        return getPropertyValues(Property.DC.CONTRIBUTOR.class);
    }

    public AudioBook setContributors(Person[] contributors) {
        removeProperties(Property.DC.CONTRIBUTOR.class);
        for (Person p : contributors) {
            addProperty(new Property.DC.CONTRIBUTOR(p));
        }
        return this;
    }

    public String getDate() {
        return getFirstPropertyValue(Property.DC.DATE.class);
    }

    public AudioBook setDate(String date) {
        replaceFirstProperty(new Property.DC.DATE(date));
        return this;
    }

}
