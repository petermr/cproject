package org.xmlcml.cmine.files;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
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
		new DefaultArgProcessor().parseArgs(args);
		Assert.assertTrue(cmDirectory.exists());
		CMDir cmDir = new CMDir(cmDirectory); 
		File fulltext_pdf = cmDir.getExistingFulltextPDF();
		Assert.assertTrue(fulltext_pdf.exists());
	}
	
	@Test
	/** creates new CMDirs for list of PDF.
	 * 
	 */
	public void testCreateCMDirs() throws IOException {
//		File cmDirectory = new File("target/testcreate/src_test_resources_org_xmlcml_files_misc_test_pdf_1471_2148_14_70_pdf");
//		if (cmDirectory.exists()) FileUtils.forceDelete(cmDirectory);
		String args = "-i src/test/resources/org/xmlcml/files/misc/theses/ -e pdf -o target/testcreate/theses/ --cmdir";
		new DefaultArgProcessor().parseArgs(args);
//		Assert.assertTrue(cmDirectory.exists());
//		CMDir cmDir = new CMDir(cmDirectory); 
//		File fulltext_pdf = cmDir.getExistingFulltextPDF();
//		Assert.assertTrue(fulltext_pdf.exists());
	}
	
}
