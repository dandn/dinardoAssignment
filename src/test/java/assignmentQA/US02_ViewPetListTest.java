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
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.openqa.selenium.support.events.EventFiringWebDriver;

@RunWith(Parameterized.class)
public class US02_ViewPetListTest {

 	private EventFiringWebDriver driver;
 	private WebDriverEventListenerClass eventListener;
	private Integer screenWidth;
	private Integer screenHeight;
	private Boolean recordCoverage;

	private class WebDriverEventListenerClass extends AbstractWebDriverEventListener {
		private long startTime, endTime;
		private boolean loadDone;

		public WebDriverEventListenerClass() {
			loadDone = false;
		}
		
		@Override
		public void beforeNavigateTo(String arg0, WebDriver arg1) {
			startTime = System.currentTimeMillis();
		}

        @Override
        public void afterNavigateTo(String arg0, WebDriver arg1) {
        		endTime = System.currentTimeMillis();
        		loadDone = true;
        	}
        
        public long getPageLoadTime() {
        		// get the page load time in milliseconds
        		return endTime - startTime;
        }
        
		public boolean isLoadDone() {
			return loadDone;
		}
	}	
	
	@Parameterized.Parameters
	public static Collection testParameters() {
		// screen width (pixels), screen height (pixels), recordCoverage
		return Arrays.asList(new Object[][] {
			{ 1366, 768, false },
			{ 400, 300, false }
		});
	}
	
	public US02_ViewPetListTest(Integer screenWidth, Integer screenHeight, Boolean recordCoverage) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.recordCoverage = recordCoverage;		
	}
	
	@Before
	public void setUp() throws Exception {
		InitializeEnvironment.configureChromeDriver();
		InitializeEnvironment.initializeUsingREST(100);
		
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("window-size="+this.screenWidth+","+this.screenHeight);
		chromeOptions.addArguments("disable-infobars");
		
		WebDriver chromeDriver = new ChromeDriver(chromeOptions);
		
		driver = new EventFiringWebDriver(chromeDriver);
		eventListener = new WebDriverEventListenerClass();
        driver.register(eventListener);
		
		driver.get(InitializeEnvironment.getWebAppLocation());
	}
	
	@After
	public void tearDown() throws Exception {
		driver.quit();
	}

	@Test
	public void testPageIsRenderedInLessThanTwoSecondsWith100Pets() {
		long loadTime = -1;
		int attempts = 20;
		do {
			if (eventListener.isLoadDone()) {
				loadTime = eventListener.getPageLoadTime();
				attempts = 0;
			}
			else {
				attempts--;
				try {
					Thread.sleep(200);
				} 
				catch (InterruptedException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		} while (attempts > 0);
		
		assertFalse("Assertion fails if page did not complete loading using the given number of 'attempts'.", loadTime == -1);
		assertTrue("The page did not load under two seconds.", loadTime < 2000);
		
		List<WebElement> listOfPets = driver.findElements(By.xpath("//tr[@class='pet-item']"));
		assertEquals("Make sure that the correct number of animals are in the list.", 100, listOfPets.size());
	}
		
	public void testTableRowEntryIsWellFormatted(String fieldType, int row) {
		List<WebElement> listOfPets = driver.findElements(By.xpath("//tr[@class='pet-item']"));	
		
		// get the relevant row entry in the "List of Pets"
		WebElement rowEntry = listOfPets.get(row);

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
		int widthSpan = spanElement.getSize().getWidth(); // 332
		int heightSpan = spanElement.getSize().getHeight(); // 34
			
		assertTrue("Check that the width of a span for " + fieldType + " is okay.", widthSpan >= 200);
		assertTrue("Check that the height of a span for " + fieldType + " is okay.", heightSpan == 34);
			//int widthInput = inputElement.getSize().getWidth(); // 181
			//int heightInput = inputElement.getSize().getHeight(); // 38
		
		Actions act = new Actions(driver);
		act.doubleClick(spanElement).build().perform();
		
		int widthEditInput = inputElement.getSize().getWidth(); // 266
		int heightEditInput = inputElement.getSize().getHeight(); // 38
		assertTrue("Check that the width of selected input for " + fieldType + " is okay.", widthEditInput >= 180);
		assertTrue("Check that the height of selected input for " + fieldType + " is okay.", heightEditInput == 38);
	}
	
	@Test
	public void testTablePetNameEntryIsWellFormatted() {
		testTableRowEntryIsWellFormatted("petName", 2);
	}
	
	@Test
	public void testTablePetStatusEntryIsWellFormatted() {
		testTableRowEntryIsWellFormatted("petStatus", 1);
	}

	@Test
	public void testTableFirstPetRowEntryIsVisibleOnStartup() {
		// just an idea: Check that the first pet row entry is visible when the browser opens
		// - i.e., the user doesn't have to scroll down to find it
		List<WebElement> listOfPets = driver.findElements(By.xpath("//tr[@class='pet-item']"));	
		
		// get the relevant row entry in the "List of Pets"
		WebElement rowEntry = listOfPets.get(0);

	    Dimension weD = rowEntry.getSize();
	    Point weP = rowEntry.getLocation();
	    Dimension d = driver.manage().window().getSize();

	    int x = d.getWidth();
	    int y = d.getHeight();
	    int x2 = weD.getWidth() + weP.getX();
	    int y2 = weD.getHeight() + weP.getY();

	    assertTrue(x2 <= x && y2 <= y);
	}
}
