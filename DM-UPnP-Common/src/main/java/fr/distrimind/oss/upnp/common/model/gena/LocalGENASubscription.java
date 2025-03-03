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

import fr.distrimind.oss.upnp.common.model.ServiceManager;
import fr.distrimind.oss.upnp.common.model.UserConstants;
import fr.distrimind.oss.upnp.common.model.message.header.SubscriptionIdHeader;
import fr.distrimind.oss.upnp.common.model.meta.LocalService;
import fr.distrimind.oss.upnp.common.model.meta.StateVariable;
import fr.distrimind.oss.upnp.common.model.state.StateVariableValue;
import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerFourBytes;
import fr.distrimind.oss.upnp.common.util.Exceptions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * An incoming subscription to a local service.
 * <p>
 * Uses the {@link ServiceManager} to read the initial state of
 * the {@link LocalService} on instantation. Typically, the
 * {@link #registerOnService()} method is called next, and from this point forward all
 * {@link ServiceManager#EVENTED_STATE_VARIABLES} property change
 * events are detected by this subscription. After moderation of state variable values
 * (frequency and range of changes), the {@link #eventReceived()} method is called.
 * Delivery of the event message to the subscriber is not part of this class, but the
 * implementor of {@link #eventReceived()}.
 * </p>
 *
 * @author Christian Bauer
 */
public abstract class LocalGENASubscription<T> extends GENASubscription<LocalService<T>> implements PropertyChangeListener {

    final private static DMLogger log = Log.getLogger(LocalGENASubscription.class);

    final List<URL> callbackURLs;

    // Moderation history
    final Map<String, Long> lastSentTimestamp = new HashMap<>();
    final Map<String, Long> lastSentNumericValue = new HashMap<>();

    protected LocalGENASubscription(LocalService<T> service, List<URL> callbackURLs) throws Exception {
        super(service);
        this.callbackURLs = callbackURLs;
    }

    public LocalGENASubscription(LocalService<T> service,
                                 Integer requestedDurationSeconds, List<URL> callbackURLs) throws Exception {
        super(service);

        setSubscriptionDuration(requestedDurationSeconds);

        log.debug("Reading initial state of local service at subscription time");
        long currentTime = new Date().getTime();
        this.currentValues.clear();

        Collection<StateVariableValue<LocalService<T>>> values = getService().getManager().getCurrentState();
		if (log.isTraceEnabled()) {
			log.trace("Got evented state variable values: " + values.size());
		}

		for (StateVariableValue<LocalService<T>> value : values) {
            this.currentValues.put(value.getStateVariable().getName(), value);

            if (log.isTraceEnabled()) {
				log.trace("Read state variable value '" + value.getStateVariable().getName() + "': " + value);
            }

            // Preserve "last sent" state for future moderation
            lastSentTimestamp.put(value.getStateVariable().getName(), currentTime);
            if (value.getStateVariable().isModeratedNumericType()) {
                lastSentNumericValue.put(value.getStateVariable().getName(), Long.valueOf(value.toString()));
            }
        }

        this.subscriptionId = SubscriptionIdHeader.PREFIX + UUID.randomUUID();
        this.currentSequence = new UnsignedIntegerFourBytes(0);
        this.callbackURLs = callbackURLs;
    }

    synchronized public List<URL> getCallbackURLs() {
        return callbackURLs;
    }

    /**
     * Adds a property change listener on the {@link ServiceManager}.
     */
    synchronized public void registerOnService() {
        getService().getManager().getPropertyChangeSupport().addPropertyChangeListener(this);
    }

    synchronized public void establish() {
        established();
    }

    /**
     * Removes a property change listener on the {@link ServiceManager}.
     */
    synchronized public void end(CancelReason reason) {
        try {
            getService().getManager().getPropertyChangeSupport().removePropertyChangeListener(this);
        } catch (Exception ex) {
			if (log.isWarnEnabled()) log.warn("Removal of local service property change listener failed: ", Exceptions.unwrap(ex));
        }
        ended(reason);
    }

    /**
     * Moderates {@link ServiceManager#EVENTED_STATE_VARIABLES} events and state variable
     * values, calls {@link #eventReceived()}.
     */
    @Override
	@SuppressWarnings("unchecked")
	synchronized public void propertyChange(PropertyChangeEvent e) {
        if (!ServiceManager.EVENTED_STATE_VARIABLES.equals(e.getPropertyName())) return;

		if (log.isDebugEnabled()) {
            log.debug("Eventing triggered, getting state for subscription: " + getSubscriptionId());
		}

		long currentTime = new Date().getTime();

        @SuppressWarnings("unchecked") Collection<StateVariableValue<?>> newValues = (Collection<StateVariableValue<?>>) e.getNewValue();
        Set<String> excludedVariables = moderateStateVariables(currentTime, newValues);

        currentValues.clear();
        for (StateVariableValue<?> newValue : newValues) {
            String name = newValue.getStateVariable().getName();
            if (!excludedVariables.contains(name)) {
				if (log.isDebugEnabled()) {
					log.debug("Adding state variable value to current values of event: " + newValue.getStateVariable() + " = " + newValue);
				}
				currentValues.put(newValue.getStateVariable().getName(), (StateVariableValue<LocalService<T>>) newValue);

                // Preserve "last sent" state for future moderation
                lastSentTimestamp.put(name, currentTime);
                if (newValue.getStateVariable().isModeratedNumericType()) {
                    lastSentNumericValue.put(name, Long.valueOf(newValue.toString()));
                }
            }
        }

        if (!currentValues.isEmpty()) {

			if (log.isDebugEnabled()) {
				log.debug("Propagating new state variable values to subscription: " + this);
			}
			// TODO: I'm not happy with this design, this dispatches to a separate thread which _then_
            // is supposed to lock and read the values off this instance. That obviously doesn't work
            // so it's currently a hack in SendingEvent.java
            eventReceived();
        } else {
            log.debug("No state variable values for event (all moderated out?), not triggering event");
        }
    }

    /**
     * Checks whether a state variable is moderated, and if this change is within the maximum rate and range limits.
     *
     * @param currentTime The current unix time.
     * @param values The state variable values to moderate.
     * @return A collection of state variable values that although they might have changed, are excluded from the event.
     */
    synchronized protected Set<String> moderateStateVariables(long currentTime, Collection<StateVariableValue<?>> values) {

        Set<String> excludedVariables = new HashSet<>();

        // Moderate event variables that have a maximum rate or minimum delta
        for (StateVariableValue<?> stateVariableValue : values) {

            StateVariable<?> stateVariable = stateVariableValue.getStateVariable();
            String stateVariableName = stateVariableValue.getStateVariable().getName();

            if (stateVariable.getEventDetails().getEventMaximumRateMilliseconds() == 0 &&
                    stateVariable.getEventDetails().getEventMinimumDelta() == 0) {

				if (log.isTraceEnabled()) {
					log.trace("Variable is not moderated: " + stateVariable);
				}
				continue;
            }

            // That should actually never happen, because we always "send" it as the initial state/event
            if (!lastSentTimestamp.containsKey(stateVariableName)) {
				if (log.isTraceEnabled()) {
					log.trace("Variable is moderated but was never sent before: " + stateVariable);
				}
				continue;
            }

            if (stateVariable.getEventDetails().getEventMaximumRateMilliseconds() > 0) {
                long timestampLastSent = lastSentTimestamp.get(stateVariableName);
                long timestampNextSend = timestampLastSent + (stateVariable.getEventDetails().getEventMaximumRateMilliseconds());
                if (currentTime <= timestampNextSend) {

					if (log.isTraceEnabled()) {
						log.trace("Excluding state variable with maximum rate: " + stateVariable);
					}
					excludedVariables.add(stateVariableName);
                    continue;
                }
            }

            if (stateVariable.isModeratedNumericType() && lastSentNumericValue.get(stateVariableName) != null) {

                long oldValue = lastSentNumericValue.get(stateVariableName);
                long newValue = Long.parseLong(stateVariableValue.toString());
                long minDelta = stateVariable.getEventDetails().getEventMinimumDelta();

                if (newValue > oldValue && newValue - oldValue < minDelta) {
					if (log.isTraceEnabled()) {
						log.trace("Excluding state variable with minimum delta: " + stateVariable);
					}
					excludedVariables.add(stateVariableName);
                    continue;
                }

                if (newValue < oldValue && oldValue - newValue < minDelta) {
					if (log.isTraceEnabled()) {
						log.trace("Excluding state variable with minimum delta: " + stateVariable);
					}
					excludedVariables.add(stateVariableName);
                }
            }

        }
        return excludedVariables;
    }

    synchronized public void incrementSequence() {
        this.currentSequence.increment(true);
    }

    /**
     * @param requestedDurationSeconds If <code>null</code> defaults to
     *                                 {@link UserConstants#DEFAULT_SUBSCRIPTION_DURATION_SECONDS}
     */
    synchronized public void setSubscriptionDuration(Integer requestedDurationSeconds) {
        this.requestedDurationSeconds =
                requestedDurationSeconds == null
                        ? UserConstants.DEFAULT_SUBSCRIPTION_DURATION_SECONDS
                        : requestedDurationSeconds;

        setActualSubscriptionDurationSeconds(this.requestedDurationSeconds);
    }

    public abstract void ended(CancelReason reason);

}
