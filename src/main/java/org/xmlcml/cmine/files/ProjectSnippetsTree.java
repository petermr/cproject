package org.xmlcml.cmine.files;

import java.util.Iterator;
import java.util.List;
import org.xmlcml.xml.XMLUtil;
import nu.xom.Element;

public class ProjectSnippetsTree extends Element {

	public static final String PROJECT_SNIPPETS_TREE = "projectSnippetsTree";

	private List<Element> projectSnippetsTreeList;

	private CProject cProject;
	
	public ProjectSnippetsTree(CProject cProject) {
		super(PROJECT_SNIPPETS_TREE);
		this.cProject = cProject;
	}

	public Iterator<Element> iterator() {
		getOrCreateElementChildren();
		return projectSnippetsTreeList.iterator();
	}

	private List<Element> getOrCreateElementChildren() {
		if (projectSnippetsTreeList == null) {
			projectSnippetsTreeList = XMLUtil.getQueryElements(this, SnippetsTree.SNIPPETS_TREE);
		}
		return projectSnippetsTreeList;
	}

	public void add(SnippetsTree snippetsTree) {
		this.appendChild(snippetsTree.copy());
		projectSnippetsTreeList = null;
	}

	public int size() {
		return getOrCreateElementChildren().size();
	}

	public SnippetsTree get(int i) {
		getOrCreateElementChildren();
		Element snippets = (i >= projectSnippetsTreeList.size() || i < 0) ? null : projectSnippetsTreeList.get(i);
		return snippets == null ? null : SnippetsTree.createSnippetsTree(snippets);
	}

	public String toString() {
		return this.toXML();
	}
}

