package org.xmlcml.cmine.files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class PluginOption implements Comparable<PluginOption> {

	private static final String PUNCTUATION_TO_REMOVE = "[\\*\\/\\(\\)\\[\\]\\@\\>]";
	private static final String SNIPPETS_XML = ".snippets.xml";
	private static final Logger LOG = Logger.getLogger(PluginOption.class);
	private static final String SUMMARY = "summary";
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public String pluginName;
	public String optionName;
	protected List<String> options;
	protected List<String> flags;
	protected File projectDir;
	protected String optionString;
	protected String resultXPathAttribute;
	protected String resultXPathBase;
	protected List<OptionFlag> optionFlags; // obsolete?
	
	public PluginOption() {
		init();
	}
	
	private void init() {
		this.optionFlags = new ArrayList<OptionFlag>();
	}

	public PluginOption(String pluginName, String optionName) {
		this.pluginName = pluginName;
		this.optionName = optionName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((optionName == null) ? 0 : optionName.hashCode());
		result = prime * result + ((pluginName == null) ? 0 : pluginName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PluginOption other = (PluginOption) obj;
		if (optionName == null) {
			if (other.optionName != null)
				return false;
		} else if (!optionName.equals(other.optionName))
			return false;
		if (pluginName == null) {
			if (other.pluginName != null)
				return false;
		} else if (!pluginName.equals(other.pluginName))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return pluginName+":"+optionName;
	}

	public int compareTo(PluginOption o) {
		if (o == null) {
			return -1;
		} else {
			return this.toString().compareTo(o.toString());
		}
	}

	public static List<String> createStringList(List<PluginOption> pluginOptionList) {
		List<String> stringList = new ArrayList<String>();
		for (PluginOption pluginOption : pluginOptionList) {
			stringList.add(pluginOption.toString());
		}
		return stringList;
	}

	public static PluginOption getPluginOption(String pluginOptionName) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getOptionString() {
		return optionString;
	}

	public String getHeading() {
		String heading = getSnippetsName();
		if (optionString != null) {
			heading = optionString; 
		}
		return heading;
	}

	public String getSnippetsName() {
		return SUMMARY+"/"+this.pluginName+"/"+this.optionName;
	}

//	public String getSnippetsTreeName() {
//		return getSnippetsName()+SNIPPETS_XML;
//	}

	protected void setOptionFlags(List<OptionFlag> optionFlags) {
		this.optionFlags = optionFlags;
	}

	List<OptionFlag> getOptionFlags() {
		return this.optionFlags;
	}

	protected String getOption(String option) {
		return option;
	}

	public void setProjectDir(File projectDir) {
		this.projectDir = projectDir;
	}

	public File getProjectDir() {
		return projectDir;
	}

	public static String removePunctuation(String snippetsName) {
		return snippetsName.replaceAll(PUNCTUATION_TO_REMOVE, "_");
	}

}
