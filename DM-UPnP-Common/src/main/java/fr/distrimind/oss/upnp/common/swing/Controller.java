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
 package fr.distrimind.oss.upnp.common.swing;

import javax.swing.AbstractButton;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

/**
 * Interface for building a hierarchical controller structure (HMVC).
 * HMVC works with a tree of triads, these triads are a Model (usually several
 * JavaBeans and their binding models for the UI), a View (usually several Swing
 * UI components), and a Controller. This is a basic interface of a controller
 * that has a pointer to a parent controller (can be null if it's the root of the
 * tree) and a collection of subcontrollers (can be empty, usually isn't empty).
 * The hierarchy of controller supports propagation of action execution and
 * propagation of events.
 * If a controllers view is a {@link java.awt.Frame}, you should also register it as a
 * {@link WindowListener}, so that it can properly clean up its state when the
 * window is closed.
 *
 * @author Christian Bauer
 */
public interface Controller<V extends Container> extends ActionListener, WindowListener {

    V getView();

    Controller<? extends Container> getParentController();

    java.util.List<Controller<?>> getSubControllers();

    void dispose();

    <E extends Event<?>> void registerEventListener(Class<E> eventClass, EventListener<E> eventListener);
    <PAYLOAD> void fireEvent(Event<PAYLOAD> event);
    <PAYLOAD> void fireEventGlobal(Event<PAYLOAD> event);
    <PAYLOAD> void fireEvent(Event<PAYLOAD> event, boolean global);

    void registerAction(AbstractButton source, DefaultAction action);
    void registerAction(AbstractButton source, String actionCommand, DefaultAction action);
    void preActionExecute();
    void postActionExecute();
    void failedActionExecute();
    void finalActionExecute();
}
