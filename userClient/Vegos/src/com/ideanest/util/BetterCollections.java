package com.ideanest.util;

import java.util.*;
import java.io.Serializable;

/**
 *
 */
public class BetterCollections {

private static abstract class BetterCollectionBase extends DelegatedCollection implements Serializable, BetterCollection {

	public boolean addAll(Collection c) {
		if (c instanceof Lockable) {
			synchronized( ((Lockable) c).getLock() ) {
				return getBase().addAll(c);
			}
		} else {
			return getBase().addAll(c);
		}
	}

	public boolean containsAll(Collection c) {
		if (c instanceof Lockable) {
			synchronized( ((Lockable) c).getLock() ) {
				return getBase().containsAll(c);
			}
		} else {
			return getBase().containsAll(c);
		}
	}

	public boolean removeAll(Collection c) {
		if (c instanceof Lockable) {
			synchronized( ((Lockable) c).getLock() ) {
				return getBase().removeAll(c);
			}
		} else {
			return getBase().removeAll(c);
		}
	}

	public boolean retainAll(Collection c) {
		if (c instanceof Lockable) {
			synchronized( ((Lockable) c).getLock() ) {
				return getBase().retainAll(c);
			}
		} else {
			return getBase().retainAll(c);
		}
	}

	public boolean equals(Object o) {return betterEquals(o);}
	
	protected boolean betterEquals(Object o) {
		if (o instanceof Lockable) {
			synchronized( ((Lockable) o).getLock() ) {
				return getBase().equals(o);
			}
		} else {
			return getBase().equals(o);
		}
	}

}

private static class BetterCollection_ extends BetterCollectionBase {
	private final Collection base;
	protected BetterCollection_(Collection base) {this.base = base;}
	protected Collection getBase() {return base;}
}

private static final class BetterSet_ extends BetterCollection_ implements Set {
	protected BetterSet_(Set base) {super(base);}
}

private static final class BetterSortedSet_ extends BetterCollectionBase implements SortedSet {
	private final SortedSet base;
	protected BetterSortedSet_(SortedSet base) {this.base = base;}
	protected Collection getBase() {return base;}

	public Comparator comparator() {return base.comparator();}
	public Object first() {return base.first();}
	public Object last() {return base.last();}

	public SortedSet headSet(Object o) {
		return new BetterSortedSet_(base.headSet(o));
	}
	public SortedSet tailSet(Object o) {
		return new BetterSortedSet_(base.tailSet(o));
	}
	public SortedSet subSet(Object o1, Object o2) {
		return new BetterSortedSet_(base.subSet(o1, o2));
	}
}

private static final class BetterList_ extends BetterCollectionBase implements List {
	private final List base;
	protected BetterList_(List base) {this.base = base;}
	protected Collection getBase() {return base;}

	public void add(int index, Object o) {base.add(index, o);}
	public Object get(int index) {return base.get(index);}
	public int indexOf(Object o) {return base.indexOf(o);}
	public int lastIndexOf(Object o) {return base.lastIndexOf(o);}
	public ListIterator listIterator() {return base.listIterator();}
	public ListIterator listIterator(int index) {return base.listIterator(index);}
	public Object remove(int index) {return base.remove(index);}
	public Object set(int index, Object o) {return base.set(index, o);}

	public boolean addAll(int index, Collection c) {
		if (c instanceof Lockable) {
			synchronized( ((Lockable) c).getLock() ) {
				return base.addAll(index, c);
			}
		} else {
			return base.addAll(index, c);
		}
	}

	public List subList(int fromIndex, int toIndex) {
		return new BetterList_(base.subList(fromIndex, toIndex));
	}
}

private static abstract class LockableCollectionBase implements Collection, BetterCollection, Serializable, Lockable {
	protected final Object lock;
	protected LockableCollectionBase() {this(new Object());}
	protected LockableCollectionBase(Object lock) {this.lock = lock;}
	public final Object getLock() {return lock;}
	/**
	 * @post return implements BetterCollection
	 */
	protected abstract Collection getBase();

