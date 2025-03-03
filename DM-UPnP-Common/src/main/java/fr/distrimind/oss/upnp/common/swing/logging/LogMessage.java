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
 */ /*
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
 */ /*
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
 package fr.distrimind.oss.upnp.common.swing.logging;

import fr.distrimind.oss.flexilogxml.common.log.LogRecord;
import fr.distrimind.oss.flexilogxml.common.log.Level;

import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

/**
 * @author Christian Bauer
 */
public class LogMessage {

    private final LogRecord logRecord;
    private final String thread = Thread.currentThread().getName();
    private final String source;

    public LogMessage(String message) {
        this(Level.INFO, message);
    }

    public LogMessage(String source, String message) {
        this(Level.INFO, source, message);
    }

    public LogMessage(Level level, String message) {
        this(level, null, message);
    }

    public LogMessage(Level level, String source, String message) {
        this(new LogRecord(level, message), source);
    }
    public LogMessage(LogRecord logRecord) {
        this(logRecord, null);
    }
    public LogMessage(LogRecord logRecord, String source) {
        if (logRecord==null)
            throw new NullPointerException();
        this.logRecord=logRecord;
        this.source=source;
    }

    public Level getLevel() {
        return logRecord.getLevel();
    }

    public Long getCreatedOn() {
        return logRecord.getCreatedOnUTC();
    }

    public String getThread() {
        return thread;
    }

    public String getSource() {
        return source;
    }

    public String getMessage() {
        return logRecord.getMessage();
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS", Locale.ROOT);
        return getLevel() + " - " +
                dateFormat.format(new Date(getCreatedOn())) + " - " +
                getThread() + " : " +
                getSource() + " : " +
                getMessage();
    }
}