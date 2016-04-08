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
	
	private List<CTree> cmTreeList;
	
	public CTreeList() {
		ensureCTreeList();
	}

	public CTreeList(List<CTree> cTrees) {
		ensureCTreeList();
		for (CTree cTree : cTrees) {
			cmTreeList.add(cTree);
		}
	}

	private void ensureCTreeList() {
		if (cmTreeList == null) {
			cmTreeList = new ArrayList<CTree>();
		}
	}

	public int size() {
		ensureCTreeList();
		return cmTreeList.size();
	}

	public Iterator<CTree> iterator() {
		ensureCTreeList();
		return cmTreeList.iterator();
	}
	
	public CTree get(int i) {
		ensureCTreeList();
		return cmTreeList.get(i);
	}
	
	public void add(CTree cmTree) {
		ensureCTreeList();
		cmTreeList.add(cmTree);
	}

	/** gets CTree by name .
	 * 
	 * @param name (uses getName())
	 * @return
	 */
	public CTree getCTreeByName(String name) {
		if (name != null) {
			for (CTree cTree : cmTreeList) {
				if (cTree.getName().equals(name)) {
					return cTree;
				}
			}
		}
		return null;
	}
	
}