	public boolean add(Object o) {synchronized(lock) {return getBase().add(o);}}
	public void clear() {synchronized(lock) {getBase().clear();}}
	public boolean contains(Object o) {synchronized(lock) {return getBase().contains(o);}}
	public boolean isEmpty() {synchronized(lock) {return getBase().isEmpty();}}
	public Iterator iterator() {return getBase().iterator();}
	public boolean remove(Object o) {synchronized(lock) {return getBase().remove(o);}}
	public int size() {synchronized(lock) {return getBase().size();}}
	public Object[] toArray() {synchronized(lock) {return getBase().toArray();}}
	public Object[] toArray(Object[] a) {synchronized(lock) {return getBase().toArray(a);}}
	public String toString() {synchronized(lock) {return getBase().toString();}}

	// since our base is assumed to be a BetterCollection, it'll take care of checking
	// if the argument is Lockable and synchronizing it if necessary
	public boolean addAll(Collection c) {synchronized(lock) {return getBase().addAll(c);}}
	public boolean containsAll(Collection c) {synchronized(lock) {return getBase().containsAll(c);}}
	public boolean removeAll(Collection c) {synchronized(lock) {return getBase().removeAll(c);}}
	public boolean retainAll(Collection c) {synchronized(lock) {return getBase().retainAll(c);}}

}

private static class LockableCollection_ extends LockableCollectionBase {
	private final Collection base;
	protected LockableCollection_(Collection base) {
		if (!(base instanceof BetterCollection)) throw new IllegalArgumentException("base must be a BetterCollection");
		this.base = base;
	}
	protected Collection getBase() {return base;}
}

private static final class LockableSet_ extends LockableCollection_ implements Set {
	protected LockableSet_(Set base) {super(base);}
	public boolean equals(Object o) {synchronized(lock) {return getBase().equals(o);}}
	public int hashCode() {synchronized(lock) {return getBase().hashCode();}}
}

private static final class LockableSortedSet_ extends LockableCollectionBase implements SortedSet {
	private final SortedSet base;
	protected LockableSortedSet_(SortedSet base) {this.base = base;}
	protected LockableSortedSet_(SortedSet base, Object lock) {super(lock); this.base = base;}
	protected Collection getBase() {return base;}
	public boolean equals(Object o) {synchronized(lock) {return getBase().equals(o);}}
	public int hashCode() {synchronized(lock) {return getBase().hashCode();}}

	public Comparator comparator() {synchronized(lock) {return base.comparator();}}
	public Object first() {synchronized(lock) {return base.first();}}
	public Object last() {synchronized(lock) {return base.last();}}

	public SortedSet headSet(Object o) {
		synchronized(lock) {
			return new LockableSortedSet_(base.headSet(o), lock);
		}
	}
	public SortedSet tailSet(Object o) {
		synchronized(lock) {
			return new LockableSortedSet_(base.tailSet(o), lock);
		}
	}
	public SortedSet subSet(Object o1, Object o2) {
		synchronized(lock) {
			return new LockableSortedSet_(base.subSet(o1, o2), lock);
		}
	}
}

private static final class LockableList_ extends LockableCollectionBase implements List {
	private final List base;
	protected LockableList_(List base) {this.base = base;}
	protected LockableList_(List base, Object lock) {super(lock); this.base = base;}
	protected Collection getBase() {return base;}
	public boolean equals(Object o) {synchronized(lock) {return getBase().equals(o);}}
	public int hashCode() {synchronized(lock) {return getBase().hashCode();}}

	public void add(int index, Object o) {synchronized(lock) {base.add(index, o);}}
	public Object get(int index) {synchronized(lock) {return base.get(index);}}
	public int indexOf(Object o) {synchronized(lock) {return base.indexOf(o);}}
	public int lastIndexOf(Object o) {synchronized(lock) {return base.lastIndexOf(o);}}
	public ListIterator listIterator() {return base.listIterator();}
	public ListIterator listIterator(int index) {return base.listIterator(index);}
	public Object remove(int index) {synchronized(lock) {return base.remove(index);}}
	public Object set(int index, Object o) {synchronized(lock) {return base.set(index, o);}}
	public boolean addAll(int index, Collection c) {synchronized(lock) {return base.addAll(index, c);}}

