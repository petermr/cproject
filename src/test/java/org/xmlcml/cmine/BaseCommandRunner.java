package org.xmlcml.cmine;

import nu.xom.Element;

@Deprecated
public class BaseCommandRunner {

	private BaseParser baseParser;
	private BaseCommandElement baseCommandElement;

	public BaseCommandRunner(BaseParser baseParser) {
		this.setBaseParser(baseParser);
	}

	public BaseParser getBaseParser() {
		return baseParser;
	}

	public void setBaseParser(BaseParser baseParser) {
		this.baseParser = baseParser;
	}

	public void runCommands() {
		baseCommandElement = baseParser == null ? null : baseParser.getBaseElement();
		if (baseCommandElement != null) {
			baseCommandElement.runCommand();
		}
	}

	
}
