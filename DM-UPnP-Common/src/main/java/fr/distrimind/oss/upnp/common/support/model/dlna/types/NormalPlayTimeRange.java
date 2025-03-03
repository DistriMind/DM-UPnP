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
package fr.distrimind.oss.upnp.common.support.model.dlna.types;

import fr.distrimind.oss.upnp.common.model.types.InvalidValueException;

/**
 *
 * @author Mario Franco
 */
public class NormalPlayTimeRange {

    public static final String PREFIX = "npt=";
    
    private final NormalPlayTime timeStart;
    private final NormalPlayTime timeEnd;
    private NormalPlayTime timeDuration;

    public NormalPlayTimeRange(long timeStart, long timeEnd) {
        this.timeStart = new NormalPlayTime(timeStart);
        this.timeEnd = new NormalPlayTime(timeEnd);
    }

    public NormalPlayTimeRange(NormalPlayTime timeStart, NormalPlayTime timeEnd) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public NormalPlayTimeRange(NormalPlayTime timeStart, NormalPlayTime timeEnd, NormalPlayTime timeDuration) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.timeDuration = timeDuration;
    }

    /**
     * @return the timeStart
     */
    public NormalPlayTime getTimeStart() {
        return timeStart;
    }

    /**
     * @return the timeEnd
     */
    public NormalPlayTime getTimeEnd() {
        return timeEnd;
    }

    /**
     * @return the timeDuration
     */
    public NormalPlayTime getTimeDuration() {
        return timeDuration;
    }

    /**
     * 
     * @return String format of Normal Play Time Range for response message header 
     */
    public String getString() {
        return getString(true);
    }

    /**
     * 
     * @return String format of Normal Play Time Range for response message header 
     */
    public String getString(boolean includeDuration) {
        String s = PREFIX;

        s += timeStart.getString() + "-";
        if (timeEnd != null) {
            s += timeEnd.getString();
        }
        if (includeDuration) {
            s += "/" + (timeDuration != null ? timeDuration.getString() : "*");
        }

        return s;
    }

    public static NormalPlayTimeRange valueOf(String s) throws InvalidValueException {
        return valueOf(s, false);
    }

    @SuppressWarnings("PMD.ImplicitSwitchFallThrough")
    public static NormalPlayTimeRange valueOf(String s, boolean mandatoryTimeEnd) throws InvalidValueException {
        if (s.startsWith(PREFIX)) {
            NormalPlayTime timeStart;
            NormalPlayTime timeEnd = null;
            NormalPlayTime timeDuration = null;
            String[] params = s.substring(PREFIX.length()).split("[-/]");
            switch (params.length) {
                case 3:
                    if (!params[2].isEmpty() && !"*".equals(params[2])) {
                        timeDuration = NormalPlayTime.valueOf(params[2]);
                    }
                case 2:
                    if (!params[1].isEmpty()) {
                        timeEnd = NormalPlayTime.valueOf(params[1]);
                    }
                case 1:
                    if (!params[0].isEmpty() && (!mandatoryTimeEnd || params.length > 1)) {
                        timeStart = NormalPlayTime.valueOf(params[0]);
                        return new NormalPlayTimeRange(timeStart, timeEnd, timeDuration);
                    }
                default:
                    break;
            }
        }
        throw new InvalidValueException("Can't parse NormalPlayTimeRange: " + s);
    }
}
