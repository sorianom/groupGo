package com.ideanest.util;

import java.util.*;
/**
 * Insert the type's description here.
 *
 * @author Piotr Kaminski (piotr@ideanest.com)
 * @version 1.0, 2002-02-21
 */
public interface MultiMap {
	public interface Entry extends Map.Entry {
	}

	boolean add(Object key, Object value);

	boolean addAll(MultiMap map);

	boolean addAll(Map map);

	void clear();

	boolean contains(Object key, Object value);

	boolean containsKey(Object key);

	boolean containsValue(Object value);

	Set entrySet();

	boolean equals(Object o);

	Collection get(Object key);

	int hashCode();

	boolean isEmpty();

	Set keySet();

	boolean remove(Object key);

	boolean remove(Object key, Object value);

	int size();

	Collection values();
}
