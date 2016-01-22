package org.xmlcml.cmine.files;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Element;

/** files extracted from CTree by filepath search.
 * 
 * @author pm286
 *
 */
public class CTreeFiles implements Iterable<File> {

	private static final Logger LOG = Logger.getLogger(XMLSnippets.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private List<File> fileList;
	
	public Iterator<File> iterator() {
		return fileList.iterator();
	}

	public CTreeFiles() {
		
	}

	public CTreeFiles(List<File> elementList) {
		this();
		this.fileList = new ArrayList<File>(elementList);
	}

	public int size() {
		return this.fileList == null ? -1 : fileList.size();
	}

	public File get(int i) {
		return this.fileList == null || i >= fileList.size() || i < 0 
				? null : fileList.get(i);
	}


}
