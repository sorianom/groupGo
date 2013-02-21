package com.ideanest.swing;

import com.ideanest.util.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;


/**
 * Provides adapters between base JDK collections and the JFC/Swing
 * <code>ListModel</code> interface.  This class consists exclusively of static methods
 * that return wrappers backed by a specified collection but implementing
 * the Swing <code>ListModel</code> interface.
 * <p>
 * To use, call the appropriate method to wrap your collection at the
 * point it's created.  You should wrap it immediately and never again
 * refer to the backing collection, since only mutations that go through
 * the wrapper will cause events to be fired.  To put it another way,
 * mutating the underlying collection directly will lead to inconsistent
 * event reporting, and hence an inconsistent GUI (if used with Swing).
 * <p>
 * Once your collection is wrapped, you can use it as a normal collection.
 * All standard methods are supported (depending on the wrapper you chose).
 * Since the wrapper also implements the <code>ListModel</code> interface, you
 * can pass it directly to any Swing component requiring such a model (e.g.
 * <code>JList</code>, <code>JComboBox</code> (with the
 * <code>ComboListModelAdapter</code>, etc).  To avoid casting,
 * interfaces combining the collection interfaces with the Swing <code>ListModel</code>
 * are provided for your convenience.  You can use these when declaring
 * variables in your code.
 * <p>
 * The list model presented to Swing will be ordered according to the underlying
 * collection's iterator.  For ordered collections (lists, sorted sets) this is
 * well defined and you'll get a consistent, expected order.  For other kinds of
 * collections, the order may change with each mutation.  This will be reflected
 * in the Swing presentation.
 * <p>
 * Note that any objects you derive from an active wrapper (including mutating
 * iterators and subcollections) will also correctly cause events to be fired in
 * the original collection they were derived from.  You cannot, however, wrap the
 * various collections returned by a <code>Map</code>, since those can be mutated
 * by the original map, which is not in a wrapper.
 * <p>
 * The objects returned by methods in this class are:
 * <ul>
 * <li><em>not</em> thread-safe</li>
 * <li><em>not</em> cloneable (create a new collection instance using a copy-constructor
 *     instead, then wrap it in an active adapter)</li>
 * <li>serializable (but the listeners list will <em>not</em> be serialized)</li>
 * </ul>
 *
 * <p>
 * @author Piotr Kaminski
 */

public class ActiveCollections {

private static abstract class ActiveCollectionBase extends DelegatedCollection implements ActiveCollection, Serializable {

	private transient ListDataListenerSupport support;
	private final boolean fireOnEventDispatchThread = true;

	protected ListDataListenerSupport getSupport() {
		if (support == null) support = new ListDataListenerSupport(this);
		return support;
	}
	public void addListDataListener(ListDataListener l) {
		getSupport().addListDataListener(l);
	}
	public void removeListDataListener(ListDataListener l) {
		getSupport().removeListDataListener(l);
	}
	protected void fireContentsChanged(final int i0, final int i1) {
		// System.out.println("changed " + i0 + " to " + i1);
		if (support == null) return;
		if (fireOnEventDispatchThread && !SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						getSupport().fireContentsChanged(i0, i1);
					}
				});
			} catch (InterruptedException e) {
				throw new InterruptedRuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new UnexpectedException(e);
			}
		} else {
			getSupport().fireContentsChanged(i0, i1);
		}
	}
	protected void fireIntervalAdded(final int i0, final int i1) {
		// System.out.println("added " + i0 + " to " + i1);
		if (support == null) return;
		if (fireOnEventDispatchThread && !SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						getSupport().fireIntervalAdded(i0, i1);
					}
				});
			} catch (InterruptedException e) {
				throw new InterruptedRuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new UnexpectedException(e);
			}
		} else {
			getSupport().fireIntervalAdded(i0, i1);
		}
	}
	protected void fireIntervalRemoved(final int i0, final int i1) {
		// System.out.println("removed " + i0 + " to " + i1);
		if (support == null) return;
		if (fireOnEventDispatchThread && !SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						getSupport().fireIntervalRemoved(i0, i1);
					}
				});
			} catch (InterruptedException e) {
				throw new InterruptedRuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new UnexpectedException(e);
			}
		} else {
			getSupport().fireIntervalRemoved(i0, i1);
		}
	}

	public int getSize() {return size();}
	public Object getElementAt(int index) {return getList().get(index);}
	
	/**
	 * Ugly workaround for Swing bug.  Swing uses <code>equals</code> to compare
	 * model attributes when deciding whether to fire a property change event.  If
	 * you have the misfortune of switching between models whose contents happen
	 * to be the same at the moment, the switch is not made properly.  This hack
	 * tries to detect whether Swing is asking if the models are equals, and performs an
	 * identity-based comparison if so.  Note that <code>hashCode</code> does not
	 * need to be overriden, since this reimplemention does not widen the notion of
	 * equality.
	 * @see Bug report 4528403 in the bug parade
	 */
	public boolean equals(Object o) {
		StackTraceElement stack[] = (new Throwable()).getStackTrace();
		if (stack[1].getClassName().startsWith("javax.swing.")) {
			return this == o;
		} else {
			return super.equals(o);
		}
	}

	/**
	 * @post return instanceof BetterCollection
	 */
	protected abstract Collection getBase();

	protected abstract List getList();

	protected static abstract class ActiveIteratorBase implements Iterator {
		protected ActiveIteratorBase() {}
		protected abstract Iterator getIteratorBase();
		public boolean hasNext() {return getIteratorBase().hasNext();}
		public Object next() {return getIteratorBase().next();}
	}

}

