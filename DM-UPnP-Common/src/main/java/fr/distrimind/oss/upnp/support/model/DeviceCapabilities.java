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

package fr.distrimind.oss.upnp.support.model;

import fr.distrimind.oss.upnp.model.ModelUtil;
import fr.distrimind.oss.upnp.model.action.ActionArgumentValue;

import java.util.List;
import java.util.Map;

/**
 * @author Christian Bauer
 */
public class DeviceCapabilities {

    private final List<StorageMedium> playMedia;
    private List<StorageMedium> recMedia = List.of(StorageMedium.NOT_IMPLEMENTED);
    private List<RecordQualityMode> recQualityModes = List.of(RecordQualityMode.NOT_IMPLEMENTED);

    public DeviceCapabilities(Map<String, ? extends ActionArgumentValue<?>> args) {
        this(
                StorageMedium.valueOfCommaSeparatedList((String) args.get("PlayMedia").getValue()),
                StorageMedium.valueOfCommaSeparatedList((String) args.get("RecMedia").getValue()),
                RecordQualityMode.valueOfCommaSeparatedList((String) args.get("RecQualityModes").getValue())
        );
    }

    public DeviceCapabilities(List<StorageMedium> playMedia) {
        this.playMedia = playMedia;
    }

    public DeviceCapabilities(List<StorageMedium> playMedia, List<StorageMedium> recMedia, List<RecordQualityMode> recQualityModes) {
        this.playMedia = playMedia;
        this.recMedia = recMedia;
        this.recQualityModes = recQualityModes;
    }

    public List<StorageMedium> getPlayMedia() {
        return playMedia;
    }

    public List<StorageMedium> getRecMedia() {
        return recMedia;
    }

    public List<RecordQualityMode> getRecQualityModes() {
        return recQualityModes;
    }

    public String getPlayMediaString() {
        return ModelUtil.toCommaSeparatedList(playMedia);
    }

    public String getRecMediaString() {
        return ModelUtil.toCommaSeparatedList(recMedia);
    }

    public String getRecQualityModesString() {
        return ModelUtil.toCommaSeparatedList(recQualityModes);
    }
}
