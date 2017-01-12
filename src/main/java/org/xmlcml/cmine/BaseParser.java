package org.xmlcml.cmine;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.files.ResourceLocation;
import org.xmlcml.xml.XMLUtil;

import nu.xom.Attribute;
import nu.xom.Element;

public class BaseParser {

	private static final String ARG_LIST = "argList";
	private static final String INCLUDE = "include";
	private static final String ARG = "arg";
	private static final String ATTRIBUTE = "attribute";
	private static final String ATTRIBUTES = "attributes";
	private static final String ELEMENT = "element";
	private static final String ELEMENTS = "elements";
	
	private static final Logger LOG = Logger.getLogger(BaseParser.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String ARGUMENT_PREFIX = "-";
	protected static final String ABBREV = "abbreviation";
	protected static final String EXPAND = "expand";
	protected static final String SEARCH = "search";
	private static final String NAME = "name";
	private static final String MANDATORY = "mandatory";
	private static final String DEFAULT = "default";
	private static final String TRUE = "true";
	public static final String SYMBOL = "symbol";
	public static final String SYMBOLS_XML = "/org/xmlcml/cmine/args/symbols.xml";

	protected Queue<String> argQueue;
	public BaseCommandElement baseElement;
	protected Map<String, List<String>> argumentsByAbbreviation;
	private List<String> argList;
	private String argsResource;
	private Element argsXMLElement;
	private String argsResourceName;
	protected Map<String, ArgumentDefinition> argumentDefinitionMap;
	private Element argAttributeDefinitionsElement;
	private Map<String, Element> argAttributeDefinitionMap;
	private Element argElementDefinitionsElement;
	private Map<String, Element> argElementDefinitionMap;


	public BaseParser() {
		init();
	}
	
	private void init() {
		argQueue = new LinkedList<String>();
		readArgsXML();
	}
	
	protected void readArgsXML() {
		InputStream resource = getArgsResource();
		argsXMLElement = XMLUtil.parseQuietlyToDocument(resource).getRootElement();
		if (!argsXMLElement.getLocalName().equals(ARG_LIST)) {
			throw new RuntimeException("root must be '"+ARG_LIST+"'");
		}
		LOG.debug("args "+argsXMLElement.toXML().substring(0,  100)+" ...");
		processInclude();
		createArgAttributeDefinitions();
		createArgElementDefinitions();
		argumentDefinitionMap = getOrCreateArgumentDefinitionMap();
		LOG.debug("args "+argumentDefinitionMap);
		
	}

	private void processInclude() {
		String includeParserName = argsXMLElement.getAttributeValue(INCLUDE);
		if (includeParserName != null) {
			try {
				Class parserClass = this.getClass().forName(includeParserName);
				BaseParser parser = (BaseParser) parserClass.newInstance();
				parser.readArgsXML();
			} catch (Exception e) {
				throw new RuntimeException("Cannot create class object "+includeParserName, e);
			}
		}
	}

	/** defines the attributes allowed for an arg
	 * 
	 * defined in <defaults> in args.xml
	 * 
	 */
	private void createArgAttributeDefinitions() {
		ensureArgAttributeDefinitionMap();
		argAttributeDefinitionsElement = XMLUtil.getSingleElement(argsXMLElement, "./*[local-name()='"+ATTRIBUTES+"']");
		List<Element> argAttributeDefinitions = XMLUtil.getQueryElements(argAttributeDefinitionsElement, "./*[local-name()='"+ATTRIBUTE+"']");
		for (Element argAttributeDefinition : argAttributeDefinitions) {
			String name = argAttributeDefinition.getAttributeValue(NAME);
			argAttributeDefinitionMap.put(name, argAttributeDefinition);
//			LOG.debug("name "+name);
		}
		LOG.debug("argAttributeDefs: "+argAttributeDefinitionMap);
	}

	private void ensureArgAttributeDefinitionMap() {
		if (argAttributeDefinitionMap == null) {
			argAttributeDefinitionMap = new HashMap<String, Element>();
		}
	}

	/** defines the elements allowed for an arg
	 * 
	 * defined in <defaults> in args.xml
	 * 
	 */
	private void createArgElementDefinitions() {
		ensureArgElementDefinitionMap();
		argElementDefinitionsElement = XMLUtil.getSingleElement(argsXMLElement, "./*[local-name()='"+ELEMENTS+"']");
		List<Element> argElementDefinitions = XMLUtil.getQueryElements(argElementDefinitionsElement, "./*[local-name()='"+ELEMENT+"']");
		for (Element argElementDefinition : argElementDefinitions) {
			String name = argElementDefinition.getAttributeValue(NAME);
			argElementDefinitionMap.put(name, argElementDefinition);
		}
		LOG.debug("argElementDefs: "+argElementDefinitionMap);
	}

	private void ensureArgElementDefinitionMap() {
		if (argElementDefinitionMap == null) {
			argElementDefinitionMap = new HashMap<String, Element>();
		}
	}

	private Map<String, ArgumentDefinition> getOrCreateArgumentDefinitionMap() {
		if (argumentDefinitionMap == null) {
			argumentDefinitionMap = new HashMap<String, ArgumentDefinition>();
		}
		LOG.debug("args1 "+argsXMLElement.toXML());
		List<Element> argumentDefinitionElements = XMLUtil.getQueryElements(argsXMLElement, "./*[local-name()='"+ARG+"']");
		LOG.debug("args11 "+argumentDefinitionElements);
		for (Element argDefElement : argumentDefinitionElements) {
			ArgumentDefinition argumentDefinition = ArgumentDefinition.createArgumentDefinition(argDefElement);
			checkAgainstArgAttributes(argDefElement);
			checkAgainstArgElements(argDefElement);
			String name = argumentDefinition.getName();
			if (name == null) {
				throw new RuntimeException("all argumentDefinitions must have name");
			}
			argumentDefinitionMap.put(name, argumentDefinition);
		}
		return argumentDefinitionMap;
	}

	private void checkAgainstArgAttributes(Element argDefElement) {
		for (int i = 0; i < argDefElement.getAttributeCount(); i++) {
			Attribute argDefAtt = argDefElement.getAttribute(i);
			String argDefAttName = argDefAtt.getLocalName();
//			LOG.debug("argDefAttribute: "+argDefAttName);
			Element argAttributeElement = argAttributeDefinitionMap.get(argDefAttName);
			if (argAttributeElement == null) {
				LOG.warn("unknown attribute on ang: "+argDefAttName);
			} else {
				checkValueOfArgDefAttribute(argDefAtt, argAttributeElement);
			}
		}
		checkMandatoryAttributes(argDefElement);
		addDefaultsToArgs(argDefElement);
	}

	private void addDefaultsToArgs(Element argDefElement) {
		List<Element> defaultAttributeElements = XMLUtil.getQueryElements(argAttributeDefinitionsElement, "./*[@"+DEFAULT+"]");
		for (Element defaultAttributeElement : defaultAttributeElements) {
			Attribute defaultAttribute = defaultAttributeElement.getAttribute(NAME);
			String defAttValue = defaultAttribute.getValue();
			Attribute requiredAttribute = argDefElement.getAttribute(defAttValue);
			if (requiredAttribute == null) {
				String defValue = defaultAttributeElement.getAttributeValue(DEFAULT);
				argDefElement.addAttribute(new Attribute(defAttValue, defValue));
			} else {
				LOG.trace("already "+requiredAttribute);
			}
		}
	}

	private void checkAgainstArgElements(Element argDefElement) {
		for (int i = 0; i < argDefElement.getChildElements().size(); i++) {
			Element argDefChild = argDefElement.getChildElements().get(i);
			String argDefChildName = argDefChild.getLocalName();
//			LOG.debug("argDefChild: "+argDefChildName);
			Element argElementElement = argElementDefinitionMap.get(argDefChildName);
			if (argElementElement == null) {
				LOG.warn("unknown child of arg: "+argDefChildName);
			} else {
				checkArgDefChild(argDefChild, argElementElement);
			}
		}
		checkMandatoryChildren(argDefElement);
	}

	private void checkMandatoryAttributes(Element argDefElement) {
		List<Element> mandatoryAttributes = XMLUtil.getQueryElements(argAttributeDefinitionsElement, "./*[@"+MANDATORY+"='"+TRUE+"']");
		for (Element mandatoryAttribute : mandatoryAttributes) {
			String name = mandatoryAttribute.getAttributeValue(NAME);
			Attribute requiredAttribute = argDefElement.getAttribute(name);
			if (requiredAttribute == null) {
				LOG.warn("required attribute missing on argDef: "+name);
			}
		}
	}

	private void checkMandatoryChildren(Element argDefElement) {
		List<Element> mandatoryChildren = XMLUtil.getQueryElements(argElementDefinitionsElement, "./*[@"+MANDATORY+"='"+TRUE+"']");
//		LOG.debug("mandatory children"+mandatoryChildren);
		for (Element mandatoryChild : mandatoryChildren) {
			String name = mandatoryChild.getAttributeValue(NAME);
//			LOG.debug("mandatory name: "+name);
			Element requiredChild = XMLUtil.getSingleElement(argDefElement, "./*[local-name()='"+name+"']");
			if (requiredChild == null) {
				LOG.warn("required child missing on argDef: "+name);
			}
		}
	}

	/** checks to see if argDef is consistent with lossible and mandatory attributes.
	 * 
	 * @param argDefElement
	 * @param argAttributeElement
	 */
	private void checkValueOfArgDefAttribute(Attribute argDefAttribute, Element argAttributeElement) {
		String attName = argDefAttribute.getLocalName();
		String value = argDefAttribute.getValue();
		LOG.trace("check value NYI");
	}

	/** checks to see if argDef is consistent with lossible and mandatory attributes.
	 * 
	 * @param argDefElement
	 * @param argAttributeElement
	 */
	private void checkArgDefChild(Element argDefChild, Element argAttributeElement) {
//		String attName = argDefAttribute.getLocalName();
//		String value = argDefAttribute.getValue();
	}

	private InputStream getArgsResource() {
		argsResource = getArgsXMLResourceName();
		LOG.debug("args "+argsResource);
		return this.getClass().getResourceAsStream(argsResource);
	}

	protected String getArgsXMLResourceName() {
		Class clazz = this.getClass();
		LOG.debug("clazz "+clazz);
		argsResourceName = clazz.getPackage().getName()+"."+"args";
		argsResourceName = argsResourceName.replaceAll("\\.",  "/");
		argsResourceName = "/"+argsResourceName+"/"+"args.xml";
		LOG.debug("args "+argsResourceName);
		return argsResourceName;
	}

	public static BaseCommandElement createCommandElement(String className) {
		BaseCommandElement emmaElement = null;
		if (className == null) {
			LOG.warn("null className");
		} else {
			try {
				Class clazz = BaseParser.class.forName(className);
				if (clazz == null) {
					throw new RuntimeException("cannot create class: "+clazz);
				}
				if (!BaseCommandElement.class.isAssignableFrom(clazz)) {
					throw new RuntimeException(clazz+" is not a subclass of BaseCommandElement");
				}
				emmaElement = (BaseCommandElement) clazz.newInstance();
				if (emmaElement == null) {
					LOG.warn("cannot create object of class: "+clazz);
				}
			} catch (Exception e) {
				throw new RuntimeException("cannot create class: "+className, e);				
			}
		}
		if (emmaElement == null) {
			emmaElement = new BaseCommandElement();
		}
		return emmaElement;
	}

	protected Argument readArgument() {
		String token = argQueue.peek();
		if (!token.startsWith(ARGUMENT_PREFIX)) {
			return null;
		}
		if (token.equals(ARGUMENT_PREFIX)) {
			throw new RuntimeException("Argument "+ARGUMENT_PREFIX+" not yet supported");
		}
		if (token.equals(BaseCommandElement.COMMAND_SEPARATOR)) {
			return null;
		}
		Argument argument = new Argument(token);
		argQueue.remove();
		while(!argQueue.isEmpty()) {
			token = argQueue.peek();
			if (token == null || token.startsWith(ARGUMENT_PREFIX)) {
				break; 
			}
			argument.add(argQueue.remove());
		}
		LOG.trace("read argument: "+argument);
		return argument;
	}

	protected void parseArgs() {
		if (argQueue.isEmpty()) {
			throw new RuntimeException("No args given");
		}
		List<Argument> argumentList = readArguments();
		baseElement.setArgumentList(argumentList);
		if (!argQueue.isEmpty()) {
			readCommands();
		}
	}

	public void readCommands() {
		while (!argQueue.isEmpty()) {
			String token = argQueue.peek();
			if (token.equals(BaseCommandElement.COMMAND_SEPARATOR)) {
				argQueue.poll();
				BaseCommandElement command = readCommand();
				baseElement.appendChild(command);
			} else {
				throw new RuntimeException("Unexpected token; expecting command or EOI: "+token);
			}
		}
	}

	public List<Argument> readArguments() {
		List<Argument> argumentList = new ArrayList<Argument>();
		while (!argQueue.isEmpty()) {
			Argument argument = readArgument();
			if (argument == null) {
				break;
			}
			argumentList.add(argument);
		}
		return argumentList;
	}

	public BaseCommandElement readCommand() {
		String commandToken = argQueue.remove();
		String commandElementClassName = createCommandElementClassName(commandToken);
		BaseCommandElement commandElement = BaseParser.createCommandElement(commandElementClassName);
		List<Argument> argumentList = readArguments();
		commandElement.setArgumentList(argumentList);
		return commandElement;
	}

	protected String createCommandElementClassName(String commandToken) {
		return this.getClass().getPackage().getName()+"."+"BaseCommandElement";
	}

	public BaseCommandElement parseArgs(String args) {
		List<String> argList = args == null ? new ArrayList<String>() : Arrays.asList(args.trim().split("\\s+"));
		return parseArgs(argList);
	}

	public BaseCommandElement parseArgs(String[] args) {
		return parseArgs(Arrays.asList(args));
	}

	public BaseCommandElement parseArgs(List<String> args) {
		baseElement = new BaseCommandElement();
		this.argList = substituteSymbols(args);
		this.argQueue.addAll(argList);
		parseArgs();
		return baseElement;
	}

	/** examines all args and expands where possible.
	 * 
	 * @param argList
	 * @return
	 */
	public List<String> substituteSymbols(List<String> argList) {
		addSymbolsToExpansionMap();
		List<String> newArgs = new ArrayList<String>();
		for (String arg : argList) {
			newArgs.addAll(addArgOrExpandedArgs(arg));
		}
		return newArgs;
	}

	/** createds a new list with symbols replaced by expansion else original symbols.
	 * 
	 * @param arg
	 * @return
	 */
	protected List<String> addArgOrExpandedArgs(String arg) {
		List<String> newArgs =	new ArrayList<String>();
		List<String> expandedArgs = argumentsByAbbreviation.get(arg);
		if (expandedArgs != null) {
			newArgs.addAll(expandedArgs);
		} else {
			newArgs.add(arg);
		}
		return newArgs;
	}

	protected void addSymbolsToExpansionMap() {
		if (argumentsByAbbreviation == null) {
			argumentsByAbbreviation = new HashMap<String, List<String>>();
		}
		readSymbolsAndAddToMap();
	}

	protected void readSymbolsAndAddToMap() {
		String symbolFileLocation = getSymbolFileLocation();
		LOG.debug("symbol file: "+symbolFileLocation);
		List<Element> symbolList = createSubstitutionSymbolList(symbolFileLocation);
		for (Element symbol : symbolList) {
			addSymbolToExpansionMap(symbol);
		}
	}

	protected String getSymbolFileLocation() {
		String symbolFileLocation = this.getClass().getName();
		LOG.debug("SF "+symbolFileLocation);
		List<String> chunks = Arrays.asList(symbolFileLocation.split("\\."));
		List<String> chunks1 = new ArrayList<String>();
		for (int i = 0; i < chunks.size() - 1; i++) {
			chunks1.add(chunks.get(i));
		}
		symbolFileLocation = "/"+StringUtils.join(chunks1, "/");
		symbolFileLocation += "/"+"args.xml";
		LOG.debug("symbolFile: "+symbolFileLocation);
		return symbolFileLocation;
	}

	private void addSymbolToExpansionMap(Element symbol) {
		String abbrev = symbol.getAttributeValue(ABBREV);
		String expand = symbol.getAttributeValue(EXPAND);
		argumentsByAbbreviation.put(abbrev, Arrays.asList(expand.split("\\s+")));
	}

	private List<Element> createSubstitutionSymbolList(String symbolFileLocation) {
		ResourceLocation location = new ResourceLocation();
		InputStream is = location.getInputStreamHeuristically(symbolFileLocation);
		if (is == null) {
			throw new RuntimeException("Cannot read/find stream: "+symbolFileLocation);
		}
		Element symbols = XMLUtil.parseQuietlyToDocument(is).getRootElement();
		List<Element> symbolList = XMLUtil.getQueryElements(symbols, SYMBOL);
		return symbolList;
	}

	public List<String> getArgList() {
		return argList;
	}

	public BaseCommandElement getBaseElement() {
		return baseElement;
	}

}
