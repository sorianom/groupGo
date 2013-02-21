package com.ideanest.swing;

import com.ideanest.util.*;
import javax.swing.undo.*;
import javax.swing.event.*;

import java.util.*;

/**
 * An undo manager that manages a limited list of undo edits.
 */
public class ActiveUndoManager implements UndoableEditListener {
	private int limit, splitIndex;
	private boolean busy;
	private final ActiveList edits, undoableEdits, redoableEdits;
	private final ActiveList constEdits, constUndoableEdits, constRedoableEdits;

	public ActiveUndoManager() {
		this(25);
	}

	public ActiveUndoManager(int limit) {
		this.limit = limit;

		edits = ActiveCollections.activeList(new RingArrayList(limit + 1));
		undoableEdits = ActiveCollections.activeList(new RingArrayList(limit + 1));
		redoableEdits = ActiveCollections.activeList(new RingArrayList(Math.min(limit/2, 10)));

		constEdits = ActiveCollections.unmodifiableActiveList(edits);
		constUndoableEdits = ActiveCollections.unmodifiableActiveList(undoableEdits);
		constRedoableEdits = ActiveCollections.unmodifiableActiveList(redoableEdits);
	}

	/**
	 * Add an edit to the manager.  If adding this edit would exceed the
	 * maximum number of edits, remove the oldest edit and kill it.
	 *
	 * @param UndoableEdit the edit to add
	 * @throws IllegalStateException if an undo or redo operation is currently in progress
	 */
	public synchronized void addEdit(UndoableEdit edit) {
		if (busy) throw new IllegalStateException();

		// drop all edits following current split index
		for (int i=edits.size()-1; i>=splitIndex; i--) {
			((UndoableEdit) edits.get(i)).die();
		}
		if (splitIndex < edits.size()) edits.subList(splitIndex, edits.size()).clear();
		redoableEdits.clear();

		// try to merge new edit in, otherwise append
		ADD_EDIT: {
			if (splitIndex > 0) {
				UndoableEdit last = (UndoableEdit) edits.get(splitIndex - 1);
				if (last.addEdit(edit)) break ADD_EDIT;
				if (edit.replaceEdit(last)) {
					edits.remove(edits.size()-1);
					if (undoableEdits.size() > 0) undoableEdits.remove(0);
					edits.add(edit);
					if (edit.isSignificant()) undoableEdits.add(0, edit);
					break ADD_EDIT;
				}
			}
			// we'll need one more space, enforce limit first
			if (splitIndex >= limit) trim(limit - 1);
			edits.add(edit);
			if (edit.isSignificant()) undoableEdits.add(0, edit);
			splitIndex++;
		}

	}

	public synchronized void clear() {
		for (ListIterator it = edits.listIterator(edits.size()); it.hasPrevious(); ) {
			UndoableEdit edit = (UndoableEdit) it.previous();
			edit.die();
		}
		edits.clear();
		undoableEdits.clear();
		redoableEdits.clear();
		splitIndex = 0;
	}

	/**
	 * Return the list of all edits currently buffered by this undo manager.
	 * The list includes both significant and insignificant edits.
	 *
	 * @return the list of all edits
	 */
	public ActiveList getEdits() {return constEdits;}

	/**
	 * Return the maximum size of the edit list, including both significant and
	 * insignificant edits.
	 *
	 * @return the maximum number of edits buffered
	 */
	public synchronized int getLimit() {
		return limit;
	}

	/**
	 * Return the list of significant redoable edits.
	 *
	 * @return the list of significant redoable edits
	 */
	public ActiveList getRedoableEdits() {return redoableEdits;}

	/**
	 * Return the index where the next edit to be added would be stored
	 * in the edits list.  This indicates the first redoable edit, or the
	 * end of the list if there are no redoable edits.
	 *
	 * @return the split index into the edits list
	 */
	public synchronized int getSplitIndex() {return splitIndex;}

	/**
	 * Return the list of significant undoable edits.
	 *
	 * @return the list of significant undoable edits
	 */
	public ActiveList getUndoableEdits() {return undoableEdits;}

	/**
	 * Redo up to and including the first significant event in the buffer.
	 * If there are no significant events, redo all (insignificant) events available.
	 *
	 * @throws CannotRedoException if there are no events to redo at all
	 */
	public synchronized void redo() {
		if (splitIndex == edits.size()) throw new CannotRedoException();
		try {
			redoTo((UndoableEdit) getRedoableEdits().get(0));
		} catch (NoSuchElementException e) {
			redoTo(edits.size() - 1);
		}
	}

