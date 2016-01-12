package org.xmlcml.cmine.files;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cmine.CMineFixtures;
import org.xmlcml.cmine.args.DefaultArgProcessor;

public class CProjectUnzipTest {

	private static final Logger LOG = Logger.getLogger(CProjectUnzipTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private File zipsDir;
	private File targetZips;

	@Test
	public void testUnzip() throws IOException {
		copyToAndCleanOutDir(new File(CMineFixtures.MISC_DIR, "zips"));
		String args = "-i fulltext.xml -o scholarly.html --project "+targetZips;
		LOG.debug(args);
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
	}

	@Test
	public void testUnzipWithArgs() throws IOException {
		copyToAndCleanOutDir(new File(CMineFixtures.MISC_DIR, "zips"));
		String args = "-i fulltext.xml --unzip --include .*\\.XML --rename .*\\.XML fulltext.xml --project "+targetZips;
		LOG.debug(args);
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
	}

	@Test
	@Ignore // large
	public void testUnzipLarge() throws IOException {
		copyToAndCleanOutDir(new File("../patents/I20150317/UTIL08979"));
		String args = "-i fulltext.xml --unzip --include .*\\.XML --rename .*\\.XML fulltext.xml --project "+targetZips;
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
	}

	private void copyToAndCleanOutDir(File zipsDir) throws IOException {
		targetZips = new File("target/zips");
		if (targetZips.exists()) {
			FileUtils.deleteDirectory(targetZips);
		}
		FileUtils.copyDirectory(zipsDir,  targetZips);
		
	}
}
