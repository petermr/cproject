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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlHtml;
import org.xmlcml.xml.XMLUtil;

import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import nu.xom.Comment;
import nu.xom.Node;

/** mainly static tools.
 * 
 * @author pm286
 *
 */
public class CMineUtil {

	private static final String HTML_START = "<";
	private static final String PDF_START = "%PDF";
	private static final String URL_PUNCT = "[\\/\\$\\%\\*\\(\\)\\[\\]]";
	private static final String HTTP_DX_DOI_ORG = "http_dx\\.doi\\.org_?";
//	                                               http_dx.doi.org
	private static final Logger LOG = Logger.getLogger(CMineUtil.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String NEW_LINE_SEPARATOR = "\n";
	public static final String HTML_TYPE = "text/html";
	public static final String PDF_TYPE = "text/pdf";

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
		Object result = null;
		try {
			result = ctx.read(jsonPath);
		} catch (Exception e) {
			LOG.trace("bad path: "+jsonPath);
		}
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

	/** normalizes files based on DOIs.
	 * 
	 * removes "http_dx_doi_org_" and converts punctuation to "_"
	 * 
	 * @param doi
	 * @return
	 */
	public final static File normalizeDOIBasedFile(File file) {
		File normalizedFile = null;
		if (file != null) {
			String name = file.getName();
			String name1 = normalizeDOIBasedFilename(name);
			normalizedFile = new File(file.getParentFile(), name1);
		}
		return normalizedFile;
	}
	
	/** normalizes filenames based on DOIs.
	 * 
	 * removes "http_dx_doi_org_" and converts punctuation to "_"
	 * 
	 * @param doi
	 * @return
	 */
	public final static String normalizeDOIBasedFilename(String doi) {
		// http_dx.doi.org_10.1002_suco.201500193
		String doi1 = doi.replaceAll(HTTP_DX_DOI_ORG, "");
		doi1 = doi1.replaceAll(URL_PUNCT, "_");
		return doi1;
	}

	/**
	 * flattens 
	 * http:/dx.doi.org/10.1051/ro/2016009
	 * to
	 * http_dx.doi.org_10.1051_ro_2016009
	 * 
	 * @param doi
	 * @return
	 */
	public final static String denormalizeDOI(String doi) {
		String doi1 = doi.replaceAll("http://?", "http_");
		doi1 = doi1.replaceAll("/", "_");
		return doi1;
	}

	public static HtmlElement createEmptyHTMLWthComment(String comment) {
		HtmlHtml html = new HtmlHtml();
		html.appendChild(new Comment(comment));
		return html;
	}

	/** reads file and determines type from start
	 * make take time for large files
	 * @param file
	 * @return
	 */
	public static String getTypeOfContent(File file) {
		String contentType = null;
		try {
			String content = FileUtils.readFileToString(file);
			if (content == null) {
				
			} else if (content.startsWith(PDF_START)) {
				contentType = CMineUtil.PDF_TYPE; 
			} else if (content.trim().startsWith(HTML_START)) {
				contentType = CMineUtil.HTML_TYPE; 
			}
		} catch (IOException e) {
			LOG.error("Cannot read file: "+e);
		}
		return contentType;
	}
}
