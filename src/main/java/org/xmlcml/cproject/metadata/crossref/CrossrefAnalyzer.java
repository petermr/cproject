package org.xmlcml.cproject.metadata.crossref;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cproject.files.CProject;
import org.xmlcml.cproject.files.CTreeList;
import org.xmlcml.cproject.metadata.AbstractMDAnalyzer;
import org.xmlcml.cproject.metadata.AbstractMetadata;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.xml.XMLUtil;

import com.google.common.collect.HashMultiset;

import nu.xom.Node;

public class CrossrefAnalyzer extends AbstractMDAnalyzer {
	
	private static final Logger LOG = Logger.getLogger(CrossrefAnalyzer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public CrossrefAnalyzer() {
	}

	public CrossrefAnalyzer(File directory) {
		this.setCProject(directory);
	}

	public CrossrefAnalyzer(CProject cProject) {
		this.setCProject(cProject);
	}
	
	

}
