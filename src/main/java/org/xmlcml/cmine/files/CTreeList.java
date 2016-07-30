package org.xmlcml.cmine.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** list of CTree objects.
 * 
 * the list is sorted before use (might cause small performance hit...)
 * 
 * @author pm286
 *
 */
public class CTreeList implements Iterable<CTree> {
	
	private static final Logger LOG = Logger.getLogger(CTreeList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private List<CTree> cTreeList;
	
	public CTreeList() {
		ensureCTreeList();
	}

	public CTreeList(List<CTree> cTrees) {
		ensureCTreeList();
		for (CTree cTree : cTrees) {
			cTreeList.add(cTree);
		}
		sort();
	}

	private void ensureCTreeList() {
		if (cTreeList == null) {
			cTreeList = new ArrayList<CTree>();
		}
	}

	public int size() {
		ensureCTreeList();
		return cTreeList.size();
	}

	public Iterator<CTree> iterator() {
		ensureCTreeList();
		return cTreeList.iterator();
	}
	
	public CTree get(int i) {
		ensureCTreeList();
		return cTreeList.get(i);
	}
	
	public void add(CTree cmTree) {
		ensureCTreeList();
		cTreeList.add(cmTree);
	}
	
	public Set<CTree> asSet() {
		return new HashSet<CTree>(cTreeList);
	}
	
	public CTreeList not(CTreeList cTreeList) {
		Set<CTree> newSet = new HashSet<CTree>(this.asSet());
		newSet.removeAll(cTreeList.asSet());
		List<CTree> newList = new ArrayList<CTree>(newSet);
		CTreeList newCTreeList = new CTreeList(newList);
		return newCTreeList;
	}
	
	public CTreeList and(CTreeList cTreeList) {
		Set<CTree> newSet = new HashSet<CTree>(this.asSet());
		newSet.retainAll(cTreeList.asSet());
		List<CTree> newList = new ArrayList<CTree>(newSet);
		CTreeList newCTreeList = new CTreeList(newList);
		return newCTreeList;
	}
	
	public CTreeList or(CTreeList cTreeList) {
		Set<CTree> newSet = new HashSet<CTree>(this.asSet());
		newSet.addAll(cTreeList.asSet());
		List<CTree> newList = new ArrayList<CTree>(newSet);
		CTreeList newCTreeList = new CTreeList(newList);
		return newCTreeList;
	}

	/** removes from list.
	 * 
	 * @param cTree
	 * @return
	 */
	public boolean remove(CTree cTree) {
		return cTreeList.remove(cTree);
	}

	/** removes from list and deletes directory.
	 * 
	 * Cannot be undone
	 * 
	 * @param cTree
	 * @return
	 * @throws IOException 
	 */
	public boolean delete(CTree cTree) throws IOException {
		if (cTreeList.remove(cTree)) {
			FileUtils.deleteDirectory(cTree.getDirectory());
			return true;
		}
		return false;
	}
	
	public List<CTree> getCTreeList() {
		if (cTreeList != null) {
			Collections.sort(cTreeList);
		}
		return cTreeList;
	}

	/** directories in CTrees
	 *  
	 * may include nulls
	 * 
	 * @return
	 */
	public List<File> getCTreeDirectoryList() {
		Collections.sort(cTreeList);
		List<File> directoryList = new ArrayList<File>();
		for (CTree cTree : cTreeList) {
			File directory = cTree.getDirectory();
			directoryList.add(directory);
		}
		return directoryList;
	}

	public void sort() {
		if (cTreeList != null) {
			Collections.sort(cTreeList);
		}
	}

}
