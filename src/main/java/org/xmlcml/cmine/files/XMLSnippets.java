package org.xmlcml.cmine.files;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Attribute;
import nu.xom.Element;

/** manages a list of XML elements extracted from a document by XPath
 * 
 * @author pm286
 *
 */
public class XMLSnippets implements Iterable<Element>{

	private static final Logger LOG = Logger.getLogger(XMLSnippets.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String FILE = "file";
	
	private List<Element> elementList;
	private File file;
	private Element snippetsElement;
	
	public Iterator<Element> iterator() {
		return elementList.iterator();
	}

	public XMLSnippets() {
		
	}

	public XMLSnippets(List<Element> elementList, File file) {
		this();
		this.file = file;
		this.elementList = new ArrayList<Element>(elementList);
		getSnippetsElement();
	}

	public int size() {
		return this.elementList == null ? -1 : elementList.size();
	}

	public Element get(int i) {
		return this.elementList == null || i >= elementList.size() || i < 0 
				? null : elementList.get(i);
	}

	/** gets XML value of snippet(i).
	 * 
	 * @param i
	 * @return null if out of range else element.get(i).getValue()
	 */
	public String getValue(int i) {
		Element element = this.get(i);
		return element == null ? null : element.getValue();
	}

	public void addFile(File file) {
		this.file = file;
	}
	
	public Element getSnippetsElement() {
		snippetsElement = new Element("snippets");
		snippetsElement.addAttribute(new Attribute(FILE, file.getAbsolutePath()));
		for (Element element : elementList) {
			element.detach();
			snippetsElement.appendChild(element);
		}
		return snippetsElement;
	}
	
}
