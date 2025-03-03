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
package fr.distrimind.oss.upnp.common.support.contentdirectory;

import fr.distrimind.oss.upnp.common.binding.annotations.UpnpAction;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpInputArgument;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpOutputArgument;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpService;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpServiceId;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpServiceType;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpStateVariable;
import fr.distrimind.oss.upnp.common.binding.annotations.UpnpStateVariables;
import fr.distrimind.oss.upnp.common.model.types.ErrorCode;
import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerFourBytes;
import fr.distrimind.oss.upnp.common.model.types.csv.CSV;
import fr.distrimind.oss.upnp.common.model.types.csv.CSVString;
import fr.distrimind.oss.upnp.common.support.avtransport.AbstractAVTransportService;
import fr.distrimind.oss.upnp.common.support.model.BrowseFlag;
import fr.distrimind.oss.upnp.common.support.model.BrowseResult;
import fr.distrimind.oss.upnp.common.support.model.DIDLContent;
import fr.distrimind.oss.upnp.common.support.model.SortCriterion;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple ContentDirectory service skeleton.
 * <p>
 * Only state variables and actions required by <em>ContentDirectory:1</em>
 * (not the optional ones) are implemented.
 * </p>
 *
 * @author Alessio Gaeta
 * @author Christian Bauer
 */

@UpnpService(
        serviceId = @UpnpServiceId("ContentDirectory"),
        serviceType = @UpnpServiceType(value = "ContentDirectory", version = 1)
)

@UpnpStateVariables({
                            @UpnpStateVariable(
                                    name = "A_ARG_TYPE_ObjectID",
                                    sendEvents = false,
                                    datatype = AbstractAVTransportService.STRING),
                            @UpnpStateVariable(
                                    name = "A_ARG_TYPE_Result",
                                    sendEvents = false,
                                    datatype = AbstractAVTransportService.STRING),
                            @UpnpStateVariable(
                                    name = "A_ARG_TYPE_BrowseFlag",
                                    sendEvents = false,
                                    datatype = AbstractAVTransportService.STRING,
                                    allowedValuesEnum = BrowseFlag.class),
                            @UpnpStateVariable(
                                    name = "A_ARG_TYPE_Filter",
                                    sendEvents = false,
                                    datatype = AbstractAVTransportService.STRING),
                            @UpnpStateVariable(
                                    name = "A_ARG_TYPE_SortCriteria",
                                    sendEvents = false,
                                    datatype = AbstractAVTransportService.STRING),
                            @UpnpStateVariable(
                                    name = "A_ARG_TYPE_Index",
                                    sendEvents = false,
                                    datatype = "ui4"),
                            @UpnpStateVariable(
                                    name = AbstractContentDirectoryService.A_ARG_TYPE_COUNT,
                                    sendEvents = false,
                                    datatype = "ui4"),
                            @UpnpStateVariable(
                                    name = "A_ARG_TYPE_UpdateID",
                                    sendEvents = false,
                                    datatype = "ui4"),
                            @UpnpStateVariable(
                                    name = "A_ARG_TYPE_URI",
                                    sendEvents = false,
                                    datatype = "uri"),
                            @UpnpStateVariable(
                                    name = "A_ARG_TYPE_SearchCriteria",
                                    sendEvents = false,
                                    datatype = AbstractAVTransportService.STRING)
                    })
@SuppressWarnings("PMD.LooseCoupling")
public abstract class AbstractContentDirectoryService {

    public static final String CAPS_WILDCARD = "*";
    public static final String A_ARG_TYPE_COUNT = "A_ARG_TYPE_Count";

    @UpnpStateVariable(sendEvents = false)
    final private CSV<String> searchCapabilities;

    @UpnpStateVariable(sendEvents = false)
    final private CSV<String> sortCapabilities;

    @UpnpStateVariable(
            sendEvents = true,
            defaultValue = "0",
            eventMaximumRateMilliseconds = 200
    )
    private final UnsignedIntegerFourBytes systemUpdateID = new UnsignedIntegerFourBytes(0);

    final protected PropertyChangeSupport propertyChangeSupport;

    protected AbstractContentDirectoryService() {
        this(new ArrayList<>(), new ArrayList<>(), null);
    }

    protected AbstractContentDirectoryService(List<String> searchCapabilities, List<String> sortCapabilities) {
        this(searchCapabilities, sortCapabilities, null);
    }

