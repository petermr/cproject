package org.xmlcml.cmine.files;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cmine.CMineFixtures;
import org.xmlcml.cmine.args.DefaultArgProcessor;


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
	
	@Test
	/** creates a new CMDir for a PDF.
	 * 
	 */
	public void testCreateCMDir() throws IOException {
		File cmDirectory = new File("target/testcreate/src_test_resources_org_xmlcml_files_misc_test_pdf_1471_2148_14_70_pdf");
		if (cmDirectory.exists()) FileUtils.forceDelete(cmDirectory);
		String args = "-i src/test/resources/org/xmlcml/files/misc/test_pdf_1471-2148-14-70.pdf  -o target/testcreate/ --cmdir";
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
		Assert.assertTrue(cmDirectory.exists());
		CMDir cmDir = new CMDir(cmDirectory); 
		File fulltext_pdf = cmDir.getExistingFulltextPDF();
		Assert.assertTrue(fulltext_pdf.exists());
	}

	@Test
	public void testCreateCMDirsFromProject() throws IOException {
		File project1 = new File(CMineFixtures.PROJECTS_DIR, "project1");
		File targetProject1 = new File("target/projects/project1");
		FileUtils.copyDirectory(project1, targetProject1);
		String args = " --project "+targetProject1;
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		CMDirList cmDirList = argProcessor.getCMDirList();
		Assert.assertEquals("ctrees", 2, cmDirList.size());
		
	}

	@Test
	/** creates new CMDirs for list of PDF.
	 * 
	 */
	public void testCreateCMDirs() throws IOException {
		File inputDir = new File(CMineFixtures.MISC_DIR, "theses/");
		File outputDir = new File("target/testcreate/theses/");
		if (outputDir.exists()) FileUtils.forceDelete(outputDir);
		Assert.assertFalse(outputDir.exists());
		
		String args = "-i "+inputDir+" -e pdf -o "+outputDir+" --ctree";
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		Assert.assertTrue(outputDir.exists());
		Assert.assertEquals(3, outputDir.listFiles().length);
		CMDirList ctreeList = argProcessor.getCMDirList();
		Assert.assertEquals("ctrees", 3, ctreeList.size());
	}
	
}
