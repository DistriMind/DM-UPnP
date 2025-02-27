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
 package fr.distrimind.oss.upnp.swing.logging;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.util.List;
import fr.distrimind.oss.flexilogxml.log.DMLogger;
import fr.distrimind.oss.upnp.Log;
import fr.distrimind.oss.flexilogxml.log.Level;

 /**
 * @author Christian Bauer
 */
 public class LogCategorySelector extends JDialog {
     private static final long serialVersionUID = 1L;
     protected final JPanel mainPanel = new JPanel();

     public LogCategorySelector(List<LogCategory> logCategories) {
         setTitle("Select logging categories...");

         mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
         mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

         addLogCategories(logCategories);

         JScrollPane scrollPane = new JScrollPane(mainPanel);
         scrollPane.setBorder(BorderFactory.createEmptyBorder());
         add(scrollPane);

         setMaximumSize(new Dimension(750, 550));
         setResizable(false);
         pack();
     }

     protected void addLogCategories(List<LogCategory> logCategories) {
         for (LogCategory logCategory : logCategories) {
             addLogCategory(logCategory);
         }
     }

     protected void addLogCategory(final LogCategory logCategory) {

         final JPanel categoryPanel = new JPanel(new BorderLayout());
         categoryPanel.setBorder(BorderFactory.createTitledBorder(logCategory.getName()));

         addLoggerGroups(logCategory, categoryPanel);

         mainPanel.add(categoryPanel);
     }

     protected void addLoggerGroups(final LogCategory logCategory, final JPanel categoryPanel) {

         JPanel checkboxPanel = new JPanel();
         checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
         for (final LogCategory.Group group : logCategory.getGroups()) {

             final JCheckBox checkBox = getjCheckBox(group);
             checkboxPanel.add(checkBox);
         }

         JToolBar buttonBar = new JToolBar();
         buttonBar.setFloatable(false);

         JButton enableAllButton = getjButton(logCategory, categoryPanel);
         buttonBar.add(enableAllButton);

         JButton disableAllButton = getButton(logCategory, categoryPanel);
         buttonBar.add(disableAllButton);

         categoryPanel.add(checkboxPanel, BorderLayout.CENTER);
         categoryPanel.add(buttonBar, BorderLayout.NORTH);
     }

     private JButton getButton(LogCategory logCategory, JPanel categoryPanel) {
         JButton disableAllButton = new JButton("None");
         disableAllButton.setFocusable(false);
         disableAllButton.addActionListener(e -> {
             for (LogCategory.Group group : logCategory.getGroups()) {
                 disableLoggerGroup(group);
             }
             categoryPanel.removeAll();
             addLoggerGroups(logCategory, categoryPanel);
             categoryPanel.revalidate();
         });
         return disableAllButton;
     }

     private JButton getjButton(LogCategory logCategory, JPanel categoryPanel) {
         JButton enableAllButton = new JButton("All");
         enableAllButton.setFocusable(false);
         enableAllButton.addActionListener(e -> {
             for (LogCategory.Group group : logCategory.getGroups()) {
                 enableLoggerGroup(group);
             }
             categoryPanel.removeAll();
             addLoggerGroups(logCategory, categoryPanel);
             categoryPanel.revalidate();
         });
         return enableAllButton;
     }

     private JCheckBox getjCheckBox(LogCategory.Group group) {
         final JCheckBox checkBox = new JCheckBox(group.getName());
         checkBox.setSelected(group.isEnabled());
         checkBox.setFocusable(false);
         checkBox.addItemListener(e -> {
             if (e.getStateChange() == ItemEvent.DESELECTED) {
                 disableLoggerGroup(group);
             } else if (e.getStateChange() == ItemEvent.SELECTED) {
                 enableLoggerGroup(group);
             }
         });
         return checkBox;
     }

     protected void enableLoggerGroup(LogCategory.Group group) {
         group.setEnabled(true);

         group.getPreviousLevels().clear();
         for (LogCategory.LoggerLevel loggerLevel : group.getLoggerLevels()) {
             DMLogger logger = Log.getLogger(loggerLevel.getLogger());

             group.getPreviousLevels().add(
                     new LogCategory.LoggerLevel(logger.getName(), logger.getLogLevel())
             );

             logger.setLogLevel(loggerLevel.getLevel());
         }
     }

     protected void disableLoggerGroup(LogCategory.Group group) {
         group.setEnabled(false);

         for (LogCategory.LoggerLevel loggerLevel : group.getPreviousLevels()) {
             DMLogger logger = Log.getLogger(loggerLevel.getLogger());
             logger.setLogLevel(loggerLevel.getLevel());
         }

         if (group.getPreviousLevels().isEmpty()) {
             // Failsafe, if someone messed with the correct order of enabling/disabling e.g. in a subclass
             for (LogCategory.LoggerLevel loggerLevel : group.getLoggerLevels()) {
                 DMLogger logger = Log.getLogger(loggerLevel.getLogger());
                 logger.setLogLevel(Level.INFO);
             }
         }

         group.getPreviousLevels().clear();
     }

 }