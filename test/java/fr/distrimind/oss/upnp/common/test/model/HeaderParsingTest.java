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

package fr.distrimind.oss.upnp.common.test.model;

import fr.distrimind.oss.upnp.common.model.Constants;
import fr.distrimind.oss.upnp.common.model.ServerClientTokens;
import fr.distrimind.oss.upnp.common.model.message.header.*;
import fr.distrimind.oss.upnp.common.model.types.DeviceType;
import fr.distrimind.oss.upnp.common.model.types.NamedDeviceType;
import fr.distrimind.oss.upnp.common.model.types.NamedServiceType;
import fr.distrimind.oss.upnp.common.model.types.ServiceType;
import fr.distrimind.oss.upnp.common.model.types.UDADeviceType;
import fr.distrimind.oss.upnp.common.model.types.UDAServiceType;
import fr.distrimind.oss.upnp.common.util.MimeType;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Locale;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;


public class HeaderParsingTest {

    public static final String HTTP_127_0_0_1_FOO = "http://127.0.0.1/foo";
    public static final String URN_SCHEMAS_UPNP_ORG_SERVICE_MY_SERVICE_TYPE_123 = "urn:schemas-upnp-org:service:MyServiceType:123";
    public static final String FOO_BAR = "foo-bar";
    public static final String MY_DEVICE_TYPE = "MyDeviceType";
    public static final String URN_SCHEMAS_UPNP_ORG_DEVICE_MY_DEVICE_TYPE_123 = "urn:schemas-upnp-org:device:MyDeviceType:123";
    public static final String SCHEMAS_UPNP_ORG = "schemas-upnp-org";
    public static final String FOO_DOR_BAR = "foo.bar";
    public static final String ABC = "abc";
    public static final String MY_SERVICE_TYPE = "MyServiceType";

    @Test
    public void parseContentTypeHeader() {
        ContentTypeHeader header = new ContentTypeHeader(MimeType.valueOf("foo/bar;charset=\"utf-8\""));
        assertEquals(header.getString(), "foo/bar;charset=\"utf-8\"");
    }

    @Test
    public void parseDeviceType() {
        DeviceType deviceType = DeviceType.valueOf("urn:foo-bar:device:MyDeviceType:123");
        assertEquals(deviceType.getNamespace(), FOO_BAR);
        assertEquals(deviceType.getType(), MY_DEVICE_TYPE);
        assertEquals(deviceType.getVersion(), 123);
    }

    @Test
    public void parseUDADeviceType() {
        UDADeviceType deviceType = (UDADeviceType)DeviceType.valueOf(URN_SCHEMAS_UPNP_ORG_DEVICE_MY_DEVICE_TYPE_123);
        assertEquals(deviceType.getType(), MY_DEVICE_TYPE);
        assertEquals(deviceType.getVersion(), 123);
    }

    @Test
    public void parseInvalidDeviceTypeHeader() {
        DeviceTypeHeader header = new DeviceTypeHeader();
        header.setString("urn:foo-bar:device:!@#:123");
        assertEquals(header.getValue().getNamespace(), FOO_BAR);
        assertEquals(header.getValue().getType(), "---");
        assertEquals(header.getValue().getVersion(), 123);
        assertEquals(header.getString(), "urn:foo-bar:device:---:123");
    }

    @Test
    public void parseDeviceTypeHeaderURI() {
        DeviceTypeHeader header = new DeviceTypeHeader(URI.create(URN_SCHEMAS_UPNP_ORG_DEVICE_MY_DEVICE_TYPE_123));
        assertEquals(header.getValue().getNamespace(), SCHEMAS_UPNP_ORG);
        assertEquals(header.getValue().getType(), MY_DEVICE_TYPE);
        assertEquals(header.getValue().getVersion(), 123);
        assertEquals(header.getString(), URN_SCHEMAS_UPNP_ORG_DEVICE_MY_DEVICE_TYPE_123);

    }

