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

package fr.distrimind.oss.upnp.model.message.header;

import fr.distrimind.oss.upnp.model.UserConstants;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @author Christian Bauer
 */
public class TimeoutHeader extends UpnpHeader<Integer> {

    // It's probably OK to assume that "infinite" means 4000 years?
    public static final Integer INFINITE_VALUE = Integer.MAX_VALUE;

    public static final Pattern PATTERN = Pattern.compile("Second-(?:([0-9]+)|infinite)");

    /**
     * Defaults to {@link UserConstants#DEFAULT_SUBSCRIPTION_DURATION_SECONDS}.
     */
    public TimeoutHeader() {
        setValue(UserConstants.DEFAULT_SUBSCRIPTION_DURATION_SECONDS);
    }

    public TimeoutHeader(int timeoutSeconds) {
        setValue(timeoutSeconds);
    }

    public TimeoutHeader(Integer timeoutSeconds) {
        setValue(timeoutSeconds);
    }

    @Override
	public void setString(String s) throws InvalidHeaderException {

        Matcher matcher = PATTERN.matcher(s);
        if (!matcher.matches()) {
            throw new InvalidHeaderException("Can't parse timeout seconds integer from: " + s);
        }

        if (matcher.group(1) != null) {
            setValue(Integer.parseInt(matcher.group(1)));
        } else {
            setValue(INFINITE_VALUE);
        }

    }

    @Override
	public String getString() {
        return "Second-" + (getValue().equals(INFINITE_VALUE) ? "infinite" : getValue());
    }
}