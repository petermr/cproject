package org.xmlcml.cmine.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class CMineTestFixtures {

	private static final Logger LOG = Logger.getLogger(CMineTestFixtures.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static void cleanAndCopyDir(File sourceDir, File targetDir) throws IOException {
		if (targetDir.exists()) FileUtils.forceDelete(targetDir);
		FileUtils.copyDirectory(sourceDir, targetDir);
	}



}
