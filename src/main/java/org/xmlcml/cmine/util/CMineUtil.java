package org.xmlcml.cmine.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.xml.XMLUtil;

import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.google.common.collect.Multisets;

import nu.xom.Node;

/** mainly static tools.
 * 
 * @author pm286
 *
 */
public class CMineUtil {

	private static final Logger LOG = Logger.getLogger(CMineUtil.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String NEW_LINE_SEPARATOR = "\n";

	/** sort entrySet by count.
	 * convenience method.
	 * @param wordSet
	 * @return
	 */
	public static Iterable<Multiset.Entry<String>> getEntriesSortedByCount(Multiset<String> wordSet) {
		return Multisets.copyHighestCountFirst(wordSet).entrySet();
	}

	public static List<Multiset.Entry<String>> getEntryListSortedByCount(Multiset<String> wordSet) {
		return Lists.newArrayList(Multisets.copyHighestCountFirst(wordSet).entrySet());
	}

	public static Iterable<Entry<String>> getEntriesSortedByValue(Multiset<String> wordSet) {
		return  ImmutableSortedMultiset.copyOf(wordSet).entrySet();
	}
	
	/** extracts a list of attribute values.
	 * 
	 * @return
	 */
	public static List<String> getAttributeValues(Node searchNode, String xpath) {
		List<Node> nodes = XMLUtil.getQueryNodes(searchNode, xpath);
		List<String> nodeValues = new ArrayList<String>();
		for (Node node : nodes) {
			String value = node.getValue();
			if (value != null && value.trim().length() != 0) {
				nodeValues.add(value);
			}
		}
		return nodeValues;
	}

	/** Catch errors from. running ProcessBUilder with an uninstalled program
	 *  
	 * @param e
	 * @param programName to run , e.g. 'latexml' , 'tesseract'
	 */
	public static void catchUninstalledProgram(IOException e, String programName) {
		String error = e.getMessage();
		if (error.startsWith("Cannot run program \""+programName+"\": error=2")) {
			LOG.error("******** "+programName+" must be installed *************");
		} else {
			throw new RuntimeException("cannot convert file, ", e);
		}
	}

	public static Process runProcess(String[] args, InputStream inputStream) throws IOException {
	    List<String> argList = Arrays.asList(args);
		String program = argList.get(0);
		LOG.debug("ff "+new File(program).exists());
	    ProcessBuilder postBuilder = new ProcessBuilder(argList);
	    postBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
	    Process proc = null;        
	    try {
	        proc = postBuilder.start();
	        LOG.debug("Processing input with "+program);
	    } catch (IOException e) {
	    	CMineUtil.catchUninstalledProgram(e, program);
	    	return null;
	    }
	    OutputStream outputStream = proc.getOutputStream();
	    if (inputStream != null) {
	    	IOUtils.copy(inputStream, outputStream);
	    }
	    if (outputStream != null) {
	    	outputStream.close();
	    }
	    return proc;
	}

	/**
	 * 
	 * @param fileName
	 * @param csvHeaders list of headers ("row 1")
	 * @param valueListList
	 */
	public static void writeCSV(String fileName, List<String> csvHeaders, List<List<String>> valueListList) {
		FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
                 
        try {
        	File file = new File(fileName);
            fileWriter = new FileWriter(file);
            csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
            csvFilePrinter.printRecord(csvHeaders);
             
            for (List<String> record : valueListList) {
                csvFilePrinter.printRecord(record);
            }
        } catch (Exception e) {
            throw new RuntimeException("failed to write CSV", e);
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
                csvFilePrinter.close();
            } catch (IOException e) {
                throw new RuntimeException("failed to close/flush CSV", e);
            }
        }
	}
	
	public static Object getObjectForJsonPath(String json, String jsonPath) {
		ReadContext ctx = JsonPath.parse(json);
		Object result = ctx.read(jsonPath);
		return result;
	}

	/**
	 * 
	 * @param json
	 * @param jsonPath
	 * @return null if not found
	 */
	public static String getStringForJsonPath(String json, String jsonPath) {
		ReadContext ctx = JsonPath.parse(json);
		String result = null;
		try {
			result = ctx.read(jsonPath);
		} catch (Exception e) {
			// cannot find so returns null
		}
		return result;
	}

}