    @Test
    public void parseDeviceUSNHeader() {
        DeviceUSNHeader header = new DeviceUSNHeader();
        header.setString("uuid:MY-DEVICE-123::urn:schemas-upnp-org:device:MY-DEVICE-TYPE:1");
        assertEquals(header.getValue().getUdn().getIdentifierString(), "MY-DEVICE-123");
        assert header.getValue().getDeviceType() instanceof UDADeviceType;
    }

    @Test
    public void parseDeviceUSNHeaderStatic() {
        DeviceUSNHeader header = new DeviceUSNHeader(
                NamedDeviceType.valueOf("uuid:MY-DEVICE-123::urn:schemas-upnp-org:device:MY-DEVICE-TYPE:1")
        );
        assertEquals(header.getValue().getUdn().getIdentifierString(), "MY-DEVICE-123");
        assert header.getValue().getDeviceType() instanceof UDADeviceType;
    }

    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidDeviceUSNHeader() {
        DeviceUSNHeader header = new DeviceUSNHeader();
        header.setString("uuid:MY-DEVICE-123--urn:schemas-upnp-org:device:MY-DEVICE-TYPE:1");
    }

    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidEXTHeader() {
        EXTHeader header = new EXTHeader();
        header.setString("MUST BE EMPTY STRING");
    }

    @Test
    public void parseHostHeaderConstructor() {
        HostHeader header = new HostHeader(FOO_DOR_BAR, 1234);
        assertEquals(header.getValue().getHost(), FOO_DOR_BAR);
        assertEquals(header.getValue().getPort(), 1234);

        header = new HostHeader(1234);
        assertEquals(header.getValue().getHost(), Constants.IPV4_UPNP_MULTICAST_GROUP);
        assertEquals(header.getValue().getPort(), 1234);
    }

    @Test
    public void parseHostHeader() {
        HostHeader header = new HostHeader();
        assertEquals(header.getValue().getHost(), Constants.IPV4_UPNP_MULTICAST_GROUP);
        assertEquals(header.getValue().getPort(), Constants.UPNP_MULTICAST_PORT);

        header = new HostHeader();
        header.setString("foo.bar:1234");
        assertEquals(header.getValue().getHost(), FOO_DOR_BAR);
        assertEquals(header.getValue().getPort(), 1234);

        header = new HostHeader();
        header.setString(FOO_DOR_BAR);
        assertEquals(header.getValue().getHost(), FOO_DOR_BAR);
        assertEquals(header.getValue().getPort(), Constants.UPNP_MULTICAST_PORT);
    }

    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidHostHeader() {
        HostHeader header = new HostHeader();
        header.setString("foo.bar:abc");
    }

    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidLocationHeader() {
        LocationHeader header = new LocationHeader();
        header.setString("this://is.not...a valid URL");
    }

    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidMANHeader() {
        MANHeader header = new MANHeader(ABC);
        header.setString("\"foo.bar\"; ns = baz"); // Not valid
    }

    @Test
    public void parseMANHeaderNoNS() {
        MANHeader header = new MANHeader(ABC);
        header.setString("\"foo.bar\"");
        assert FOO_DOR_BAR.equals(header.getValue());
        assert header.getNamespace() == null;
        assert "\"foo.bar\"".equals(header.getString());
    }

    @Test
    public void parseMANHeaderNS() {
        MANHeader header = new MANHeader(ABC);
        header.setString("\"foo.bar\"; ns =12");
        assert FOO_DOR_BAR.equals(header.getValue());
        assert "12".equals(header.getNamespace());
        assert "\"foo.bar\"; ns=12".equals(header.getString());
    }

    @Test
    public void parseMaxAgeHeader() {
        MaxAgeHeader header = new MaxAgeHeader();
        header.setString("max-age=1234, foobar=baz");
        assertEquals(header.getValue(), Integer.valueOf(1234));
    }

    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidMaxAgeHeader() {
        MaxAgeHeader header = new MaxAgeHeader();
        header.setString("max-foo=123");
    }

