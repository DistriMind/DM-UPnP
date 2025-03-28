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
import fr.distrimind.oss.upnp.common.support.model.container.Container;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class VideoItem extends Item {

    public static final Class CLASS = new Class("object.item.videoItem");

    public VideoItem() {
        setClazz(CLASS);
    }

    public VideoItem(Item other) {
        super(other);
    }

    public VideoItem(String id, Container parent, String title, String creator, Res... resource) {
        this(id, parent.getId(), title, creator, resource);
    }

    public VideoItem(String id, String parentID, String title, String creator, Res... resource) {
        super(id, parentID, title, creator, CLASS);
        if (resource != null) {
            getResources().addAll(Arrays.asList(resource));
        }
    }

    public String getFirstGenre() {
        return getFirstPropertyValue(Property.UPNP.GENRE.class);
    }

    public List<String> getGenres() {
        return getPropertyValues(Property.UPNP.GENRE.class);
    }

    public VideoItem setGenres(String[] genres) {
        removeProperties(Property.UPNP.GENRE.class);
        for (String genre : genres) {
            addProperty(new Property.UPNP.GENRE(genre));
        }
        return this;
    }

    public String getDescription() {
        return getFirstPropertyValue(Property.DC.DESCRIPTION.class);
    }

    public VideoItem setDescription(String description) {
        replaceFirstProperty(new Property.DC.DESCRIPTION(description));
        return this;
    }

    public String getLongDescription() {
        return getFirstPropertyValue(Property.UPNP.LONG_DESCRIPTION.class);
    }

    public VideoItem setLongDescription(String description) {
        replaceFirstProperty(new Property.UPNP.LONG_DESCRIPTION(description));
        return this;
    }

    public Person getFirstProducer() {
        return getFirstPropertyValue(Property.UPNP.PRODUCER.class);
    }

    public List<Person> getProducers() {
        return getPropertyValues(Property.UPNP.PRODUCER.class);
    }

    public VideoItem setProducers(List<Person> persons) {
        removeProperties(Property.UPNP.PRODUCER.class);
        for (Person p : persons) {
            addProperty(new Property.UPNP.PRODUCER(p));
        }
        return this;
    }

    public String getRating() {
        return getFirstPropertyValue(Property.UPNP.RATING.class);
    }

    public VideoItem setRating(String rating) {
        replaceFirstProperty(new Property.UPNP.RATING(rating));
        return this;
    }

    public PersonWithRole getFirstActor() {
        return getFirstPropertyValue(Property.UPNP.ACTOR.class);
    }

    public List<PersonWithRole> getActors() {
        return getPropertyValues(Property.UPNP.ACTOR.class);
    }

    public VideoItem setActors(PersonWithRole[] persons) {
        removeProperties(Property.UPNP.ACTOR.class);
        for (PersonWithRole p : persons) {
            addProperty(new Property.UPNP.ACTOR(p));
        }
        return this;
    }

    public Person getFirstDirector() {
        return getFirstPropertyValue(Property.UPNP.DIRECTOR.class);
    }

    public List<Person> getDirectors() {
        return getPropertyValues(Property.UPNP.DIRECTOR.class);
    }

    public VideoItem setDirectors(List<Person> persons) {
        removeProperties(Property.UPNP.DIRECTOR.class);
        for (Person p : persons) {
            addProperty(new Property.UPNP.DIRECTOR(p));
        }
        return this;
    }

    public Person getFirstPublisher() {
        return getFirstPropertyValue(Property.DC.PUBLISHER.class);
    }

    public List<Person> getPublishers() {
        return getPropertyValues(Property.DC.PUBLISHER.class);
    }

    public VideoItem setPublishers(List<Person> publishers) {
        removeProperties(Property.DC.PUBLISHER.class);
        for (Person publisher : publishers) {
            addProperty(new Property.DC.PUBLISHER(publisher));
        }
        return this;
    }

    public String getLanguage() {
        return getFirstPropertyValue(Property.DC.LANGUAGE.class);
    }

    public VideoItem setLanguage(String language) {
        replaceFirstProperty(new Property.DC.LANGUAGE(language));
        return this;
    }

    public URI getFirstRelation() {
        return getFirstPropertyValue(Property.DC.RELATION.class);
    }

    public List<URI> getRelations() {
        return getPropertyValues(Property.DC.RELATION.class);
    }

    public VideoItem setRelations(List<URI> relations) {
        removeProperties(Property.DC.RELATION.class);
        for (URI relation : relations) {
            addProperty(new Property.DC.RELATION(relation));
        }
        return this;
    }

}
