package org.xmlcml.cmine.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.args.FileXPathSearcher;
import org.xmlcml.xml.XMLUtil;

import com.google.common.collect.Multiset;

import nu.xom.Element;

public class CProject extends CContainer {

	private static final Logger LOG = Logger.getLogger(CProject.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String PROJECT_TEMPLATE_XML = "cProjectTemplate.xml";
	private static final String TREE_TEMPLATE_XML = "cTreeTemplate.xml";
	public final static String EUPMC_RESULTS_JSON = "eupmc_results.json";
	public final static String RESULTS = "results";
	public final static String SUMMARY = "summary";

	public static final String SNIPPETS_XML = "snippets.xml";
	public static final String DOCUMENTS_XML = "documents.xml";
	public static final String COUNT_XML = "count.xml";
	
	public static final String DATA_TABLES_HTML = "dataTables.html";

	protected static final String[] ALLOWED_FILE_NAMES = new String[] {
		MANIFEST_XML,
		LOG_XML,
		EUPMC_RESULTS_JSON,
	};

	private static final List<String> RESERVED_CHILD_DIRECTORY_NAMES = Arrays.asList(
		new String[]{
				RESULTS, 
				SUMMARY}
		);
		

	protected static final Pattern[] ALLOWED_FILE_PATTERNS = new Pattern[] {
	};
	
	protected static final String[] ALLOWED_DIR_NAMES = new String[] {
		RESULTS,
	};
	
	protected static final Pattern[] ALLOWED_DIR_PATTERNS = new Pattern[] {
	};
	public static final String OMIT_EMPTY = "omitEmpty";
	
	private Element projectTemplateElement;
	private Element treeTemplateElement;
	private CTreeList cTreeList;
	private PluginSnippetsTree currentPluginSnippetsTree;
	private ProjectFilesTree projectFilesTree;
	private ResultsElementList summaryResultsElementList;
	private String name;
	
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
		for (File directory : allChildDirectoryList) {
			if (false) {
			} else if (
				isAllowedFile(directory, ALLOWED_DIR_PATTERNS) ||
				isAllowedFileName(directory, ALLOWED_DIR_NAMES)) {
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
	
	public static boolean isReservedProjectChildDirectory(File f) {
		String name = f.getName();
		return RESERVED_CHILD_DIRECTORY_NAMES.contains(name);
	}

	@Override
	protected void getAllowedAndUnknownFiles() {
		for (File file : allChildFileList) {
			if (false) {
			} else if (
				isAllowedFile(file, ALLOWED_FILE_PATTERNS) ||
				isAllowedFileName(file, ALLOWED_FILE_NAMES)) {
				allowedChildFileList.add(file);
			} else {
				unknownChildFileList.add(file);
			}
		}
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
		testTree.getDirectoryAndFileList();
		return isAnyAllowed(testTree.allChildFileList, CTree.ALLOWED_FILE_PATTERNS) ||
				isAnyAllowed(testTree.allChildFileList, CTree.ALLOWED_FILE_NAMES) ||
				isAnyAllowed(testTree.allChildDirectoryList, CTree.ALLOWED_DIR_PATTERNS) ||
				isAnyAllowed(testTree.allChildDirectoryList, CTree.ALLOWED_DIR_NAMES);
	}

	public CTreeList getCTreeList() {
		this.getOrCreateFilesDirectoryCTreeLists();
		LOG.trace("CTREE "+cTreeList.size());
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
				if (ResultContainerElement.isEmpty(f)) {
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

	public ProjectFilesTree extractProjectFilesTree(String glob) {
		ProjectFilesTree projectFilesTree = new ProjectFilesTree(this);
		List<CTreeFiles> cTreeFilesList = new ArrayList<CTreeFiles>();
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
	public PluginSnippetsTree extractPluginSnippetsTree(String glob, String xpath) {
		PluginSnippetsTree pluginSnippetsTree = new PluginSnippetsTree(this);
		LOG.debug("Extracting snippets ...");
		getOrCreateCurrentPluginSnippetsTree();
		CTreeList cTreeList = this.getCTreeList();
		for (CTree cTree : cTreeList) {
			SnippetsTree snippetsTree = cTree.extractXPathSnippetsTree(glob, xpath);
			if (snippetsTree.size() > 0) {
//				currentPluginSnippetsTree.add(snippetsTree);
				pluginSnippetsTree.add(snippetsTree);
			}
		}
		return currentPluginSnippetsTree;
	}
	
	/** get list of matched Elements from CTrees in project.
	 * 
	 * @param glob
	 * @param xpath
	 * @return
	 */
	public PluginSnippetsTree extractPluginSnippetsTree(String searchExpression) {
		FileXPathSearcher fileXPathSearcher = new FileXPathSearcher(searchExpression);
		String glob = fileXPathSearcher.getCurrentGlob();
		String xpath = fileXPathSearcher.getCurrentXPath();
		PluginSnippetsTree pluginSnippetsTree = extractPluginSnippetsTree(glob, xpath);
		return pluginSnippetsTree;
	}

	/** returns any existing PST.
	 * if null, and a 
	 * 
	 * @return
	 */
	public PluginSnippetsTree getOrCreateCurrentPluginSnippetsTree() {
		if (currentPluginSnippetsTree == null) {
			currentPluginSnippetsTree = new PluginSnippetsTree(this);
		}
		return currentPluginSnippetsTree;
//		return pluginOption == null ? null : getOrCreatePluginSnippetsTree(pluginOption.getSnippetsName());
//		return pluginOption == null ? null : getOrCreatePluginSnippetsTree(pluginOption.getSnippetsName());
	}
	
//	public PluginSnippetsTree getOrCreatePluginSnippetsTree(String expression) {
//		PluginSnippetsTree pluginSnippetsTree = null;
//		if (expression != null) {
//			ensurePluginSnippetsTreeByName();
//			pluginSnippetsTree = pluginSnippetsTreeByExpression.get(expression);
//			if (pluginSnippetsTree == null) {
//				pluginSnippetsTree = new PluginSnippetsTree(this);
//				pluginSnippetsTreeByExpression.put(expression, pluginSnippetsTree);
//				LOG.trace("EXP "+expression+" | "+pluginSnippetsTreeByExpression.keySet());
//			} else {
//				LOG.trace("SNIPPETS TREE "+pluginSnippetsTree);
//			}
//		}
//		return pluginSnippetsTree;
//	}
//	
	/** creates a PST from an existing pluginSnippetsTree element.
	 * 
	 * @return
	 */
	private PluginSnippetsTree createPluginSnippetsTreeFromExistingElement() {
		File pstFile = new File(directory, "");
		throw new RuntimeException("Use PluginSnippetsTree instead");
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

	public void addSummaryResultsElement(ResultContainerElement summaryResultsElement) {
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

	public void add(SnippetsTree snippetsTree) {
		if (snippetsTree != null) {
			PluginSnippetsTree pluginSnippetsTree = getOrCreateCurrentPluginSnippetsTree();
			pluginSnippetsTree.add(snippetsTree);
		}
	}

//	private void ensurePluginSnippetsTreeByName() {
//		if (pluginSnippetsTreeByExpression == null) {
//			pluginSnippetsTreeByExpression = new HashMap<String, PluginSnippetsTree>();
//		}
//	}

	public void outputPluginSnippetsTree(String expression, File outputFile) {
		// file(**/word/**/results.xml)xpath(//result[@count>20])
//		expression = PluginOption.removePunctuation(expression);
		try {
			outputFile.getParentFile().mkdirs();
			XMLUtil.debug(currentPluginSnippetsTree, new FileOutputStream(outputFile), 1);
		} catch (Exception e) {
			throw new RuntimeException("Cannot wrtite: "+outputFile, e);
		}
	}

	public void setDirectory(File projectDir) {
		this.directory = projectDir;
	}

	/** returns directories such as "results" and "summary"
	 * 
	 * @return
	 */
	public List<File> getReservedChildDirectoryList() {
		List<File> reservedFiles = new ArrayList<File>();
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (CProject.isReservedProjectChildDirectory(file)) {
					reservedFiles.add(file);
				}
			}
		}
		return reservedFiles;
	}

	public File getReservedChildDirectory(String name) {
		List<File> reservedFiles = getReservedChildDirectoryList();
		for (File reservedFile : reservedFiles) {
			if (reservedFile.getName().equals(name)) {
				return reservedFile;
			}
		}
		return null;
	}

	/** gets project name (creates one from base of file if null).
	 * 
	 * @return
	 */
	public String getName() {
		if (name == null) {
			name = FilenameUtils.getBaseName(directory.toString());
		}
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
