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



import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;
import fr.distrimind.oss.upnp.common.model.ModelUtil;

/**
 * @author Christian Bauer
 */
public class SAXParser {

	final private static DMLogger log = Log.getLogger(SAXParser.class);

	public static final URI XML_SCHEMA_NAMESPACE =
			URI.create("https://www.w3.org/2001/xml.xsd");
	public static final URL XML_SCHEMA_RESOURCE =
			Thread.currentThread().getContextClassLoader().getResource("org/seamless/schemas/xml.xsd");

	final private XMLReader xr;

	public SAXParser() {
		this(null);
	}

	public SAXParser(DefaultHandler handler) {
		this.xr = create();
		if (handler != null)
			xr.setContentHandler(handler);
	}

	public void setContentHandler(ContentHandler handler) {
		xr.setContentHandler(handler);
	}

	protected XMLReader create() {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();

			// Configure factory to prevent XXE attacks
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			if (!ModelUtil.ANDROID_RUNTIME) {
				factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
				factory.setXIncludeAware(false);
			}

			factory.setNamespaceAware(true);

			if (getSchemaSources() != null) {
				factory.setSchema(createSchema(getSchemaSources()));
			}

			XMLReader xmlReader = factory.newSAXParser().getXMLReader();
			xmlReader.setErrorHandler(getErrorHandler());
			return xmlReader;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	protected Schema createSchema(Source[] schemaSources) {
		try {
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schemaFactory.setResourceResolver(new CatalogResourceResolver(
					new HashMap<>() {
						private static final long serialVersionUID = 1L;
						{
						put(XML_SCHEMA_NAMESPACE, XML_SCHEMA_RESOURCE);
					}}
			));
			return schemaFactory.newSchema(schemaSources);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@SuppressWarnings("PMD.ReturnEmptyCollectionRatherThanNull")
	protected Source[] getSchemaSources() {
		return null;
	}

	protected ErrorHandler getErrorHandler() {
		return new SimpleErrorHandler();
	}

	public void parse(InputSource source) throws ParserException {
		try {
			xr.parse(source);
		} catch (Exception ex) {
			throw new ParserException(ex);
		}
	}

	/**
	 * Always throws exceptions and stops parsing.
	 */
	public static class SimpleErrorHandler implements ErrorHandler {
		@Override
		public void warning(SAXParseException e) throws SAXException {
			throw new SAXException(e);
		}

		@Override
		public void error(SAXParseException e) throws SAXException {
			throw new SAXException(e);
		}

		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			throw new SAXException(e);
		}
	}

	@SuppressWarnings("PMD.AvoidStringBufferField")
	public static class Handler<I> extends DefaultHandler {

		protected SAXParser parser;
		protected I instance;
		protected Handler<?> parent;
		protected StringBuilder chars = new StringBuilder();
		protected Attributes attributes;

		public Handler(I instance) {
			this(instance, null, null);
		}

		public Handler(I instance, SAXParser parser) {
			this(instance, parser, null);
		}

		public Handler(I instance, Handler<?> parent) {
			this(instance, parent.getParser(), parent);
		}

		public Handler(I instance, SAXParser parser, Handler<?> parent) {
			this.instance = instance;
			this.parser = parser;
			this.parent = parent;
			if (parser != null) {
				parser.setContentHandler(this);
			}
		}

		public I getInstance() {
			return instance;
		}

		public SAXParser getParser() {
			return parser;
		}

		public Handler<?> getParent() {
			return parent;
		}

		protected void switchToParent() {
			if (parser != null && parent != null) {
				parser.setContentHandler(parent);
				attributes = null;
			}
		}

		public String getCharacters() {
			return chars.toString();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
								 Attributes attributes) throws SAXException {
			this.chars = new StringBuilder();
			this.attributes = new AttributesImpl(attributes); // see https://docstore.mik.ua/orelly/xml/sax2/ch05_01.htm, section 5.1.1
			if (log.isTraceEnabled()) {
				log.trace(getClass().getSimpleName() + " starting: " + localName);
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (chars.length()+length>1_000_000)
				throw new SAXException();
			chars.append(ch, start, length);
		}

		@Override
		public void endElement(String uri, String localName,
							   String qName) throws SAXException {

			if (isLastElement(uri, localName, qName)) {
				if (log.isTraceEnabled()) {
					log.trace(getClass().getSimpleName() + ": last element, switching to parent: " + localName);
				}
				switchToParent();
				return;
			}

			if (log.isTraceEnabled()) {
				log.trace(getClass().getSimpleName() + " ending: " + localName);
			}
		}

		protected boolean isLastElement(String uri, String localName, String qName) {
			return false;
		}

		protected Attributes getAttributes() {
			return attributes;
		}
	}

}

