package org.xmlcml.cmine.args.log;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** tool to log events and data from CTree.
 * 
 * @author pm286
 *
 */
/** why not use Log4j?
 * have asked
 * http://stackoverflow.com/questions/31903280/recording-data-and-events-using-log4j-over-many-classes
 * 
 * see also
 * 
 * http://stackoverflow.com/questions/2763740/log4j-log-output-of-a-specific-class-to-a-specific-appender?lq=1
 * 
 * @author pm286
 *
 */

public class CMineLog extends AbstractLogElement {

	static final Logger LOG = Logger.getLogger(CMineLog.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private final static String TAG = "log";

	public CMineLog() {
		super(TAG);
	}
	
	public CMineLog(File file) {
		super(TAG, file);
	}


}
