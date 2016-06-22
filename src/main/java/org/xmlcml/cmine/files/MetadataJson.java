package org.xmlcml.cmine.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.util.CMineUtil;
import org.xmlcml.cmine.util.CSVWriter;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.minidev.json.JSONArray;

/** tyical metadata from crossRef:
 * 
 * {
  "indexed": {
    "date-parts": [
      [
        2016,
        6,
        2
      ]
    ],
    "date-time": "2016-06-02T04:40:53Z",
    "timestamp": 1464842453796
  },
  "reference-count": 0,
  "publisher": "SAGE Publications",
  "DOI": "10.1177/1933719116651150",
  "type": "journal-article",
  "created": {
    "date-parts": [
      [
        2016,
        6,
        2
      ]
    ],
    "date-time": "2016-06-02T03:39:34Z",
    "timestamp": 1464838774000
  },
  "source": "CrossRef",
  "title": [
    "The Emerging Role of FOXL2 in Regulating the Transcriptional Activation Function of Estrogen Receptor  : An Insight Into Ovarian Folliculogenesis"
  ],
  "prefix": "http://id.crossref.org/prefix/10.1177",
  "author": [
    {
      "affiliation": [],
      "family": "Hirano",
      "given": "M."
    },
	...
  ],
  "member": "http://id.crossref.org/member/179",
  "published-online": {
    "date-parts": [
      [
        2016,
        6,
        1
      ]
    ]
  },
  "container-title": [
    "Reproductive Sciences"
  ],
  "deposited": {
    "date-parts": [
      [
        2016,
        6,
        2
      ]
    ],
    "date-time": "2016-06-02T03:39:34Z",
    "timestamp": 1464838774000
  },
  "score": 1,
  "subtitle": [],
  "issued": {
    "date-parts": [
      [
        2016,
        6,
        1
      ]
    ]
  },
  "URL": "http://dx.doi.org/10.1177/1933719116651150",
  "ISSN": [
    "1933-7191",
    "1933-7205"
  ],
  "subject": [
    "Obstetrics and Gynaecology"
  ]
}
 * 
 * @author pm286
 *
 */
public class MetadataJson {

