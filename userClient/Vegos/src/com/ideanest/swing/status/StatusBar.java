package com.ideanest.swing.status;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * This type was created in VisualAge.
 *
 * @author Piotr Kaminski
 */
public class StatusBar extends JPanel {
	protected JLabel messageLabel;
	protected JProgressBar progressBar;
	protected StatusGroupModel model;

	protected StatusGroupListener modelListener = new StatusGroupListener() {
		public void statusChanged(StatusGroupEvent ev) {updateView();}
		public void statusAdded(StatusGroupEvent ev) {updateView();}
		public void statusRemoved(StatusGroupEvent ev) {updateView();}
	};

	/**
	 * StatusBar constructor comment.
	 */
	public StatusBar() {
		super();
		init();
	}

	public synchronized void setModel(StatusGroupModel model) {
		if (this.model != null) this.model.removeStatusGroupListener(modelListener);
		this.model = model;
		if (model != null) model.addStatusGroupListener(modelListener);
		updateView();
	}

	protected void init() {

		Border b = BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(UIManager.getColor("controlShadow")),
			BorderFactory.createEmptyBorder(2, 4, 2, 4)
		);

		messageLabel = new JLabel();
		messageLabel.setBorder(b);
		messageLabel.setMinimumSize(new Dimension(150, 0));

		progressBar = new JProgressBar();
		progressBar.setBorder(b);
		progressBar.setMinimumSize(new Dimension(100, 0));

		final JSplitPane splitPane = new JSplitPane(
			JSplitPane.HORIZONTAL_SPLIT,
			true,
			progressBar,
			messageLabel
		);
		splitPane.setOneTouchExpandable(false);
		splitPane.setDividerSize(2);

		setLayout(new BorderLayout());
		add(splitPane);

	}

	protected void updateView() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				StatusGroupModel m = model;
				if (m == null || m.getSize() == 0) {
					messageLabel.setText(" ");
					progressBar.setValue(0);
				} else {
					StatusModel s = m.getStatusAt(0);
					messageLabel.setText(s.getMessage() == null ? " " : s.getMessage());
					progressBar.setMaximum(s.getMaxProgress());
					progressBar.setValue(s.getProgress());
					if (s.getMaxProgress() == 0 && s.getProgress() > 0) {
						progressBar.setString("Working...");
					}
				}
			}
		});
	}
}
