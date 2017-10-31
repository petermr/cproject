package org.xmlcml.cproject.util;

import java.util.List;

import org.xmlcml.graphics.html.HtmlElement;
import org.xmlcml.graphics.html.HtmlTr;

public interface CellCalculator {

	void addCellValues(List<CellRenderer> columnHeadingList, HtmlTr htmlTr, int iRow);

	HtmlElement createCellContents(int iRow, int iCol);

}
