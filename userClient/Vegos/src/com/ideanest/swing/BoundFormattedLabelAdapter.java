package com.ideanest.swing;

import com.ideanest.util.*;

import javax.swing.*;
import javax.swing.event.*;

import java.beans.*;
import java.text.*;
import java.lang.reflect.*;
// import java.util.logging.*;
import java.awt.event.*;

/**
 * An adapter that hooks up a bound property of some source object to
 * a <code>JLabel</code>, updating its string representation with a
 * formatter each time the property changes value.  You provide the
 * label and formatter.  You must also provide the source object and
 * the property name to track.  The appropriate method for retrieving
 * the property is found through the object's class's bean descriptor.
 * <p>
 * If you want the formatted string to include some kind of message (say,
 * a prefix in front of the value), you'll need to use a <code>MessageFormat</code>.
 * Since a <code>MessageFormat</code> requires an array of objects to format,
 * you might be interested in using the <code>OneArgMessageFormatAdapter</code>.
 * <p>
 * <b>Warning:</b> This adapter will install a property change listener
 * on the source object.  The listener will <em>not</em> be automatically
 * removed even if the label is disposed of, which could lead to a memory
 * leak.  To ensure that the listener is removed, set the label to <code>null</code>
 * when the adapter is no longer needed.  This deficiency should be fixed
 * in the future, once JDK 1.4 becomes available.
 *
 * @see OneArgMessageFormatAdapter
 */
public class BoundFormattedLabelAdapter {
	private JLabel label;
	private Format format;
	private Object source;
	private String propertyName;
	private Object lastValue;
	private boolean hasLastValue = false;
	private PropertySource propertySource;

	// protected static final Logger log = Logger.getLogger(BoundFormattedLabelAdapter.class.getName());

	private static final Object[] NO_ARGS = new Object[0];
	private static final Class[] ADV_LISTENER_ARGS = new Class[] {String.class, PropertyChangeListener.class};
	private static final Class[] SIMPLE_LISTENER_ARGS = new Class[] {PropertyChangeListener.class};

	/* TODO: switch to a HierarchyListener (JDK1.4) to be notified of isShowing changes
	private AncestorListener ancestorListener = new AncestorListener() {
		public void ancestorMoved(AncestorEvent ev) {}
		public void ancestorAdded(AncestorEvent ev) {
			log.fine("ancestor added");
			startUpdating();
		}
		public void ancestorRemoved(AncestorEvent ev) {
			log.fine("ancestor removed");
			stopUpdating();
		}
	};
	*/

	protected abstract class PropertySource implements PropertyChangeListener {
		private final BeanInfo beanInfo;
		private final Method mGet;
		public PropertySource() {
			try {
				beanInfo = Introspector.getBeanInfo(source.getClass());
				PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
				for (int i=0; i<props.length; i++) {
					PropertyDescriptor prop = props[i];
					if (prop.getName().equals(propertyName)) {
						if (!prop.isBound()) throw new RuntimeException("property " + source.getClass().getName() + "." + propertyName + " is not bound");
						mGet = prop.getReadMethod();
						return;
					}
				}
				throw new RuntimeException("property " + source.getClass().getName() + "." + propertyName + " not found");
			} catch (IntrospectionException e) {
				throw new UnexpectedException(e);
			}
		}
		public Object getValue() throws InvocationTargetException, IllegalAccessException {
			return mGet.invoke(source, NO_ARGS);
		}
		public abstract void addListener() throws InvocationTargetException, IllegalAccessException;
		public abstract void removeListener() throws InvocationTargetException, IllegalAccessException;

	}

	protected class AdvancedPropertySource extends PropertySource {
		private final Method mAdd, mRemove;
		private final Object[] args;
		public AdvancedPropertySource() {
			try {
				mAdd = source.getClass().getMethod("addPropertyChangeListener", ADV_LISTENER_ARGS);
				mRemove = source.getClass().getMethod("removePropertyChangeListener", ADV_LISTENER_ARGS);
			} catch (NoSuchMethodException e) {
				throw new UnexpectedException(e);
			}
			args = new Object[] {propertyName, this};
		}
		public void addListener() throws InvocationTargetException, IllegalAccessException {
			mAdd.invoke(source, args);
		}
		public void removeListener() throws InvocationTargetException, IllegalAccessException {
			mRemove.invoke(source, args);
		}
		public void propertyChange(PropertyChangeEvent ev) {
			updateLabel(ev.getNewValue());
		}
	}

