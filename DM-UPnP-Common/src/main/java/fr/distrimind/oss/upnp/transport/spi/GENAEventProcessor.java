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

package fr.distrimind.oss.upnp.transport.spi;

import fr.distrimind.oss.upnp.model.state.StateVariableValue;
import fr.distrimind.oss.upnp.model.UnsupportedDataException;
import fr.distrimind.oss.upnp.model.message.gena.IncomingEventRequestMessage;
import fr.distrimind.oss.upnp.model.message.gena.OutgoingEventRequestMessage;

/**
 * Reads and writes GENA XML content.
 *
 * @author Christian Bauer
 */
public interface GENAEventProcessor {

    /**
     * Transforms a collection of {@link StateVariableValue}s into an XML message body.
     *
     * @param requestMessage The message to transform.
     *
     */
	void writeBody(OutgoingEventRequestMessage requestMessage) throws UnsupportedDataException;

    /**
     * Transforms an XML message body and adds to a collection of {@link StateVariableValue}s.
     *
     * @param requestMessage The message to transform.
     *
     */
	void readBody(IncomingEventRequestMessage requestMessage) throws UnsupportedDataException;

}