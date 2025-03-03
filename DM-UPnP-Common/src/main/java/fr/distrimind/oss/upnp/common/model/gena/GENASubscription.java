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

package fr.distrimind.oss.upnp.common.model.gena;

import java.util.LinkedHashMap;
import java.util.Map;

import fr.distrimind.oss.upnp.common.model.UserConstants;
import fr.distrimind.oss.upnp.common.model.state.StateVariableValue;
import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerFourBytes;
import fr.distrimind.oss.upnp.common.registry.Registry;
import fr.distrimind.oss.upnp.common.model.meta.Service;

/**
 * An established subscription, with identifer, expiration duration, sequence handling, and state variable values.
 * <p>
 * For every subscription, no matter if it's an incoming subscription to a local service,
 * or a local control point subscribing to a remote servce, an instance is maintained by
 * the {@link Registry}.
 * </p>
 *
 * @author Christian Bauer
 */
public abstract class GENASubscription<S extends Service<?, ?, ?>> {

    protected S service;
    protected String subscriptionId;
    protected int requestedDurationSeconds = UserConstants.DEFAULT_SUBSCRIPTION_DURATION_SECONDS;
    protected int actualDurationSeconds;
    protected UnsignedIntegerFourBytes currentSequence;
    protected Map<String, StateVariableValue<S>> currentValues = new LinkedHashMap<>();

    /**
     * Defaults to {@link UserConstants#DEFAULT_SUBSCRIPTION_DURATION_SECONDS}.
     */
    protected GENASubscription(S  service) {
        this.service = service;
    }

    public GENASubscription(S service, int requestedDurationSeconds) {
        this(service);
        this.requestedDurationSeconds = requestedDurationSeconds;
    }

    synchronized public S getService() {
        return service;
    }

    synchronized public String getSubscriptionId() {
        return subscriptionId;
    }

    synchronized public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    synchronized public int getRequestedDurationSeconds() {
        return requestedDurationSeconds;
    }

    synchronized public int getActualDurationSeconds() {
        return actualDurationSeconds;
    }

    synchronized public void setActualSubscriptionDurationSeconds(int seconds) {
        this.actualDurationSeconds = seconds;
    }

    synchronized public UnsignedIntegerFourBytes getCurrentSequence() {
        return currentSequence;
    }

    synchronized public Map<String, StateVariableValue<S>> getCurrentValues() {
        return currentValues;
    }

    public abstract void established();
    public abstract void eventReceived();

    @Override
    public String toString() {
        return "(GENASubscription, SID: " + getSubscriptionId() + ", SEQUENCE: " + getCurrentSequence() + ")";
    }
}
