package org.xmlcml.cmine.files;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cmine.CMineFixtures;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.util.CMineTestFixtures;


public class CTreeTest {

	private static final String TEST_CREATE = "target/testcreate";
	private static final String TARGET_THESES = TEST_CREATE+"/theses/";
	
	private static final Logger LOG = Logger.getLogger(CTreeTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
//	public final static File CM_DIR = new File("src/test/resources/org/xmlcml/files/");
	public final static File PLOS0115884_DIR = new File(CMineFixtures.CMINE_DIR, "journal.pone.0115884");
	
	@Test
	public void testReadCTree() {
		CTree cTree = new CTree();
		cTree.readDirectory(PLOS0115884_DIR);
		Assert.assertEquals("fileCount", 4, cTree.getReservedFileList().size());
		Assert.assertTrue("XML", cTree.hasExistingFulltextXML());
	}
	
	@Test
	/** creates a new CTree for a PDF.
	 * 
	 */
	public void testCreateCTree() throws IOException {
		File cTreeDirectory = new File(TEST_CREATE+"/test_pdf_1471_2148_14_70_pdf");
		if (cTreeDirectory.exists()) FileUtils.forceDelete(cTreeDirectory);
		String args = "-i "+CMineFixtures.MISC_DIR+"/test_pdf_1471-2148-14-70.pdf  -o target/testcreate/ --ctree";
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
		Assert.assertTrue(cTreeDirectory.exists());
		CTree cTree = new CTree(cTreeDirectory); 
		File fulltext_pdf = cTree.getExistingFulltextPDF();
		Assert.assertTrue(fulltext_pdf.exists());
	}

	@Test
	public void testCreateCTreesFromProject() throws IOException {
		File project1 = new File(CMineFixtures.PROJECTS_DIR, "project1");
		File targetProject1 = new File("target/projects/project1");
		FileUtils.copyDirectory(project1, targetProject1);
		String args = " --project "+targetProject1;
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		CTreeList cTreeList = argProcessor.getCTreeList();
		Assert.assertEquals("ctrees", 2, cTreeList.size());
		
	}

	@Test
	/** creates new CTrees for list of PDF.
	 * 
	 * SHOWCASE
	 * 
	 * takes single directory with several child PDFs and transforms them into child CTrees 
	 * with "fulltext.pdf" in each.
	 */
	public void testCreateCTreesUsingCTreeCommand() throws IOException {
		File inputDir = new File(CMineFixtures.MISC_DIR, "theses/");
		File outputDir = new File(TARGET_THESES);
		if (outputDir.exists()) FileUtils.forceDelete(outputDir);
		Assert.assertFalse(outputDir.exists());
		
		String args = "-i "+inputDir+" -e pdf -o "+outputDir+" --ctree";
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		Assert.assertTrue(outputDir.exists());
		File[] files = outputDir.listFiles();
		Arrays.sort(files);
		Assert.assertEquals(3, files.length);
		Assert.assertEquals("target:"+TARGET_THESES+"HalThesis1_pdf", 
				TARGET_THESES+"HalThesis1_pdf", files[0].getPath());
		File[] childFiles = files[0].listFiles();
		Assert.assertEquals(1, childFiles.length);
		Assert.assertEquals("fulltext.pdf", childFiles[0].getName());
		CTreeList ctreeList = argProcessor.getCTreeList();
		Assert.assertEquals("ctrees", 3, ctreeList.size());
	}
	
	@Test
	/** creates new CTrees for list of PDF.
	 * 
	 * SHOWCASE
	 * 
	 * takes several PDFs and transforms them into a project with child CTrees 
	 * with "fulltext.pdf" in each.
	 */
	public void testCreateCTreesUsingProject() throws IOException {
		File inputDir = new File(CMineFixtures.MISC_DIR, "theses/");
		File projectDir = new File(TARGET_THESES);
		if (projectDir.exists()) FileUtils.forceDelete(projectDir);
		Assert.assertFalse(projectDir.exists());
		
		String args = "-i "+inputDir+" -e pdf --project "+projectDir;
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		Assert.assertTrue(projectDir.exists());
		File[] files = projectDir.listFiles();
		Arrays.sort(files);
		Assert.assertEquals(3, files.length);
		Assert.assertEquals(projectDir+"/HalThesis1_pdf", files[0].getPath());
		File[] childFiles = files[0].listFiles();
		Assert.assertEquals(1, childFiles.length);
		Assert.assertEquals("fulltext.pdf", childFiles[0].getName());
		CTreeList ctreeList = argProcessor.getCTreeList();
		Assert.assertEquals("ctrees", 3, ctreeList.size());
	}
	
	@Test
	public void testCTreeContent1() {
		CProject cProject = new CProject(new File(CMineFixtures.PROJECTS_DIR, "project1"));
		CTreeList cTreeList = cProject.getCTreeList();
		CTree cTree1 = cTreeList.get(0);
		cTree1.getOrCreateFilesDirectoryCTreeLists();
		List<File> allChildDirectoryList = cTree1.getAllChildDirectoryList();
		Assert.assertEquals("all child dir", 0, allChildDirectoryList.size());
		List<File> allChildFileList = cTree1.getAllChildFileList();
		Assert.assertEquals("all child file", 1, allChildFileList.size());

	}
	
	@Test
	public void testGlobFileList() {
		File pmc4417228 = new File(CMineFixtures.PROJECTS_DIR, "project2/PMC4417228/");
		CTree cTree = new CTree(pmc4417228);
		// NOTE: The "**" is required
		List<File> fileList = cTree.extractFiles("**/fulltext.*");
		Assert.assertEquals(2,  fileList.size());
		// sorting problem
//		Assert.assertEquals("src/test/resources/org/xmlcml/files/projects/project2/PMC4417228/fulltext.pdf",  
//				fileList.get(0).toString());
//		Assert.assertEquals("src/test/resources/org/xmlcml/files/projects/project2/PMC4417228/fulltext.xml",  
//				fileList.get(1).toString());
	}
	
	
	@Test
	public void testGlobFileListAndXML() {
		File pmc4417228 = new File(CMineFixtures.PROJECTS_DIR, "project2/PMC4417228/");
		CTree cTree = new CTree(pmc4417228);
		List<XMLSnippets> xpathSnippetsList = cTree.extractXPathSnippetsList("**/fulltext.xml", "//kwd");
		Assert.assertEquals(1, xpathSnippetsList.size());
		XMLSnippets snippets0 = xpathSnippetsList.get(0);
		Assert.assertEquals(10, snippets0.size());
		Assert.assertEquals("Central Europe", snippets0.getValue(0));
		Assert.assertEquals("Habitats", snippets0.getValue(4));
		Assert.assertEquals("Sustainability", snippets0.getValue(9));
	}
	
	@Test
	public void testGlobFileAndXpathCommand() throws IOException {
		File targetDir = new File("target/glob/pmc4417228");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.PROJECTS_DIR, "project2/PMC4417228"), targetDir);
		String output = "snippets.xml";
		String args = " -q " + targetDir+" --search file(**/fulltext.xml)xpath(//kwd) -o "+output;
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
		for (CTree cTree : argProcessor.getCTreeList()) {
			List<XMLSnippets> snippetsList = cTree.getSnippetsList();
			LOG.debug("SNIPx "+snippetsList);
		}
	}

	@Test
	public void testGlobResultsAndXpathCommand() throws IOException {
		File targetDir = new File("target/glob/project3/ctree1");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.PROJECTS_DIR, "project3/ctree1"), targetDir);
		String output = "snippets.xml";
		String args = " -q " + targetDir+" --search file(**/results.xml)xpath(//result) -o "+output;
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
		for (CTree cTree : argProcessor.getCTreeList()) {
			List<XMLSnippets> snippetsList = cTree.getSnippetsList();
			LOG.debug("SNIPy "+snippetsList);
		}
	}

//	Not working - use CProject
//	@Test
//	public void testGlobFilesAndXpathCommand() {
//		File pmc4417228 = new File(CMineFixtures.PROJECTS_DIR, "project2/PMC4417228");
//		File pmc4521097 = new File(CMineFixtures.PROJECTS_DIR, "project2/PMC4521097");
//		File pmc4632522 = new File(CMineFixtures.PROJECTS_DIR, "project2/PMC4632522");
//		String output = "../../project2/snippets.xml";
//		String args = " -q " + pmc4417228 + " " + pmc4521097 + " " + pmc4632522 + " --search file(**/fulltext.xml)xpath(//kwd) -o "+output;
//		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
//		argProcessor.parseArgs(args);
//		argProcessor.runAndOutput();
//		for (CTree cTree : argProcessor.getCTreeList()) {
//			List<XMLSnippets> snippetsList = cTree.getSnippetsList();
//			LOG.debug("SNIPz "+snippetsList);
//		}
//	}


}
