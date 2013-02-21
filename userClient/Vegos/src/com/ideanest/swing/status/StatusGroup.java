package com.ideanest.swing.status;

/**
 * This type was created in VisualAge.
 *
 * @author Piotr Kaminski
 */

import javax.swing.event.*;
import java.util.*;

public class StatusGroup implements StatusGroupModel {
	private final List statusList = new ArrayList();
	private final EventListenerList listenerList = new EventListenerList();

	public class Status implements StatusModel {
		private String message;
		private int priority;
		private int maxProgress, progress;
		private boolean live;

		protected Status() {}
		protected Status(String message) {
			this.message = message;
		}

		public void enter() {
			if (!live) {
				addStatus(this);
				live = true;
			}
		}

		public void exit() {
			if (live) {
				removeStatus(this);
				live = false;
			}
		}

		public String getMessage() {return message;}
		public int getPriority() {return priority;}
		public int getMaxProgress() {return maxProgress;}
		public int getProgress() {return progress;}

		public synchronized void setMessage(String message) {
			this.message = message;
			notifyStatusChanged(this);
		}
		public synchronized void setPriority(int priority) {
			this.priority = priority;
			notifyStatusChanged(this);
		}
		public synchronized void setMaxProgress(int maxProgress) {
			this.maxProgress = maxProgress;
			notifyStatusChanged(this);
		}
		public synchronized void setProgress(int progress) {
			this.progress = progress;
			notifyStatusChanged(this);
		}
	}

	/**
	 * StatusGroup constructor comment.
	 */
	public StatusGroup() {
		super();
	}

	/**
	 * addStatusGroupListener method comment.
	 */
	public void addStatusGroupListener(StatusGroupListener l) {
		listenerList.add(StatusGroupListener.class, l);
	}

	public Status createStatus() {
		return new Status();
	}

	public Status createStatus(String message) {
		return new Status(message);
	}

	/**
	 * getSize method comment.
	 */
	public int getSize() {
		return statusList.size();
	}

	/**
	 * getStatusAt method comment.
	 */
	public StatusModel getStatusAt(int index) {
		return (StatusModel) statusList.get(index);
	}

	/**
	 * removeStatusGroupListener method comment.
	 */
	public void removeStatusGroupListener(StatusGroupListener l) {
		listenerList.remove(StatusGroupListener.class, l);
	}

	protected synchronized void addStatus(Status s) {
		statusList.add(s);
		fireStatusAdded(statusList.size() - 1);
	}

	protected void fireStatusAdded(int index) {
		StatusGroupEvent event = null;
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==StatusGroupListener.class) {
				if (event == null) event = new StatusGroupEvent(this, StatusGroupEvent.STATUS_ADDED, index);
				((StatusGroupListener)listeners[i+1]).statusAdded(event);
			}
		}
	}

	protected void fireStatusChanged(int index) {
		StatusGroupEvent event = null;
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==StatusGroupListener.class) {
				if (event == null) event = new StatusGroupEvent(this, StatusGroupEvent.STATUS_CHANGED, index);
				((StatusGroupListener)listeners[i+1]).statusChanged(event);
			}
		}
	}

	protected void fireStatusRemoved(int index) {
		StatusGroupEvent event = null;
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==StatusGroupListener.class) {
				if (event == null) event = new StatusGroupEvent(this, StatusGroupEvent.STATUS_REMOVED, index);
				((StatusGroupListener)listeners[i+1]).statusRemoved(event);
			}
		}
	}

	protected synchronized void notifyStatusChanged(Status s) {
		fireStatusChanged(statusList.indexOf(s));
	}

	protected synchronized void removeStatus(Status s) {
		int i = statusList.indexOf(s);
		statusList.remove(i);
		fireStatusRemoved(i);
	}
}