	protected class SimplePropertySource extends PropertySource {
		private final Method mAdd, mRemove;
		private final Object[] args;
		public SimplePropertySource() {
			try {
				mAdd = source.getClass().getMethod("addPropertyChangeListener", SIMPLE_LISTENER_ARGS);
				mRemove = source.getClass().getMethod("removePropertyChangeListener", SIMPLE_LISTENER_ARGS);
			} catch (NoSuchMethodException e) {
				throw new UnexpectedException(e);
			}
			args = new Object[] {this};
		}
		public void addListener() throws InvocationTargetException, IllegalAccessException {
			mAdd.invoke(source, args);
		}
		public void removeListener() throws InvocationTargetException, IllegalAccessException {
			mRemove.invoke(source, args);
		}
		public void propertyChange(PropertyChangeEvent ev) {
			if (ev.getPropertyName().equals(propertyName)) updateLabel(ev.getNewValue());
		}
	}

	public BoundFormattedLabelAdapter() {
	}

	public BoundFormattedLabelAdapter(JLabel label, Format format) {
		setFormat(format);
		setLabel(label);
	}

	public BoundFormattedLabelAdapter(JLabel label, Format format, Object source, String propertyName) {
		setFormat(format);
		setProperty(source, propertyName);
		setLabel(label);
	}

	public synchronized Format getFormat() {
		return format;
	}

	public synchronized JLabel getLabel() {
		return label;
	}

	public synchronized String getPropertyName() {
		return propertyName;
	}

	public synchronized Object getSource() {
		return source;
	}

	public synchronized void setFormat(Format format) {
		if (this.format != format) {
			this.format = format;
			updateLabel();
		}
	}

	public synchronized void setLabel(JLabel label) {
		if (this.label != label) {
			stopUpdating();
			// if (this.label != null) this.label.removeAncestorListener(ancestorListener);
			this.label = label;
			// if (label != null) label.addAncestorListener(ancestorListener);
			startUpdating();
		}
	}

	public synchronized void setProperty(Object source, String propertyName) {
		if (this.source == source && this.propertyName == propertyName) return;
		stopUpdating();
		propertySource = null;
		this.source = source;
		this.propertyName = propertyName;
		if (source == null || propertyName == null) return;
		try {
			source.getClass().getMethod("addPropertyChangeListener", ADV_LISTENER_ARGS);
			propertySource = new AdvancedPropertySource();
		} catch (NoSuchMethodException e) {
			propertySource = new SimplePropertySource();
		}
		startUpdating();
	}

	protected synchronized Object getValue() throws InvocationTargetException, IllegalAccessException {
		assert propertySource != null;
		if (!hasLastValue) {
			lastValue = propertySource.getValue();
			hasLastValue = true;
		}
		return lastValue;
	}

	protected synchronized boolean isLabelReady() {
		// TODO:  eventually check label.isShowing() too
		return label != null;
	}

	protected synchronized void setLabelText(final String text) {
		if (label == null) return;
		SwingUtilities.invokeLater(new Runnable() {
			private JLabel localLabel = label;
			public void run() {
				localLabel.setText(text);
			}
		});
	}

	protected synchronized void startUpdating() {
		if (!isLabelReady()) return;
		try {
			if (propertySource != null) propertySource.addListener();
			updateLabel();
		} catch (Exception e) {
			// log.log(Level.WARNING, "Unable to add bound property listener", e);
			setLabelText(e.toString());
		}
	}

	protected synchronized void stopUpdating() {
		if (propertySource != null) {
			try {
				propertySource.removeListener();
			} catch (Exception e) {
				// log.log(Level.WARNING, "Unable to remove bound property listener", e);
			}
		}
		setLabelText("");
	}

	protected synchronized void updateLabel() {
		if (!isLabelReady()) return;
		if (format == null || propertySource == null) {
			setLabelText("");
		} else {
			try {
				setLabelText(format.format(getValue()));
			} catch (Exception e) {
				// log.log(Level.WARNING, "Get and formatting bound value failed", e);
				setLabelText(e.toString());
			}
		}
	}

	protected synchronized void updateLabel(Object value) {
		lastValue = value;
		hasLastValue = true;
		updateLabel();
	}
}
