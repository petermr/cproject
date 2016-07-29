package org.xmlcml.cmine.metadata.quickscrape;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.metadata.AbstractMetadata;

public class QuickscrapeMD extends AbstractMetadata {
	
	private static final String CITATION_ABSTRACT_HTML_URL = "citation_abstract_html_url";
	private static final String CITATION_AUTHOR = "citation_author";
	private static final String CITATION_AUTHOR_EMAIL = "citation_author_email";
	private static final String CITATION_AUTHOR_INSTITUTION = "citation_author_institution";
	private static final String CITATION_AUTHORS = "citation_authors";
	private static final String CITATION_FULLTEXT_HTML_URL = "citation_fulltext_html_url";
	private static final String CITATION_DOI = "citation_doi";
	private static final String CITATION_FIRSTPAGE = "citation_firstpage";
	private static final String CITATION_ID = "citation_id";
	private static final String CITATION_ISSN = "citation_issn";
	private static final String CITATION_JOURNAL_ABBREV = "citation_journal_abbrev";
	private static final String CITATION_JOURNAL_TITLE = "citation_journal_title";
	private static final String CITATION_LASTPAGE = "citation_lastpage";
	private static final String CITATION_PDF_URL = "citation_pdf_url";
	private static final String CITATION_PUBLIC_URL = "citation_public_url";
	private static final String CITATION_PUBLISHER = "citation_publisher";
	private static final String CITATION_REFERENCE = "citation_reference";
	private static final String CITATION_TITLE = "citation_title";

//	citation_springer_api_url x 117

	private static final String DC_CREATOR = "dc.creator";
	private static final String DC_IDENTIFIER = "dc.identifier";
	private static final String DC_ISSN = "dc.issn";
	private static final String DC_PUBLISHER      = "dc.publisher";
	private static final String DC_RIGHTS = "dc.rights";
	private static final String DC_TITLE = "dc.title";
	
	private static final String PRISM_PUBLICATION_NAME = "prism.publicationName";
	private static final String PRISM_RIGHTS = "prism.rights";
	
	private static final String HW_IDENTIFIER = "hw.identifier";
	
	private static final String VALUE = ".value";
	