private static abstract class DelegatedActiveCollectionBase extends ActiveCollectionBase {
	private transient ListDataListener activeBaseListener;

	protected DelegatedActiveCollectionBase() {}

	private ListDataListener getActiveBaseListener() {
		if (activeBaseListener == null) {
			activeBaseListener = new ListDataListener() {
				public void contentsChanged(ListDataEvent ev) {
					fireContentsChanged(ev.getIndex0(), ev.getIndex1());
				}
				public void intervalAdded(ListDataEvent ev) {
					fireIntervalAdded(ev.getIndex0(), ev.getIndex1());
				}
				public void intervalRemoved(ListDataEvent ev) {
					fireIntervalRemoved(ev.getIndex0(), ev.getIndex1());
				}
			};
		}
		return activeBaseListener;
	}

	public void addListDataListener(ListDataListener l) {
		if (getSupport().getListenerCount() == 0) getActiveBase().addListDataListener(getActiveBaseListener());
		super.addListDataListener(l);
	}
	public void removeListDataListener(ListDataListener l) {
		super.removeListDataListener(l);
		if (getSupport().getListenerCount() == 0) getActiveBase().removeListDataListener(getActiveBaseListener());
	}

	/**
	 * We require our specific implementation of the ActiveCollection interface
	 * so that we get direct access to its converted list.  Otherwise, we'd need
	 * to reconvert the list locally, at potentially great expense.
	 * <p>
	 * This code will not play nice with any other ActiveCollection implementations.
	 */
	protected abstract ActiveCollectionBase getActiveBase();
	protected Collection getBase() {return getActiveBase();}
	protected List getList() {return getActiveBase().getList();}
}

private static abstract class ActiveUnindexedCollection extends ActiveCollectionBase {

	protected transient List list;
	protected int lastSize;

	protected ActiveUnindexedCollection(int initialSize) {
		lastSize = initialSize;
	}

	protected void contentsChanged() {
		list = null;
		final int previousSize = lastSize;
		final int currentSize = getBase().size();
		lastSize = currentSize;
		if (previousSize > 0) fireIntervalRemoved(0, previousSize-1);
		if (currentSize > 0) fireIntervalAdded(0, currentSize - 1);
	}

	protected List getList() {
		if (list == null) list = new ArrayList(getBase());
		return list;
	}

	public void clear() {
		getBase().clear();
		contentsChanged();
	}

	public boolean add(Object o) {
		boolean changed = getBase().add(o);
		if (changed) contentsChanged();
		return changed;
	}

	public boolean addAll(Collection c) {
		boolean changed = getBase().addAll(c);
		if (changed) contentsChanged();
		return changed;
	}

	public boolean remove(Object o) {
		boolean changed = getBase().remove(o);
		if (changed) contentsChanged();
		return changed;
	}

	public boolean removeAll(Collection c) {
		boolean changed = getBase().removeAll(c);
		if (changed) contentsChanged();
		return changed;
	}

	public boolean retainAll(Collection c) {
		boolean changed = getBase().retainAll(c);
		if (changed) contentsChanged();
		return changed;
	}

	public Iterator iterator() {return new ActiveUnindexedCollectionIterator(getBase().iterator());}

	protected class ActiveUnindexedCollectionIterator extends ActiveIteratorBase {
		protected final Iterator it;
		protected ActiveUnindexedCollectionIterator(Iterator it) {this.it = it;}
		protected Iterator getIteratorBase() {return it;}
		public void remove() {
			getIteratorBase().remove();
			contentsChanged();
		}
	}

}

private static class ActiveCollection_ extends ActiveUnindexedCollection {
	private final Collection base;
	protected ActiveCollection_(Collection base) {
		super(base.size());
		assert base instanceof BetterCollection;
		this.base = base;
	}
	protected Collection getBase() {return base;}
}

private static class ActiveSet_ extends ActiveCollection_ implements ActiveSet {
	protected ActiveSet_(Set base) {super(base);}
}

private static class ActiveSortedSet_ extends ActiveUnindexedCollection implements ActiveSortedSet {
	private final SortedSet base;
	protected ActiveSortedSet_(SortedSet base) {
		super(base.size());
		assert base instanceof BetterCollection;
		this.base = base;
	}
	protected Collection getBase() {return base;}

	public Object last() {return base.last();}
	public Object first() {return base.first();}
	public Comparator comparator() {return base.comparator();}

	public SortedSet headSet(Object o) {
		return new ActiveSortedSubSet_(base.headSet(o), this);
	}
	public SortedSet tailSet(Object o) {
		return new ActiveSortedSubSet_(base.tailSet(o), this);
	}
	public SortedSet subSet(Object o1, Object o2) {
		return new ActiveSortedSubSet_(base.subSet(o1, o2), this);
	}
}

private static class ActiveSortedSubSet_ extends ActiveSortedSet_ {
	protected final ActiveUnindexedCollection origin;
	private ListDataListener originListener = null;

	protected ActiveSortedSubSet_(SortedSet base, ActiveSortedSet_ origin) {
		super(base);
		this.origin = origin;
	}

	public void addListDataListener(ListDataListener l) {
		super.addListDataListener(l);
		if (getSupport().getListenerCount() == 1) addOriginListener();
	}

	public void removeListDataListener(ListDataListener l) {
		super.removeListDataListener(l);
		if (getSupport().getListenerCount() == 0) removeOriginListener();
	}

	private void addOriginListener() {
		if (originListener == null) originListener = createOriginListener();
		origin.addListDataListener(originListener);
	}

