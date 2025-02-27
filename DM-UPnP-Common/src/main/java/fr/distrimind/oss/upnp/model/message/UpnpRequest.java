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

import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

/**
 * A request message, with a method (GET, POST, NOTIFY, etc).
 *
 * @author Christian Bauer
 */
public class UpnpRequest extends UpnpOperation {

    public enum Method {

        GET("GET"),
        POST("POST"),
        NOTIFY("NOTIFY"),
        MSEARCH("M-SEARCH"),
        SUBSCRIBE("SUBSCRIBE"),
        UNSUBSCRIBE("UNSUBSCRIBE"),
        UNKNOWN("UNKNOWN");

        private static final Map<String, Method> byName = new HashMap<>() {
            private static final long serialVersionUID = 1L;
            {
			for (Method m : Method.values()) {
				put(m.getHttpName(), m);
			}
		}};

        private final String httpName;

        Method(String httpName) {
            this.httpName = httpName;
        }

        public String getHttpName() {
            return httpName;
        }

        public static Method getByHttpName(String httpName) {
            if (httpName == null) return UNKNOWN;
        	Method m = byName.get(httpName.toUpperCase(Locale.ROOT));
            return m != null ? m : UNKNOWN;
        }
    }

    private final Method method;
    private URI uri;

    public UpnpRequest(Method method) {
        this.method = method;
    }

    public UpnpRequest(Method method, URI uri) {
        this.method = method;
        this.uri = uri;
    }

    public UpnpRequest(Method method, URL url) {
        this.method = method;
        try {
            if (url != null) {
                this.uri = url.toURI();
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Method getMethod() {
        return method;
    }

    public String getHttpMethodName() {
        return method.getHttpName();
    }

    public URI getURI() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return getHttpMethodName() + (getURI() != null ? " " + getURI() : "");
    }
}
