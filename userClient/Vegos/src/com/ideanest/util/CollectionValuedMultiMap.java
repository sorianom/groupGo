package com.ideanest.util;

import java.util.*;
import java.lang.reflect.*;

/**
 * Insert the type's description here.
 *
 * @author Piotr Kaminski (piotr@ideanest.com)
 * @version 1.1, 2002-03-10
 */
public abstract class CollectionValuedMultiMap extends AbstractMultiMap {

	private final CollectionFactory collectionFactory;

	public static interface CollectionFactory {
		Collection createValueCollection();
	};

	public static class ClonedCollectionFactory implements CollectionFactory, java.io.Serializable {
		private final Collection prototypeCollection;
		private final Method cloneMethod;
		public ClonedCollectionFactory(Collection prototypeCollection) {
			try {
				cloneMethod = prototypeCollection.getClass().getMethod("clone", null);
				this.prototypeCollection = (Collection) cloneMethod.invoke(prototypeCollection, Safe.EMPTY_OBJECT_ARRAY);
			} catch (NoSuchMethodException e) {
				throw new UnexpectedException(e);
			} catch (IllegalAccessException e) {
				throw new UnexpectedException(e);
			} catch (InvocationTargetException e) {
				throw new UnexpectedException(e);
			}
		}
		public Collection createValueCollection() {
			try {
				return (Collection) cloneMethod.invoke(prototypeCollection, Safe.EMPTY_OBJECT_ARRAY);
			} catch (IllegalAccessException e) {
				throw new UnexpectedException(e);
			} catch (InvocationTargetException e) {
				throw new UnexpectedException(e);
			}
		}
	};

	public CollectionValuedMultiMap(CollectionFactory collectionFactory) {
		this.collectionFactory = collectionFactory;
	}

	protected Collection createValueCollection() {
		return collectionFactory.createValueCollection();
	}
}
