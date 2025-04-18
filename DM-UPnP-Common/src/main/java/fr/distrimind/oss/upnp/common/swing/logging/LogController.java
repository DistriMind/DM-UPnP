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
 package fr.distrimind.oss.upnp.common.swing.logging;

import fr.distrimind.oss.upnp.common.swing.AbstractController;
import fr.distrimind.oss.upnp.common.swing.Application;
import fr.distrimind.oss.upnp.common.swing.Controller;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

 /**
 * @author Christian Bauer
 */
public abstract class LogController extends AbstractController<JPanel> {

    final private LogCategorySelector logCategorySelector;

    // View
    final private JTable logTable;
    final private LogTableModel logTableModel;

    final private JToolBar toolBar = new JToolBar();
    final private JButton configureButton = createConfigureButton();
    final private JButton clearButton = createClearButton();
    final private JButton copyButton = createCopyButton();
    final private JButton expandButton = createExpandButton();
    final private JButton pauseButton = createPauseButton();
    final private JLabel pauseLabel = new JLabel(" (Active)");
    final private JComboBox<?> expirationComboBox = new JComboBox<>(Expiration.values());

    public enum Expiration {
        TEN_SECONDS(10, "10 Seconds"),
        SIXTY_SECONDS(60, "60 Seconds"),
        FIVE_MINUTES(300, "5 Minutes"),
        NEVER(Integer.MAX_VALUE, "Never");

        private final int seconds;
        private final String label;

        Expiration(int seconds, String label) {
            this.seconds = seconds;
            this.label = label;
        }

        public int getSeconds() {
            return seconds;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return getLabel();
        }
    }

    public LogController(Controller<? extends Container> parentController, List<LogCategory> logCategories) {
        this(parentController, Expiration.SIXTY_SECONDS, logCategories);
    }

	@SuppressWarnings("PMD")
    public LogController(Controller<? extends Container> parentController, Expiration expiration, List<LogCategory> logCategories) {
        super(new JPanel(new BorderLayout()), parentController);

        logCategorySelector = new LogCategorySelector(logCategories);

        logTableModel = new LogTableModel(expiration.getSeconds());
        logTable = new JTable(logTableModel);

        logTable.setDefaultRenderer(
                LogMessage.class,
                new LogTableCellRenderer() {
                    @Override
					protected ImageIcon getWarnErrorIcon() {
                        return LogController.this.getWarnErrorIcon();
                    }

                    @Override
					protected ImageIcon getDebugIcon() {
                        return LogController.this.getDebugIcon();
                    }

                    @Override
					protected ImageIcon getTraceIcon() {
                        return LogController.this.getTraceIcon();
                    }

                    @Override
					protected ImageIcon getInfoIcon() {
                        return LogController.this.getInfoIcon();
                    }
                });

        logTable.setCellSelectionEnabled(false);
        logTable.setRowSelectionAllowed(true);
        logTable.getSelectionModel().addListSelectionListener(
				e -> {

					if (e.getValueIsAdjusting()) return;

					if (e.getSource() == logTable.getSelectionModel()) {
						int[] rows = logTable.getSelectedRows();

						if (rows == null || rows.length == 0) {
							copyButton.setEnabled(false);
							expandButton.setEnabled(false);
						} else if (rows.length == 1) {
							copyButton.setEnabled(true);
							LogMessage msg = (LogMessage) logTableModel.getValueAt(rows[0], 0);
							expandButton.setEnabled(msg.getMessage().length() > getExpandMessageCharacterLimit());
						} else {
							copyButton.setEnabled(true);
							expandButton.setEnabled(false);
						}
					}
				}
		);

        adjustTableUI();
        initializeToolBar(expiration);

        getView().setPreferredSize(new Dimension(250, 100));
        getView().setMinimumSize(new Dimension(250, 50));
        getView().add(new JScrollPane(logTable), BorderLayout.CENTER);
        getView().add(toolBar, BorderLayout.SOUTH);
    }

    public void pushMessage(final LogMessage message) {
        // Do it on EDT
        SwingUtilities.invokeLater(() -> {

			logTableModel.pushMessage(message);

			// Scroll to bottom if nothing is selected
			if (!logTableModel.isPaused()) {
				logTable.scrollRectToVisible(
						logTable.getCellRect(logTableModel.getRowCount() - 1, 0, true)
				);
			}
		});
    }