	private static final Logger LOG = Logger.getLogger(MetadataJson.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	/** main list of metadata:
	 * [funder x 1231, prefix x 18556, deposited x 18556, subject x 7290, link x 3943, source x 18556, type x 18556,
	 *  title x 18556, URL x 18556, score x 18556, ISBN x 2643, member x 18556, reference-count x 18556, assertion x 1211,
	 *  published-online x 9981, issued x 18556, article-number x 354, clinical-trial-number, DOI x 18556, alternative-id x 8629, 
	 *  editor x 133, issue x 9941, indexed x 18556, created x 18556, author x 14887, ISSN x 15402, archive x 893, 
	 *  update-to x 27, volume x 11298, license x 4503, published-print x 12927, update-policy x 2349, container-title x 18556, 
	 *  subtitle x 18556, publisher x 18556, page x 11863]

	 */

	public static final String JOURNAL_ARTICLE = "journal-article";
	
	/** CrossRef JSON keys
	 * 
	 */
	public static final String CROSSREF_DOI = "$.DOI";
	public static final String CROSSREF_INDEXED_DATE_TIME = "$.indexed.date-time";
	public static final String CROSSREF_PUBLISHER = "$.publisher";
	public static final String CROSSREF_SOURCE = "$.source";
	public static final String CROSSREF_TITLE = "$.title";
	public static final String CROSSREF_TYPE = "$.type";
	public static final String CROSSREF_URL = "$.URL";
	
	/**
	/**
{
  "fulltext_pdf": {
    "value": [
      "http://archneur.jamanetwork.com/data/Journals/NEUR/15965/archneur_v40_n13_p784.pdf"
    ]
  },
  "fulltext_html": {
    "value": []
  },
  "title": {
    "value": [
      "Incorrect Table Entries and Word"
    ]
  },
  "author": {
    "value": []
  },
  "date": {
    "value": []
  },
  "doi": {
    "value": [
      "10.1001/archneur.40.13.784"
    ]
  },
  "volume": {
    "value": [
      "40"
    ]
  },
  "issue": {
    "value": [
      "13"
    ]
  },
  "firstpage": {
    "value": [
      "784"
    ]
  },
  "description": {
    "value": [
      "Other from JAMA Neurology — Incorrect Table Entries and Word"
    ]
  }
}
	 */
	
	/** quickscrape JSON keys
	 * 
fulltext_pdf x 33
date x 31
fulltext_html x 31
doi x 31
title x 31
publisher x 27
volume x 24
authors x 23
issue x 21
firstpage x 20
description x 19
abstract x 19
journal x 16
license x 15
figure x 14
copyright x 13
supplementary_material x 11
language x 10
issn x 10
figure_caption x 8
author x 6
lastpage x 6
fulltext_xml x 6
abstract_html x 6
source x 5
identifier x 5
creators x 5
contributors x 5
references x 3
keywords x 3
htmlBodyAuthors x 3
corresponding_author_email x 3
onlineDate x 3
htmlBodyAuthorUrls x 3
author_institutions x 3
citationDate x 3
author_contrib_html x 2
fulltext_ePUB x 2
caption x 2
date_accepted x 2
date_published x 2
date_submitted x 2
abstract2 x 2
discussion_html x 2
coordinates_cif x 2
methods_html x 2
author_institution x 2
author_name x 2
results_html x 2
supplementary_file x 2
htmlCitations x 2
introduction_html x 2
references_html x 2
figures_image x 2
editor_name x 2
fulltext_html_frameset x 2
tables_html x 2
journal_name x 2
figures_html x 2
journal_issn x 2
supplementary_material_richtext
supplementary_material_ms-excel
supplementary_material_encapsulated-postscript
section
supplementary_material_audio
supplementary_material_ascii
supplementary_material_ms-word
supplementary_material_wordperfect
smallfigure
conference
supplementary_material_movie
supplementary_material_postscript
supplementary_material_mpg
supplementary_material_html
supplementary_material_sbml
abstract_text
html_title
supplementary_material_xml
conclusion_html
competing_interests_html
structure_factors_cif
largefigure
supplementary_material_owl
supplementary_material_pdf


	 */
//	public static final String QUICKSCRAPE_AUTHOR = "$.author";
//	public static final String QUICKSCRAPE_DATE = "$.date";
//	public static final String QUICKSCRAPE_DESCRIPTION = "$.description";
//	public static final String QUICKSCRAPE_DOI = "$.doi";
//	public static final String QUICKSCRAPE_FIRST_PAGE = "$.firstpage";
//	public static final String QUICKSCRAPE_FULLTEXT_HTML = "$.fulltext_html";
//	public static final String QUICKSCRAPE_FULLTEXT_PDF = "$.fulltext_pdf";
//	public static final String QUICKSCRAPE_FULLTEXT_XML = "$.fulltext_xml";
//	public static final String QUICKSCRAPE_PUBLISHER = "$.publisher";
//	public static final String QUICKSCRAPE_FIRST_ISSUE = "$.issue";
//	public static final String QUICKSCRAPE_SOURCE = "$.source";
//	public static final String QUICKSCRAPE_TITLE = "$.title";
//	public static final String QUICKSCRAPE_VOLUME = "$.volume";
//	public static final String QUICKSCRAPE_TYPE = "$.type";
//	public static final String QUICKSCRAPE_URL = "$.URL";
	
	/** terms in current scrapers */
	public static final String QS_ABSTRACT = "abstract";
	public static final String QS_ABSTRACT_HTML = "abstract_html";
	public static final String QS_AUTHOR = "author";
	public static final String QS_AUTHORS = "authors";
	public static final String QS_CONTRIBUTORS = "contributors";
	public static final String QS_COPYRIGHT = "copyright";
	public static final String QS_CREATORS = "creators";
	public static final String QS_DATE = "date";
	public static final String QS_DESCRIPTION = "description";
	public static final String QS_DOI = "doi";
	public static final String QS_FIGURE = "figure";
	public static final String QS_FIGURE_CAPTION = "figure_caption";
	public static final String QS_FIRST_PAGE = "firstpage";
	public static final String QS_FULLTEXT_HTML = "fulltext_html";
	public static final String QS_FULLTEXT_PDF = "fulltext_pdf";
	public static final String QS_FULLTEXT_XML = "fulltext_xml";
	public static final String QS_IDENTIFIER = "identifier";
	public static final String QS_ISSN = "issn";
	public static final String QS_ISSUE = "issue";
	public static final String QS_JOURNAL = "journal";
	public static final String QS_LANGUAGE = "language";
	public static final String QS_LAST_PAGE = "lastpage";
	public static final String QS_LICENSE = "license";
	public static final String QS_PUBLISHER = "publisher";
	public static final String QS_SOURCE = "source";
	public static final String QS_SUPP_MATERIAL = "supplementary_material";
	public static final String QS_TITLE = "title";
	public static final String QS_VOLUME = "volume";
	public static final String QS_TYPE = "type";
	public static final String QS_URL = "URL";
	
	public static List<String> QS_TERMS = new ArrayList<String>();
	static {
        QS_TERMS.add(QS_ABSTRACT);
        QS_TERMS.add(QS_ABSTRACT_HTML);
        QS_TERMS.add(QS_AUTHOR);
        QS_TERMS.add(QS_AUTHORS);
        QS_TERMS.add(QS_CONTRIBUTORS);
        QS_TERMS.add(QS_COPYRIGHT);
        QS_TERMS.add(QS_CREATORS);
        QS_TERMS.add(QS_DATE);
        QS_TERMS.add(QS_DESCRIPTION);
        QS_TERMS.add(QS_DOI);
        QS_TERMS.add(QS_FIGURE);
        QS_TERMS.add(QS_FIGURE_CAPTION);
        QS_TERMS.add(QS_FIRST_PAGE);
        QS_TERMS.add(QS_FULLTEXT_HTML);
        QS_TERMS.add(QS_FULLTEXT_PDF);
        QS_TERMS.add(QS_FULLTEXT_XML);
        QS_TERMS.add(QS_IDENTIFIER);
        QS_TERMS.add(QS_ISSN);
        QS_TERMS.add(QS_ISSUE);
        QS_TERMS.add(QS_JOURNAL);
        QS_TERMS.add(QS_LANGUAGE);
        QS_TERMS.add(QS_LAST_PAGE);
        QS_TERMS.add(QS_LICENSE);
        QS_TERMS.add(QS_PUBLISHER);
        QS_TERMS.add(QS_SOURCE);
        QS_TERMS.add(QS_SUPP_MATERIAL);
        QS_TERMS.add(QS_TITLE);
        QS_TERMS.add(QS_VOLUME);
        QS_TERMS.add(QS_TYPE);
        QS_TERMS.add(QS_URL);
		
	}
	
	
	public static final String SCRAPER_ELEMENTS = "elements";

	public enum CrossRefType {
		JOURNAL_ARTICLE("journal-article"),
		BOOK_CHAPTER("book-chapter"),
		COMPONENT("component"),
		PROCEEDINGS_ARTICLE("proceedings-article"),
		DATASET("dataset"),    
		JOURNAL_ISSUE("journal-issue"),
		OTHER("other"),
		REPORT("report"),
		MONOGRAPH("monograph"),
		BOOK("book"),
		JOURNAL("journal"),
		REFERENCE_ENTRY("reference-entry"),
		DISSERTATION("dissertation"),
		STANDARD("standard"),
		REPORT_SERIES("report-series"),
		PROCEEDINGS("proceedings"),
		JOURNAL_VOLUME("journal-volume"),
		BOOK_SECTION("book-section"),
		REFERENCE_BOOK("reference-book");
		private String type;

		private CrossRefType(String type) {
			this.type = type;
		}
	}
	

	private File jsonFile;
	private JsonElement jsonElement;

	public MetadataJson() {
	}
	
	public static MetadataJson createMetadataJson(File jsonFile) {
		MetadataJson metadataJson = null;
		if (jsonFile != null && jsonFile.exists() && 
				CTree.JSON.equals(FilenameUtils.getExtension(jsonFile.getName()))) {
			try {
				JsonParser jsonParser = new JsonParser();
				JsonElement jsonElement = jsonParser.parse(FileUtils.readFileToString(jsonFile));
				metadataJson = new MetadataJson();
				metadataJson.setJsonFile(jsonFile);
				metadataJson.setJsonElement(jsonElement);
			} catch (JsonSyntaxException e) {
				throw new RuntimeException("Json syntax error in "+jsonFile, e);
			} catch (IOException e) {
				throw new RuntimeException("Cannot read file "+jsonFile, e);
			}
		}
		return metadataJson;
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
			value = array.size() == 0 ? null : array.get(0).toString();
		}
		return value;
	}
	