	private void removeOriginListener() {
		assert originListener != null;
		origin.removeListDataListener(originListener);
		originListener = null;
	}

	protected ListDataListener createOriginListener() {
		return new ListDataListener() {
			public void contentsChanged(ListDataEvent ev) {
				assert false;
			}
			public void intervalAdded(ListDataEvent ev) {
				// DANGER:  assume we always add *all* elements to an empty list
				// final int previousSize = lastSize;
				final int currentSize = size();
				if (currentSize == 0) return;
				list = null;
				lastSize = currentSize;
				fireIntervalAdded(0, currentSize - 1);
			}
			public void intervalRemoved(ListDataEvent ev) {
				// DANGER:  assume we always remove *all* elements
				if (lastSize == 0) return;
				list = null;
				final int previousSize = lastSize;
				lastSize = 0;
				fireIntervalRemoved(0, previousSize - 1);
			}
		};
	}

	protected void contentsChanged() {
		origin.contentsChanged();
	}

}

private static class ActiveList_ extends ActiveCollectionBase implements ActiveList {

	private final List base;

	protected ActiveList_(List base) {
		assert base instanceof BetterCollection;
		this.base = base;
	}

	protected Collection getBase() {return base;}
	protected List getList() {return base;}

	public List subList(int fromIndex, int toIndex) {
		return new ActiveSubList_(getList().subList(fromIndex, toIndex), this, fromIndex);
	}

	public Object get(int index) {return getList().get(index);}
	public int indexOf(Object o) {return getList().indexOf(o);}
	public int lastIndexOf(Object o) {return getList().lastIndexOf(o);}

	protected void intervalAdded(int index0, int index1) {
		fireIntervalAdded(index0, index1);
	}

	protected void intervalRemoved(int index0, int index1) {
		fireIntervalRemoved(index0, index1);
	}

	protected void intervalChanged(int index0, int index1) {
		fireContentsChanged(index0, index1);
	}

	protected void contentsCleared(int oldSize) {
		if (oldSize == 0) return;
		intervalRemoved(0, oldSize-1);
	}

	public Object set(int index, Object o) {
		final Object r = getList().set(index, o);
		intervalChanged(index, index);
		return r;
	}

	public void clear() {
		final int s = size();
		getBase().clear();
		contentsCleared(s);
	}

	public boolean add(Object o) {
		final boolean changed = getBase().add(o);
		if (changed) {
			final int i = size() - 1;
			intervalAdded(i, i);
		}
		return changed;
	}

	public void add(int index, Object o) {
		final int s = size();
		getList().add(index, o);
		if (size() > s) intervalAdded(index, index);
	}

	public boolean addAll(Collection c) {
		final int s = size();
		boolean changed = getBase().addAll(c);
		if (changed) intervalAdded(s, size()-1);
		return changed;
	}

	public boolean addAll(int index, Collection c) {
		final int s = size();
		boolean changed = getBase().addAll(c);
		if (changed) intervalAdded(index, index + size() - s);
		return changed;
	}

	public Object remove(int index) {
		final Object r = getList().remove(index);
		intervalRemoved(index, index);
		return r;
	}

	public boolean remove(Object o1) {
		int i = 0;
		for (Iterator it = getBase().iterator(); it.hasNext(); i++) {
			Object o2 = it.next();
			if (Safe.equals(o1, o2)) {
				it.remove();
				intervalRemoved(i, i);
				return true;
			}
		}
		return false;
	}

	public boolean removeAll(Collection c) {
		if (c instanceof Lockable) {
			synchronized( ((Lockable) c).getLock() ) {
				return removeAll_(c);
			}
		} else {
			return removeAll_(c);
		}
	}

	protected boolean removeAll_(Collection c) {
		boolean changed = false, removing = false;
		int i = 0, begin = 0;
		for (Iterator it = getBase().iterator(); it.hasNext(); i++) {
			if (c.contains(it.next())) {
				it.remove();
				if (!removing) {
					removing = true;
					changed = true;
					begin = i;
				}
			} else if (removing) {
				intervalRemoved(begin, i-1);
				removing = false;
				i = begin;
			}
		}
		if (removing) intervalRemoved(begin, i-1);
		return changed;
	}

	public boolean retainAll(Collection c) {
		if (c instanceof Lockable) {
			synchronized( ((Lockable) c).getLock() ) {
				return retainAll_(c);
			}
		} else {
			return retainAll_(c);
		}
	}

	protected boolean retainAll_(Collection c) {
		boolean changed = false, removing = false;
		int i = 0, begin = 0;
		for (Iterator it = getBase().iterator(); it.hasNext(); i++) {
			if (!c.contains(it.next())) {
				it.remove();
				if (!removing) {
					removing = true;
					changed = true;
					begin = i;
				}
			} else if (removing) {
				intervalRemoved(begin, i-1);
				removing = false;
				i = begin;
			}
		}
		if (removing) intervalRemoved(begin, i-1);
		return changed;
	}

	public Iterator iterator() {return listIterator(0);}
	public ListIterator listIterator() {return listIterator(0);}
	public ListIterator listIterator(int index) {
		return new ActiveListIterator(getList().listIterator(index), index);
	}

