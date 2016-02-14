package org.xmlcml.cmine.files;

import java.util.ArrayList;
import java.util.List;

public class PluginOption implements Comparable<PluginOption> {

	public String pluginName;
	public String optionName;
	
	public PluginOption() {
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
	
	
}