	public String getJsonMapStringByPath(String jsonPath) {
		String value = null;
		if (jsonPath != null && jsonElement != null) {
			Map<String, String> map = (Map<String, String>) CMineUtil.getObjectForJsonPath(jsonElement.toString(), jsonPath);
			LOG.debug("map "+map);
		}
		return value;
	}
	
	/** create spreadSheet using Crossref metadata.
	 * 
	 * @param cTreeList
	 * @param filename
	 * @throws IOException
	 */
	public static void createCrossrefSpreadsheet(CTreeList cTreeList, String filename) throws IOException {
		List<String> header = Arrays.asList(new String[]{
				CROSSREF_URL,
				CROSSREF_INDEXED_DATE_TIME,
				CROSSREF_PUBLISHER,
				CROSSREF_TYPE,
				CROSSREF_DOI,
				CROSSREF_SOURCE,
				CROSSREF_TITLE
				});
		CSVWriter csvWriter = new CSVWriter();
		csvWriter.addRow(header);
		for (CTree cTree : cTreeList) {
			MetadataJson metadataJson = cTree.getMetadataJson(CTree.CROSSREF_RESULT_JSON);
			if (MetadataJson.JOURNAL_ARTICLE.equals( metadataJson.getJsonStringByPath(CROSSREF_TYPE))) {
				List<String> row = new ArrayList<String>();
				for (String metadataPath : header) {
					String value = metadataJson.getJsonStringByPath(metadataPath);
					row.add(value);
				}
				csvWriter.addRow(row);
			} else {
				LOG.debug("skipped");
			}
		}
		csvWriter.writeCsvFile(filename);
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


}
