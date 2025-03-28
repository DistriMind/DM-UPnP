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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import fr.distrimind.oss.upnp.common.model.types.InvalidValueException;

/**
 *
 * @author Mario Franco
 */
public class BufferInfoType {

    final static Pattern pattern = Pattern.compile("^dejitter=(\\d{1,10})(;CDB=(\\d{1,10});BTM=(0|1|2))?(;TD=(\\d{1,10}))?(;BFR=(0|1))?$", Pattern.CASE_INSENSITIVE);
    private final Long dejitterSize;
    private CodedDataBuffer cdb;
    private Long targetDuration;
    private Boolean fullnessReports;

    public BufferInfoType(Long dejitterSize) {
        this.dejitterSize = dejitterSize;
    }

    public BufferInfoType(Long dejitterSize, CodedDataBuffer cdb,
            Long targetDuration, Boolean fullnessReports) {
        this.dejitterSize = dejitterSize;
        this.cdb = cdb;
        this.targetDuration = targetDuration;
        this.fullnessReports = fullnessReports;
    }

    public static BufferInfoType valueOf(String s) throws InvalidValueException {
        Matcher matcher = pattern.matcher(s);
        if (matcher.matches()) {
            try {
                Long dejitterSize = Long.parseLong(matcher.group(1));
                CodedDataBuffer cdb = null;
                Long targetDuration = null;
                Boolean fullnessReports = null;

                if (matcher.group(2) != null) {
                    cdb = new CodedDataBuffer(Long.parseLong(matcher.group(3)),
                            CodedDataBuffer.TransferMechanism.values()[Integer.parseInt(matcher.group(4))]);
                }
                if (matcher.group(5) != null) {
                    targetDuration = Long.parseLong(matcher.group(6));
                }
                if (matcher.group(7) != null) {
                    fullnessReports = "1".equals(matcher.group(8));
                }
                return new BufferInfoType(dejitterSize, cdb, targetDuration, fullnessReports);
            } catch (NumberFormatException ignored) {
            }
        }
        throw new InvalidValueException("Can't parse BufferInfoType: " + s);
    }

    public String getString() {
        String s = "dejitter=" + dejitterSize.toString();
        if (cdb != null) {
            s += ";CDB=" + cdb.getSize().toString() + ";BTM=" + cdb.getTranfer().ordinal();
        }
        if (targetDuration != null) {
            s += ";TD=" + targetDuration;
        }
        if (fullnessReports != null) {
            s += ";BFR=" + (fullnessReports?"1":"0");
        }
        return s;
    }

    /**
     * @return the dejitter size
     */
    public Long getDejitterSize() {
        return dejitterSize;
    }

    /**
     * @return the cdb
     */
    public CodedDataBuffer getCdb() {
        return cdb;
    }

    /**
     * @return the targetDuration
     */
    public Long getTargetDuration() {
        return targetDuration;
    }

    /**
     * @return the fullnessReports
     */
    public Boolean isFullnessReports() {
        return fullnessReports;
    }
}
