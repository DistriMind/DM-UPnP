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

import fr.distrimind.oss.upnp.common.model.types.Base64Datatype;
import fr.distrimind.oss.upnp.common.model.types.DLNADoc;
import fr.distrimind.oss.upnp.common.model.types.Datatype;
import fr.distrimind.oss.upnp.common.model.types.DateTimeDatatype;
import fr.distrimind.oss.upnp.common.model.types.DoubleDatatype;
import fr.distrimind.oss.upnp.common.model.types.FloatDatatype;
import fr.distrimind.oss.upnp.common.model.types.IntegerDatatype;
import fr.distrimind.oss.upnp.common.model.types.InvalidValueException;
import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerFourBytesDatatype;
import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerOneByteDatatype;
import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerTwoBytesDatatype;
import fr.distrimind.oss.upnp.common.model.types.csv.CSVBoolean;
import fr.distrimind.oss.upnp.common.model.types.csv.CSVString;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.testng.Assert.assertEquals;


public class DatatypesTest {

    public static final String CET = "CET";

    @Test
    public void upperLowerCase() {
        // Broken devices do this
        assertEquals(Datatype.Builtin.getByDescriptorName("String"), Datatype.Builtin.STRING);
        assertEquals(Datatype.Builtin.getByDescriptorName("strinG"), Datatype.Builtin.STRING);
        assertEquals(Datatype.Builtin.getByDescriptorName("STRING"), Datatype.Builtin.STRING);
        assertEquals(Datatype.Builtin.getByDescriptorName("string"), Datatype.Builtin.STRING);
    }

    @Test
    public void validUnsignedIntegers() {

        UnsignedIntegerOneByteDatatype typeOne = new UnsignedIntegerOneByteDatatype();
        assertEquals(typeOne.valueOf("123").getValue(), Long.valueOf(123L));

        UnsignedIntegerTwoBytesDatatype typeTwo = new UnsignedIntegerTwoBytesDatatype();
        assertEquals(typeTwo.valueOf("257").getValue(), Long.valueOf(257L));

        UnsignedIntegerFourBytesDatatype typeFour = new UnsignedIntegerFourBytesDatatype();
        assertEquals(typeFour.valueOf("65536").getValue(), Long.valueOf(65536L));
        assertEquals(typeFour.valueOf("4294967295").getValue(), Long.valueOf(4294967295L));

        // Well, no need to write another test for that
        assertEquals(typeFour.valueOf("4294967295").increment(true).getValue(), Long.valueOf(1));

    }

    @Test(expectedExceptions = InvalidValueException.class)
    public void invalidUnsignedIntegersOne() {
        UnsignedIntegerOneByteDatatype typeOne = new UnsignedIntegerOneByteDatatype();
        typeOne.valueOf("256");
    }

    @Test(expectedExceptions = InvalidValueException.class)
    public void invalidUnsignedIntegersTwo() {
        UnsignedIntegerTwoBytesDatatype typeTwo = new UnsignedIntegerTwoBytesDatatype();
        typeTwo.valueOf("65536");
    }

    @Test
    public void signedIntegers() {

        IntegerDatatype type = new IntegerDatatype(1);
        assert type.isValid(123);
        assert type.isValid(-124);
        assert type.valueOf("123") == 123;
        assert type.valueOf("-124") == -124;
        assert !type.isValid(256);

        type = new IntegerDatatype(2);
        assert type.isValid(257);
        assert type.isValid(-257);
        assert type.valueOf("257") == 257;
        assert type.valueOf("-257") == -257;
        assert !type.isValid(32768);

    }

