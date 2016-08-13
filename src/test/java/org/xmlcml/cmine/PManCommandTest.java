package org.xmlcml.cmine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.cmine.metadata.crossref.CrossrefMD;
import org.xmlcml.cmine.util.CMineTestFixtures;
import org.xmlcml.cmine.util.Utils;

import com.google.gson.JsonArray;

/** tests commands under the 'cmine' command
 * 
 * @author pm286
 *
 */
public class PManCommandTest {

	public static final Logger LOG = Logger.getLogger(PManCommandTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	/** EXTRACTS URLS FROM GETPAPERS AND SHUFFLES THEM.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCrossrefUnshuffledURLS() throws IOException {
		File targetDir = new File(CMineFixtures.GETPAPERS_TARGET, "unshuffled");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.GETPAPERS_SRC, "unshuffled"), targetDir);
		String cmd = "--project "+targetDir.toString()+" --outUrls urls.txt --metadataType crossref";
		new PMan().run(cmd);
		File urls = new File(targetDir, "urls.txt");
		Assert.assertTrue(urls.exists());
		List<String> lines = FileUtils.readLines(urls);
		Assert.assertEquals(30,  lines.size());
		Assert.assertEquals("["
				+ "http://dx.doi.org/10.1002/adaw.30456,"
				+ " http://dx.doi.org/10.1007/s00332-016-9284-y,"
				+ " http://dx.doi.org/10.1007/s00300-016-1897-y,"
				+ " http://dx.doi.org/10.1007/s00294-016-0568-4,"
				+ " http://dx.doi.org/10.1002/acs.2674,"
				+ " http://dx.doi.org/10.1016/j.ijpe.2016.01.022,"
				+ " http://dx.doi.org/10.1007/s00294-016-0564-8,"
				+ " http://dx.doi.org/10.1515/tjj-2015-0056,"
				+ " http://dx.doi.org/10.1002/acs.2662,"
				+ " http://dx.doi.org/10.1007/s00291-015-0429-4,"
				+ " http://dx.doi.org/10.1016/j.ijpe.2016.01.021,"
				+ " http://dx.doi.org/10.1007/s0028",
				lines.toString().substring(0, 500));
	}

	
	/** EXTRACTS URLS FROM GETPAPERS AND SHUFFLES THEM.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCrossrefShuffledURLS() throws IOException {
		File targetDir = new File(CMineFixtures.GETPAPERS_TARGET, "unshuffled");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.GETPAPERS_SRC, "unshuffled"), targetDir);
		String cmd = "--project "+targetDir.toString()+" --outUrls urls.txt shuffle --metadataType crossref";
		PMan pman = new PMan();
		pman.run(cmd);
		File urls = new File(targetDir, "urls.txt");
		Assert.assertTrue(urls.exists());
		List<String> lines = FileUtils.readLines(urls);
		Assert.assertEquals(30,  lines.size());
		Assert.assertEquals("["
				+ "http://dx.doi.org/10.1002/adaw.30456,"
				+ " http://dx.doi.org/10.1007/s00332-016-9284-y,"
				+ " http://dx.doi.org/10.1007/s00300-016-1897-y,"
				+ " http://dx.doi.org/10.1007/s00294-016-0568-4,"
				+ " http://dx.doi.org/10.1002/acs.2674,"
				+ " http://dx.doi.org/10.1016/j.ijpe.2016.01.022,"
				+ " http://dx.doi.org/10.1007/s00294-016-0564-8,"
				+ " http://dx.doi.org/10.1515/tjj-2015-0056,"
				+ " http://dx.doi.org/10.1002/acs.2662,"
				+ " http://dx.doi.org/10.1007/s00291-015-0429-4,"
				+ " http://dx.doi.org/10.1016/j.ijpe.2016.01.021,"
				+ " http://dx.doi.org/10.1007/s0028",
				lines.toString().substring(0, 500));
	}

	/** EXTRACTS CSV from GETPAPERS/CROSSREF
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCreateCrossrefCSV() throws IOException {
		File targetDir = new File(CMineFixtures.GETPAPERS_TARGET, "20160601/csv");
		CMineTestFixtures.cleanAndCopyDir(CMineFixtures.GETPAPERS_SRC_20160601, targetDir);
		String cmd = "--project "+targetDir.toString()+" --csv crossref.csv License Title DOI";
		new PMan().run(cmd);
		File csvFile = new File(targetDir, "crossref.csv");
		Assert.assertTrue(csvFile.exists());
		List<String> lines = FileUtils.readLines(csvFile);
		Assert.assertEquals(21,  lines.size());
		Assert.assertEquals("["
				+ "License,Title,DOI,"
				+ " 0: [],Sodium Reduction—Saving Lives by Putting Choice Into Consumers’ Hands,10.1001/jama.2016.7992,"
				+ " \"2: [http://doi.wiley.com/10.1002/tdm_license_1, http://onlinelibrary.wiley.com/termsAndConditions]\",\"The relationship between physical aggression, foreign policy and moral choices: Phenotypic and genetic findings\",10.1002/ab.21660,"
				+ " \"2: [http://doi.wiley.com/10.1002/tdm_license_1, http://onlinelibrary.wiley.com/termsAndConditions]\",Die Hard in Notting Hill: Gender Differences in Recalling Contents from Action and Romantic Movies,10.1002/acp.3238,"
				+ " \"2: [http://doi.wiley.com/10.1002/tdm_license_1, http://onlinelibrary.wiley.com/termsAndConditions]\",Improving Unfamiliar Face Matching by Masking the External Facial Features,10.1002/acp.3239,"
				+ " \"2: [http://doi.wiley.com/10.1002/tdm_license_1, http://onlinelibrary.wiley.com/termsAndConditions]\",Note Taking and Note Reviewing Enhance Jurors' Recall of Trial Information,10.1002/acp.3240,"
				+ " \"2: [http://doi.wiley.com/10.1002/tdm_lic",
				lines.toString().substring(0, Math.min(lines.toString().length(), 1000)));
	}

	/** EXTRACTS CSV from GETPAPERS/CROSSREF
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCreateCrossrefAllHeadersCSV() throws IOException {
		File targetDir = new File(CMineFixtures.GETPAPERS_TARGET, "20160601/csv");
		CMineTestFixtures.cleanAndCopyDir(CMineFixtures.GETPAPERS_SRC_20160601, targetDir);
		String cmd = "--project "+targetDir.toString()+" --metadataType crossref --csv crossref1.csv";
		new PMan().run(cmd);
		File csvFile = new File(targetDir, "crossref1.csv");
		Assert.assertTrue("csvFile exists", csvFile.exists());
		List<String> lines = FileUtils.readLines(csvFile);
		Assert.assertEquals(132,  lines.size());
		Assert.assertEquals("["
				+ "URL,Title,Date,PDFURL,PDFFile,HTMLURL,HTMLFile,XMLURL,XMLFile,DOI,Publisher,Volume,AuthorList,Type,Issue,FirstPage,Description,Abstract,Journal,License,Links,Copyright,ISSN,Keywords,QuickscrapeMD,CrossrefMD,PublisherMD,Prefix,"
				+ " http://dx.doi.org/10.1001/jama.2016.7992,Sodium Reduction—Saving Lives by Putting Choice Into Consumers’ Hands,2016-06-01T23:24:00Z,,N,,N,,N,10.1001/jama.2016.7992,American Medical Association (AMA),,\"[Frieden Thomas R. [{\"\"name\"\":\"\"Centers for Disease Control and Prevention, Atlanta, Georgia\"\"}], ]\",journal-article,,,,,JAMA,0: [],0: [],,0098-7484,\"[[\"\"Medicine(all)\"\"]]\",N,N,N,http://id.crossref.org/prefix/10.1001,"
				+ " http://dx.doi.org/10.1002/ab.21660,\"The relationship between physical aggression, foreign policy and moral choices: Phenotypic and genetic findings\",2016-06-01T04:27:39Z,,N,,N,,N,10.1002/ab.21660,Wiley-Blackwell,,\"[McDermott Rose [{\"\"name\"\":\"\"Brown University; Providence Rhode Island\"\"}], , Hatemi Peter K. [{\"\"name\"\":\"\"Pennsylvania State University; State College Pennsylvania\"\"}], ]\",journal-article,,,,,Aggr. Behav.,"
				+ "\"2: [http://doi.wiley.com/10.1002/tdm_license_1, http://onlinelibrary.wiley.com/termsAndConditions]\",1: [application/pdf http://api.wiley.com/onlinelibrary/tdm/v1/articles/10.1002%2Fab.21660],,0096-140X,\"[[\"\"Psychology(all)\"\"]]\",N,N,N,http://id.crossref.org/prefix/10.1002, http://dx.doi.org/10.1002/acp.3238,Die Hard in Notting Hill: Gender Differences in Recalling Contents from Action and Romantic Movies,2016-06-01T08:12:49Z,,N,,N,,N,10.1002/acp.3238,Wiley-Blackwell,,\"[Wühr Peter [{\"\"name\"\":\"\"Institut für Psychologie; Technische Universität Dortmund; Dortmund Germany\"\"}], , Schwarz Sascha [{\"\"name\"\":\"\"Institut für Psychologie; Bergische Universität Wuppertal; Wuppertal Germany\"\"}], ]\",journal-article,,,,,Applied Cognitive Psychology,"
				+ "\"2: [http://doi.wiley.com/10.1002/tdm_license_1, http://onlinelibrary.wiley.com/termsAndConditions]\",1: [application/pdf http://api.wiley.com/onlinelibrary/tdm/v1/articles/10.1002%2Facp.3238],,0888-4080,\"[[\"\"Experimental and Cognitive Psychology\"\"]]\",N,N,N,http://id.crossref.org/prefix/10.1002, http://dx.doi.org/10.1002/acp.3239,Improving Unfamiliar Face Matching by Masking the External Facial Features,2016-06-01T08:13:03Z,,N,,N,,N,10.1002/acp.3239,Wiley-Blackwell,,\"[Kemp Richard I. [{\"\"name\"\":\"\"School of Psychology; University of New South Wales; Sydney Australia\"\"}], , Caon Alita [{\"\"name\"\":\"\"School of Psychology; University of New South Wales; Sydney Australia\"\"}], , Howard Mark [{\"\"name\"\":\"\"School of Psychology; University of New South Wales; Sydney Australia\"\"}], , Brooks Kevin R. [{\"\"name\"\":\"\"Department of Psychology; Macquarie University; Sydney Australia\"\"},{\"\"name\"\":\"\"Perception in Action Research Centre (PARC), Faculty of Human Sciences; Macquarie University; Sydney Australia\"\"}], ]\",journal-article,,,,,Applied Cognitive Psychology,"
				+ "\"2: [http://doi.wiley.com/10.1002/tdm_license_1, http://onlinelibrary.wiley.com/termsAndConditions]\",1: [application/pdf http://api.",
				lines.toString().substring(0, Math.min(lines.toString().length(), 3000)));
	}


	/** CSV HELP
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCSVHelp() throws IOException {
		String cmd = "--csv"; // no csv filename so should give help
		new PMan().run(cmd);
	}

	/** MERGE PROJECTS (crossref)
	 * 
	 * @throws IOException
	 */
	@Test
	public void testMergeProjects() throws IOException {
		File source1Dir = new File(CMineFixtures.GETPAPERS_SRC, "20160601");
		File source2Dir = new File(CMineFixtures.GETPAPERS_SRC, "20160602");
		File target1Dir = new File(CMineFixtures.GETPAPERS_TARGET, "project1");
		File target2Dir = new File(CMineFixtures.GETPAPERS_TARGET, "project2");
		CMineTestFixtures.cleanAndCopyDir(source1Dir, target1Dir);
		CMineTestFixtures.cleanAndCopyDir(source2Dir, target2Dir);
		CProject project1 = new CProject(target1Dir);
		CProject project2 = new CProject(target2Dir);
		Assert.assertEquals("project1", 21, project1.getResetCTreeList().size());
		Assert.assertEquals("project2", 35, project2.getResetCTreeList().size());
		String cmd = "--project "+target1Dir.toString()+" --mergeProjects "+target2Dir.toString();
		new PMan().run(cmd);
		project1 = new CProject(target1Dir); // because we haven't cleared the counts in the project
		Assert.assertEquals("project1", 56, project1.getResetCTreeList().size());
		Assert.assertEquals("project2", 35, project2.getResetCTreeList().size());
		
	}
	
	/** EXTRACT URLS (temporary)
	 * 
	 * @throws IOException
	 */
	@Test
	@Ignore
	public void testGetOpenUrls() throws IOException {
		File targetDir = new File(CMineFixtures.GETPAPERS_TARGET, "unshuffled");
		CMineTestFixtures.cleanAndCopyDir(new File(CMineFixtures.GETPAPERS_SRC, "unshuffled"), targetDir);
		String cmd = "--project "+targetDir.toString()+" --outUrls urls.txt --metadataType crossref";
	}
	
	
	/** RENAME CTREE NAMES (normalize DOI names)
	 * 
	 * @throws IOException
	 */
	@Test
	public void testRenameDOIBasedNames() throws IOException {
		File source1Dir = new File(CMineFixtures.GETPAPERS_OPEN, "httpUrls");
		File target1Dir = new File(CMineFixtures.GETPAPERS_TARGET, "httpUrls");
		CMineTestFixtures.cleanAndCopyDir(source1Dir, target1Dir);
		CProject project1 = new CProject(target1Dir);
		Assert.assertEquals("project1", 11, project1.getResetCTreeList().size());
		Assert.assertEquals("pre normalize", "["
				+ "target/getpapers/httpUrls/http_dx.doi.org_10.1063_1.4941232,"
				+ " target/getpapers/httpUrls/http_dx.doi.org_10.1088_0022-3727_49_9_095001,"
				+ " target/getpapers/httpUrls/http_dx.doi.org_10.1088_0031-8949_t167_", 
				Utils.truncate(project1.getResetCTreeList().toString(), 0, 200));
		String cmd = "--project "+target1Dir.toString()+" --renameCTree noHttp";
		new PMan().run(cmd);
		project1 = new CProject(target1Dir); // because we haven't cleared the counts in the project
		Assert.assertEquals("project1", 11, project1.getResetCTreeList().size());
		Assert.assertEquals("post normalize", "["
				+ "target/getpapers/httpUrls/10.1063_1.4941232,"
				+ " target/getpapers/httpUrls/10.1088_0022-3727_49_9_095001,"
				+ " target/getpapers/httpUrls/10.1088_0031-8949_t167_1_014076,"
				+ " target/getpapers/httpUrls/10.1088_0953"
				, Utils.truncate(project1.getResetCTreeList().toString(), 0, 200));
	}

	/** RENAME CTREE NAMES and merge getpapers and quickscrape
	 * 
	 * typically getpapers and quickscrape may produce incompatible directories (we'll change this)
	 * 
	 * molecules
├── 10.3390_molecules21020174
│   └── crossref_result.json
├── 10.3390_molecules21020178
│   └── crossref_result.json
├── 10.3390_molecules21020180
│   └── crossref_result.json
...
├── 10.3390_molecules21020189
│   └── crossref_result.json
├── crossref_results.json
├── http_dx.doi.org_10.3390_molecules21020174
├── http_dx.doi.org_10.3390_molecules21020178
│   ├── fulltext.html
│   ├── fulltext.pdf
│   ├── fulltext.xml
│   └── results.json
├── http_dx.doi.org_10.3390_molecules21020180
│   ├── fulltext.html
│   ├── fulltext.pdf
│   ├── fulltext.xml
│   └── results.json
...
├── http_dx.doi.org_10.3390_molecules21020189
│   ├── fulltext.html
│   ├── fulltext.pdf
│   ├── fulltext.xml
│   └── results.json
└── urls.txt

'renameCTree noHttp' should normalize the dir names and copy files into a single place

may become unnecessary as getpapers and quickscrape are reconciled

	 * @throws IOException
	 */
	@Test
	public void testMergeGetpapersAndQuickscrapeNames() throws IOException {
		File source1Dir = new File(CMineFixtures.GETPAPERS_OPEN, "molecules");
		File target1Dir = new File(CMineFixtures.GETPAPERS_TARGET, "molecules");
		CMineTestFixtures.cleanAndCopyDir(source1Dir, target1Dir);
		CProject project1 = new CProject(target1Dir);
		Assert.assertEquals("project1", 22, project1.getResetCTreeList().size());
		String cmd = "--project "+target1Dir.toString()+" --renameCTree noHttp";
		new PMan().run(cmd);
		project1 = new CProject(target1Dir); // because we haven't cleared the counts in the project
		Assert.assertEquals("project1", 11, project1.getResetCTreeList().size());
		Assert.assertEquals("post normalize", "["
				+ "target/getpapers/molecules/10.3390_molecules21020174,"
				+ " target/getpapers/molecules/10.3390_molecules21020178,"
				+ " target/getpapers/molecules/10.3390_molecules21020180,"
				+ " target/getpapers/molecules/10.3390_mo"
				, Utils.truncate(project1.getResetCTreeList().toString(), 0, 200));
	}
	
	/** rename files in CTree
	 * 
	 * @throws IOException
	 */
	@Test
	public void testRenameFiles() throws IOException {
		
		File source1Dir = new File(CMineFixtures.GETPAPERS_OPEN, "httpUrls");
		File target1Dir = new File(CMineFixtures.GETPAPERS_TARGET, "httpUrls");
		CMineTestFixtures.cleanAndCopyDir(source1Dir, target1Dir);
		CProject project1 = new CProject(target1Dir);
		Assert.assertEquals("project1", 11, project1.getResetCTreeList().size());
		File oldFile = new File(project1.getDirectory(), "http_dx.doi.org_10.1063_1.4941232/results.json");
		Assert.assertTrue("oldFile exists", oldFile.exists());
		File newFile = new File(project1.getDirectory(), "http_dx.doi.org_10.1063_1.4941232/quickscrape_result.json");
		Assert.assertFalse("newFile not exists", newFile.exists());

		String cmd = "--project "+target1Dir.toString()+" --renameFile results.json quickscrape_result.json";
		new PMan().run(cmd);
		project1 = new CProject(target1Dir); // because we haven't cleared the counts in the project
		Assert.assertEquals("project1", 11, project1.getResetCTreeList().size());
		oldFile = new File(project1.getDirectory(), "http_dx.doi.org_10.1063_1.4941232/results.json");
		Assert.assertFalse("oldFile not exists", oldFile.exists());
		newFile = new File(project1.getDirectory(), "http_dx.doi.org_10.1063_1.4941232/quickscrape_result.json");
		Assert.assertTrue("newFile exists", newFile.exists());
	}
	
	/** rename files in CTree
	 * 
	 * @throws IOException
	 */
	@Test
	public void testDeleteFiles() throws IOException {
		
		File source1Dir = new File(CMineFixtures.GETPAPERS_OPEN, "httpUrls");
		File target1Dir = new File(CMineFixtures.GETPAPERS_TARGET, "httpUrls");
		CMineTestFixtures.cleanAndCopyDir(source1Dir, target1Dir);
		CProject project1 = new CProject(target1Dir);
		Assert.assertEquals("project1", 11, project1.getResetCTreeList().size());
		File file1 = new File(project1.getDirectory(), "http_dx.doi.org_10.1103_physrevb.93.075101/results.json");
		Assert.assertTrue("file1 exists", file1.exists());
		File file2 = new File(project1.getDirectory(), "http_dx.doi.org_10.1103_physrevb.93.075101/fulltext.html");
		Assert.assertTrue("file2 exists", file2.exists());

		String cmd = "--project "+target1Dir.toString()+" --deleteFile results.json fulltext.html";
		new PMan().run(cmd);
		project1 = new CProject(target1Dir); // because we haven't cleared the counts in the project
		Assert.assertEquals("project1", 11, project1.getResetCTreeList().size());
		file1 = new File(project1.getDirectory(), "http_dx.doi.org_10.1103_physrevb.93.075101/results.json");
		Assert.assertFalse("file1 not exists", file1.exists());
		file2 = new File(project1.getDirectory(), "http_dx.doi.org_10.1103_physrevb.93.075101/fulltext.html");
		Assert.assertFalse("file2 not exists", file2.exists());
	}
	

	/** INPUT AND OUTPUT URLS
	 * 
	 * @throws IOException
	 */
	@Test
	public void testInputOutputUrls() throws IOException {
		File source1Dir = new File(CMineFixtures.GETPAPERS_OPEN, "lic20160201truncated");
		File inUrls = new File(CMineFixtures.GETPAPERS_OPEN, "lic20160201/urls.txt");
		Assert.assertEquals("urls", 123, FileUtils.readLines(inUrls).size());
		File target1Dir = new File(CMineFixtures.GETPAPERS_TARGET, "lic20160201truncated");
		CMineTestFixtures.cleanAndCopyDir(source1Dir, target1Dir);
		File outUrls = new File(target1Dir, "outUrls.txt");
		Assert.assertFalse(outUrls.getAbsolutePath()+" exists", outUrls.exists());
		CProject project1 = new CProject(target1Dir);
		Assert.assertEquals("project1", 69, project1.getResetCTreeList().size());
		String cmd = "--project "+target1Dir.toString()+" --inUrls "+" urls.txt" +" markEmpty --outUrls outUrls.txt";
		new PMan().run(cmd);
		project1 = new CProject(target1Dir); // because we haven't cleared the counts in the project
//		Assert.assertEquals("project1", 70, project1.getCTreeList().size());
		Assert.assertEquals("urls", "["
				+ "target/getpapers/lic20160201truncated/http_dx.doi.org_10.1088_1757-899x_106_1_012014,"
				+ " target/getpapers/lic20160201truncated/http_dx.doi.org_10.1088_1757-899x_106_1_012015,"
				+ " target/getpapers/lic2016020"
				, Utils.truncate(project1.getResetCTreeList().toString(), 0, 200));

		Assert.assertTrue(outUrls.getAbsolutePath()+" exists", outUrls.exists());
		Assert.assertEquals("urls", 54, FileUtils.readLines(outUrls).size());

	}
	

	/** CHECK DOWNLOADED FILES
	 * 
	 * sometimes the files downloaded by quickscrape are different MIM types from exoected.
	 * Most commonly "fulltext.pdf" is actually an HTML (?frame) indicating that downlaod failed.
	 * 
	 * This utility will detect incorrect PDFs and rename `fulltext.pdf` to `fulltext.pdf.html`
	 * make take several hundred ms to read bytes from a large PDF file
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCheckFileContents() throws IOException {
		File source1Dir = new File(CMineFixtures.GETPAPERS_OPEN, "lic20160201truncated");
		File target1Dir = new File(CMineFixtures.GETPAPERS_TARGET, "lic20160201truncated");
		CMineTestFixtures.cleanAndCopyDir(source1Dir, target1Dir);
		List<File> pdfFiles = new ArrayList<File>(FileUtils.listFiles(target1Dir, new String[]{"pdf"}, true));
		Assert.assertEquals(26,  pdfFiles.size());
		CProject project1 = new CProject(target1Dir);
		Assert.assertEquals("project1", 69, project1.getResetCTreeList().size());
		File pdf = new File(target1Dir, "http_dx.doi.org_10.1103_physrevb.93.075101/fulltext.pdf");
		Assert.assertTrue("pdf exists", pdf.exists());
		File pdfHtml = new File(target1Dir, "http_dx.doi.org_10.1103_physrevb.93.075101/fulltext.pdf.html");
		Assert.assertFalse("pdfHtml not exists", pdfHtml.exists());

		String cmd = "--project "+target1Dir.toString()+" --renamePDF";
		new PMan().run(cmd);
		
		pdf = new File(target1Dir, "http_dx.doi.org_10.1103_physrevb.93.075101/fulltext.pdf");
		Assert.assertFalse("pdf not exists", pdf.exists());
		pdfHtml = new File(target1Dir, "http_dx.doi.org_10.1103_physrevb.93.075101/fulltext.pdf.html");
		Assert.assertTrue("pdfHtml exists", pdfHtml.exists());
		
		pdfFiles = new ArrayList<File>(FileUtils.listFiles(target1Dir, new String[]{"pdf"}, true));
		Assert.assertEquals(25,  pdfFiles.size());

	}
	

	/** MERGE PROJECTS
	 * 
	 * @throws IOException
	 */
	@Test
	public void testMergeCprojects() throws IOException {
		File source1Dir = new File(CMineFixtures.GETPAPERS_OPEN, "licmini");
		File source2Dir = new File(CMineFixtures.GETPAPERS_OPEN, "pubmini");
		File target1Dir = new File(CMineFixtures.GETPAPERS_TARGET, "licmini");
		File target2Dir = new File(CMineFixtures.GETPAPERS_TARGET, "pubmini");
		CMineTestFixtures.cleanAndCopyDir(source1Dir, target1Dir);
		CMineTestFixtures.cleanAndCopyDir(source2Dir, target2Dir);
		CProject project1 = new CProject(target1Dir);
		CProject project2 = new CProject(target2Dir);
		Assert.assertEquals("project1", 57, project1.getResetCTreeList().size());
		Assert.assertEquals("project2",278, project2.getResetCTreeList().size());
		File duplicates = new File(CMineFixtures.GETPAPERS_TARGET, "duplicates");

		String cmd = "--project "+target1Dir.toString()+" --mergeProjects "+target2Dir.toString()+" --duplicates "+duplicates.toString();
		new PMan().run(cmd);

		CProject project1a = new CProject(target1Dir);
		Assert.assertEquals("project1", 310, project1a.getResetCTreeList().size());
		JsonArray array = new CrossrefMD().readMetadataArrayFromConcatenatedFile(new File(target1Dir, "crossref_results.json"));
		Assert.assertEquals(310, array.size());
		Assert.assertEquals("project2", 278, project2.getResetCTreeList().size());
		Assert.assertTrue("duplicates exists", duplicates.exists());
		Assert.assertTrue("duplicates is directory ", duplicates.isDirectory());
		CProject duplicatesProject = new CProject(duplicates);
		Assert.assertEquals("duplicates trees", 25, duplicatesProject.getResetCTreeList().size());

	}
	
	
	
	

}
