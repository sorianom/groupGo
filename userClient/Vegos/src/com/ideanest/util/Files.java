package com.ideanest.util;

/**
 * This type was created in VisualAge.
 *
 * @author Piotr Kaminski
 */

import java.io.*;
import java.net.*;

public final class Files {

	private Files() {
	}

	/**
	 * Fixes a platform dependent filename to standard URI form.
	 *
	 * @param str The string to fix.
	 *
	 * @return Returns the fixed URI string.
	 */
	public static String filenameToURI(String str) {

		// handle platform dependent strings
		str = str.replace(java.io.File.separatorChar, '/');

		// Windows fix
		if (str.length() >= 2) {
			if (str.charAt(1) == ':') {
				char ch0 = Character.toUpperCase(str.charAt(0));
				if (ch0 >= 'A' && ch0 <= 'Z') {
					str = "/" + str;
				}
			}
		}

		// done
		return str;
	}

	public static URL fileToURL(File file) throws MalformedURLException, IOException {
		return new URL("file", "", filenameToURI(file.getCanonicalPath()));
	}

	/**
	 * Check whether the <code>child</code> file is eventually contained within the
	 * <code>parent</code> file.  The algorithm uses the canonical version of the
	 * path for both files.
	 *
	 * @param parent the candidate parent file, which should be a directory
	 * @param child the candidate child, which can be a directory or a file
	 * @return whether <code>child</code> is in fact contained within <code>parent</code>
	 * @exception IOException if something fails and the answer is inconclusive
	 */
	public static boolean isParent(File parent, File child) throws IOException {
		parent = new File(parent.getCanonicalPath());
		String s = child.getCanonicalPath();
		boolean inside = false;
		while(s != null) {
			child = new File(s);
			if (parent.equals(child)) {
				inside = true;
				break;
			}
			s = child.getParent();
		}
		return inside;
	}

	/**
	 * Fixes a platform dependent filename to standard URI form.
	 *
	 * @param str The string to fix.
	 *
	 * @return Returns the fixed URI string.
	 */
	public static String URIToFilename(String str) {

		// Windows fix
		if (str.length() >= 3) {
			if (str.charAt(0) == '/' && str.charAt(2) == ':') {
				char ch1 = Character.toUpperCase(str.charAt(1));
				if (ch1 >= 'A' && ch1 <= 'Z') {
					str = str.substring(1);
				}
			}
		}

		// handle platform dependent strings
		str = str.replace('/', java.io.File.separatorChar);

		// done
		return str;
	}

	public static File URLToFile(URL url) throws MalformedURLException {
		if (!"file".equals(url.getProtocol())) throw new MalformedURLException("URL protocol must be 'file'.");
		return new File(URIToFilename(url.getFile()));
	}
}