    @Test
    public void parseMXHeader() {
        MXHeader header = new MXHeader();
        header.setString("111");
        assertEquals(header.getValue(), Integer.valueOf(111));
        
        header = new MXHeader();
        header.setString("123");
        assertEquals(header.getValue(), MXHeader.DEFAULT_VALUE);

    }

    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidMXHeader() {
        MXHeader header = new MXHeader();
        header.setString(ABC);
    }

    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidNTSHeader() {
        NTSHeader header = new NTSHeader();
        header.setString("foo");
    }

    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidRootDeviceHeader() {
        RootDeviceHeader header = new RootDeviceHeader();
        header.setString("upnp:foodevice");
    }

    @Test
    public void parseServerHeader() {
        ServerHeader header = new ServerHeader();
        header.setString("foo/1 UPnP/1.1 bar/2");
        assertEquals(header.getValue().getOsName(), "foo");
        assertEquals(header.getValue().getOsVersion(), "1");
        assertEquals(header.getValue().getProductName(), "bar");
        assertEquals(header.getValue().getProductVersion(), "2");
        assertEquals(header.getValue().getMajorVersion(), 1);
        assertEquals(header.getValue().getMinorVersion(), 1);

        // Commas...
        header = new ServerHeader();
        header.setString("foo/1, UPnP/1.1, bar/2");
        assertEquals(header.getValue().getOsName(), "foo");
        assertEquals(header.getValue().getOsVersion(), "1");
        assertEquals(header.getValue().getProductName(), "bar");
        assertEquals(header.getValue().getProductVersion(), "2");
        assertEquals(header.getValue().getMajorVersion(), 1);
        assertEquals(header.getValue().getMinorVersion(), 1);

        // Whitespace in tokens
        header = new ServerHeader();
        header.setString("foo baz/1 UPnP/1.1 bar abc/2");
        assertEquals(header.getValue().getOsName(), "foo baz");
        assertEquals(header.getValue().getOsVersion(), "1");
        assertEquals(header.getValue().getProductName(), "bar abc");
        assertEquals(header.getValue().getProductVersion(), "2");
        assertEquals(header.getValue().getMajorVersion(), 1);
        assertEquals(header.getValue().getMinorVersion(), 1);

        // Commas and whitespace!
        header = new ServerHeader();
        header.setString("foo baz/1, UPnP/1.1, bar abc/2");
        assertEquals(header.getValue().getOsName(), "foo baz");
        assertEquals(header.getValue().getOsVersion(), "1");
        assertEquals(header.getValue().getProductName(), "bar abc");
        assertEquals(header.getValue().getProductVersion(), "2");
        assertEquals(header.getValue().getMajorVersion(), 1);
        assertEquals(header.getValue().getMinorVersion(), 1);

        // Absolutely not valid!
        header = new ServerHeader();
        header.setString("foo/1 UPnP/1.");
        assertEquals(header.getValue().getOsName(), ServerClientTokens.UNKNOWN_PLACEHOLDER);
        assertEquals(header.getValue().getOsVersion(), ServerClientTokens.UNKNOWN_PLACEHOLDER);
        assertEquals(header.getValue().getProductName(), ServerClientTokens.UNKNOWN_PLACEHOLDER);
        assertEquals(header.getValue().getProductVersion(), ServerClientTokens.UNKNOWN_PLACEHOLDER);
        assertEquals(header.getValue().getMajorVersion(), 1);
        assertEquals(header.getValue().getMinorVersion(), 0); // Assume UDA 1.0

    }

    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidServerHeader() {
        ServerHeader header = new ServerHeader();
        header.setString("foo/1 baz/123 bar/2");
    }

    @Test
    public void parseServiceType() {
        ServiceType serviceType = ServiceType.valueOf("urn:foo-bar:service:MyServiceType:123");
        assertEquals(serviceType.getNamespace(), FOO_BAR);
        assertEquals(serviceType.getType(), MY_SERVICE_TYPE);
        assertEquals(serviceType.getVersion(), 123);
    }

