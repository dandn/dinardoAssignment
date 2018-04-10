package assignmentQA;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@RunWith(Parameterized.class)
public class US03_AddNewPetNameTest {

 	private WebDriver driver;
	private Integer screenWidth;
	private Integer screenHeight;
	private Boolean recordCoverage;

	@Parameterized.Parameters
	public static Collection testParameters() {
		// screen width (pixels), screen height (pixels), recordCoverage
		return Arrays.asList(new Object[][] {
			{ 1366, 768, false },
			{ 400, 300, false }
		});
	}
	
	public US03_AddNewPetNameTest(Integer screenWidth, Integer screenHeight, Boolean recordCoverage) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.recordCoverage = recordCoverage;		
	}
	
	@Before
	public void setUp() throws Exception {
		InitializeEnvironment.configureChromeDriver();
		InitializeEnvironment.initializeUsingREST(10);
		
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("window-size="+this.screenWidth+","+this.screenHeight);
		chromeOptions.addArguments("disable-infobars");
		
		driver = new ChromeDriver(chromeOptions);
		driver.get(InitializeEnvironment.getWebAppLocation());
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
	}

	public void testCreateNewPet(String petName, String petStatus, String entryMethod, Boolean newEntryExpected) {
		// get the initial number of items in the list of pets
		List<WebElement> listOfPets = driver.findElements(By.xpath("//tr[@class='pet-item']"));	
		int initialNumberOfPets = listOfPets.size();
		
		// get "Pet Name" text field
		WebElement petNameElement = driver.findElement(By.xpath("//input[@class='form-control pet-name']"));
		// get "Pet Status" text field
		WebElement petStatusElement = driver.findElement(By.xpath("//input[@class='form-control pet-status']"));
		// get "Create" button
		WebElement createButton = driver.findElement(By.id("btn-create"));
		
		// enter the test values
		if (petName != null) {
			petNameElement.sendKeys(petName);
		}
		if (petStatus != null) {
			petStatusElement.sendKeys(petStatus);
		}
		
		switch (entryMethod) {
			case "petName":
				petNameElement.sendKeys(Keys.ENTER);
				break;
			case "petStatus":
				petStatusElement.sendKeys(Keys.ENTER);
				break;
			case "create":
				createButton.click();
				break;
		}
		
		String postText = "notInitialized";
        int attempts = 0;
        while (attempts < 5) {
        		try {
				listOfPets = driver.findElements(By.xpath("//tr[@class='pet-item']"));
				int numberOfPets = listOfPets.size();
				
				if (newEntryExpected) {
					assertEquals(initialNumberOfPets+1, numberOfPets);
				}
				else {
					assertEquals(initialNumberOfPets, numberOfPets);
				}
				
				// check that the values are present in the last row entry in the "List of Pets"
				WebElement lastEntry = listOfPets.get(listOfPets.size()-1);
				
				WebElement spanElementPetName = lastEntry.findElement(By.xpath("td/span[@class='pet lbl pet-name']"));
				String text = spanElementPetName.getText();
				assertEquals(petName, text);
		
				WebElement inputElementPetName = lastEntry.findElement(By.xpath("td/input[@class='pet usr-input pet-name']"));
				String value = inputElementPetName.getAttribute("value");
				assertEquals(petName, value);
				
				WebElement spanElementPetStatus = lastEntry.findElement(By.xpath("td/span[@class='pet lbl pet-status']"));
				text = spanElementPetStatus.getText();
				assertEquals(petStatus, text);
		
				WebElement inputElementPetStatus = lastEntry.findElement(By.xpath("td/input[@class='pet usr-input pet-status']"));
				value = inputElementPetStatus.getAttribute("value");
				assertEquals(petStatus, value);
        		}
        		catch (StaleElementReferenceException e) {
        			try {
					Thread.sleep(100);
				}
        			catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        		}
        		attempts++;
		}
	}
	
	// start valid entry cases
	@Test
	public void testValidPetNameAndValidPetStatus_withEnterOnPetName() {
		testCreateNewPet("Cat1", "Happy", "petName", true);
	}
	
	@Test
	public void testValidPetNameAndValidPetStatus_withEnterOnPetStatus() {
		testCreateNewPet("Cat2", "Grumpy", "petStatus", true);
	}
	
	@Test
	public void testValidPetNameAndValidPetStatus_withCreate() {
		testCreateNewPet("Cat3", "Tired", "create", true);
	}
	// end valid entry cases
	
	
	// start invalid entry cases
	@Test
	public void testValidPetNameAndEmptyPetStatus_withEnterOnPetName() {
		testCreateNewPet("Cat4", null, "petName", false);
	}
	
	@Test
	public void testEmptyPetNameAndValidPetStatus_withEnterOnPetName() {
		testCreateNewPet(null, "Curious", "petName", false);
	}
	
	@Test
	public void testEmptyPetNameAndEmptyPetStatus_withEnterOnPetName() {
		testCreateNewPet(null, null, "petName", false);
	}
	
	@Test
	public void testValidPetNameAndEmptyPetStatus_withEnterOnPetStatus() {
		testCreateNewPet("Cat4", null, "petStatus", false);
	}
	
	@Test
	public void testEmptyPetNameAndValidPetStatus_withEnterOnPetStatus() {
		testCreateNewPet(null, "Curious", "petStatus", false);
	}
	
	@Test
	public void testEmptyPetNameAndEmptyPetStatus_withEnterOnPetStatus() {
		testCreateNewPet(null, null, "petStatus", false);
	}	
	
	@Test
	public void testValidPetNameAndEmptyPetStatus_withCreate() {
		testCreateNewPet("Cat4", null, "create", false);
	}
	
	@Test
	public void testEmptyPetNameAndValidPetStatus_withCreate() {
		testCreateNewPet(null, "Curious", "create", false);
	}
	
	@Test
	public void testEmptyPetNameAndEmptyPetStatus_withCreate() {
		testCreateNewPet(null, null, "create", false);
	}
	// end invalid entry cases
	
	
	@Test
	public void testForwardTabOrder() {
		// find the elements of interest
		WebElement petNameElement = driver.findElement(By.xpath("//input[@class='form-control pet-name']"));
		WebElement petStatusElement = driver.findElement(By.xpath("//input[@class='form-control pet-status']"));
		WebElement createButton = driver.findElement(By.id("btn-create"));
		
		// place the focus on petName
		petNameElement.click();
		assertTrue("check \"Pet Name\" field has focus", 
				petNameElement.equals(driver.switchTo().activeElement()));
		
		// enter TAB
		driver.switchTo().activeElement().sendKeys(Keys.TAB);
		assertTrue("check \"Pet Status\" field has focus", 
				petStatusElement.equals(driver.switchTo().activeElement()));

		// enter TAB
		driver.switchTo().activeElement().sendKeys(Keys.TAB);
		assertTrue("check \"Create\" button has focus", 
				createButton.equals(driver.switchTo().activeElement()));
	}
	
	@Test
	public void testReverseTabOrder() {
		// find the elements of interest
		WebElement petNameElement = driver.findElement(By.xpath("//input[@class='form-control pet-name']"));
		WebElement petStatusElement = driver.findElement(By.xpath("//input[@class='form-control pet-status']"));
		
		// place the focus on petName
		petStatusElement.click();
		assertTrue("check \"Pet Status\" field has focus", 
				petStatusElement.equals(driver.switchTo().activeElement()));
		
		// enter SHIFT + TAB
		String selectAll = Keys.chord(Keys.SHIFT, Keys.TAB);
		driver.switchTo().activeElement().sendKeys(selectAll);
		assertTrue("check \"Pet Name\" field has focus",
				petNameElement.equals(driver.switchTo().activeElement()));
	}
}
