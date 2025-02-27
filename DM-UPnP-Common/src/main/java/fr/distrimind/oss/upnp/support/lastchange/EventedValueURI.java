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

import fr.distrimind.oss.upnp.model.types.Datatype;
import fr.distrimind.oss.upnp.model.types.InvalidValueException;
import fr.distrimind.oss.upnp.util.Exceptions;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.Log;

/**
 * @author Christian Bauer
 */
public class EventedValueURI extends EventedValue<URI> {

    final private static DMLogger log = Log.getLogger(EventedValueURI.class);

    public EventedValueURI(URI value) {
        super(value);
    }

    public EventedValueURI(Collection<Map.Entry<String, String>> attributes) {
        super(attributes);
    }
    
    @Override
    protected URI valueOf(String s) throws InvalidValueException {
        try {
            // These URIs are really defined as 'string' datatype in AVTransport1.0.pdf, but we can try
            // to parse whatever devices give us, like the Roku which sends "unknown url".
            return super.valueOf(s);
        } catch (InvalidValueException ex) {
            if (log.isInfoEnabled()) log.info("Ignoring invalid URI in evented value '" + s +"': ", Exceptions.unwrap(ex));
            return null;
        }
    }

    @SuppressWarnings("unchecked")
	@Override
    protected Datatype<URI> getDatatype() {
        return (Datatype<URI>)Datatype.Builtin.URI.getDatatype();
    }
}
