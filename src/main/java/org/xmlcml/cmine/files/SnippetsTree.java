package org.xmlcml.cmine.files;

import java.util.Iterator;
import java.util.List;

import org.xmlcml.xml.XMLUtil;

import nu.xom.Element;

public class SnippetsTree extends Element {

	public static final String SNIPPETS_TREE = "snippetsTree";

	private List<Element> snippetsList;
	
	public SnippetsTree() {
		super(SNIPPETS_TREE);
	}

	public static SnippetsTree createSnippetsTree(Element snippets) {
		SnippetsTree snippetsTree = null;
		if (snippets != null && snippets.getLocalName().equals(SnippetsTree.SNIPPETS_TREE)) {
			snippetsTree = new SnippetsTree();
			XMLUtil.copyAttributes(snippets, snippetsTree);
			List<Element> childElements = XMLUtil.getQueryElements(snippets, "*");
			for (Element childElement : childElements) {
				snippetsTree.appendChild(childElement.copy());
			}
		}
		return snippetsTree;
	}


	public Iterator<Element> iterator() {
		getOrCreateElementChildren();
		return snippetsList.iterator();
	}

	private List<Element> getOrCreateElementChildren() {
		if (snippetsList == null) {
			snippetsList = XMLUtil.getQueryElements(this, XMLSnippets.SNIPPETS);
		}
		return snippetsList;
	}

	public void add(XMLSnippets snippets) {
		this.appendChild(snippets.copy());
		snippetsList = null;
	}

	public int size() {
		return getOrCreateElementChildren().size();
	}

	public XMLSnippets get(int i) {
		getOrCreateElementChildren();
		Element snippets = (i >= snippetsList.size() || i < 0) ? null : snippetsList.get(i);
		return snippets == null ? null : XMLSnippets.createXMLSnippets(snippets);
	}

	public String toString() {
		return this.toXML();
	}
}