    protected void adjustTableUI() {
        logTable.setFocusable(false);
        logTable.setRowHeight(18);
        logTable.getTableHeader().setReorderingAllowed(false);
        logTable.setBorder(BorderFactory.createEmptyBorder());

        logTable.getColumnModel().getColumn(0).setMinWidth(30);
        logTable.getColumnModel().getColumn(0).setMaxWidth(30);
        logTable.getColumnModel().getColumn(0).setResizable(false);


        logTable.getColumnModel().getColumn(1).setMinWidth(90);
        logTable.getColumnModel().getColumn(1).setMaxWidth(90);
        logTable.getColumnModel().getColumn(1).setResizable(false);

        logTable.getColumnModel().getColumn(2).setMinWidth(100);
        logTable.getColumnModel().getColumn(2).setMaxWidth(250);

        logTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        logTable.getColumnModel().getColumn(3).setMaxWidth(400);

        logTable.getColumnModel().getColumn(4).setPreferredWidth(600);
    }

    protected void initializeToolBar(Expiration expiration) {
        configureButton.setFocusable(false);
        configureButton.addActionListener(e -> {
			Application.center(logCategorySelector, getParentWindow());
			logCategorySelector.setVisible(!logCategorySelector.isVisible());
		});

        clearButton.setFocusable(false);
        clearButton.addActionListener(e -> logTableModel.clearMessages());

        copyButton.setFocusable(false);
        copyButton.setEnabled(false);
        copyButton.addActionListener(e -> {
			StringBuilder sb = new StringBuilder();
			List<LogMessage> messages = getSelectedMessages();
			for (LogMessage message : messages) {
				sb.append(message.toString()).append("\n");
			}
			Application.copyToClipboard(sb.toString());
		});

        expandButton.setFocusable(false);
        expandButton.setEnabled(false);
        expandButton.addActionListener(e -> {
			List<LogMessage> messages = getSelectedMessages();
			if (messages.size() != 1) return;
			expand(messages.get(0));
		});

        pauseButton.setFocusable(false);
        pauseButton.addActionListener(e -> {
			logTableModel.setPaused(!logTableModel.isPaused());
			if (logTableModel.isPaused()) {
				pauseLabel.setText(" (Paused)");
			} else {
				pauseLabel.setText(" (Active)");
			}
		});

        expirationComboBox.setSelectedItem(expiration);
        expirationComboBox.setMaximumSize(new Dimension(100, 32));
        expirationComboBox.addActionListener(e -> {
			JComboBox<?> cb = (JComboBox<?>) e.getSource();
			Expiration expiration1 = (Expiration) cb.getSelectedItem();
			logTableModel.setMaxAgeSeconds(Objects.requireNonNull(expiration1).getSeconds());
		});

        toolBar.setFloatable(false);
        toolBar.add(copyButton);
        toolBar.add(expandButton);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(configureButton);
        toolBar.add(clearButton);
        toolBar.add(pauseButton);
        toolBar.add(pauseLabel);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(new JLabel("Clear after:"));
        toolBar.add(expirationComboBox);
    }

    protected List<LogMessage> getSelectedMessages() {
        List<LogMessage> messages = new ArrayList<>();
        for (int row : logTable.getSelectedRows()) {
            messages.add((LogMessage) logTableModel.getValueAt(row, 0));
        }
        return messages;
    }

    protected int getExpandMessageCharacterLimit() {
        return 100;
    }

    public LogTableModel getLogTableModel() {
        return logTableModel;
    }

    protected JButton createConfigureButton() {
        return new JButton("Options...", Application.createImageIcon(LogController.class, "img/configure.png"));
    }

    protected JButton createClearButton() {
        return new JButton("Clear Log", Application.createImageIcon(LogController.class, "img/removetext.png"));
    }

    protected JButton createCopyButton() {
        return new JButton("Copy", Application.createImageIcon(LogController.class, "img/copyclipboard.png"));
    }

    protected JButton createExpandButton() {
        return new JButton("Expand", Application.createImageIcon(LogController.class, "img/viewtext.png"));
    }

    protected JButton createPauseButton() {
        return new JButton("Pause/Continue Log", Application.createImageIcon(LogController.class, "img/pause.png"));
    }

    protected ImageIcon getWarnErrorIcon() {
        return Application.createImageIcon(LogController.class, "img/warn.png");
    }

    protected ImageIcon getDebugIcon() {
        return Application.createImageIcon(LogController.class, "img/debug.png");
    }

    protected ImageIcon getTraceIcon() {
        return Application.createImageIcon(LogController.class, "img/trace.png");
    }

    protected ImageIcon getInfoIcon() {
        return Application.createImageIcon(LogController.class, "img/info.png");
    }

    protected abstract void expand(LogMessage message);

    protected abstract Frame getParentWindow();

}