package org.xmlcml.cmine.args.log;

import java.io.File;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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

	private static final String LOG_ELEMENT = "log";
	static final Logger LOG = Logger.getLogger(CMineLog.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private final static String TAG = LOG_ELEMENT;

	public CMineLog() {
		super(TAG);
	}
	
	public CMineLog(File file) {
		super(TAG, file);
	}

	public void removeNodes(String xpath) {
		List<Node> nodes = XMLUtil.getQueryNodes(this, xpath);
		for (Node node : nodes) {
			node.detach();
		}
	}

	public void mergeLogFile(File logXmlFile, File dir, String... xpathList) {
		Element logXmlElement = XMLUtil.parseQuietlyToDocument(logXmlFile).getRootElement();
		AbstractLogElement logElement = new AbstractLogElement(LOG_ELEMENT);
		XMLUtil.copyAttributes(logXmlElement, logElement);
		logElement.addAttribute(new Attribute("file", dir.toString()));
		this.appendChild(logElement);
		for (String xpath : xpathList) {
			List<Node> nodes = XMLUtil.getQueryNodes(logXmlElement, xpath);
			for (Node node : nodes) {
				logElement.appendChild(node.copy());
			}
		}
	}

	public void collectWithinLog(String xpath, String msg) {
		List<Element> logElements = XMLUtil.getQueryElements(this, LOG_ELEMENT);
		for (Element logElement : logElements) {
			List<Element> elements = XMLUtil.getQueryElements(logElement, xpath);
			int count = elements.size();
			for (Element element : elements) {
				element.detach();
			}
			if (count > 0) {
				Element info = new InfoElement(msg);
				info.appendChild(String.valueOf(count));
				logElement.appendChild(info);
			}
		}
	}

	public void deleteFromLog(String xpath) {
		List<Element> logElements = XMLUtil.getQueryElements(this, LOG_ELEMENT);
		for (Element logElement : logElements) {
			List<Element> elements = XMLUtil.getQueryElements(logElement, xpath);
			for (Element element : elements) {
				element.detach();
			}
		}
	}


}
