package org.xmlcml.cmine.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

public class CSVWriter {

	private static final Logger LOG = Logger.getLogger(CSVWriter.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String NEW_LINE_SEPARATOR = "\n";
	private List<String> header;
	private String filename;
	private List<List<String>> rows;
	
	public CSVWriter() {
	}
	
    public List<String> getHeader() {
		return header;
	}

	public void setHeader(List<String> header) {
		this.header = header;
	}

	public String getFileName() {
		return filename;
	}

	public void setFileName(String fileName) {
		this.filename = fileName;
	}

	public List<List<String>> getRows() {
		return rows;
	}

	public void setRows(List<List<String>> rows) {
		this.rows = rows;
	}

	public void writeCsvFile(String filename) throws IOException {
		this.filename = filename;
		writeCsvFile();
	}

	public void writeCsvFile() throws IOException {
		if (filename != null) {
	        File file = new File(filename);
	        file.getParentFile().mkdirs();
	        FileWriter fileWriter = new FileWriter(file);
	        CSVPrinter csvFilePrinter = 
	        		new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR));
	        if (header != null) {
	        	csvFilePrinter.print(header);
	        }
	        if (rows != null) {
		        for (int i = 0; i < rows.size(); i++) {
		            csvFilePrinter.printRecord(rows.get(i));
		        }
	        }
	        fileWriter.flush();
	        fileWriter.close();
	        csvFilePrinter.close();
		}
    }

	public void createMultisetAndOutputRowsWithCounts(List<String> values, String filename) throws IOException {
		Multiset<String> set = HashMultiset.create();
		set.addAll(values);
		List<Entry<String>> sortedEntryList = CMineUtil.getEntryListSortedByCount(set);
		List<List<String>> rows = new ArrayList<List<String>>();
		for (Entry<String> entry : sortedEntryList) {
			List<String> row = new ArrayList<String>();
			row.add(String.valueOf(entry.getElement()));
			row.add(String.valueOf(entry.getCount()));
			rows.add(row);
		}
		setFileName(filename);
		setRows(rows);
		writeCsvFile();
	}

	public void addRow(List<String> row) {
		if (rows == null) {
			rows = new ArrayList<List<String>>();
		}
		rows.add(row);
	}
}
