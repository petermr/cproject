package org.xmlcml.cmine.lookup;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.xml.XMLUtil;

import nu.xom.Element;

public class StringDictionary extends AbstractDictionary {

	private static final Logger LOG = Logger.getLogger(StringDictionary.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public StringDictionary() {
		
	}

	public void setInputStream(String name, InputStream is) throws IOException {
		Element dictionaryElement = XMLUtil.parseQuietlyToDocument(is).getRootElement();
		title = dictionaryElement.getAttributeValue("title");
		List<Element> entryList = XMLUtil.getQueryElements(dictionaryElement, "entry");
		trailingWordsByLeadWord = new HashMap<String, List<List<String>>>();
		for (Element entry : entryList) {
			String value = entry.getValue().trim();
			if (value.length() == 0) {
				continue;
			}
			String[] wordList = value.split("\\s+");
			String key = wordList[0];
			List<List<String>> lists = trailingWordsByLeadWord.get(key);
			if (lists == null) {
				lists = new ArrayList<List<String>>();
				trailingWordsByLeadWord.put(key, lists);
			}
			List<String> trailingList = new ArrayList<String>();
			lists.add(trailingList);
			for (int i = 1; i < wordList.length; i++) {
				trailingList.add(wordList[i]);
			}
		}
//		LOG.debug(trailingWordsByHead);
	}
	
	@Override
	public boolean contains(String s) {
		List<List<String>> words = getTrailingWords(s);
		return words == null;
	}
	
	public List<List<String>> getTrailingWords(String headWord) {
		return trailingWordsByLeadWord != null ? trailingWordsByLeadWord.get(headWord) : null;
	}
	

}
