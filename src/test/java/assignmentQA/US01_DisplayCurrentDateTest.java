package assignmentQA;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
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
import org.openqa.selenium.support.Color;

@RunWith(Parameterized.class)
public class US01_DisplayCurrentDateTest {
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
	
	public US01_DisplayCurrentDateTest(Integer screenWidth, Integer screenHeight, Boolean recordCoverage) {
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

	@Test
	public void testDateFormat() {
    		// should rather use unique IDs for elements of interest
    		WebElement bannerDateElement = driver.findElement(By.xpath("//span[@class='banner-date']/div"));
    		String bannerDate = bannerDateElement.getText();
    		String systemDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-YYYY")).toString();
    		
    		assertEquals("Check that the banner date matches the system date in the format DD-MM-YYYY.", systemDate, bannerDate);
	}
	
	@Test
	public void testBannerIsBlack() {
    		// is the current grey value returned actually acceptable? Maybe need to refine test plan.
    		WebElement assignmentMastHead = driver.findElement(By.xpath("//div[@class='assignment-masthead']"));
    		String backgroundColor = assignmentMastHead.getCssValue("background-color");
    		String hexBackgroundColor = Color.fromString(backgroundColor).asHex();
    		String hexExpectedColor = "#000000"; // the color black
    		
    		assertEquals("Check that the background banner is black.", hexExpectedColor, hexBackgroundColor);
	}
	
	@Test
	public void testDateIsRightJustified() {
		// further work might need to be done to consider inner/outer width of windows (e.g. including/excluding scroll bars)
		int windowWidth = driver.manage().window().getSize().getWidth();
		
		WebElement bannerDateElement = driver.findElement(By.xpath("//span[@class='banner-date']/div"));
		Point point = bannerDateElement.getLocation();
		Dimension size = bannerDateElement.getSize();
		
		// get coordinate of top right hand corner of banner date bounding box
		int xBannerDate = size.getWidth() + point.getX();
		int yBannerDate = point.getY();
		
		// check that above coordinate (xBannerDate, yBannerDate) falls in top right hand corner of display
		// - this coordinate should be within 50 pixels of the top right hand corner
		
		int xComparePoint = windowWidth - 50;
		int yComparePoint = 50;
		
		assertTrue("Check that y coordinate of banner date box is ok.", yBannerDate <= yComparePoint);
		assertTrue("Check that x coordinate of banner date box is ok.", xBannerDate >= xComparePoint);
	}

}