    protected AbstractContentDirectoryService(List<String> searchCapabilities, List<String> sortCapabilities,
                                              PropertyChangeSupport propertyChangeSupport) {
        this.propertyChangeSupport = propertyChangeSupport != null ? propertyChangeSupport : new PropertyChangeSupport(this);
        this.searchCapabilities = new CSVString();
        this.searchCapabilities.addAll(searchCapabilities);
        this.sortCapabilities = new CSVString();
        this.sortCapabilities.addAll(sortCapabilities);
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "SearchCaps"))
    public CSV<String> getSearchCapabilities() {
        return searchCapabilities;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "SortCaps"))
    public CSV<String> getSortCapabilities() {
        return sortCapabilities;
    }

    @UpnpAction(out = @UpnpOutputArgument(name = "Id"))
    synchronized public UnsignedIntegerFourBytes getSystemUpdateID() {
        return systemUpdateID;
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    /**
     * Call this method after making changes to your content directory.
     * <p>
     * This will notify clients that their view of the content directory is potentially
     * outdated and has to be refreshed.
   
     */
    synchronized protected void changeSystemUpdateID() {
        Long oldUpdateID = getSystemUpdateID().getValue();
        systemUpdateID.increment(true);
        getPropertyChangeSupport().firePropertyChange(
                "SystemUpdateID",
                oldUpdateID,
                getSystemUpdateID().getValue()
        );
    }

    @UpnpAction(out = {
            @UpnpOutputArgument(name = "Result",
                                stateVariable = "A_ARG_TYPE_Result",
                                getterName = "getResult"),
            @UpnpOutputArgument(name = "NumberReturned",
                                stateVariable = A_ARG_TYPE_COUNT,
                                getterName = "getCount"),
            @UpnpOutputArgument(name = "TotalMatches",
                                stateVariable = A_ARG_TYPE_COUNT,
                                getterName = "getTotalMatches"),
            @UpnpOutputArgument(name = "UpdateID",
                                stateVariable = "A_ARG_TYPE_UpdateID",
                                getterName = "getContainerUpdateID")
    })
    public BrowseResult browse(
            @UpnpInputArgument(name = "ObjectID", aliases = "ContainerID") String objectId,
            @UpnpInputArgument(name = "BrowseFlag") String browseFlag,
            @UpnpInputArgument(name = "Filter") String filter,
            @UpnpInputArgument(name = "StartingIndex", stateVariable = "A_ARG_TYPE_Index") UnsignedIntegerFourBytes firstResult,
            @UpnpInputArgument(name = "RequestedCount", stateVariable = A_ARG_TYPE_COUNT) UnsignedIntegerFourBytes maxResults,
            @UpnpInputArgument(name = "SortCriteria") String orderBy)
            throws ContentDirectoryException {

        List<SortCriterion> orderByCriteria;
        try {
            orderByCriteria = SortCriterion.valueOf(orderBy);
        } catch (Exception ex) {
            throw new ContentDirectoryException(ContentDirectoryErrorCode.UNSUPPORTED_SORT_CRITERIA, ex.toString());
        }

        try {
            return browse(
                    objectId,
                    BrowseFlag.valueOrNullOf(browseFlag),
                    filter,
                    firstResult.getValue(), maxResults.getValue(),
                    orderByCriteria
            );
        } catch (ContentDirectoryException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ContentDirectoryException(ErrorCode.ACTION_FAILED, ex.toString());
        }
    }

    /**
     * Implement this method to implement browsing of your content.
     * <p>
     * This is a required action defined by <em>ContentDirectory:1</em>.
   
     * <p>
     * You should wrap any exception into a {@link ContentDirectoryException}, so a property
     * error message can be returned to control points.
   
     */
    public abstract BrowseResult browse(String objectID, BrowseFlag browseFlag,
                                        String filter,
                                        long firstResult, long maxResults,
                                        List<SortCriterion> orderBy) throws ContentDirectoryException;


    @UpnpAction(out = {
            @UpnpOutputArgument(name = "Result",
                                stateVariable = "A_ARG_TYPE_Result",
                                getterName = "getResult"),
            @UpnpOutputArgument(name = "NumberReturned",
                                stateVariable = A_ARG_TYPE_COUNT,
                                getterName = "getCount"),
            @UpnpOutputArgument(name = "TotalMatches",
                                stateVariable = A_ARG_TYPE_COUNT,
                                getterName = "getTotalMatches"),
            @UpnpOutputArgument(name = "UpdateID",
                                stateVariable = "A_ARG_TYPE_UpdateID",
                                getterName = "getContainerUpdateID")
    })
    public BrowseResult search(
            @UpnpInputArgument(name = "ContainerID", stateVariable = "A_ARG_TYPE_ObjectID") String containerId,
            @UpnpInputArgument(name = "SearchCriteria") String searchCriteria,
            @UpnpInputArgument(name = "Filter") String filter,
            @UpnpInputArgument(name = "StartingIndex", stateVariable = "A_ARG_TYPE_Index") UnsignedIntegerFourBytes firstResult,
            @UpnpInputArgument(name = "RequestedCount", stateVariable = A_ARG_TYPE_COUNT) UnsignedIntegerFourBytes maxResults,
            @UpnpInputArgument(name = "SortCriteria") String orderBy)
            throws ContentDirectoryException {

        List<SortCriterion> orderByCriteria;
        try {
            orderByCriteria = SortCriterion.valueOf(orderBy);
        } catch (Exception ex) {
            throw new ContentDirectoryException(ContentDirectoryErrorCode.UNSUPPORTED_SORT_CRITERIA, ex.toString());
        }

        try {
            return search(
                    containerId,
                    searchCriteria,
                    filter,
                    firstResult.getValue(), maxResults.getValue(),
                    orderByCriteria
            );
        } catch (ContentDirectoryException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ContentDirectoryException(ErrorCode.ACTION_FAILED, ex.toString());
        }
    }

    /**
     * Override this method to implement searching of your content.
     * <p>
     * The default implementation returns an empty result.
   
     */
    public BrowseResult search(String containerId, String searchCriteria, String filter,
                               long firstResult, long maxResults, List<SortCriterion> orderBy) throws ContentDirectoryException {

        try {
            return new BrowseResult(new DIDLParser().generate(new DIDLContent()), 0, 0);
        } catch (Exception ex) {
            throw new ContentDirectoryException(ErrorCode.ACTION_FAILED, ex.toString());
        }
    }
}
