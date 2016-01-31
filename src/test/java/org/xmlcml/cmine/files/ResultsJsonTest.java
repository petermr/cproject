package org.xmlcml.cmine.files;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cmine.CMineFixtures;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ResultsJsonTest {

	private static final Logger LOG = Logger.getLogger(ResultsJsonTest.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testReadResultsJson() throws IOException {
		File file = new File(CMineFixtures.CMINE_DIR, "resultsJson");
		CTree ctree = new CTree(file);
		File resultsJson = ctree.getExistingResultsJSON();
		Assert.assertNotNull(resultsJson);
		String resultsJsonString = FileUtils.readFileToString(resultsJson);
	    JsonParser parser = new JsonParser();
	    JsonObject jsonObject = (JsonObject) parser.parse(resultsJsonString);
		System.out.println(jsonObject.get("journal"));
	}
	
	@Test
	public void testReadResultsJsonKeys() throws IOException {
		File file = new File(CMineFixtures.CMINE_DIR, "resultsJson");
		String resultsJsonString = readResultsJsonString(file);
	    JsonParser parser = new JsonParser();
	    JsonObject jsonObject = (JsonObject) parser.parse(resultsJsonString);
	    Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
	    Assert.assertEquals(19,  entrySet.size());
	    Iterator<Map.Entry<String, JsonElement>> entryIterator = entrySet.iterator();
	    Map<String, String> valueByKey = new HashMap<String, String>();
	    while(entryIterator.hasNext()) {
	    	Map.Entry<String, JsonElement> entry = entryIterator.next();
	    	String key = entry.getKey();
	    	JsonArray array = ((JsonObject)entry.getValue()).get("value").getAsJsonArray();
	    	if (array.size() == 0) {
	    		LOG.debug(key+"=null");
	    		continue;
	    	} else if (array.size() == 1) {
	    		String value = array.get(0).getAsString();
	    		LOG.debug(key+"="+value);
	    		valueByKey.put(key, value);
	    	} else {
	    		LOG.debug(key+"="+array.size()+"/"+array);
	    	}
	    }
	    Assert.assertEquals(11, valueByKey.size());
	}

	private String readResultsJsonString(File file) throws IOException {
		CTree ctree = new CTree(file);
		File resultsJson = ctree.getExistingResultsJSON();
		String resultsJsonString = FileUtils.readFileToString(resultsJson);
		return resultsJsonString;
	}
	
}
