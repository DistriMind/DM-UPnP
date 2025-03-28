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

import fr.distrimind.oss.upnp.common.binding.annotations.UpnpAction;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpInputArgument;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpOutputArgument;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpService;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpServiceId;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpServiceType;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpStateVariable;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpStateVariables;
import fr.distrimind.oss.upnp.common.model.ModelUtil;
import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerFourBytes;
import fr.distrimind.oss.upnp.common.support.avtransport.lastchange.AVTransportLastChangeParser;
import fr.distrimind.oss.upnp.common.support.avtransport.lastchange.AVTransportVariable;
import fr.distrimind.oss.upnp.common.support.lastchange.LastChange;
import fr.distrimind.oss.upnp.common.support.lastchange.LastChangeDelegator;
import fr.distrimind.oss.upnp.common.support.model.DeviceCapabilities;
import fr.distrimind.oss.upnp.common.support.model.MediaInfo;
import fr.distrimind.oss.upnp.common.support.model.PlayMode;
import fr.distrimind.oss.upnp.common.support.model.PositionInfo;
import fr.distrimind.oss.upnp.common.support.model.RecordMediumWriteStatus;
import fr.distrimind.oss.upnp.common.support.model.RecordQualityMode;
import fr.distrimind.oss.upnp.common.support.model.SeekMode;
import fr.distrimind.oss.upnp.common.support.model.StorageMedium;
import fr.distrimind.oss.upnp.common.support.model.TransportAction;
import fr.distrimind.oss.upnp.common.support.model.TransportInfo;
import fr.distrimind.oss.upnp.common.support.model.TransportSettings;
import fr.distrimind.oss.upnp.common.support.model.TransportState;
import fr.distrimind.oss.upnp.common.support.model.TransportStatus;

import java.beans.PropertyChangeSupport;
import java.net.URI;
import java.util.List;

/**
 * Skeleton of service with "LastChange" eventing support.
 *
 * @author Christian Bauer
 */

