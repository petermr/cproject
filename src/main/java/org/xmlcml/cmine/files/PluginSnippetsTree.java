package org.xmlcml.cmine.files;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.xml.XMLUtil;

import nu.xom.Element;

/** this is the accumulation of snippets for a given plugin. 
 * It should really be called PluginSnippetsTree, so we'll deprecate this 
 * and change the name.
 * 
 * @author pm286
 *
 */

public class PluginSnippetsTree extends Element {

	private static final Logger LOG = Logger.getLogger(PluginSnippetsTree.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String PROJECT_SNIPPETS_TREE = "projectSnippetsTree";

	private List<Element> snippetsElementList;
	private CProject cProject;
	private List<SnippetsTree> snippetsTreeList;
	private List<String> filenameList;
	private Map<String, SnippetsTree> snippetsTreeByCTreeName;
	private PluginOption pluginOption = null;
	
	public PluginSnippetsTree(CProject cProject) {
		this();
		this.cProject = cProject;
	}

	public PluginSnippetsTree() {
		super(PROJECT_SNIPPETS_TREE);
	}

	public static PluginSnippetsTree createPluginSnippetsTree(Element pluginSnippetsTreeXML) {
		PluginSnippetsTree pluginSnippetsTree = null;
		if (pluginSnippetsTreeXML != null) {
			String localName = pluginSnippetsTreeXML.getLocalName();
			if (PluginSnippetsTree.PROJECT_SNIPPETS_TREE.equals(localName)) {
				pluginSnippetsTree = new PluginSnippetsTree();
				XMLUtil.copyAttributes(pluginSnippetsTreeXML, pluginSnippetsTree);
				List<Element> childElements = XMLUtil.getQueryElements(pluginSnippetsTreeXML, "*");
				for (Element childElement : childElements) {
					SnippetsTree snippetsTree = SnippetsTree.createSnippetsTree(childElement);
					if (snippetsTree == null) {
						throw new RuntimeException("Cannot create SnippetsTree");
					}
					pluginSnippetsTree.add(snippetsTree);
				}
			}
		}
		return pluginSnippetsTree;
	}
	public Iterator<Element> iterator() {
		getOrCreateElementChildren();
		return snippetsElementList.iterator();
	}

	private List<Element> getOrCreateElementChildren() {
		if (snippetsElementList == null) {
			snippetsElementList = XMLUtil.getQueryElements(this, SnippetsTree.SNIPPETS_TREE);
		}
		return snippetsElementList;
	}

	public List<SnippetsTree> getOrCreateSnippetsTreeList() {
		if (snippetsTreeList == null) {
			getOrCreateElementChildren();
			snippetsTreeList = new ArrayList<SnippetsTree>();
			for (Element element : snippetsElementList) {
				SnippetsTree snippetsTree = SnippetsTree.createSnippetsTree(element);
				if (snippetsTree == null) {
					throw new RuntimeException("cannot create snippetsTree");
				}
				snippetsTreeList.add(snippetsTree);
			}
		}
		return snippetsTreeList;
	}

	public void add(SnippetsTree snippetsTree) {
		// don't add empty trees
		if (snippetsTree.size() > 0) {
			snippetsTree.getPluginOption();
			this.appendChild(snippetsTree);
		}
		snippetsElementList = null;
	}

	public int size() {
		return getOrCreateElementChildren().size();
	}

	public SnippetsTree get(int i) {
		getOrCreateElementChildren();
		Element snippets = (i >= snippetsElementList.size() || i < 0) ? null : snippetsElementList.get(i);
		return snippets == null ? null : SnippetsTree.createSnippetsTree(snippets);
	}

	public String toString() {
		return this.toXML();
	}

	public List<String> getOrCreateFilenameList() {
		if (filenameList == null) {
			getOrCreateSnippetsTreeList();
			filenameList = new ArrayList<String>();
			for (SnippetsTree snippetsTree : snippetsTreeList) {
				String filename = snippetsTree.getFilename();
				filenameList.add(filename);
			}
		}
		return filenameList;
	}

	public Collection<? extends String> getCTreeNameList() {
		List<String> cTreeNameList = new ArrayList<String>();
		getOrCreateSnippetsTreeList();
		for (SnippetsTree snippetsTree : snippetsTreeList) {
			String cTreeName = snippetsTree.getCTreeName();
			LOG.trace(">>>"+cTreeName);
			if (cTreeName != null) {
				cTreeNameList.add(cTreeName);
			}
		}
		return cTreeNameList;
	}

	public Map<String, SnippetsTree> getOrCreateSnippetsTreeByCTreeName() {
		if (snippetsTreeByCTreeName == null) {
			snippetsTreeByCTreeName = new HashMap<String, SnippetsTree>();
			getOrCreateSnippetsTreeList();
			for (SnippetsTree snippetsTree : snippetsTreeList) {
				String projectName = snippetsTree.getCTreeName();
				if (projectName != null) {
					snippetsTreeByCTreeName.put(projectName, snippetsTree);
				}
			}
		}
		return snippetsTreeByCTreeName;
	}

	public PluginOption getPluginOption() {
		if (pluginOption == null) {
			getOrCreateSnippetsTreeList();
			for (SnippetsTree snippetsTree : snippetsTreeList) {
				PluginOption po = snippetsTree.getPluginOption();
				if (pluginOption == null) {
					pluginOption = po;
				} else if (!pluginOption.equals(po)) {
					throw new RuntimeException("duplicate PluginOption: "+pluginOption);
				}
			}
		}
		return pluginOption;
	}

}

