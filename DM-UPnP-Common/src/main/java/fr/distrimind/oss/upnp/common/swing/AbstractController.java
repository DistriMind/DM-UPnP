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
import javax.swing.JFrame;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.upnp.common.Log;

/**
 * @author Christian Bauer
 */
public class AbstractController<V extends Container> implements Controller<V> {

    final private static DMLogger log = Log.getLogger(AbstractController.class);

    private V view;
    private Controller<? extends Container> parentController;
    private final java.util.List<Controller<?>> subControllers = new ArrayList<>();
    private final Map<String, DefaultAction> actions = new HashMap<>();
    private final Map<Class<?>, List<EventListener<?>>> eventListeners = new HashMap<>();

    /**
     * Subclass is using the given view but is a root controller (e.g. Applet view)
     *
     *
     */
    public AbstractController(V view) {
        this.view = view;
    }

    /**
     * Subclass wants to control own view and is root controller.
     */
    public AbstractController() {
    }

    /**
     * Subclass wants to control own view and is a subcontroller.
     *
     *
     */
    public AbstractController(Controller<V> parentController) {
        this(null, parentController);
    }

    /**
     * Subclass is completely dependent on the given view and is a subcontroller.
     *
     *
     */
    public AbstractController(V view, Controller<? extends Container> parentController) {
        this.view = view;

        // Check if this is a subcontroller or a root controller
        if (parentController != null) {
            this.parentController = parentController;
            parentController.getSubControllers().add(this);
        }
    }

    @Override
	public V getView() {
        return view;
    }

    @Override
	public Controller<? extends Container> getParentController() {
        return parentController;
    }

    @Override
	public java.util.List<Controller<?>> getSubControllers() {
        return subControllers;
    }


    /**
     * This method disposes all subcontrollers first, recursively. Disposing a controller means it
     * will no longer be attached to its parent controller.
     */
    @Override
	public void dispose() {
        log.debug("Disposing controller");
        Iterator<Controller<?>> it = subControllers.iterator();
        while (it.hasNext()) {
            Controller<?> subcontroller = it.next();
            subcontroller.dispose();
            it.remove();
        }
    }

    /**
     * Register an action that can be executed by this controller.
     *
     * @param source The prepared (== has action command assigned) action trigger source
     * @param action An actual action implementation.
     */
    @Override
	public void registerAction(AbstractButton source, DefaultAction action) {
        source.removeActionListener(this);
        source.addActionListener(this);
        this.actions.put(source.getActionCommand(), action);
    }

    /**
     * Register an action that can be executed by this controller.
     *
     * @param source        The source component, this method sets action command and registers the controller as listener.
     * @param actionCommand The action command, used as a key when registering and executing actions.
     * @param action        An actual action implementation.
     */
    @Override
	public void registerAction(AbstractButton source, String actionCommand, DefaultAction action) {
        source.setActionCommand(actionCommand);
        registerAction(source, action);
    }

    public void deregisterAction(String actionCommand) {
        this.actions.remove(actionCommand);
    }

    /**
     * Register an event listener that is being executed when an event is intercepted by this controller.
     *
     * @param eventClass    The actual event class these listeners is interested in.
     * @param eventListener The listener implementation.
     */
    @Override
    public <E extends Event<?>> void registerEventListener(Class<E> eventClass, EventListener<E> eventListener) {
		if (log.isDebugEnabled()) {
            log.debug("Registering listener: " + eventListener + " for event type: " + eventClass.getName());
		}
		java.util.List<EventListener<?>> listenersForEvent = eventListeners.get(eventClass);
        if (listenersForEvent == null) {
            listenersForEvent = new ArrayList<>();
        }
        listenersForEvent.add(eventListener);
        eventListeners.put(eventClass, listenersForEvent);
    }

    /**
     * Fire an event and pass it into the hierarchy of controllers.

     * The event is propagated only to the controller instance and its subcontrollers, not upwards in the hierarchy.
     *
     * @param event The event to be propagated.
     */
    @Override
    public <PAYLOAD> void fireEvent(Event<PAYLOAD> event) {
        fireEvent(event, false);
    }

