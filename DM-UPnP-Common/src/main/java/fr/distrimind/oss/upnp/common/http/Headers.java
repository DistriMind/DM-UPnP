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
 */ /*
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
 */ /*
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
 package fr.distrimind.oss.upnp.common.http;

 import fr.distrimind.oss.upnp.common.model.Constants;

 import java.io.ByteArrayInputStream;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;

 /**
  * Unifies various HTTP header representations.
  * <p>
  * Snoracle, do you think you could add something like this to JDK 11g? Or did you kick out
  * the guy who knows how HTTP works so Larry can get his new jet filled up?
  * </p>
  * <p>
  * TODO: This is just a case-insensitive multi-valued map of strings...
  * </p>
  *
  * @author Christian Bauer
  */
 public class Headers implements IHeaders {

	 final static byte CR = 13;
	 final static byte LF = 10;

	 final Map<String, List<String>> map = new HashMap<>(32);
	 private boolean normalizeHeaders = true;

	 public Headers() {
	 }

	 public Headers(Map<String, List<String>> map) {
		 putAll(map);
	 }

	 @SuppressWarnings("PMD.LooseCoupling")
	 public Headers(ByteArrayInputStream inputStream) {
		 if (inputStream.available()> Constants.MAX_HEADER_LENGTH_IN_BYTES)
			 throw new IllegalArgumentException();
		 StringBuilder sb = new StringBuilder(256);
		 Headers headers = new Headers();
		 String line = readLine(sb, inputStream);
		 String lastHeader = null;
		 if (!line.isEmpty()) {
			 do {
				 char firstChar = line.charAt(0);
				 if (lastHeader != null && (firstChar == ' ' || firstChar == '\t')) {
					 List<String> current = headers.get(lastHeader);
					 int lastPos = current.size() - 1;
					 String newString = current.get(lastPos) + line.trim();
					 current.set(lastPos, newString);
				 } else {
					 String[] header = splitHeader(line);
					 headers.add(header[0], header[1]);
					 lastHeader = header[0];
				 }

				 sb.delete(0, sb.length());
				 line = readLine(sb, inputStream);
			 } while (!line.isEmpty());
		 }

		 putAll(headers);
	 }

	 public Headers(boolean normalizeHeaders) {
		 this.normalizeHeaders = normalizeHeaders;
	 }

	 @Override
	 public int size() {
		 return map.size();
	 }

	 @Override
	 public boolean isEmpty() {
		 return map.isEmpty();
	 }

	 @Override
	 public boolean containsKey(Object key) {
		 return key instanceof String && map.containsKey(normalize((String) key));
	 }

	 @Override
	 public boolean containsValue(Object value) {
		 return map.containsValue(value);
	 }

	 @Override
	 public List<String> get(Object key) {
		 return map.get(normalize((String) key));
	 }

	 @Override
	 public List<String> put(String key, List<String> value) {
		 return map.put(normalize(key), value);
	 }

	 @Override
	 public List<String> remove(Object key) {
		 return map.remove(normalize((String) key));
	 }

	 @Override
	 public void putAll(Map<? extends String, ? extends List<String>> t) {
		 // Enforce key normalization!
		 for (Entry<? extends String, ? extends List<String>> entry : t.entrySet()) {
			 put(entry.getKey(), entry.getValue());
		 }
	 }

	 @Override
	 public void clear() {
		 map.clear();
	 }

	 @Override
	 public Set<String> keySet() {
		 return map.keySet();
	 }
	 @Override
	 public Collection<List<String>> values() {
		 return map.values();
	 }
	 @Override
	 public Set<Map.Entry<String, List<String>>> entrySet() {
		 return map.entrySet();
	 }
	 @Override
	 public boolean equals(Object o) {
		 if (o instanceof Headers)
		 	return map.equals(((Headers)o).map);
		 else if (o instanceof Map)
			 return map.equals(o);
		 else
			 return false;

	 }
	 @Override
	 public int hashCode() {
		 return map.hashCode();
	 }

	 @Override
	 public String getFirstHeader(String key) {
		 List<String> l = map.get(normalize(key));
		 return l != null && !l.isEmpty() ? l.get(0) : null;
	 }

	 @Override
	 public void add(String key, String value) {
		 String k = normalize(key);
		 List<String> l = map.computeIfAbsent(k, k1 -> new LinkedList<>());
		 l.add(value);
	 }

	 @Override
	 public void set(String key, String value) {
		 List<String> l = new LinkedList<>();
		 l.add(value);
		 put(key, l);
	 }

	 private String normalize(String key) {
		 String result=key;

		 if (normalizeHeaders) {
			 if (key == null) return null;
			 if (key.isEmpty()) return key;
			 char[] b;
			 b = key.toCharArray();
			 final int caseDiff = 'a' - 'A';//android optimization

			 if (b[0] >= 'a' && b[0] <= 'z') {
				 b[0] = (char) (b[0] - caseDiff);
			 }
			 int length = key.length();//android optimization
			 for (int i = 1; i < length;  i++) {
				 if (b[i] >= 'A' && b[i] <= 'Z') {
					 b[i] = (char) (b[i] + caseDiff);
				 }
			 }
			 result = new String(b);
		 }
		 return result;
	 }

	 public static String readLine(ByteArrayInputStream is) {
		 return readLine(new StringBuilder(256), is);
	 }

	 public static String readLine(StringBuilder sb, ByteArrayInputStream is) {
		 int nextByte;
		 while((nextByte = is.read()) != -1) {
			 char nextChar = (char) nextByte;
			 if (nextChar == CR) {
				 nextByte = (char) is.read();
				 if (nextByte == LF) {
					 break;
				 }
			 } else if (nextChar == LF) {
				 break;
			 }

			 sb.append(nextChar);
		 }
		 return sb.toString();
	 }

	 protected String[] splitHeader(String sb) {
		 int nameStart;
		 int nameEnd;
		 int colonEnd;
		 int valueStart;
		 int valueEnd;

		 nameStart = findNonWhitespace(sb, 0);
		 for (nameEnd = nameStart; nameEnd < sb.length(); nameEnd++) {
			 char ch = sb.charAt(nameEnd);
			 if (ch == ':' || Character.isWhitespace(ch)) {
				 break;
			 }
		 }

		 for (colonEnd = nameEnd; colonEnd < sb.length(); colonEnd++) {
			 if (sb.charAt(colonEnd) == ':') {
				 colonEnd++;
				 break;
			 }
		 }

		 valueStart = findNonWhitespace(sb, colonEnd);
		 valueEnd = findEndOfString(sb);

		 // This gets a bit messy because there are really HTTP headers without values (go figure...)
		 return new String[]
				 {
						 sb.substring(nameStart, nameEnd),
						 sb.length() >= valueEnd && valueStart < valueEnd
								 ? sb.substring(valueStart, valueEnd)
								 : null
				 };
	 }

	 protected int findNonWhitespace(String sb, int offset) {
		 int result;
		 for (result = offset; result < sb.length(); result++) {
			 if (!Character.isWhitespace(sb.charAt(result))) {
				 break;
			 }
		 }
		 return result;
	 }

	 protected int findEndOfString(String sb) {
		 int result;
		 for (result = sb.length(); result > 0; result--) {
			 if (!Character.isWhitespace(sb.charAt(result - 1))) {
				 break;
			 }
		 }
		 return result;
	 }

	 @Override
	 public String toString() {
		 StringBuilder headerString = new StringBuilder(512);
		 for (Entry<String, List<String>> headerEntry : entrySet()) {

			 headerString.append(headerEntry.getKey()).append(": ");

			 for (String v : headerEntry.getValue()) {
				 headerString.append(v).append(",");
			 }
			 headerString.delete(headerString.length()-1, headerString.length());
			 headerString.append("\r\n");
		 }
		 return headerString.toString();
	 }

 }