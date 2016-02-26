package org.xmlcml.cmine.util;

import java.io.File;
import java.util.List;

import org.xmlcml.html.HtmlTr;

public interface CellCalculator {

	void addCellValues(List<CellRenderer> columnHeadingList, HtmlTr htmlTr, int iRow);

	String createCellContents(int iRow, int iCol);

}
