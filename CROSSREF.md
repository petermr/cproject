# CROSSREF tools

## shuffled URLs


MetadataLongTest

	@Test
	/** reads a getpapers project and extracts the shuffled urls.
	 * 
	 * @throws IOException
	 */
	public void testGetShuffledDOIURLs() throws IOException {
		CProject cProject = new CProject(new File(CMineFixtures.GETPAPERS_NEW, "2016020"+i+"-articles"));
		File shuffled = new File(cProject.getDirectory(), MetadataManager.SHUFFLED_URLS_TXT);
		cProject.extractShuffledUrlsFromCrossrefToFile(shuffled);
		MetadataManager metadataManager = new MetadataManager();
		metadataManager.readMetadataTable(new File(cProject.getDirectory(), "crossref_common.csv"), MetadataManager.CR);
		List<String> lines = FileUtils.readLines(shuffled);
		lines = lines.subList(0,  10);
		Assert.assertEquals("lines "+lines.size(), "[http://dx.doi.org/10.1002/zoo.21264,"
				+ " http://dx.doi.org/10.1007/s41105-016-0048-8,"
				+ " http://dx.doi.org/10.1016/s2225-4110(16)00008-0,"
				...
				+ " http://dx.doi.org/10.1049/iet-wss.2014.0090]"
				+ "",  lines.toString());
	}

	@Test
	/** reads a getpapers project and reads the common metadata.
	 * 
	 * @throws IOException
	 */
	public void testReadCrossrefCommonCSV() throws IOException {
		if (!CMineFixtures.exist(CMineFixtures.GETPAPERS_NEW)) return;
		int i = 1; // file number
		CProject cProject = new CProject(new File(CMineFixtures.GETPAPERS_NEW, "2016020"+i+"-articles"));
		// pre-existing CSV file
		File csvFile = new File(cProject.getDirectory(), "crossref_common.csv");
		MetadataManager metadataManager = new MetadataManager();
		RectangularTable table = metadataManager.readMetadataTable(csvFile, MetadataManager.CROSSREF);
		Assert.assertEquals("[License, Title, DOI, Publisher, Prefix, Date, Keywords]", table.getHeader().toString());
	}
	
