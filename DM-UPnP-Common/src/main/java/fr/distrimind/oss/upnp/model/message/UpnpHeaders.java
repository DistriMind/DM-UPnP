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

package fr.distrimind.oss.upnp.model.message;

import fr.distrimind.oss.upnp.http.Headers;
import fr.distrimind.oss.upnp.model.message.header.UpnpHeader;

import java.io.ByteArrayInputStream;
import java.util.*;
import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.Log;

/**
 * Provides UPnP header API in addition to plain multimap HTTP header access.
 *
 * @author Christian Bauer
 */
public class UpnpHeaders extends Headers implements IUpnpHeaders {

    final private static DMLogger logger = Log.getLogger(UpnpHeaders.class);

    protected Map<UpnpHeader.Type, List<UpnpHeader<?>>> parsedHeaders;

    public UpnpHeaders() {
    }

    public UpnpHeaders(Map<String, List<String>> headers) {
        super(headers);
    }

    public UpnpHeaders(ByteArrayInputStream inputStream) {
        super(inputStream);
    }

    public UpnpHeaders(boolean normalizeHeaders) {
        super(normalizeHeaders);
    }

    protected void parseHeaders() {
        // This runs as late as possible and only when necessary (getter called and map is dirty)
        parsedHeaders = new LinkedHashMap<>();

        if (logger.isDebugEnabled()) {
            logger.debug("Parsing all HTTP headers for known UPnP headers: " + size());
        }
        for (Entry<String, List<String>> entry : entrySet()) {

            if (entry.getKey() == null) continue; // Oh yes, the JDK has 'null' HTTP headers

            UpnpHeader.Type type = UpnpHeader.Type.getByHttpName(entry.getKey());
            if (type == null) {

                if (logger.isDebugEnabled()) {
                    logger.debug("Ignoring non-UPNP HTTP header: " + entry.getKey());
                }
                continue;
            }

            for (String value : entry.getValue()) {
                UpnpHeader<?> upnpHeader = UpnpHeader.newInstance(type, value);
                if (upnpHeader == null || upnpHeader.getValue() == null) {


                    if (logger.isDebugEnabled()) {
                        logger.debug(
                                "Ignoring known but irrelevant header (value violates the UDA specification?) '"
                                        + type.getHttpName()
                                        + "': "
                                        + value
                        );
                    }
                } else {
                    addParsedValue(type, upnpHeader);
                }
            }
        }
    }

    protected void addParsedValue(UpnpHeader.Type type, UpnpHeader<?> value) {
        if (logger.isDebugEnabled()) {
            logger.debug("Adding parsed header: " + value);
        }
		List<UpnpHeader<?>> list = parsedHeaders.computeIfAbsent(type, k -> new LinkedList<>());
		list.add(value);
    }

    @Override
    public List<String> put(String key, List<String> values) {
        parsedHeaders = null;
        return super.put(key, values);
    }

    @Override
    public void add(String key, String value) {
        parsedHeaders = null;
        super.add(key, value);
    }

    @Override
    public List<String> remove(Object key) {
        parsedHeaders = null;
        return super.remove(key);
    }

    @Override
    public void clear() {
        parsedHeaders = null;
        super.clear();
    }
    @Override
    public boolean containsKey(UpnpHeader.Type type) {
        if (parsedHeaders == null) parseHeaders();
        return parsedHeaders.containsKey(type);
    }
    @Override
    public List<UpnpHeader<?>> get(UpnpHeader.Type type) {
        if (parsedHeaders == null) parseHeaders();
        return parsedHeaders.get(type);
    }
    @Override
    public void add(UpnpHeader.Type type, UpnpHeader<?> value) {
        super.add(type.getHttpName(), value.getString());
        if (parsedHeaders != null)
            addParsedValue(type, value);
    }
    @Override
    public void remove(UpnpHeader.Type type) {
        super.remove(type.getHttpName());
        if (parsedHeaders != null)
            parsedHeaders.remove(type);
    }
    @Override
    public List<UpnpHeader<?>> getList(UpnpHeader.Type type) {
        if (parsedHeaders == null) parseHeaders();
        return parsedHeaders.get(type) != null
                ? Collections.unmodifiableList(parsedHeaders.get(type))
                : Collections.emptyList();
    }

    @Override
    public UpnpHeader<?> getFirstHeader(UpnpHeader.Type type) {
        List<UpnpHeader<?>> l=getList(type);
        return l.isEmpty()?
                null:
                l.iterator().next();
    }

    @SuppressWarnings("unchecked")
    @Override
	public <H extends UpnpHeader<?>> H getFirstHeader(UpnpHeader.Type type, Class<H> subtype) {
        List<UpnpHeader<?>> headers = getList(type);

		for (UpnpHeader<?> header : headers) {
            if (subtype.isAssignableFrom(header.getClass())) {
                return (H) header;
            }
        }
        return null;
    }

    @Override
    public String getFirstHeaderString(UpnpHeader.Type type) {
        UpnpHeader<?> header = getFirstHeader(type);
        return header != null ? header.getString() : null;
    }

    @Override

    public void log() {

        if (logger.isDebugEnabled()) {
            logger.debug("############################ RAW HEADERS ###########################");
            for (Entry<String, List<String>> entry : entrySet()) {
                logger.debug("=== NAME : " + entry.getKey());
                for (String v : entry.getValue()) {
                    logger.debug("VALUE: " + v);
                }
            }
            if (parsedHeaders != null && !parsedHeaders.isEmpty()) {
                logger.debug("########################## PARSED HEADERS ##########################");
                for (Map.Entry<UpnpHeader.Type, List<UpnpHeader<?>>> entry : parsedHeaders.entrySet()) {
                    if (logger.isDebugEnabled()) logger.debug("=== TYPE: " + entry.getKey());
                    for (UpnpHeader<?> upnpHeader : entry.getValue()) {
                        if (logger.isDebugEnabled()) logger.debug("HEADER: " + upnpHeader);
                    }
                }
            }
            logger.debug("####################################################################");
        }
    }

}
