package org.xmlcml.cmine.files;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** list of CMDir objects.
 * 
 * @author pm286
 *
 */
public class CMDirList implements Iterable<CMDir> {

	
	private static final Logger LOG = Logger
			.getLogger(CMDirList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private List<CMDir> cmDirList;
	
	public CMDirList() {
		ensureCMDirList();
	}

	private void ensureCMDirList() {
		if (cmDirList == null) {
			cmDirList = new ArrayList<CMDir>();
		}
	}

	public int size() {
		ensureCMDirList();
		return cmDirList.size();
	}

	public Iterator<CMDir> iterator() {
		ensureCMDirList();
		return cmDirList.iterator();
	}
	
	public CMDir get(int i) {
		ensureCMDirList();
		return cmDirList.get(i);
	}
	
	public void add(CMDir cmDir) {
		ensureCMDirList();
		cmDirList.add(cmDir);
	}
	
}
