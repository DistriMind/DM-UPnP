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
package fr.distrimind.oss.upnp.common.model.types;

/**
 *
 * @author Mario Franco
 */
public class PragmaType {

    private final String token;
    private boolean quote;
    private final String value;

    public PragmaType(String token, String value, boolean quote) {
        this.token = token;
        this.value = value;
        this.quote = quote;
    }
    
    public PragmaType(String token, String value) {
        this.token = token;
        this.value = value;
    }

    public PragmaType(String value) {
        this.token = null;
        this.value = value;
    }

    
    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
    
    /**
     * 
     * @return String format of Bytes Range for response message header 
     */
    public String getString() {
        String s ="";
        if (token!=null)
            s += token + "=";

        s += quote? "\""+value+"\"" : value;
        return s;
    }

    public static PragmaType valueOf(String s) throws InvalidValueException {
        if (!s.isEmpty()) {
            String token=null;
            String value;
            boolean quote = false;
            String[] params = s.split("=");
            if (params.length > 1) {
                token = params[0];
                value = params[1];
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    quote = true;
                    value = value.substring(1, value.length()-1);
                }
            }
            else {
                value = s;
            }
            return new PragmaType(token, value, quote);
        }
        throw new InvalidValueException("Can't parse Bytes Range: " + s);
    }

}
