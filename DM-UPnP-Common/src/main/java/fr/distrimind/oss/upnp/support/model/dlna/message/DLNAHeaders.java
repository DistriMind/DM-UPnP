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

package fr.distrimind.oss.upnp.support.model.dlna.message;

import fr.distrimind.oss.upnp.model.message.header.UpnpHeader;

import java.io.ByteArrayInputStream;
import java.util.*;
import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.Log;
import fr.distrimind.oss.upnp.model.message.UpnpHeaders;
import fr.distrimind.oss.upnp.support.model.dlna.message.header.DLNAHeader;

/**
 * Provides UPnP header API in addition to plain multimap HTTP header access.
 *
 * @author Mario Franco
 * @author Christian Bauer
 */
public class DLNAHeaders extends UpnpHeaders implements IDLNAHeaders {

    final private static DMLogger logger = Log.getLogger(DLNAHeaders.class);

    protected Map<DLNAHeader.Type, List<UpnpHeader<?>>> parsedDLNAHeaders;

    public DLNAHeaders() {
    }

    public DLNAHeaders(Map<String, List<String>> headers) {
        super(headers);
    }

    public DLNAHeaders(ByteArrayInputStream inputStream) {
        super(inputStream);
    }
    
    @Override
    protected void parseHeaders() {
        if (parsedHeaders == null) super.parseHeaders();
        
        // This runs as late as possible and only when necessary (getter called and map is dirty)
        parsedDLNAHeaders = new LinkedHashMap<>();
        if (logger.isDebugEnabled()) logger.debug("Parsing all HTTP headers for known UPnP headers: {0}", size());
        for (Entry<String, List<String>> entry : entrySet()) {

            if (entry.getKey() == null) continue; // Oh yes, the JDK has 'null' HTTP headers

            DLNAHeader.Type type = DLNAHeader.Type.getByHttpName(entry.getKey());
            if (type == null) {
                if (logger.isDebugEnabled()) logger.debug("Ignoring non-UPNP HTTP header: {0}", entry.getKey());
                continue;
            }

            for (String value : entry.getValue()) {
                UpnpHeader<?> upnpHeader = DLNAHeader.newInstance(type, value);
                if (upnpHeader == null || upnpHeader.getValue() == null) {
                    if (logger.isDebugEnabled()) logger.debug("Ignoring known but non-parsable header (value violates the UDA specification?) '{0}': {1}", type.getHttpName(), value);
                } else {
                    addParsedValue(type, upnpHeader);
                }
            }
        }
    }

    protected void addParsedValue(DLNAHeader.Type type, UpnpHeader<?> value) {
        logger.debug("Adding parsed header: {0}", value);
		List<UpnpHeader<?>> list = parsedDLNAHeaders.computeIfAbsent(type, k -> new LinkedList<>());
		list.add(value);
    }

    @Override
    public List<String> put(String key, List<String> values) {
        parsedDLNAHeaders = null;
        return super.put(key, values);
    }

    @Override
    public void add(String key, String value) {
        parsedDLNAHeaders = null;
        super.add(key, value);
    }

    @Override
    public List<String> remove(Object key) {
        parsedDLNAHeaders = null;
        return super.remove(key);
    }

    @Override
    public void clear() {
        parsedDLNAHeaders = null;
        super.clear();
    }
    @Override
    public boolean containsKey(DLNAHeader.Type type) {
        if (parsedDLNAHeaders == null) parseHeaders();
        return parsedDLNAHeaders.containsKey(type);
    }
    @Override
    public List<UpnpHeader<?>> get(DLNAHeader.Type type) {
        if (parsedDLNAHeaders == null) parseHeaders();
        return parsedDLNAHeaders.get(type);
    }
    @Override
    public void add(DLNAHeader.Type type, UpnpHeader<?> value) {
        super.add(type.getHttpName(), value.getString());
        if (parsedDLNAHeaders != null)
            addParsedValue(type, value);
    }
    @Override
    public void remove(DLNAHeader.Type type) {
        super.remove(type.getHttpName());
        if (parsedDLNAHeaders != null)
            parsedDLNAHeaders.remove(type);
    }
    @Override
    public List<UpnpHeader<?>> getAsArray(DLNAHeader.Type type) {
        if (parsedDLNAHeaders == null) parseHeaders();
        return parsedDLNAHeaders.get(type) != null
                ? parsedDLNAHeaders.get(type)
                : Collections.emptyList();
    }

    @Override
    public UpnpHeader<?> getFirstHeader(DLNAHeader.Type type) {
        return getAsArray(type).stream().findFirst().orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Override
	public <H extends UpnpHeader<?>> H getFirstHeader(DLNAHeader.Type type, Class<H> subtype) {
        List<UpnpHeader<?>> headers = getAsArray(type);

		for (UpnpHeader<?> header : headers) {
            if (subtype.isAssignableFrom(header.getClass())) {
                return (H) header;
            }
        }
        return null;
    }

    @Override
    public void log() {
        if (logger.isDebugEnabled()) {
            super.log();
            if (parsedDLNAHeaders != null && !parsedDLNAHeaders.isEmpty()) {
                logger.debug("########################## PARSED DLNA HEADERS ##########################");
                for (Map.Entry<DLNAHeader.Type, List<UpnpHeader<?>>> entry : parsedDLNAHeaders.entrySet()) {
                    if (logger.isDebugEnabled()) logger.debug("=== TYPE: {0}", entry.getKey());
                    for (UpnpHeader<?> upnpHeader : entry.getValue()) {
                        logger.debug("HEADER: {0}", upnpHeader);
                    }
                }
            }
            logger.debug("####################################################################");
        }
    }

}
