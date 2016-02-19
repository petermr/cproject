package org.xmlcml.cmine.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.html.HtmlA;
import org.xmlcml.html.HtmlBody;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlHead;
import org.xmlcml.html.HtmlHtml;
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
	
	private static final String RESULTS = "results";
	private static final String DEFAULTS = 
			    DataTablesTool.TABLE+
			" "+DataTablesTool.TABLE_STRIPED+
			" "+DataTablesTool.TABLE_BORDERED+
			" "+DataTablesTool.TABLE_HOVER;
	private static final String BS_EXAMPLE_TABLE_RESPONSIVE = "bs-example table-responsive";

	private String title;
	private String tableId; // HTML ID of table element

	public List<CellRenderer> columnHeadingList;
	private List<String> rowHeadingList;
	private String rowHeadingName;
	private CellCalculator cellCalculator;
	private String link0;
	private String link1;

	public DataTablesTool() {
		this.setTableId(RESULTS);
	}

	public DataTablesTool(CellCalculator cellCalculator) {
		this();
		this.setCellCalculator(cellCalculator);
	}
	
	public void setCellCalculator(CellCalculator cellCalculator) {
		this.cellCalculator = cellCalculator;
	}

	public HtmlHead makeDataTableHead() {
		HtmlHead head = new HtmlHead();
		head.addUTF8Charset();
		head.addTitle(title);
		head.addCSSStylesheetLink(JQUERY_DATA_TABLES_CSS);
		head.addJavascriptLink(JQUERY_1_8_2_MIN_JS);
		head.addJavascriptLink(JQUERY_DATA_TABLES_MIN_JS);
		head.addJavascript(DATA_TABLE_FUNCTION0 + tableId + DATA_TABLE_FUNCTION1);
		return head;
	}

	public HtmlTd createHyperlinkedCell(String href, String aValue) {
		HtmlTd htmlTd = new HtmlTd();
		HtmlA htmlA = new HtmlA();
		htmlA.appendChild(aValue);
		htmlA.setHref(href);
		htmlTd.appendChild(htmlA);
		return htmlTd;
	}

	public HtmlThead createHtmlHead() {
		HtmlThead htmlThead = new HtmlThead();
		HtmlTr htmlTr = new HtmlTr();
		htmlThead.appendChild(htmlTr);
		htmlTr.appendChild(createColumnHeading(this.getId()));
		addRemainingColumnHeadings(htmlTr);
		return htmlThead;
	}

	private HtmlTh createColumnHeading(String id) {
		HtmlTh htmlTh = new HtmlTh();
		htmlTh.appendChild(id);
		return htmlTh;
	}

	private void addRemainingColumnHeadings(HtmlTr htmlTr) {
		
		for (CellRenderer renderer : columnHeadingList) {
			if (renderer.isVisible()) {
				HtmlTh htmlTh = new HtmlTh();
				htmlTr.appendChild(htmlTh);
//				htmlTh.appendChild(renderer.getHtmlElement());
				htmlTh.appendChild(renderer.getFlag());
			}
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTableId(String id) {
		this.tableId = id;
	}
	
	public String getId() {
		return tableId;
	}

	public void setRowHeadingList(List<String> rowHeadingList) {
		this.rowHeadingList = rowHeadingList;
	}

	public List<String> getOrCreateRowHeadingList() {
		if (rowHeadingList == null) {
			rowHeadingList = new ArrayList<String>();
		}
		return rowHeadingList;
	}

	public void setColumnHeadingList(List<CellRenderer> columnHeadingList) {
		this.columnHeadingList = columnHeadingList;
	}

	public List<CellRenderer> getOrCreateColumnHeadingList() {
		if (columnHeadingList == null) {
			columnHeadingList = new ArrayList<CellRenderer>();
		}
		return columnHeadingList;
	}

	/** this calls addCellValues(htmlTr, rowHeading) which includes ResultsAnalysis logic.
	 * 
	 * @param cellCalculator TODO
	 * @param link0
	 * @param link1
	 * @param htmlTbody
	 */
	public void addRows(HtmlTbody htmlTbody) {
		for (int iRow = 0; iRow < rowHeadingList.size(); iRow++) {
			String rowHeading = rowHeadingList.get(iRow);
			HtmlTr htmlTr = new HtmlTr();
			htmlTbody.appendChild(htmlTr);
			String href = link0 + rowHeading + link1;
			HtmlTd htmlTd = createHyperlinkedCell(href, rowHeading);
			htmlTr.appendChild(htmlTd);
			cellCalculator.addCellValues(columnHeadingList, htmlTr, iRow);
		}
	}

	public void addCellValuesToRow(HtmlTr htmlTr, int iRow) {
		for (int iCol = 0; iCol < columnHeadingList.size(); iCol++) {
			HtmlTd htmlTd = new HtmlTd();
			htmlTr.appendChild(htmlTd);
			String contents = cellCalculator.createCellContents(iRow, iCol);
			contents = contents == null ? "" : contents;
			htmlTd.appendChild(contents);
		}
	}

	public HtmlTable createHtmlDataTable() {
		HtmlTable htmlTable = new HtmlTable();
		htmlTable.appendChild(createHtmlHead());
		HtmlTbody htmlTbody = new HtmlTbody();
		htmlTable.appendChild(htmlTbody);
		addRows(htmlTbody);
		return htmlTable;
	}

	public HtmlHtml createHtmlWithDataTable(HtmlTable table) {
		HtmlHtml html = new HtmlHtml();
		HtmlHead head = makeDataTableHead();
		html.appendChild(head);
		HtmlBody body = new HtmlBody();
		html.appendChild(body);
		HtmlDiv htmlDiv = new HtmlDiv();
		htmlDiv.setClassAttribute(BS_EXAMPLE_TABLE_RESPONSIVE);
		body.appendChild(htmlDiv);
		htmlDiv.appendChild(table);
		return html;
	}

	public void setRowHeadingName(String rowHeadingName) {
		this.rowHeadingName = rowHeadingName;
	}

	public void setLink0(String link0) {
		this.link0 = link0;
	}

	public String getLink0() {
		return link0;
	}
	
	public void setLink1(String link1) {
		this.link1 = link1;
	}

	public String getLink1() {
		return link1;
	}

	public HtmlHtml createHtml(CellCalculator cellCalculator) {
		HtmlTable htmlTable = createHtmlDataTable();
		htmlTable.setClassAttribute(DataTablesTool.DEFAULTS);
		htmlTable.setId(tableId);
		HtmlHtml html = createHtmlWithDataTable(htmlTable);
		return html;
	}
	
}
