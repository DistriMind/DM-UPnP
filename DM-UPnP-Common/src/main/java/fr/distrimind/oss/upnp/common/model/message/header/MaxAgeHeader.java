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

package fr.distrimind.oss.upnp.common.model.message.header;

import fr.distrimind.oss.upnp.common.model.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Locale;

/**
 * @author Christian Bauer
 */
public class MaxAgeHeader extends UpnpHeader<Integer> {

    // UDA 1.1 expands on the rules in UDA 1.0 and clearly says that anything but max-age has to be ignored
    public static final Pattern MAX_AGE_REGEX = Pattern.compile(".*max-age\\s*=\\s*([0-9]+).*");

    public MaxAgeHeader(Integer maxAge) {
        setValue(maxAge);
    }

    public MaxAgeHeader() {
        setValue(Constants.MIN_ADVERTISEMENT_AGE_SECONDS);
    }

    @Override
	public void setString(String s) throws InvalidHeaderException {

        Matcher matcher = MAX_AGE_REGEX.matcher(s.toLowerCase(Locale.ROOT));
        if (!matcher.matches()){
            throw new InvalidHeaderException("Invalid cache-control value, can't parse max-age seconds: " + s);
        }

        Integer maxAge = Integer.parseInt(matcher.group(1));
        setValue(maxAge);
    }

    @Override
	public String getString() {
        return "max-age="+getValue().toString();
    }
}
