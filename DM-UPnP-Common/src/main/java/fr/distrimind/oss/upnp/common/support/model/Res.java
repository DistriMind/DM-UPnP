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

import fr.distrimind.oss.upnp.common.util.MimeType;

import java.net.URI;

/**
 * @author Christian Bauer
 */
public class Res {

    protected URI importUri;
    protected ProtocolInfo protocolInfo;
    protected Long size;
    protected String duration;
    protected Long bitrate;
    protected Long sampleFrequency;
    protected Long bitsPerSample;
    protected Long nrAudioChannels;
    protected Long colorDepth;
    protected String protection;
    protected String resolution;

    protected String value;

    public Res() {
    }

    public Res(String httpGetMimeType, Long size, String duration, Long bitrate, String value) {
        this(new ProtocolInfo(Protocol.HTTP_GET, ProtocolInfo.WILDCARD, httpGetMimeType, ProtocolInfo.WILDCARD), size, duration, bitrate, value);
    }
    
    public Res(MimeType httpGetMimeType, Long size, String duration, Long bitrate, String value) {
        this(new ProtocolInfo(httpGetMimeType), size, duration, bitrate, value);
    }

    public Res(MimeType httpGetMimeType, Long size, String value) {
        this(new ProtocolInfo(httpGetMimeType), size, value);
    }

    public Res(ProtocolInfo protocolInfo, Long size, String value) {
        this.protocolInfo = protocolInfo;
        this.size = size;
        this.value = value;
    }

    public Res(ProtocolInfo protocolInfo, Long size, String duration, Long bitrate, String value) {
        this.protocolInfo = protocolInfo;
        this.size = size;
        this.duration = duration;
        this.bitrate = bitrate;
        this.value = value;
    }

    public Res(URI importUri, ProtocolInfo protocolInfo, Long size, String duration, Long bitrate, Long sampleFrequency, Long bitsPerSample, Long nrAudioChannels, Long colorDepth, String protection, String resolution, String value) {
        this.importUri = importUri;
        this.protocolInfo = protocolInfo;
        this.size = size;
        this.duration = duration;
        this.bitrate = bitrate;
        this.sampleFrequency = sampleFrequency;
        this.bitsPerSample = bitsPerSample;
        this.nrAudioChannels = nrAudioChannels;
        this.colorDepth = colorDepth;
        this.protection = protection;
        this.resolution = resolution;
        this.value = value;
    }

    public URI getImportUri() {
        return importUri;
    }

    public void setImportUri(URI importUri) {
        this.importUri = importUri;
    }

    public ProtocolInfo getProtocolInfo() {
        return protocolInfo;
    }

    public void setProtocolInfo(ProtocolInfo protocolInfo) {
        this.protocolInfo = protocolInfo;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Long getBitrate() {
        return bitrate;
    }

    public void setBitrate(Long bitrate) {
        this.bitrate = bitrate;
    }

    public Long getSampleFrequency() {
        return sampleFrequency;
    }

    public void setSampleFrequency(Long sampleFrequency) {
        this.sampleFrequency = sampleFrequency;
    }

    public Long getBitsPerSample() {
        return bitsPerSample;
    }

    public void setBitsPerSample(Long bitsPerSample) {
        this.bitsPerSample = bitsPerSample;
    }

    public Long getNrAudioChannels() {
        return nrAudioChannels;
    }

    public void setNrAudioChannels(Long nrAudioChannels) {
        this.nrAudioChannels = nrAudioChannels;
    }

    public Long getColorDepth() {
        return colorDepth;
    }

    public void setColorDepth(Long colorDepth) {
        this.colorDepth = colorDepth;
    }

    public String getProtection() {
        return protection;
    }

    public void setProtection(String protection) {
        this.protection = protection;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setResolution(int x, int y) {
        this.resolution = x + "x" + y;
    }

    public int getResolutionX() {
        return getResolution() != null && getResolution().split("x").length == 2
                ? Integer.parseInt(getResolution().split("x")[0])
                : 0;
    }

    public int getResolutionY() {
        return getResolution() != null && getResolution().split("x").length == 2
                ? Integer.parseInt(getResolution().split("x")[1])
                : 0;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