    /**
     * Fire an event and pass it into the hierarchy of controllers.

     * The event is propagated to the controller instance, its subcontrollers, and upwards into the controller
     * hierarchy. This operation effectively propagates the event to every controller in the whole hierarchy.
     *
     * @param event The event to be propagated.
     */
    @Override
	public <PAYLOAD> void fireEventGlobal(Event<PAYLOAD> event) {
        fireEvent(event, true);
    }

    @Override
	public <PAYLOAD> void fireEvent(Event<PAYLOAD> event, boolean global) {
        if (!event.alreadyFired(this)) {
            log.trace("Event has not been fired already");
            if (eventListeners.get(event.getClass()) != null) {
				if (log.isTraceEnabled()) {
					log.trace("Have listeners for this type of event: " + eventListeners.get(event.getClass()));
				}
				for (EventListener<?> eventListener : eventListeners.get(event.getClass())) {
					if (log.isDebugEnabled()) {
						log.debug("Processing event: " + event.getClass().getName() + " with listener: " + eventListener.getClass().getName());
					}
					eventListener.handleUntypedEvent(event);
                }
            }
            event.addFiredInController(this);
			if (log.isDebugEnabled()) {
				log.debug("Passing event: " + event.getClass().getName() + " DOWN in the controller hierarchy");
			}
			for (Controller<?> subController : subControllers) subController.fireEvent(event, global);
        } else {
            log.trace("Event already fired here, ignoring...");
        }
        if (getParentController() != null
                && !event.alreadyFired(getParentController())
                && global) {
			if (log.isDebugEnabled()) {
				log.debug("Passing event: " + event.getClass().getName() + " UP in the controller hierarchy");
			}
			getParentController().fireEvent(event, true);
        } else {
            log.trace("Event does not propagate up the tree from here");
        }
    }


    /**
     * Executes an action if it has been registered for this controller, otherwise passes it up the chain.

     * This method extracts the source of the action (an <code>AbstractButton</code>) and gets the action
     * command. If the controller has this command registered, the registered action is executed. Otherwise,
     * the action is passed upwards in the hierarchy of controllers.
     *
     * @param actionEvent the action
     */
    @Override
	public void actionPerformed(ActionEvent actionEvent) {

        try {
            AbstractButton button = (AbstractButton) actionEvent.getSource();
            String actionCommand = button.getActionCommand();
            DefaultAction action = actions.get(actionCommand);

            if (action != null) {
                // This controller can handle the action
				if (log.isDebugEnabled()) {
					log.debug("Handling command: " + actionCommand + " with action: " + action.getClass());
				}
				try {
                    preActionExecute();
                    log.debug("Dispatching to action for execution");
                    action.executeInController(this, actionEvent);
                    postActionExecute();
                } catch (RuntimeException ex) {
                    failedActionExecute();
                    throw ex;
                } catch (Exception ex) {
                    failedActionExecute();
                    throw new RuntimeException(ex);
                } finally {
                    finalActionExecute();
                }
            } else {
                // Let's try the parent controller in the hierarchy
                if (getParentController() != null) {
                    log.debug("Passing action on to parent controller");
                    parentController.actionPerformed(actionEvent);
                } else {
                    throw new RuntimeException("Nobody is responsible for action command: " + actionCommand);
                }
            }
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("Action source is not an AbstractButton: " + actionEvent);
        }
    }

    @Override
	public void preActionExecute() {
    }

    @Override
	public void postActionExecute() {
    }

    @Override
	public void failedActionExecute() {
    }

    @Override
	public void finalActionExecute() {
    }

    // If this controller is responsible for a JFrame, close it and all its children when the
    // window is closed.
    @Override
	public void windowClosing(WindowEvent windowEvent) {
        dispose();
        ((JFrame)getView()).dispose();
    }

    @Override
	public void windowOpened(WindowEvent windowEvent) {
    }

    @Override
	public void windowClosed(WindowEvent windowEvent) {
    }

    @Override
	public void windowIconified(WindowEvent windowEvent) {
    }

    @Override
	public void windowDeiconified(WindowEvent windowEvent) {
    }

    @Override
	public void windowActivated(WindowEvent windowEvent) {
    }

    @Override
	public void windowDeactivated(WindowEvent windowEvent) {
    }

}