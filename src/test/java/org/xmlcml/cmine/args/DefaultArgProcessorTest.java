package org.xmlcml.cmine.args;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;

public class DefaultArgProcessorTest {
	
	private static final Logger LOG = Logger.getLogger(DefaultArgProcessorTest.class);
	static {
		LOG.setLevel(org.apache.log4j.Level.DEBUG);
	}

	@Test
	@Ignore // side-effects creates files
	public void testArgs() {
		String[] args = {
			"-i", "foo", "bar",
			"-o", "plugh",
		};
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		Assert.assertEquals("input", 2, argProcessor.getInputList().size());
		Assert.assertEquals("input", "foo", argProcessor.getInputList().get(0));
		Assert.assertEquals("input", "bar", argProcessor.getInputList().get(1));
		Assert.assertEquals("output", "plugh", argProcessor.getOutput());
	}

	@Test
	public void testSingleWildcards() {
		String[] args = {
			"-i", "foo{1:3}bof", "bar{a|b|zzz}plugh", 
		};
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		Assert.assertEquals("input", 2, argProcessor.getInputList().size());
		Assert.assertEquals("input", "foo{1:3}bof", argProcessor.getInputList().get(0));
		Assert.assertEquals("input", "bar{a|b|zzz}plugh", argProcessor.getInputList().get(1));
		argProcessor.expandWildcardsExhaustively();
		Assert.assertEquals("input", 6, argProcessor.getInputList().size());
		Assert.assertEquals("input", "foo1bof", argProcessor.getInputList().get(0));
		Assert.assertEquals("input", "foo2bof", argProcessor.getInputList().get(1));
		Assert.assertEquals("input", "foo3bof", argProcessor.getInputList().get(2));
		Assert.assertEquals("input", "baraplugh", argProcessor.getInputList().get(3));
		Assert.assertEquals("input", "barbplugh", argProcessor.getInputList().get(4));
		Assert.assertEquals("input", "barzzzplugh", argProcessor.getInputList().get(5));
	}
	
	
	@Test
	public void testMultipleWildcards() {
		String[] args = {
			"-i", "foo{1:3}bof{3:6}plugh",
		};
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		Assert.assertEquals("input", 1, argProcessor.getInputList().size());
		Assert.assertEquals("input", "foo{1:3}bof{3:6}plugh", argProcessor.getInputList().get(0));
		argProcessor.expandWildcardsExhaustively();
		Assert.assertEquals("input", 12, argProcessor.getInputList().size());
		Assert.assertEquals("input", "foo1bof3plugh", argProcessor.getInputList().get(0));
	}
	
	@Test
	public void testArgCounts() {
		String[] args = {"-o", "foo"};
		new DefaultArgProcessor().parseArgs(args);
		try {
			args = new String[]{"-o", "foo", "bar"};
			new DefaultArgProcessor().parseArgs(args);
		} catch (Exception e) {
			Assert.assertEquals("too many arguments", 
					"cannot process argument: -o (IllegalArgumentException: --output; argument count (2) is not compatible with {1,1})",
					e.getMessage());
		}
	}
	
	@Test
	public void testMakeDocs() {
		String args = "--makedocs";
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs(args);
		argProcessor.runAndOutput();
	}
	
	@Test
	public void testVersion() {
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs("--version");
	}
	
	@Test
	public void testProject() {
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		try {
			argProcessor.parseArgs("--project");
			Assert.fail("should trap zero arguments");
		} catch (Exception e) {
			// OK
		}
		
		argProcessor = new DefaultArgProcessor();
		argProcessor.parseArgs("--project foo");
	}
	
	
	@Test
	public void testLog() throws IOException {
		DefaultArgProcessor argProcessor = new DefaultArgProcessor();
		File targetFile = new File("target/test/log/");
		targetFile.mkdirs();
		// dummy file
		FileUtils.write(new File(targetFile, "fulltext.txt"), "fulltext");
		argProcessor.parseArgs("-q "+targetFile+" -i fulltext.txt  --c.test --log");
		argProcessor.runAndOutput();
	}
	
}
