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

package fr.distrimind.oss.upnp.common.support.contentdirectory.callback;

import fr.distrimind.oss.upnp.common.controlpoint.ActionCallback;
import fr.distrimind.oss.upnp.common.model.action.ActionException;
import fr.distrimind.oss.upnp.common.model.action.ActionInvocation;
import fr.distrimind.oss.upnp.common.model.meta.Service;
import fr.distrimind.oss.upnp.common.model.types.ErrorCode;
import fr.distrimind.oss.upnp.common.model.types.UnsignedIntegerFourBytes;
import fr.distrimind.oss.upnp.common.support.contentdirectory.DIDLParser;
import fr.distrimind.oss.upnp.common.support.model.DIDLContent;
import fr.distrimind.oss.upnp.common.support.model.SearchResult;
import fr.distrimind.oss.upnp.common.support.model.SortCriterion;

import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * Invokes a "Search" action, parses the result.
 *
 * @author TK Kocheran &lt;rfkrocktk@gmail.com&gt;
 */
public abstract class Search extends ActionCallback {

    public static final String CAPS_WILDCARD = "*";

    public enum Status {
        NO_CONTENT("No Content"),
        LOADING("Loading..."),
        OK("OK");

        private final String defaultMessage;

        Status(String defaultMessage) {
            this.defaultMessage = defaultMessage;
        }

        public String getDefaultMessage() {
            return this.defaultMessage;
        }
    }

    final private static DMLogger log = Log.getLogger(Search.class);

    /**
     * Search with first result 0 and {@link #getDefaultMaxResults()}, filters with {@link #CAPS_WILDCARD}.
     */
    public Search(Service<?, ?, ?> service, String containerId, String searchCriteria) {
        this(service, containerId, searchCriteria, CAPS_WILDCARD, 0, null);
    }

    /**
     * @param maxResults Can be <code>null</code>, then {@link #getDefaultMaxResults()} is used.
     */
    public Search(Service<?, ?, ?> service, String containerId, String searchCriteria, String filter,
                  long firstResult, Long maxResults, SortCriterion... orderBy) {
        super(new ActionInvocation<>(service.getAction("Search")));

		if (log.isDebugEnabled()) {
            log.debug("Creating browse action for container ID: " + containerId);
		}

		getActionInvocation().setInput("ContainerID", containerId);
        getActionInvocation().setInput("SearchCriteria", searchCriteria);
        getActionInvocation().setInput("Filter", filter);
        getActionInvocation().setInput("StartingIndex", new UnsignedIntegerFourBytes(firstResult));
        getActionInvocation().setInput(
                "RequestedCount",
                new UnsignedIntegerFourBytes(maxResults == null ? getDefaultMaxResults() : maxResults)
        );
        getActionInvocation().setInput("SortCriteria", SortCriterion.toString(orderBy));
    }

    @Override
    public void run() {
        updateStatus(Status.LOADING);
        super.run();
    }

    @Override
    public void success(ActionInvocation<?> actionInvocation) {
        log.debug("Successful search action, reading output argument values");

        SearchResult result = new SearchResult(
                actionInvocation.getOutput("Result").getValue().toString(),
                (UnsignedIntegerFourBytes) actionInvocation.getOutput("NumberReturned").getValue(),
                (UnsignedIntegerFourBytes) actionInvocation.getOutput("TotalMatches").getValue(),
                (UnsignedIntegerFourBytes) actionInvocation.getOutput("UpdateID").getValue());

        boolean proceed = receivedRaw(actionInvocation, result);

        if (proceed && result.getCountLong() > 0 && !result.getResult().isEmpty()) {
            try {
                DIDLParser didlParser = new DIDLParser();
                DIDLContent didl = didlParser.parse(result.getResult());
                received(actionInvocation, didl);
                updateStatus(Status.OK);
            } catch (Exception ex) {
                actionInvocation.setFailure(
                        new ActionException(ErrorCode.ACTION_FAILED, "Can't parse DIDL XML response: " + ex, ex)
                );
                failure(actionInvocation, null);
            }
        } else {
            received(actionInvocation, new DIDLContent());
            updateStatus(Status.NO_CONTENT);
        }
    }

    /**
     * Some media servers will crash if there is no limit on the maximum number of results.
     *
     * @return The default limit, 999.
     */
    public Long getDefaultMaxResults() {
        return 999L;
    }

    public boolean receivedRaw(ActionInvocation<?> actionInvocation, SearchResult searchResult) {
        return true;
    }

    public abstract void received(ActionInvocation<?> actionInvocation, DIDLContent didl);

    public abstract void updateStatus(Status status);
}
