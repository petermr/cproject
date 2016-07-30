package org.xmlcml.cmine.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.args.FileXPathSearcher;
import org.xmlcml.cmine.metadata.AbstractMetadata;
import org.xmlcml.cmine.metadata.ProjectAnalyzer;
import org.xmlcml.cmine.util.CMineGlobber;
import org.xmlcml.cmine.util.CMineUtil;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.xml.XMLUtil;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import nu.xom.Element;
import nu.xom.Node;

public class CProject extends CContainer {

	private static final Logger LOG = Logger.getLogger(CProject.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String PROJECT_TEMPLATE_XML = "cProjectTemplate.xml";
	public static final String TREE_TEMPLATE_XML = "cTreeTemplate.xml";
	public final static String EUPMC_RESULTS_JSON = "eupmc_results.json";
	public static final String URL_LIST = "urlList.txt";
	
	public final static String IMAGE   = "image";
	public final static String RESULTS = "results";
	public final static String TABLE   = "table";

	// suffixes
	private static final String HTML = "html";

	// move these to plugin subdirs later
	public static final String SPECIES_GENUS_SNIPPETS_XML = "species.genus.snippets.xml";
	public static final String SPECIES_BINOMIAL_SNIPPETS_XML = "species.binomial.snippets.xml";
	public static final String GENE_HUMAN_SNIPPETS_XML = "gene.human.snippets.xml";
	public static final String SEQUENCE_DNAPRIMER_SNIPPETS_XML = "sequence.dnaprimer.snippets.xml";
	public static final String WORD_FREQUENCIES_SNIPPETS_XML = "word.frequencies.snippets.xml";
	
	public static final String DATA_TABLES_HTML = "dataTables.html";

	protected static final String[] ALLOWED_FILE_NAMES = new String[] {
			
		MANIFEST_XML,
		LOG_XML,
		EUPMC_RESULTS_JSON,
		URL_LIST
	};
	
	protected static final Pattern[] ALLOWED_FILE_PATTERNS = new Pattern[] {
	};
	
	protected static final String[] ALLOWED_DIR_NAMES = new String[] {
		RESULTS,
		TABLE,
		IMAGE,
	};
	
	protected static final Pattern[] ALLOWED_DIR_PATTERNS = new Pattern[] {
	};
	public static final String OMIT_EMPTY = "omitEmpty";
	
	private Element projectTemplateElement;
	private Element treeTemplateElement;
	private CTreeList cTreeList;
	private ProjectSnippetsTree projectSnippetsTree;
	private ProjectFilesTree projectFilesTree;
	private ResultsElementList summaryResultsElementList;
	private ArrayList<File> scholarlyList;
//	private boolean shuffleUrls;
//	private boolean pseudoHost;
	private ProjectAnalyzer projectAnalyzer;
	
	public CProject(File cProjectDir) {
		super();
		this.directory = cProjectDir;
		projectTemplateElement = readTemplate(PROJECT_TEMPLATE_XML);
		treeTemplateElement = readTemplate(TREE_TEMPLATE_XML);
	}

	@Override
	protected CManifest createManifest() {
		manifest = new CProjectManifest(this);
		return manifest;
	}
	
	@Override
	protected void getAllowedAndUnknownDirectories() {
		cTreeList = new CTreeList();
		int i = 0;
		for (File directory : allChildDirectoryList) {
//			if (i++ % 100 == 0) System.out.print(".");
			if (false) {
			} else if (
				(isAllowedFile(directory, ALLOWED_DIR_PATTERNS) ||
				isAllowedFileName(directory, ALLOWED_DIR_NAMES))) {
				allowedChildDirectoryList.add(directory);
				// don't consider for CTree
			} else if (isCTree(directory)) {
				CTree cTree = new CTree(directory);
				cTreeList.add(cTree);
			} else {
				unknownChildDirectoryList.add(directory);
			}
		}
	}

	@Override
	protected void getAllowedAndUnknownFiles() {
		for (File file : allChildFileList) {
			if (false) {
			} else if (
				isAllowedFile(file, ALLOWED_FILE_PATTERNS) ||
				isAllowedFileName(file, ALLOWED_FILE_NAMES) ||
				includeAllDirectories()) {
				allowedChildFileList.add(file);
			} else {
				unknownChildFileList.add(file);
			}
		}
	}
	
	private boolean isAllowedFilename(String filename) {
		return (Arrays.asList(ALLOWED_FILE_NAMES).contains(filename));
	}

	/** currently just take a simple approach.
	 * 
	 * if manifest.xml or fulltext.* or results.json is present this should be OK
	 * later we'll use manifest templates
	 * 
	 * @param directory
	 * @return
	 */
	private boolean isCTree(File directory) {
		getTreesAndDirectories();
		CTree testTree = new CTree(directory);
		testTree.getOrCreateChildDirectoryAndChildFileList();
		// put filenames first to eliminate matching
		boolean allowed = 
				isAnyAllowed(testTree.allChildFileList, CTree.ALLOWED_FILE_NAMES) ||
				isAnyAllowed(testTree.allChildDirectoryList, CTree.ALLOWED_DIR_NAMES) ||
				isAnyAllowed(testTree.allChildFileList, CTree.ALLOWED_FILE_PATTERNS) ||
				isAnyAllowed(testTree.allChildDirectoryList, CTree.ALLOWED_DIR_PATTERNS) ||
				includeAllDirectories()
				;
		return allowed;
	}

	public CTreeList getCTreeList() {
		this.getOrCreateFilesDirectoryCTreeLists();
		if (cTreeList != null) {
			cTreeList.sort();
		}
		return cTreeList;
	}

	public List<File> getResultsXMLFileList() {
		List<File> resultsXMLList = new ArrayList<File>();
		this.getCTreeList();
		for (CTree cTree : cTreeList) {
			List<File> resultsXMLList0 = cTree.getResultsXMLFileList();
			resultsXMLList.addAll(resultsXMLList0);
		}
		return resultsXMLList;
	}

	public List<File> getResultsXMLFileList(String control) {
		List<File> resultsXMLList = getResultsXMLFileList();
		if (CProject.OMIT_EMPTY.equals(control)) {
			for (int i = resultsXMLList.size() - 1; i >= 0; i--) {
				File f = resultsXMLList.get(i);
				if (ResultsElement.isEmpty(f)) {
					resultsXMLList.remove(i);
				}
			}
		}
		return resultsXMLList;
	}

	/** outputs filenames relative to project directory.
	 * 
	 * normalizes to UNIX separator
	 * 
	 * i.e. file.get(i) should be equivalent to new File(cprojectDirectory, paths.get(i))
	 * 
	 * @param files
	 * @return list of relative paths
	 */
	public List<String> getRelativeProjectPaths(List<File> files) {
		List<String> fileNames = new ArrayList<String>();
		for (File file : files) {
			String fileName = getRelativeProjectPath(file);
			if (fileName != null) {
				fileNames.add(fileName);
			}
		}
		return fileNames;
	}

	/** outputs filenams relative to project directory.
	 * 
	 * normalizes to UNIX separator
	 * 
	 * i.e. file should be equivalent to new File(cprojectDirectory, path)
	 * 
	 * @param file
	 * @return relative path; null if cannot construct it.
	 */
	public String getRelativeProjectPath(File file) {
		String directoryName = FilenameUtils.normalize(directory.getAbsolutePath(), true);
		String fileName = FilenameUtils.normalize(file.getAbsolutePath(), true);
		String pathName = null;
		if (fileName.startsWith(directoryName)) {
			pathName = fileName.substring(directoryName.length() + 1); // includes separator
		}
		return pathName;
	}

	/**
	 * 
	 * @param glob (e.g. * * /word/ * * /result.xml) [spaces to escape comments so remove spaces a]
	 * @return
	 */
	public ProjectFilesTree extractProjectFilesTree(String glob) {
		ProjectFilesTree projectFilesTree = new ProjectFilesTree(this);
//		List<CTreeFiles> cTreeFilesList = new ArrayList<CTreeFiles>();
		CTreeList cTreeList = this.getCTreeList();
		for (CTree cTree : cTreeList) {
			CTreeFiles cTreeFiles = cTree.extractCTreeFiles(glob);
			projectFilesTree.add(cTreeFiles);
		}
		return projectFilesTree;
	}

	/** get list of matched Elements from CTrees in project.
	 * 
	 * @param glob
	 * @param xpath
	 * @return
	 */
	public ProjectSnippetsTree extractProjectSnippetsTree(String glob, String xpath) {
		projectSnippetsTree = new ProjectSnippetsTree(this);
		CTreeList cTreeList = this.getCTreeList();
		for (CTree cTree : cTreeList) {
			SnippetsTree snippetsTree = cTree.extractXPathSnippetsTree(glob, xpath);
			if (snippetsTree.size() > 0) {
				projectSnippetsTree.add(snippetsTree);
			}
		}
		return projectSnippetsTree;
	}
	
	/** get list of matched Elements from CTrees in project.
	 * 
	 * @param glob
	 * @param xpath
	 * @return
	 */
	public ProjectSnippetsTree extractProjectSnippetsTree(String searchExpression) {
		FileXPathSearcher fileXPathSearcher = new FileXPathSearcher(searchExpression);
		String glob = fileXPathSearcher.getCurrentGlob();
		String xpath = fileXPathSearcher.getCurrentXPath();
		projectSnippetsTree = extractProjectSnippetsTree(glob, xpath);
		return projectSnippetsTree;
	}

	public ProjectSnippetsTree getProjectSnippetsTree() {
		return projectSnippetsTree;
	}
	
	public ProjectFilesTree getProjectFilesTree() {
		return projectFilesTree;
	}

	public void add(CTreeFiles cTreeFiles) {
		ensureProjectFilesTree();
		projectFilesTree.add(cTreeFiles);
	}

	private void ensureProjectFilesTree() {
		if (projectFilesTree == null) {
			projectFilesTree = new ProjectFilesTree(this);
		}
	}

	public void add(SnippetsTree snippetsTree) {
		ensureProjectSnippetsTree();
		projectSnippetsTree.add(snippetsTree);
	}

	private void ensureProjectSnippetsTree() {
		if (projectSnippetsTree == null) {
			projectSnippetsTree = new ProjectSnippetsTree(this);
		}
	}

	public void outputProjectSnippetsTree(File outputFile) {
		outputTreeFile(projectSnippetsTree, outputFile);
	}

	public void outputProjectFilesTree(File outputFile) {
		outputTreeFile(projectFilesTree, outputFile);
	}

	private void outputTreeFile(Element tree, File outputFile)  {
		if (tree != null) {
			try {
				XMLUtil.debug(tree, outputFile, 1);
				LOG.trace("wrote: "+outputFile);
			} catch (IOException e) {
				throw new RuntimeException("Cannot write output: ", e);
			}
		}
	}

	public void addSummaryResultsElement(ResultsElement summaryResultsElement) {
		ensureSummaryResultsElementList();
		LOG.trace("> "+summaryResultsElement.toXML());
		summaryResultsElementList.addToMultiset(summaryResultsElement);
	}

	private void ensureSummaryResultsElementList() {
		if (this.summaryResultsElementList == null) {
			this.summaryResultsElementList = new ResultsElementList();
		}
	}
	
	public Multiset<String> getMultiset() {
		return summaryResultsElementList == null ? null : summaryResultsElementList.getMultisetSortedByCount();
	}

	public boolean hasScholarlyHTML() {
		CTreeList cTreeList = this.getCTreeList();
		for (CTree cTree : cTreeList) {
			if (!cTree.hasScholarlyHTML()) {
				return false;
			}
		}
		return true;
	}

	/** heuristic lists all CProjects under projectTop directory.
	 * finds descendant files through glob and tests them for conformity with CProject
	 * globbing through CMineGlobber
	 * 
	 * @param projectTop
	 * @param glob - allows selection of possible projects
	 * @return
	 */
	public static List<CProject> globCProjects(File projectTop, String glob) {
		List<CProject> projectList = new ArrayList<CProject>();
		List<File> possibleProjectFiles = CMineGlobber.listGlobbedFilesQuietly(projectTop, glob);
		for (File possibleProjectFile : possibleProjectFiles) {
			if (possibleProjectFile.isDirectory()) {
				CProject cProject = CProject.createPossibleCProject(possibleProjectFile);
				if (cProject != null) {
					projectList.add(cProject);
				}
			}
		}
		return projectList;
	}

	public Set<String> extractMetadataItemSet(AbstractMetadata.Type sourceType, String type) {
		CTreeList cTreeList = getCTreeList();
		Set<String> set = new HashSet<String>();
		for (CTree cTree : cTreeList) {
			AbstractMetadata metadata = AbstractMetadata.getMetadata(cTree, sourceType);
			String typeValue = metadata.getJsonStringByPath(type);
			set.add(typeValue);
		}
		return set;
	}

	public Multimap<String, String> extractMetadataItemMap(AbstractMetadata.Type sourceType, String key, String type) {
		CTreeList cTreeList = getCTreeList();
		Multimap<String, String> map = ArrayListMultimap.create();
		for (CTree cTree : cTreeList) {
			AbstractMetadata metadata = AbstractMetadata.getMetadata(cTree, sourceType);
			if (metadata != null) {
				String keyValue = metadata.getJsonStringByPath(key);
				String typeValue = metadata.getJsonStringByPath(type);
				map.put(keyValue, typeValue);
			}
		}
		return map;
	}
	
	public Multimap<CTree, File> extractCTreeFileMapContaining(String reservedName) {
		CTreeList cTreeList = getCTreeList();
		Multimap<CTree, File> map = ArrayListMultimap.create();
		for (CTree cTree : cTreeList) {
			File file = cTree.getExistingReservedFile(reservedName);
			if (file != null && file.exists()) {
				map.put(cTree, file);
			}
		}
		return map;
	}
	
	public File createAllowedFile(String filename) {
		File file = null;
		if (isAllowedFilename(filename)) {
			file = new File(directory, filename);
		}
		return file;
	}
	
	// ====================
	
	public ArrayList<File> getOrCreateScholarlyHtmlList() {
		List<File> files = new ArrayList<File>(FileUtils.listFiles(
				getDirectory(), new String[]{HTML}, true));
		scholarlyList = new ArrayList<File>();
		for (File file : files) {
			if (file.getName().equals(CTree.SCHOLARLY_HTML)) {
				scholarlyList.add(file);
			}
		}
		return scholarlyList;
	}

	public Multiset<String> getOrCreateHtmlBiblioKeys() {
		getOrCreateScholarlyHtmlList();
		Multiset<String> keySet = HashMultiset.create();
		for (File scholarly : scholarlyList) {
			HtmlElement htmlElement = HtmlElement.create(XMLUtil.parseQuietlyToDocument(scholarly).getRootElement());
			List<Node> nodes = XMLUtil.getQueryNodes(htmlElement, "//*[local-name()='meta']/@name");
			for (Node node : nodes) {
				String name = node.getValue().toLowerCase();
				name = name.replace("dcterms", "dc");
				keySet.add(name);
			}
		}
		return keySet;
	}

	private static CProject createPossibleCProject(File possibleProjectFile) {
		CProject project = new CProject(possibleProjectFile);
		CTreeList cTreeList = project.getCTreeList();
		return (cTreeList.size() == 0) ? null : project;
		
	}

	public CTreeList getCTreeList(CTreeExplorer explorer) {
		CTreeList cTreeListOld = this.getCTreeList();
		CTreeList cTreeList = new CTreeList();
		for (CTree cTree : cTreeListOld) {
			if (cTree.matches(explorer)) {
				cTreeList.add(cTree);
			}
		}
		return cTreeList;
	}

	public void normalizeDOIBasedDirectoryCTrees() {
		getCTreeList();
		for (int i = cTreeList.size() - 1; i >= 0; i--) {
			CTree cTree = cTreeList.get(i);
			cTree.normalizeDOIBasedDirectory();
		}
	}

	public List<String> extractShuffledCrossrefUrls() {
		ProjectAnalyzer projectAnalyzer = this.getOrCreateProjectAnalyzer();
		projectAnalyzer.setMetadataType(AbstractMetadata.Type.CROSSREF);
		projectAnalyzer.setShuffleUrls(true);
		projectAnalyzer.setPseudoHost(true);
		List<String> urls = projectAnalyzer.extractURLs();
		return urls;
	}

	public void extractShuffledUrlsFromCrossrefToFile(File file) throws IOException {
		ProjectAnalyzer projectAnalyzer = this.getOrCreateProjectAnalyzer();
		projectAnalyzer.setMetadataType(AbstractMetadata.Type.CROSSREF);
		projectAnalyzer.setShuffleUrls(true);
		projectAnalyzer.setPseudoHost(true);
		projectAnalyzer.extractURLsToFile(file);
	}

	public void setProjectAnalyzer(ProjectAnalyzer projectAnalyzer) {
		this.projectAnalyzer = projectAnalyzer;
	}

	public ProjectAnalyzer getOrCreateProjectAnalyzer() {
		if (this.projectAnalyzer == null) {
			this.projectAnalyzer = new ProjectAnalyzer(this);
		}
		return projectAnalyzer;
	
	}

	public List<String> getDOIPrefixList() {
		List<String> doiPrefixList = new ArrayList<String>();
		CTreeList cTreeList = this.getCTreeList();
		for (CTree cTree : cTreeList) {
			
			String doiPrefix = cTree.extractDOIPrefix();
			doiPrefixList.add(doiPrefix);
		}
		return doiPrefixList;
	}

	public int size() {
		getCTreeList();
		return (cTreeList == null) ? 0 : cTreeList.size();
	}

	public List<String> extractShuffledFlattenedCrossrefUrls() {
		List<String> urls = extractShuffledCrossrefUrls();
		List<String> flattenedUrls = new ArrayList<String>();
		for (int j = 0; j < urls.size(); j++) {
			String url = urls.get(j);
			String flattenedURL = CMineUtil.denormalizeDOI(url);
			flattenedUrls.add(flattenedURL);
		}
		return flattenedUrls;
	}


}