	protected class ActiveListIterator implements ListIterator {
		protected final ListIterator it;
		protected int lastIndex;
		protected boolean cursorPastElement = false;
		protected ActiveListIterator(ListIterator it, int lastIndex) {
			this.it = it;
			this.lastIndex = lastIndex;
		}
		public boolean hasNext() {return it.hasNext();}
		public boolean hasPrevious() {return it.hasPrevious();}
		public int nextIndex() {return it.nextIndex();}
		public int previousIndex() {return it.previousIndex();}
		public Object next() {
			Object r = it.next();
			if (cursorPastElement) lastIndex++;
			cursorPastElement = true;
			return r;
		}
		public Object previous() {
			Object r = it.previous();
			if (!cursorPastElement) lastIndex--;
			cursorPastElement = false;
			return r;
		}
		public void set(Object o) {
			it.set(o);
			intervalChanged(lastIndex, lastIndex);
		}
		public void add(Object o) {
			it.add(o);
			intervalAdded(lastIndex, lastIndex);
			lastIndex++;
		}
		public void remove() {
			it.remove();
			intervalRemoved(lastIndex, lastIndex);
			if (cursorPastElement) lastIndex--;
		}
	}

}

private static class ActiveSubList_ extends ActiveList_ {
	protected final ActiveList_ origin;
	protected final int fromIndex;
	private ListDataListener originListener = null;

	protected ActiveSubList_(List base, ActiveList_ origin, int fromIndex) {
		super(base);
		this.origin = origin;
		this.fromIndex = fromIndex;
	}

	protected void intervalAdded(int index0, int index1) {
		super.intervalAdded(index0, index1);
		origin.intervalAdded(index0 + fromIndex, index1 + fromIndex);
	}

	protected void intervalRemoved(int index0, int index1) {
		super.intervalRemoved(index0, index1);
		origin.intervalRemoved(index0 + fromIndex, index1 + fromIndex);
	}

	protected void intervalChanged(int index0, int index1) {
		// we'll get a callback from the backing collection about the change,
		// then we'll fire the event to our own listeners
		origin.intervalChanged(index0 + fromIndex, index1 + fromIndex);
	}

	public void addListDataListener(ListDataListener l) {
		super.addListDataListener(l);
		if (getSupport().getListenerCount() == 1) addOriginListener();
	}

	public void removeListDataListener(ListDataListener l) {
		super.removeListDataListener(l);
		if (getSupport().getListenerCount() == 0) removeOriginListener();
	}

	private void addOriginListener() {
		if (originListener == null) originListener = createOriginListener();
		origin.addListDataListener(originListener);
	}

	private void removeOriginListener() {
		assert originListener != null;
		origin.removeListDataListener(originListener);
		originListener = null;
	}

	protected ListDataListener createOriginListener() {
		return new ListDataListener() {
			public void contentsChanged(ListDataEvent ev) {
				int oi0 = ev.getIndex0(), oi1 = ev.getIndex1();
				if (oi1 < oi0) {oi1 = oi0; oi0 = ev.getIndex1();}
				int toIndex = fromIndex + size();
				if (oi0 < toIndex && oi1 >= fromIndex) {
					int ii0 = Math.max(oi0, fromIndex) - fromIndex;
					int ii1 = Math.min(oi1, toIndex - 1) - fromIndex;
					fireContentsChanged(ii0, ii1);
				}
			}
			// ignore add/remove notifications, since it's illegal to modify
			// the backing collection directly; don't assert false since it's
			// only checked if/when the sublist is next accessed
			public void intervalAdded(ListDataEvent ev) {
			}
			public void intervalRemoved(ListDataEvent ev) {
			}
		};
	}
}

private static abstract class LockableActiveCollectionBase extends DelegatedActiveCollectionBase implements Lockable {
	protected final Object mutex;

	protected LockableActiveCollectionBase() {
		this(new Object());
	}
	protected LockableActiveCollectionBase(Object mutex) {
		this.mutex = mutex;
	}

	public Object getLock() {return mutex;}
	
	public boolean equals(Object o) {synchronized(mutex) {return getBase().equals(o);}}
	public int hashCode() {synchronized(mutex) {return getBase().hashCode();}}

	public Object getElementAt(int index) {synchronized(mutex) {return getList().get(index);}}

	public boolean isEmpty() {synchronized(mutex) {return getBase().isEmpty();}}
	public boolean contains(Object o) {synchronized(mutex) {return getBase().contains(o);}}
	public boolean containsAll(Collection c) {synchronized(mutex) {return getBase().containsAll(c);}}
	public int size() {synchronized(mutex) {return getBase().size();}}
	public Object[] toArray() {synchronized(mutex) {return getBase().toArray();}}
	public Object[] toArray(Object[] a) {synchronized(mutex) {return getBase().toArray(a);}}
	public String toString() {synchronized(mutex) {return getBase().toString();}}

	public boolean add(Object o) {synchronized(mutex) {return getBase().add(o);}}
	public boolean addAll(Collection c) {synchronized(mutex) {return getBase().addAll(c);}}
	public void clear() {synchronized(mutex) {getBase().clear();}}
	public boolean remove(Object o) {synchronized(mutex) {return getBase().remove(o);}}
	public boolean removeAll(Collection c) {synchronized(mutex) {return getBase().removeAll(c);}}
	public boolean retainAll(Collection c) {synchronized(mutex) {return getBase().retainAll(c);}}

	public Iterator iterator() {return new LockableActiveCollectionIterator(getBase().iterator());}

	protected class LockableActiveCollectionIterator extends ActiveIteratorBase {
		protected final Iterator it;
		protected LockableActiveCollectionIterator(Iterator it) {this.it = it;}
		protected Iterator getIteratorBase() {return it;}
		public void remove() {
			synchronized(mutex) {
				getIteratorBase().remove();
			}
		}
	}
}

