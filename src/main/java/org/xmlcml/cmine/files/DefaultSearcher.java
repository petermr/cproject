package org.xmlcml.cmine.files;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.xml.XPathGenerator;

public class DefaultSearcher {

	public static final Logger LOG = Logger.getLogger(DefaultSearcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}


	protected String name;

	public String getName() {
		return name;
	}

	public ResultsElement search(List<? extends Element> elements) {
		ResultsElement resultsElement = new ResultsElement();
		for (Element element : elements) {
			String xpath = new XPathGenerator(element).getXPath();
			LOG.trace("xpath: "+xpath);
			ResultsElement subResultsElement = this.searchXomElement(element);
			if (subResultsElement.size() > 0) {
				LOG.debug("XPATH :"+element.toXML());
				subResultsElement.setXPath(xpath);
				resultsElement.transferResultElements(subResultsElement);
			}
		}
		return resultsElement;
	}

	/** create resultsElement.
	 * 
	 * May be empty if no hits
	 * 
	 * @param xomElement
	 * @return
	 */
	public ResultsElement searchXomElement(Element xomElement) {
		ResultsElement resultsElement = new ResultsElement();
		String value = getValue(xomElement);
		List<ResultElement> resultElementList = search(value); // crude to start with
		for (ResultElement resultElement : resultElementList) {
			resultsElement.appendChild(resultElement);
		}
		return resultsElement;
	}

	/** flatten all tags.
	 * 
	 * @param xomElement
	 * @return
	 */
	public String getValue(Element xomElement) {
		return xomElement.getValue();
	}

	public List<ResultElement> search(String value) {
		List<ResultElement> resultElementList = new ArrayList<ResultElement>();
		Matcher matcher = getPattern().matcher(value);
		int start = 0;
		while (matcher.find(start)) {
			ResultElement resultElement = createResultElement(value, matcher);
			resultElementList.add(resultElement);
			start = matcher.end();
		}
		return resultElementList;
	}
	
	protected Pattern getPattern() {
		throw new RuntimeException("Must overload getPattern()");
	}
	
	protected ResultElement createResultElement(String value, Matcher Matcher) {
		throw new RuntimeException("Must overload createResultElement(String value, Matcher Matcher))");
	}

	protected ResultElement createResultElement() {
		throw new RuntimeException("Must override createResultElement()");
	}

}
