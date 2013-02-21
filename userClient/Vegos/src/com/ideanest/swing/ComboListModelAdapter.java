package com.ideanest.swing;

import javax.swing.*;
import javax.swing.event.*;

/**
 * An adapter to turn a <code>ListModel</code> into a <code>ComboBoxModel</code>.
 * It just holds a reference to the selected item, and passes on all events from the
 * list model.  It is not directly mutable, but will notify listeners
 * of any changes to the underlying list.
 * <p>
 * To use, take any <code>ListModel</code> and pass it as an argument to the
 * constructor.
 * <p>
 * @author Piotr Kaminski
 */

public class ComboListModelAdapter implements ComboBoxModel {
	private final ListModel listModel;
	private Object selectedItem;
	private final ListDataListenerSupport support = new ListDataListenerSupport(this);
	private ListDataListener listener;

	public ComboListModelAdapter(ListModel listModel) {
		this.listModel = listModel;
	}

	public void addListDataListener(ListDataListener l) {
		if (support.getListenerCount() == 0) listModel.addListDataListener(getListener());
		support.addListDataListener(l);
	}

	public Object getElementAt(int index) {
		return listModel.getElementAt(index);
	}

	public Object getSelectedItem() {
		return selectedItem;
	}

	public int getSize() {
		return listModel.getSize();
	}

	public void removeListDataListener(ListDataListener l) {
		support.removeListDataListener(l);
		if (support.getListenerCount() == 0) listModel.removeListDataListener(getListener());
	}

	public void setSelectedItem(Object item) {
		selectedItem = item;
	}

	private ListDataListener getListener() {
		if (listener == null) {
			listener = new ListDataListener() {
				public void contentsChanged(ListDataEvent ev) {
					support.fireContentsChanged(ev.getIndex0(), ev.getIndex1());
				}
				public void intervalAdded(ListDataEvent ev) {
					support.fireIntervalAdded(ev.getIndex0(), ev.getIndex1());
				}
				public void intervalRemoved(ListDataEvent ev) {
					support.fireIntervalRemoved(ev.getIndex0(), ev.getIndex1());
				}
			};
		}
		return listener;
	}
}
