package com.anhtester.keywords;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ActionKeyword {

   private static int WAIT_TIME = 5;
   private static int WAIT_PAGE_LOAD_TIME = 30;

   private static WebDriver driver;

   public ActionKeyword(WebDriver driver) {
      ActionKeyword.driver = driver;
   }

   public static void clickElement(WebDriver driver, By locator, int seconds) {
      JavascriptExecutor js = (JavascriptExecutor) driver;

      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
      wait.until(ExpectedConditions.elementToBeClickable(locator));

      js.executeScript("arguments[0].style.border='3px solid red'", driver.findElement(locator));
      driver.findElement(locator).click();
   }

   public static void clickElement(WebDriver driver, WebElement element, int seconds) {
      JavascriptExecutor js = (JavascriptExecutor) driver;

      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
      wait.until(ExpectedConditions.elementToBeClickable(element));

      js.executeScript("arguments[0].style.border='3px solid red'", element);
      element.click();
   }

   public static void clickElement(WebDriver driver, By locator) {
      JavascriptExecutor js = (JavascriptExecutor) driver;

      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME));
      WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));

      // Cuon element vao giua viewport TRUOC khi click (fix nut Save nam duoi fold)
      js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
      js.executeScript("arguments[0].style.border='3px solid red'", driver.findElement(locator));
      driver.findElement(locator).click();
   }

   public static void setText(WebDriver driver, By locator, String text) {
      JavascriptExecutor js = (JavascriptExecutor) driver;
      js.executeScript("arguments[0].style.border='3px solid red'", driver.findElement(locator));
      driver.findElement(locator).sendKeys(text);
   }

   public static void setText(WebDriver driver, By locator, String text, int seconds) {
      JavascriptExecutor js = (JavascriptExecutor) driver;
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
      wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
      js.executeScript("arguments[0].style.border='3px solid red'", driver.findElement(locator));
      driver.findElement(locator).sendKeys(text);
   }

   public static boolean isElementPresent(WebDriver driver, By locator) {
      try {
         driver.findElement(locator).isDisplayed();
      } catch (Exception ex) {
         return false;
      }
      return true;
   }

   public static boolean isElementPresent(WebDriver driver, By locator, int seconds) {
      try {
         WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
         wait.until(ExpectedConditions.presenceOfElementLocated(locator));
         driver.findElement(locator).isDisplayed();
      } catch (Exception ex) {
         return false;
      }
      return true;
   }

   public static boolean isElementVisible(WebDriver driver, WebElement element, int seconds) {
      try {
         WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
         wait.until(ExpectedConditions.visibilityOf(element));
         element.isDisplayed();
      } catch (Exception ex) {
         return false;
      }
      return true;
   }

   public static void waitForPageLoaded(WebDriver driver) {
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_PAGE_LOAD_TIME));
      ExpectedCondition<Boolean> jsLoad = webDriver ->
              ((JavascriptExecutor) webDriver)
                      .executeScript("return document.readyState")
                      .equals("complete");

      wait.until(jsLoad);

      waitForJQueryLoad(driver);
      waitForAngularLoad(driver);
   }

   private static void waitForJQueryLoad(WebDriver driver) {
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_PAGE_LOAD_TIME));
      try {
         wait.until(_driver -> (Boolean) ((JavascriptExecutor) driver)
                 .executeScript(
                         "return window.jQuery == undefined || jQuery.active == 0"));
      } catch (Exception ignored) {
      }
   }

   private static void waitForAngularLoad(WebDriver driver) {
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_PAGE_LOAD_TIME));
      try {
         wait.until(_driver -> (Boolean) ((JavascriptExecutor) driver)
                 .executeScript(
                         "return window.getAllAngularTestabilities ? " +
                                 "window.getAllAngularTestabilities()" +
                                 ".every(x=>x.isStable()) : true"));
      } catch (Exception ignored) {
      }
   }
}
