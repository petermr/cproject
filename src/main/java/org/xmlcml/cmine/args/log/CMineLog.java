package org.xmlcml.cmine.args.log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.xmlcml.xml.XMLUtil;

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
	public final static String TAG = "log";

	private File file;

	public CMineLog(File file) {
		super(TAG);
		this.file = file;
	}

	public void error(String message) {
		addMethodNameAddMessageAndAppend(new ErrorElement(), message);
	}

	public void warn(String message) {
		addMethodNameAddMessageAndAppend(new WarnElement(), message);
	}

	public void info(String message) {
		addMethodNameAddMessageAndAppend(new InfoElement(), message);
	}

	public void debug(String message) {
		addMethodNameAddMessageAndAppend(new DebugElement(), message);
	}

	public void writeLog() {
		if (file != null) {
			this.createDateTimeIntervals();
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				XMLUtil.debug(this, baos, 1);
				FileUtils.write(file, baos.toString());
			} catch (IOException e) {
				throw new RuntimeException("Cannot write LOG: ", e);
			}
		}
	}

	private void createDateTimeIntervals() {
		List<Element> logChildren = XMLUtil.getQueryElements(this, "//log[@date]/*[@date]");
		for (Element child : logChildren) {
			AbstractLogElement childElement = (AbstractLogElement) child;
			DateTime childDateTime = new DateTime(childElement.getDateTimeString());
			AbstractLogElement parentElement = (AbstractLogElement) child.getParent();
			DateTime parentDateTime = new DateTime(parentElement.getDateTimeString());
			Interval interval = new Interval(parentDateTime, childDateTime);
			childElement.setInterval(interval.toDurationMillis());
			childElement.removeAttribute(childElement.getAttribute(DATE));
		}
	}


}
