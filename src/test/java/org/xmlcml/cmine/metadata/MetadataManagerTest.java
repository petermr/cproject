package org.xmlcml.cmine.metadata;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.cmine.CMineFixtures;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.cmine.files.CTree;
import org.xmlcml.cmine.util.RectangularTable;

public class MetadataManagerTest {

	private static final Logger LOG = Logger.getLogger(MetadataManagerTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
//	private static final String GETPAPERS_NEW = "../getpapersNew";

	/** EXTRACTS SINGLE COLUMN FROM TABLE AND WRITES TO NEW CSV.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetDOIColumnAsCSV() throws IOException {
		if (!CMineFixtures.exist(CMineFixtures.GETPAPERS_NEW)) return;
		int i = 1;
		CProject cProject = new CProject(new File(CMineFixtures.GETPAPERS_NEW, "2016020"+i+"-articles"));
		File inputCsvFile = new File(cProject.getDirectory(), "crossref_common.csv");
		File outputCsvFile = new File(cProject.getDirectory(), "dois.txt");
		RectangularTable table = RectangularTable.readTable(inputCsvFile, true);
		table.writeColumn(outputCsvFile, MetadataManager.DOI);
	}

	/** NOT FINISHED? */
	@Test
	public void testGetFreshQuickscrapeDirectories() throws IOException {
		if (!CMineFixtures.exist(CMineFixtures.GETPAPERS_NEW)) return;
		int i = 1;
		CProject getpapersProject = new CProject(new File(CMineFixtures.GETPAPERS_NEW, "2016020"+i+"-articles"));
		List<String> flattenedCrossrefUrls = getpapersProject.extractShuffledFlattenedCrossrefUrls();
		File quickscrapeDir = new File(getpapersProject.getDirectory(), MetadataManager.QUICKSCRAPE_DIR);
		CProject quickscrapeProject = new CProject(quickscrapeDir);
		
		for (CTree cTree : quickscrapeProject.getResetCTreeList()) {
			String doiname = cTree.getDirectory().getName();
			if (flattenedCrossrefUrls.contains(doiname)) {
				// add me 
			} else {
				LOG.warn("cannot find: "+doiname);
			}
		}
		MetadataManager metadataManager = new MetadataManager();
	}
	
}
