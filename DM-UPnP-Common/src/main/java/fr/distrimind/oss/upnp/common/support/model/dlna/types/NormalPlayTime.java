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

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import fr.distrimind.oss.upnp.common.model.types.InvalidValueException;

/**
 * @author Mario Franco
 */
public class NormalPlayTime {

    public enum Format {

        SECONDS,
        TIME
    }
    final static Pattern pattern = Pattern.compile("^(\\d+):(\\d{1,2}):(\\d{1,2})(\\.(\\d{1,3}))?|(\\d+)(\\.(\\d{1,3}))?$", Pattern.CASE_INSENSITIVE);
    private long milliseconds;

    public NormalPlayTime(long milliseconds) {
        if (milliseconds < 0) {
            throw new InvalidValueException("Invalid parameter milliseconds: " + milliseconds);
        }

        this.milliseconds = milliseconds;
    }

    public NormalPlayTime(long hours, long minutes, long seconds, long milliseconds) throws InvalidValueException {
        if (hours < 0) {
            throw new InvalidValueException("Invalid parameter hours: " + hours);
        }

        if (minutes < 0 || minutes > 59) {
            throw new InvalidValueException("Invalid parameter minutes: " + hours);
        }

        if (seconds < 0 || seconds > 59) {
            throw new InvalidValueException("Invalid parameter seconds: " + hours);
        }
        if (milliseconds < 0 || milliseconds > 999) {
            throw new InvalidValueException("Invalid parameter milliseconds: " + milliseconds);
        }

        this.milliseconds = (hours * 60 * 60 + minutes * 60 + seconds) * 1000 + milliseconds;
    }

    /**
     * @return the milliseconds
     */
    public long getMilliseconds() {
        return milliseconds;
    }

    /**
     * @param milliseconds the milliseconds to set
     */
    public void setMilliseconds(long milliseconds) {
        if (milliseconds < 0) {
            throw new InvalidValueException("Invalid parameter milliseconds: " + milliseconds);
        }

        this.milliseconds = milliseconds;
    }

    public String getString() {
        return getString(Format.SECONDS);
    }

    /**
     * We don't ignore the right zeros in milliseconds, a small compromise 
     *
     */
    public String getString(Format format) {        
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        long ms = milliseconds % 1000;
		if (Objects.requireNonNull(format) == Format.TIME) {
			seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds));
			long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
			long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));
			return String.format(Locale.ROOT, "%d:%02d:%02d.%03d", hours, minutes, seconds, ms);
		}
		return String.format(Locale.ROOT, "%d.%03d", seconds, ms);
	}

    public static NormalPlayTime valueOf(String s) throws InvalidValueException {
        Matcher matcher = pattern.matcher(s);
        if (matcher.matches()) {
            int msMultiplier;
            try {
                if (matcher.group(1) != null) {
                    msMultiplier = (int) Math.pow(10, 3 - matcher.group(5).length());
                    return new NormalPlayTime(
                            Long.parseLong(matcher.group(1)),
                            Long.parseLong(matcher.group(2)),
                            Long.parseLong(matcher.group(3)),
                            Long.parseLong(matcher.group(5))*msMultiplier);
                } else {
                    msMultiplier = (int) Math.pow(10, 3 - matcher.group(8).length());
                    return new NormalPlayTime(
                            Long.parseLong(matcher.group(6)) * 1000 + Long.parseLong(matcher.group(8))*msMultiplier);
                }
            } catch (NumberFormatException ignored) {
            }
        }
        throw new InvalidValueException("Can't parse NormalPlayTime: " + s);
    }
}
