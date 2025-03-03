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

import fr.distrimind.oss.upnp.common.Log;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;

/**
 * @author Michael Pujos
 */
public class XmlPullParserUtils {

	final private static DMLogger log = Log.getLogger(XmlPullParserUtils.class);

	public static boolean isNullOrEmpty(String s) {
		return s == null || s.isEmpty();
	}

	public static void searchTag(Element element, String tag) throws IOException {
		if (!searchTagImpl(element, tag)) {
			throw new IOException(String.format("Tag '%s' not found", tag));
		}
	}

	@SuppressWarnings("PMD.LooseCoupling")
	public static boolean searchTagImpl(Element element, String tag) {
		Elements children = element.children();
		for (Element child : children) {
			String tagName = child.tagName();
			if (tagsEquals(tagName, tag)) {
				return true;
			}
			if (searchTagImpl(child, tag)) {
				return true;
			}
		}
		return false;
	}

	public static String fixXMLEntities(String xml) {
		if (xml==null)
			return null;
		StringBuilder fixedXml = new StringBuilder(xml.length());

		boolean isFixed = false;

		for (int i = 0; i < xml.length(); i++) {

			char c = xml.charAt(i);
			if (c == '&') {
				// will not detect all possibly valid entities but should be sufficient for the purpose
				String sub = xml.substring(i, Math.min(i + 10, xml.length()));
				if (!sub.startsWith("&#") && !sub.startsWith("&lt;") && !sub.startsWith("&gt;") && !sub.startsWith("&amp;") &&
						!sub.startsWith("&apos;") && !sub.startsWith("&quot;")) {
					isFixed = true;
					fixedXml.append("&amp;");
				} else {
					fixedXml.append(c);
				}
			} else {
				fixedXml.append(c);
			}
		}

		if (isFixed) {
			log.warn("fixed badly encoded entities in XML");
		}

		return fixedXml.toString();
	}


	public static boolean tagsEquals(String foundTag, String tag)
	{
		return tag.equalsIgnoreCase(foundTag) || (tag.length()<foundTag.length() && foundTag.charAt(foundTag.length()-tag.length()-1)==':' && tag.regionMatches(true, 0, foundTag, foundTag.length()-tag.length(), tag.length()));
	}
}