    @Test
    public void parseUDAServiceType() {
        UDAServiceType serviceType = (UDAServiceType)ServiceType.valueOf(URN_SCHEMAS_UPNP_ORG_SERVICE_MY_SERVICE_TYPE_123);
        assertEquals(serviceType.getType(), MY_SERVICE_TYPE);
        assertEquals(serviceType.getVersion(), 123);
    }

    @Test
    public void parseInvalidServiceTypeHeader() {
        ServiceTypeHeader header = new ServiceTypeHeader();
        header.setString("urn:foo-bar:service:!@#:123");
        assertEquals(header.getValue().getNamespace(), FOO_BAR);
        assertEquals(header.getValue().getType(), "---");
        assertEquals(header.getValue().getVersion(), 123);
        assertEquals(header.getString(), "urn:foo-bar:service:---:123");
    }

    @Test
    public void parseServiceTypeHeaderURI() {
        ServiceTypeHeader header = new ServiceTypeHeader(URI.create(URN_SCHEMAS_UPNP_ORG_SERVICE_MY_SERVICE_TYPE_123));
        assertEquals(header.getValue().getNamespace(), SCHEMAS_UPNP_ORG);
        assertEquals(header.getValue().getType(), MY_SERVICE_TYPE);
        assertEquals(header.getValue().getVersion(), 123);
        assertEquals(header.getString(), URN_SCHEMAS_UPNP_ORG_SERVICE_MY_SERVICE_TYPE_123);
    }

    @Test
    public void parseServiceUSNHeader() {
        ServiceUSNHeader header = new ServiceUSNHeader();
        header.setString("uuid:MY-SERVICE-123::urn:schemas-upnp-org:service:MY-SERVICE-TYPE:1");
        assertEquals(header.getValue().getUdn().getIdentifierString(), "MY-SERVICE-123");
        assert header.getValue().getServiceType() instanceof UDAServiceType;
    }

    @Test
    public void parseServiceUSNHeaderStatic() {
        ServiceUSNHeader header = new ServiceUSNHeader(
                NamedServiceType.valueOf("uuid:MY-SERVICE-123::urn:schemas-upnp-org:service:MY-SERVICE-TYPE:1")
        );
        assertEquals(header.getValue().getUdn().getIdentifierString(), "MY-SERVICE-123");
        assert header.getValue().getServiceType() instanceof UDAServiceType;
    }

    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidServiceUSNHeader() {
        ServiceUSNHeader header = new ServiceUSNHeader();
        header.setString("uuid:MY-SERVICE-123--urn:schemas-upnp-org:service:MY-SERVICE-TYPE:1");
    }

    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidSTAllHeader() {
        STAllHeader header = new STAllHeader();
        header.setString("ssdp:foo");
    }

    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidUDADeviceTypeHeader() {
        UDADeviceTypeHeader header = new UDADeviceTypeHeader();
        header.setString("urn:foo-bar:device:!@#:123");
    }

    @Test
    public void parseUDADeviceTypeHeaderURI() {
        UDADeviceTypeHeader header = new UDADeviceTypeHeader(URI.create(URN_SCHEMAS_UPNP_ORG_DEVICE_MY_DEVICE_TYPE_123));
        assertEquals(header.getValue().getNamespace(), SCHEMAS_UPNP_ORG);
        assertEquals(header.getValue().getType(), MY_DEVICE_TYPE);
        assertEquals(header.getValue().getVersion(), 123);
        assertEquals(header.getString(), URN_SCHEMAS_UPNP_ORG_DEVICE_MY_DEVICE_TYPE_123);
    }

    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidUDAServiceTypeHeader() {
        UDAServiceTypeHeader header = new UDAServiceTypeHeader();
        header.setString("urn:foo-bar:service:!@#:123");
    }

