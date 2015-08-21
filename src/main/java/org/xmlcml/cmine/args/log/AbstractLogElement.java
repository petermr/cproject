package org.xmlcml.cmine.args.log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.xmlcml.xml.XMLUtil;


public class AbstractLogElement extends Element {

	private static final String MILLIS = "millis";
	private static final Logger LOG = Logger.getLogger(AbstractLogElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
//	public List<String> levels = new ArrayList<String>() 
			
	public static final String MSG = "message";
	public static final String DATE = "date";
	public static final String METHOD = "method";
	protected File file;
	
	protected AbstractLogElement(String tag) {
		super(tag);
		setDateTime(new DateTime());
	}

	public AbstractLogElement(String tag, File file) {
		this(tag);
		this.file = file;
	}

	public void addMessage(String msg) {
		this.addAttribute(new Attribute(MSG, msg));
	}

	public String getMessage() {
		return getAttributeValue(MSG);
	}

	public String getDateTimeString() {
		return getAttributeValue(DATE);
	}

	public void setDateTime(DateTime dateTime) {
		this.addAttribute(new Attribute(DATE, dateTime.toString()));
	}

	void setMethod(String methodName) {
		this.addAttribute(new Attribute(METHOD, methodName));
	}

	public String getMethod() {
		return getAttributeValue(METHOD);
	}

	/** get name of method which called the Log.
	 * 
	 * see http://stackoverflow.com/questions/442747/getting-the-name-of-the-current-executing-method
	 * for logic.
	 * 
	 * This method has the stack:
	 *   callingMethod 
	 *     addInfo     
	 *       super
	 *         this method
	 *       
	 * hence the [3]
	 * 
	 * @return
	 */
	protected String getNameOfMethodCallingLogger() {
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		LOG.trace(stacktrace[1]+"\n"
				+stacktrace[1]+"\n"
				+stacktrace[2]+"\n"
				+stacktrace[3]+"\n"
				+stacktrace[4]+"\n"
						+ "============================");
	    StackTraceElement e = stacktrace[4];//coz 0th will be getStackTrace so 1st is this, 2nd is addInfo, 3rd is calling prog
	    String methodString = e.toString();
	    methodString = methodString.replaceAll("\\(.*\\)", ""); // remove line number
		return methodString;
	}

	protected void addMethodNameAddMessageAndAppend(AbstractLogElement element, String message) {
		String methodName = getNameOfMethodCallingLogger();
		element.setMethod(methodName);
		if (message != null) {
			element.addMessage(message);
		}
		this.appendChild(element);
	}

	public void setInterval(long durationMillis) {
		this.addAttribute(new Attribute(MILLIS, String.valueOf(durationMillis)));
	}
	
	public long getInterval() {
		return new Long(this.getAttributeValue(MILLIS));
	}

	public String toString() {
		return file.toString();
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

	public void trace(String message) {
		addMethodNameAddMessageAndAppend(new TraceElement(), message);
	}

	public void writeLog() {
		if (file != null) {
			this.createDateTimeIntervals();
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				XMLUtil.debug(this, baos, 1);
				LOG.trace(">>"+this.toXML());
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