@UpnpService(
        serviceId = @UpnpServiceId("AVTransport"),
        serviceType = @UpnpServiceType(value = "AVTransport", version = 1),
        stringConvertibleTypes = LastChange.class
)
@UpnpStateVariables({
        @UpnpStateVariable(
                name = "TransportState",
                sendEvents = false,
                allowedValuesEnum = TransportState.class),
        @UpnpStateVariable(
                name = "TransportStatus",
                sendEvents = false,
                allowedValuesEnum = TransportStatus.class),
        @UpnpStateVariable(
                name = "PlaybackStorageMedium",
                sendEvents = false,
                defaultValue = "NONE",
                allowedValuesEnum = StorageMedium.class),
        @UpnpStateVariable(
                name = "RecordStorageMedium",
                sendEvents = false,
                defaultValue = AbstractAVTransportService.NOT_IMPLEMENTED,
                allowedValuesEnum = StorageMedium.class),
        @UpnpStateVariable(
                name = "PossiblePlaybackStorageMedia",
                sendEvents = false,
                datatype = AbstractAVTransportService.STRING,
                defaultValue = "NETWORK"),
        @UpnpStateVariable(
                name = "PossibleRecordStorageMedia",
                sendEvents = false,
                datatype = AbstractAVTransportService.STRING,
                defaultValue = AbstractAVTransportService.NOT_IMPLEMENTED),
        @UpnpStateVariable( // TODO
                name = "CurrentPlayMode",
                sendEvents = false,
                defaultValue = "NORMAL",
                allowedValuesEnum = PlayMode.class),
        @UpnpStateVariable( // TODO
                name = "TransportPlaySpeed",
                sendEvents = false,
                datatype = AbstractAVTransportService.STRING,
                defaultValue = "1"), // 1, 1/2, 2, -1, 1/10, etc.
        @UpnpStateVariable(
                name = "RecordMediumWriteStatus",
                sendEvents = false,
                defaultValue = AbstractAVTransportService.NOT_IMPLEMENTED,
                allowedValuesEnum = RecordMediumWriteStatus.class),
        @UpnpStateVariable(
                name = "CurrentRecordQualityMode",
                sendEvents = false,
                defaultValue = AbstractAVTransportService.NOT_IMPLEMENTED,
                allowedValuesEnum = RecordQualityMode.class),
        @UpnpStateVariable(
                name = "PossibleRecordQualityModes",
                sendEvents = false,
                datatype = AbstractAVTransportService.STRING,
                defaultValue = AbstractAVTransportService.NOT_IMPLEMENTED),
        @UpnpStateVariable(
                name = "NumberOfTracks",
                sendEvents = false,
                datatype = "ui4",
                defaultValue = "0"),
        @UpnpStateVariable(
                name = "CurrentTrack",
                sendEvents = false,
                datatype = "ui4",
                defaultValue = "0"),
        @UpnpStateVariable(
                name = "CurrentTrackDuration",
                sendEvents = false,
                datatype = AbstractAVTransportService.STRING), // H+:MM:SS[.F+] or H+:MM:SS[.F0/F1]
        @UpnpStateVariable(
                name = "CurrentMediaDuration",
                sendEvents = false,
                datatype = AbstractAVTransportService.STRING,
                defaultValue = "00:00:00"),
        @UpnpStateVariable(
                name = "CurrentTrackMetaData",
                sendEvents = false,
                datatype = AbstractAVTransportService.STRING,
                defaultValue = AbstractAVTransportService.NOT_IMPLEMENTED),
        @UpnpStateVariable(
                name = "CurrentTrackURI",
                sendEvents = false,
                datatype = AbstractAVTransportService.STRING),
        @UpnpStateVariable(
                name = AbstractAVTransportService.AVTRANSPORT_URI,
                sendEvents = false,
                datatype = AbstractAVTransportService.STRING),
        @UpnpStateVariable(
                name = AbstractAVTransportService.AVTRANSPORT_URIMETA_DATA,
                sendEvents = false,
                datatype = AbstractAVTransportService.STRING,
                defaultValue = AbstractAVTransportService.NOT_IMPLEMENTED),
        @UpnpStateVariable(
                name = "NextAVTransportURI",
                sendEvents = false,
                datatype = AbstractAVTransportService.STRING,
                defaultValue = AbstractAVTransportService.NOT_IMPLEMENTED),
        @UpnpStateVariable(
                name = "NextAVTransportURIMetaData",
                sendEvents = false,
                datatype = AbstractAVTransportService.STRING,
                defaultValue = AbstractAVTransportService.NOT_IMPLEMENTED),
        @UpnpStateVariable(
                name = "RelativeTimePosition",
                sendEvents = false,
                datatype = AbstractAVTransportService.STRING), // H+:MM:SS[.F+] or H+:MM:SS[.F0/F1] (in track)
        @UpnpStateVariable(
                name = "AbsoluteTimePosition",
                sendEvents = false,
                datatype = AbstractAVTransportService.STRING), // H+:MM:SS[.F+] or H+:MM:SS[.F0/F1] (in media)
        @UpnpStateVariable(
                name = "RelativeCounterPosition",
                sendEvents = false,
                datatype = "i4",
                defaultValue = "2147483647"), // Max value means not implemented
        @UpnpStateVariable(
                name = "AbsoluteCounterPosition",
                sendEvents = false,
                datatype = "i4",
                defaultValue = "2147483647"), // Max value means not implemented
        @UpnpStateVariable(
                name = "CurrentTransportActions",
                sendEvents = false,
                datatype = AbstractAVTransportService.STRING), // Play, Stop, Pause, Seek, Next, Previous and Record
        @UpnpStateVariable(
                name = "A_ARG_TYPE_SeekMode",
                sendEvents = false,
                allowedValuesEnum = SeekMode.class), // The 'type' of seek we can perform (or should perform)
        @UpnpStateVariable(
                name = "A_ARG_TYPE_SeekTarget",
                sendEvents = false,
                datatype = AbstractAVTransportService.STRING), // The actual seek (offset or whatever) value
        @UpnpStateVariable(
                name = "A_ARG_TYPE_InstanceID",
                sendEvents = false,
                datatype = "ui4")
})
public abstract class AbstractAVTransportService implements LastChangeDelegator {

