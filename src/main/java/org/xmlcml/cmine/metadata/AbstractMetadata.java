package org.xmlcml.cmine.metadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.files.CTree;
import org.xmlcml.cmine.metadata.crossref.CrossrefMD;
import org.xmlcml.cmine.metadata.quickscrape.QuickscrapeMD;
import org.xmlcml.cmine.util.CMineUtil;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.xml.XMLUtil;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

import net.minidev.json.JSONArray;

/* manages metadata
 * 
 * @author pm286
 *
 */
public abstract class AbstractMetadata {

	/** fields
	 * 
	 */
	public static final String ABSTRACT        = "Abstract";
	public static final String AUTHOR_LIST     = "AuthorList";
	public static final String COPYRIGHT       = "Copyright";
	public static final String CROSSREF_MD     = "CrossrefMD";
	public static final String DATE            = "Date";
	public static final String DESCRIPTION     = "Description";
	public static final String DOI             = "DOI";
	public static final String DOWNLOADED_HTML = "HTMLFile";
	public static final String DOWNLOADED_PDF  = "PDFFile";
	public static final String DOWNLOADED_XML  = "XMLFile";
	public static final String FIRST_PAGE      = "FirstPage";
	public static final String HTMLURL         = "HTMLURL";
	public static final String ISSN            = "ISSN";
	public static final String ISSUE           = "Issue";
	public static final String JOURNAL         = "Journal";
	public static final String KEYWORDS        = "Keywords";
	public static final String LICENSE         = "License";
	public static final String LINKS           = "Links";
	public static final String PDFURL          = "PDFURL";
	public static final String PREFIX          = "Prefix";
	public static final String PUBLISHER       = "Publisher";
	public static final String PUBLISHER_MD    = "PublisherMD";
	public static final String QUICKSCRAPE_MD  = "QuickscrapeMD";
	public static final String TITLE           = "Title";
	public static final String TYPE            = "Type";
	public static final String URL             = "URL";
	public static final String VOLUME          = "Volume";
	public static final String XMLURL          = "XMLURL";

