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

package fr.distrimind.oss.upnp.common.model.types;

import fr.distrimind.oss.upnp.common.model.Constants;
import fr.distrimind.oss.upnp.common.model.ModelUtil;

import java.util.Objects;
import java.util.regex.Matcher;

/**
 * Header in SOAP action messages, naturally declaring the same thing as the body of the SOAP message.
 *
 * @author Christian Bauer
 */
public class SoapActionType {

    public static final String MAGIC_CONTROL_NS = "schemas-upnp-org";
    public static final String MAGIC_CONTROL_TYPE = "control-1-0";


    private final String namespace;
    private final String type;
    private final String actionName;
    private final Integer version;

    public SoapActionType(ServiceType serviceType, String actionName) {
        this(serviceType.getNamespace(), serviceType.getType(), serviceType.getVersion(), actionName);
    }

    public SoapActionType(String namespace, String type, Integer version, String actionName) {
        this.namespace = namespace;
        this.type = type;
        this.version = version;
        this.actionName = actionName;

        if (actionName != null && !ModelUtil.isValidUDAName(actionName)) {
            throw new IllegalArgumentException("Action name contains illegal characters: " + actionName);
        }
    }

    public String getActionName() {
        return actionName;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getType() {
        return type;
    }

    public Integer getVersion() {
        return version;
    }

    public static SoapActionType valueOf(String s) throws InvalidValueException {
        Matcher magicControlMatcher = Constants.getPatternSOAPActionTypeMagicControl().matcher(s);
        
        try {
        	if (magicControlMatcher.matches()) {
        		return new SoapActionType(MAGIC_CONTROL_NS, MAGIC_CONTROL_TYPE, null, magicControlMatcher.group(1)); // throws IllegalArgumentException
        	}

        	Matcher matcher = Constants.getPatternSOAPActionType().matcher(s);
        	if (matcher.matches())
        		return new SoapActionType(matcher.group(1), matcher.group(2), Integer.valueOf(matcher.group(3)), matcher.group(4));

        } catch(RuntimeException e) {
        	throw new InvalidValueException(String.format(
                "Can't parse action type string (namespace/type/version#actionName) '%s': %s", s, e
            ));
        }
        throw new InvalidValueException("Can't parse action type string (namespace/type/version#actionName): " + s);
    }

    public ServiceType getServiceType() {
        if (version == null) return null;
        return new ServiceType(namespace, type, version);
    }

    @Override
    public String toString() {
        return getTypeString() + "#" + getActionName();
    }

    public String getTypeString() {
        if (version == null) {
            return "urn:" + getNamespace() + ":" + getType();
        } else {
            return "urn:" + getNamespace() + ":service:" + getType() + ":" + getVersion();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SoapActionType)) return false;

        SoapActionType that = (SoapActionType) o;

        if (!actionName.equals(that.actionName)) return false;
        if (!namespace.equals(that.namespace)) return false;
        if (!type.equals(that.type)) return false;
		return Objects.equals(version, that.version);
	}

    @Override
    public int hashCode() {
        int result = namespace.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + actionName.hashCode();
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

}