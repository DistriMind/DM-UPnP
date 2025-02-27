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

package fr.distrimind.oss.upnp.model.action;

import fr.distrimind.oss.upnp.model.types.ErrorCode;

/**
 * Thrown (encapsulated in {@link ActionInvocation}) when an action execution failed.
 *
 * @author Christian Bauer
 */
public class ActionException extends Exception {

    private static final long serialVersionUID = 1L;
    private final int errorCode;

    public ActionException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ActionException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ActionException(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getDescription());
    }

    public ActionException(ErrorCode errorCode, String message) {
        this(errorCode, message, true);
    }

    public ActionException(ErrorCode errorCode, String message, boolean concatMessages) {
        this(errorCode.getCode(), concatMessages ? (errorCode.getDescription() + ". " + message + ".") : message);
    }

    public ActionException(ErrorCode errorCode, String message, Throwable cause) {
        this(errorCode.getCode(), errorCode.getDescription() + ". " + message + ".", cause);
    }

    public int getErrorCode() {
        return errorCode;
    }
}