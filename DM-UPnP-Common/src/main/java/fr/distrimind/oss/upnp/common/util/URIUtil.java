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

package fr.distrimind.oss.upnp.common.util;



import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

/**
 * @author Christian Bauer
 */
public class URIUtil {

	public static final String UTF_8 = "UTF-8";

	/**
	 * Guarantees that the returned URI is absolute, no matter what the argument is.
	 *
	 * @param base An absolute base URI, can be null!
	 * @param uri  A string that either represents a relative or an already absolute URI
	 * @return An absolute URI
	 * @throws IllegalArgumentException If the base URI is null and the given URI string is not absolute
	 */
	public static URI createAbsoluteURI(URI base, String uri) throws IllegalArgumentException {
		return createAbsoluteURI(base, URI.create(uri));
	}

	@SuppressWarnings("PMD.AvoidReassigningParameters")
	public static URI createAbsoluteURI(URI base, URI relativeOrNot) throws IllegalArgumentException {
		if (base == null && !relativeOrNot.isAbsolute()) {
			throw new IllegalArgumentException("Base URI is null and given URI is not absolute");
		} else if (base == null) {
			return relativeOrNot;
		} else {
			// If the given base URI has no path we give it a root path
			if (base.getPath().isEmpty()) {
				try {
					base = new URI(base.getScheme(), base.getAuthority(), "/", base.getQuery(), base.getFragment());
				} catch (Exception ex) {
					throw new IllegalArgumentException(ex);
				}
			}
			return base.resolve(relativeOrNot);
		}
	}

	public static URL createAbsoluteURL(URL base, String uri) throws IllegalArgumentException {
		return createAbsoluteURL(base, URI.create(uri));
	}

	public static URL createAbsoluteURL(URL base, URI relativeOrNot) throws IllegalArgumentException {

		if (base == null && !relativeOrNot.isAbsolute()) {
			throw new IllegalArgumentException("Base URL is null and given URI is not absolute");
		} else if (base == null) {
			try {
				return relativeOrNot.toURL();
			} catch (Exception ex) {
				throw new IllegalArgumentException("Base URL was null and given URI can't be converted to URL");
			}
		} else {
			try {
				URI baseURI = base.toURI();
				URI absoluteURI = createAbsoluteURI(baseURI, relativeOrNot);
				return absoluteURI.toURL();
			} catch (Exception ex) {
				throw new IllegalArgumentException(
						"Base URL is not an URI, or can't create absolute URI (null?), " +
								"or absolute URI can not be converted to URL", ex);
			}
		}
	}

	public static URL createAbsoluteURL(URI base, URI relativeOrNot) throws IllegalArgumentException {
		try {
			return createAbsoluteURI(base, relativeOrNot).toURL();
		} catch (Exception ex) {
			throw new IllegalArgumentException("Absolute URI can not be converted to URL", ex);
		}
	}

	public static URL createAbsoluteURL(InetAddress address, int localStreamPort, URI relativeOrNot) throws IllegalArgumentException {
		try {
			if (address instanceof Inet6Address) {
				return createAbsoluteURL(new URL("http://[" + address.getHostAddress() + "]:" + localStreamPort), relativeOrNot);
			} else if (address instanceof Inet4Address) {
				return createAbsoluteURL(new URL("http://" + address.getHostAddress() + ":" + localStreamPort), relativeOrNot);
			} else {
				throw new IllegalArgumentException("InetAddress is neither IPv4 nor IPv6: " + address);
			}
		} catch (Exception ex) {
			throw new IllegalArgumentException("Address, port, and URI can not be converted to URL", ex);
		}
	}

	public static URI createRelativePathURI(URI uri) {
		assertRelativeURI("Given", uri);

		// Remove all "./" segments
		URI normalizedURI = uri.normalize();

		// Remove all "../" segments
		String uriString = normalizedURI.toString();
		int idx;
		while ((idx = uriString.indexOf("../")) != -1)
			uriString = uriString.substring(0, idx) + uriString.substring(idx + 3);

		// Make relative path
		while (uriString.startsWith("/"))
			uriString = uriString.substring(1);

		return URI.create(uriString);
	}

	public static URI createRelativeURI(URI base, URI full) {
		return base.relativize(full);
	}

	public static URI createRelativeURI(URL base, URL full) throws IllegalArgumentException {
		try {
			return createRelativeURI(base.toURI(), full.toURI());
		} catch (Exception ex) {
			throw new IllegalArgumentException("Can't convert base or full URL to URI", ex);
		}
	}

