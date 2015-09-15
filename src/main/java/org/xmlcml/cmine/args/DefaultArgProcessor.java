package org.xmlcml.cmine.args;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.args.log.AbstractLogElement;
import org.xmlcml.cmine.args.log.CMineLog;
import org.xmlcml.cmine.files.CMDir;
import org.xmlcml.cmine.files.CMDirList;
import org.xmlcml.cmine.files.DefaultSearcher;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlFactory;
import org.xmlcml.html.HtmlP;
import org.xmlcml.xml.XMLUtil;


/** base class for all arg processing. Also contains the workflow logic:
 * 
 * the list of CMDirs is created in 
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
	
	this will generate CMDirList 
	after that 

 * 
 * 
 * runAndOutput() iterates through each CMDir
 * 
		for (int i = 0; i < cmDirList.size(); i++) {
			currentCMDir = cmDirList.get(i);
			// generateLogFile here
			currentCMDir.getOrCreateLog();
			// each CMDir has a ContentProcessor
			currentCMDir.ensureContentProcessor(this);
			// possible initFooOption
			runInitMethodsOnChosenArgOptions();
			// possible runFooOption
			runRunMethodsOnChosenArgOptions();
			// possible outputFooOptions
			runOutputMethodsOnChosenArgOptions();
		}
		// a "reduce" or "gather" method to run overe many CMDirs (e.g summaries)
		runFinalMethodsOnChosenArgOptions();
	}

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
	
	protected CMDirList cmDirList;
	// change protection later
	protected CMDir currentCMDir;
	protected String summaryFileName;
	// variable processing
	protected Map<String, String> variableByNameMap;
	private VariableProcessor variableProcessor;
	// searching
	protected List<DefaultSearcher> searcherList; // req
	protected HashMap<String, DefaultSearcher> searcherByNameMap; // req
	protected String project;
	private AbstractLogElement cTreeLog;
	private AbstractLogElement coreLog;
	
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
	public void parseQSNorma(ArgumentOption option, ArgIterator argIterator) {
		parseCMDir(option, argIterator);
	}

	public void parseCMDir(ArgumentOption option, ArgIterator argIterator) {
		List<String> cmDirNames = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (cmDirNames.size() == 0) {
			if (inputList == null || inputList.size() == 0) {
				LOG.error("Must give inputList before --cmdir");
			} else if (output == null) {
				LOG.error("Must give output before --cmdir");
			} else {
				finalizeInputList();
//				generateFilenamesFromInputDirectory();
				createCMDirListFromInput();
			}
		} else {
			createCMDirList(cmDirNames);
		}
	}

	public void parseInput(ArgumentOption option, ArgIterator argIterator) {
		List<String> inputs = argIterator.createTokenListUpToNextNonDigitMinus(option);
		inputList = expandAllWildcards(inputs);
	}

	public void parseLogfile(ArgumentOption option, ArgIterator argIterator) {
		List<String> strings = argIterator.getStrings(option);
		logfileName = (strings.size() == 0) ? CMDir.LOGFILE : strings.get(0);
	}

	public void parseOutput(ArgumentOption option, ArgIterator argIterator) {
		output = argIterator.getString(option);
	}

	public void parseProject(ArgumentOption option, ArgIterator argIterator) {
		project = argIterator.getString(option);
		if (project != null) {
			createCMDirListFromProject();
		}
	}

	public void parseRecursive(ArgumentOption option, ArgIterator argIterator) {
		recursive = argIterator.getBoolean(option);
	}

	public void parseSummaryFile(ArgumentOption option, ArgIterator argIterator) {
		summaryFileName = argIterator.getString(option);
	}

	public void runMakeDocs(ArgumentOption option) {
		transformArgs2html();
	}

	public void runTest(ArgumentOption option) {
		String name = new Object(){}.getClass().getEnclosingMethod().getName();
		cTreeLog.info("testing");
	}

	public void outputMethod(ArgumentOption option) {
		LOG.error("outputMethod needs overwriting");
	}

	// =====================================


	public void printHelp(ArgumentOption option, ArgIterator argIterator) {
		printHelp();
	}
	
	private void createCMDirListFromProject() {
		if (project != null) {
			File projectFile = new File(project);
			if (!projectFile.exists() || projectFile.isFile()) {
				LOG.error("project directory: "+project+" does not exist or is not a directory");
			} else {
				cmDirList = new CMDirList();
				List<File> subdirectories = Arrays.asList(projectFile.listFiles(new FileFilter() {
					public boolean accept(File file) {
						return file != null && file.isDirectory();
					}}));
				for (File subDirectory : subdirectories) {
					CMDir cmdir = new CMDir(subDirectory);
					cmDirList.add(cmdir);
				}
				LOG.trace("cmdirs: "+cmDirList.size());
			}
		}
	}

	private void createCMDirListFromInput() {
		File outputDir = output == null ? null : new File(output);
		cmDirList = new CMDirList();
		for (String filename : inputList) {
			File infile = new File(filename);
			if (!infile.isDirectory()) {
				File cmdirParent = output == null ? infile.getParentFile() : outputDir;
				String cmName = filename.replaceAll("\\p{Punct}", "_")+"/";
				File directory = new File(cmdirParent, cmName);
				CMDir cmDir = new CMDir(directory, true);
				String reservedFilename = CMDir.getCMDirReservedFilenameForExtension(filename);
				try {
					cmDir.writeReservedFile(infile, reservedFilename, true);
					cmDirList.add(cmDir);
				} catch (Exception e) {
					throw new RuntimeException("Cannot create/write: "+filename, e);
				}
			}
		}
	}
	

	protected void printVersion() {
		DefaultArgProcessor.getVersionManager().printVersion();
	}


	private void createCMDirList(List<String> qDirectoryNames) {
		FileFilter directoryFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};

		cmDirList = new CMDirList();
		LOG.trace("creating CMDIRList from: "+qDirectoryNames);
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
			CMDir cmDir = new CMDir(qDirectoryName);
			LOG.trace("...creating CMDIR from: "+qDirectoryName);
			if (cmDir.containsNoReservedFilenames() && cmDir.containsNoReservedDirectories()) {
				LOG.debug("... No reserved files or directories: "+cmDir);
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
				LOG.trace("Recursing CMDIRs is probably  a BUG");
				createCMDirList(childFilenames);
			} else {
				cmDirList.add(cmDir);
			}
		}
		LOG.trace("CMDIRList: "+cmDirList.size());
		for (CMDir cmdir : cmDirList) {
			LOG.trace("CMDir: "+cmdir);
			
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
				FileUtils.writeStringToFile(argsHtmlFile, xmlString);
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

	public CMDirList getCMDirList() {
		ensureCMDirList();
		return cmDirList;
	}

	protected void ensureCMDirList() {
		if (cmDirList == null) {
			cmDirList = new CMDirList();
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
				throw new RuntimeException(methodName+"; "+this.getClass()+"; "+option.getClass()+"; \nContact Norma developers: ", nsme);
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
		ensureCMDirList();
		if (cmDirList.size() == 0) {
			if (project != null) {
				output = project;
			} else if (output != null) {
				LOG.warn("please replace --output with --project");
				project = output;
			} else {
				LOG.error("Cannot create output: --project or --output must be given");
				return;
			}
			LOG.debug("treating as CMDir creation under project "+project);
			runRunMethodsOnChosenArgOptions();
		} else {
			for (int i = 0; i < cmDirList.size(); i++) {
				currentCMDir = cmDirList.get(i);
				coreLog.info("running: "+currentCMDir.getDirectory());
				cTreeLog = currentCMDir.getOrCreateCTreeLog(this, logfileName);
				currentCMDir.ensureContentProcessor(this);
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

	protected void ensureSearcherList() {
		if (searcherList == null) {
			searcherList = new ArrayList<DefaultSearcher>();
		}
	}
	
	public List<DefaultSearcher> getSearcherList() {
		return searcherList;
	}

	public List<? extends Element> extractPSectionElements(CMDir cmDir) {
		List<? extends Element> elements = null;
		if (cmDir != null) {
			cmDir.ensureScholarlyHtmlElement();
			elements = HtmlP.extractSelfAndDescendantIs(cmDir.htmlElement);
		}
		return elements;
	}

	/** gets the HtmlElement for ScholarlyHtml.
	 * 
	 * 
	 * @return
	 */
	public static HtmlElement getScholarlyHtmlElement(CMDir cmDir) {
		HtmlElement htmlElement = null;
		if (cmDir != null && cmDir.hasScholarlyHTML()) {
			File scholarlyHtmlFile = cmDir.getExistingScholarlyHTML();
			try {
				Element xml = XMLUtil.parseQuietlyToDocument(scholarlyHtmlFile).getRootElement();
				htmlElement = new HtmlFactory().parse(scholarlyHtmlFile);
			} catch (Exception e) {
				LOG.error("Cannot create scholarlyHtmlElement");
			}
		}
		return htmlElement;
	}

}
