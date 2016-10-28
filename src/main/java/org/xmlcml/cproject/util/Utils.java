package org.xmlcml.cproject.util;

public class Utils {

	/** returns substring from start inclusive to end inclusive.
	 * allows for strings of shorter length
	 * @param start
	 * @param end
	 * @return
	 */
	public static String truncate(String s, int start, int end) {
		if (s == null) return null;
		start = Math.min(s.length(), start);
		end = Math.min(s.length(), end);
		return s.substring(start, end);
	}
}
