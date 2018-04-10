package assignmentQA;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InitializeTests {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSmallDb() {
		// created this test to have an easy way to initialize db for performance tests
		InitializeEnvironment.initializeUsingREST(20);
	}

	@Test
	public void testMediumDb() {
		// created this test to have an easy way to initialize db for performance tests
		InitializeEnvironment.initializeUsingREST(1000);
	}
}
