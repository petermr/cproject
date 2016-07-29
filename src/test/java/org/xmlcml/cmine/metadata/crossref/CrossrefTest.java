package org.xmlcml.cmine.metadata.crossref;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.cmine.CMineFixtures;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.cmine.files.CTreeList;
import org.xmlcml.cmine.metadata.AbstractCM;
import org.xmlcml.cmine.metadata.AbstractMetadata;
import org.xmlcml.cmine.metadata.CMDOI;
import org.xmlcml.cmine.metadata.CMURL;
import org.xmlcml.cmine.metadata.DOIResolver;
import org.xmlcml.cmine.util.CMineUtil;
import org.xmlcml.cmine.util.RectangularTable;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CrossrefTest {

	
	private final static String SS = ">>>>>>>>>>>>>>>>>>>>>>";
	
	private static final File TEST_CROSSREF_SAMPLE = new File(CMineFixtures.TEST_CROSSREF_DIR, "sample");
//	static final File GETPAPERS = new File("../getpapers");
//	static final File GETPAPERS_NEW = new File("../getpapersNew");
//	private static final File GETPAPERS_2016060 = new File(GETPAPERS+"/2016060");
//	public static final File GETPAPERS_20160601 = new File(GETPAPERS+"/20160601");
;
//	private static final String XREF_DIR = "/Users/pm286/workspace/cmdev/norma-dev/xref";
	private static final Logger LOG = Logger.getLogger(CrossrefTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}



	@Test
	@Ignore // downloads many
	public void testCreateDownloadAgro() throws IOException {
		String fromDate = "2010-05-02";
		String untilDate = "2016-05-03";
		String query = "Puccinia";
		CrossrefDownloader.runCrossRefQuery(fromDate, untilDate, query, true, 500, new File("target/xref/puccinia"));
	}
	

	@Test
	@Ignore // downloads many
	public void testCreateDownloadTheoChem() throws IOException {
		String fromDate = "2016-04-03";
		String untilDate = "2016-05-03";
		String query = "theoretical+chemistry";
		CrossrefDownloader.runCrossRefQuery(fromDate, untilDate, query, true, 50, new File("target/xref/theochem"));
	}

	@Test
	@Ignore // downloads many
	public void testCreateDownloadGlutamate() throws IOException {
		String fromDate = "2016-04-03";
		String untilDate = "2016-05-03";
		String query = "glutamate";
		CrossrefDownloader.runCrossRefQuery(fromDate, untilDate, query, true, 50, new File("target/xref/glutamate"));
	}

	@Test
	public void testQuickscrape() throws IOException {
		File scrapers = new File("workspace/journalScrapers/scrapers");
		File outdir = new File("target/crossref");
		String CROSSREF = "/Users/pm286/.nvm/v0.10.38/bin/quickscrape";
	    Process process = CMineUtil.runProcess(
	    		new String[]{CROSSREF, "-q", "http://dx.plos.org/10.1371/journal.pone.0075293", "-d", scrapers.toString(), "-o", outdir.toString()}, null);
	}

	
	@Test
	public void testResolveDOI() throws Exception {
		String crossRefDOI = "http://dx.doi.org/10.3389/fpsyg.2016.00565";
		String s = new DOIResolver().resolveDOI(crossRefDOI);
//		LOG.debug("DOI "+s);
	}

	@Test
	@Ignore // downloads
	public void testCreateDaily() throws IOException {
		String fromDate = "2016-05-02";
		String untilDate = "2016-05-03";
		String query = null;
		boolean resolveDois = true;
		boolean skipHyphenDois = true;
		int rows = 10;
		int offset = 0;
		for (int j = 0; j < 5; j++) {
			CrossrefDownloader.runCrossRefDate(fromDate, untilDate, resolveDois, rows, offset, new File("xref/daily/20100601/"));
			offset += rows;
		}
	}

	@Test
	public void testGetMetadataObjectList() throws Exception {
		
		AbstractMetadata crossrefMetadata = new CrossrefMD();
		crossrefMetadata.readMetadataArrayFromConcatenatedFile(new File(CMineFixtures.TEST_CROSSREF_SAMPLE, "crossref_results.json"));
		List<JsonObject> metadataObjectList = crossrefMetadata.getMetadataObjectListFromConcatenatedArray();
		Assert.assertEquals("items", 1552, metadataObjectList.size());
	}

	@Test
	public void testGetMetadataKeys() throws Exception {
		
		AbstractMetadata crossrefMetadata = new CrossrefMD();
		crossrefMetadata.readMetadataArrayFromConcatenatedFile(new File(CMineFixtures.TEST_CROSSREF_SAMPLE, "crossref_results.json"));
		List<JsonObject> metadataObjectList = crossrefMetadata.getMetadataObjectListFromConcatenatedArray();
		JsonObject object0 = metadataObjectList.get(0);
		Assert.assertEquals("fields", 24, object0.entrySet().size());
	}

	@Test
	public void testGetObjectMetadata1() throws Exception {
		AbstractMetadata crossrefMetadata = new CrossrefMD();
		crossrefMetadata.readMetadataArrayFromConcatenatedFile(new File(CMineFixtures.TEST_CROSSREF_SAMPLE, "crossref_results.json"));
		crossrefMetadata.getOrCreateMetadataList();
		LOG.debug("keys "+CMineUtil.getEntriesSortedByCount(crossrefMetadata.getKeysmap()));
		LOG.debug("KEYS "+crossrefMetadata.getKeysmap().size());
		LOG.debug("KEYS1 "+crossrefMetadata.getKeysmap().entrySet().size());
		crossrefMetadata.debugStringValues();
		crossrefMetadata.debugNumberValues();
		LOG.debug("all keys "+CMineUtil.getEntriesSortedByCount(crossrefMetadata.getAllKeys()));
//		LOG.debug("author "+crossrefMetadata.getAuthorEntriesSortedByCount());
//		LOG.debug("funder "+crossrefMetadata.getFunderEntriesSortedByCount());
//		LOG.debug("license "+crossrefMetadata.getLicenseEntriesSortedByCount());
//		LOG.debug("link "+crossrefMetadata.getLinkEntriesSortedByCount());
//		LOG.debug("strings "+crossrefMetadata.getStringValueMap());
//		LOG.debug("stringLists "+crossrefMetadata.getStringListValueMap());
//		LOG.debug("stringMap "+crossrefMetadata.getStringMultimapByKey());

	}

	@Test
	public void testJsonPrimitive() {
		String json = "{"
				+ "\"string\": \"mystring\","
				+ "\"number\": 42"
				+ "}";
		LOG.debug("JS "+json);
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = (JsonObject) jsonParser.parse(json);
		AbstractMetadata crossrefMD = new CrossrefMD();
		crossrefMD.analyzeElement(0, jsonObject.get("string"));
		crossrefMD.analyzeElement(0, jsonObject.get("number"));
		
	}

	@Test
	public void testJsonPrimitive1() {
		String json = "{"
				+ "\"indexed\": {"
				+ "  \"date-parts\": ["
		        + "     ["
		        + " 	  2016,"
		        + " 	  6,"
		        + "	      2"
		        + "	    ]"
		        + "	  ],"
		        + "	  \"date-time\": \"2016-06-02T11:40:24Z\","
		        + "	  \"timestamp\": 1464867624071"
		        +	"},"
		        +	"\"reference-count\": 61,"
		        +	"\"publisher\": \"Elsevier BV\""
				+ "}";
		
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = (JsonObject) jsonParser.parse(json);
		AbstractMetadata crossrefMetadata = new CrossrefMD();
		crossrefMetadata.analyzeElement(0, jsonObject.get("publisher"));
		crossrefMetadata.analyzeElement(0, jsonObject.get("reference-count"));
		crossrefMetadata.analyzeElement(0, jsonObject.get("indexed"));
		
	}
	
	@Test 
	@Ignore 
	public void testCrossRefURLs2DOI2FilterCSVFiles() throws IOException {
		int MAX = 99999;
		String fromDate = "2016-05-01";
		int start = 0;
		int delta = 100;
		File pubFilterFile = new File(CMineFixtures.TEST_CROSSREF_DIR, "pubFilter.txt");
		File projectTop = new File(CMineFixtures.TEST_CROSSREF_DIR, "daily/"+fromDate+"/");
		List<List<String>> rows = new ArrayList<List<String>>();
		rows.add(Arrays.asList(new String[] {"trees", "child", "dois", "urls", "filter", "first"}));

		List<String> doiPrefixList = new ArrayList<String>();
		List<String> urlPrefixList = new ArrayList<String>();
		List<String> pubPrefixList = new ArrayList<String>();
		List<Pair<Pattern, String>> filterList = readFilter(pubFilterFile);
		
		for (; start < MAX; start+=delta) {
			String subPath = createSubPath0(fromDate, start, delta);
			File cProjectDir = new File(projectTop, subPath);
			if (!cProjectDir.exists()) {
				LOG.debug("break");
				break;
			}
			CProject cProject = new CProject(cProjectDir);
			List<String> row = new ArrayList<String>();
			row.add(String.valueOf(cProject.getCTreeList().size()));
			row.add(String.valueOf(cProject.getAllChildDirectoryList().size()));
			
			List<String> doiNames = FileUtils.readLines(new File(projectTop, subPath+".dois.txt"));
			List<CMDOI> doiList = CMDOI.readDois(doiNames);
			doiPrefixList.addAll(AbstractCM.getPrefixList(doiList));
			row.add(String.valueOf(doiNames.size()));
			
			List<String> urlNames = FileUtils.readLines(new File(projectTop, subPath+".urls.txt"));
			List<CMURL> urlList = CMURL.readUrls(urlNames);
			for (String urlPrefix : AbstractCM.getPrefixList(urlList)) {
				if (urlPrefix != null) {
					urlPrefixList.add(urlPrefix);
				}
			}
			row.add(String.valueOf(urlNames.size()));

			for (String urlPrefix : AbstractCM.getPrefixList(urlList)) {
				if (urlPrefix != null) {
					pubPrefixList.add(normalizePub(urlPrefix, filterList));
				}
			}
			row.add(String.valueOf(urlNames.size()));

			try {
				row.add(String.valueOf(FileUtils.readLines(new File(projectTop, subPath+".urls.filter.txt")).size()));
			} catch (Exception e) {
				LOG.warn(e);
			}
			row.add(doiNames.size() > 0 ? doiNames.get(0) : "-");
			row.add(urlNames.size() > 0 ? urlNames.get(0) : "-");
			rows.add(row);
		}
		File csvDir = new File("target/csvtest/");
		csvDir.mkdirs();
		RectangularTable csvTable = new RectangularTable();
		csvTable.setRows(rows);
		csvTable.writeCsvFile(new File(csvDir, "projects.csv").toString());
		
		RectangularTable doiCounter = new RectangularTable();
		doiCounter.createMultisetAndOutputRowsWithCounts(doiPrefixList, new File(csvDir, "doiCount.csv").toString());
		RectangularTable urlCounter = new RectangularTable();
		urlCounter.createMultisetAndOutputRowsWithCounts(urlPrefixList, new File(csvDir, "urlCount.csv").toString());
		RectangularTable pubCounter = new RectangularTable();
		pubCounter.createMultisetAndOutputRowsWithCounts(pubPrefixList, new File(csvDir, "pubCount.csv").toString());
		
	}
	


	@Test
	public void testAggregateCrossrefDOIPrefixes() {
		CProject cProject = new CProject(CMineFixtures.GETPAPERS_20160601);
//		cProject.normalizeDOIBasedDirectoryCTrees();
		CTreeList cTreeList = cProject.getCTreeList();
		
		List<String> doiPrefixList = cProject.getDOIPrefixList();
//		LOG.debug("DOIPREFIX "+doiPrefixList);
	}
	
	// =========================
	
	private String normalizePub(String urlPrefix, List<Pair<Pattern, String>> filterList) {
		for (Pair<Pattern, String> filter : filterList) {
			Pattern pattern = filter.getLeft();
			Matcher matcher = pattern.matcher(urlPrefix);
			if (matcher.find()) {
				urlPrefix = urlPrefix.replaceAll(pattern.toString(), filter.getRight());
			}
		}
		return urlPrefix;
	}

	private List<Pair<Pattern, String>> readFilter(File pubFilterFile) throws IOException {
		List<String> lines = FileUtils.readLines(pubFilterFile);
		List<Pair<Pattern, String>> filterList = new ArrayList<Pair<Pattern, String>>();
		for (String line : lines) {
			String[] parts = line.split("\\s+");
			if (parts.length == 0 || parts.length > 2) {
				LOG.warn("filter requires 1/2 parts");
				continue;
			}
			String replace = parts.length == 1 ? "" : parts[1];
//			LOG.debug(">>"+replace);
			Pattern pattern = Pattern.compile(parts[0]);
			Pair<Pattern, String> filter = new MutablePair<Pattern, String>(pattern, replace);
			filterList.add(filter);
		}
		return filterList;
	}

	private String createSubPath(String fromDate, int count, int delta) {
		String s1 = createSubPath0(fromDate, count, delta);
		String s2 = fromDate+"/"+s1;
		LOG.debug("SUBPATH "+s1);
		return s2;
	}

	private String createSubPath0(String fromDate, int count, int delta) {
		String fromDateMin = fromDate.replaceAll("\\-", "");
		String s1 = fromDateMin+"_"+count+"_"+delta;
		return s1;
	}
}