CrossrefCSVTest

	/** GET HEADERS FROM CROSSREF SPREADSHEET.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetpapers() throws IOException {
		RectangularTable table = RectangularTable.readTable(CMineFixtures.CROSSREF_SRC_A_1_CSV, true);
		Assert.assertEquals(12141, table.size());
		Assert.assertEquals("[License, Title, DOI, Publisher, Prefix, Date, Keywords]", table.getHeader().toString());
	}

	/** ANALYZE DOI COLUMN FROM CROSSREF SPREADSHEET.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testAnalyzeDOIColumn() throws IOException {
		String colHead = "DOI";
		RectangularTable table = RectangularTable.readTable(CMineFixtures.CROSSREF_SRC_A_1_CSV, true);
		List<String> columnValues = table.getColumn(table.getIndexOfColumn(colHead));
		Assert.assertEquals(12141, columnValues.size());
		List<Multiset.Entry<String>> multisetList = table.extractSortedMultisetList(colHead);
		Assert.assertEquals(12141, multisetList.size());
		List<Multiset.Entry<String>> uniqueMultisetList = table.extractUniqueMultisetList(colHead);
		Assert.assertEquals(12141, uniqueMultisetList.size());
		List<Multiset.Entry<String>> duplicateMultisetList = table.extractDuplicateMultisetList(colHead);
		Assert.assertEquals(0, duplicateMultisetList.size());
		
	}
	
	/** ANALYZE LICENSE COLUMN FROM CROSSREF SPREADSHEET.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testAnalyzeLicenseColumn() throws IOException {
		String colHead = "License";
		RectangularTable table = RectangularTable.readTable(CMineFixtures.CROSSREF_SRC_A_1_CSV, true);
		List<String> columnValues = table.getColumn(table.getIndexOfColumn(colHead));
		Assert.assertEquals(12141, columnValues.size());
		List<Multiset.Entry<String>> multisetList = table.extractSortedMultisetList(colHead);
		Assert.assertEquals(33, multisetList.size());
		List<Multiset.Entry<String>> uniqueMultisetList = table.extractUniqueMultisetList(colHead);
		Assert.assertEquals(2, uniqueMultisetList.size());
		Assert.assertEquals("["
				+ "3: [http://doi.wiley.com/10.1002/tdm_license_1,"
				+ " http://creativecommons.org/licenses/by/4.0/,"
				+ " http://creativecommons.org/licenses/by/4.0/],"
				+ " 3: [http://doi.wiley.com/10.1002/tdm_license_1,"
				+ " http://creativecommons.org/licenses/by-nc-nd/4.0/,"
				+ " http://creativecommons.org/licenses/by-nc-nd/4.0/]]", uniqueMultisetList.toString());
		List<Multiset.Entry<String>> duplicateMultisetList = table.extractDuplicateMultisetList(colHead);
		Assert.assertEquals(31, duplicateMultisetList.size());
		Assert.assertEquals("["
				+ "0: [] x 7537,"
				+ " 1: [http://www.elsevier.com/tdm/userlicense/1.0/] x 2116,"
				+ " 1: [http://www.springer.com/tdm] x 1023,"
				+ " 2: [http://doi.wiley.com/10.1002/tdm_license_1, http://onlinelibrary.wiley.com/termsAndConditions] x 607,"
				+ " 2: [http://www.elsevier.com/tdm/userlicense/1.0/, http://creativecommons.org/licenses/by-nc-nd/4.0/] x 130,"
				+ " 1: [http://www.acm.org/publications/policies/copyright_policy#Background] x 125,"
				+ " 1: [http://link.aps.org/licenses/aps-default-license] x 91,"
				+ " 1: [http://creativecommons.org/licenses/by/4.0/] x 65,"
				+ " 2: [http://iopscience.iop.org/info/page/text-and-data-mining, http://iopscience.iop.org/page/copyright] x 62,"
				+ " 2: [http://iopscience.iop.org/info/page/text-and-data-mining, http://creativecommons.org/licenses/by/3.0/] x 54,"
				+ " 1: [http://creativecommons.org/licenses/by/3.0/] x 50,"
				+ " 2: [http://link.aps.org/licenses/aps-default-license, http://link.aps.org/licenses/aps-default-accepted-manuscript-license] x 45,"
				+ " 1: [http://creativecommons.org/licenses/by-nc/4.0] x 31,"
				+ " 2: [http://www.elsevier.com/tdm/userlicense/1.0/, http://creativecommons.org/licenses/by/4.0/] x 30,"
				+ " 2: [http://doi.wiley.com/10.1002/tdm_license_1, http://creativecommons.org/licenses/by/4.0/] x 24,"
				+ " 1: [http://www.bmj.org/licenses/tdm/1.0/terms-and-conditions.html] x 20,"
				+ " 1: [http://doi.wiley.com/10.1002/tdm_license_1] x 20,"
				+ " 1: [http://creativecommons.org/licenses/by-nc-nd/4.0] x 16,"
				+ " 2: [http://creativecommons.org/licenses/by/2.5/za/, http://creativecommons.org/licenses/by/2.5/za/] x 16,"
				+ " 2: [http://doi.wiley.com/10.1002/tdm_license_1, http://creativecommons.org/licenses/by-nc-nd/4.0/] x 15,"
				+ " 1: [http://creativecommons.org/licenses/by/4.0] x 13,"
				+ " 3: [http://doi.wiley.com/10.1002/tdm_license_1, http://onlinelibrary.wiley.com/termsAndConditions, http://onlinelibrary.wiley.com/termsAndConditions] x 9,"
				+ " 1: [http://creativecommons.org/licenses/by-nc/3.0/] x 8, 1: [http://pubs.acs.org/page/policy/authorchoice_termsofuse.html] x 8,"
				+ " 2: [http://doi.wiley.com/10.1002/tdm_license_1, http://creativecommons.org/licenses/by-nc/4.0/] x 5,"
				+ " 1: [https://publishing.aip.org/authors/rights-and-permissions] x 4,"
				+ " 3: [http://iopscience.iop.org/info/page/text-and-data-mining, http://iopscience.iop.org/page/copyright, http://creativecommons.org/licenses/by-nc-nd/3.0] x 4,"
				+ " 3: [http://creativecommons.org/licenses/by/4.0/, http://creativecommons.org/licenses/by/4.0/, http://creativecommons.org/licenses/by/4.0/] x 4,"
				+ " 1: [http://creativecommons.org/licenses/by-sa/4.0] x 3,"
				+ " 1: [https://www.osapublishing.org/submit/licenses/license_v1.cfm#vor] x 2,"
				+ " 3: [http://iopscience.iop.org/info/page/text-and-data-mining, http://creativecommons.org/licenses/by/3.0/, http://creativecommons.org/licenses/by/3.0] x 2]",
				duplicateMultisetList.toString());
		
	}

	/** ANALYZE PUBLISHERS FROM SPREADSHEET.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testAnalyzePublishers() throws IOException {
		String colHead = "Publisher";
		RectangularTable table = RectangularTable.readTable(CMineFixtures.CROSSREF_SRC_A_1_CSV, true);
		List<String> columnValues = table.getColumn(table.getIndexOfColumn(colHead));
		Assert.assertEquals(12141, columnValues.size());
		List<Multiset.Entry<String>> multisetList = table.extractSortedMultisetList(colHead);
		Assert.assertEquals(325, multisetList.size());
		Assert.assertEquals("[Elsevier BV x 2271,"
		+ " Springer Science + Business Media x 1038,"
		+ " Wiley-Blackwell x 682,"
		+ " Hamad bin Khalifa University Press (HBKU Press) x 456,"
		+ " Informa UK Limited x 438,"
		+ " Clute Institute x 364,"
		+ " Logos Medi", multisetList.toString().substring(0,  200));
		List<Multiset.Entry<String>> uniqueMultisetList = table.extractUniqueMultisetList(colHead);
		Assert.assertEquals(53, uniqueMultisetList.size());
		Assert.assertEquals("[Association Palaeovertebrata,"
				+ " Federal Reserve Bank of Kansas City,"
				+ " Associacao Sergipana de Ciencia,"
				+ " University of South Florida Libraries,"
				+ " Science and Education Centre of North America,"
				+ " Rubber Divisi", uniqueMultisetList.toString().substring(0,  200));
		List<Multiset.Entry<String>> duplicateMultisetList = table.extractDuplicateMultisetList(colHead);
		Assert.assertEquals(272, duplicateMultisetList.size());
		Assert.assertEquals("[Elsevier BV x 2271,"
				+ " Springer Science + Business Media x 1038,"
				+ " Wiley-Blackwell x 682,"
				+ " Hamad bin Khalifa University Press (HBKU Press) x 456,"
				+ " Informa UK Limited x 438,"
				+ " Clute Institute x 364,"
				+ " Logos Medi", duplicateMultisetList.toString().substring(0,  200));
	}
	

CrossrefTest

    public void testResolveDOI() throws Exception {
		String crossRefDOI = "http://dx.doi.org/10.3389/fpsyg.2016.00565";
		String s = new DOIResolver().resolveDOI(crossRefDOI);
	}
	
		/** GET CROSSREF METADATA FROM JSON
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetCrossrefMetadataFromJson() throws Exception {
		CrossrefMD crossrefMetadata = new CrossrefMD();
		crossrefMetadata.readMetadataArrayFromConcatenatedFile(new File(CMineFixtures.TEST_CROSSREF_SAMPLE, "crossref_results.json"));
		crossrefMetadata.getOrCreateMetadataList();
		Assert.assertEquals("KEYS ", 36356, +crossrefMetadata.getKeysmap().size());
		Assert.assertEquals("unique KEYS ", 35,  crossrefMetadata.getKeysmap().entrySet().size());
		crossrefMetadata.debugStringValues();
		crossrefMetadata.debugNumberValues();
		String allKeys = CMineUtil.getEntriesSortedByCount(crossrefMetadata.getAllKeys()).toString();
		Assert.assertTrue(allKeys.contains("prefix x 1552")  && allKeys.contains("title x 1552"));
		/**
		// omitted as problems with sorting entries
		Assert.assertEquals("allkeys", 
				"[prefix x 1552, deposited x 1552, source x 1552, type x 1552, title x 1552, URL x 1552,"
				+ " score x 1552, member x 1552, reference-count x 1552, issued x 1552, DOI x 1552,"
				+ " indexed x 1552, created x 1552, container-title x 1552, subtitle x 1552, publisher x 1552,"
				+ " ISSN x 1445, author x 1209, page x 1109, published-print x 1060, published-online x 1051,"
				+ " volume x 1036, issue x 875, subject x 777, license x 706, alternative-id x 700, link x 666,"
				+ " update-policy x 227, ISBN x 172, archive x 163, assertion x 146, funder x 126, article-number x 36,"
				+ " editor x 12, update-to x 8]" , allKeys.substring(0, 200));
				*/
		String authors = crossrefMetadata.getAuthorListAsStrings().toString();
		Assert.assertEquals("author", "[Martin K. Heath, Lindsey Brooks D., Ma Jianguo, Nichols Timothy C., Jiang Xiaoning, Dayton Paul A., Lee Ming-Che, Yang Ying-Chin, Chen Yen-Cheng, Chang Bee-Song, Li Yi-Chen, Huang Shih-Che, Miao Xin," , authors.substring(0, 200));
		String funders = crossrefMetadata.getFunderEntriesSortedByCount().toString();
		Assert.assertEquals("funders", "[DEC 2013/09/B/ST5/03391 National Science Centre of Poland x 2, 024.001.035 Ministry of Education and Science 10.13039/501100005992 x 2,  CRO Aviano x 2,  National Science Foundation 10.13039/10000000" , funders.substring(0, 200));
		String licenses = crossrefMetadata.getLicenseEntriesSortedByCount().toString();
		Assert.assertEquals("licenses", "[http://www.elsevier.com/tdm/userlicense/1.0/ x 282, http://doi.wiley.com/10.1002/tdm_license_1 x 162, http://onlinelibrary.wiley.com/termsAndConditions x 154, http://www.springer.com/tdm x 130, http:" , licenses.substring(0, 200));
		String links = crossrefMetadata.getLinkEntriesSortedByCount().toString();
		Assert.assertEquals("links", "[application/pdf http://api.wiley.com/onlinelibrary/tdm/v1/articles/10.1111%2Fjoic.12292, application/pdf http://stacks.iop.org/0295-5075/114/i=3/a=30006/pdf, text/plain http://api.elsevier.com/conten" , links.substring(0, 200));

	}

