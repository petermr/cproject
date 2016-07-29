package org.xmlcml.cmine;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class CMineFixtures {
	
	public static final Logger LOG = Logger.getLogger(CMineFixtures.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static File TEST_CMINE_DIR = new File("src/test/resources/org/xmlcml");
	public final static File TEST_FILES_DIR = new File("src/test/resources/org/xmlcml/files");
	
	public final static File TEST_DOWNLOAD_DIR = new File(TEST_CMINE_DIR, "download");
	
	public final static File TEST_PROJECTS_DIR = new File(TEST_FILES_DIR, "projects");
	public final static File TEST_RESULTS_DIR = new File(TEST_FILES_DIR, "results");
	public final static File TEST_MISC_DIR = new File(TEST_FILES_DIR, "misc");
	public static final File TEST_CROSSREF_DIR = new File(CMineFixtures.TEST_DOWNLOAD_DIR, "crossref");
	public static final File TEST_CROSSREF_SAMPLE = new File(CMineFixtures.TEST_CROSSREF_DIR, "sample");
	public static final File TEST_SAMPLE = new File(CMineFixtures.TEST_DOWNLOAD_DIR, "sample");

	public static final File GETPAPERS = new File(TEST_CMINE_DIR, "getpapers");
	public static final File GETPAPERS_TARGET = new File("target/getpapers");
	public static final File QUICKSCRAPE20160601_CSV = new File(GETPAPERS, "20160601quickscrape.csv");
	
	public static final File CROSSREF20160601_CSV = new File(GETPAPERS, "20160601crossref_1.csv");
	public static final File CROSSREF20160601_MERGED_CSV = new File(GETPAPERS, "20160601merged.csv");

//	public static File GETPAPERS_20160601SCRAPED = new File(GETPAPERS_20160601, "quickscrape");
//	public static File GETPAPERS_QUICKSCRAPE_CSV = new File(GETPAPERS, "20160601quickscrape.csv");
	public static File GETPAPERS_20160601 = new File(GETPAPERS, "20160601");
	public static File GETPAPERS_20160602 = new File(GETPAPERS, "20160602");
	public static File GETPAPERS_20160601SCRAPED = new File(GETPAPERS_20160601, "quickscrape");
	public static File GETPAPERS_20160602SCRAPED = new File(GETPAPERS, "20160602scraped");

	public static final File CROSSREF_A_1_CSV =  new File(GETPAPERS, "crossref_a_1.csv");
	// PMR only; test with existence
	public static final File GETPAPERS_NEW = new File("../getpapersNew");
//	public static final File GETPAPERS_20160601 = new File(GETPAPERS, "20160601");
	public static final File SCRAPER_DIR = new File("../../journal-scrapers/scrapers");
	public static final File GETPAPERS_SMALL = new File(GETPAPERS_NEW, "201601small");
	
	
	public static boolean exist(File file) {
		boolean exist = true;
		if (file != null && !file.exists()) {
			LOG.info("skipped local test: "+file);
			exist = false;
		}
		return exist;
	}

}
