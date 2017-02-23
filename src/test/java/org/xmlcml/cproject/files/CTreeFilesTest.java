package org.xmlcml.cproject.files;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 * 
 * @author pm286
 *
 */
public class CTreeFilesTest {

	;
	private static final Logger LOG = Logger.getLogger(CTreeFilesTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testAddFile() {
		CTree cTree = new CTree(new File("zz/cTree"));
		CTreeFiles cTreeFiles = new CTreeFiles(cTree);
		cTreeFiles.add(new File("a/b.xml"));
		cTreeFiles.add(new File("c/d.txt"));
		cTreeFiles.add(new File("b/e.pdf"));
		Assert.assertEquals("cTreeFiles", ""
				+ "<cTreeFiles cTree=\"zz/cTree\">"
				+ "<file name=\"a/b.xml\" />"
				+ "<file name=\"c/d.txt\" />"
				+ "<file name=\"b/e.pdf\" />"
				+ "</cTreeFiles>", 
				cTreeFiles.toString());
	}
	
	@Test
	public void testAddFileAndSort() {
		CTree cTree = new CTree(new File("zz/cTree"));
		CTreeFiles cTreeFiles = new CTreeFiles(cTree);
		cTreeFiles.add(new File("a/b.xml"));
		cTreeFiles.add(new File("c/d.txt"));
		cTreeFiles.add(new File("b/e.pdf"));
		cTreeFiles.sort();
		Assert.assertEquals("cTreeFiles", ""
				+ "<cTreeFiles cTree=\"zz/cTree\">"
				+ "<file name=\"a/b.xml\" />"
				+ "<file name=\"b/e.pdf\" />"
				+ "<file name=\"c/d.txt\" />"
				+ "</cTreeFiles>", 
				cTreeFiles.toString());
	}
}
