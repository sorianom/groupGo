package com.ideanest.swing;

import javax.swing.event.*;

/**
 * Support for maintaining a list of ListDataListeners and firing events.
 *
 * @author Piotr Kaminski
 */

public class ListDataListenerSupport {
	private final Object source;
	private final EventListenerList listenerList = new EventListenerList();

	public ListDataListenerSupport(Object source) {
		this.source = source;
	}

	public void addListDataListener(ListDataListener l) {
		listenerList.add(ListDataListener.class, l);
	}

	public void fireContentsChanged(int index0, int index1) {
		ListDataEvent event = null;
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==ListDataListener.class) {
				if (event == null) event = new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED, index0, index1);
				((ListDataListener)listeners[i+1]).contentsChanged(event);
			}
		}
	}

	public void fireIntervalAdded(int index0, int index1) {
		ListDataEvent event = null;
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==ListDataListener.class) {
				if (event == null) event = new ListDataEvent(source, ListDataEvent.INTERVAL_ADDED, index0, index1);
				((ListDataListener)listeners[i+1]).intervalAdded(event);
			}
		}
	}

	public void fireIntervalRemoved(int index0, int index1) {
		ListDataEvent event = null;
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==ListDataListener.class) {
				if (event == null) event = new ListDataEvent(source, ListDataEvent.INTERVAL_REMOVED, index0, index1);
				((ListDataListener)listeners[i+1]).intervalRemoved(event);
			}
		}
	}

	public int getListenerCount() {return listenerList.getListenerCount();}

	public void removeListDataListener(ListDataListener l) {
		listenerList.remove(ListDataListener.class, l);
	}
}
