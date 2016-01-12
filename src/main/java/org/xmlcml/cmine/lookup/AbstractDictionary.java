package org.xmlcml.cmine.lookup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public abstract class AbstractDictionary {

	protected String title;
	protected Map<String, List<List<String>>> trailingWordsByLeadWord;
	
	public abstract boolean contains(String string);
	/** for indexing compound words
	 * 
	 * "transcription factor" returns [["factor"]]
	 * "Royal Mail" and "Royal Shakespeare Company" returns [["Mail"],["Shakespeare","Company"]]
	 * 
	 * "food" returns [[]] (not null)
	 * @param key
	 * @return
	 */
	public abstract List<List<String>> getTrailingWords(String key);

	
//	public static AbstractDictionary createDictionary(String dictionaryName) {
//		AbstractDictionary dictionary = null;
//		if (false) {
//			// should test special cases here
//		} else {
//			File file = new File(dictionaryName);
//			dictionary = new StringDictionary();
//			try {
//				dictionary.readFile(file);
//			} catch (IOException e) {
//				throw new RuntimeException("Cannot read dictionary File: "+file, e);
//			}
//		}
//		return dictionary;
//	}

	public static AbstractDictionary createDictionary(String dictionarySource, InputStream is) {
		AbstractDictionary dictionary = null;
		if (false) {
			// should test special cases here
		} else {
//			File file = new File(dictionaryName);
			dictionary = new StringDictionary();
			try {
				dictionary.readFile(dictionarySource, is);
			} catch (IOException e) {
				throw new RuntimeException("Cannot read dictionary File: "+dictionarySource, e);
			}
		}
		return dictionary;
	}

	protected abstract void readFile(String dictionarySource, InputStream is) throws IOException;

	public Map<String, List<List<String>>> getTrailingWordsByLeadWord() {
		return trailingWordsByLeadWord;
	}
	
	public String getTitle() {
		return title;
	}
}
