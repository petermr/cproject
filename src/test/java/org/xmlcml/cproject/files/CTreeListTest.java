package org.xmlcml.cproject.files;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.cproject.CMineFixtures;
import org.xmlcml.cproject.files.CProject;
import org.xmlcml.cproject.files.CTreeList;

import junit.framework.Assert;

public class CTreeListTest {

	private static final Logger LOG = Logger.getLogger(CTreeListTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testFileOrder() {
		CProject cProject = new CProject(CMineFixtures.TEST_SAMPLE);
		CTreeList cTreeList = cProject.getResetCTreeList();
//		LOG.debug(cTreeList.getCTreeDirectoryList());
		Assert.assertEquals("ctrees",  "["
				+ "src/test/resources/org/xmlcml/download/sample/PMC4678086,"
				+ " src/test/resources/org/xmlcml/download/sample/PMC4686705,"
				+ " src/test/resources/org/xmlcml/download/sample/PMC4691483,"
				+ " src/test/resources/org/xmlcml/download/sample/PMC4706908,"
				+ " src/test/resources/org/xmlcml/download/sample/PMC4706911,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1001_jama.2016.7992,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1007_s13201-016-0429-9,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1016_s0096-0217(16)30238-2,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1017_s1816383116000278,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1021_jacs.6b05099,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1024_2296-4924_a000010,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1027_0227-5910_a000389,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1029_2016eo053239,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1038_srep27030,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1039_c6tc01003b,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1042_bsr20160124,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1051_metal_2016016,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1053_j.ajkd.2016.04.015,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1055_s-006-31518,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1057_udi.2016.10,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1061_(asce)is.1943-555x.0000314,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1063_pt.3.3183,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1080_1533015x.2016.1181014,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1111_spsr.12213,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1158_2326-6066.cir-16-0089,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1166_jctn.2016.4873,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1177_1755738016647282,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.14387_jkspth.2016.49.67,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1515_fjsb-2010-0305,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.15611_pn.2016.422.14,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.1590_s0103-21862016000100014,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.16962_eapjpmc_issn.2394-9376_2015_v1i1.05,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.20448_journal.510_2016.3.1_510.1.32.37,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.21528_cbic2011-905,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.3161_15081109acc2016.18.1.017,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.3324_haematol.2016.148015,"
				+ " src/test/resources/org/xmlcml/download/sample/http_dx.doi.org_10.4000_geocarrefour.9765"
				+ "]",
				cTreeList.getCTreeDirectoryList().toString());
	}
}