	static final Logger LOG = Logger.getLogger(QuickscrapeMD.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	/**
{
  "fulltext_pdf": {
    "value": [
      "http://archneur.jamanetwork.com/data/Journals/NEUR/15965/archneur_v40_n13_p784.pdf"
    ]
  },
  "fulltext_html": {
    "value": [
      "http://onlinelibrary.wiley.com/doi/10.1002/rnc.3573/full"
    ]
  },
  "title": {
    "value": [
      "Incorrect Table Entries and Word"
    ]
  },
  "author": {
    "value": [
      "Bassam Lajin",
      "Kevin A. Francesconi"
    ]
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
  },
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
		

	/** terms in current scrapers */
	public static final String ABSTRACT         = "abstract";
	public static final String ABSTRACT_HTML    = "abstract_html";
	public static final String AUTHOR           = "author";
	public static final String AUTHOR_INSTITUTION   = "author_institution";
	public static final String AUTHORS          = "authors";
	public static final String CONTRIBUTORS     = "contributors";
	public static final String COPYRIGHT        = "copyright";
	public static final String CREATORS         = "creators";
	public static final String DATE             = "date";
	public static final String DESCRIPTION      = "description";
	public static final String DOI              = "doi";
	public static final String FIGURE           = "figure";
	public static final String FIGURE_CAPTION   = "figure_caption";
	public static final String FIRST_PAGE       = "firstpage";
	public static final String FULLTEXT_HTML    = "fulltext_html";
	public static final String FULLTEXT_PDF     = "fulltext_pdf";
	public static final String FULLTEXT_XML     = "fulltext_xml";
	public static final String IDENTIFIER       = "identifier";
	public static final String ISSN             = "issn";
	public static final String ISSUE            = "issue";
	public static final String JOURNAL          = "journal";
	public static final String LANGUAGE         = "language";
	public static final String LAST_PAGE        = "lastpage";
	public static final String LICENSE          = "license";
	public static final String PUBLISHER        = "publisher";
	public static final String SOURCE           = "source";
	public static final String SUPP_MATERIAL    = "supplementary_material";
	public static final String TITLE            = "title";
	public static final String VOLUME           = "volume";
	public static final String TYPE             = "type";
	public static final String URL              = "URL";
	
	private static final String $_ABSTRACT_VALUE      = "$."+ABSTRACT+VALUE;
	private static final String $_AUTHOR_VALUE        = "$."+AUTHOR+VALUE;
	private static final String $_AUTHOR_INSTITUTION  = "$."+AUTHOR_INSTITUTION+VALUE;
	private static final String $_COPYRIGHT_VALUE     = "$."+COPYRIGHT+VALUE;
	private static final String $_DATE_VALUE          = "$."+DATE+VALUE;
	private static final String $_DESCRIPTION_VALUE   = "$."+DESCRIPTION+VALUE;
	private static final String $_DOI_VALUE           = "$."+DOI+VALUE;
	private static final String $_FIRSTPAGE_VALUE     = "$."+FIRST_PAGE+VALUE;
	private static final String $_FULLTEXT_HTML_VALUE = "$."+FULLTEXT_HTML+VALUE;
	private static final String $_FULLTEXT_PDF_VALUE  = "$."+FULLTEXT_PDF+VALUE;
	private static final String $_FULLTEXT_XML_VALUE  = "$."+FULLTEXT_XML+VALUE;
	private static final String $_ISSN_VALUE          = "$."+ISSN+VALUE;
	private static final String $_ISSUE_VALUE         = "$."+ISSUE+VALUE;
	private static final String $_JOURNAL_VALUE       = "$."+JOURNAL+VALUE;
	private static final String $_LICENSE_VALUE       = "$."+LICENSE+VALUE;
	private static final String $_PUBLISHER_VALUE     = "$."+PUBLISHER+VALUE;
	private static final String $_TITLE_VALUE         = "$."+TITLE+VALUE;
	private static final String $_URL_VALUE           = "$."+URL+VALUE;
	private static final String $_VOLUME_VALUE        = "$."+VOLUME+VALUE;

	public static List<String> TERMS = new ArrayList<String>();
	static {
        TERMS.add(ABSTRACT);
        TERMS.add(ABSTRACT_HTML);
        TERMS.add(AUTHOR);
        TERMS.add(AUTHORS);
        TERMS.add(CONTRIBUTORS);
        TERMS.add(COPYRIGHT);
        TERMS.add(CREATORS);
        TERMS.add(DATE);
        TERMS.add(DESCRIPTION);
        TERMS.add(DOI);
        TERMS.add(FIGURE);
        TERMS.add(FIGURE_CAPTION);
        TERMS.add(FIRST_PAGE);
        TERMS.add(FULLTEXT_HTML);
        TERMS.add(FULLTEXT_PDF);
        TERMS.add(FULLTEXT_XML);
        TERMS.add(IDENTIFIER);
        TERMS.add(ISSN);
        TERMS.add(ISSUE);
        TERMS.add(JOURNAL);
        TERMS.add(LANGUAGE);
        TERMS.add(LAST_PAGE);
        TERMS.add(LICENSE);
        TERMS.add(PUBLISHER);
        TERMS.add(SOURCE);
        TERMS.add(SUPP_MATERIAL);
        TERMS.add(TITLE);
        TERMS.add(VOLUME);
        TERMS.add(TYPE);
        TERMS.add(URL);
	}
	
	public final static String RESULTS_JSON = "results.json";
	
	public QuickscrapeMD() {
		super();
		hasQuickscrapeMetadata = true;
	}
	
	public static AbstractMetadata createMetadata() {
		return new QuickscrapeMD();
	}

	@Override
	public String getAbstract() {
		return getJsonArrayStringByPath($_ABSTRACT_VALUE);
	}

	@Override
	public String getAbstractURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAuthorEmail() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAuthorListAsStrings() {
		return getJsonArrayByPath($_AUTHOR_VALUE);
	}

	@Override
	public String getAuthorInstitution() {
		return getJsonValueOrHtmlMetaContent($_AUTHOR_INSTITUTION, new String[] {CITATION_AUTHOR_INSTITUTION});
	}

	@Override
	public String getCitations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCopyright() {
		return getJsonArrayStringByPath($_COPYRIGHT_VALUE);
	}

	@Override
	public String getCreator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDate() {
		return getJsonArrayStringByPath($_DATE_VALUE);
	}

	@Override
	public String getDescription() {
		return getJsonArrayStringByPath($_DESCRIPTION_VALUE);
	}

	@Override
	public String getDOI() {
		return getJsonArrayStringByPath($_DOI_VALUE);
	}

	@Override
	public String getFirstPage() {
		return getJsonArrayStringByPath($_FIRSTPAGE_VALUE);
	}

	@Override
	public String getFulltextHTMLURL() {
		return getJsonArrayStringByPath($_FULLTEXT_HTML_VALUE);
	}

	@Override
	public String getFulltextPDFURL() {
		return getJsonArrayStringByPath($_FULLTEXT_PDF_VALUE);
	}

	@Override
	public String getFulltextPublicURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFulltextXMLURL() {
		return getJsonArrayStringByPath($_FULLTEXT_XML_VALUE);
	}

	@Override
	public String getISSN() {
		return getJsonValueOrHtmlMetaContent($_ISSN_VALUE, new String[] {CITATION_ISSN});
	}

	@Override
	public String getIssue() {
		return getJsonArrayStringByPath($_ISSUE_VALUE);
	}

	@Override
	public String getJournal() {
		return getJsonValueOrHtmlMetaContent($_JOURNAL_VALUE, new String[] {CITATION_JOURNAL_TITLE});
	}

	@Override
	public String getKeywords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLastPage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLicense() {
		return getJsonValueOrHtmlMetaContent($_LICENSE_VALUE, new String[] {DC_RIGHTS});		
	}

	@Override
	public String getPublicURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPublisher() {
		return getJsonValueOrHtmlMetaContent($_PUBLISHER_VALUE, new String[] {CITATION_PUBLISHER, DC_PUBLISHER});
	}

	@Override
	public String getReferenceCount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRights() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle() {
		return getJsonArrayStringByPath($_TITLE_VALUE);
	}


	@Override
	public String getURL() {
		return getJsonArrayStringByPath($_URL_VALUE);
	}

	@Override
	public String getVolume() {
		return getJsonArrayStringByPath($_VOLUME_VALUE);
	}

	@Override
	public String getLinks() {
		return null;
	}

	@Override
	public String getPrefix() {
		return null;
	}

	@Override 
	public String hasQuickscrapeMetadata() {
		hasQuickscrapeMetadata = (cTree != null && cTree.getExistingResultsJSON() != null);
		return hasQuickscrapeMetadata ? "Y" : "N";
	}
	

}
