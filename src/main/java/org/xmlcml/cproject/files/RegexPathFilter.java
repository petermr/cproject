package org.xmlcml.cproject.files;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class RegexPathFilter implements IOFileFilter {
	private static final Logger LOG = Logger.getLogger(RegexPathFilter.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private Pattern pattern;

	public RegexPathFilter(Pattern pattern) {
		this.pattern = pattern;
	}
	/** this one is  called by FileUtils.listFiles
	 * 
	 */
	public boolean accept(File file) {
		String path = file.getAbsolutePath();
		Matcher matcher = pattern.matcher(path);
		boolean matches = matcher.matches();
		return matches;
	}

	/** this isn't called.*/
	public boolean accept(File dir, String name) {
		LOG.debug("DIR"+dir+"/"+name);
		return false;
	}
	
	public Pattern getPattern() {
		return pattern;
	}

}
