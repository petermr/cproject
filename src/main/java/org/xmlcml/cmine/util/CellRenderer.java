package org.xmlcml.cmine.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.html.HtmlA;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlSpan;

import nu.xom.Attribute;

public class CellRenderer {

	private static final Logger LOG = Logger.getLogger(CellRenderer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private String flag;
	private String value;
	private int characterCount;
	private boolean wordCount;
	private boolean visible;
	private String href0;
	private String href1;

	public CellRenderer(String flag) {
		this.flag = flag;
		setDefaults();
	}

	private void setDefaults() {
		this.visible = true;
	}

	public CellRenderer setBrief(int characterCount) {
		this.characterCount = characterCount;
		return this;
	}

	public CellRenderer setWordCount(boolean wordCount) {
		this.wordCount = wordCount;
		return this;
	}

	public CellRenderer setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}

	public CellRenderer setHref0(String href0) {
		this.href0 = href0;
		return this;
	}

	public CellRenderer setHref1(String href1) {
		this.href1 = href1;
		return this;
	}

	public boolean isVisible() {
		return visible;
	}

	public String getValue() {
		return value;
	}

	public HtmlElement getHtmlElement() {
		HtmlElement element = new HtmlSpan();
		String s = value;
		if (characterCount != 0) {
			s = s.substring(0, Math.min(s.length(), characterCount));
			s += "...";
			element.addAttribute(new Attribute("title", value));
		}
		if (wordCount) {
			s = "["+s.length()+"]";
			element.addAttribute(new Attribute("title", value));
		}
		if (href0 != null || href1 != null) {
			element = createA(s);
		} else {
			element.appendChild(s);
		}
		return element;
	}

	private HtmlElement createA(String entityRef) {
		HtmlA a;
		a = new HtmlA();
		a.appendChild(entityRef);
		String href = createHref(entityRef);
		if (href != null) {
			a.setHref(href);
		}
		return a;
	}

	private String createHref(String entityRef) {
		String href = null;
		if (href0 != null) {
			href = href0;
		};
		href += entityRef;
		if (href1 != null) {
			href += href1;
		}
		return href;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFlag() {
		return flag;
	}

}