	public static final Logger LOG = Logger.getLogger(AbstractMetadata.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public enum Type {
		EPMC(CTree.EUPMC_RESULT_JSON),
		QUICKSCRAPE(CTree.RESULTS_JSON),
		CROSSREF(CrossrefMD.CROSSREF_RESULT_JSON)
		;
		private String cTreeFilename;

		private Type(String cTreeFilename) {
			this.cTreeFilename = cTreeFilename;
		}
		public String getJsonFilename() {
			return cTreeFilename;
		}
	}
	
	public static final String SCRAPER_ELEMENTS = "elements";

	static final String LOCAL_NAME = "//*[local-name()='";
	static final String META = "meta";
	static final String AND = " and (";
	static final String NAME_EQ_APOS = "@name='";
	static final String APOS = "'";
	static final String OR = " or ";
	static final String END = ") ]";
	static final String TRANSLATE = "translate(";
	static final String UPPER_LOWER = "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'";
	static final String TRANSLATE_NAME_EQ_APOS = TRANSLATE + "@name, " + UPPER_LOWER + ")="+APOS;

			
	public final static List<String> getDefaultHeaders() {
		return Arrays.asList(
			new String[] {
				URL,
				TITLE,
				DATE,
				PDFURL,
				DOWNLOADED_PDF,
				HTMLURL,
				DOWNLOADED_HTML,
				XMLURL,
				DOWNLOADED_XML,
				DOI,
				PUBLISHER,
				VOLUME,
				AUTHOR_LIST,
				TYPE,
				ISSUE,
				FIRST_PAGE,
				DESCRIPTION,
				ABSTRACT,
				JOURNAL,
				LICENSE,
				LINKS,
				COPYRIGHT,
				ISSN,
				KEYWORDS,
				QUICKSCRAPE_MD,
				CROSSREF_MD,
				PUBLISHER_MD,
				PREFIX,
			});
	}
	
	public static final List<String> COMMON_HEADERS = new ArrayList<String>(Arrays.asList(new String[] {
			AbstractMetadata.LICENSE,
			AbstractMetadata.TITLE,
			AbstractMetadata.DOI,
			AbstractMetadata.PUBLISHER,
			AbstractMetadata.PREFIX,
			AbstractMetadata.DATE,
			AbstractMetadata.KEYWORDS,
	}));

	

	private File jsonFile;
	private JsonElement jsonElement;
	private Multiset<String> allKeys;
	protected String currentKey;
	private Multiset<String> keysmap;
	private int maxLevel = 1;
	private Multimap<String, String> stringValueByKey;
	private JsonArray metadataArray;
	private JsonElement metadataJson;
	private List<JsonObject> metadataObjectList;
	private Multimap<String, String> numberValueByKey;
	protected CTree cTree;
	private HtmlElement fulltextXHtml;
	protected Multimap<String, String> stringValueMap;
	protected Multimap<String, List<String>> stringListValueMap;
	protected boolean hasQuickscrapeMetadata;
	protected boolean hasCrossrefMetadata;
	protected boolean hasPublisherMetadata;

	/**
ARRAY translator; [{"affiliation":[],"family":"Munder","given":"Marc"},{"affiliation":[],"family":"Hennion","given":"Antoine"}]
36082 [main] DEBUG org.xmlcml.cmine.metadata.crossref.CrossrefMD  - ARRAY chair; [{"affiliation":[],"family":"侯洵","given":"侯洵"}]	 */
	public AbstractMetadata() {
	}
	
	public static AbstractMetadata createMetadata(File jsonFile, AbstractMetadata.Type metadataType) {
		AbstractMetadata metadata = null;
		if (jsonFile != null && jsonFile.exists() && 
				CTree.JSON.equals(FilenameUtils.getExtension(jsonFile.getName()))) {
			try {
				JsonParser jsonParser = new JsonParser();
				JsonElement jsonElement = jsonParser.parse(FileUtils.readFileToString(jsonFile));
				if (Type.CROSSREF.equals(metadataType)) {
					metadata = CrossrefMD.createMetadata();
					metadata.analyzeElement(0, jsonElement);
				} else if (Type.QUICKSCRAPE.equals(metadataType)) {
					metadata = QuickscrapeMD.createMetadata();
					metadata.analyzeElement(0, jsonElement);
				} else {
					throw new RuntimeException("Unknown metadata type: "+metadataType);
				}
				metadata.setJsonFile(jsonFile);
				metadata.setJsonElement(jsonElement);
			} catch (JsonSyntaxException e) {
				throw new RuntimeException("Json syntax error in "+jsonFile, e);
			} catch (IOException e) {
				throw new RuntimeException("Cannot read file "+jsonFile, e);
			}
		}
		return metadata;
	}
	
	public void setJsonElement(JsonElement jsonElement) {
		this.jsonElement = jsonElement;
	}

	public JsonElement getJsonElement() {
		return jsonElement;
	}

	public void setJsonFile(File jsonFile) {
		this.jsonFile = jsonFile;
	}

	public File getJsonFile() {
		return jsonFile;
	}

	public Object getJsonObjectByPath(String jsonPath) {
		Object object = null;
		if (jsonPath != null && jsonElement != null) {
			object = CMineUtil.getObjectForJsonPath(jsonElement.toString(), jsonPath);
		}
		return object;
	}
	
	public String getJsonStringByPath(String jsonPath) {
		String value = null;
		if (jsonPath != null && jsonElement != null) {
			value = CMineUtil.getStringForJsonPath(jsonElement.toString(), jsonPath);
		}
		return value;
	}
	
	public String getJsonArrayStringByPath(String jsonPath) {
		String value = null;
		if (jsonPath != null && jsonElement != null) {
			JSONArray array = (JSONArray) CMineUtil.getObjectForJsonPath(jsonElement.toString(), jsonPath);
			value = array == null || array.size() == 0 ? null : array.get(0).toString();
		}
		return value;
	}
	
	public List<String> getJsonArrayByPath(String jsonPath) {
		List<String> values = null;
		if (jsonPath != null && jsonElement != null) {
			JSONArray array = (JSONArray) CMineUtil.getObjectForJsonPath(jsonElement.toString(), jsonPath);
			// because I don't understand the JSONArray object
			values = (List<String>) array.clone();
		}
		return values;
	}
	
	public String getJsonMapStringByPath(String jsonPath) {
		String value = null;
		if (jsonPath != null && jsonElement != null) {
			Map<String, String> map = (Map<String, String>) CMineUtil.getObjectForJsonPath(jsonElement.toString(), jsonPath);
		}
		return value;
	}
	
	public Set<String> extractKeys() {
		Set<String> keys = new HashSet<String>();
		Set<Map.Entry<String, JsonElement>> entrySet = jsonElement.getAsJsonObject().entrySet();
		for (Map.Entry<String, JsonElement> map : entrySet) {
			String key = map.getKey();
			keys.add(key);
		}
		return keys;
	}

	private void addKey(int level, String key) {
		if (level <= maxLevel ) {
			if (keysmap == null) {
				keysmap = HashMultiset.create();
			}
			keysmap.add(key);
		}
	}

	private void addKeyValue(String currentKey, Number value) {
		if (numberValueByKey == null) {
			numberValueByKey = ArrayListMultimap.create();
		}
		numberValueByKey.put(currentKey, value.toString());
	}

	private void addKeyValue(String currentKey, String value) {
		if (stringValueByKey == null) {
			stringValueByKey = ArrayListMultimap.create();
		}
		stringValueByKey.put(currentKey, value);
	}

	private JsonObject analyzeObject(int level, JsonObject obj) {
		Set<Map.Entry<String, JsonElement>> set = obj.entrySet();
		ensureAllKeys();
		for (Map.Entry<String, JsonElement> entry : set) {
			currentKey = entry.getKey();
			JsonElement value = entry.getValue();
//			System.out.println(getPrompt(level)+currentKey);
			allKeys.add(currentKey);
			addKey(level, currentKey);
			if (analyzeSpecificObject(value)) {
			} else {
				analyzeElement(level+1, value);
			}
		}
		return obj;
	}

	public boolean analyzeSpecificObject(JsonElement value) {
		return false;
	}

	private Object analyzePrimitive(JsonPrimitive prim) {
		Object obj = JsonUtils.getNumber(prim);
		if (obj == null) {
			obj = JsonUtils.getString(prim);
			if (obj != null) {
				addKeyValue(currentKey, (String)obj);
			}
		} else {
			addKeyValue(currentKey, (Number)obj);
		}
		return obj;
	}

	/** 
	prefix x 1552, doi S
	deposited x 1552, DATE-PARTS
	source x 1552, crossref S
	type x 1552, journal-article S
	title x 1552, S A1
	URL x 1552, S
	score x 1552, N
	member x 1552, S crossref
	reference-count x 1552, L
	issued x 1552, DATE-PARTS
	DOI x 1552, S
	indexed x 1552, DATE-PARTS
	created x 1552, DATE-PARTS A[1,3]
	container-title x 1552, A1 S
	subtitle x 1552, An
	publisher x 1552, 
	ISSN x 1445, A2 S
	author x 1209, An PERSON
	page x 1109, S
	published-print x 1060, DATE-TIME
	published-online x 1051, DATE-TIME
	volume x 1036, S
	issue x 875, S
	subject x 777, An S
	license x 706, A1 OBJ
	alternative-id x 700, A1 S
	link x 666, An LINK
	update-policy x 227, S
	ISBN x 172, A
	archive x 163, A1 S
	assertion x 146, An OBJ
	funder x 126, An OBJ
	article-number x 36, S
	editor x 12, An PERSON
	update-to x 8 A OBJ
	*/
	
	private Object analyzeArray(int level, JsonArray array) {
		Object object = null;
		if (array.size() == 0) {
//			System.out.println(getPrompt(level)+"emptyArray");
			return null;
		} else if (array.size() == 1) {
			JsonElement array0 = array.get(0);
			if (array0.isJsonPrimitive()) {
				object = analyzePrimitive(array0.getAsJsonPrimitive());
			} else if (array0.isJsonArray()) {
				object = analyzeArray(level+1, array0.getAsJsonArray());
			} else if (array0.isJsonObject()) {
				object = analyzeObject(level+1, array0.getAsJsonObject());
			}
//			System.out.println(getPrompt(level)+"1-element "+object);
		} else {
			JsonElement array0 = array.get(0);
			List<Object> list = null;
			Class clazz = array0.getClass();
			if (array0.isJsonPrimitive()) {
				list = new ArrayList<Object>();
			}
			for (int i = 0; i < array.size(); i++) {
				JsonElement arrayi = array.get(i);
				Class clazzi = arrayi.getClass();
				if (!clazzi.equals(clazz)) {
					throw new RuntimeException("inconsistent class in array("+i+"): "+clazzi+" != "+clazz);
				}
				if (array0.isJsonPrimitive()) {
					Object obj = analyzePrimitive(arrayi.getAsJsonPrimitive());
					list.add(obj);
				} else if (array0.isJsonArray()) {
					analyzeArray(level+1, array0.getAsJsonArray());
				} else if (array0.isJsonObject()) {
					analyzeObject(level+1, array0.getAsJsonObject());
				}
			}
			object = list;
//			System.out.println(getPrompt(level)+"Array size "+array.size()+" "+object);
		}
		return object;
	}

	public JsonElement analyzeElement(int level, JsonElement elem) {
			JsonElement element1 = null;
			if (elem == null) {
			} else if (elem.isJsonPrimitive()) {
				JsonPrimitive prim = elem.getAsJsonPrimitive();
				analyzePrimitive(prim);
				element1 = prim;
			} else if (elem.isJsonArray()) {
				JsonArray array = elem.getAsJsonArray();
				analyzeArray(level+1, array);
				element1 = array;
			} else if (elem.isJsonObject()) {
				JsonObject obj = elem.getAsJsonObject();
				analyzeObject(level+1, obj);
				element1 = obj;
			} else {
				LOG.warn("unknown element "+elem);
			}
	//		System.out.println("type "+element1.getClass().getSimpleName());
			return element1;
		}

	public void getOrCreateMetadataList() {
		if (metadataObjectList == null) {
			metadataObjectList = getMetadataObjectListFromConcatenatedArray();
			for (JsonElement elem : metadataObjectList) {
				this.analyzeElement(0, elem);
			}
		}
	}

	public void debugNumberValues() {
		Multimap<String, String> map = getNumberValueByKey();
		debugStringMap(map);
	}

	private void debugStringMap(Multimap<String, String> map) {
		if (map != null) {
			for (String key : map.keySet()) {
				List<String> ss = new ArrayList<String>(map.get(key));
				Multiset<String> mset = HashMultiset.create();
				mset.addAll(ss);
				List<Multiset.Entry<String>> map1 = CMineUtil.getEntryListSortedByCount(mset);
				if (map1.get(0).getCount() > 10) {
					System.out.println(key+": "+map1.subList(0, Math.min(10, map1.size())));
				} else {
					System.out.println(key+": "+map1.size());
				}
			}
		}
	}

	public void debugStringValues() {
		Multimap<String, String> map = getStringValueByKey();
		if (map != null) {
			debugStringMap(map);
		}
	}

	private void ensureAllKeys() {
		if (allKeys == null) {
			allKeys = HashMultiset.create();
		}
	}

	public Multiset<String> getAllKeys() {
		return allKeys;
	}

	public Multiset<String> getKeysmap() {
		return keysmap;
	}

	public List<JsonObject> getMetadataObjectListFromConcatenatedArray() {
		metadataObjectList = new ArrayList<JsonObject>();
		for (JsonElement element : metadataArray) {
			JsonObject jsonMetadataObject = element.getAsJsonObject();
			metadataObjectList.add(jsonMetadataObject);
		}
		return metadataObjectList;
	}

	public Multimap<String, String> getNumberValueByKey() {
		return numberValueByKey;
	}

	private String getPrompt(int level) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < level; i++) {
			sb.append(">");
		}
		sb.append(" ");
		return sb.toString();
	}

	public Multimap<String, String> getStringValueByKey() {
		return stringValueByKey;
	}

	public JsonElement readMetadataJson(File jsonFile) throws IOException {
		metadataJson = new JsonParser().parse(FileUtils.readFileToString(jsonFile));
		return metadataJson;
	}

	public JsonElement readMetadataJsonFromSingleFile(File jsonFile) throws IOException {
		readMetadataJson(jsonFile);
		metadataArray = null;
		return metadataJson;
	}

	public JsonArray readMetadataArrayFromConcatenatedFile(File jsonFile) throws IOException {
		readMetadataJson(jsonFile);
		metadataArray = metadataJson.getAsJsonArray();
		return metadataArray;
	}

	public static AbstractMetadata getMetadata(CTree cTree, AbstractMetadata.Type sourceType) {
		String jsonType = sourceType.getJsonFilename();
		File jsonFile = cTree.getAllowedChildFile(jsonType);
		AbstractMetadata metadata = jsonFile == null ? null : AbstractMetadata.createMetadata(jsonFile, sourceType);
		return metadata;
	}
	
	public String hasDownloadedFulltextPDF() {
		return cTree != null && cTree.hasFulltextPDF() ? "Y" : "N";
	}

	public String hasDownloadedFulltextHTML() {
		return cTree != null && cTree.hasFulltextHTML() ? "Y" : "N";
	}

	public String hasDownloadedFulltextXML() {
		return cTree != null && cTree.hasExistingFulltextXML() ? "Y" : "N";
	}

	public String getAbstract() {return "";}
	/**
	citation_abstract_html_url x 320
	 */
	public String getAbstractURL() {return "";}
	/**
	citation_reference x 1349
	 */
	/**
	citation_author_email x 191
	*/
	public String getAuthorEmail() {return "";}
	/**
	 citation_author_institution x 1114
	 */
	public String getAuthorInstitution() {return "";}
	/**
	citation_author x 1383
	DC.Contributor x 330
	DC.creator x 23
	citation_authors x 22
	*/
	public List<String> getAuthorListAsStrings() {return new ArrayList<String>();}
	public String getCitations() {return "";}
	public String getCopyright() {return "";}
	/**
	dc.creator x 202
	DC.Creator x 118
	 */
	public String getCreator() {return "";}
	/**
	citation_online_date x 233
	DC.Date x 114
	citation_date x 105
	citation_publication_date x 96
	citation_cover_date x 56
	dc.date x 49
	prism.publicationDate x 28
	dc.dateSubmitted x 20
	dc.dateAccepted x 20
	citation_year x 24
	 */
	public String getDate() {return "";}
	/**
	description x 93
	DC.description x 22
	dc.description x 17
	 */
	public String getDescription() {return "";}
	/**
	citation_doi x 361
	dc.identifier x 173
	DC.Identifier x 126
	citation_id x 110
	HW.identifier x 86
	DC.identifier x 30
	 */
	public String getDOI() {return "";}
	/**
	citation_firstpage x 344
	prism.startingPage x 19
	 */
	public String getFirstPage() {return "";}
	/**
	citation_lastpage x 264
	 */
	public String getLastPage() {return "";}