	public List subList(int fromIndex, int toIndex) {
		synchronized(lock) {
			return new LockableList_(base.subList(fromIndex, toIndex), lock);
		}
	}
}

private static abstract class UnmodifiableCollectionBase extends DelegatedCollection implements Serializable {
	/**
	 * @post return implements BetterCollection
	 */
	protected abstract Collection getBase();

	public boolean add(Object o) {throw new UnsupportedOperationException();}
	public boolean addAll(Collection c) {throw new UnsupportedOperationException();}
	public void clear() {throw new UnsupportedOperationException();}
	public boolean remove(Object o) {throw new UnsupportedOperationException();}
	public boolean removeAll(Collection c) {throw new UnsupportedOperationException();}
	public boolean retainAll(Collection c) {throw new UnsupportedOperationException();}
	public Iterator iterator() {return new UnmodifiableIterator(super.iterator());}
}

private static class UnmodifiableIterator implements Iterator {
	protected final Iterator base;
	protected UnmodifiableIterator(Iterator base) {this.base = base;}
	public boolean hasNext() {return base.hasNext();}
	public Object next() {return base.next();}
	public void remove() {throw new UnsupportedOperationException();}
}

private static class UnmodifiableCollection_ extends UnmodifiableCollectionBase {
	private final Collection base;
	protected UnmodifiableCollection_(Collection base) {this.base = base;}
	protected Collection getBase() {return base;}
}

private static final class LockableUnmodifiableCollection_ extends UnmodifiableCollection_ implements Lockable {
	protected LockableUnmodifiableCollection_(Collection base) {super(base);}
	public Object getLock() {return ((Lockable) getBase()).getLock();}
}

private static class UnmodifiableSet_ extends UnmodifiableCollection_ implements Set {
	protected UnmodifiableSet_(Set base) {super(base);}
}

private static final class LockableUnmodifiableSet_ extends UnmodifiableSet_ implements Lockable {
	protected LockableUnmodifiableSet_(Set base) {super(base);}
	public Object getLock() {return ((Lockable) getBase()).getLock();}
}

private static class UnmodifiableSortedSet_ extends UnmodifiableCollectionBase implements SortedSet {
	private final SortedSet base;
	protected UnmodifiableSortedSet_(SortedSet base) {this.base = base;}
	protected Collection getBase() {return base;}

	public Comparator comparator() {return base.comparator();}
	public Object first() {return base.first();}
	public Object last() {return base.last();}

	protected SortedSet wrapSubset(SortedSet subset) {
		return new UnmodifiableSortedSet_(subset);
	}
	public SortedSet headSet(Object o) {
		return wrapSubset(base.headSet(o));
	}
	public SortedSet tailSet(Object o) {
		return wrapSubset(base.tailSet(o));
	}
	public SortedSet subSet(Object o1, Object o2) {
		return wrapSubset(base.subSet(o1, o2));
	}
}

private static final class LockableUnmodifiableSortedSet_ extends UnmodifiableSortedSet_ implements Lockable {
	protected LockableUnmodifiableSortedSet_(SortedSet base) {super(base);}
	public Object getLock() {return ((Lockable) getBase()).getLock();}
	protected SortedSet wrapSubset(SortedSet subset) {
		return new LockableUnmodifiableSortedSet_(subset);
	}
}

private static class UnmodifiableList_ extends UnmodifiableCollectionBase implements List {
	private final List base;
	protected UnmodifiableList_(List base) {this.base = base;}
	protected Collection getBase() {return base;}
	
	public void add(int index, Object o) {throw new UnsupportedOperationException();}
	public boolean addAll(int index, Collection c) {throw new UnsupportedOperationException();}
	public Object get(int index) {return base.get(index);}
	public int indexOf(Object o) {return base.indexOf(o);}
	public int lastIndexOf(Object o) {return base.lastIndexOf(o);}
	public ListIterator listIterator() {return base.listIterator();}
	public ListIterator listIterator(int index) {return base.listIterator(index);}
	public Object remove(int index) {throw new UnsupportedOperationException();}
	public Object set(int index, Object o) {throw new UnsupportedOperationException();}

