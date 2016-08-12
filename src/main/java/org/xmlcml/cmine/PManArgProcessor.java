package org.xmlcml.cmine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.args.ArgIterator;
import org.xmlcml.cmine.args.ArgumentOption;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.cmine.files.CTree;
import org.xmlcml.cmine.files.CTreeList;
import org.xmlcml.cmine.metadata.AbstractMDAnalyzer;
import org.xmlcml.cmine.metadata.AbstractMetadata;
import org.xmlcml.cmine.metadata.AbstractMetadata.Type;
import org.xmlcml.cmine.metadata.crossref.CrossrefAnalyzer;
import org.xmlcml.cmine.metadata.crossref.CrossrefMD;
import org.xmlcml.cmine.metadata.epmc.EpmcMD;
import org.xmlcml.cmine.metadata.quickscrape.QuickscrapeMD;
import org.xmlcml.cmine.util.CMineUtil;

/** runs CMine commands especially crossref, etc.
 * 
 * Might get split out from as its own class later.
 * 
 * @author pm286
 *
 */
public class PManArgProcessor extends DefaultArgProcessor {

	public static final Logger LOG = Logger.getLogger(PManArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static String RESOURCE_NAME_TOP = "/org/xmlcml/pman";
	private static String ARGS_RESOURCE = RESOURCE_NAME_TOP+"/"+"args.xml";
	private static final String SHUFFLE = "shuffle";
	private static final String URLS_TXT = "urls.txt";
	private static final String NO_HTTP = "noHttp";
	private static final String MARK_EMPTY = "markEmpty";

	private String inUrlFilename;
	private AbstractMetadata.Type metadataType;
	private Boolean shuffle;
	private String csvFilename;
	private List<String> csvHeadings;
	private String cProject2Name;
	private List<String> renameOptions;
	private String outUrlFilename;
	private CTreeList cTreeList;
	private boolean markEmpty;
	private List<String> inUrls;

	public PManArgProcessor() {
		super();
		this.readArgumentOptions(this.getArgsResource());
	}
	
	public PManArgProcessor(String args) {
		this(args == null ? null : args.replaceAll("\\s+", " ").split(" "));
	}

	public PManArgProcessor(String[] args) {
		this();
		setDefaults();
		parseArgs(args);
	}

	private void setDefaults() {
		shuffle = false;
		metadataType = Type.CROSSREF;
	}

	private String getArgsResource() {
		return ARGS_RESOURCE;
	}
	
	/** create filename to extract CSV
	 */
	public void parseCSV(ArgumentOption option, ArgIterator argIterator) {
		List<String> csvArgs = argIterator.getStrings(option);
		csvFilename = null;
		if (csvArgs.size() < 1) {
			csvHelp();
//			throw new RuntimeException("CSV requires filename");
		} else {
			csvFilename = csvArgs.get(0);
			if (csvArgs.size() > 1) {
				csvHeadings = new ArrayList<String>(csvArgs.subList(1, csvArgs.size()));
			} else {
				csvHeadings = new ArrayList<String>(AbstractMetadata.getDefaultHeaders());
			}
		}
	}

	/** create input filename with URLs
	 */
	public void parseInUrls(ArgumentOption option, ArgIterator argIterator) {
		List<String> strings = argIterator.getStrings(option);
		inUrlFilename = (strings.size() == 0) ? getDefaultUrlFilename() : strings.get(0);
		if (strings.size() > 1) {
			markEmpty = strings.get(1).toLowerCase().equalsIgnoreCase(MARK_EMPTY);
		}
	}

	/** create filename to extract URLs to
	 */
	public void parseOutUrls(ArgumentOption option, ArgIterator argIterator) {
		List<String> strings = argIterator.getStrings(option);
		outUrlFilename = (strings.size() == 0) ? getDefaultUrlFilename() : strings.get(0);
		if (strings.size() > 1) {
			shuffle = strings.get(1).toLowerCase().equals(SHUFFLE);
		}
	}

	private String getDefaultUrlFilename() {
		return URLS_TXT;
	}
	
	/** 
	 */
	public void parseMergeProject(ArgumentOption option, ArgIterator argIterator) {
		cProject2Name = argIterator.getString(option);
	}

	/** 
	 */
	public void parseMetadataType(ArgumentOption option, ArgIterator argIterator) {
		String metadataString = argIterator.getString(option);
		getMetadataType(metadataString);
	}

	/** 
	 */
	public void parseRenameCTree(ArgumentOption option, ArgIterator argIterator) {
		renameOptions = argIterator.getStrings(option);
	}

	/** shuffle URLs
	 */
	public void parseShuffle(ArgumentOption option, ArgIterator argIterator) {
		shuffle = argIterator.getBoolean(option);
	}

	// ----------- RUN -----------
	
	/** rename CTrees
	 */
	public void runRenameCTree(ArgumentOption option) {
		if (renameOptions != null) {
			if (renameOptions.contains(NO_HTTP)) {
				currentCTree.normalizeDOIBasedDirectory();
			}
			// perhaps more options here...
		}
	}
	
	// --------- FINAL ------------

	/** final extract to CSV
	 */
	public void finalCSV(ArgumentOption option) {
		if (csvFilename == null) {
			throw new RuntimeException("must give csvFile");
		}
		try {
			File csvFile = new File(cProject.getDirectory(), csvFilename);
			AbstractMDAnalyzer crossrefAnalyzer = new CrossrefAnalyzer(cProject);
			crossrefAnalyzer.addRowsToTable(csvHeadings, AbstractMetadata.Type.CROSSREF);
			crossrefAnalyzer.createMultisets();
			crossrefAnalyzer.writeCsvFile(csvFile);
		} catch (IOException e) {
			throw new RuntimeException("cannot write CSV: "+csvFilename, e);
		}
	}

	/** final input Urls
	 */
	public void finalInUrls(ArgumentOption option) {
		String name = FilenameUtils.getName(inUrlFilename);
		File inUrlFile = new File(cProject.getDirectory(), name);
		inUrls = null;
		try {
			inUrls = FileUtils.readLines(inUrlFile);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read input URLS", e);
		}
		cTreeList = cProject.getCTreeList();
		for (CTree cTree : cTreeList) {
			touchQuickscrapeMDInEmptyDirectories(cTree);
			removeFromInUrls(cTree);
		}
	}

	private void removeFromInUrls(CTree cTree) {
		for (int i = 0; i < inUrls.size(); i++) {
			if (CMineUtil.denormalizeDOI(inUrls.get(i)).equals(cTree.getDirectory().getName())) {
				inUrls.remove(i);
				break;
			}
		}
	}

	private void touchQuickscrapeMDInEmptyDirectories(CTree cTree) {
		if (markEmpty) {
			if (cTree.getExistingQuickscrapeMD() == null) {
				cTree.createFile(AbstractMetadata.Type.QUICKSCRAPE.getCTreeMDFilename());
			}
		}
	}
	
	/** final output Urls
	 */
	public void finalOutUrls(ArgumentOption option) {
		try {
			File outUrlFile = new File(cProject.getDirectory(), outUrlFilename);
			if (inUrls != null) {
				FileUtils.writeLines(outUrlFile, inUrls, "\n");
			} else {
				cProject.extractShuffledUrlsFromCrossrefToFile(outUrlFile);
			}
		} catch (IOException e) {
			throw new RuntimeException("cannot write urls: "+outUrlFilename, e);
		}
	}
	
	/** final extract Urls
	 */
	public void finalMergeProjects(ArgumentOption option) {
		if (cProject == null) {
			throw new RuntimeException("mergeProjects must have existing CProject");
		}
		CProject cProject2 = new CProject(new File(cProject2Name));
		cProject.mergeProjects(cProject2);
	}
			



	//=================

	private void csvHelp() {
		if (metadataType == null) {
			AbstractMetadata.csvHelp();
		} else if (Type.CROSSREF == metadataType) {
			CrossrefMD.csvHelp();
		} else if (Type.EPMC == metadataType) {
			EpmcMD.csvHelp();
		} else if (Type.QUICKSCRAPE == metadataType) {
			QuickscrapeMD.csvHelp();
		}
	}


	private AbstractMetadata.Type getMetadataType(String metadataString) {
		if (metadataString != null) {
			metadataString = metadataString.toUpperCase();
			metadataType = AbstractMetadata.Type.valueOf(metadataString);
		}
		return metadataType;
	}


	
	// ===============
	@Override
	/** parse args and resolve their dependencies.
	 *
	 * (don't run any argument actions)
	 *
	 */
	public void parseArgs(String[] args) {
		super.parseArgs(args);
	}

}
