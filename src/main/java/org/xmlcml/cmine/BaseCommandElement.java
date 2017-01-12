package org.xmlcml.cmine;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.xml.XMLUtil;

import nu.xom.Attribute;
import nu.xom.Element;

public /*abstract*/ class BaseCommandElement extends Element{

	private static final Logger LOG = Logger.getLogger(BaseCommandElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	protected static final String NAME = "_name";
	public static final String TYPE = "type";
	public static final String COMMAND_SEPARATOR = "-c";
	protected static final String PROJECT = "project";
	protected static final String TAG = "command";

	protected BaseCommandElement() {
		super(TAG);
	}
	
	protected BaseCommandElement(String tag) {
		super(tag);
	}
	
	public void runCommand() {
		parseArguments();
		addArgumentDefaults();
		checkArguments();
	}

	private void parseArguments() {
		LOG.warn("parseArguments NYI");
	}

	private void addArgumentDefaults() {
		LOG.warn("addArgumentDefaults NYI");
	}

	private void checkArguments() {
		LOG.warn("checkArguments NYI");
	}

	public List<Argument> getArgumentList() {
		List<Argument> argumentList = new ArrayList<Argument>();
		for (int i = 0; i < this.getAttributeCount(); i++) {
			Attribute attribute = this.getAttribute(i);
			Argument argument = Argument.createArgument(attribute);
			argumentList.add(argument);
		}
		return argumentList;
	}
	
	public void setArgumentList(List<Argument> argumentList) {
		for (Argument argument : argumentList) {
			Attribute attribute = Argument.createAttribute(argument);
			this.addAttribute(attribute);
		}
	}

	/** space-separated arguments (includes leading space)
	 * 
	 * @return
	 */
	public String getArgumentString() {
		List<Argument> argumentList = getArgumentList();
		Collections.sort(argumentList);
		StringBuilder sb = new StringBuilder();
		for (Argument argument : argumentList) {
			if (!NAME.equals(argument.getName())) {
				sb.append(" ");
				sb.append(Argument.MINUS_MINUS);
				sb.append(argument.getName());
				for (String value : argument.getValues()) {
					sb.append(" ");
					sb.append(value);
				}
			}
		}
		return sb.toString();
	}

	public String getName() {
		return this.getAttributeValue(NAME);
	}

	public Argument getArgument(String name) {
		Argument argument = Argument.createArgument(this.getAttribute(name));
		return argument;
	}

	public String getCommandString() {
		String s = COMMAND_SEPARATOR+" " + getName() + getArgumentString();
		return s;
	}

	public String getType() {
		return this.getAttributeValue(TYPE);
	}

	public File getProjectDir() {
		return new File(this.getProjectName());
	}

	public String getProjectName() {
		return this.getAttributeValue(PROJECT);
	}

	public String toString() {
		return this.toXML();
	}

	public BaseCommandElement getCommand(String name) {
		return (BaseCommandElement)XMLUtil.getSingleElement(this, "*[local-name()='"+BaseCommandElement.TAG+"' and @"+NAME+"='"+name+"']");
	}
}