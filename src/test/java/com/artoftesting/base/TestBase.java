package com.artoftesting.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import io.github.bonigarcia.wdm.WebDriverManager;


/**
* The TestBase class is the base class to fetch environment specific configuration parameters from 
* Jenkins/Maven. Based on the parameters, it performs the browser setup and tear-down functions.
* 
* @author  Kuldeep Rana
*/


public class TestBase {
	
	protected static WebDriver driver;
	public static Properties envConfig;
	WebDriverWait wait;
	
	
	//Environment value fetched from POM with 'careersIn' and 'production' being the valid values 
	public static final String ENV = System.getProperty("env", "Production");

	//BROWSER value fetched from POM with Chrome being the default value 
	private static final String BROWSER = System.getProperty("browser", "Chrome");
	
		
	//Automation suite setup method to configure and instantiate a particular browser
	@BeforeSuite
    public void suiteSetup() throws Exception {
		
		//Browser configuration - can add more browsers and remote driver here
		if (BROWSER.equals("Firefox")) {
			WebDriverManager.firefoxdriver().setup(); //can also use set property method for browser executables
			driver = new FirefoxDriver();
         }
		else if (BROWSER.equals("Chrome")) {
			WebDriverManager.chromedriver().setup();
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--disable-notifications");
			driver = new ChromeDriver(options);             
         }
		 else if (BROWSER.equals("IE")) {
			 WebDriverManager.iedriver().setup();
             driver = new InternetExplorerDriver();
         }
		 else {
             throw new RuntimeException("Browser type unsupported");
         }
		
		//Setting implicit wait
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		driver.manage().window().maximize();
		
		//Setting WebDriverWait with max timeout value of 20 seconds
		wait = new WebDriverWait(driver, 20);

		//Environment specific properties file loading
		InputStream configFile = new FileInputStream(System.getProperty("user.dir") + 
				"\\src\\test\\java\\com\\artoftesting\\config\\" + ENV +  ".properties");		 
		envConfig = new Properties();
		envConfig.load(configFile);
	
	}

	
	@BeforeMethod()
    public void loadBaseUrl(Method method) {
        driver.get(envConfig.getProperty("baseUrl"));
        
    }
 

	@AfterMethod
	public void screenshotAndDeleteCookies(ITestResult testResult) throws IOException {
		//Taking screenshot in case of failure
		if(testResult.getStatus() == ITestResult.FAILURE){
			File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrFile, new File("errorScreenshots\\" + testResult.getName() + "-" 
					+ Arrays.toString(testResult.getParameters()) +  ".jpg"));	
			}
		
		//Deleting cookies
		driver.manage().deleteAllCookies();
	}
 

    @AfterSuite
    public void suiteTearDown() {
    	driver.quit();
    }

}
