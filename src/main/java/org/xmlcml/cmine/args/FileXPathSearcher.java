package org.xmlcml.cmine.args;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.cmine.files.CTree;
import org.xmlcml.cmine.files.CTreeFiles;
import org.xmlcml.cmine.files.SnippetsTree;
import org.xmlcml.cmine.files.XMLSnippets;

/** searches CTree by filepath (glob) and Xpath.
 * 
 * uses expression of type:
 * file(glob1)xpath(xpath1)file(glob2)xpath(2)... with 1 or more terms
 * 
 * CURRENTLY ONLY file(glob)xpath(xpath) is supported
 * 
 * @author pm286
 *
 */
public class FileXPathSearcher {

	private static final Logger LOG = Logger.getLogger(FileXPathSearcher.class);
	static {
		LOG.setLevel(org.apache.log4j.Level.DEBUG);
	}

	private static final String RPAREN = ")";
	private static final String LPAREN = "(";
	private static final String FILE = "file";
	private static final String FILE1 = FILE+LPAREN;
	private static final String XPATH = "xpath";
	private static final String XPATH1 = XPATH+LPAREN;


	
	private String searchExpression;
	private String start;
	private int currentChar;
	private StringBuilder sb;
	private List<String> chunkList;
	private CTree cTree;
	private CTreeFiles cTreeFiles;
	private SnippetsTree snippetsTree;
	private String currentGlob;
	private String currentXPath;

	public FileXPathSearcher(CTree cTree, String searchExpression) {
		this(searchExpression);
		this.cTree = cTree;
	}

	public FileXPathSearcher(String searchExpression) {
		parse(searchExpression);
	}

	private void parse(String searchExpression) {
		this.searchExpression = searchExpression;
		if (searchExpression == null) {
			LOG.error("Null search expression");
			return;
		}
		sb = new StringBuilder(searchExpression.trim());
		currentChar = 0;
		start = FILE1;
		chunkList = new ArrayList<String>();
		while (currentChar < sb.length()) {
			String ss = sb.substring(currentChar).toString();
			if (ss.startsWith(start)) {
				currentChar += start.length();
				if (FILE1.equals(start)) {
					start = XPATH1;
				} else if (XPATH1.equals(start)) {
					start = FILE1;
				}
				getChunk();
			} else {
				throw new RuntimeException("cannot parse ("+searchExpression+") at char: "+currentChar);
			}
		}
		if (chunkList.size() > 0) {
			currentGlob = chunkList.get(0);
		}
		if (chunkList.size() > 1) {
			currentXPath = chunkList.get(1);
		}
		LOG.trace(chunkList);
	}

	private String getChunk() {
		String ss = sb.substring(currentChar);
		int i = ss.indexOf(start);
		if (i == -1) {
			if (ss.endsWith(")")) {
				i = ss.length();
			} else{
				throw new RuntimeException("Cannot find next token ("+start+") or end: "+ss);
			}
		}
		String chunk = ss.substring(0,  i - 1);
		chunkList.add(chunk);
		LOG.trace(chunk);
		currentChar += i;
		return chunk;
	}

	public List<String> getChunkList() {
		return chunkList;
	}
	
	/** currently only glob_xpath without repeats.
	 * 
	 */
	public void search() {
		if (chunkList.size() > 2) {
			throw new RuntimeException("Cannot yet deal with multiple globbing");
		}
		for (int i = 0; i < chunkList.size(); i++) {
			// alternates file and xpath
			if (i %2 == 0) {
				currentGlob = chunkList.get(i);
				cTreeFiles = cTree.extractCTreeFiles(currentGlob);
			} else {
				CTreeFiles filesWithSnippets = new CTreeFiles(cTree);
				snippetsTree = new SnippetsTree();
				currentXPath = chunkList.get(i);
				LOG.trace("XP "+currentXPath);
				for (File currentFile : cTreeFiles) {
					LOG.trace("CF "+currentFile);
					XMLSnippets snippets = cTree.extractXMLSnippets(currentXPath, currentFile);
					LOG.trace("SN "+snippets.toXML());
					if (snippets.size() != 0) {
						LOG.trace("ADD SNIPPETS");
						filesWithSnippets.add(currentFile);
						snippetsTree.add(snippets);
					} else {
						LOG.trace("empty: "+currentFile);
					}
					LOG.trace("Snip: "+snippets.toXML());
				}
				cTreeFiles = filesWithSnippets;
			}
		}
		cTree.setCTreeFiles(cTreeFiles);
		cTree.setSnippetsTree(snippetsTree);
	}

	public SnippetsTree getSnippetsTree() {
		return snippetsTree;
	}

	public CTreeFiles getCTreeFiles() {
		return cTreeFiles;
	}

	public void output(String output) {
		// TODO Auto-generated method stub
	}
	
	public String getCurrentGlob() {
		return currentGlob;
	}
	
	public String getCurrentXPath() {
		return currentXPath;
	}

	public String getSearchExpression() {
		return searchExpression;
	}

	public void setSearchExpression(String searchExpression) {
		this.searchExpression = searchExpression;
	}

	public static String createFilterExpression(List<String> filterStrings) {
		String filterExpression = null;
		if (filterStrings != null && filterStrings.size() == 1) {
			filterExpression = filterStrings.get(0);
		} else {
			StringBuilder sb = new StringBuilder();
			int counter = 0;
			while (counter < filterStrings.size()) {
				String token = filterStrings.get(counter++);
				if (FILE.equals(token)) {
					if (counter >= filterStrings.size()) {
						throw new RuntimeException("file requires an argument");
					}
					sb.append(token);
					sb.append(LPAREN);
					sb.append(filterStrings.get(counter++));
					sb.append(RPAREN);
				} else if (XPATH.equals(token)) {
					if (counter >= filterStrings.size()) {
						throw new RuntimeException("xpath requires an argument");
					}
					sb.append(token);
					sb.append(LPAREN);
					sb.append(filterStrings.get(counter++));
					sb.append(RPAREN);
				} else {
					LOG.warn("unsupported token in filter: "+token);
				}
			}
			filterExpression = sb.toString();
		}
		return filterExpression;
	}

}
