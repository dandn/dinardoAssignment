package assignmentQA;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ US01_DisplayCurrentDateTest.class, US02_ViewPetListTest.class, US03_AddNewPetNameTest.class,
		US04_ModifyExistingPetNameTest.class, US05_CheckRESTAPITest.class })
public class AllTests {

}
