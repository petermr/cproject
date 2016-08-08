package org.xmlcml.cmine;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.cmine.util.CMineTestFixtures;
import org.xmlcml.cmine.util.Utils;

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
		String cmd = "--project "+targetDir.toString()+" --extractUrls urls.txt --metadataType crossref";
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
		String cmd = "--project "+targetDir.toString()+" --extractUrls urls.txt shuffle --metadataType crossref";
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
		String cmd = "--project "+targetDir.toString()+" --csv crossref.csv License Title DOI --metadataType crossref";
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
		Assert.assertEquals("project1", 21, project1.getCTreeList().size());
		Assert.assertEquals("project2", 35, project2.getCTreeList().size());
		String cmd = "--project "+target1Dir.toString()+" --mergeProjects "+target2Dir.toString();
		new PMan().run(cmd);
		project1 = new CProject(target1Dir); // because we haven't cleared the counts in the project
		Assert.assertEquals("project1", 56, project1.getCTreeList().size());
		Assert.assertEquals("project2", 35, project2.getCTreeList().size());
		
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
		String cmd = "--project "+targetDir.toString()+" --extractUrls urls.txt --metadataType crossref";
	}
	
	
	/** NORMALIZE CTREE NAMES
	 * 
	 * @throws IOException
	 */
	@Test
	public void testRenameDOIBasedNames() throws IOException {
		File source1Dir = new File(CMineFixtures.GETPAPERS_OPEN, "httpUrls");
		File target1Dir = new File(CMineFixtures.GETPAPERS_TARGET, "httpUrls");
		CMineTestFixtures.cleanAndCopyDir(source1Dir, target1Dir);
		CProject project1 = new CProject(target1Dir);
		Assert.assertEquals("project1", 11, project1.getCTreeList().size());
		Assert.assertEquals("pre normalize", "["
				+ "target/getpapers/httpUrls/http_dx.doi.org_10.1063_1.4941232,"
				+ " target/getpapers/httpUrls/http_dx.doi.org_10.1088_0022-3727_49_9_095001,"
				+ " target/getpapers/httpUrls/http_dx.doi.org_10.1088_0031-8949_t167_", 
				Utils.truncate(project1.getCTreeList().toString(), 0, 200));
		String cmd = "--project "+target1Dir.toString()+" --renameCTree noHttp";
		new PMan().run(cmd);
		project1 = new CProject(target1Dir); // because we haven't cleared the counts in the project
		Assert.assertEquals("project1", 11, project1.getCTreeList().size());
		Assert.assertEquals("post normalize", "["
				+ "target/getpapers/httpUrls/10.1063_1.4941232,"
				+ " target/getpapers/httpUrls/10.1088_0022-3727_49_9_095001,"
				+ " target/getpapers/httpUrls/10.1088_0031-8949_t167_1_014076,"
				+ " target/getpapers/httpUrls/10.1088_0953"
				, Utils.truncate(project1.getCTreeList().toString(), 0, 200));
	}
	
	
	
	

}
