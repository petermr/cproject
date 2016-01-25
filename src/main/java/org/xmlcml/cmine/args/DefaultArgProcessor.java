package org.xmlcml.cmine.args;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.args.log.AbstractLogElement;
import org.xmlcml.cmine.args.log.CMineLog;
import org.xmlcml.cmine.files.AbstractSearcher;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.cmine.files.CTree;
import org.xmlcml.cmine.files.CTreeFiles;
import org.xmlcml.cmine.files.CTreeList;
import org.xmlcml.cmine.files.EuclidSource;
import org.xmlcml.cmine.files.ProjectFilesTree;
import org.xmlcml.cmine.files.ProjectSnippetsTree;
import org.xmlcml.cmine.files.SnippetsTree;
import org.xmlcml.cmine.lookup.DefaultStringDictionary;
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
		// a "reduce" or "gather" method to run over many CTrees (e.g summaries)
		runFinalMethodsOnChosenArgOptions();
		// includes the finalAnalysis (--analyze) 
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
	private static String RESOURCE_NAME_TOP = "/org/xmlcml/cmine/args";
	protected static final String ARGS_XML = "args.xml";
	private static String ARGS_RESOURCE = RESOURCE_NAME_TOP+"/"+ARGS_XML;
	private static final String NAME = "name";
	private static final String VERSION = "version";
	
	public static final String WHITESPACE = "\\s+";
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
	public List<String> inputList;
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
	private AbstractLogElement cTreeLog;
	private AbstractLogElement coreLog;
	private boolean unzip = false;
	List<List<String>> renamePairs;
	protected List<DefaultStringDictionary> dictionaryList;
	private String analysisExpression;
	private File outputFile;
	CProject cProject;
	
	private ProjectSnippetsTree projectSnippetsTree;
	private ProjectFilesTree projectFilesTree;
	private ProjectAndTreeFactory projectAndTreeFactory;
	protected String projectDirString;
	private String includePatternString;
	private ArgumentExpander argumentExpander;
	private CTreeFiles cTreeFiles;
	
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
		ensureProjectAndTreeFactory();
		projectAndTreeFactory.createCTreeListFrom(cTreeNames);
	}

	/** create a CTreeList from --ctree argument
	 */ 
	 @Deprecated // old name
	public void parseCMDir(ArgumentOption option, ArgIterator argIterator) {
		parseCTree(option, argIterator);
	}

	public void parseInput(ArgumentOption option, ArgIterator argIterator) {
		List<String> inputs = argIterator.createTokenListUpToNextNonDigitMinus(option);
		inputList = ensureArgumentExpander().expandAllWildcards(inputs);
	}

	public void parseLogfile(ArgumentOption option, ArgIterator argIterator) {
		List<String> strings = argIterator.getStrings(option);
		logfileName = (strings.size() == 0) ? CTree.LOGFILE : strings.get(0);
	}

	public void parseOutput(ArgumentOption option, ArgIterator argIterator) {
		output = argIterator.getString(option);
	}

	public void parseProject(ArgumentOption option, ArgIterator argIterator) {
		projectDirString = argIterator.getString(option);
		ensureProjectAndTreeFactory().createProject();
		ensureProjectAndTreeFactory().createCTreeListFromProject();
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
	
	public void parseAnalysis(ArgumentOption option, ArgIterator argIterator) {
		List<String> analyzeStrings = argIterator.createTokenListUpToNextNonDigitMinus(option);
		setAnalysis(analyzeStrings);
	}

	public void parseDictionary(ArgumentOption option, ArgIterator argIterator) {
		List<String> dictionarySources = argIterator.createTokenListUpToNextNonDigitMinus(option);
		createAndAddDictionaries(dictionarySources);
	}

	public void runMakeDocs(ArgumentOption option) {
		transformArgs2html();
	}

	public void runAnalysis(ArgumentOption option) {
		analyzeCTree();
	}

	public void runTest(ArgumentOption option) {
		String name = new Object(){}.getClass().getEnclosingMethod().getName();
		cTreeLog.info("testing");
	}

	public void outputMethod(ArgumentOption option) {
		LOG.trace("output method not written");
	}

	public void outputAnalysis(ArgumentOption option) {
		String output = getOutput();
		if (currentCTree != null) {
			outputFile = output == null ? null : new File(currentCTree.getDirectory(), output);
			if (currentCTree.getSnippetsTree() != null) {
				outputSnippetsTree(outputFile);
			} else if (currentCTree.getCTreeFiles() != null) {
				outputCTreeFiles(outputFile);
			} else {
				LOG.debug("Analysis: No snippets or files to output");
			}
		}
	}

	public void finalAnalysis(ArgumentOption option) {
		finalAnalysisRoutine();
		LOG.trace("FINAL ANALYSIS");
	}


	private void finalAnalysisRoutine() {
		if (cProject == null) {
			LOG.debug("no project to analyze");
			return;
		}
		File directory = cProject.getDirectory();
		if (directory == null) {
			LOG.debug("no directory to analyze");
			return;
		}
		if (output == null) {
			LOG.debug("no output file given");
			return;
		}
		File outputFile = new File(directory, getOutput());
		ProjectSnippetsTree projectSnippetsTree = cProject.getProjectSnippetsTree();
		ProjectFilesTree projectFilesTree = cProject.getProjectFilesTree();
		if (projectSnippetsTree != null) {
			cProject.outputProjectSnippetsTree(outputFile);
		} else if (projectFilesTree != null) {
			cProject.outputProjectFilesTree(outputFile);
		}
	}

	private void outputSnippetsTree(File outputFile) {
		SnippetsTree snippetsTree = currentCTree.getSnippetsTree();
		if (snippetsTree != null && outputFile != null) {
			try {
				XMLUtil.debug(snippetsTree, outputFile, 1);
			} catch (IOException e) {
				throw new RuntimeException("Cannot output snippets", e);
			}
		}
	}

	private void outputCTreeFiles(File outputFile) {
		CTreeFiles cTreeFiles = currentCTree.getCTreeFiles();
		if (cTreeFiles != null && outputFile != null) {
			try {
				XMLUtil.debug(cTreeFiles, outputFile, 1);
			} catch (IOException e) {
				throw new RuntimeException("Cannot output cTreeFiles", e);
			}
		}
	}

	// =====================================


	public void printHelp(ArgumentOption option, ArgIterator argIterator) {
		printHelp();
	}
	
	private ProjectAndTreeFactory ensureProjectAndTreeFactory() {
		if (projectAndTreeFactory == null) {
			projectAndTreeFactory = new ProjectAndTreeFactory(this);
		}
		return projectAndTreeFactory;
	}
	
	private void setIncludePatternString(ArgumentOption option, ArgIterator argIterator) {
		List<String> includeStrings = argIterator.createTokenListUpToNextNonDigitMinus(option);
		includePatternString = includeStrings != null && includeStrings.size() == 1 ? includeStrings.get(0) : null;
	}

	private void setAnalysis(List<String> analyzeStrings) {
		if (analyzeStrings != null && analyzeStrings.size() == 1) {
			analysisExpression = analyzeStrings.get(0);
		} else {
			LOG.error("--analyze requires 1 expression");
		}
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

	/** this called once per CTree and write the output.
	 * 
	 */
	private void analyzeCTree() {
		FileXPathSearcher fileXPathSearcher = new FileXPathSearcher(analysisExpression);
//		String glob = fileXPathSearcher.getCurrentGlob();
		String xpath = fileXPathSearcher.getCurrentXPath();
		if (currentCTree != null) {
			fileXPathSearcher = new FileXPathSearcher(currentCTree, analysisExpression);
			fileXPathSearcher.search();
			CTreeFiles cTreeFiles = fileXPathSearcher.getCTreeFiles();
			if (cProject != null) {
				cProject.add(cTreeFiles);
			}
			if (xpath != null) {
				SnippetsTree snippetsTree = fileXPathSearcher.getSnippetsTree();
				if (cProject != null) {
					cProject.add(snippetsTree);
				}
			}
		}
	}


	public void createAndAddDictionaries(List<String> dictionarySources) {
		ensureDictionaryList();
		for (String dictionarySource : dictionarySources) {
			
			InputStream is = EuclidSource.getInputStream(dictionarySource);
			if (is == null) {
				throw new RuntimeException("cannot read/create inputStream for dictionary: "+dictionarySource);
			}
			DefaultStringDictionary dictionary = DefaultStringDictionary.createDictionary(dictionarySource, is);
			if (dictionary == null) {
				throw new RuntimeException("cannot read/create dictionary: "+dictionarySource);
			}
			dictionaryList.add(dictionary);
		}
	}

	protected void ensureDictionaryList() {
		if (dictionaryList == null) {
			dictionaryList = new ArrayList<DefaultStringDictionary>();
		}
	}


	protected void printVersion() {
		DefaultArgProcessor.getVersionManager().printVersion();
	}


	private void transformArgs2html() {
		InputStream transformStream = this.getClass().getResourceAsStream(ARGS2HTML_XSL);
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
	void ensureInputList() {
		if (inputList == null) {
			inputList = new ArrayList<String>();
		}
	}

	public String getOutput() {
		return output;
	}

	public File getOutputFile() {
		return outputFile;
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

	void finalizeInputList() {
		List<String> inputList0 = new ArrayList<String>();
		ensureInputList();
		for (String input : inputList) {
			File file = new File(input);
			if (file.isDirectory()) {
				LOG.trace("DIR: "+file.getAbsolutePath()+"; isDir "+file.isDirectory()+"; Exist "+file.exists()+"; "+file.getAbsolutePath());
				addDirectoryFiles(inputList0, file);
			} else {
				inputList0.add(input);
			}
		}
		inputList = inputList0;
		Collections.sort(inputList);
		LOG.trace("sorted: "+inputList);
	}

	/** will return a sorted list
	 * 
	 * @param inputList0
	 * @param file
	 */
	private void addDirectoryFiles(List<String> inputList0, File file) {
		String[] extensions = getExtensions().toArray(new String[0]);
		List<File> files = new ArrayList<File>(
				FileUtils.listFiles(file, extensions, recursive));
		for (File file0 : files) {
			inputList0.add(file0.toString());
		}
		Collections.sort(inputList0);
		LOG.trace("sort: "+inputList0);
	}

	private String[] addDefaultsAndParsedArgs(String[] commandLineArgs) {
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
			if (projectDirString != null) {
				output = projectDirString;
			} else if (output != null) {
				LOG.warn("please replace --output with --project");
				projectDirString = output;
			} else {
				LOG.error("Cannot create output: --project or --output must be given");
				return;
			}
			LOG.trace("treating as CTree creation under project "+projectDirString);
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

	public ArgumentExpander ensureArgumentExpander() {
		if (this.argumentExpander == null) {
			argumentExpander = new ArgumentExpander(this);
		}
		return argumentExpander;
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

	public List<DefaultStringDictionary> getDictionaryList() {
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

	public CTree getCTree() {
		return currentCTree != null ? currentCTree : 
			(cTreeList != null && cTreeList.size() == 1) ? cTreeList.get(0) : null;
	}
	
	public CProject getCProject() {
		return cProject;
	}

	public ProjectSnippetsTree getProjectSnippetsTree() {
		projectSnippetsTree = cProject == null ? null : cProject.getProjectSnippetsTree();
		return projectSnippetsTree;
	}

	public ProjectFilesTree getProjectFilesTree() {
		projectFilesTree = cProject == null ? null : cProject.getProjectFilesTree();
		return projectFilesTree;
	}

	public String getProjectDirString() {
		return projectDirString;
	}

	public String getIncludePatternString() {
		return includePatternString;
	}

	public void expandWildcardsExhaustively() {
		ensureArgumentExpander().expandWildcardsExhaustively();
	}

}
