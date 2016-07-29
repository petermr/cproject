package org.xmlcml.cmine.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math.distribution.ZipfDistribution;
import org.apache.commons.math.distribution.ZipfDistributionImpl;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test
;
public class URLShuffleTest {

	public static final Logger LOG = Logger.getLogger(URLShuffleTest.class);
	private int testCount;
	static {
		LOG.setLevel(Level.DEBUG);
	}


	public List<String> createURLs() {
		List<String> domains = Arrays.asList(new String[] {
				"http://a.com",
				"http://b.com",
				"http://c.com",
				"http://d.com",
				"http://e.com",
				"http://f.com",
		});
		List<Integer> frequency = Arrays.asList(new Integer[] {
				12,
				7,
				5,
				3,
				2,
				1
		});
		
		return createUrls(domains, frequency);
	}

	public List<String> createURLs1() {
		List<String> roots = new ArrayList<String>();
		testCount = 0;
		for (int i = 0; i < 26; i++) {
			for (int j = 0; j < 9; j++) {
				String root = "http://"+String.valueOf((char)('a' + i)+""+j+".org");
				roots.add(root);
				testCount++;
			}
		};
		testCount *= 3;
		ZipfDistribution zipf = new ZipfDistributionImpl(roots.size(), 1.);
		List<Integer> frequency = new ArrayList<Integer>();
		for (int i = 0; i < roots.size(); i++) {
			double p = zipf.probability(i);
			Integer ff = (int) Math.ceil((testCount * zipf.probability(i + 1)));
			frequency.add(ff);
		}
		
		return createUrls(roots, frequency);
	}

	private List<String> createUrls(List<String> roots, List<Integer> frequency) {
		List<String> urls = new ArrayList<String>();
		for (int i = 0; i < Math.min(roots.size(), frequency.size()); i++) {
			String root = roots.get(i);
			for (int j = 0; j < frequency.get(i); j++) {
				urls.add(root+"/"+j);
			}
		}
		return urls;
	}
	
	@Test
	public void testShuffleURLs() {
		URLShuffler shuffler = new URLShuffler(); 
		shuffler.readURLs(createURLs());
		List<String> urls = shuffler.getShuffledUrls();
		LOG.trace(urls);
	}
	
	@Test
	public void testShuffleURLs1() {
		URLShuffler shuffler = new URLShuffler(); 
		shuffler.readURLs(createURLs1());
		List<String> urls = shuffler.getShuffledUrls();
		LOG.trace(urls);
	}
}
