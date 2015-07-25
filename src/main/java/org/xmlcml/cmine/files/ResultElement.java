package org.xmlcml.cmine.files;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** a container for a "result" from an action on a CMDir.
 * 
 * Normally output to the "results" directory
 * 
 * @author pm286
 *
 */
public class ResultElement extends Element {
	
	private static final Logger LOG = Logger.getLogger(ResultElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String EXACT  = "exact";
	private static final String ID     = "id";
	public  static final String MATCH  = "match";
	private static final String NAME   = "name";
	public  static final String POST   = "post";
	public  static final String PRE    = "pre";
	public  static final String TAG    = "result";
	public  static final String TITLE  = "title";
	private static final String XPATH  = "xpath";

	public ResultElement() {
		super(TAG);
	}

	public ResultElement(String title) {
		this();
		this.setTitle(title);
	}

	private void setTitle(String title) {
		if (title == null) {
			throw new RuntimeException("title cannot be null");
		}
		this.addAttribute(new Attribute(TITLE, title));
	}

	public String getExact() {
		return this.getAttributeValue(EXACT);
	}
	
	public void setExact(String value) {
		setValue(EXACT, value);
	}
	
	public String getMatch() {
		return this.getAttributeValue(MATCH);
	}
	
	public void setMatch(String value) {
		setValue(MATCH, value);
	}
	
	public String getName() {
		return this.getAttributeValue(NAME);
	}
	
	public void setName(String value) {
		setValue(NAME, value);
	}
	
	public String getPre() {
		return this.getAttributeValue(PRE);
	}
	
	public void setPre(String value) {
		setValue(PRE, value);
	}
	
	public String getPost() {
		return this.getAttributeValue(POST);
	}
	
	public void setPost(String value) {
		setValue(POST, value);
	}
	
	public void setXPath(String xpath) {
		this.addAttribute(new Attribute(XPATH, xpath));
	}

	public String getXPath() {
		return this.getAttributeValue(XPATH);
	}

	public void setValue(String name, String value) {
		Attribute attribute = new Attribute(name, value);
		this.addAttribute(attribute);
	}

	public void setId(String lookupName, String lookupId) {
		if (lookupName != null && lookupId != null) {
			this.addAttribute(new Attribute("_LOOKUP_"+lookupName, lookupId));
		}
	}

	
}
