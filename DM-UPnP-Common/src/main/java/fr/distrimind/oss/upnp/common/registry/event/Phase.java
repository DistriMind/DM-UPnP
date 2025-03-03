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

package fr.distrimind.oss.upnp.common.registry.event;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Christian Bauer
 */

public class Phase {

    public static AnnotationLiteral<Alive> ALIVE = new AnnotationLiteral<>() {
		private static final long serialVersionUID = 1L;
	};

	public static AnnotationLiteral<Complete> COMPLETE = new AnnotationLiteral<>() {
		private static final long serialVersionUID = 1L;
	};

	public static AnnotationLiteral<Byebye> BYEBYE = new AnnotationLiteral<>() {
		private static final long serialVersionUID = 1L;
	};

	public static AnnotationLiteral<Updated> UPDATED = new AnnotationLiteral<>() {
		private static final long serialVersionUID = 1L;
	};


    @Qualifier
    @Target({FIELD, PARAMETER})
    @Retention(RUNTIME)
	public @interface Alive {

    }

    @Qualifier
    @Target({FIELD, PARAMETER})
    @Retention(RUNTIME)
	public @interface Complete {

    }

    @Qualifier
    @Target({FIELD, PARAMETER})
    @Retention(RUNTIME)
	public @interface Byebye {

    }

    @Qualifier
    @Target({FIELD, PARAMETER})
    @Retention(RUNTIME)
	public @interface Updated {

    }

}
