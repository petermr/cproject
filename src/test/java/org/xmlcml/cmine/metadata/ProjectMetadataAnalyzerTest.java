package org.xmlcml.cmine.metadata;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cmine.CMineFixtures;
import org.xmlcml.cmine.files.CProject;

import com.google.common.collect.Multiset;

public class ProjectMetadataAnalyzerTest {

	private static final Logger LOG = Logger.getLogger(ProjectMetadataAnalyzerTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	// FIXME choose smaller metadata
	public void testCreateMetadataList() throws IOException {
		File cProjectDir = new File(CMineFixtures.GETPAPERS, "20160601");
		CProject cProject = new CProject(cProjectDir);
		ProjectAnalyzer projectAnalyzer = cProject.getOrCreateProjectAnalyzer();
		projectAnalyzer.setMetadataType(AbstractMetadata.Type.CROSSREF);
		List<AbstractMetadata> metadataList = projectAnalyzer.getOrCreateMetadataList();
		Assert.assertEquals("mj", 21, metadataList.size());
	}

	@Test
	public void testExtractKeys() throws IOException {
		File cProjectDir = new File(CMineFixtures.GETPAPERS, "20160601");
		CProject cProject = new CProject(cProjectDir);
		ProjectAnalyzer projectAnalyzer = cProject.getOrCreateProjectAnalyzer();
		projectAnalyzer.setMetadataType(AbstractMetadata.Type.CROSSREF);
		List<AbstractMetadata> metadataList = projectAnalyzer.getOrCreateMetadataList();
		Assert.assertNotNull("metadataList: ", metadataList);
		Assert.assertTrue("metadataList size: "+metadataList.size(), metadataList.size() == 21);
		Multiset<String> keys = projectAnalyzer.getOrCreateAllKeys();
		Assert.assertEquals("keys "+keys.size(), 478, keys.size());
		LOG.debug(keys);
	}

	@Test
	public void testExtractURLs() throws IOException {
		File cProjectDir = new File(CMineFixtures.GETPAPERS, "20160601");
		CProject cProject = new CProject(cProjectDir);
		ProjectAnalyzer projectAnalyzer = cProject.getOrCreateProjectAnalyzer();
		projectAnalyzer.setShuffleUrls(true);
		projectAnalyzer.setPseudoHost(true);
		List<String> urls = cProject.extractShuffledCrossrefUrls();
		Assert.assertTrue("urls "+urls.size(), urls.size() == 20);
	}

	@Test
	/**
	 * 
	 * @throws IOException
	 */
	public void testExtractURLsToFile() throws IOException {
		File cProjectDir = new File(CMineFixtures.GETPAPERS, "20160601");
		CProject cProject = new CProject(cProjectDir);
		ProjectAnalyzer projectAnalyzer = cProject.getOrCreateProjectAnalyzer();
		projectAnalyzer.setMetadataType(AbstractMetadata.Type.CROSSREF);
		projectAnalyzer.setShuffleUrls(true);
		projectAnalyzer.setPseudoHost(true);
		File urlFile = cProject.createAllowedFile(CProject.URL_LIST);
		FileUtils.deleteQuietly(urlFile);
		cProject.extractShuffledUrlsFromCrossrefToFile(urlFile);
		int size = FileUtils.readLines(urlFile).size();
		Assert.assertEquals(""+size, 20, size);
//		projectAnalyzer.
	}

	@Test
	/**
	 * 
	 * @throws IOException
	 */
	@Ignore
	public void testDownloadNewURLs() throws IOException {
		CProject cProject = new CProject(CMineFixtures.GETPAPERS_SMALL);
		ProjectAnalyzer projectAnalyzer = cProject.getOrCreateProjectAnalyzer();
		projectAnalyzer.setMetadataType(AbstractMetadata.Type.CROSSREF);
		projectAnalyzer.setShuffleUrls(true);
		projectAnalyzer.setPseudoHost(true);
		File urlFile = cProject.createAllowedFile(CProject.URL_LIST);
		FileUtils.deleteQuietly(urlFile);
		cProject.extractShuffledUrlsFromCrossrefToFile(urlFile);
	}


}