    public static final String NOT_IMPLEMENTED = "NOT_IMPLEMENTED";
    public static final String STRING = "string";
    public static final String AVTRANSPORT_URI = "AVTransportURI";
    public static final String AVTRANSPORT_URIMETA_DATA = "AVTransportURIMetaData";
    public static final String INSTANCE_ID = "InstanceID";
    @UpnpStateVariable(eventMaximumRateMilliseconds = 200)
    final private LastChange lastChange;
    final protected PropertyChangeSupport propertyChangeSupport;

    protected AbstractAVTransportService() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.lastChange = new LastChange(new AVTransportLastChangeParser());
    }

    protected AbstractAVTransportService(LastChange lastChange) {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.lastChange = lastChange;
    }

    protected AbstractAVTransportService(PropertyChangeSupport propertyChangeSupport) {
        this.propertyChangeSupport = propertyChangeSupport;
        this.lastChange = new LastChange(new AVTransportLastChangeParser());
    }

    protected AbstractAVTransportService(PropertyChangeSupport propertyChangeSupport, LastChange lastChange) {
        this.propertyChangeSupport = propertyChangeSupport;
        this.lastChange = lastChange;
    }

    @Override
    public LastChange getLastChange() {
        return lastChange;
    }

    @Override
    public void appendCurrentState(LastChange lc, UnsignedIntegerFourBytes instanceId) throws Exception {

        MediaInfo mediaInfo = getMediaInfo(instanceId);
        TransportInfo transportInfo = getTransportInfo(instanceId);
        TransportSettings transportSettings = getTransportSettings(instanceId);
        PositionInfo positionInfo = getPositionInfo(instanceId);
        DeviceCapabilities deviceCaps = getDeviceCapabilities(instanceId);

        lc.setEventedValue(
                instanceId,
                new AVTransportVariable.AVTransportURI(URI.create(mediaInfo.getCurrentURI())),
                new AVTransportVariable.AVTransportURIMetaData(mediaInfo.getCurrentURIMetaData()),
                new AVTransportVariable.CurrentMediaDuration(mediaInfo.getMediaDuration()),
                new AVTransportVariable.CurrentPlayMode(transportSettings.getPlayMode()),
                new AVTransportVariable.CurrentRecordQualityMode(transportSettings.getRecQualityMode()),
                new AVTransportVariable.CurrentTrack(positionInfo.getTrack()),
                new AVTransportVariable.CurrentTrackDuration(positionInfo.getTrackDuration()),
                new AVTransportVariable.CurrentTrackMetaData(positionInfo.getTrackMetaData()),
                new AVTransportVariable.CurrentTrackURI(URI.create(positionInfo.getTrackURI())),
                new AVTransportVariable.CurrentTransportActions(getCurrentTransportActions(instanceId)),
                new AVTransportVariable.NextAVTransportURI(URI.create(mediaInfo.getNextURI())),
                new AVTransportVariable.NextAVTransportURIMetaData(mediaInfo.getNextURIMetaData()),
                new AVTransportVariable.NumberOfTracks(mediaInfo.getNumberOfTracks()),
                new AVTransportVariable.PossiblePlaybackStorageMedia(deviceCaps.getPlayMedia()),
                new AVTransportVariable.PossibleRecordQualityModes(deviceCaps.getRecQualityModes()),
                new AVTransportVariable.PossibleRecordStorageMedia(deviceCaps.getRecMedia()),
                new AVTransportVariable.RecordMediumWriteStatus(mediaInfo.getWriteStatus()),
                new AVTransportVariable.RecordStorageMedium(mediaInfo.getRecordMedium()),
                new AVTransportVariable.TransportPlaySpeed(transportInfo.getCurrentSpeed()),
                new AVTransportVariable.TransportState(transportInfo.getCurrentTransportState()),
                new AVTransportVariable.TransportStatus(transportInfo.getCurrentTransportStatus())
        );
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    public static UnsignedIntegerFourBytes getDefaultInstanceID() {
        return new UnsignedIntegerFourBytes(0);
    }

    @UpnpAction
    public abstract void setAVTransportURI(@UpnpInputArgument(name = INSTANCE_ID) UnsignedIntegerFourBytes instanceId,
                                           @UpnpInputArgument(name = "CurrentURI", stateVariable = AVTRANSPORT_URI) String currentURI,
                                           @UpnpInputArgument(name = "CurrentURIMetaData", stateVariable = AVTRANSPORT_URIMETA_DATA) String currentURIMetaData)
            throws AVTransportException;

    @UpnpAction
    public abstract void setNextAVTransportURI(@UpnpInputArgument(name = INSTANCE_ID) UnsignedIntegerFourBytes instanceId,
                                               @UpnpInputArgument(name = "NextURI", stateVariable = AVTRANSPORT_URI) String nextURI,
                                               @UpnpInputArgument(name = "NextURIMetaData", stateVariable = AVTRANSPORT_URIMETA_DATA) String nextURIMetaData)
            throws AVTransportException;

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "NrTracks", stateVariable = "NumberOfTracks", getterName = "getNumberOfTracks"),
            @UpnpOutputArgument(name = "MediaDuration", stateVariable = "CurrentMediaDuration", getterName = "getMediaDuration"),
            @UpnpOutputArgument(name = "CurrentURI", stateVariable = AVTRANSPORT_URI, getterName = "getCurrentURI"),
            @UpnpOutputArgument(name = "CurrentURIMetaData", stateVariable = AVTRANSPORT_URIMETA_DATA, getterName = "getCurrentURIMetaData"),
            @UpnpOutputArgument(name = "NextURI", stateVariable = "NextAVTransportURI", getterName = "getNextURI"),
            @UpnpOutputArgument(name = "NextURIMetaData", stateVariable = "NextAVTransportURIMetaData", getterName = "getNextURIMetaData"),
            @UpnpOutputArgument(name = "PlayMedium", stateVariable = "PlaybackStorageMedium", getterName = "getPlayMedium"),
            @UpnpOutputArgument(name = "RecordMedium", stateVariable = "RecordStorageMedium", getterName = "getRecordMedium"),
            @UpnpOutputArgument(name = "WriteStatus", stateVariable = "RecordMediumWriteStatus", getterName = "getWriteStatus")
    })
    public abstract MediaInfo getMediaInfo(@UpnpInputArgument(name = INSTANCE_ID) UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "CurrentTransportState", stateVariable = "TransportState", getterName = "getCurrentTransportState"),
            @UpnpOutputArgument(name = "CurrentTransportStatus", stateVariable = "TransportStatus", getterName = "getCurrentTransportStatus"),
            @UpnpOutputArgument(name = "CurrentSpeed", stateVariable = "TransportPlaySpeed", getterName = "getCurrentSpeed")
    })
    public abstract TransportInfo getTransportInfo(@UpnpInputArgument(name = INSTANCE_ID) UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "Track", stateVariable = "CurrentTrack", getterName = "getTrack"),
            @UpnpOutputArgument(name = "TrackDuration", stateVariable = "CurrentTrackDuration", getterName = "getTrackDuration"),
            @UpnpOutputArgument(name = "TrackMetaData", stateVariable = "CurrentTrackMetaData", getterName = "getTrackMetaData"),
            @UpnpOutputArgument(name = "TrackURI", stateVariable = "CurrentTrackURI", getterName = "getTrackURI"),
            @UpnpOutputArgument(name = "RelTime", stateVariable = "RelativeTimePosition", getterName = "getRelTime"),
            @UpnpOutputArgument(name = "AbsTime", stateVariable = "AbsoluteTimePosition", getterName = "getAbsTime"),
            @UpnpOutputArgument(name = "RelCount", stateVariable = "RelativeCounterPosition", getterName = "getRelCount"),
            @UpnpOutputArgument(name = "AbsCount", stateVariable = "AbsoluteCounterPosition", getterName = "getAbsCount")
    })
    public abstract PositionInfo getPositionInfo(@UpnpInputArgument(name = INSTANCE_ID) UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "PlayMedia", stateVariable = "PossiblePlaybackStorageMedia", getterName = "getPlayMediaString"),
            @UpnpOutputArgument(name = "RecMedia", stateVariable = "PossibleRecordStorageMedia", getterName = "getRecMediaString"),
            @UpnpOutputArgument(name = "RecQualityModes", stateVariable = "PossibleRecordQualityModes", getterName = "getRecQualityModesString")
    })
    public abstract DeviceCapabilities getDeviceCapabilities(@UpnpInputArgument(name = INSTANCE_ID) UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "PlayMode", stateVariable = "CurrentPlayMode", getterName = "getPlayMode"),
            @UpnpOutputArgument(name = "RecQualityMode", stateVariable = "CurrentRecordQualityMode", getterName = "getRecQualityMode")
    })
    public abstract TransportSettings getTransportSettings(@UpnpInputArgument(name = INSTANCE_ID) UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

    @UpnpAction
    public abstract void stop(@UpnpInputArgument(name = INSTANCE_ID) UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

    @UpnpAction
    public abstract void play(@UpnpInputArgument(name = INSTANCE_ID) UnsignedIntegerFourBytes instanceId,
                              @UpnpInputArgument(name = "Speed", stateVariable = "TransportPlaySpeed") String speed)
            throws AVTransportException;

    @UpnpAction
    public abstract void pause(@UpnpInputArgument(name = INSTANCE_ID) UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

    @UpnpAction
    public abstract void record(@UpnpInputArgument(name = INSTANCE_ID) UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

    @UpnpAction
    public abstract void seek(@UpnpInputArgument(name = INSTANCE_ID) UnsignedIntegerFourBytes instanceId,
                              @UpnpInputArgument(name = "Unit", stateVariable = "A_ARG_TYPE_SeekMode") String unit,
                              @UpnpInputArgument(name = "Target", stateVariable = "A_ARG_TYPE_SeekTarget") String target)
            throws AVTransportException;

    @UpnpAction
    public abstract void next(@UpnpInputArgument(name = INSTANCE_ID) UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

    @UpnpAction
    public abstract void previous(@UpnpInputArgument(name = INSTANCE_ID) UnsignedIntegerFourBytes instanceId)
            throws AVTransportException;

    @UpnpAction
    public abstract void setPlayMode(@UpnpInputArgument(name = INSTANCE_ID) UnsignedIntegerFourBytes instanceId,
                                     @UpnpInputArgument(name = "NewPlayMode", stateVariable = "CurrentPlayMode") String newPlayMode)
            throws AVTransportException;

    @UpnpAction
    public abstract void setRecordQualityMode(@UpnpInputArgument(name = INSTANCE_ID) UnsignedIntegerFourBytes instanceId,
                                              @UpnpInputArgument(name = "NewRecordQualityMode", stateVariable = "CurrentRecordQualityMode") String newRecordQualityMode)
            throws AVTransportException;

    @UpnpAction(name = "GetCurrentTransportActions", out = @UpnpOutputArgument(name = "Actions", stateVariable = "CurrentTransportActions"))
    public String getCurrentTransportActionsString(@UpnpInputArgument(name = INSTANCE_ID) UnsignedIntegerFourBytes instanceId)
            throws AVTransportException {
        try {
            return ModelUtil.toCommaSeparatedList(getCurrentTransportActions(instanceId));
        } catch (Exception ex) {
            return ""; // TODO: Empty string is not defined in spec but seems reasonable for no available action?
        }
    }

    protected abstract List<TransportAction> getCurrentTransportActions(UnsignedIntegerFourBytes instanceId) throws Exception;
}
