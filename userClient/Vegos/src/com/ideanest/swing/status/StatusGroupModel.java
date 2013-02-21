package com.ideanest.swing.status;

/**
 * This type was created in VisualAge.
 *
 * @author Piotr Kaminski
 */
public interface StatusGroupModel {

	void addStatusGroupListener(StatusGroupListener l);

	int getSize();

	StatusModel getStatusAt(int index);

	void removeStatusGroupListener(StatusGroupListener l);
}