    @Test
    public void dateAndTime() {
        DateTimeDatatype type = (DateTimeDatatype) Datatype.Builtin.DATE.getDatatype();

        Calendar expexted = Calendar.getInstance();
        expexted.set(Calendar.YEAR, 2010);
        expexted.set(Calendar.MONTH, 10);
        expexted.set(Calendar.DAY_OF_MONTH, 3);
        expexted.set(Calendar.HOUR_OF_DAY, 8);
        expexted.set(Calendar.MINUTE, 9);
        expexted.set(Calendar.SECOND, 10);

        Calendar parsedDate = type.valueOf("2010-11-03");
        assertEquals(parsedDate.get(Calendar.YEAR), expexted.get(Calendar.YEAR));
        assertEquals(parsedDate.get(Calendar.MONTH), expexted.get(Calendar.MONTH));
        assertEquals(parsedDate.get(Calendar.DAY_OF_MONTH), expexted.get(Calendar.DAY_OF_MONTH));
        assertEquals(type.getString(expexted), "2010-11-03");

        type = (DateTimeDatatype) Datatype.Builtin.DATETIME.getDatatype();

        parsedDate = type.valueOf("2010-11-03");
        assertEquals(parsedDate.get(Calendar.YEAR), expexted.get(Calendar.YEAR));
        assertEquals(parsedDate.get(Calendar.MONTH), expexted.get(Calendar.MONTH));
        assertEquals(parsedDate.get(Calendar.DAY_OF_MONTH), expexted.get(Calendar.DAY_OF_MONTH));

        parsedDate = type.valueOf("2010-11-03T08:09:10");
        assertEquals(parsedDate.get(Calendar.YEAR), expexted.get(Calendar.YEAR));
        assertEquals(parsedDate.get(Calendar.MONTH), expexted.get(Calendar.MONTH));
        assertEquals(parsedDate.get(Calendar.DAY_OF_MONTH), expexted.get(Calendar.DAY_OF_MONTH));
        assertEquals(parsedDate.get(Calendar.HOUR_OF_DAY), expexted.get(Calendar.HOUR_OF_DAY));
        assertEquals(parsedDate.get(Calendar.MINUTE), expexted.get(Calendar.MINUTE));
        assertEquals(parsedDate.get(Calendar.SECOND), expexted.get(Calendar.SECOND));

        assertEquals(type.getString(expexted), "2010-11-03T08:09:10");
    }

