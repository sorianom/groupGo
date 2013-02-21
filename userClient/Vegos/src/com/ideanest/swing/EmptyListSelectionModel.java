package com.ideanest.swing;

import javax.swing.event.*;
import javax.swing.*;

/**
 * A list selection model that refuses all selections.
 */
public class EmptyListSelectionModel implements ListSelectionModel {
	private static EmptyListSelectionModel instance;

	public EmptyListSelectionModel() {}

	/**
	 * Since instances are stateless, we can cache a singleton instance to
	 * be used by everyone.  There's no need to enforce full singleton
	 * semantics, though.
	 *
	 * @return an instance of this class
	 */
	public synchronized static EmptyListSelectionModel getInstance() {
		if (instance == null) instance = new EmptyListSelectionModel();
		return instance;
	}

	public void addListSelectionListener(ListSelectionListener x) {
	}

	public void addSelectionInterval(int index0, int index1) {}

	public void clearSelection() {}

	public int getAnchorSelectionIndex() {
		return -1;
	}

	public int getLeadSelectionIndex() {
		return -1;
	}

	public int getMaxSelectionIndex() {
		return -1;
	}

	public int getMinSelectionIndex() {
		return -1;
	}

	public int getSelectionMode() {
		return SINGLE_SELECTION;
	}

	public boolean getValueIsAdjusting() {
		return false;
	}

	public void insertIndexInterval(int index, int length, boolean before) {
	}

	public boolean isSelectedIndex(int index) {
		return false;
	}

	public boolean isSelectionEmpty() {
		return true;
	}

	public void removeIndexInterval(int index0, int index1) {
	}

	public void removeListSelectionListener(ListSelectionListener x) {
	}

	public void removeSelectionInterval(int index0, int index1) {
	}

	public void setAnchorSelectionIndex(int index) {
	}

	public void setLeadSelectionIndex(int index) {
	}

	public void setSelectionInterval(int index0, int index1) {
	}

	/**
	 * This method is meaningless for an empty selection model.  Do not call.
	 */
	public void setSelectionMode(int selectionMode) {
	}

	public void setValueIsAdjusting(boolean valueIsAdjusting) {
	}
}
