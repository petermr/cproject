package org.xmlcml.cmine.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.files.PluginOption;
import org.xmlcml.cmine.files.ProjectSnippetsTree;
import org.xmlcml.cmine.files.SnippetsTree;
import org.xmlcml.html.HtmlA;
import org.xmlcml.html.HtmlHead;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.html.HtmlTbody;
import org.xmlcml.html.HtmlTd;
import org.xmlcml.html.HtmlTh;
import org.xmlcml.html.HtmlThead;
import org.xmlcml.html.HtmlTr;

public class DataTablesTool {

	private static final Logger LOG = Logger.getLogger(DataTablesTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String JQUERY_DATA_TABLES_MIN_JS = "http://ajax.aspnetcdn.com/ajax/jquery.dataTables/1.9.4/jquery.dataTables.min.js";
	public static final String JQUERY_1_8_2_MIN_JS = "http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.8.2.min.js";
	public static final String JQUERY_DATA_TABLES_CSS = "http://ajax.aspnetcdn.com/ajax/jquery.dataTables/1.9.4/css/jquery.dataTables.css";
	public final static String DATA_TABLE_FUNCTION0 = ""
	+ "$(function(){\n"
	+ "$(\"#";
	public final static String DATA_TABLE_FUNCTION1 = ""
	+ "\").dataTable();\n"
	+ "})\n";	
	public static final String TABLE = "table";
	public static final String TABLE_STRIPED = "table-striped";
	public static final String TABLE_BORDERED = "table-bordered";
	public static final String TABLE_HOVER = "table-hover";


	private HtmlThead htmlThead;
	private String title;
	private String id;

	public List<String> columnHeadingList;
	private List<String> rowHeadingList;

	public DataTablesTool() {
		
	}
	
	public DataTablesTool(String title, String id) {
		this();
		setTitle(title);
		setId(id);
	}

	public HtmlHead makeDataTableHead() {
		HtmlHead head = new HtmlHead();
		head.addUTF8Charset();
		head.addTitle(title);
		head.addCSSStylesheetLink(JQUERY_DATA_TABLES_CSS);
		head.addJavascriptLink(JQUERY_1_8_2_MIN_JS);
		head.addJavascriptLink(JQUERY_DATA_TABLES_MIN_JS);
		head.addJavascript(DATA_TABLE_FUNCTION0 + id + DATA_TABLE_FUNCTION1);
		return head;
	}

	public void addHyperlinkedIDCell(String href, String id, HtmlTr htmlTr) {
		HtmlTd htmlTd = new HtmlTd();
		htmlTr.appendChild(htmlTd);
		HtmlA htmlA = new HtmlA();
		htmlA.appendChild(id);
		htmlA.setHref(href);
		htmlTd.appendChild(htmlA);
	}

	public HtmlThead createHtmlHead(String id) {
		htmlThead = new HtmlThead();
		HtmlTr htmlTr = new HtmlTr();
		htmlThead.appendChild(htmlTr);
		addIDColumnHeading(id, htmlTr);
		addRemainingColumnHeadings(htmlTr);
		return htmlThead;
	}

	private void addIDColumnHeading(String id, HtmlTr htmlTr) {
		HtmlTh htmlTh = new HtmlTh();
		htmlTr.appendChild(htmlTh);
		htmlTh.appendChild(id);
	}

	private void addRemainingColumnHeadings(HtmlTr htmlTr) {
		
//		for (PluginOption pluginOption : pluginOptionList) {
		for (String heading : columnHeadingList) {
			HtmlTh htmlTh = new HtmlTh();
			htmlTr.appendChild(htmlTh);
			htmlTh.appendChild(heading);
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setRowHeading(List<String> rowHeadingList) {
		this.rowHeadingList = rowHeadingList;
	}

	public List<String> getRowHeadingList() {
		return rowHeadingList;
	}

	public void setColumnHeadingList(List<String> columnHeadingList) {
		this.columnHeadingList = columnHeadingList;
	}

	public List<String> getColumnHeadingList() {
		return columnHeadingList;
	}


}
