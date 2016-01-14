package org.xmlcml.cmine.args;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.args.log.AbstractLogElement;
import org.xmlcml.cmine.args.log.CMineLog;
import org.xmlcml.cmine.files.AbstractSearcher;
import org.xmlcml.cmine.files.CTree;
import org.xmlcml.cmine.files.CTreeList;
import org.xmlcml.cmine.files.EuclidSource;
import org.xmlcml.cmine.files.Unzipper;
import org.xmlcml.cmine.lookup.AbstractDictionary;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlFactory;
import org.xmlcml.html.HtmlP;
import org.xmlcml.xml.XMLUtil;

import nu.xom.Builder;
import nu.xom.Element;


/** base class for all arg processing. Also contains the workflow logic:
 * 
 * the list of CTrees is created in 
 * 
 * parseArgs(String[]) or
 * parseArgs(String)
 * 
 * calls 
 * 	protected void addArgumentOptionsAndRunParseMethods(ArgIterator argIterator, String arg) throws Exception {
 * 		which iterates through the args, loking for init* and parse* methods 
			for (ArgumentOption option : argumentOptionList) {
				if (option.matches(arg)) {
					LOG.trace("OPTION>> "+option);
					String initMethodName = option.getInitMethodName();
					if (initMethodName != null) {
						runInitMethod(option, initMethodName);
					}
					String parseMethodName = option.getParseMethodName();
					if (parseMethodName != null) {
						runParseMethod(argIterator, option, parseMethodName);
					}
					processed = true;
					chosenArgumentOptionList.add(option);
					break;
				}
			}
		}
	}
	
	this will generate CTreeList 
	after that 

 * 
 * 
 * runAndOutput() iterates through each CTree
 * 
		for (int i = 0; i < CTreeList.size(); i++) {
			currentCTree = CTreeList.get(i);
			// generateLogFile here
			currentCTree.getOrCreateLog();
			// each CTree has a ContentProcessor
			currentCTree.ensureContentProcessor(this);
			// possible initFooOption
			runInitMethodsOnChosenArgOptions();
			// possible runFooOption
			runRunMethodsOnChosenArgOptions();
			// possible outputFooOptions
			runOutputMethodsOnChosenArgOptions();
		}
		// a "reduce" or "gather" method to run overe many CTrees (e.g summaries)
		runFinalMethodsOnChosenArgOptions();
	}

 * NOTE: changed all *internal* CTree-type names to CTree. 2015-09-15
 * 
 * @author pm286
 *
 */
public class DefaultArgProcessor {
	