	protected List wrapSublist(List sublist) {
		return new UnmodifiableList_(sublist);
	}
	public List subList(int fromIndex, int toIndex) {
		return wrapSublist(base.subList(fromIndex, toIndex));
	}
}

private static final class LockableUnmodifiableList_ extends UnmodifiableList_ implements Lockable {
	protected LockableUnmodifiableList_(List base) {super(base);}
	public Object getLock() {return ((Lockable) getBase()).getLock();}
	protected List wrapSublist(List sublist) {
		return new LockableUnmodifiableList_(sublist);
	}
}

	/**
	 * No instances allowed, just a bunch of static methods.
	 */
	private BetterCollections() {
	}

	public static Collection betterCollection(Collection c) {
		if (c instanceof BetterCollection) return c;
		if (c instanceof Lockable) throw new NotYetImplementedException("can't wrap non-better lockables");
		return new BetterCollection_(c);
	}

	public static List betterList(List c) {
		if (c instanceof BetterCollection) return c;
		if (c instanceof Lockable) throw new NotYetImplementedException("can't wrap non-better lockables");
		return new BetterList_(c);
	}

	public static Set betterSet(Set c) {
		if (c instanceof BetterCollection) return c;
		if (c instanceof Lockable) throw new NotYetImplementedException("can't wrap non-better lockables");
		return new BetterSet_(c);
	}

	public static SortedSet betterSortedSet(SortedSet c) {
		if (c instanceof BetterCollection) return c;
		if (c instanceof Lockable) throw new NotYetImplementedException("can't wrap non-better lockables");
		return new BetterSortedSet_(c);
	}

	public static Collection synchronizedCollection(Collection c) {
		if (c instanceof Lockable) return c;
		else return new LockableCollection_(betterCollection(c));
	}

	public static List synchronizedList(List c) {
		if (c instanceof Lockable) return c;
		else return new LockableList_(betterList(c));
	}

	public static Set synchronizedSet(Set c) {
		if (c instanceof Lockable) return c;
		else return new LockableSet_(betterSet(c));
	}

	public static SortedSet synchronizedSortedSet(SortedSet c) {
		if (c instanceof Lockable) return c;
		else return new LockableSortedSet_(betterSortedSet(c));
	}

	public static Collection unmodifiableCollection(Collection c) {
		if (c instanceof UnmodifiableCollectionBase) return c;
		if (c instanceof Lockable) {
			if (!(c instanceof BetterCollection)) throw new NotYetImplementedException("can't wrap non-better lockable collection");
			return new LockableUnmodifiableCollection_(c);
		}
		return new UnmodifiableCollection_(betterCollection(c));
	}

	public static List unmodifiableList(List c) {
		if (c instanceof UnmodifiableCollectionBase) return c;
		if (c instanceof Lockable) {
			if (!(c instanceof BetterCollection)) throw new NotYetImplementedException("can't wrap non-better lockable collection");
			return new LockableUnmodifiableList_(c);
		}
		return new UnmodifiableList_(betterList(c));
	}

	public static Set unmodifiableSet(Set c) {
		if (c instanceof UnmodifiableCollectionBase) return c;
		if (c instanceof Lockable) {
			if (!(c instanceof BetterCollection)) throw new NotYetImplementedException("can't wrap non-better lockable collection");
			return new LockableUnmodifiableSet_(c);
		}
		return new UnmodifiableSet_(betterSet(c));
	}

	public static SortedSet unmodifiableSortedSet(SortedSet c) {
		if (c instanceof UnmodifiableCollectionBase) return c;
		if (c instanceof Lockable) {
			if (!(c instanceof BetterCollection)) throw new NotYetImplementedException("can't wrap non-better lockable collection");
			return new LockableUnmodifiableSortedSet_(c);
		}
		return new UnmodifiableSortedSet_(betterSortedSet(c));
	}
}
