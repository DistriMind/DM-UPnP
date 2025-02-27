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


package fr.distrimind.oss.upnp.android;

import fr.distrimind.oss.flexilogxml.FlexiLogXML;
import fr.distrimind.oss.flexilogxml.TestGroup;
import fr.distrimind.oss.flexilogxml.Tests;
import fr.distrimind.oss.upnp.android.transport.JDKServerJDKClientTest;
import fr.distrimind.oss.upnp.android.transport.JDKServerUndertowClientTest;
import fr.distrimind.oss.upnp.android.transport.UndertowServerJDKClientTest;
import fr.distrimind.oss.upnp.android.transport.UndertowServerUndertowClientTest;
import fr.distrimind.oss.flexilogxml.log.Level;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AllTests {
	public static List<TestGroup> additionalTestGroups=null;
	public static Tests getTests() {
		List<TestGroup> testGroups=List.of(
				new TestGroup("TestStreamServerClient", List.of(
						JDKServerJDKClientTest.class,
						UndertowServerUndertowClientTest.class,
						UndertowServerJDKClientTest.class,
						JDKServerUndertowClientTest.class
				))
		);
		if (additionalTestGroups!=null)
		{
			List<TestGroup> l=new ArrayList<>();
			l.addAll(testGroups);
			l.addAll(additionalTestGroups);
			testGroups=l;

		}
		return new Tests(testGroups);
	}
	public static void main(String[] args) throws IOException {
		Tests t=getTests();
		File f=new File("./DM-UPnP-Android/src/test/resources/fr/distrimind/oss/upnp/android/AllTestsNG.xml");
		t.saveTestNGToXML(f);
		FlexiLogXML.log(Level.INFO, "XML Test NG file saved into: "+f.getCanonicalPath());
	}
}
