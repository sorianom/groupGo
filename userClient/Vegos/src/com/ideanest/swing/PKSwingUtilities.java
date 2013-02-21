package com.ideanest.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
// import net.who.pkaminsk.debug.Debug;
// import net.who.pkaminsk.util.*;

/**
 * Some methods of general utility when dealing with Swing.
 */

public final class PKSwingUtilities {

	private PKSwingUtilities() {
		super();
	}

	public static void addPopupMenu(final JComponent c, final JPopupMenu popup) {
		c.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger() && popup.isEnabled()) {
					popup.setInvoker(e.getComponent());
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger() && popup.isEnabled()) {
					popup.setInvoker(e.getComponent());
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	public static void invoke(Runnable r) throws InterruptedException, InvocationTargetException {
		if (SwingUtilities.isEventDispatchThread()) {
			try {
				r.run();
			} catch (Exception e) {
				throw new InvocationTargetException(e);
			}
		} else {
			SwingUtilities.invokeAndWait(r);
		}
	}

	public static void show(Throwable e) {
		show(e, null);
	}

	public static void show(final Throwable e, final JComponent parent) {
		// System.out.println(e);
		try {
			invoke(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(parent, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			});
		} catch (InterruptedException e1) {
		} catch (java.lang.reflect.InvocationTargetException e1) {
			System.err.println(e1.getTargetException());
			// Debug.log(e1.getTargetException());
		}
	}
}