	private static final Logger LOG = Logger.getLogger(DefaultArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String ARGS2HTML_XSL = "/org/xmlcml/cmine/args/args2html.xsl";
	private static final File MAIN_RESOURCES = new File("src/main/resources");
	public static final String MINUS = "-";
	public static final String[] DEFAULT_EXTENSIONS = {"html", "xml", "pdf"};
	public final static String H = "-h";
	public final static String HELP = "--help";
	private static Pattern INTEGER_RANGE = Pattern.compile("(.*)\\{(\\d+),(\\d+)\\}(.*)");

	private static String RESOURCE_NAME_TOP = "/org/xmlcml/cmine/args";
	protected static final String ARGS_XML = "args.xml";
	private static String ARGS_RESOURCE = RESOURCE_NAME_TOP+"/"+ARGS_XML;
	private static final String NAME = "name";
	private static final String VERSION = "version";
	
	private static final Pattern INTEGER_RANGE_PATTERN = Pattern.compile("(\\d+):(\\d+)");
	public static final String WHITESPACE = "\\s+";
	public static Pattern GENERAL_PATTERN = Pattern.compile("\\{([^\\}]*)\\}");
	
	public static final VersionManager DEFAULT_VERSION_MANAGER = new VersionManager();
	public static final String LOGFILE = "target/log.xml";
	
	/** creates a list of tokens that are found in an allowed list.
	 * 
	 * @param allowed
	 * @param tokens
	 * @return list of allowed tokens
	 */
	protected static List<String> getChosenList(List<String> allowed, List<String> tokens) {
		List<String> chosenTokens = new ArrayList<String>();
		for (String method : tokens) {
			if (allowed.contains(method)) {
				chosenTokens.add(method);
			} else {
				LOG.error("Unknown token: "+method);
			}
		}
		return chosenTokens;
	}

	// arg values
	protected String output;
	protected List<String> extensionList = null;
	private boolean recursive = false;
	protected List<String> inputList;
	protected String logfileName;
	public String update;
	
	public List<ArgumentOption> argumentOptionList;
	public List<ArgumentOption> chosenArgumentOptionList;
	
	protected CTreeList cTreeList;
	// change protection later
	protected CTree currentCTree;
	protected String summaryFileName;
	// variable processing
	protected Map<String, String> variableByNameMap;
	private VariableProcessor variableProcessor;
	protected String project;
	private AbstractLogElement cTreeLog;
	private AbstractLogElement coreLog;
	private File projectFile;
	private String includePatternString;
	private boolean unzip = false;
	private List<List<String>> renamePairs;
	private String zipRootName;
	protected List<AbstractDictionary> dictionaryList;
	
	protected List<ArgumentOption> getArgumentOptionList() {
		return argumentOptionList;
	}

	public DefaultArgProcessor() {
		ensureDefaultLogFiles();
		readArgumentOptions(getArgsResource());
	}
	
	private void ensureDefaultLogFiles() {
		TREE_LOG();
		CORE_LOG();
	}

	public AbstractLogElement CORE_LOG() {
		if (coreLog == null) {
			createInitLog(new File("target/defaultInitLog.xml"));
		}
		return coreLog;
	}

	public AbstractLogElement TREE_LOG() {
		if (cTreeLog == null) {
			createCTreeLog(new File("target/defaultCTreeLog.xml"));
		}
		return cTreeLog;
	}

	public void createCTreeLog(File logFile) {
		cTreeLog = new CMineLog(logFile);
	}

	public void createInitLog(File logFile) {
		coreLog = new CMineLog(logFile);
	}

	protected static VersionManager getVersionManager() {
//		LOG.debug("VM Default "+DEFAULT_VERSION_MANAGER.hashCode()+" "+DEFAULT_VERSION_MANAGER.getName()+";"+DEFAULT_VERSION_MANAGER.getVersion());
		return DEFAULT_VERSION_MANAGER;
	}
	
	
	private String getArgsResource() {
		return ARGS_RESOURCE;
	}
	
	public void readArgumentOptions(String resourceName) {
		ensureArgumentOptionList();
		try {
			InputStream is = this.getClass().getResourceAsStream(resourceName);
			if (is == null) {
				throw new RuntimeException("Cannot read/find input resource stream: "+resourceName);
			}
			Element argListElement = new Builder().build(is).getRootElement();
			coreLog = this.getOrCreateLog(logfileName);
			getVersionManager().readNameVersion(argListElement);
			createArgumentOptions(argListElement);
		} catch (Exception e) {
			throw new RuntimeException("Cannot read/process args file "+resourceName, e);
		}
	}

	private void createArgumentOptions(Element argElement) {
		List<Element> elementList = XMLUtil.getQueryElements(argElement, "/*/*[local-name()='arg']");
		for (Element element : elementList) {
			ArgumentOption argOption = ArgumentOption.createOption(this.getClass(), element);
			LOG.trace("created ArgumentOption: "+argOption);
			argumentOptionList.add(argOption);
		}
	}
	
	private void ensureArgumentOptionList() {
		if (this.argumentOptionList == null) {
			this.argumentOptionList = new ArrayList<ArgumentOption>();
		}
	}

	public void expandWildcardsExhaustively() {
		while (expandWildcardsOnce());
	}
	
	public boolean expandWildcardsOnce() {
		boolean change = false;
		ensureInputList();
		List<String> newInputList = new ArrayList<String>();
		for (String input : inputList) {
			List<String> expanded = expandWildcardsOnce(input);
			newInputList.addAll(expanded);
			change |= (expanded.size() > 1 || !expanded.get(0).equals(input));
		}
		inputList = newInputList;
		return change;
	}


	/** expand expressions/wildcards in input.
	 * 
	 * @param input
	 * @return
	 */
	private List<String> expandWildcardsOnce(String input) {
		Matcher matcher = GENERAL_PATTERN.matcher(input);
		List<String> inputs = new ArrayList<String>(); 
		if (matcher.find()) {
			String content = matcher.group(1);
			String pre = input.substring(0, matcher.start());
			String post = input.substring(matcher.end());
			inputs = expandIntegerMatch(content, pre, post);
			if (inputs.size() == 0) {
				inputs = expandStrings(content, pre, post);
			} 
			if (inputs.size() == 0) {
				LOG.error("Cannot expand "+content);
			}
		} else {
			inputs.add(input);
		}
		return inputs;
	}

	private List<String> expandIntegerMatch(String content, String pre, String post) {
		List<String> stringList = new ArrayList<String>();
		Matcher matcher = INTEGER_RANGE_PATTERN.matcher(content);
		if (matcher.find()) {
			int start = Integer.parseInt(matcher.group(1));
			int end = Integer.parseInt(matcher.group(2));
			for (int i = start; i <= end; i++) {
				String s = pre + i + post;
				stringList.add(s);
			}
		}
		return stringList;
	}

	private List<String> expandStrings(String content, String pre, String post) {
		List<String> newStringList = new ArrayList<String>();
		List<String> vars = Arrays.asList(content.split("\\|"));
		for (String var : vars) {
			newStringList.add(pre + var + post);
		}
		
		return newStringList;
	}
	
	public AbstractLogElement getOrCreateLog(String logfileName) {
		AbstractLogElement cMineLog = null;
		if (logfileName == null) {
			logfileName = DefaultArgProcessor.LOGFILE;
		}
		File file = new File(logfileName);
		cMineLog = new CMineLog(file);
		return cMineLog;
	}
	

	// ============ METHODS ===============

	public void parseVersion(ArgumentOption option, ArgIterator argIterator) {
		argIterator.createTokenListUpToNextNonDigitMinus(option);
		printVersion();
	}

	public void parseExtensions(ArgumentOption option, ArgIterator argIterator) {
		List<String> extensions = argIterator.createTokenListUpToNextNonDigitMinus(option);
		setExtensions(extensions);
	}

	/** obsolete name */
	@Deprecated
	public void parseQSNorma(ArgumentOption option, ArgIterator argIterator) {
		parseCTree(option, argIterator);
	}

	/** create a CTreeList from --ctree argument
	 */
	public void parseCTree(ArgumentOption option, ArgIterator argIterator) {
		List<String> cTreeNames = argIterator.createTokenListUpToNextNonDigitMinus(option);
		createCTreeListFrom(cTreeNames);
	}

	/** create a CTreeList from --ctree argument
	 * 
	 * @Deprecated // old name
	 */
	public void parseCMDir(ArgumentOption option, ArgIterator argIterator) {
		parseCTree(option, argIterator);
//		List<String> cTreeNames = argIterator.createTokenListUpToNextNonDigitMinus(option);
//		createCTreeListFrom(cTreeNames);
	}

	public void parseInput(ArgumentOption option, ArgIterator argIterator) {
		List<String> inputs = argIterator.createTokenListUpToNextNonDigitMinus(option);
		inputList = expandAllWildcards(inputs);
	}

	public void parseLogfile(ArgumentOption option, ArgIterator argIterator) {
		List<String> strings = argIterator.getStrings(option);
		logfileName = (strings.size() == 0) ? CTree.LOGFILE : strings.get(0);
	}

	public void parseOutput(ArgumentOption option, ArgIterator argIterator) {
		output = argIterator.getString(option);
	}

	public void parseProject(ArgumentOption option, ArgIterator argIterator) {
		project = argIterator.getString(option);
		createCTreeListFromProject();
	}

	public void parseRecursive(ArgumentOption option, ArgIterator argIterator) {
		recursive = argIterator.getBoolean(option);
	}

	public void parseSummaryFile(ArgumentOption option, ArgIterator argIterator) {
		summaryFileName = argIterator.getString(option);
	}

	public void parseUnzip(ArgumentOption option, ArgIterator argIterator) {
		setUnzip(true);
	}

	public void parseInclude(ArgumentOption option, ArgIterator argIterator) {
		setIncludePatternString(option, argIterator);
	}

	public void parseRename(ArgumentOption option, ArgIterator argIterator) {
		setRenamePairs(option, argIterator);
	}

	public void parseDictionary(ArgumentOption option, ArgIterator argIterator) {
		List<String> dictionarySources = argIterator.createTokenListUpToNextNonDigitMinus(option);
		createAndAddDictionaries(dictionarySources);
	}

	public void createAndAddDictionaries(List<String> dictionarySources) {
		ensureDictionaryList();
		for (String dictionarySource : dictionarySources) {
			
			InputStream is = EuclidSource.getInputStream(dictionarySource);
			if (is == null) {
				throw new RuntimeException("cannot read/create inputStream for dictionary: "+dictionarySource);
			}
			AbstractDictionary dictionary = AbstractDictionary.createDictionary(dictionarySource, is);
			if (dictionary == null) {
				throw new RuntimeException("cannot read/create dictionary: "+dictionarySource);
			}
			dictionaryList.add(dictionary);
		}
	}

	protected void ensureDictionaryList() {
		if (dictionaryList == null) {
			dictionaryList = new ArrayList<AbstractDictionary>();
		}
	}

	public void runMakeDocs(ArgumentOption option) {
		transformArgs2html();
	}

	public void runTest(ArgumentOption option) {
		String name = new Object(){}.getClass().getEnclosingMethod().getName();
		cTreeLog.info("testing");
	}

	public void outputMethod(ArgumentOption option) {
		//LOG.error("outputMethod needs overwriting");
	}

	// =====================================


	public void printHelp(ArgumentOption option, ArgIterator argIterator) {
		printHelp();
	}
	
	private void createCTreeListFrom(List<String> cTreeNames) {
		if (cTreeNames.size() == 0) {
			if (inputList == null || inputList.size() == 0) {
				LOG.error("Must give inputList before --CTree or --ctree");
			} else if (output == null) {
				LOG.error("Must give output before --CTree or --ctree");
			} else {
				finalizeInputList();
//				generateFilenamesFromInputDirectory();
				createCTreeFromOutput();
			}
		} else {
			createCTreeList(cTreeNames);
		}
	}
	
	private void setIncludePatternString(ArgumentOption option, ArgIterator argIterator) {
		List<String> includeStrings = argIterator.createTokenListUpToNextNonDigitMinus(option);
		includePatternString = includeStrings != null && includeStrings.size() == 1 ? includeStrings.get(0) : null;
	}

	private void setRenamePairs(ArgumentOption option, ArgIterator argIterator) {
		List<String> renameStrings = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (renameStrings != null && renameStrings.size() %2 == 0) {
			renamePairs = new ArrayList<List<String>>();
			for (int i = 0; i < renameStrings.size(); i += 2) {
				List<String> stringPair = new ArrayList<String>();
				stringPair.add(renameStrings.get(i));
				stringPair.add(renameStrings.get(i+1));
				renamePairs.add(stringPair);
			}
		} else {
			LOG.warn("--rename requires an even number of arguments");
		}
	}


	/** create CTrees EITHER from *.PDF/HTML/XML etc  
	 * OR from subdirectories which will be CTrees
	 * 
	 * LOGIC:
	 *  (a) if "project" exists and is a directory then assume directory children are 
	 *    already valid CTrees
	 *  (b) is "project" does NOT exist but "input" does and is directory:
	 *    list all files (files) of given extension(s) (-e foo bar) under "input" and create
	 *    project as directory, then create NEW directories under "project"
	 *    using names of "files" and creating "fulltext.foo" "fulltext.bar"
	 *  
	 *    
	 */
	private void createCTreeListFromProject() {
		if (project != null) {
			projectFile = new File(project);
			if (projectFile.isDirectory()) {
				createCTreesFromDirectories();
			} else if (projectFile.isFile()) {
				LOG.error("project file must be a directory: "+projectFile);
			} else if (!projectFile.exists()) {
				createCTreesFromInputFiles();
			} else {
				LOG.error("Unacceptable project option, probable BUG");
			}
		}
	}

	private void createCTreesFromDirectories() {
		cTreeList = new CTreeList();
		extractDirectoriesToCTrees();
		extractZipFilesToCTrees();
		LOG.trace("cTrees: "+cTreeList.size());
	}

	private void extractZipFilesToCTrees() {
		List<File> zipFiles = Arrays.asList(projectFile.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file != null && isZipFile(file);
			}}));
		for (File zipFile : zipFiles) {
			createTreeFromZipFile(projectFile, zipFile);
			CTree cTree = new CTree(zipFile);
			cTreeList.add(cTree);
		}
	}

	private void extractDirectoriesToCTrees() {
		List<File> subdirectories = Arrays.asList(projectFile.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file != null && file.isDirectory();
			}}));
		for (File subDirectory : subdirectories) {
			CTree cTree = new CTree(subDirectory);
			cTreeList.add(cTree);
		}
	}

	private void createCTreesFromInputFiles() {
		if (inputList == null || inputList.size() == 0 ) {
			LOG.error("no input files to create under project");
//		} else if (extensionList == null || extensionList.size() == 0) {
//			LOG.error("no extensions given to filter input files");
		} else {
			// don't worry about extensions?
			LOG.info(projectFile+" does not exist, creating project and populating it");
			projectFile.mkdirs();
			for (String filename : inputList) {
				File file = new File(filename);
				if (file.isDirectory()) {
					File[] files = file.listFiles();
					if (files != null) {
						for (File file0 : files) {
							createCTreeFromFilenameAndWriteReservedFile(projectFile, file0);
						}
					}
				} else if (isZipFile(file)){
					createTreeFromZipFile(projectFile, file);
				} else {
					createCTreeFromFilenameAndWriteReservedFile(projectFile, file);
				}
			}
		}
	}

	private void createTreeFromZipFile(File projectFile, File file) {
		Unzipper unZipper = new Unzipper();
		unZipper.setZipFile(file);
		unZipper.setOutDir(projectFile);
		unZipper.setIncludePatternString(includePatternString);
		try {
			unZipper.extractZip();
		} catch (IOException e) {
			throw new RuntimeException("Cannot unzip file: "+e);
		}
		zipRootName = unZipper.getZipRootName();
		if (zipRootName == null) {
			LOG.debug("No zipRoot "+file);
		} else {
			renameFiles(new File(projectFile, zipRootName));
		}
	}

	private boolean isZipFile(File file) {
		boolean isZip = false;
		if (FilenameUtils.getExtension(file.toString()).toLowerCase().equals("zip")) {
			ZipFile zf;
			try {
				zf = new ZipFile(file);
				Enumeration<? extends ZipEntry> zipEntries = zf.entries();
				isZip = zipEntries.hasMoreElements();
			} catch (ZipException ze) {
				isZip = false;
			} catch (IOException e) {
				isZip = false;
			}
		}
		return isZip;
		
	}
	
	private void renameFiles(File rootFile) {
		if (renamePairs != null && rootFile != null) {
			if (!rootFile.isDirectory()) {
				throw new RuntimeException("rootFile is not a directory: "+rootFile);
			}
			List<File> files = new ArrayList<File>(FileUtils.listFiles(rootFile, null, true));
			for (List<String> renamePair : renamePairs) {
				for (File file : files) {
					if (file.getName().matches(renamePair.get(0))) {
					    File newNameFile = new File(rootFile, renamePair.get(1));
//						LOG.debug("F0 "+file+" => "+newNameFile);
					    boolean isMoved = file.renameTo(newNameFile);
					    if (!isMoved) {
					        throw new RuntimeException("cannot rename: "+file.getName());
					    }					
					}
				}
			}
		}
	}
	
	private void createCTreeFromOutput() {
		File newCTree = output == null ? null : new File(output);
		if (newCTree != null) {
			createCTreeFromFile(newCTree);
		}
	}

	private void createCTreeFromFile(File cTree) {
		cTreeList = new CTreeList();
		for (String filename : inputList) {
			createCTreeFromFilenameAndWriteReservedFile(cTree, new File(filename));
		}
	}

