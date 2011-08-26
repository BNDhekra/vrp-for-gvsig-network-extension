package com.iver.cit.gvsig.graphtests;


import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.gvsig.graph.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestJGraphT_BAD_SHORTEST_PATH.class);
		suite.addTestSuite(TestAngle.class);
		//$JUnit-END$
		return suite;
	}

}