    @Test
    public void parseUDAServiceTypeHeaderURI() {
        UDAServiceTypeHeader header = new UDAServiceTypeHeader(URI.create(URN_SCHEMAS_UPNP_ORG_SERVICE_MY_SERVICE_TYPE_123));
        assertEquals(header.getValue().getNamespace(), SCHEMAS_UPNP_ORG);
        assertEquals(header.getValue().getType(), MY_SERVICE_TYPE);
        assertEquals(header.getValue().getVersion(), 123);
        assertEquals(header.getString(), URN_SCHEMAS_UPNP_ORG_SERVICE_MY_SERVICE_TYPE_123);

    }

    @Test
    public void parseUDNHeader() {
        UDNHeader header = new UDNHeader();
        header.setString("uuid:MY-UUID-1234");
        assertEquals(header.getValue().getIdentifierString(), "MY-UUID-1234");
    }

    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidUDNHeaderPrefix() {
        UDNHeader header = new UDNHeader();
        header.setString("MY-UUID-1234");
    }

    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidUDNHeaderURN() {
        UDNHeader header = new UDNHeader();
        header.setString("uuid:MY-UUID-1234::urn:foo-bar:baz");
    }

    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidUSNRootDeviceHeader() {
        USNRootDeviceHeader header = new USNRootDeviceHeader();
        header.setString("uuid:MY-UUID-1234::upnp:rootfoo");
    }

    @Test
    public void parseSoapActionHeader() {
        SoapActionHeader header = new SoapActionHeader(URI.create("urn:schemas-upnp-org:service:MyServiceType:1#MyAction"));
        assertEquals(header.getValue().getServiceType().getNamespace(), SCHEMAS_UPNP_ORG);
        assertEquals(header.getValue().getServiceType().getType(), MY_SERVICE_TYPE);
        assertEquals(header.getValue().getServiceType().getVersion(), 1);
        assertEquals(header.getValue().getActionName(), "MyAction");
        assertEquals(header.getString(), "\"urn:schemas-upnp-org:service:MyServiceType:1#MyAction\"");

    }

    @Test
    public void parseSoapActionHeaderString() {
        SoapActionHeader header = new SoapActionHeader();
        header.setString("\"urn:schemas-upnp-org:service:MyServiceType:1#MyAction\"");
        assertEquals(header.getValue().getServiceType().getNamespace(), SCHEMAS_UPNP_ORG);
        assertEquals(header.getValue().getServiceType().getType(), MY_SERVICE_TYPE);
        assertEquals(header.getValue().getServiceType().getVersion(), 1);
        assertEquals(header.getValue().getActionName(), "MyAction");
        assertEquals(header.getString(), "\"urn:schemas-upnp-org:service:MyServiceType:1#MyAction\"");
    }

    @Test
    public void parseSoapActionHeaderQueryString() {
        SoapActionHeader header = new SoapActionHeader();
        header.setString("\"urn:schemas-upnp-org:control-1-0#QueryStateVariable\"");
		assertNull(header.getValue().getServiceType());
        assertEquals(header.getValue().getType(), "control-1-0");
		assertNull(header.getValue().getVersion());
        assertEquals(header.getValue().getActionName(), "QueryStateVariable");
        assertEquals(header.getString(), "\"urn:schemas-upnp-org:control-1-0#QueryStateVariable\"");
    }

    @Test
    public void parseEventSequenceHeaderString() {
        EventSequenceHeader header = new EventSequenceHeader();
        header.setString("0");
        assertEquals(header.getValue().getValue(), Long.valueOf(0));
        header.setString("001");
        assertEquals(header.getValue().getValue(), Long.valueOf(1));
        header.setString("123");
        assertEquals(header.getValue().getValue(), Long.valueOf(123));

    }

    @Test
    public void parseTimeoutHeaderString() {
        TimeoutHeader header = new TimeoutHeader();
        header.setString("Second-123");
        assertEquals(header.getValue(), Integer.valueOf(123));
        header.setString("Second-infinite");
        assertEquals(header.getValue(), TimeoutHeader.INFINITE_VALUE);
    }

