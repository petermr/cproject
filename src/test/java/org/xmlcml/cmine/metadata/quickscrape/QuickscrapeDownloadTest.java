package org.xmlcml.cmine.metadata.quickscrape;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cmine.CMineFixtures;
import org.xmlcml.cmine.files.CProject;

public class QuickscrapeDownloadTest {
	
	private static final Logger LOG = Logger.getLogger(QuickscrapeDownloadTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}


	@Test
	public void testCreateURLs() throws IOException {
		CProject cProject = createTargetDirectoryAndProject(CMineFixtures.GETPAPERS_20160601, new File(CMineFixtures.GETPAPERS_TARGET, "20160601small"));
		File urlFile      = cProject.createAllowedFile(CProject.URL_LIST);
		FileUtils.deleteQuietly(urlFile);
		cProject.extractShuffledUrlsFromCrossrefToFile(urlFile);
		Assert.assertTrue(urlFile.exists());
		Assert.assertEquals(20, FileUtils.readLines(urlFile).size());
	}

	// -----------------------------
	// make a clean copy in target
	private CProject createTargetDirectoryAndProject(File cProjectDir, File targetDir) throws IOException {
		FileUtils.deleteQuietly(targetDir);
		FileUtils.copyDirectory(cProjectDir, targetDir);
		CProject cProject = new CProject(targetDir);
		return cProject;
	}

}
