package com.ideanest.swing.status;

import java.util.*;
/**
 * This type was created in VisualAge.
 *
 * @author Piotr Kaminski
 */
public class StatusGroupEvent extends EventObject {
	public static final int STATUS_CHANGED = 0;
	public static final int STATUS_ADDED = 1;
	public static final int STATUS_REMOVED = 2;

	private final int type;
	private final int index;

	/**
	 * StatusGroupEvent constructor comment.
	 * @param source java.lang.Object
	 */
	public StatusGroupEvent(Object source, int type, int index) {
		super(source);
		this.type = type;
		this.index = index;
	}

	public int getIndex() {return index;}

	public int getType() {return type;}
}
