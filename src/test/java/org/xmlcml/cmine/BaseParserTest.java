package org.xmlcml.cmine;


import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;

@Ignore // based on new parser
public class BaseParserTest {

	private static final Logger LOG = Logger.getLogger(BaseParserTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testParser() {
		BaseParser baseParser = new BaseParser();
		String cmd = "-p myProject";
		baseParser.parseArgs(cmd);
		List<String> argList = baseParser.getArgList();
		Assert.assertEquals(2, argList.size());
		Assert.assertEquals("[-p, myProject]", argList.toString());
	}

	@Test
	public void testParserWithSubstitions() {
		BaseParser baseParser = new BaseParser();
		String cmd = "-p myProject _summary";
		baseParser.parseArgs(cmd);
		List<String> argList = baseParser.getArgList();
		Assert.assertEquals(4, argList.size());
		Assert.assertEquals("[-p, myProject, --summaryFile, summary.xml]", argList.toString());
		Assert.assertEquals("args xml", "<command project=\"myProject\" summaryFile=\"summary.xml\" />", baseParser.getBaseElement().toXML());
	}

	@Test
	public void testCreateCommandElements() {
		BaseParser baseParser = new BaseParser();
		String cmd = "-p myProject";
		baseParser.parseArgs(cmd);
		baseParser.getBaseElement().runCommand();
	}

}