//	private void createCTreeFromFilenameAndWriteReservedFile(File cTreeDir, File file) {
//		
//	}

	private void createCTreeFromFilenameAndWriteReservedFile(File cTreeDir, File infile) {
		String filename = infile.getName();
		if (!infile.isDirectory()) {
			ensureCTreeList();
//			File CTreeParent = output == null ? infile.getParentFile() : cTreeDir;
			File CTreeParent = cTreeDir == null ? infile.getParentFile() : cTreeDir;
			String cmName = createUnderscoredFilename(filename);
			File directory = new File(CTreeParent, cmName);
			CTree cTree = new CTree(directory, true);
			String reservedFilename = CTree.getCTreeReservedFilenameForExtension(filename);
			try {
				cTree.writeReservedFile(infile, reservedFilename, true);
				cTreeList.add(cTree);
			} catch (Exception e) {
				throw new RuntimeException("Cannot create/write: "+filename, e);
			}
		}
	}

	private String createUnderscoredFilename(String filename) {
		String cmName = filename.replaceAll("\\p{Punct}", "_")+"/";
		return cmName;
	}
	

	protected void printVersion() {
		DefaultArgProcessor.getVersionManager().printVersion();
	}


	private void createCTreeList(List<String> qDirectoryNames) {
		FileFilter directoryFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};

		cTreeList = new CTreeList();
		LOG.trace("creating CTreeList from: "+qDirectoryNames);
		for (String qDirectoryName : qDirectoryNames) {
			File qDirectory = new File(qDirectoryName);
			if (!qDirectory.exists()) {
				LOG.error("File does not exist: "+qDirectory.getAbsolutePath());
				continue;
			}
			if (!qDirectory.isDirectory()) {
				LOG.error("Not a directory: "+qDirectory.getAbsolutePath());
				continue;
			}
			CTree cTree = new CTree(qDirectoryName);
			LOG.trace("...creating CTree from: "+qDirectoryName);
			if (cTree.containsNoReservedFilenames() && cTree.containsNoReservedDirectories()) {
				LOG.trace("... No reserved files or directories: "+cTree);
				List<File> childFiles = new ArrayList<File>(Arrays.asList(qDirectory.listFiles(directoryFilter)));
				List<String> childFilenames = new ArrayList<String>();
				for (File childFile : childFiles) {
					if (childFile.isDirectory()) {
						childFilenames.add(childFile.toString());
					}
				}
				LOG.trace(childFilenames);
				// recurse (no mixed directory structures)
				// FIXME 
				LOG.trace("Recursing CTrees is probably  a BUG");
				createCTreeList(childFilenames);
			} else {
				cTreeList.add(cTree);
			}
		}
		LOG.trace("CTreeList: "+cTreeList.size());
		for (CTree CTree : cTreeList) {
			LOG.trace("CTree: "+CTree);
			
		}
	}


	private List<String> expandAllWildcards(List<String> inputs) {
		inputList = new ArrayList<String>();
		for (String input : inputs) {
			inputList.addAll(expandWildcards(input));
		}
		return inputList;
	}
	
	/** expand expressions/wildcards in input.
	 * 
	 * @param input
	 * @return
	 */
	private List<String> expandWildcards(String input) {
		Matcher matcher = INTEGER_RANGE.matcher(input);
		List<String> inputs = new ArrayList<String>();
		if (matcher.matches()) {
			int start = Integer.parseInt(matcher.group(2));
			int end = Integer.parseInt(matcher.group(3));
			if (start <= end) {
				for (int i = start; i <= end; i++) {
					String input0 = matcher.group(1)+i+matcher.group(4);
					inputs.add(input0);
				}
			}
		} else {
			inputs.add(input);
		}
		LOG.trace("inputs: "+inputs);
		return inputs;
	}

	private void transformArgs2html() {
		InputStream transformStream = getArgsXml2HtmlXsl();
		if (transformStream == null) {
			throw new RuntimeException("Cannot find argsXml2Html file");
		}
		List<File> argsList = getArgs2HtmlList();
		for (File argsXmlFile : argsList) {
			File argsHtmlFile = getHtmlFromXML(argsXmlFile);
			try {
				String xmlString = transformArgsXml2Html(new FileInputStream(argsXmlFile), transformStream);
				FileUtils.writeStringToFile(argsHtmlFile, xmlString, Charset.forName("UTF-8"));
			} catch (Exception e) {
				throw new RuntimeException("Cannot transform "+argsXmlFile, e);
			}
		}
	}

	private String transformArgsXml2Html(InputStream argsXmlIs, InputStream argsXml2HtmlXslIs) throws Exception {
		TransformerFactory tfactory = TransformerFactory.newInstance();
	    Transformer javaxTransformer = tfactory.newTransformer(new StreamSource(argsXml2HtmlXslIs));
		OutputStream baos = new ByteArrayOutputStream();
		javaxTransformer.transform(new StreamSource(argsXmlIs),  new StreamResult(baos));
		return baos.toString();
	}

	private InputStream getArgsXml2HtmlXsl() {
		return this.getClass().getResourceAsStream(ARGS2HTML_XSL);
	}

	private File getHtmlFromXML(File argsXml) {
		String xmlPath = argsXml.getPath();
		String htmlPath = xmlPath.replaceAll("\\.xml", ".html");
		return new File(htmlPath);
	}

	private List<File> getArgs2HtmlList() {
		List<File> argsList = new ArrayList<File>(FileUtils.listFiles(MAIN_RESOURCES, new String[]{"xml"}, true));
		for (int i = argsList.size() - 1; i >= 0; i--) {
			File file = argsList.get(i);
			if (!(ARGS_XML.equals(file.getName()))) {
				argsList.remove(i);
			}
		}
		return argsList;
	}

	// =====================================
	public void setExtensions(List<String> extensions) {
		this.extensionList = extensions;
	}

	public void setInputPatternString(String includePatternString) {
		this.includePatternString = includePatternString;
	}

	public List<String> getInputList() {
		ensureInputList();
		return inputList;
	}

	public String getString() {
		ensureInputList();
		return (inputList.size() != 1) ? null : inputList.get(0);
	}
	private void ensureInputList() {
		if (inputList == null) {
			inputList = new ArrayList<String>();
		}
	}

	public String getOutput() {
		return output;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public String getSummaryFileName() {
		return summaryFileName;
	}

	public CTreeList getCTreeList() {
		ensureCTreeList();
		return cTreeList;
	}

	protected void ensureCTreeList() {
		if (cTreeList == null) {
			cTreeList = new CTreeList();
		}
	}
	
	// --------------------------------
	
	public void parseArgs(String[] commandLineArgs) {
		if (commandLineArgs == null || commandLineArgs.length == 0) {
			printHelp();
		} else {
			String[] totalArgs = addDefaultsAndParsedArgs(commandLineArgs);
			ArgIterator argIterator = new ArgIterator(totalArgs);
			LOG.trace("args with defaults is: "+new ArrayList<String>(Arrays.asList(totalArgs)));
			while (argIterator.hasNext()) {
				String arg = argIterator.next();
				LOG.trace("arg> "+arg);
				try {
					addArgumentOptionsAndRunParseMethods(argIterator, arg);
				} catch (Exception e) {
					throw new RuntimeException("cannot process argument: "+arg+" ("+ExceptionUtils.getRootCauseMessage(e)+")", e);
				}
			}
			finalizeArgs();
		}
	}
	
	public void parseArgs(String args) {
		parseArgs(args.trim().split("\\s+"));
	}

	private void finalizeArgs() {
		processArgumentDependencies();
		finalizeInputList();
	}

	private void processArgumentDependencies() {
		for (ArgumentOption argumentOption : chosenArgumentOptionList) {
			argumentOption.processDependencies(chosenArgumentOptionList);
		}
	}

	private void finalizeInputList() {
		List<String> inputList0 = new ArrayList<String>();
		ensureInputList();
		for (String input : inputList) {
			File file = new File(input);
			if (file.isDirectory()) {
				LOG.trace("DIR: "+file.getAbsolutePath()+"; "+file.isDirectory());
				addDirectoryFiles(inputList0, file);
			} else {
				inputList0.add(input);
			}
		}
		inputList = inputList0;
	}

	private void addDirectoryFiles(List<String> inputList0, File file) {
		String[] extensions = getExtensions().toArray(new String[0]);
		List<File> files = new ArrayList<File>(
				FileUtils.listFiles(file, extensions, recursive));
		for (File file0 : files) {
			inputList0.add(file0.toString());
		}
	}

	private String[] addDefaultsAndParsedArgs(String[] commandLineArgs) {
		String[] defaultArgs = createDefaultArgumentStrings();
		List<String> totalArgList = new ArrayList<String>(Arrays.asList(createDefaultArgumentStrings()));
		List<String> commandArgList = Arrays.asList(commandLineArgs);
		totalArgList.addAll(commandArgList);
		String[] totalArgs = totalArgList.toArray(new String[0]);
		return totalArgs;
	}

	private String[] createDefaultArgumentStrings() {
		StringBuilder sb = new StringBuilder();
		for (ArgumentOption option : argumentOptionList) {
			String defalt = String.valueOf(option.getDefault());
			LOG.trace("default: "+defalt);
			if (defalt != null && defalt.toString().trim().length() > 0) {
				String command = getBriefOrVerboseCommand(option);
				sb.append(command+" "+option.getDefault()+" ");
			}
		}
		String s = sb.toString().trim();
		return s.length() == 0 ? new String[0] : s.split("\\s+");
	}

	private String getBriefOrVerboseCommand(ArgumentOption option) {
		String command = option.getBrief();
		if (command == null || command.trim().length() == 0) {
			command = option.getVerbose();
		}
		return command;
	}

	public List<String> getExtensions() {
		ensureExtensionList();
		return extensionList;
	}

	private void ensureExtensionList() {
		if (extensionList == null) {
			extensionList = new ArrayList<String>();
		}
	}
	
	public void setUnzip(boolean b) {
		this.unzip  = b;
	}

	public void runInitMethodsOnChosenArgOptions() {
		runMethodsOfType(ArgumentOption.INIT_METHOD);
	}
	
	public void runRunMethodsOnChosenArgOptions() {
		runMethodsOfType(ArgumentOption.RUN_METHOD);
	}

	public void runOutputMethodsOnChosenArgOptions() {
		runMethodsOfType(ArgumentOption.OUTPUT_METHOD);
	}

	public void runFinalMethodsOnChosenArgOptions() {
		runMethodsOfType(ArgumentOption.FINAL_METHOD);
	}

	protected void runMethodsOfType(String methodNameType) {
		List<ArgumentOption> optionList = getOptionsWithMethod(methodNameType);
		for (ArgumentOption option : optionList) {
			LOG.trace("option "+option+" "+this.getClass());
			String methodName = null;
			try {
				methodName = option.getMethodName(methodNameType);
				if (methodName != null) {
					instantiateAndRunMethod(option, methodName);
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("cannot run ["+methodName+"] in "+option.getVerbose()+
						" ("+ExceptionUtils.getRootCauseMessage(e)+")");
			}
		}
	}

	private List<ArgumentOption> getOptionsWithMethod(String methodName) {
		List<ArgumentOption> optionList0 = new ArrayList<ArgumentOption>();
		for (ArgumentOption option : chosenArgumentOptionList) {
			LOG.trace("run "+option.getRunMethodName());
			if (option.getMethodName(methodName) != null) {
				LOG.trace("added run "+option.getRunMethodName());
				optionList0.add(option);
			}
		}
		return optionList0;
	}


	protected void addArgumentOptionsAndRunParseMethods(ArgIterator argIterator, String arg) throws Exception {
		ensureChosenArgumentList();
		boolean processed = false;
		if (!arg.startsWith(MINUS)) {
			LOG.error("Parsing failed at: ("+arg+"), expected \"-\" trying to recover");
		} else {
			for (ArgumentOption option : argumentOptionList) {
				if (option.matches(arg)) {
					LOG.trace("OPTION>> "+option);
					String initMethodName = option.getInitMethodName();
					if (initMethodName != null) {
						runInitMethod1(option, initMethodName);
					}
					String parseMethodName = option.getParseMethodName();
					if (parseMethodName != null) {
						runParseMethod1(argIterator, option, parseMethodName);
					}
					processed = true;
					chosenArgumentOptionList.add(option);
					break;
				}
			}
			if (!processed) {
				LOG.error("Unknown arg: ("+arg+"), trying to recover");
			}
		}
	}

	private void runInitMethod1(ArgumentOption option, String initMethodName) {
		runMethod(null, option, initMethodName);
	}

	private void runParseMethod1(ArgIterator argIterator, ArgumentOption option, String parseMethodName) {
		runMethod(argIterator, option, parseMethodName);
	}

	private void runMethod(ArgIterator argIterator, ArgumentOption option, String methodName) {
		Method method;
		try {
			if (argIterator == null) {
				method = this.getClass().getMethod(methodName, option.getClass());
			} else {
				method = this.getClass().getMethod(methodName, option.getClass(), argIterator.getClass());
			}
		} catch (NoSuchMethodException e) {
			debugMethods();
			throw new RuntimeException("Cannot find: "+methodName+" in "+this.getClass()+"; from argument "+option.getClass()+";", e);
		}
		method.setAccessible(true);
		try {
			if (argIterator == null) {
					method.invoke(this, option);
			} else {
				method.invoke(this, option, argIterator);
			}
		} catch (Exception e) {
			LOG.trace("failed to run "+methodName+" in "+this.getClass()+"; from argument "+option.getClass()+";"+e.getCause());
//			e.printStackTrace();
			throw new RuntimeException("Cannot run: "+methodName+" in "+this.getClass()+"; from argument "+option.getClass()+";", e);
		}
	}

	private void debugMethods() {
		LOG.debug("methods for "+this.getClass());
		for (Method meth : this.getClass().getDeclaredMethods()) {
			LOG.debug(meth);
		}
	}

	private void instantiateAndRunMethod(ArgumentOption option, String methodName)
			throws IllegalAccessException, InvocationTargetException {
		if (methodName != null) {
			Method method = null;
			try {
				method = this.getClass().getMethod(methodName, option.getClass()); 
			} catch (NoSuchMethodException nsme) {
				throw new RuntimeException(methodName+"; "+this.getClass()+"; "+option.getClass()+"; \nmethod not implemented: ", nsme);
			}
			try {
				method.setAccessible(true);
 				method.invoke(this, option);
			} catch (Exception ee) {
				throw new RuntimeException("invoke "+methodName+" fails", ee);
			}
		}
	}

	private void ensureChosenArgumentList() {
		if (chosenArgumentOptionList == null) {
			chosenArgumentOptionList = new ArrayList<ArgumentOption>();
		}
	}

	protected void printHelp() {
		for (ArgumentOption option : argumentOptionList) {
			System.err.println(option.getHelp());
		}
	}
	
	public List<ArgumentOption> getChosenArgumentList() {
		ensureChosenArgumentList();
		return chosenArgumentOptionList;
	}
	
	public String createDebugString() {
		StringBuilder sb = new StringBuilder();
		getChosenArgumentList();
		for (ArgumentOption argumentOption : chosenArgumentOptionList) {
			sb.append(argumentOption.toString()+"\n");
		}
		return sb.toString();
	}

	/** MAIN CONTROL LOOP
	 * 
	 */
	public void runAndOutput() {
		ensureCTreeList();
		if (cTreeList.size() == 0) {
			if (project != null) {
				output = project;
			} else if (output != null) {
				LOG.warn("please replace --output with --project");
				project = output;
			} else {
				LOG.error("Cannot create output: --project or --output must be given");
				return;
			}
			LOG.debug("treating as CTree creation under project "+project);
			runRunMethodsOnChosenArgOptions();
		} else {
			for (int i = 0; i < cTreeList.size(); i++) {
				currentCTree = cTreeList.get(i);
				coreLog.info("running: "+currentCTree.getDirectory());
				cTreeLog = currentCTree.getOrCreateCTreeLog(this, logfileName);
				currentCTree.ensureContentProcessor(this);
				try {
					runInitMethodsOnChosenArgOptions();
					runRunMethodsOnChosenArgOptions();
					runOutputMethodsOnChosenArgOptions();
					if (cTreeLog != null) {
						cTreeLog.writeLog();
					}
				} catch (Exception e) {
					coreLog.error("error in running, terminated: "+e);
					continue;
				}
				if (i % 10 == 0) System.out.print(".");
				LOG.trace(coreLog.toXML());
			}
			
		}
		runFinalMethodsOnChosenArgOptions();
		writeLog();
	}


	private void writeLog() {
		if (coreLog != null) {
			coreLog.writeLog();
		}
	}

	protected void addVariableAndExpandReferences(String name, String value) {
		ensureVariableProcessor();
		try {
			variableProcessor.addVariableAndExpandReferences(name, value);
		} catch (Exception e) {
			LOG.error("add variable {"+name+", "+value+"} failed");
		}
	}

	public VariableProcessor ensureVariableProcessor() {
		if (variableProcessor == null) {
			variableProcessor = new VariableProcessor();
		}
		return variableProcessor;
	}

	public String getUpdate() {
		return update;
	}

	public List<AbstractDictionary> getDictionaryList() {
		return dictionaryList;
	}

	public List<? extends Element> extractPSectionElements(CTree cTree) {
		List<? extends Element> elements = null;
		if (cTree != null) {
			cTree.ensureScholarlyHtmlElement();
			elements = HtmlP.extractSelfAndDescendantIs(cTree.htmlElement);
		}
		return elements;
	}

	/** gets the HtmlElement for ScholarlyHtml.
	 * 
	 * 
	 * @return
	 */
	public static HtmlElement getScholarlyHtmlElement(CTree cTree) {
		HtmlElement htmlElement = null;
		if (cTree != null && cTree.hasScholarlyHTML()) {
			File scholarlyHtmlFile = cTree.getExistingScholarlyHTML();
			try {
				Element xml = XMLUtil.parseQuietlyToDocument(scholarlyHtmlFile).getRootElement();
				htmlElement = new HtmlFactory().parse(scholarlyHtmlFile);
			} catch (Exception e) {
				LOG.error("Cannot create scholarlyHtmlElement");
			}
		}
		return htmlElement;
	}

	// HORRIBLE, REFACTOR
	public List<? extends AbstractSearcher> getSearcherList() {
		throw new RuntimeException("Can Only be used by sublassses of DefaultArgProcessor ");
	}

}
