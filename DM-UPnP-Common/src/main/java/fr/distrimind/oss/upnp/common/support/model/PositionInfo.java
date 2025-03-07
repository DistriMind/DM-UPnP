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

package fr.distrimind.oss.upnp.common.support.model;

import fr.distrimind.oss.upnp.common.model.ModelUtil;
import fr.distrimind.oss.upnp.common.model.action.ActionArgumentValue;
import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerFourBytes;

import java.util.Map;

/**
 * @author Christian Bauer
 */
public class PositionInfo {

    private UnsignedIntegerFourBytes track = new UnsignedIntegerFourBytes(0);
    private String trackDuration = "00:00:00";
    private String trackMetaData = "NOT_IMPLEMENTED";
    private String trackURI = "";
    private String relTime = "00:00:00";
    private String absTime = "00:00:00"; // TODO: MORE VALUES IN DOMAIN!
    private int relCount = Integer.MAX_VALUE; // Indicates that we don't support this
    private int absCount = Integer.MAX_VALUE;

    public PositionInfo() {
    }

    public PositionInfo(Map<String, ? extends ActionArgumentValue<?>> args) {
        this(
                ((UnsignedIntegerFourBytes) args.get("Track").getValue()).getValue(),
                (String) args.get("TrackDuration").getValue(),
                (String) args.get("TrackMetaData").getValue(),
                (String) args.get("TrackURI").getValue(),
                (String) args.get("RelTime").getValue(),
                (String) args.get("AbsTime").getValue(),
                (Integer) args.get("RelCount").getValue(),
                (Integer) args.get("AbsCount").getValue()
        );
    }

    public PositionInfo(PositionInfo copy, String relTime, String absTime) {
        this.track = copy.track;
        this.trackDuration = copy.trackDuration;
        this.trackMetaData = copy.trackMetaData;
        this.trackURI = copy.trackURI;
        this.relTime = relTime;
        this.absTime = absTime;
        this.relCount = copy.relCount;
        this.absCount = copy.absCount;
    }

    public PositionInfo(PositionInfo copy, long relTimeSeconds, long absTimeSeconds) {
        this.track = copy.track;
        this.trackDuration = copy.trackDuration;
        this.trackMetaData = copy.trackMetaData;
        this.trackURI = copy.trackURI;
        this.relTime = ModelUtil.toTimeString(relTimeSeconds);
        this.absTime = ModelUtil.toTimeString(absTimeSeconds);
        this.relCount = copy.relCount;
        this.absCount = copy.absCount;
    }

    public PositionInfo(long track, String trackDuration, String trackURI,
                        String relTime, String absTime) {
        this.track = new UnsignedIntegerFourBytes(track);
        this.trackDuration = trackDuration;
        this.trackURI = trackURI;
        this.relTime = relTime;
        this.absTime = absTime;
    }

    public PositionInfo(long track, String trackDuration,
                        String trackMetaData, String trackURI,
                        String relTime, String absTime, int relCount, int absCount) {
        this.track = new UnsignedIntegerFourBytes(track);
        this.trackDuration = trackDuration;
        this.trackMetaData = trackMetaData;
        this.trackURI = trackURI;
        this.relTime = relTime;
        this.absTime = absTime;
        this.relCount = relCount;
        this.absCount = absCount;
    }

    public PositionInfo(long track, String trackMetaData, String trackURI) {
        this.track = new UnsignedIntegerFourBytes(track);
        this.trackMetaData = trackMetaData;
        this.trackURI = trackURI;
    }

    public UnsignedIntegerFourBytes getTrack() {
        return track;
    }

    public String getTrackDuration() {
        return trackDuration;
    }

    public String getTrackMetaData() {
        return trackMetaData;
    }

    public String getTrackURI() {
        return trackURI;
    }

    public String getRelTime() {
        return relTime;
    }

    public String getAbsTime() {
        return absTime;
    }

    public int getRelCount() {
        return relCount;
    }

    public int getAbsCount() {
        return absCount;
    }
    
    public void setTrackDuration(String trackDuration) {
       this.trackDuration = trackDuration;
    }

    public void setRelTime(String relTime) {
       this.relTime = relTime;
    }
       
    public long getTrackDurationSeconds() {
        return getTrackDuration() == null ? 0 : ModelUtil.fromTimeString(getTrackDuration());
    }

    public long getTrackElapsedSeconds() {
        return getRelTime() == null || "NOT_IMPLEMENTED".equals(getRelTime()) ? 0 : ModelUtil.fromTimeString(getRelTime());
    }

    public long getTrackRemainingSeconds() {
        return getTrackDurationSeconds() - getTrackElapsedSeconds();
    }

    public int getElapsedPercent() {
        long elapsed = getTrackElapsedSeconds();
        long total = getTrackDurationSeconds();
        if (elapsed == 0 || total == 0) return 0;
        return Double.valueOf(elapsed/((double)total/100)).intValue();
    }


    @Override
    public String toString() {
        return "(PositionInfo) Track: " + getTrack() + " RelTime: " + getRelTime() + " Duration: " + getTrackDuration() + " Percent: " + getElapsedPercent();
    }
}
