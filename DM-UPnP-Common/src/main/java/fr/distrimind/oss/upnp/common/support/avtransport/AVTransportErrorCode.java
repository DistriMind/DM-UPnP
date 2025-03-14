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

package fr.distrimind.oss.upnp.common.support.avtransport;

/**
 *
 */
public enum AVTransportErrorCode {

    TRANSITION_NOT_AVAILABLE(701, "The immediate transition from current to desired state not supported"),
    NO_CONTENTS(702, "The media does not contain any contents that can be played"),
    READ_ERROR(703, "The media cannot be read"),
    PLAYBACK_FORMAT_NOT_SUPPORTED(704, "The storage format of the currently loaded media is not supported for playback"),
    TRANSPORT_LOCKED(705, "The transport is 'hold locked', e.g. with a keyboard lock"),
    WRITE_ERROR(706, "The media cannot be written"),
    MEDIA_PROTECTED(707, "The media is write-protected or is of a not writable type"),
    RECORD_FORMAT_NOT_SUPPORTED(708, "The storage format of the currently loaded media is not supported for recording"),
    MEDIA_FULL(709, "There is no free space left on the loaded media"),
    SEEKMODE_NOT_SUPPORTED(710, "The specified seek mode is not supported by the device"),
    ILLEGAL_SEEK_TARGET(711, "The specified seek target is not specified in terms of the seek mode, or is not present on the media"),
    PLAYMODE_NOT_SUPPORTED(712, "The specified play mode is not supported by the device"),
    RECORDQUALITYMODE_NOT_SUPPORTED(713, "The specified record quality mode is not supported by the device"),
    ILLEGAL_MIME_TYPE(714, "The specified resource has a MIME-type which is not supported"),
    CONTENT_BUSY(715, "The resource is already being played by other means"),
    RESOURCE_NOT_FOUND(716, "The specified resource cannot be found in the network"),
    INVALID_INSTANCE_ID(718, "The specified instanceID is invalid for this AVTransport");

    private final int code;
    private final String description;

    AVTransportErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AVTransportErrorCode getByCode(int code) {
        for (AVTransportErrorCode errorCode : values()) {
            if (errorCode.getCode() == code)
                return errorCode;
        }
        return null;
    }

}
