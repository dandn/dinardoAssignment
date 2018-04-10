The contents of this project contain two components: 
1) JUnit tests to address the requirements as outlined in the user stories.
2) JMeter tests to load test functional behaviour and measure performance.

My test project has been developed using the following software versions:
- macOS 10.12.6
- Apache Maven 3.5.0
- Eclipse 4.7.0

Required: ChromeDriver (tested with 2.37), https://sites.google.com/a/chromium.org/chromedriver<br/>
Required: JMeter (tested with Apache JMeter 4.0), http://jmeter.apache.org/download_jmeter.cgi<br/>
Required: Java 1.8 (tested with Java 1.8.0_144)

Assumption:
With reference to https://github.com/ace-study-group/assignment-qa/blob/master/README.md,
I assume that the test system is running the web app as described in the section “Run the app”.

<br/>
INSTRUCTIONS:

A) For the JUnit tests:<br/>
i) Set the correct locations for the ChromeDriver and the Petstore web URL, in file:<br/>
&nbsp;&nbsp;src/test/java/assignmentQA/InitializeEnvironment.java,<br/>
&nbsp;within the methods configureChromeDriver() and getWebAppLocation(), respectively.<br/>
ii) To run the tests enter `mvn test` at the command line prompt (or within Eclipse of course).<br/>
iii) The test results should be saved to the directory: target/surefire-reports.


B) For the JMeter tests:<br/>
i) Open the file performanceTests.sh.<br/>
ii) Set the correct parameter values for: JMETER_INSTALLDIR, IPADDRESS, PORT.<br/>
&nbsp;There are some other parameters that can optionally be modified - see code comments.<br/>
iii) To run the tests enter `./performanceTests.sh` at the command line prompt.<br/>
iv) The test results should be saved to the directory: JTL_TARGETDIR, as set within the file performanceTests.sh.
