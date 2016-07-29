package org.xmlcml.cmine.metadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.cmine.CMineFixtures;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.cmine.files.CTree;
import org.xmlcml.cmine.files.CTreeList;
import org.xmlcml.cmine.metadata.crossref.CrossrefMD;

import com.google.common.collect.Multimap;

public class MetadataLongTest {

	private static final Logger LOG = Logger.getLogger(MetadataLongTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
//	private static final String GETPAPERS_NEW = "../getpapersNew";

	@Test
	public void testGetShuffledDOIURLs() throws IOException {
		if (!CMineFixtures.exist(CMineFixtures.GETPAPERS_NEW)) return;
		int numfiles = 1;
		for (int i = 1; i <= numfiles; i++) {
			CProject cProject = new CProject(new File(CMineFixtures.GETPAPERS_NEW, "2016020"+i+"-articles"));
			LOG.debug("file "+i);
			cProject.extractShuffledUrlsFromCrossrefToFile(new File(cProject.getDirectory(), MetadataManager.SHUFFLED_URLS_TXT));
			MetadataManager metadataManager = new MetadataManager();
			metadataManager.readMetadataTable(new File(cProject.getDirectory(), "crossref_common.csv"), MetadataManager.CR);
			
		}
	}

	@Test
		public void testGetURLsByPublisher() throws IOException {
			CProject cProject = new CProject(CMineFixtures.GETPAPERS_20160602);
			Multimap<String, String> map = cProject.extractMetadataItemMap(
					AbstractMetadata.Type.CROSSREF, CrossrefMD.PUBLISHER_PATH, CrossrefMD.URL_PATH);
			List<String> urlList = new ArrayList<String>();
			for (String key : map.keySet()) {
				List<String> urls = new ArrayList<String>(map.get(key));
				urlList.add(urls.get(0)); // get single URL for testing
			}
			Collections.shuffle(urlList);
			FileUtils.writeLines(new File(CMineFixtures.GETPAPERS_TARGET, "20160602/uniqueUrls.txt"), urlList, "\n");
	//		FileUtils.writeLines(new File("../getpapers/20160602/uniqueUrls2.txt"), urlList, "\n");
		}

	@Test
	//	@Ignore // LONG
		public void testLargeCProjectJSON() {
			File cProjectDir = new File(CMineFixtures.GETPAPERS, "20160601");
			CProject cProject = new CProject(cProjectDir);
			CTreeList cTreeList = cProject.getCTreeList();
			for (CTree cTree : cTreeList) {
				AbstractMetadata metadata = AbstractMetadata.getMetadata(cTree, AbstractMetadata.Type.CROSSREF);
				String s = metadata == null ? "?" : metadata.getJsonStringByPath(CrossrefMD.URL_PATH);
			}
			
		}
	
}