	public static URI createRelativeURI(URI base, URL full) throws IllegalArgumentException {
		try {
			return createRelativeURI(base, full.toURI());
		} catch (Exception ex) {
			throw new IllegalArgumentException("Can't convert full URL to URI", ex);
		}
	}

	public static URI createRelativeURI(URL base, URI full) throws IllegalArgumentException {
		try {
			return createRelativeURI(base.toURI(), full);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Can't convert base URL to URI", ex);
		}
	}

	public static boolean isAbsoluteURI(String s) {
		URI uri = URI.create(s);
		return uri.isAbsolute();
	}

	public static void assertRelativeURI(String what, URI uri) {
		if (uri.isAbsolute()) {
			throw new IllegalArgumentException(what + " URI must be relative, without scheme and authority");
		}
	}

	public static URL toURL(URI uri) {
		if (uri == null) return null;
		try {
			return uri.toURL();
		} catch (MalformedURLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static URI toURI(URL url) {

		if (url == null) return null;
		try {
			return url.toURI();
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String percentEncode(String s) {
		if (s == null) return "";
		try {
			return URLEncoder.encode(s, StandardCharsets.UTF_8);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static String percentDecode(String s) {
		if (s == null) return "";
		try {
			return URLDecoder.decode(s, StandardCharsets.UTF_8);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Implementation of path/query/fragment encoding as explained here:
	 * <a href="https://www.lunatech-research.com/archives/2009/02/03/what-every-web-developer-must-know-about-url-encoding">...</a>
	 */

	public final static BitSet ALLOWED = new BitSet() {
		private static final long serialVersionUID = 1L;
		{
		int i;
		for (i = 'a'; i <= 'z'; i++) {
			set(i);
		}
		for (i = 'A'; i <= 'Z'; i++) {
			set(i);
		}
		for (i = '0'; i <= '9'; i++) {
			set(i);
		}
		set('!');
		set('$');
		set('&');
		set('\'');
		set('(');
		set(')');
		set('*');
		set('+');
		set(',');
		set(';');
		set('=');
		set('-');
		set('.');
		set('_');
		set('~');
		set(':');
		set('@');
	}};

	public final static BitSet PATH_SEGMENT = new BitSet() {
		private static final long serialVersionUID = 1L;
		{
		or(ALLOWED);
		clear(';');
	}};

	public final static BitSet PATH_PARAM_NAME = new BitSet() {
		private static final long serialVersionUID = 1L;
		{
		or(ALLOWED);
		clear(';');
		clear('=');
	}};

	public final static BitSet PATH_PARAM_VALUE = new BitSet() {
		private static final long serialVersionUID = 1L;
		{
		or(ALLOWED);
		clear(';');
	}};

	public final static BitSet QUERY = new BitSet() {
		private static final long serialVersionUID = 1L;
		{
		or(ALLOWED);
		set('/');
		set('?');
		clear('=');
		clear('&');
		clear('+');
	}};

	public final static BitSet FRAGMENT = new BitSet() {
		private static final long serialVersionUID = 1L;
		{
		or(ALLOWED);
		set('/');
		set('?');
	}};

	public static String encodePathSegment(final String pathSegment) {
		return encode(PATH_SEGMENT, pathSegment, UTF_8);
	}

	public static String encodePathParamName(final String pathParamName) {
		return encode(PATH_PARAM_NAME, pathParamName, UTF_8);
	}

	public static String encodePathParamValue(final String pathParamValue) {
		return encode(PATH_PARAM_VALUE, pathParamValue, UTF_8);
	}

	public static String encodeQueryNameOrValue(final String queryNameOrValue) {
		return encode(QUERY, queryNameOrValue, UTF_8);
	}

	public static String encodeFragment(final String fragment) {
		return encode(FRAGMENT, fragment, UTF_8);
	}

	public static String encode(BitSet allowedCharacters, String s, String charset) {
		if (s == null)
			return null;
		final StringBuilder encoded = new StringBuilder(s.length() * 3);
		final char[] characters = s.toCharArray();
		try {
			for (char c : characters) {
				if (allowedCharacters.get(c)) {
					encoded.append(c);
				} else {
					byte[] bytes = String.valueOf(c).getBytes(charset);
					for (byte b : bytes)
						encoded.append(String.format("%%%1$02X", b & 0xFF));
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return encoded.toString();
	}

}
