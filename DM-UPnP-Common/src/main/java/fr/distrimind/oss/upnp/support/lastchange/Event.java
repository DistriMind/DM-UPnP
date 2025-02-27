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

package fr.distrimind.oss.upnp.support.lastchange;

import fr.distrimind.oss.upnp.model.types.UnsignedIntegerFourBytes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class Event {

    protected List<InstanceID> instanceIDs = new ArrayList<>();

    public Event() {
    }

    public Event(List<InstanceID> instanceIDs) {
        this.instanceIDs = instanceIDs;
    }

    public List<InstanceID> getInstanceIDs() {
        return instanceIDs;
    }

    public InstanceID getInstanceID(UnsignedIntegerFourBytes id) {
        for (InstanceID instanceID : instanceIDs) {
            if (instanceID.getId().equals(id)) return instanceID;
        }
        return null;
    }

    public void clear() {
        instanceIDs = new ArrayList<>();
    }

    public void setEventedValue(UnsignedIntegerFourBytes id, EventedValue<?> ev) {
        InstanceID instanceID = null;
        for (InstanceID i : getInstanceIDs()) {
            if (i.getId().equals(id)) {
                instanceID = i;
            }
        }
        if (instanceID == null) {
            instanceID = new InstanceID(id);
            getInstanceIDs().add(instanceID);
        }

		instanceID.getValues().removeIf(existingEv -> existingEv.getClass().equals(ev.getClass()));
        instanceID.getValues().add(ev);
    }

    @SuppressWarnings("unchecked")
	public <EV extends EventedValue<?>> EV getEventedValue(UnsignedIntegerFourBytes id, Class<EV> type) {
        for (InstanceID instanceID : getInstanceIDs()) {
            if (instanceID.getId().equals(id)) {
                for (EventedValue<?> eventedValue : instanceID.getValues()) {
                    if (eventedValue.getClass().equals(type))
                        return (EV) eventedValue;
                }
            }
        }
        return null;
    }

    public boolean hasChanges() {
        for (InstanceID instanceID : instanceIDs) {
            if (!instanceID.getValues().isEmpty()) return true;
        }
        return false;
    }
    
}
