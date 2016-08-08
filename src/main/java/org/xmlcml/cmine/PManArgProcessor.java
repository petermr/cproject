package org.xmlcml.cmine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.args.ArgIterator;
import org.xmlcml.cmine.args.ArgumentOption;
import org.xmlcml.cmine.args.DefaultArgProcessor;
import org.xmlcml.cmine.files.CProject;
import org.xmlcml.cmine.metadata.AbstractMDAnalyzer;
import org.xmlcml.cmine.metadata.AbstractMetadata;
import org.xmlcml.cmine.metadata.crossref.CrossrefAnalyzer;

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

	private String urlFilename;
	private AbstractMetadata.Type metadataType;
	private Boolean shuffle;
	private String csvFilename;
	private List<String> csvHeadings;
	private String cProject2Name;
	private List<String> renameOptions;

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
	}

	private String getArgsResource() {
		return ARGS_RESOURCE;
	}
	
	/** create filename to extract CSV
	 */
	public void parseCSV(ArgumentOption option, ArgIterator argIterator) {
		List<String> csvArgs = argIterator.getStrings(option);
		if (csvArgs.size() < 1) {
			throw new RuntimeException("CSV requires filename");
		}
		csvFilename = csvArgs.get(0);
		csvHeadings = new ArrayList<String>(csvArgs.subList(1, csvArgs.size()));
	}

	/** create filename to extract URLs to
	 */
	public void parseExtractUrls(ArgumentOption option, ArgIterator argIterator) {
		List<String> strings = argIterator.getStrings(option);
		urlFilename = (strings.size() == 0) ? getDefaultUrlFilename() : strings.get(0);
		if (strings.size() > 1) {
			shuffle = strings.get(1).toLowerCase().equals(SHUFFLE);
		}
	}

	private String getDefaultUrlFilename() {
		return URLS_TXT;
	}
	
	/** get metadataType
	 */
	public void parseMergeProject(ArgumentOption option, ArgIterator argIterator) {
		cProject2Name = argIterator.getString(option);
	}

	/** get metadataType
	 */
	public void parseMetadataType(ArgumentOption option, ArgIterator argIterator) {
		String metadataString = argIterator.getString(option);
		getMetadataType(metadataString);
	}

	/** get metadataType
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
			File file = new File(cProject.getDirectory(), csvFilename);
			AbstractMDAnalyzer crossrefAnalyzer = new CrossrefAnalyzer(cProject);
			crossrefAnalyzer.addRowsToTable(csvHeadings, AbstractMetadata.Type.CROSSREF);
			crossrefAnalyzer.createMultisets();
			crossrefAnalyzer.writeCsvFile(file);
		} catch (IOException e) {
			throw new RuntimeException("cannot write urls: "+urlFilename, e);
		}
	}

	/** final extract Urls
	 */
	public void finalExtractUrls(ArgumentOption option) {
		try {
			File file = new File(cProject.getDirectory(), urlFilename);
			cProject.extractShuffledUrlsFromCrossrefToFile(file);
		} catch (IOException e) {
			throw new RuntimeException("cannot write urls: "+urlFilename, e);
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
