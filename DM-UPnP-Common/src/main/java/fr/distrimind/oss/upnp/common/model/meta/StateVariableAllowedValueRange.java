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

package fr.distrimind.oss.upnp.common.model.meta;

import fr.distrimind.oss.upnp.common.model.Validatable;
import fr.distrimind.oss.upnp.common.model.ValidationError;

import java.util.List;
import java.util.ArrayList;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Integrity rule for a state variable, restricting its values to a range with steps.
 * <p></p>
 * TODO: The question here is: Are they crazy enough to use this for !integer (e.g. floating point) numbers?
 *
 * @author Christian Bauer
 */
public class StateVariableAllowedValueRange implements Validatable {

    final private static DMLogger log = Log.getLogger(StateVariableAllowedValueRange.class);

    final private long minimum;
    final private long maximum;
    final private long step;

    public StateVariableAllowedValueRange(long minimum, long maximum) {
        this(minimum, maximum, 1);
    }

    public StateVariableAllowedValueRange(long minimum, long maximum, long step) {
        if (minimum > maximum) {
            if (log.isWarnEnabled()) log.warn("UPnP specification violation, allowed value range minimum '" + minimum
                                + "' is greater than maximum '" + maximum + "', switching values.");
            this.minimum = maximum;
            this.maximum = minimum;
        } else {
            this.minimum = minimum;
            this.maximum = maximum;
        }
        this.step = step;
    }

    public long getMinimum() {
        return minimum;
    }

    public long getMaximum() {
        return maximum;
    }

    public long getStep() {
        return step;
    }

    public boolean isInRange(long value) {
        return value >= getMinimum() && value <= getMaximum() && (value % step) == 0;
    }

    @Override
	public List<ValidationError> validate() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Range Min: " + getMinimum() + " Max: " + getMaximum() + " Step: " + getStep();
    }
}