private static class LockableActiveCollection_ extends LockableActiveCollectionBase {
	private final ActiveCollectionBase base;
	protected LockableActiveCollection_(ActiveCollection base) {
		this(base, new Object());
	}
	protected LockableActiveCollection_(ActiveCollection base, Object mutex) {
		super(mutex);
		this.base = (ActiveCollectionBase) base;
	}
	protected ActiveCollectionBase getActiveBase() {return base;}
}

private static class LockableActiveSet_ extends LockableActiveCollection_ implements ActiveSet {
	protected LockableActiveSet_(ActiveSet base) {super(base);}
	protected LockableActiveSet_(ActiveSet base, Object mutex) {super(base, mutex);}
}

private static class LockableActiveSortedSet_ extends LockableActiveCollectionBase implements ActiveSortedSet {
	private final ActiveSortedSet_ base;
	protected LockableActiveSortedSet_(ActiveSortedSet base) {
		this(base, new Object());
	}
	protected LockableActiveSortedSet_(ActiveSortedSet base, Object mutex) {
		super(mutex);
		this.base = (ActiveSortedSet_) base;
	}
	protected ActiveCollectionBase getActiveBase() {return base;}

	public Object last() {synchronized(mutex) {return base.last();}}
	public Object first() {synchronized(mutex) {return base.first();}}
	public Comparator comparator() {synchronized(mutex) {return base.comparator();}}

	public SortedSet headSet(Object o) {
		synchronized(mutex) {
			return new LockableActiveSortedSet_((ActiveSortedSet) base.headSet(o), mutex);
		}
	}
	public SortedSet tailSet(Object o) {
		synchronized(mutex) {
			return new LockableActiveSortedSet_((ActiveSortedSet) base.tailSet(o), mutex);
		}
	}
	public SortedSet subSet(Object o1, Object o2) {
		synchronized(mutex) {
			return new LockableActiveSortedSet_((ActiveSortedSet) base.subSet(o1, o2), mutex);
		}
	}
}

private static class LockableActiveList_ extends LockableActiveCollectionBase implements ActiveList {
	private final ActiveList_ base;

	protected LockableActiveList_(ActiveList base) {
		this(base, new Object());
	}
	protected LockableActiveList_(ActiveList base, Object mutex) {
		super(mutex);
		this.base = (ActiveList_) base;
	}

	protected ActiveCollectionBase getActiveBase() {return base;}

	public List subList(int fromIndex, int toIndex) {
		synchronized(mutex) {
			return new LockableActiveList_((ActiveList) base.subList(fromIndex, toIndex), mutex);
		}
	}

	public Object get(int index) {synchronized(mutex) {return base.get(index);}}
	public int indexOf(Object o) {synchronized(mutex) {return base.indexOf(o);}}
	public int lastIndexOf(Object o) {synchronized(mutex) {return base.lastIndexOf(o);}}
	public Object set(int index, Object o) {synchronized(mutex) {return base.set(index, o);}}
	public boolean addAll(int index, Collection c) {synchronized(mutex) {return base.addAll(index, c);}}
	public void add(int index, Object o) {synchronized(mutex) {base.add(index, o);}}
	public Object remove(int index) {synchronized(mutex) {return base.remove(index);}}

	public Iterator iterator() {return listIterator();}
	public ListIterator listIterator() {return new LockableActiveListIterator(base.listIterator());}
	public ListIterator listIterator(int index) {return new LockableActiveListIterator(base.listIterator(index));}

	protected class LockableActiveListIterator extends LockableActiveCollectionIterator implements ListIterator {
		protected LockableActiveListIterator(ListIterator it) {super(it);}
		protected ListIterator getListIteratorBase() {return (ListIterator) it;}
		public boolean hasPrevious() {return getListIteratorBase().hasPrevious();}
		public int nextIndex() {return getListIteratorBase().nextIndex();}
		public int previousIndex() {return getListIteratorBase().previousIndex();}
		public Object previous() {return getListIteratorBase().previous();}
		public void set(Object o) {synchronized(mutex) {getListIteratorBase().set(o);}}
		public void add(Object o) {synchronized(mutex) {getListIteratorBase().add(o);}}
	}

}

private static abstract class UnmodifiableActiveCollectionBase extends DelegatedActiveCollectionBase {

	protected UnmodifiableActiveCollectionBase() {}

	public void clear() {throw new UnsupportedOperationException("unmodifiable collection");}
	public boolean add(Object o) {throw new UnsupportedOperationException("unmodifiable collection");}
	public boolean addAll(Collection c) {throw new UnsupportedOperationException("unmodifiable collection");}
	public boolean remove(Object o) {throw new UnsupportedOperationException("unmodifiable collection");}
	public boolean removeAll(Collection c) {throw new UnsupportedOperationException("unmodifiable collection");}
	public boolean retainAll(Collection c) {throw new UnsupportedOperationException("unmodifiable collection");}
	public Iterator iterator() {return new ActiveUnmodifiableIterator(getBase().iterator());}

	protected class ActiveUnmodifiableIterator extends ActiveIteratorBase {
		protected final Iterator it;
		protected ActiveUnmodifiableIterator(Iterator it) {this.it = it;}
		protected Iterator getIteratorBase() {return it;}
		public void remove() {throw new UnsupportedOperationException("unmodifiable collection");}
	}

}

private static class UnmodifiableActiveCollection_ extends UnmodifiableActiveCollectionBase {
	private final ActiveCollectionBase activeBase;

	protected UnmodifiableActiveCollection_(ActiveCollection activeBase) {
		super();
		this.activeBase = (ActiveCollectionBase) activeBase;
	}

