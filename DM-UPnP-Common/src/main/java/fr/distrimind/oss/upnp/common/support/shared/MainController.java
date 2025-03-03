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

package fr.distrimind.oss.upnp.common.support.shared;


import fr.distrimind.oss.flexilogxml.common.concurrent.ThreadType;
import fr.distrimind.oss.flexilogxml.common.log.Handler;
import fr.distrimind.oss.flexilogxml.common.log.LogManager;
import fr.distrimind.oss.upnp.common.Log;
import fr.distrimind.oss.upnp.common.UpnpService;
import fr.distrimind.oss.upnp.common.swing.AbstractController;
import fr.distrimind.oss.upnp.common.swing.Application;
import fr.distrimind.oss.upnp.common.swing.logging.LogCategory;
import fr.distrimind.oss.upnp.common.swing.logging.LogController;
import fr.distrimind.oss.upnp.common.swing.logging.LogMessage;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.UIManager;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import fr.distrimind.oss.flexilogxml.common.log.DMLogger;
import fr.distrimind.oss.flexilogxml.common.log.Level;

/**
 * @author Christian Bauer
 */
public abstract class MainController extends AbstractController<JFrame> {
    final DMLogger logger = Log.getLogger(MainController.class);
    // Dependencies
    final private LogController logController;

    // View
    final private JPanel logPanel;

    public MainController(JFrame view, List<LogCategory> logCategories) {
        super(view);

        // Some UI stuff (of course, why would the OS L&F be the default -- too easy?!)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            logger.error(() -> "Unable to load native look and feel: ", ex);
        }

        // Exception handler
        System.setProperty("sun.awt.exception.handler", AWTExceptionHandler.class.getName());
        ThreadFactory threadFactory= ThreadType.VIRTUAL_THREAD_IF_AVAILABLE.newThreadFactoryInstance();
        // Shutdown behavior
        Runtime.getRuntime().addShutdownHook(threadFactory.newThread(() -> {
            if (getUpnpService() != null)
                getUpnpService().shutdown();
        }));

        // Logging UI
        logController = new LogController(this, logCategories) {
            @Override
            protected void expand(LogMessage logMessage) {
                fireEventGlobal(
                        new TextExpandEvent(logMessage.getMessage())
                );
            }

            @Override
            protected Frame getParentWindow() {
                return MainController.this.getView();
            }
        };
        logPanel = logController.getView();
        logPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        // Wire UI into JUL
        // Don't reset JUL root logger but add if there is a JUL config file
        Handler handler = logRecord -> logController.pushMessage(new LogMessage(logRecord));
        if (System.getProperty("java.util.logging.config.file") == null) {
            LogManager.resetHandlers(handler);
        } else {
            LogManager.addHandler(handler);
        }
    }

    public LogController getLogController() {
        return logController;
    }

    public JPanel getLogPanel() {
        return logPanel;
    }

    public void log(Level level, String msg) {
        log(new LogMessage(level, msg));
    }

    public void log(LogMessage message) {
        getLogController().pushMessage(message);
    }

    @Override
    public void dispose() {
        super.dispose();
        ShutdownWindow.INSTANCE.setVisible(true);
    }

    public static class ShutdownWindow extends JWindow {
        private static final long serialVersionUID = 1L;
        final public static JWindow INSTANCE = new ShutdownWindow();

        protected ShutdownWindow() {
            JLabel shutdownLabel = new JLabel("Shutting down, please wait...");
            shutdownLabel.setHorizontalAlignment(JLabel.CENTER);
            getContentPane().add(shutdownLabel);
            setPreferredSize(new Dimension(300, 30));
            pack();
            Application.center(this);
        }
    }

    public abstract UpnpService getUpnpService();

}
