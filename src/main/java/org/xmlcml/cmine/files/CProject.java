package org.xmlcml.cmine.files;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Element;

public class CProject extends CContainer {

	private static final Logger LOG = Logger.getLogger(CProject.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String PROJECT_TEMPLATE_XML = "cProjectTemplate.xml";
	private static final String TREE_TEMPLATE_XML = "cTreeTemplate.xml";
	public final static String EUPMC_RESULTS_JSON = "eupmc_results.json";
	private final static String RESULTS = "results";
	
	protected static final String[] ALLOWED_FILE_NAMES = new String[] {
		MANIFEST_XML,
		LOG_XML,
		EUPMC_RESULTS_JSON,
	};
	
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
	private List<Pattern> allowedFilePatterns;
	
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

	public List<CTreeFiles> listCTreeFiles(String glob) {
		List<CTreeFiles> cTreeFilesList = new ArrayList<CTreeFiles>();
		CTreeList cTreeList = this.getCTreeList();
		for (CTree cTree : cTreeList) {
			List<File> fileList = cTree.extractFiles(glob);
			cTreeFilesList.add(new CTreeFiles(fileList));
		}
		return cTreeFilesList;
	}

	/** get list of matched Elements from CTrees in project.
	 * 
	 * @param glob
	 * @param xpath
	 * @return
	 */
	public List<List<XMLSnippets>> getXPathSnippetsListList(String glob, String xpath) {
		List<List<XMLSnippets>> elementListListList = new ArrayList<List<XMLSnippets>>();
		CTreeList cTreeList = this.getCTreeList();
		for (CTree cTree : cTreeList) {
			List<XMLSnippets> elementListList = cTree.extractXPathSnippetsList(glob, xpath);
			if (elementListList.size() > 0) {
				elementListListList.add(new ArrayList<XMLSnippets>(elementListList));
			}
		}
		return elementListListList;
	}
}