//	citation_springer_api_url x 117
	/**
	citation_fulltext_html_url x 396
	 */
	public String getFulltextHTMLURL() {return "";}
	/**
	citation_pdf_url x 376
	 */
	public String getFulltextPDFURL() {return "";}
	/**
	citation_public_url x 87
	 */
	public String getFulltextPublicURL() {return "";}
	public String getFulltextXMLURL() {return "";}
	/**
	citation_issn x 547
	prism.issn x 19
	 */
	public String getISSN() {return "";}
	/**
	citation_issue x 236
	 */
	public String getIssue() {return "";}
	/**
	citation_journal_title x 387
	citation_journal_abbrev x 213
	prism.publicationName x 29
	 */
	public String getKeywords() {return "";}
	/**
	citation_keywords x 157
	dc.subject x 67
	DC.subject x 26
	DC.Subject x 17
	 */
	public String getJournal() {return "";}
	public String getLicense() {return "";}
	/**
	citation_language x 238
	DC.Language x 119
	dc.language x 49
	 */
	public String getLanguage() {return "";}
	/**
	 */
	public String getLinks() {return "";}
	/**
	citation_public_url x 87
	 */
	public String getPrefix() {return "";}
	public String getPublicURL() {return "";}
	/**
	citation_publisher x 221
	DC.Publisher x 94
	dc.publisher x 49
	DC.publisher x 31
	 */
	public String getPublisher() {return "";}
	public String getReferenceCount() {return "";}
	/**
	dc.rights x 221
	 */
	public String getRights() {return "";}
	/**
	citation_title x 387
	DC.Title x 98
	dc.title x 49
	DC.title x 32
	 */
	public String getTitle() {return "";}
	
	public String getType() {return "";}
	
	public String getURL() {return "";}
	/**
	citation_volume x 254
	prism.volume x 19
	 */
	public String getVolume() {return "";}


	public void setCTree(CTree cTree) {
		this.cTree = cTree;
	}

	protected String getJsonValueOrHtmlMetaContent(String jsonPath, String[] xpathNames) {
		String value = getJsonArrayStringByPath(jsonPath);
		if (value == null) {
			fulltextXHtml = cTree.getOrCreateFulltextXHtml();
			if (fulltextXHtml != null) {
				value = searchWithXPath(xpathNames);
			}
		}
		return value;
	}

	String searchWithXPath(String[] names) {
		String xpath = LOCAL_NAME + META + APOS+ AND + TRANSLATE_NAME_EQ_APOS + names[0] + APOS;
		for (int i = 1; i < names.length; i++) {
			xpath += OR + TRANSLATE_NAME_EQ_APOS + names[i] + APOS;
		}
		xpath += END;
		xpath += "/@content";
//		LOG.debug(xpath);
		String result = XMLUtil.getSingleValue(fulltextXHtml, xpath);
		return result;
	}

	public Set<String> getStringKeySet() {
		Set<String> keys = new HashSet<String>();
		if (stringValueMap != null) {
			keys = stringValueMap.keySet();
		}
		return keys;
	}

	public Set<String> getStringListKeySet() {
		Set<String> keys = new HashSet<String>();
		if (stringListValueMap != null) {
			keys = stringListValueMap.keySet();
		}
		return keys;
	}

	/** gets project+cTree names
	 * 
	 * @param cTree2
	 * @return
	 */
	public String getProjectCTreeName() {
		String filename = null;
		if (cTree != null) {
			File cTreeDirectory = cTree.getDirectory();
			if (cTreeDirectory != null) {
				File projectDirectory = cTreeDirectory.getParentFile();
				filename = projectDirectory.getName()+"/"+cTreeDirectory.getName();
			}
		}
		return filename;
	}

	public String hasCrossrefMetadata() {
		return hasCrossrefMetadata ? "Y" : "N";
	}

	public String hasPublisherMetadata() {
		return hasPublisherMetadata ? "Y" : "N";
	}

	public String hasQuickscrapeMetadata() {
		return hasQuickscrapeMetadata  ? "Y" : "N";
	}


}
