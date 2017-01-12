package org.xmlcml.cmine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.args.ArgumentOption;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.args.StringPair;
import org.xmlcml.cmine.args.ValueElement;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.RealRange;

import nu.xom.Element;

/**
 * reads argument defintion from args.xml and validates and transforms actual arguments
 * 
 * @author pm286
 *
 */
public class ArgumentDefinition {

	private static final String ARG = "arg";
	private static final String BRIEF = "brief";
	private static final String LONG = "long";
	public static final String NAME = "name";
	private static final String HELP = "help";
	public static final String VALUE = "value";
	private static final String ARGS = "args";
	private static final String CLASS_TYPE = "class";
	private static final String DEFAULT = "default";
	private static final String COUNT_RANGE = "countRange";
	private static final String VALUE_RANGE = "valueRange";
	static final String FINAL_METHOD = "finalMethod";
	static final String INIT_METHOD = "initMethod";
	static final String OUTPUT_METHOD = "outputMethod";
	static final String PARSE_METHOD = "parseMethod";
	static final String RUN_METHOD = "runMethod";

	List<String> attributeNames = Arrays.asList(new String[] {
			
	});
	// these may be obsolete
	private static final String FORBIDDEN = "forbidden";
	private static final String REQUIRED = "required";
	
	private static final String PATTERN = "pattern";
	
	private static final Pattern INT_RANGE = Pattern.compile("\\{(\\*|\\-?\\d+),(\\-?\\d*|\\*)\\}");
	private static final Pattern DOUBLE_RANGE = Pattern.compile("\\{(\\-?\\+?\\d+\\.?\\d*|\\*),(\\-?\\+?\\d+\\.?\\d*|\\*)\\}");

	private static final Logger LOG = Logger.getLogger(ArgumentOption.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static Set<String> MANDATORY_ATTRIBUTES;
	static {
		MANDATORY_ATTRIBUTES = new HashSet<String>();
		MANDATORY_ATTRIBUTES.add(NAME);
		MANDATORY_ATTRIBUTES.add(LONG);
		MANDATORY_ATTRIBUTES.add(ARGS);
	}
	private static Set<String> MANDATORY_CHILDREN;
	static {
		MANDATORY_CHILDREN = new HashSet<String>();
		MANDATORY_CHILDREN.add(HELP);
	}
	private static Map<String, String> OPTIONAL_ATTRIBUTES;
	static {
		OPTIONAL_ATTRIBUTES = new HashMap<String, String>();
		OPTIONAL_ATTRIBUTES.put(CLASS_TYPE, "java.lang.String"); // class defaults to String
		OPTIONAL_ATTRIBUTES.put(DEFAULT, "");                    // default defaults to ""
	}
	
	private String name;
	private String brief;
	private String verbose;
	private String help;
	private Class<?> classType;
	private Object defalt;
	private IntRange countRange;
	private String countRangeString;
	private IntRange intValueRange = null;
	private RealRange realValueRange = null;
	private String valueRangeString;
	private String patternString = null;
	private Pattern pattern = null;
	private String forbiddenString = null;
	private List<String> forbiddenArguments = null;
	private String requiredString = null;
	private List<String> requiredArguments = null;
	
	private List<String> defaultStrings;
	private List<Integer> defaultIntegers;
	private List<Double> defaultDoubles;

	private List<String> stringValues;
	private List<Double> doubleValues;
	private List<Integer> integerValues;

	private Double defaultDouble;
	private String defaultString;
	private Integer defaultInteger;
	private Boolean defaultBoolean;
	
	private String  stringValue;
	private Integer integerValue;
	private Double  doubleValue;
	private Boolean booleanValue;
	private String args;
	private List<StringPair> stringPairValues;
	
	private String parseMethodName;
	private String runMethodName;
	private String outputMethodName;
	private String finalMethodName;
	private String initMethodName;
	
	private Class<? extends DefaultArgProcessor> argProcessorClass;
	private List<ValueElement> valueElements;
	private List<Element> helpNodes;
	private Element element;

	

	private Element argDefElement;
	
//	private void setValue(String namex, String value) {
//		if (BRIEF.equals(namex)) {
//			this.setBrief(value);
//		} else if (LONG.equals(namex)) {
//			this.setLong(value);
//		} else if (NAME.equals(namex)) {
//			this.setName(value);
//		} else if (HELP.equals(namex)) {
//			this.setHelp(value);
//		} else if (ARGS.equals(namex)) {
//			this.setArgs(value);
//		} else if (CLASS_TYPE.equals(namex)) {
//			this.setClassType(value);
//		} else if (DEFAULT.equals(namex)) {
//			this.setDefault(value);
//		} else if (COUNT_RANGE.equals(namex)) {
//			this.setCountRange(value);
//		} else if (FORBIDDEN.equals(namex)) {
//			this.setForbiddenString(value);
//		} else if (REQUIRED.equals(namex)) {
//			this.setRequiredString(value);
//		} else if (FINAL_METHOD.equals(namex)) {
//			this.setFinalMethod(value);
//		} else if (INIT_METHOD.equals(namex)) {
//			this.setInitMethod(value);
//		} else if (OUTPUT_METHOD.equals(namex)) {
//			this.setOutputMethod(value);
//		} else if (PARSE_METHOD.equals(namex)) {
//			this.setParseMethod(value);
//		} else if (PATTERN.equals(namex)) {
//			this.setPatternString(value);
//		} else if (RUN_METHOD.equals(namex)) {
//			this.setRunMethod(value);
//		} else if (VALUE_RANGE.equals(namex)) {
//			this.setValueRange(value);
//		} else {
//			throw new RuntimeException("Unknown attribute on <arg name='"+name+"'>: "+namex+"='"+value+"'");
//		}


	public ArgumentDefinition(Element argDefElement) {
		this.argDefElement = argDefElement;
		if (!ARG.contentEquals(argDefElement.getLocalName())) {
			throw new RuntimeException("args must have local name "+ARG);
		}
		name = getName();
		
	}

	public static ArgumentDefinition createArgumentDefinition(Element argDefElement) {
		ArgumentDefinition argumentDefinition = null;
		if (argDefElement != null) {
			argumentDefinition = new ArgumentDefinition(argDefElement);
		}
		return argumentDefinition;
	}


	public String getName() {
		return argDefElement.getAttributeValue(NAME);
	}
	
	public String toString() {
		String s = "argDef: ";
		s += getName();
		return s;
	}

}
