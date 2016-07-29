package org.xmlcml.cmine.metadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.cmine.CMineFixtures;
import org.xmlcml.cmine.util.RectangularTable;

import com.google.common.collect.Multiset;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

public class ScraperTest {
	
	public static final Logger LOG = Logger.getLogger(ScraperTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}


	@Test
	// PMR
	public void testCreateScraperKeys() throws JsonSyntaxException, IOException {
		if (!CMineFixtures.exist(CMineFixtures.SCRAPER_DIR)) return;
		ScraperSet scraperSet = new ScraperSet(CMineFixtures.SCRAPER_DIR);
		Multiset<String> keys = scraperSet.getOrCreateScraperKeys();
//		LOG.debug("KEYS: "+keys);
	}

	@Test
	public void testCreateScraperSpreadsheet() throws JsonSyntaxException, IOException {
		if (!CMineFixtures.exist(CMineFixtures.SCRAPER_DIR)) return;
		ScraperSet scraperSet = new ScraperSet(CMineFixtures.SCRAPER_DIR);
		Map<File, JsonElement> elementsByFile = scraperSet.getJsonElementByFile();
		List<Multiset.Entry<String>> elements = scraperSet.getOrCreateScraperElementsByCount();
		List<String> headings = new ArrayList<String>();
		for (Multiset.Entry<String> entry : elements) {
			headings.add(entry.getElement());
		}
		RectangularTable csvTable = new RectangularTable();
		csvTable.addRow(headings);
		List<File> files = new ArrayList<File>(elementsByFile.keySet());
		for (File file : files) {
			List<String> row = new ArrayList<String>();
			for (int i = 0; i < headings.size(); i++) {
				row.add("");
			}
			JsonElement element = elementsByFile.get(file);
			JsonElement elements1 = element.getAsJsonObject().get(ScraperSet.ELEMENTS);
			Set<Map.Entry<String, JsonElement>> entries = elements1.getAsJsonObject().entrySet();
			for (Map.Entry<String, JsonElement> entry : entries) {
				String name = entry.getKey();
				int idx = headings.indexOf(name);
				if (idx ==  -1) {
					LOG.error("bad key "+name);
				}
				row.set(idx, name);
			}
			csvTable.addRow(row);
		}
		csvTable.writeCsvFile(new File(CMineFixtures.SCRAPER_DIR, ScraperSet.SCRAPERS_CSV).toString());
	}

}
