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
package fr.distrimind.oss.upnp.common.support.model.dlna;

import java.util.EnumSet;
import java.util.Locale;

/**
 * @author Mario Franco
 */
@SuppressWarnings("PMD.LooseCoupling")
public class DLNAOperationsAttribute extends DLNAAttribute<EnumSet<DLNAOperations>> {

    public DLNAOperationsAttribute() {
        setValue(EnumSet.of(DLNAOperations.NONE));
    }

    public DLNAOperationsAttribute(DLNAOperations... op) {
        if (op != null && op.length > 0) {
            DLNAOperations first = op[0];
            if (op.length > 1) {
                System.arraycopy(op, 1, op, 0, op.length - 1);
                setValue(EnumSet.of(first, op));
            } else {
                setValue(EnumSet.of(first));
            }
        }
    }

    @Override
	public void setString(String s, String cf) throws InvalidDLNAProtocolAttributeException {
        EnumSet<DLNAOperations> value = EnumSet.noneOf(DLNAOperations.class);
        try {
            int parseInt = Integer.parseInt(s, 16);
            for (DLNAOperations op : DLNAOperations.values()) {
                int code = op.getCode() & parseInt;
                if (op != DLNAOperations.NONE && (op.getCode() == code)) {
                    value.add(op);
                }
            }
        } catch (NumberFormatException ignored) {
        }

        if (value.isEmpty())
            throw new InvalidDLNAProtocolAttributeException("Can't parse DLNA operations integer from: " + s);

        setValue(value);
    }

    @Override
	public String getString() {
        int code = DLNAOperations.NONE.getCode();
        for (DLNAOperations op : getValue()) {
            code |= op.getCode();
        }
        return String.format(Locale.ROOT, "%02x", code);
    }
}
