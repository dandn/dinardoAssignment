package assignmentQA;

import static org.junit.Assert.*;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import jscover.Main;

@RunWith(Parameterized.class)
public class AttemptToRunJCover {
    private static Thread server;
    private static Main main = new Main();
    private static String reportDir = "target/reports/jscover-localstorage-general";
    private final static String[] args = new String[]{
            "-ws",
            "--port=3129",
            "--proxy",
            //"--local-storage",
            //"--no-instrument=test/vendor",
            "--js-version=ECMASCRIPT7",
            "--report-dir=" + reportDir
    };

    private final WebDriver webClient = getWebClient();	
	
    private WebDriver getWebClient() {
        Proxy proxy = new Proxy().setHttpProxy("localhost:3129");
        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability(CapabilityType.PROXY, proxy);
        
		System.setProperty("webdriver.chrome.driver", "/Users/ddinardo/Documents/ChromeDriver/chromedriver");
		
		ChromeOptions chromeOptions = new ChromeOptions();

		chromeOptions.addArguments("window-size="+this.screenWidth+","+this.screenHeight);
		chromeOptions.addArguments("disable-infobars");
		//chromeOptions.addArguments("headless");
		chromeOptions.addArguments("proxy-server="+"localhost:3129");
		
		return new ChromeDriver(chromeOptions);
        
        //return new ChromeDriver(cap);
    }
	
    @BeforeClass
    public static void setUpOnce() {
		InitializeEnvironment.configureChromeDriver();
        server = new Thread(new Runnable() {
            public void run() {
                main.runMain(args);
            }
        });
        server.start();
    }
    
    @AfterClass
    public static void tearDownOnce() {
        main.stop();
    }
	
	
 	//private WebDriver driver;
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
	
	public AttemptToRunJCover(Integer screenWidth, Integer screenHeight, Boolean recordCoverage) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.recordCoverage = recordCoverage;		
	}
	
    @Before
    public void setUp2() {
		InitializeEnvironment.initializeUsingREST(10);
        File jsonFile = new File(reportDir+"/jscoverage.json");
        if (jsonFile.exists())
            jsonFile.delete();
        webClient.get(InitializeEnvironment.getWebAppLocation());
    }
	
	@Before
	public void setUp() throws Exception {
		//System.setProperty("webdriver.chrome.driver", "/Users/ddinardo/Documents/ChromeDriver/chromedriver");
		
		//ChromeOptions chromeOptions = new ChromeOptions();

		//chromeOptions.addArguments("window-size="+this.screenWidth+","+this.screenHeight);
		//chromeOptions.addArguments("disable-infobars");
		//chromeOptions.addArguments("headless");
		
		//webClient = new ChromeDriver(chromeOptions);
		//webClient.get("http://localhost:3000/");
	}

	@After
	public void tearDown() throws Exception {
		//webClient.quit();
	}
	
    @After
    public void tearDown2() {
        try {
            webClient.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        try {
            webClient.quit();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

	@Test
	public void testDateFormat() {
    		// should rather use a unique id
    		WebElement bannerDateElement = webClient.findElement(By.xpath("//span[@class='banner-date']/div"));
    		String bannerDate = bannerDateElement.getText();
    		String systemDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-YYYY")).toString();
    		
    		assertEquals(systemDate, bannerDate);
	}
	
	@Test
	public void testBannerIsBlack() {
    		// is the grey value returned actually acceptable? Maybe need to refine test plan.
    		WebElement assignmentMastHead = webClient.findElement(By.xpath("//div[@class='assignment-masthead']"));
    		String backgroundColor = assignmentMastHead.getCssValue("background-color");
    		String hexBackgroundColor = Color.fromString(backgroundColor).asHex();
    		String hexExpectedColor = "#000000"; // the color black
    		
    		assertEquals(hexExpectedColor, hexBackgroundColor);
	}


}