    @Test
    public void dateAndTimeWithZone() {
        DateTimeDatatype type =
                new DateTimeDatatype(
                        List.of("yyyy-MM-dd", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ssZ"),
                        "yyyy-MM-dd'T'HH:mm:ssZ"
                ) {
                    @Override
                    protected TimeZone getTimeZone() {
                        // Set the "local" timezone to CET for the test
                        return TimeZone.getTimeZone(CET);
                    }
                };

        Calendar expected = Calendar.getInstance();
        expected.setTimeZone(TimeZone.getTimeZone(CET));
        expected.set(Calendar.YEAR, 2010);
        expected.set(Calendar.MONTH, 10);
        expected.set(Calendar.DAY_OF_MONTH, 3);
        expected.set(Calendar.HOUR_OF_DAY, 8);
        expected.set(Calendar.MINUTE, 9);
        expected.set(Calendar.SECOND, 10);

        Calendar parsedDate = type.valueOf("2010-11-03T08:09:10");
        assertEquals(parsedDate.get(Calendar.YEAR), expected.get(Calendar.YEAR));
        assertEquals(parsedDate.get(Calendar.MONTH), expected.get(Calendar.MONTH));
        assertEquals(parsedDate.get(Calendar.DAY_OF_MONTH), expected.get(Calendar.DAY_OF_MONTH));
        assertEquals(parsedDate.get(Calendar.HOUR_OF_DAY), expected.get(Calendar.HOUR_OF_DAY));
        assertEquals(parsedDate.get(Calendar.MINUTE), expected.get(Calendar.MINUTE));
        assertEquals(parsedDate.get(Calendar.SECOND), expected.get(Calendar.SECOND));

        parsedDate = type.valueOf("2010-11-03T08:09:10+0100");
        assertEquals(parsedDate.get(Calendar.YEAR), expected.get(Calendar.YEAR));
        assertEquals(parsedDate.get(Calendar.MONTH), expected.get(Calendar.MONTH));
        assertEquals(parsedDate.get(Calendar.DAY_OF_MONTH), expected.get(Calendar.DAY_OF_MONTH));
        assertEquals(parsedDate.get(Calendar.HOUR_OF_DAY), expected.get(Calendar.HOUR_OF_DAY));
        assertEquals(parsedDate.get(Calendar.MINUTE), expected.get(Calendar.MINUTE));
        assertEquals(parsedDate.get(Calendar.SECOND), expected.get(Calendar.SECOND));
        assertEquals(parsedDate.getTimeZone(), expected.getTimeZone());

        assertEquals(type.getString(expected), "2010-11-03T08:09:10+0100");
    }

    @Test
    public void time() {
        DateTimeDatatype type = (DateTimeDatatype) Datatype.Builtin.TIME.getDatatype();

        Calendar expected = Calendar.getInstance();
        expected.setTime(new Date(0));
        expected.set(Calendar.HOUR_OF_DAY, 8);
        expected.set(Calendar.MINUTE, 9);
        expected.set(Calendar.SECOND, 10);

        Calendar parsedTime = type.valueOf("08:09:10");
        assertEquals(parsedTime.get(Calendar.HOUR_OF_DAY), expected.get(Calendar.HOUR_OF_DAY));
        assertEquals(parsedTime.get(Calendar.MINUTE), expected.get(Calendar.MINUTE));
        assertEquals(parsedTime.get(Calendar.SECOND), expected.get(Calendar.SECOND));
        assertEquals(type.getString(expected), "08:09:10");
    }

    @Test
    public void timeWithZone() {

        DateTimeDatatype type = new DateTimeDatatype(List.of("HH:mm:ssZ", "HH:mm:ss"), "HH:mm:ssZ") {
            @Override
            protected TimeZone getTimeZone() {
                // Set the "local" timezone to CET for the test
                return TimeZone.getTimeZone(CET);
            }
        };

        Calendar expected = Calendar.getInstance();
        expected.setTimeZone(TimeZone.getTimeZone(CET));
        expected.setTime(new Date(0));
        expected.set(Calendar.HOUR_OF_DAY, 8);
        expected.set(Calendar.MINUTE, 9);
        expected.set(Calendar.SECOND, 10);

        assertEquals(type.valueOf("08:09:10").getTimeInMillis(), expected.getTimeInMillis());
        assertEquals(type.valueOf("08:09:10+0100").getTimeInMillis(), expected.getTimeInMillis());
        assertEquals(type.getString(expected), "08:09:10+0100");

    }

    @Test
    public void base64() {
        Base64Datatype type = (Base64Datatype) Datatype.Builtin.BIN_BASE64.getDatatype();
        assert Arrays.equals(type.valueOf("a1b2"), new byte[]{107, 86, -10});
        assert "a1b2".equals(type.getString(new byte[]{107, 86, -10}));
    }

    @Test
    public void simpleCSV() {
        List<String> csv = new CSVString("foo,bar,baz");
        assert csv.size() == 3;
        assert "foo".equals(csv.get(0));
        assert "bar".equals(csv.get(1));
        assert "baz".equals(csv.get(2));
        assert "foo,bar,baz".equals(csv.toString());

        csv = new CSVString("f\\\\oo,b\\,ar,b\\\\az");
        assert csv.size() == 3;
        assertEquals(csv.get(0), "f\\oo");
        assertEquals(csv.get(1), "b,ar");
        assertEquals(csv.get(2), "b\\az");

        List<Boolean> csvBoolean = new CSVBoolean("1,1,0");
        assert csvBoolean.size() == 3;
        assertEquals(csvBoolean.get(0), Boolean.TRUE);
        assertEquals(csvBoolean.get(1), Boolean.TRUE);
        assertEquals(csvBoolean.get(2), Boolean.FALSE);
        assertEquals(csvBoolean.toString(), "1,1,0");
    }

    @Test
    public void parseDLNADoc() {
        DLNADoc doc = DLNADoc.valueOf("DMS-1.50");
        assertEquals(doc.getDevClass(), "DMS");
        assertEquals(doc.getVersion(), DLNADoc.Version.V1_5.toString());
        assertEquals(doc.toString(), "DMS-1.50");

        doc = DLNADoc.valueOf("M-DMS-1.50");
        assertEquals(doc.getDevClass(), "M-DMS");
        assertEquals(doc.getVersion(), DLNADoc.Version.V1_5.toString());
        assertEquals(doc.toString(), "M-DMS-1.50");
    }

    @Test
    public void caseSensitivity() {
        Datatype.Builtin dt = Datatype.Builtin.getByDescriptorName("datetime");
        assert dt != null;
        dt = Datatype.Builtin.getByDescriptorName("dateTime");
        assert dt != null;
        dt = Datatype.Builtin.getByDescriptorName("DATETIME");
        assert dt != null;
    }

    @Test
    public void valueOfDouble() {
        DoubleDatatype dt = (DoubleDatatype)Datatype.Builtin.R8.getDatatype();
        Double d = dt.valueOf("1.23");
        assertEquals(d, 1.23d);
    }

    @Test
    public void valueOfFloat() {
        FloatDatatype dt = (FloatDatatype)Datatype.Builtin.R4.getDatatype();
        Float f = dt.valueOf("1.23456");
        assertEquals(f, 1.23456f);
    }

}
