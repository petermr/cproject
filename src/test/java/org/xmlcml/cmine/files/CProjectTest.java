package org.xmlcml.cmine.files;

import java.io.File;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cmine.CMineFixtures;
import org.xmlcml.html.HtmlElement;

public class CProjectTest {

	private static final Logger LOG = Logger.getLogger(CProjectTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testCProject() {
		File project1Dir = new File(CMineFixtures.PROJECTS_DIR, "project1");
		Assert.assertTrue(project1Dir.exists());
		CContainer cProject = new CProject(project1Dir);
		File projectDir = cProject.getDirectory();
		Assert.assertTrue(projectDir.exists());
	}
	
	@Test
	public void testCProjectManifest() {
		CContainer cProject = new CProject(new File(CMineFixtures.PROJECTS_DIR, "project1"));
		CProjectManifest manifest = (CProjectManifest) cProject.getOrCreateManifest();
		Assert.assertNotNull(manifest);
		File manifestFile = manifest.getOrCreateManifestFile();
		Element manifestElement = manifest.getOrCreateManifestElement();
		Assert.assertNotNull("need manifest file in "+cProject.getDirectory(), manifestFile);
	}

	@Test
	public void testManifestDocs() {		
		CContainer cProject = new CProject(new File(CMineFixtures.PROJECTS_DIR, "project1"));
		CProjectManifest manifest = (CProjectManifest) cProject.getOrCreateManifest();
		HtmlElement docElement = manifest.getDocHtml();
		LOG.debug(docElement);
	}

	@Test
	public void testUpdateManifest() {
		CContainer cProject = new CProject(new File(CMineFixtures.PROJECTS_DIR, "project1"));
		cProject.updateManifest();
	}
	
	@Test
	public void testGetCTreeList() {
		CProject cProject = new CProject(new File(CMineFixtures.PROJECTS_DIR, "project1"));
//		cProject.getOrCreateAllowedAndCTreeLists();
		List<File> allChildDirectoryList = cProject.getAllChildDirectoryList();
		Assert.assertEquals("all child dir", 2, allChildDirectoryList.size());
		List<File> allChildFileList = cProject.getAllChildFileList();
		Assert.assertEquals("all child file", 1, allChildFileList.size());
		List<CTree> cTreeList = cProject.getCTreeList();
		Assert.assertEquals("trees", 2, cTreeList.size());
	}
	
	@Test
	/** this is a small set doenloaded from EPMC
	 * 
	 */
	public void testCTreeContent2() {
		CProject cProject = new CProject(new File(CMineFixtures.PROJECTS_DIR, "project2"));
		List<File> allChildDirectoryList = cProject.getAllChildDirectoryList();
		Assert.assertEquals("all child dir", 3, allChildDirectoryList.size());
		List<File> allowedChildDirectoryList = cProject.getAllowedChildDirectoryList();
		// this is zero because the CTrees are not counted as allowedDirectories
		Assert.assertEquals("allowed child dir", 0, allowedChildDirectoryList.size());
		List<File> unknownChildDirectoryList = cProject.getUnknownChildDirectoryList();
		Assert.assertEquals("unknown child dir", 0, unknownChildDirectoryList.size());
		List<CTree> cTreeList = cProject.getCTreeList();
		Assert.assertEquals("all child dir", 3, cTreeList.size());
		
		List<File> allChildFileList = cProject.getAllChildFileList();
		Assert.assertEquals("all child file", 2, allChildFileList.size());
		List<File> allowedChildFileList = cProject.getAllowedChildFileList();
		Assert.assertEquals("allowed child file", 2, allowedChildFileList.size());
		List<File> unknownChildFileList = cProject.getUnknownChildFileList();
		Assert.assertEquals("unknown child file", 0, unknownChildFileList.size());
		
		CTree cTree1 = cTreeList.get(0);
		cTree1.getOrCreateFilesDirectoryCTreeLists();

	}

	@Test
	/** this is an irregular structure to test the system.
	 * 
	 */
	public void testCTreeContent3() {
		CProject cProject = new CProject(new File(CMineFixtures.PROJECTS_DIR, "project3"));
		List<File> allChildDirectoryList = cProject.getAllChildDirectoryList();
		Assert.assertEquals("all child dir", 3, allChildDirectoryList.size());
		List<File> allowedChildDirectoryList = cProject.getAllowedChildDirectoryList();
		// this is zero because the CTrees are not counted as allowedDirectories
		Assert.assertEquals("allowed child dir", 0, allowedChildDirectoryList.size());
		List<File> unknownChildDirectoryList = cProject.getUnknownChildDirectoryList();
		Assert.assertEquals("unknown child dir", 1, unknownChildDirectoryList.size());
		List<CTree> cTreeList = cProject.getCTreeList();
		Assert.assertEquals("all child dir", 2, cTreeList.size());
		
		List<File> allChildFileList = cProject.getAllChildFileList();
		Assert.assertEquals("all child file", 3, allChildFileList.size());
		List<File> allowedChildFileList = cProject.getAllowedChildFileList();
		Assert.assertEquals("allowed child file", 2, allowedChildFileList.size());
		List<File> unknownChildFileList = cProject.getUnknownChildFileList();
		Assert.assertEquals("unknown child file", 1, unknownChildFileList.size());
		
		// cTree1 is a normal CTree
		CTree cTree1 = cTreeList.get(0);
		allowedChildDirectoryList = cTree1.getAllowedChildDirectoryList();
		Assert.assertEquals("allowed child dir", 1, allowedChildDirectoryList.size());
		unknownChildDirectoryList = cTree1.getUnknownChildDirectoryList();
		Assert.assertEquals("unknown child dir", 0, unknownChildDirectoryList.size());
		
		allChildFileList = cTree1.getAllChildFileList();
		Assert.assertEquals("all child file", 2, allChildFileList.size());
		allowedChildFileList = cTree1.getAllowedChildFileList();
		Assert.assertEquals("allowed child file", 2, allowedChildFileList.size());
		unknownChildFileList = cTree1.getUnknownChildFileList();
		Assert.assertEquals("unknown child file", 0, unknownChildFileList.size());

		// cTree2 is a normal CTree, without results/
		CTree cTree2 = cTreeList.get(0);
		allowedChildDirectoryList = cTree2.getAllowedChildDirectoryList();
		Assert.assertEquals("allowed child dir", 1, allowedChildDirectoryList.size());
		unknownChildDirectoryList = cTree2.getUnknownChildDirectoryList();
		Assert.assertEquals("unknown child dir", 0, unknownChildDirectoryList.size());
		
		allChildFileList = cTree2.getAllChildFileList();
		Assert.assertEquals("all child file", 2, allChildFileList.size());
		allowedChildFileList = cTree2.getAllowedChildFileList();
		Assert.assertEquals("allowed child file", 2, allowedChildFileList.size());
		unknownChildFileList = cTree2.getUnknownChildFileList();
		Assert.assertEquals("unknown child file", 0, unknownChildFileList.size());

		// there are only 2 CTrees as the nonctree/ has no reserved filenames
		
	}
	
	@Test
	public void testRelativeProjectPath() {
		CProject cProject = new CProject(new File(CMineFixtures.PROJECTS_DIR, "project3"));
		String relativePath = cProject.getRelativeProjectPath(cProject.getResultsXMLFileList().get(0));
		Assert.assertEquals("relpath", "ctree1/results/word/frequencies/results.xml", relativePath);
	}

	@Test
	public void testResultsXML() {
		CProject cProject = new CProject(new File(CMineFixtures.PROJECTS_DIR, "project3"));
		List<File> resultsXMLFileList = cProject.getResultsXMLFileList();
		Assert.assertEquals("all results.xml", 1, resultsXMLFileList.size());
		cProject = new CProject(new File(CMineFixtures.PROJECTS_DIR, "regex10"));
		List<File> resultsXMLFiles = cProject.getResultsXMLFileList();
		Assert.assertEquals("all results.xml", 10, resultsXMLFiles.size());
		Assert.assertEquals("all results.xml", 9, cProject.getResultsXMLFileList(CProject.OMIT_EMPTY).size());
		List<String> relativePaths = cProject.getRelativeProjectPaths(resultsXMLFiles);
		/** possible unpredictable order
		Assert.assertEquals("relative paths", 
				"["
				+ "e0115544/results/regex/consort0/results.xml,"
				+ " e0116215/results/regex/consort0/results.xml,"
				+ " e0116596/results/regex/consort0/results.xml,"
				+ " e0116903/results/regex/consort0/results.xml,"
				+ " e0117956/results/regex/consort0/results.xml,"
				+ " e0118659/results/regex/consort0/results.xml,"
				+ " e0118685/results/regex/consort0/results.xml,"
				+ " e0118692/results/regex/consort0/results.xml,"
				+ " e0118792/results/regex/consort0/results.xml,"
				+ " e0119090/results/regex/consort0/results.xml"
				+ "]",
				relativePaths.toString());
				*/
	}
	
	@Test
	public void testUnzip() {
		
	}

}
