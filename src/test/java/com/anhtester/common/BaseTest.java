package com.anhtester.common;

import com.anhtester.constants.ConfigData;
import com.anhtester.drivers.DriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.time.Duration;

public class BaseTest {

   @BeforeMethod
   @Parameters({"browser"})
   public void createDriver(@Optional("chrome") String browserName) {
      WebDriver driver;
      switch (browserName.trim().toLowerCase()) {
         case "chrome":
            System.out.println("Launching Chrome browser...");
            driver = new ChromeDriver();
            break;
         case "firefox":
            System.out.println("Launching Firefox browser...");
            driver = new FirefoxDriver();
            break;
         case "edge":
            System.out.println("Launching Edge browser...");
            driver = new EdgeDriver();
            break;
         default:
            System.out.println("Browser: " + browserName + " is invalid, Launching Chrome as browser of choice...");
            driver = new ChromeDriver();
      }

      DriverManager.setDriver(driver);

      DriverManager.getDriver().manage().window().maximize();
      DriverManager.getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
   }

   @AfterMethod
   public void closeDriver() {
      DriverManager.quit();
   }

   public void sleep(double seconds) {
      try {
         Thread.sleep((long) (seconds * 1000));
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
}
