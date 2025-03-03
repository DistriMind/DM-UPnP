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

package fr.distrimind.oss.upnp.common.support.contentdirectory.ui;

import fr.distrimind.oss.upnp.common.controlpoint.ActionCallback;
import fr.distrimind.oss.upnp.common.controlpoint.ControlPoint;
import fr.distrimind.oss.upnp.common.model.meta.Service;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;

/**
 * @author Christian Bauer
 */
public class ContentTreeExpandListener implements TreeWillExpandListener {

    final protected ControlPoint controlPoint;
    final protected Service<?, ?, ?> service;
    final protected DefaultTreeModel treeModel;
    final protected ContentBrowseActionCallbackCreator actionCreator;

    public ContentTreeExpandListener(ControlPoint controlPoint,
                                     Service<?, ?, ?> service,
                                     DefaultTreeModel treeModel,
                                     ContentBrowseActionCallbackCreator actionCreator) {
        this.controlPoint = controlPoint;
        this.service = service;
        this.treeModel = treeModel;
        this.actionCreator = actionCreator;
    }

    @Override
	public void treeWillExpand(final TreeExpansionEvent e) throws ExpandVetoException {
        final DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();

        // Remove all "old" children such as the loading/progress messages
        treeNode.removeAllChildren();
        treeModel.nodeStructureChanged(treeNode);

        // Perform the loading in a background thread
        ActionCallback callback =
                actionCreator.createContentBrowseActionCallback(
                        service, treeModel, treeNode
                );
        controlPoint.execute(callback);
    }

    @Override
	public void treeWillCollapse(TreeExpansionEvent e) throws ExpandVetoException {

    }

}
