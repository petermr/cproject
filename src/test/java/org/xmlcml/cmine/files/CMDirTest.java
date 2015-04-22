package org.xmlcml.cmine.files;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


public class CMDirTest {

	
	private static final Logger LOG = Logger.getLogger(CMDirTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static File CM_DIR = new File("src/test/resources/org/xmlcml/files/");
	public final static File PLOS0115884_DIR = new File(CM_DIR, "journal.pone.0115884");
	
	@Test
	public void testReadCMDir() {
		CMDir cmDir = new CMDir();
		cmDir.readDirectory(PLOS0115884_DIR);
		Assert.assertEquals("fileCount", 4, cmDir.getReservedFileList().size());
		Assert.assertTrue("XML", cmDir.hasExistingFulltextXML());
	}
	
//	@Test
//	// FIXME
//	public void testCMDir() throws IOException {
//		File container0115884 = new File("target/plosone/0115884/");
//		// copy so we don't write back into test area
//		FileUtils.copyDirectory(Fixtures.TEST_PLOSONE_0115884_DIR, container0115884);
//		String[] args = {
//			"-q", container0115884.toString(),
//		};
//		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
//		argProcessor.parseArgs(args);
//		CMDirList cmDirList = argProcessor.getCMDirList();
//		Assert.assertEquals(1,  cmDirList.size());
//		LOG.trace(cmDirList.get(0).toString());
//	}
}
