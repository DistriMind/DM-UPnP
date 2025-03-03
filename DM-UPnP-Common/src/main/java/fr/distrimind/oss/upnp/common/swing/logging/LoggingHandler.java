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

import fr.distrimind.oss.flexilogxml.log.Handler;
import fr.distrimind.oss.flexilogxml.log.LogRecord;


 /**
 * @author Christian Bauer
 */
public abstract class LoggingHandler implements Handler {

    public int sourcePathElements = 3;

    public LoggingHandler() {
    }

    public LoggingHandler(int sourcePathElements) {
        this.sourcePathElements = sourcePathElements;
    }

    @Override
    public void pushNewLog(LogRecord logRecord) {
        LogMessage logMessage = new LogMessage(
                logRecord,
                getSource(logRecord)
        );

        log(logMessage);
    }


    protected String getSource(LogRecord record) {
        return "";
        /*StringBuilder sb = new StringBuilder(180);
        String[] split = record.getSourceClassName().split("\\.");
        if (split.length > sourcePathElements) {
            split = Arrays.copyOfRange(split, split.length-sourcePathElements, split.length);
        }
        for (String s : split) {
            sb.append(s).append(".");
        }
        sb.append(record.getSourceMethodName());
        return sb.toString();*/
    }

    protected abstract void log(LogMessage msg);

}