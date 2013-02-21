package com.ideanest.swing.status;

import java.util.*;
/**
 * This type was created in VisualAge.
 *
 * @author Piotr Kaminski
 */
public interface StatusGroupListener extends EventListener {

	void statusAdded(StatusGroupEvent ev);

	void statusChanged(StatusGroupEvent ev);

	void statusRemoved(StatusGroupEvent ev);
}
