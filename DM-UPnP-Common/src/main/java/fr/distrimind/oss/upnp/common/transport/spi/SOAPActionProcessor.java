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

package fr.distrimind.oss.upnp.common.transport.spi;

import fr.distrimind.oss.upnp.common.model.message.StreamRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.StreamResponseMessage;
import fr.distrimind.oss.upnp.common.model.UnsupportedDataException;
import fr.distrimind.oss.upnp.common.model.action.ActionInvocation;
import fr.distrimind.oss.upnp.common.model.message.control.ActionRequestMessage;
import fr.distrimind.oss.upnp.common.model.message.control.ActionResponseMessage;
import fr.distrimind.oss.upnp.common.model.meta.Service;

/**
 * Converts UPnP SOAP messages from/to action invocations.
 * <p>
 * The UPnP protocol layer processes local and remote {@link ActionInvocation}
 * instances. The UPnP transport layer accepts and returns {@link StreamRequestMessage}s
 * and {@link StreamResponseMessage}s. This processor is an adapter between the
 * two layers, reading and writing SOAP content.
 * </p>
 *
 * @author Christian Bauer
 */
public interface SOAPActionProcessor {

    /**
     * Converts the given invocation input into SOAP XML content, setting on the given request message.
     *
     * @param requestMessage The request message on which the SOAP content is set.
     * @param actionInvocation The action invocation from which input argument values are read.
     * @throws UnsupportedDataException if a problem occurs
     */
    <S extends Service<?, ?, ?>> void writeBody(ActionRequestMessage requestMessage, ActionInvocation<S> actionInvocation) throws UnsupportedDataException;

    /**
     * Converts the given invocation output into SOAP XML content, setting on the given response message.
     *
     * @param responseMessage The response message on which the SOAP content is set.
     * @param actionInvocation The action invocation from which output argument values are read.
     * @throws UnsupportedDataException if a problem occurs
     */
    <S extends Service<?, ?, ?>> void writeBody(ActionResponseMessage responseMessage, ActionInvocation<S> actionInvocation) throws UnsupportedDataException;

    /**
     * Converts SOAP XML content of the request message and sets input argument values on the given invocation.
     *
     * @param requestMessage The request message from which SOAP content is read.
     * @param actionInvocation The action invocation on which input argument values are set.
     * @throws UnsupportedDataException if a problem occurs
     */
    <S extends Service<?, ?, ?>> void readBody(ActionRequestMessage requestMessage, ActionInvocation<S> actionInvocation) throws UnsupportedDataException;

    /**
     * Converts SOAP XML content of the response message and sets output argument values on the given invocation.
     *
     * @param responseMsg The response message from which SOAP content is read.
     * @param actionInvocation The action invocation on which output argument values are set.
     * @throws UnsupportedDataException if a problem occurs
     */
    <S extends Service<?, ?, ?>> void readBody(ActionResponseMessage responseMsg, ActionInvocation<S> actionInvocation) throws UnsupportedDataException;

}
