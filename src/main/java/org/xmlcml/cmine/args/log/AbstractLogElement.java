package org.xmlcml.cmine.args.log;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;


public class AbstractLogElement extends Element {

	private static final String MILLIS = "millis";
	private static final Logger LOG = Logger.getLogger(AbstractLogElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public static final String MSG = "message";
	public static final String DATE = "date";
	public static final String METHOD = "method";
	
	protected AbstractLogElement(String tag) {
		super(tag);
		setDateTime(new DateTime());
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

}
