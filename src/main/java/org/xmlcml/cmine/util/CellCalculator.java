package org.xmlcml.cmine.util;

import java.util.List;

import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlTr;

public interface CellCalculator {

	void addCellValues(List<CellRenderer> columnHeadingList, HtmlTr htmlTr, int iRow);

	HtmlElement createCellContents(int iRow, int iCol);

}