CrossrefLongTest

	/** CREATE CSV FILE FROM CPROJECT
	 * 
	 */

	@Test
	/** starts with cProject created from crossref output.
	 * i.e. directories all contain a single crossref_result.json file.
	 * 
	 * this summarizes the metadata into a communal table and writes the CSV file
	 * 
	 * 
	 * @throws IOException
	 */
	public void testReadProjectAndWriteCSV() throws IOException {
		if (!CMineFixtures.exist(CMineFixtures.GETPAPERS_NEW)) return;
		List<String> headers = new ArrayList<String>(Arrays.asList(new String[] {
				AbstractMetadata.LICENSE,
				AbstractMetadata.TITLE,
				AbstractMetadata.DOI,
				AbstractMetadata.PUBLISHER,
				AbstractMetadata.PREFIX,
				AbstractMetadata.DATE,
				AbstractMetadata.KEYWORDS,
		}));
		int i = 1;
		MetadataObjects allMetadataObjects = new MetadataObjects();
		CProject cProject = new CProject(new File(CMineFixtures.GETPAPERS_NEW+"/2016020"+i+"-articles"));
		AbstractMDAnalyzer crossrefAnalyzer = new CrossrefAnalyzer(cProject);
		crossrefAnalyzer.addRowsToTable(headers, AbstractMetadata.Type.CROSSREF);
		crossrefAnalyzer.createMultisets();
		crossrefAnalyzer.writeDOIs(new File(CMineFixtures.GETPAPERS_TARGET, "crossref_a_"+i+".doi.txt"));
		File file = new File(CMineFixtures.GETPAPERS_TARGET, "/crossref_a_"+i+".csv");
		crossrefAnalyzer.writeCsvFile(file);
		int size = cProject.size();
		LOG.debug("wrote: "+file+": "+size);
		// aggregate
		MetadataObjects metadataObjects = crossrefAnalyzer.getMetadataObjects();
		allMetadataObjects.writeMultisetSortedByCount(allMetadataObjects.getPublisherMultiset(), new File(CMineFixtures.GETPAPERS_TARGET, "publishers_a_"+i+".txt").toString());
		allMetadataObjects.addAll(metadataObjects);
		allMetadataObjects.writeStringKeys("target/metadata/stringKeys_a.txt");
		allMetadataObjects.writeMultisetSortedByCount(allMetadataObjects.getPublisherMultiset(),  new File(CMineFixtures.GETPAPERS_TARGET, "publishers_a7.txt"));
		allMetadataObjects.writeMultisetSortedByCount(allMetadataObjects.getFinalLicenseSet(),  new File(CMineFixtures.GETPAPERS_TARGET, "licenses_a7.txt"));
		allMetadataObjects.writeMultisetSortedByCount(allMetadataObjects.getPrefixSet(),  new File(CMineFixtures.GETPAPERS_TARGET, "prefix_a7.txt"));
		allMetadataObjects.writeMultisetSortedByCount(allMetadataObjects.getFinalKeywordSet(),  new File(CMineFixtures.GETPAPERS_TARGET, "keyword_a7.txt"));
		
	}
	
	/** CREATES A CSV FILE FROM CROSSREF DIRECTORY.
	 * 
	 * and READS IT
	 * 
	 * @throws IOException
	 */
	@Test
	// PMR_ONLY
	public void testCreateCrossrefCommonCSV() throws IOException {
		if (!CMineFixtures.exist(CMineFixtures.GETPAPERS_NEW)) return;
		List<String> headers = AbstractMetadata.COMMON_HEADERS;
		int i = 1;
		File articles = new File(CMineFixtures.GETPAPERS_NEW, "2016020"+i+"-articles");
		CProject cProject = new CProject(articles);
		AbstractMDAnalyzer crossrefAnalyzer = new CrossrefAnalyzer(cProject);
		crossrefAnalyzer.addRowsToTable(headers, AbstractMetadata.Type.CROSSREF);
		File csvFile= new File(CMineFixtures.GETPAPERS_TARGET, "crossref/common.csv");
		FileUtils.forceDelete(csvFile);
		crossrefAnalyzer.writeCsvFile(csvFile);
		Assert.assertTrue(csvFile.exists());
		// check file
		RectangularTable table = RectangularTable.readTable(csvFile, true);
		Assert.assertEquals(12141, table.size());
	}
	

	


	
	


