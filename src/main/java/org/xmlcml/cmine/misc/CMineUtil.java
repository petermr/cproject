package org.xmlcml.cmine.misc;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.files.CTree;

public class CMineUtil {
	
	private static final Logger LOG = Logger.getLogger(CMineUtil.class);
	static {
		LOG.setLevel(Level.DEBUG);
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

}