	protected ActiveCollectionBase getActiveBase() {return activeBase;}

}

private static class LockableUnmodifiableActiveCollection_ extends UnmodifiableActiveCollection_ implements Lockable {
	protected LockableUnmodifiableActiveCollection_(ActiveCollection activeBase) {
		super(activeBase);
		assert activeBase instanceof Lockable;
	}
	public Object getLock() {return ((Lockable) getActiveBase()).getLock();}
}

private static class UnmodifiableActiveSet_ extends UnmodifiableActiveCollection_ implements ActiveSet {
	protected UnmodifiableActiveSet_(ActiveSet activeBase) {super(activeBase);}
}

private static class LockableUnmodifiableActiveSet_ extends UnmodifiableActiveSet_ implements Lockable {
	protected LockableUnmodifiableActiveSet_(ActiveSet activeBase) {
		super(activeBase);
		assert activeBase instanceof Lockable;
	}
	public Object getLock() {return ((Lockable) getActiveBase()).getLock();}
}

private static class UnmodifiableActiveSortedSet_ extends UnmodifiableActiveCollectionBase implements ActiveSortedSet {
	protected ActiveSortedSet activeBase;

	protected UnmodifiableActiveSortedSet_(ActiveSortedSet activeBase) {
		super();
		this.activeBase = activeBase;
	}

	protected ActiveCollectionBase getActiveBase() {return (ActiveCollectionBase) activeBase;}

	public Object last() {return activeBase.last();}
	public Object first() {return activeBase.first();}
	public Comparator comparator() {return activeBase.comparator();}

	protected SortedSet wrapSubset(ActiveSortedSet subset) {
		return new UnmodifiableActiveSortedSet_(subset);
	}
	public SortedSet headSet(Object o) {
		return wrapSubset((ActiveSortedSet) activeBase.headSet(o));
	}
	public SortedSet tailSet(Object o) {
		return wrapSubset((ActiveSortedSet) activeBase.tailSet(o));
	}
	public SortedSet subSet(Object o1, Object o2) {
		return wrapSubset((ActiveSortedSet) activeBase.subSet(o1, o2));
	}
}

private static class LockableUnmodifiableActiveSortedSet_ extends UnmodifiableActiveSortedSet_ implements Lockable {
	protected LockableUnmodifiableActiveSortedSet_(ActiveSortedSet activeBase) {
		super(activeBase);
	}
	public Object getLock() {return ((Lockable) getActiveBase()).getLock();}
	protected SortedSet wrapSubset(ActiveSortedSet subset) {
		return new LockableUnmodifiableActiveSortedSet_(subset);
	}
}

private static class UnmodifiableActiveList_ extends UnmodifiableActiveCollectionBase implements ActiveList {
	private final ActiveList activeBase;

	protected UnmodifiableActiveList_(ActiveList activeBase) {
		super();
		this.activeBase = activeBase;
	}

	protected ActiveCollectionBase getActiveBase() {return (ActiveCollectionBase) activeBase;}

	protected List wrapSublist(ActiveList sublist) {
		return new UnmodifiableActiveList_(sublist);
	}
	public List subList(int fromIndex, int toIndex) {
		return wrapSublist((ActiveList) activeBase.subList(fromIndex, toIndex));
	}

	public Object get(int index) {return activeBase.get(index);}
	public int indexOf(Object o) {return activeBase.indexOf(o);}
	public int lastIndexOf(Object o) {return activeBase.lastIndexOf(o);}

	public Object set(int index, Object o) {throw new UnsupportedOperationException("unmodifiable collection");}
	public void add(int index, Object o) {throw new UnsupportedOperationException("unmodifiable collection");}
	public boolean addAll(int index, Collection c) {throw new UnsupportedOperationException("unmodifiable collection");}
	public Object remove(int index) {throw new UnsupportedOperationException("unmodifiable collection");}

	public Iterator iterator() {return listIterator();}
	public ListIterator listIterator() {return new UnmodifiableListIterator(activeBase.listIterator());}
	public ListIterator listIterator(int index) {return new UnmodifiableListIterator(activeBase.listIterator(index));}

	protected static class UnmodifiableListIterator implements ListIterator {
		protected final ListIterator it;
		protected UnmodifiableListIterator(ListIterator it) {
			this.it = it;
		}
		public boolean hasNext() {return it.hasNext();}
		public boolean hasPrevious() {return it.hasPrevious();}
		public int nextIndex() {return it.nextIndex();}
		public int previousIndex() {return it.previousIndex();}
		public Object next() {return it.next();}
		public Object previous() {return it.previous();}
		public void set(Object o) {throw new UnsupportedOperationException("unmodifiable collection");}
		public void add(Object o) {throw new UnsupportedOperationException("unmodifiable collection");}
		public void remove() {throw new UnsupportedOperationException("unmodifiable collection");}
	}
}

