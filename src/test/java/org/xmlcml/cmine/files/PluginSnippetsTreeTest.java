package org.xmlcml.cmine.files;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cmine.CMineFixtures;
import org.xmlcml.cmine.util.CMineTestFixtures;

public class PluginSnippetsTreeTest {

	private static final Logger LOG = Logger.getLogger(PluginSnippetsTreeTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testSnippetsByCTreeName() throws IOException {
		File zikaFile = new File(CMineFixtures.TEST_RESULTS_DIR, "zika");
		Assert.assertTrue(""+zikaFile, zikaFile.exists());
		Assert.assertTrue(new File(zikaFile, "sequence.dnaprimer.snippets.xml").exists());
		PluginSnippetsTree pluginSnippetsTree = CMineTestFixtures.createPluginSnippetsTree(zikaFile, "sequence.dnaprimer.snippets.xml");
		// [PMC4654492, PMC4671560]
		SnippetsTree snippetsTree = pluginSnippetsTree.getOrCreateSnippetsTreeByCTreeName().get("PMC4654492");
		Assert.assertNotNull(snippetsTree);
		Assert.assertEquals("cTreeName", "PMC4654492", snippetsTree.getCTreeName());
		snippetsTree = pluginSnippetsTree.getOrCreateSnippetsTreeByCTreeName().get("PMC4654493");
		Assert.assertNull(snippetsTree);
	}


	//==================================
	

}
