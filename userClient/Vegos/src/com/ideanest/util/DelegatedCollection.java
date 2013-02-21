package com.ideanest.util;

import java.util.*;

/**
 * Delegates all collection operations, including equals and hashCode, to
 * a base collection.
 */

public abstract class DelegatedCollection implements Collection {
	
	public boolean equals(Object o) {return getBase().equals(o);}
	public int hashCode() {return getBase().hashCode();}

	public boolean add(Object o) {return getBase().add(o);}
	public boolean addAll(Collection c) {return getBase().addAll(c);}
	public void clear() {getBase().clear();}
	public boolean contains(Object o) {return getBase().contains(o);}
	public boolean containsAll(Collection c) {return getBase().containsAll(c);}
	public boolean isEmpty() {return getBase().isEmpty();}
	public Iterator iterator() {return getBase().iterator();}
	public boolean remove(Object o) {return getBase().remove(o);}
	public boolean removeAll(Collection c) {return getBase().removeAll(c);}
	public boolean retainAll(Collection c) {return getBase().retainAll(c);}
	public int size() {return getBase().size();}
	public Object[] toArray() {return getBase().toArray();}
	public Object[] toArray(Object[] a) {return getBase().toArray(a);}
	public String toString() {return getBase().toString();}
	protected abstract Collection getBase();
}
