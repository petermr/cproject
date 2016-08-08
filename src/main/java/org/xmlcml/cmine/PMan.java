package org.xmlcml.cmine;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cmine.args.DefaultArgProcessor;

public class PMan {

	private static final Logger LOG = Logger.getLogger(PMan.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private DefaultArgProcessor argProcessor;

	public static void main(String[] args) {
		PMan cmine = new PMan();
		cmine.run(args);
	}

	public void run(String[] args) {
		argProcessor = new PManArgProcessor(args);
		argProcessor.runAndOutput();
	}

	public void run(String args) {
		argProcessor = new PManArgProcessor(args.split("\\s+"));
		argProcessor.runAndOutput();
	}

	public DefaultArgProcessor getArgProcessor() {
		return argProcessor;
	}
}
