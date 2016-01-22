package org.xmlcml.cmine.args;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cmine.files.CTree;
import org.xmlcml.cmine.files.XMLSnippets;

/** searches CTree by filepath (glob) and Xpath.
 * 
 * uses expression of type:
 * file(glob1)xpath(xpath1)file(glob2)xpath(2)... with 1 or more terms
 * 
 * @author pm286
 *
 */
public class FileXPathSearcher {

	private static final Logger LOG = Logger.getLogger(FileXPathSearcher.class);
	static {
		LOG.setLevel(org.apache.log4j.Level.DEBUG);
	}

	private static final String FILE = "file(";
	private static final String XPATH = "xpath(";
	private String searchExpression;
	private String start;
	private int current;
	private StringBuilder sb;
	private String chunk;
	private List<String> chunkList;
	private CTree cTree;
	private List<File> currentFiles;
	private List<File> extractedFileList;
	private List<XMLSnippets> snippetsList;

	public FileXPathSearcher(CTree cTree, String searchExpression) {
		this.searchExpression = searchExpression;
		this.cTree = cTree
;		parse(searchExpression);
	}

	private void parse(String searchExpression) {
		sb = new StringBuilder(searchExpression.trim());
		current = 0;
		start = FILE;
		chunkList = new ArrayList<String>();
		while (current < sb.length()) {
			String ss = sb.substring(current).toString();
			if (ss.startsWith(start)) {
				current += start.length();
				if (FILE.equals(start)) {
					start = XPATH;
				} else if (XPATH.equals(start)) {
					start = FILE;
				}
				getChunk();
			} else {
				throw new RuntimeException("cannot parse ("+searchExpression+") at char: "+current);
			}
		}
		LOG.trace(chunkList);
	}

	private String getChunk() {
		String ss = sb.substring(current);
		int i = ss.indexOf(start);
		if (i == -1) {
			if (ss.endsWith(")")) {
				i = ss.length();
			} else{
				throw new RuntimeException("Cannot find next token ("+start+") or end: "+ss);
			}
		}
		chunk = ss.substring(0,  i - 1);
		chunkList.add(chunk);
		LOG.trace(chunk);
		current += i;
		return chunk;
	}

	public List<String> getChunkList() {
		return chunkList;
	}
	
	public void search() {
		if (chunkList.size() > 2) {
			throw new RuntimeException("Cannot yet deal with multiple globbing");
		}
		for (int i = 0; i < chunkList.size(); i++) {
			if (i %2 == 0) {
				String fileGlob = chunkList.get(i);
				currentFiles = extractFiles(fileGlob);
			} else {
				List<File> filesWithSnippets = new ArrayList<File>();
				snippetsList = new ArrayList<XMLSnippets>();
				String xpath = chunkList.get(i);
				for (File currentFile : currentFiles) {
					XMLSnippets snippets = extractSnippets(currentFile, xpath);
					if (snippets.size() != 0) {
						filesWithSnippets.add(currentFile);
						snippetsList.add(snippets);
					} else {
						LOG.info("empty: "+currentFile);
					}
					LOG.trace("Snip: "+snippets.getSnippetsElement().toXML());
				}
				currentFiles = filesWithSnippets;
			}
		}
		cTree.setSearchFiles(currentFiles);
		cTree.setSnippetsList(snippetsList);
	}

	private XMLSnippets extractSnippets(File file, String xpath) {
		LOG.trace("XPath: "+chunk);
		XMLSnippets snippets = cTree.extractXMLSnippets(xpath, file);
		return snippets;
	}

	private List<File> extractFiles(String fileGlob) {
		extractedFileList = cTree.extractFiles(fileGlob);
		return extractedFileList;
	}
	
	public List<File> getExtractedFiles() {
		return extractedFileList;
	}

	public void output(String output) {
		// TODO Auto-generated method stub
		
	}

}
