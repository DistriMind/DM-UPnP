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

package fr.distrimind.oss.upnp.common.xml;



import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Another namespace-URI-to-whatever (namespace, context, resolver, map) magic thingy.
 * <p>
 * Of course it's just a map, like so many others in the JAXP hell. But this time someone
 * really went all out on the API fugliness. The person who designed this probably got promoted
 * and is now designing the signaling system for the airport you are about to land on.
 * Feeling better?
 * </p>
 *
 * @author Christian Bauer
 */
public class CatalogResourceResolver implements LSResourceResolver {

	final private static DMLogger log = Log.getLogger(CatalogResourceResolver.class);

	private final Map<URI, URL> catalog;

	public CatalogResourceResolver(Map<URI, URL> catalog) {
		this.catalog = catalog;
	}

	@Override
	public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
		if (log.isTraceEnabled()) {
			log.trace("Trying to resolve system identifier URI in catalog: " + systemId);
		}
		URL systemURL;
		if ((systemURL = catalog.get(URI.create(systemId))) != null) {
			if (log.isTraceEnabled()) {
                log.trace("Loading catalog resource: " + systemURL);
			}
			try {
				Input i = new Input(systemURL.openStream());
				i.setBaseURI(baseURI);
				i.setSystemId(systemId);
				i.setPublicId(publicId);
				return i;
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		if (log.isInfoEnabled()) log.info(
				"System identifier not found in catalog, continuing with default resolution " +
						"(this most likely means remote HTTP request!): " + systemId
			);
		return null;
	}

	// WTF...
	private static final class Input implements LSInput {

		InputStream in;

		public Input(InputStream in) {
			this.in = in;
		}

		@Override
		public Reader getCharacterStream() {
			return null;
		}

		@Override
		public void setCharacterStream(Reader characterStream) {
		}

		@Override
		public InputStream getByteStream() {
			return in;
		}

		@Override
		public void setByteStream(InputStream byteStream) {
		}

		@Override
		public String getStringData() {
			return null;
		}

		@Override
		public void setStringData(String stringData) {
		}

		@Override
		public String getSystemId() {
			return null;
		}

		@Override
		public void setSystemId(String systemId) {
		}

		@Override
		public String getPublicId() {
			return null;
		}

		@Override
		public void setPublicId(String publicId) {
		}

		@Override
		public String getBaseURI() {
			return null;
		}

		@Override
		public void setBaseURI(String baseURI) {
		}

		@Override
		public String getEncoding() {
			return null;
		}

		@Override
		public void setEncoding(String encoding) {
		}

		@Override
		public boolean getCertifiedText() {
			return false;
		}

		@Override
		public void setCertifiedText(boolean certifiedText) {
		}
	}
}
