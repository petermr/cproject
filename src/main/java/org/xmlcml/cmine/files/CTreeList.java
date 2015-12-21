package org.xmlcml.cmine.files;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** list of CTree objects.
 * 
 * @author pm286
 *
 */
public class CTreeList implements Iterable<CTree> {

	
	private static final Logger LOG = Logger
			.getLogger(CTreeList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private List<CTree> cmDirList;
	
	public CTreeList() {
		ensureCTreeList();
	}

	private void ensureCTreeList() {
		if (cmDirList == null) {
			cmDirList = new ArrayList<CTree>();
		}
	}

	public int size() {
		ensureCTreeList();
		return cmDirList.size();
	}

	public Iterator<CTree> iterator() {
		ensureCTreeList();
		return cmDirList.iterator();
	}
	
	public CTree get(int i) {
		ensureCTreeList();
		return cmDirList.get(i);
	}
	
	public void add(CTree cmDir) {
		ensureCTreeList();
		cmDirList.add(cmDir);
	}
	
}
