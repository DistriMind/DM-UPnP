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

/**
 * @author Christian Bauer
 */
public class MXHeader extends UpnpHeader<Integer> {

    // 3 second seems like a good default to spread search responses (UDA says 120?!? wtf)
    public static final Integer DEFAULT_VALUE = 3;

    /**
     * Defaults to 3 seconds.
     */
    public MXHeader() {
        setValue(DEFAULT_VALUE);
    }

    public MXHeader(Integer delayInSeconds) {
        setValue(delayInSeconds);
    }

    @Override
	public void setString(String s) throws InvalidHeaderException {
        int value;
        try {
            value = Integer.parseInt(s);
        } catch (Exception ex) {
            throw new InvalidHeaderException("Can't parse MX seconds integer from: " + s);
        }

        // UDA 1.0, section 1.2.3: "If the MX header specifies a value greater than 120, the device
        // should assume that it contained the value 120 or less."
        if (value < 0 || value > 120) {
            setValue(DEFAULT_VALUE);
        } else {
            setValue(value);
        }
    }

    @Override
	public String getString() {
        return getValue().toString();
    }
}
