package assignmentQA;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
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
import org.openqa.selenium.interactions.Actions;

@RunWith(Parameterized.class)
public class US04_ModifyExistingPetNameTest {

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
	
	public US04_ModifyExistingPetNameTest(Integer screenWidth, Integer screenHeight, Boolean recordCoverage) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.recordCoverage = recordCoverage;		
	}
	
	@Before
	public void setUp() throws Exception {
		InitializeEnvironment.configureChromeDriver();
		InitializeEnvironment.initializeUsingREST(6);
		
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
	
	public void testFieldModification(String rowItem, String fieldType, String newValue, String entryMethod, Boolean newEntryExpected) {
		
		// rowItem: "first", "middle", or "last"
		// fieldType: "petName", or "petStatus"
		// newValue: some new value string
		// entryMethod: "escape", "enter", or "clickOutside"
		// newEntryExpected: true or false (should the fieldType value have been modified)		
		
		// get the initial number of items in the list of pets
		List<WebElement> listOfPets = driver.findElements(By.xpath("//tr[@class='pet-item']"));	
		
		int index = -1;
		switch (rowItem) {
			case "first":
				index = 0;
				break;
			case "middle":
				index = listOfPets.size() / 2;
				break;
			case "last":
				index = listOfPets.size() - 1;
				break;
		}

		// get the relevant row entry in the "List of Pets"
		WebElement rowEntry = listOfPets.get(index);
		
		WebElement spanElement = null;
		WebElement inputElement = null;
		switch (fieldType) {
			case "petName":
				spanElement = rowEntry.findElement(By.xpath("td/span[@class='pet lbl pet-name']"));
				inputElement = rowEntry.findElement(By.xpath("td/input[@class='pet usr-input pet-name']"));
				break;
			case "petStatus":
				spanElement = rowEntry.findElement(By.xpath("td/span[@class='pet lbl pet-status']"));
				inputElement = rowEntry.findElement(By.xpath("td/input[@class='pet usr-input pet-status']"));	
				break;
		}

		//String initValue = inputElement.getAttribute("value");
		String initText = spanElement.getText();
		
		Actions act = new Actions(driver);
		act.doubleClick(spanElement).build().perform();
		
		inputElement.clear();
		inputElement.sendKeys(newValue);
		
		switch (entryMethod) {
			case "escape":
				inputElement.sendKeys(Keys.ESCAPE);
				break;
			case "enter":
				inputElement.sendKeys(Keys.ENTER);
				break;
			case "clickOutside":
				// click on some text on the browser
				WebElement listOfPetsText = driver.findElement(By.cssSelector("h3.card-title"));
				listOfPetsText.click();
				break;
		}

		String postText = "notInitialized";
        int attempts = 0;
        while (attempts < 5) {
        		try {
        			listOfPets = driver.findElements(By.xpath("//tr[@class='pet-item']"));
        			rowEntry = listOfPets.get(index);
		
        			switch (fieldType) {
        				case "petName":
        					spanElement = rowEntry.findElement(By.xpath("td/span[@class='pet lbl pet-name']"));
        					inputElement = rowEntry.findElement(By.xpath("td/input[@class='pet usr-input pet-name']"));
        					break;
        				case "petStatus":
        					spanElement = rowEntry.findElement(By.xpath("td/span[@class='pet lbl pet-status']"));
        					inputElement = rowEntry.findElement(By.xpath("td/input[@class='pet usr-input pet-status']"));	
        					break;
        			}
			
        			postText = spanElement.getText();
        			//String postValue = inputElement.getAttribute("value");
        			break;
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
		
		if (newEntryExpected) {
			Assert.assertNotEquals("Check that the initial value is not equal to the new value.", initText, postText);
			Assert.assertEquals("The new entry is expected in the targeted field.", newValue, postText);
		}
		else {
			Assert.assertEquals("No new entry is expected in the targeted field.", initText, postText);
		}
	}
	
	@Test
	public void testFieldModificationOfPetName_withEnter() {
		testFieldModification("middle", "petName", "Cat", "enter", true);
	}
	
	@Test
	public void testFieldModificationOfPetName_withClickOutside() {
		testFieldModification("middle", "petName", "Cat", "clickOutside", true);
	}
	
	@Test
	public void testFieldModificationOfPetName_withEscape() {
		testFieldModification("middle", "petName", "Cat", "escape", false);
	}
	
	@Test
	public void testFieldModificationOfPetStatus_withEnter() {
		testFieldModification("middle", "petStatus", "Happy", "enter", true);
	}
	
	@Test
	public void testFieldModificationOfPetStatus_withClickOutside() {
		testFieldModification("middle", "petStatus", "Happy", "clickOutside", true);
	}
	
	@Test
	public void testFieldModificationOfPetStatus_withEscape() {
		testFieldModification("middle", "petStatus", "Happy", "escape", false);
	}
	
	
	// check that fields can not be cleared : according to US03 pet name and pet status are MANDATORY
	@Test
	public void testFieldClearPetName_withEnter() {
		testFieldModification("middle", "petName", "", "enter", false);
	}
	
	@Test
	public void testFieldClearPetName_withClickOutside() {
		testFieldModification("middle", "petName", "", "clickOutside", false);
	}
	
	@Test
	public void testFieldClearPetStatus_withEnter() {
		testFieldModification("middle", "petStatus", "", "enter", false);
	}
	
	@Test
	public void testFieldClearPetStatus_withClickOutside() {
		testFieldModification("middle", "petStatus", "", "clickOutside", false);
	}
}
