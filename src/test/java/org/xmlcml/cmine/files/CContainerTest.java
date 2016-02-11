package org.xmlcml.cmine.files;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cmine.CMineFixtures;

public class CContainerTest {

	private static final Logger LOG = Logger.getLogger(CContainerTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testGetAllowedChildFile() {
		CContainer cProject = new CProject(new File(CMineFixtures.TEST_PROJECTS_DIR, "project3"));
		File file = cProject.getAllowedChildFile(CProject.EUPMC_RESULTS_JSON);
		Assert.assertNotNull("eupmc not null", file);
		Assert.assertNotNull("log not null", cProject.getAllowedChildFile(CContainer.LOG_XML));
		Assert.assertNotNull("unknown not null", cProject.getUnknownChildFile("unknown.txt"));
		Assert.assertNull("unknown null", cProject.getAllowedChildFile("unknown.txt"));
		Assert.assertNull("unknown null", cProject.getAllowedChildFile("junk"));
		Assert.assertNull("unknown null", cProject.getUnknownChildFile("junk"));
	}

}