	/**
	 * Redo up to and including the given edit.
	 *
	 * @param edit the edit up to which to redo
	 * @throws NoSuchElementException if the given edit is not in the buffer or is after the split index
	 */
	public synchronized void redoTo(UndoableEdit edit) {
		int i = edits.indexOf(edit);
		if (i == -1 || i < splitIndex) throw new NoSuchElementException();
		redoTo(i);
	}

	/**
	 * Set the maximum size of the edit list, including both significant and
	 * insignificant edits.
	 *
	 * @param the new maximum number of edits buffered
	 */
	public synchronized void setLimit(int limit) {
		this.limit = limit;
		trim(limit);
	}

	/**
	 * Undo up to and including the first significant event in the buffer.
	 * If there are no significant events, undo all (insignificant) events.
	 *
	 * @throws CannotUndoException if there are no events to undo at all
	 */
	public synchronized void undo() {
		if (edits.size() == 0) throw new CannotUndoException();
		try {
			undoTo((UndoableEdit) getUndoableEdits().get(0));
		} catch (NoSuchElementException e) {
			undoTo(0);
		}
	}

	public void undoableEditHappened(UndoableEditEvent ev) {
		addEdit(ev.getEdit());
	}

	/**
	 * Undo up to and including the given edit.
	 *
	 * @param edit the edit up to which to undo
	 * @throws NoSuchElementException if the given edit is not in the buffer or is after the split index
	 */
	public synchronized void undoTo(UndoableEdit edit) {
		int i = edits.indexOf(edit);
		if (i == -1 || i >= splitIndex) throw new NoSuchElementException();
		undoTo(i);
	}

	/**
	 * Redo up to and including the index.
	 *
	 * @param index the index up to and including which to redo
	 */
	protected synchronized void redoTo(int index) {
		if (index < splitIndex || index >= edits.size()) throw new IndexOutOfBoundsException();
		busy = true;
		try {
			for (ListIterator it = edits.listIterator(splitIndex); it.nextIndex() <= index; ) {
				UndoableEdit edit = (UndoableEdit) it.next();
				edit.redo();
				if (redoableEdits.get(0) == edit) {
					redoableEdits.remove(0);
					undoableEdits.add(0, edit);
				}
			}
			splitIndex = index + 1;
		} finally {
			busy = false;
		}
	}

	protected void trim(int n) {
		if (n <= edits.size()) return;
		int half = n/2;
		int keepFrom = splitIndex - 1 - half;
		int keepTo = splitIndex - 1 + half;
		if (keepTo - keepFrom + 1 > n) keepFrom++;
		if (keepFrom < 0) {
			keepTo -= keepFrom;
			keepFrom = 0;
		}
		if (keepTo >= edits.size()) {
			keepFrom -= keepTo - edits.size() - 1;
			keepTo = edits.size() - 1;
		}

		int sigEdits = 0;
		for (ListIterator it = edits.listIterator(edits.size()); it.previousIndex() > keepTo; ) {
			UndoableEdit edit = (UndoableEdit) it.previous();
			if (edit.isSignificant()) sigEdits++;
			edit.die();
		}
		if (keepTo+1 < edits.size()) edits.subList(keepTo+1, edits.size()).clear();
		if (sigEdits > 0) redoableEdits.subList(redoableEdits.size()-sigEdits, redoableEdits.size()).clear();

		sigEdits = 0;
		for (ListIterator it = edits.listIterator(keepFrom); it.hasPrevious(); ) {
			UndoableEdit edit = (UndoableEdit) it.previous();
			if (edit.isSignificant()) sigEdits++;
			edit.die();
		}
		if (keepFrom > 0) edits.subList(0, keepFrom).clear();
		if (sigEdits > 0) undoableEdits.subList(undoableEdits.size()-sigEdits, undoableEdits.size()).clear();
	}

	/**
	 * Undo up to and including the index.
	 *
	 * @param index the index up to and including which to undo
	 */
	protected synchronized void undoTo(int index) {
		if (index < 0 || index >= splitIndex) throw new IndexOutOfBoundsException();
		busy = true;
		try {
			for (ListIterator it = edits.listIterator(splitIndex); it.previousIndex() >= index; ) {
				UndoableEdit edit = (UndoableEdit) it.previous();
				edit.undo();
				if (undoableEdits.get(0) == edit) {
					undoableEdits.remove(0);
					redoableEdits.add(0, edit);
				}
			}
			splitIndex = index;
		} finally {
			busy = false;
		}
	}
}
