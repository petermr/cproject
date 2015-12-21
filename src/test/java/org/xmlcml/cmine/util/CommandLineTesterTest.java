package org.xmlcml.cmine.util;

import junit.framework.Assert;

import org.junit.Test;

/** tests CommandLineTester.
 * 
 * @author pm286
 *
 */
public class CommandLineTesterTest {

	@Test
	public void testPwd() throws Exception {
		CommandLineTester tester = new CommandLineTester();
		tester.setCommand("pwd");
		tester.run();
		String output = tester.getOutputString();
		Assert.assertEquals("pwd", System.getProperty("user.home")+"/workspace/cmine\n", output);
	}

	@Test
	public void testLs() throws Exception {
		CommandLineTester tester = new CommandLineTester();
		tester.setCommand("ls");
		tester.addArgument("-l");
		tester.addArgument("pom.xml");
		tester.run();
		String output = tester.getOutputString();
		Assert.assertTrue("ls: "+output, output.endsWith("pom.xml\n"));
		Assert.assertTrue("ls: "+output, output.split("\\s+").length > 8); 
	}
}