private static class LockableUnmodifiableActiveList_ extends UnmodifiableActiveList_ implements Lockable {
	protected LockableUnmodifiableActiveList_(ActiveList activeBase) {
		super(activeBase);
	}
	public Object getLock() {return ((Lockable) getActiveBase()).getLock();}
	protected List wrapSublist(ActiveList sublist) {
		return new LockableUnmodifiableActiveList_(sublist);
	}
}

	private ActiveCollections() {
	}

	/**
	 * Returns an active version of the specified collection that fires
	 * <code>ListDataEvent</code>s when the collection is mutated.  Note
	 * that all mutation must take place through the active version, or
	 * through objects that it returns (i.e. through iterators or
	 * subcollections).<p>
	 *
	 * The returned collection does <i>not</i> pass the hashCode and equals
	 * operations through to the backing collection, but relies on
	 * <tt>Object</tt>'s <tt>equals</tt> and <tt>hashCode</tt> methods.  This
	 * is necessary to preserve the contracts of these operations in the case
	 * that the backing collection is a set or a list.<p>
	 *
	 * The returned collection will be serializable if the specified collection is serializable.
	 *
	 * @param c the collection for which an active version is to be returned
	 * @return an active version of the specified collection
	 */
	public static ActiveCollection activeCollection(Collection c) {
		if (c instanceof ActiveCollection) return (ActiveCollection) c;
		return new ActiveCollection_(BetterCollections.betterCollection(c));
	}

	/**
	 * Returns an active version of the specified list that fires
	 * <code>ListDataEvent</code>s when the list is mutated.  Note
	 * that all mutation must take place through the active version, or
	 * through objects that it returns (i.e. through iterators or
	 * sublists).<p>
	 *
	 * The returned list will be serializable if the specified list is serializable.
	 *
	 * @param list the list for which an active version is to be returned
	 * @return an active version of the specified list
	 */
	public static ActiveList activeList(List list) {
		if (list instanceof ActiveList) return (ActiveList) list;
		return new ActiveList_(BetterCollections.betterList(list));
	}

	/**
	 * Returns an active version of the specified set that fires
	 * <code>ListDataEvent</code>s when the set is mutated.  Note
	 * that all mutation must take place through the active version, or
	 * through objects that it returns (i.e. through iterators or
	 * subsets).<p>
	 *
	 * The returned set will be serializable if the specified set is serializable.
	 *
	 * @param c the set for which an active version is to be returned
	 * @return an active version of the specified set
	 */
	public static ActiveSet activeSet(Set c) {
		if (c instanceof ActiveSet) return (ActiveSet) c;
		return new ActiveSet_(BetterCollections.betterSet(c));
	}

	/**
	 * Returns an active version of the specified sorted set that fires
	 * <code>ListDataEvent</code>s when the sorted set is mutated.  Note
	 * that all mutation must take place through the active version, or
	 * through objects that it returns (i.e. through iterators or
	 * subsorted sets).<p>
	 *
	 * The returned sorted set will be serializable if the specified sorted set is serializable.
	 *
	 * @param c the sorted set for which an active version is to be returned
	 * @return an active version of the specified sorted set
	 */
	public static ActiveSortedSet activeSortedSet(SortedSet c) {
		if (c instanceof ActiveSortedSet) return (ActiveSortedSet) c;
		return new ActiveSortedSet_(BetterCollections.betterSortedSet(c));
	}

	/**
	 * Returns a synchronized active version of the specified collection that fires
	 * <code>ListDataEvent</code>s when the collection is mutated.  Note
	 * that all mutation must take place through the active version, or
	 * through objects that it returns (i.e. through iterators or
	 * subcollections).<p>
	 *
	 * The returned collection will implement <code>Lockable</code> and will
	 * enforce mutual exclusion on all its operations.  To maintain a lock on
	 * the collection for the duration of more than one operation, you should
	 * synchronize on its lock.  This is absolutely required for iteration,
	 * for example:
	 * <pre>ActiveCollection bag = ActiveCollections.synchronizedActiveCollection(new ArrayList());
	 * bag.add("a");  bag.add("b");
	 * synchronized( ((Lockable) bag).getLock() ) {
	 *   Iterator it = bag.iterator();
	 *   while(it.hasNext()) {
	 *     System.out.println(it.next());
	 *   }
	 * }</pre>
	 * <p>
	 *
	 * The returned collection does <i>not</i> pass the hashCode and equals
	 * operations through to the backing collection, but relies on
	 * <tt>Object</tt>'s <tt>equals</tt> and <tt>hashCode</tt> methods.  This
	 * is necessary to preserve the contracts of these operations in the case
	 * that the backing collection is a set or a list.<p>
	 *
	 * The returned collection will be serializable if the specified collection is serializable.
	 *
	 * @param c the collection for which an active version is to be returned
	 * @return an active version of the specified collection
	 */
	public static ActiveCollection synchronizedActiveCollection(Collection c) {
		ActiveCollection ac = activeCollection(c);
		if (c instanceof Lockable) {
			return new LockableActiveCollection_(ac, ((Lockable) c).getLock());
		} else {
			return new LockableActiveCollection_(ac);
		}
	}

	/**
	 * Returns a synchronized active version of the specified list that fires
	 * <code>ListDataEvent</code>s when the list is mutated.  Note
	 * that all mutation must take place through the active version, or
	 * through objects that it returns (i.e. through iterators or
	 * subcollections).<p>
	 *
	 * The returned list will implement <code>Lockable</code> and will
	 * enforce mutual exclusion on all its operations.
	 * <p>
	 * The returned list will be serializable if the specified collection is serializable.
	 *
	 * @param c the list for which a synchronized active version is to be returned
	 * @return an active version of the specified list
	 */
	public static ActiveList synchronizedActiveList(List c) {
		ActiveList ac = activeList(c);
		if (c instanceof Lockable) {
			return new LockableActiveList_(ac, ((Lockable) c).getLock());
		} else {
			return new LockableActiveList_(ac);
		}
	}

	/**
	 * Returns a synchronized active version of the specified set that fires
	 * <code>ListDataEvent</code>s when the set is mutated.  Note
	 * that all mutation must take place through the active version, or
	 * through objects that it returns (i.e. through iterators or
	 * subcollections).<p>
	 *
	 * The returned set will implement <code>Lockable</code> and will
	 * enforce mutual exclusion on all its operations.
	 * <p>
	 * The returned set will be serializable if the specified collection is serializable.
	 *
	 * @param c the set for which a synchronized active version is to be returned
	 * @return an active version of the specified set
	 */
	public static ActiveSet synchronizedActiveSet(Set c) {
		ActiveSet ac = activeSet(c);
		if (c instanceof Lockable) {
			return new LockableActiveSet_(ac, ((Lockable) c).getLock());
		} else {
			return new LockableActiveSet_(ac);
		}
	}

	/**
	 * Returns a synchronized active version of the specified sorted set that fires
	 * <code>ListDataEvent</code>s when the set is mutated.  Note
	 * that all mutation must take place through the active version, or
	 * through objects that it returns (i.e. through iterators or
	 * subcollections).<p>
	 *
	 * The returned sorted set will implement <code>Lockable</code> and will
	 * enforce mutual exclusion on all its operations.
	 * <p>
	 * The returned sorted set will be serializable if the specified collection is serializable.
	 *
	 * @param c the sorted set for which a synchronized active version is to be returned
	 * @return an active version of the specified sorted set
	 */
	public static ActiveSortedSet synchronizedActiveSortedSet(SortedSet c) {
		ActiveSortedSet ac = activeSortedSet(c);
		if (c instanceof Lockable) {
			return new LockableActiveSortedSet_(ac, ((Lockable) c).getLock());
		} else {
			return new LockableActiveSortedSet_(ac);
		}
	}

	/**
	 * Returns an unmodifiable view of the specified active collection.
	 * This method allows modules to provide users with "read-only"
	 * access to internal collections. Query operations on the returned
	 * collection "read through" to the specified collection, and attempts to
	 * modify the returned collection, whether direct or via its iterator,
	 * result in an <code>UnsupportedOperationException</code>.<p>
	 *
	 * The returned collection does <i>not</i> pass the hashCode and equals
	 * operations through to the backing collection, but relies on
	 * <tt>Object</tt>'s <tt>equals</tt> and <tt>hashCode</tt> methods.  This
	 * is necessary to preserve the contracts of these operations in the case
	 * that the backing collection is a set or a list.<p>
	 *
	 * The returned collection will be serializable if the specified collection is serializable.
	 * The returned collection will be lockable if the specified collection is lockable.
	 *
	 * @param c the active collection for which an unmodifiable view is to be returned
	 * @return an active unmodifiable view of the specified collection
	 */
	public static ActiveCollection unmodifiableActiveCollection(ActiveCollection c) {
		if (c instanceof UnmodifiableActiveCollection_) return c;
		if (c instanceof Lockable) return new LockableUnmodifiableActiveCollection_(c);
		return new UnmodifiableActiveCollection_(c);
	}

	/**
	 * Returns an unmodifiable view of the specified active list.
	 * This method allows modules to provide users with "read-only"
	 * access to internal lists. Query operations on the returned
	 * list "read through" to the specified list, and attempts to
	 * modify the returned list, whether direct or via its iterator,
	 * result in an <code>UnsupportedOperationException</code>.<p>
	 *
	 * The returned list will be serializable if the specified list is serializable.
	 * The returned list will be lockable if the specified collection is lockable.
	 *
	 * @param c the active list for which an unmodifiable view is to be returned
	 * @return an active unmodifiable view of the specified list
	 */
	public static ActiveList unmodifiableActiveList(ActiveList c) {
		if (c instanceof UnmodifiableActiveList_) return c;
		if (c instanceof Lockable) return new LockableUnmodifiableActiveList_(c);
		return new UnmodifiableActiveList_(c);
	}

	/**
	 * Returns an unmodifiable view of the specified active set.
	 * This method allows modules to provide users with "read-only"
	 * access to internal sets. Query operations on the returned
	 * set "read through" to the specified set, and attempts to
	 * modify the returned set, whether direct or via its iterator,
	 * result in an <code>UnsupportedOperationException</code>.<p>
	 *
	 * The returned set will be serializable if the specified set is serializable.
	 * The returned set will be lockable if the specified collection is lockable.
	 *
	 * @param c the active set for which an unmodifiable view is to be returned
	 * @return an active unmodifiable view of the specified set
	 */
	public static ActiveSet unmodifiableActiveSet(ActiveSet c) {
		if (c instanceof UnmodifiableActiveSet_) return c;
		if (c instanceof Lockable) return new LockableUnmodifiableActiveSet_(c);
		return new UnmodifiableActiveSet_(c);
	}

	/**
	 * Returns an unmodifiable view of the specified active sorted set.
	 * This method allows modules to provide users with "read-only"
	 * access to internal sorted sets. Query operations on the returned
	 * sorted set "read through" to the specified sorted set, and attempts to
	 * modify the returned sorted set, whether direct or via its iterator,
	 * result in an <code>UnsupportedOperationException</code>.<p>
	 *
	 * The returned sorted set will be serializable if the specified sorted set is serializable.
	 * The returned sorted set will be lockable if the specified collection is lockable.
	 *
	 * @param c the active sorted set for which an unmodifiable view is to be returned
	 * @return an active unmodifiable view of the specified sorted set
	 */
	public static ActiveSortedSet unmodifiableActiveSortedSet(ActiveSortedSet c) {
		if (c instanceof UnmodifiableActiveSortedSet_) return c;
		if (c instanceof Lockable) return new LockableUnmodifiableActiveSortedSet_(c);
		return new UnmodifiableActiveSortedSet_(c);
	}
}
