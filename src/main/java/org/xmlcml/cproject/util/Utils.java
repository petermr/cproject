package org.xmlcml.cproject.util;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

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
	/** get Files filtered by IOFilter.
	 * recurse through directories
	 * 
	 * @param dir
	 * @param ioFileFilter
	 * @return
	 */
	public static File[] getFilesWithFilter(File dir, IOFileFilter ioFileFilter) {
		Collection<File> fileCollection = FileUtils.listFiles(dir, ioFileFilter, TrueFileFilter.INSTANCE);
		return fileCollection.toArray(new File[0]);
	}

        /**
         * Ensure path in generic (UNIX-style) format can be used to match paths on current platform. 
         * 
         * @param genericPathRegexString Path regex expressed in UNIX style using (forward) slashes
         * @return The input path regex with path separators replaced with current platform equivalent if necessary.
         */
        public static String convertPathRegexToCurrentPlatform(String genericPathRegexString) {
            String sep = File.separator;
            String result = genericPathRegexString;
            if (sep.equalsIgnoreCase("\\")) { 
                String replS = "\\" + sep + "\\" + sep;
                result = genericPathRegexString.replaceAll("/", replS);
            }
            return result;
        }
}
