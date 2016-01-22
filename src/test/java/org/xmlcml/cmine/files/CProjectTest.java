package org.xmlcml.cmine.files;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cmine.CMineFixtures;
import org.xmlcml.cmine.util.CMineTestFixtures;
import org.xmlcml.html.HtmlElement;

import nu.xom.Element;

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
		List<File> allChildDirectoryList = cProject.getAllChildDirectoryList();
		Assert.assertEquals("all child dir", 2, allChildDirectoryList.size());
		List<File> allChildFileList = cProject.getAllChildFileList();
		Assert.assertEquals("all child file", 1, allChildFileList.size());
		CTreeList cTreeList = cProject.getCTreeList();
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
		CTreeList cTreeList = cProject.getCTreeList();
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
		CTreeList cTreeList = cProject.getCTreeList();
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
		Assert.assertEquals("relpath", "ctree1/results/sequence/dnaprimer/results.xml", relativePath);
	}

	@Test
	public void testResultsXML() {
		CProject cProject = new CProject(new File(CMineFixtures.PROJECTS_DIR, "project3"));
		List<File> resultsXMLFileList = cProject.getResultsXMLFileList();
		Assert.assertEquals("all results.xml", 2, resultsXMLFileList.size());
		cProject = new CProject(new File(CMineFixtures.PROJECTS_DIR, "regex10"));
		List<File> resultsXMLFiles = cProject.getResultsXMLFileList();
		Assert.assertEquals("all results.xml", 10, resultsXMLFiles.size());
		Assert.assertEquals("all results.xml", 9, cProject.getResultsXMLFileList(CProject.OMIT_EMPTY).size());
//		List<String> relativePaths = cProject.getRelativeProjectPaths(resultsXMLFiles);
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
	
	
	@Test
	public void testGlobFileListHuge() {
		File patentFile = new File("../patents");
		if (!patentFile.exists()) return; // only for PMR
		CProject cProject = new CProject(new File(patentFile, "US08979"));
		List<CTreeFiles> fileListList = cProject.listCTreeFiles("**/*");
		Assert.assertEquals(995,  fileListList.size());
		Assert.assertEquals(13,  fileListList.get(0).size());
	}

	
	@Test
	@Ignore
	public void testGlobFileListHugeResults() {
		File patentFile = new File("../patents");
		if (!patentFile.exists()) return; // only for PMR
		CProject cProject = new CProject(new File(patentFile, "US08979"));
		List<CTreeFiles> fileListList = cProject.listCTreeFiles("**/results.xml");
		Assert.assertEquals(995,  fileListList.size());
		Assert.assertEquals(1,  fileListList.get(0).size());
		Assert.assertEquals("../patents/US08979/US08979000-20150317/results/word/frequencies/results.xml",  
				fileListList.get(0).get(0).toString());
	}

	/** files for project2 are:
	 * /Users/pm286/workspace/cmine/src/test/resources/org/xmlcml/files/projects
	 * 
$ tree project2
project2
├── PMC4417228
│   ├── fulltext.pdf
│   └── fulltext.xml
├── PMC4521097
│   ├── fulltext.pdf
│   └── fulltext.xml
├── PMC4632522
│   ├── fulltext.pdf
│   └── fulltext.xml
├── eupmc_results.json
└── log.xml

	 * 
	 */
	@Test
	public void testGlobFileList() {
		CProject cProject = new CProject(new File(CMineFixtures.PROJECTS_DIR, "project2"));
		List<CTreeFiles> fileListList = cProject.listCTreeFiles("**/*");
		// 3 CTrees of form PMCddddddd
		Assert.assertEquals("a", 3,  fileListList.size());
		// the first one has two child files (fulltext.pdf and fulltext.xml)
		Assert.assertEquals("b", 2,  fileListList.get(0).size());
		// fails on unsorted lists
		
//		Assert.assertEquals("c", "src/test/resources/org/xmlcml/files/projects/project2/PMC4417228/fulltext.pdf",  
//				fileListList.get(0).get(0).toString());
//		Assert.assertEquals("d", "src/test/resources/org/xmlcml/files/projects/project2/PMC4417228/fulltext.xml",  
//				fileListList.get(0).get(1).toString());
	}

	@Test
	public void testGlobFileListAndXPathSearch() throws IOException {
		File targetDir = new File("target/glob/project2/ctree1");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.PROJECTS_DIR, "project2/"), targetDir);
		CProject cProject = new CProject(targetDir);
		List<List<XMLSnippets>> snippetsListList = cProject.getXPathSnippetsListList("**/fulltext.xml", "//title[starts-with(.,'Data')]");
		Assert.assertEquals("a", 2, snippetsListList.size());
		List<XMLSnippets> snippetsList0 = snippetsListList.get(0);
		Assert.assertEquals("b", 1, snippetsList0.size());
		XMLSnippets elementList0 = snippetsList0.get(0);
//		Assert.assertEquals("c", 2, elementList0.size());
//		Assert.assertEquals("d", "Data collection", elementList0.getValue(0));
//		Assert.assertEquals("e", "Data analysis", elementList0.getValue(1));
//		List<XMLSnippets> snippetsList1 = snippetsListList.get(1);
//		Assert.assertEquals("f", 1, snippetsList1.size());
//		XMLSnippets snippets1 = snippetsList1.get(0);
//		Assert.assertEquals("g", 1, snippets1.size());
//		Assert.assertEquals("h", "Data accessibility", snippets1.get(0).getValue());
	}
	
	@Test
	public void testGlobFileListAndXPathSearchCommand() throws IOException {
		File targetDir = new File("target/glob/project2/ctree1");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.PROJECTS_DIR, "project2/"), targetDir);
		CProject cProject = new CProject(targetDir);
		List<List<XMLSnippets>> snippetsListList = cProject.getXPathSnippetsListList("**/fulltext.xml", "//title[starts-with(.,'Data')]");
		Assert.assertEquals("a", 2, snippetsListList.size());
		List<XMLSnippets> elementListList0 = snippetsListList.get(0);
		Assert.assertEquals("b", 1, elementListList0.size());
		XMLSnippets snippets0 = elementListList0.get(0);
//		Assert.assertEquals("c", 2, snippets0.size());
//		Assert.assertEquals("d", "Data collection", snippets0.get(0).getValue());
//		Assert.assertEquals("e", "Data analysis", snippets0.get(1).getValue());
//		List<XMLSnippets> snippetsList1 = snippetsListList.get(1);
//		Assert.assertEquals("f", 1, snippetsList1.size());
//		XMLSnippets snippets1 = snippetsList1.get(0);
//		Assert.assertEquals("g", 1, snippets1.size());
//		Assert.assertEquals("h", "Data accessibility", snippets1.get(0).getValue());
	}
	

}