    @Test
    public void parseCallbackHeaderString() {
        CallbackHeader header = new CallbackHeader();
        header.setString("<http://127.0.0.1/foo>");
        assertEquals(header.getValue().size(), 1);
        assertEquals(header.getValue().get(0).toString(), HTTP_127_0_0_1_FOO);

        header.setString("<http://127.0.0.1/foo><http://127.0.0.1/bar>");
        assertEquals(header.getValue().size(), 2);
        assertEquals(header.getValue().get(0).toString(), HTTP_127_0_0_1_FOO);
        assertEquals(header.getValue().get(1).toString(), "http://127.0.0.1/bar");

        header.setString("<http://127.0.0.1/foo> <http://127.0.0.1/bar>");
        assertEquals(header.getValue().size(), 2);
        assertEquals(header.getValue().get(0).toString(), HTTP_127_0_0_1_FOO);
        assertEquals(header.getValue().get(1).toString(), "http://127.0.0.1/bar");
    }

    @Test
    public void parseInvalidCallbackHeaderString() {
        CallbackHeader header = new CallbackHeader();
        header.setString("<http://127.0.0.1/foo> <ftp://127.0.0.1/bar>");
        assertEquals(header.getValue().size(), 1);
        assertEquals(header.getValue().get(0).toString(), HTTP_127_0_0_1_FOO);

        /* TODO: I'm having trouble finding a valid URL that is
           an invalid URI in the standard JDK...
        header.setString("<http://127.0.0.1/foo> <http://we_need_a_valid_URL_but_invalid_URI>");
        assertEquals(header.getValue().size(), 1);
        assertEquals(header.getValue().get(0).toString(), "http://127.0.0.1/foo");
        */
    }

    @Test
    public void parseSubscriptionIdHeaderString() {
        SubscriptionIdHeader header = new SubscriptionIdHeader();
        header.setString("uuid:123-123-123-123");
        assertEquals(header.getValue(), "uuid:123-123-123-123");
    }

    @Test(expectedExceptions = InvalidHeaderException.class)
    public void parseInvalidSubscriptionIdHeaderString() {
        SubscriptionIdHeader header = new SubscriptionIdHeader();
        header.setString("abc:123-123-123-123");
    }

    @Test
    public void parseInterfaceMacAddress() {
        InterfaceMacHeader header = new InterfaceMacHeader("00:17:ab:e9:65:a0");
        assertEquals(header.getValue().length, 6);
        assertEquals(header.getString().toUpperCase(Locale.ROOT), "00:17:AB:E9:65:A0");
    }

    @Test
    public void parseRange() {
        RangeHeader header = new RangeHeader("bytes=1539686400-1540210688");
        assertEquals(header.getValue().getFirstByte(), Long.valueOf(1539686400));
        assertEquals(header.getValue().getLastByte(), Long.valueOf(1540210688));
        assertEquals(header.getString(), "bytes=1539686400-1540210688");
    }
    
    @Test
    public void parseContentRange() {
        ContentRangeHeader header = new ContentRangeHeader("bytes 1539686400-1540210688/21323123");
        assertEquals(header.getValue().getFirstByte(), Long.valueOf(1539686400));
        assertEquals(header.getValue().getLastByte(), Long.valueOf(1540210688));
        assertEquals(header.getValue().getByteLength(), Long.valueOf(21323123));
        assertEquals(header.getString(), "bytes 1539686400-1540210688/21323123");
    }
    
    @Test
    public void parsePragma() {
        PragmaHeader header = new PragmaHeader("no-cache");
        assertEquals(header.getValue().getValue(),"no-cache");
        assertEquals(header.getString(), "no-cache");
        
        header.setString("token=value");
        assertEquals(header.getValue().getToken(),"token");
        assertEquals(header.getValue().getValue(),"value");
        assertEquals(header.getString(), "token=value");
        
        header.setString("token=\"value\"");
        assertEquals(header.getValue().getToken(),"token");
        assertEquals(header.getValue().getValue(),"value");
        assertEquals(header.getString(), "token=\"value\"");
    }
}
