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

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/*
Taken from: http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob_plain;f=core/java/com/android/internal/logging/AndroidHandler.java;hb=c2ad241504fcaa12d4579d3b0b4038d1ca8d08c9
 */
public class FixedAndroidLogHandler extends Handler {
    /**
     * Holds the formatter for all Android log handlers.
     */
    private static final Formatter THE_FORMATTER = new Formatter() {
        @Override
        public String format(LogRecord r) {
            Throwable thrown = r.getThrown();
            if (thrown != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                sw.write(r.getMessage());
                sw.write("\n");
                thrown.printStackTrace(pw);
                pw.flush();
                return sw.toString();
            } else {
                return r.getMessage();
            }
        }
    };

    /**
     * Constructs a new instance of the Android log handler.
     */
    public FixedAndroidLogHandler() {
        setFormatter(THE_FORMATTER);
    }

    @Override
    public final void setFormatter(Formatter newFormatter) throws SecurityException {
        super.setFormatter(newFormatter);
    }

    @Override
    public void close() {
        // No need to close, but must implement abstract method.
    }

    @Override
    public void flush() {
        // No need to flush, but must implement abstract method.
    }

    @Override
    public void publish(LogRecord record) {
        try {
            int level = getAndroidLevel(record.getLevel());
            String tag = getTag(record);

            /* ############################################################################################

            Instead of using the perfectly fine java.util.logging API for setting the
            loggable levels, this call relies on a totally obscure "local.prop" file which you have to place on
            your device. By default, if you do not have that file and if you do not execute some magic
            "setprop" commands on your device, only INFO/WARN/ERROR is loggable. So whatever you do with
            java.util.logging.Logger.setLevel(...) doesn't have any effect. The debug messages might arrive
            here, but they are dropped because you _also_ have to set the Android internal logging level with
            the aforementioned magic switches.

            Also, consider that you have to understand how a JUL logger name is mapped to the "tag" of
            the Android log. Basically, the whole cutting and cropping procedure above is what you have to
            memorize if you want to log with JUL and configure Android for debug output.

            I actually admire the pure evil of this setup, even Mr. Ceki can learn something!

            Commenting out these lines makes it all work as expected:

            if (!Log.isLoggable(tag, level)) {
                return;
            }

            ############################################################################################### */

            String message = getFormatter().format(record);
            Log.println(level, tag, message);
        } catch (RuntimeException e) {
            Log.e("AndroidHandler", "Error logging message.", e);
        }
    }

    private static String getTag(LogRecord record) {
        String tag = record.getLoggerName();

        if (tag == null) {
            // Anonymous logger.
            tag = "null";
        } else {
            // Tags must be <= 23 characters.
            int length = tag.length();
            if (length > 23) {
                // Most loggers use the full class name. Try dropping the
                // package.
                int lastPeriod = tag.lastIndexOf(".");
                if (length - lastPeriod - 1 <= 23) {
                    tag = tag.substring(lastPeriod + 1);
                } else {
                    // Use last 23 chars.
                    tag = tag.substring(tag.length() - 23);
                }
            }
        }
        return tag;
    }

    /**
     * Converts a {@link java.util.logging.Logger} logging level into an Android one.
     *
     * @param level The {@link java.util.logging.Logger} logging level.
     *
     * @return The resulting Android logging level.
     */
    static int getAndroidLevel(Level level) {
        int value = level.intValue();
        if (value >= 1000) { // SEVERE
            return Log.ERROR;
        } else if (value >= 900) { // WARNING
            return Log.WARN;
        } else if (value >= 800) { // INFO
            return Log.INFO;
        } else {
            return Log.DEBUG;
        }
    }

}