package com.anhtester.keywords;

import com.anhtester.constants.ConfigData;
import com.anhtester.drivers.DriverManager;
import com.anhtester.helpers.CaptureHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class WebUI {

   private static final Logger log = LoggerFactory.getLogger(WebUI.class);

   private static int EXPLICIT_WAIT_TIMEOUT = 10;
   private static double STEP_TIME = 0;
   private static int PAGE_LOAD_TIMEOUT = 20;
   private static int RETRY_TIMEOUT = 15;
   private static int RETRY_POLLING_MILLIS = 500;
   //Thời gian chờ kết quả cho MỖI lần gõ từ khoá vào ô search ajax
   private static int SEARCH_TIMEOUT = 5;

   //Retry - Thao tác với thành phần giao diện tự vẽ lại DOM (Datatable, modal...)

   /**
    * Tạo vòng chờ có thể thử lại, bỏ qua các lỗi mang tính nhất thời.
    * Dùng khi thao tác trên thành phần tự vẽ lại DOM như Datatable: node tìm được ở vòng trước
    * có thể bị thay mới ở vòng sau, vòng chờ này sẽ tự tìm lại thay vì ném lỗi ra ngoài.
    */
   public static Wait<WebDriver> retryWait() {
      return new FluentWait<>(DriverManager.getDriver())
              .withTimeout(Duration.ofSeconds(RETRY_TIMEOUT))
              .pollingEvery(Duration.ofMillis(RETRY_POLLING_MILLIS))
              .ignoring(NoSuchElementException.class)
              .ignoring(StaleElementReferenceException.class)
              .ignoring(ElementClickInterceptedException.class)
              //Element có trong DOM nhưng chưa thao tác được: đang ẩn, đang chạy hiệu ứng,
              //hoặc là link chỉ hiện khi hover mà trạng thái hover chưa kịp ăn.
              .ignoring(ElementNotInteractableException.class);
   }

   /**
    * Lặp lại điều kiện cho tới khi trả về giá trị khác null và khác false.
    * Mọi element phải được tìm lại BÊN TRONG điều kiện, không truyền WebElement từ ngoài vào,
    * vì WebElement chỉ là tham chiếu tới node cũ và không tự tìm lại khi node đó bị thay mới.
    *
    * @param condition Điều kiện cần lặp lại, nhận WebDriver và trả về kết quả mong muốn
    * @return Giá trị mà điều kiện trả về khi thoả
    */
   public static <T> T retryUntil(Function<WebDriver, T> condition) {
      return retryWait().until(condition);
   }

   //Search - Ô tìm kiếm ajax của selectpicker (Customer, Project, Related To...)

   /**
    * Gõ từ khoá vào ô search ajax của selectpicker rồi chờ option hiện ra.
    * Loại ô search này rất hay hụt: plugin chỉ gắn handler ajax sau khi dropdown mở xong,
    * gõ sớm hơn thì không có request nào được bắn đi và danh sách trống trơn.
    * Vì vậy nếu hết thời gian chờ mà option chưa hiện, hàm sẽ xoá sạch ô search và gõ lại từ đầu,
    * lặp tối đa maxRetries lần trước khi báo fail.
    *
    * @param buttonDropdown Nút mở dropdown, dùng để mở lại nếu dropdown bị đóng giữa chừng
    * @param searchBox      Ô nhập từ khoá bên trong dropdown
    * @param option         Option cần chờ hiện ra sau khi search
    * @param keyword        Từ khoá cần gõ
    * @param maxRetries     Số lần được phép gõ lại
    * @return Option đã hiện ra
    */
   public static WebElement searchSelectPickerOption(By buttonDropdown, By searchBox, By option, String keyword, int maxRetries) {
      return searchSelectPickerOption(buttonDropdown, searchBox, option, keyword, maxRetries, SEARCH_TIMEOUT);
   }

   public static WebElement searchSelectPickerOption(By buttonDropdown, By searchBox, By option, String keyword, int maxRetries, int timeOutPerTry) {
      for (int attempt = 1; attempt <= maxRetries; attempt++) {
         openDropdownIfClosed(buttonDropdown, searchBox);

         //Xoá sạch rồi gõ lại từ đầu để ép plugin bắn một request ajax mới
         retryUntil(_driver -> {
            WebElement input = _driver.findElement(searchBox);
            input.clear();
            input.sendKeys(keyword);
            return true;
         });

         try {
            WebDriverWait waitOption = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeOutPerTry), Duration.ofMillis(500));
            WebElement found = waitOption.until(ExpectedConditions.visibilityOfElementLocated(option));
            logConsole("✅ Thấy kết quả '" + keyword + "' ở lần search thứ " + attempt + "/" + maxRetries);
            return found;
         } catch (Throwable error) {
            logConsole("⚠️ Lần search thứ " + attempt + "/" + maxRetries + " chưa thấy '" + keyword + "', xoá ô search và gõ lại.");
         }
      }

      logConsole("❌ Không tìm thấy '" + keyword + "' sau " + maxRetries + " lần search.");
      Assert.fail("FAILED. Không tìm thấy kết quả '" + keyword + "' sau " + maxRetries + " lần search. Option: " + option);
      return null;
   }

   /**
    * Chỉ bấm mở dropdown khi ô search đang không hiển thị.
    * Bấm khi dropdown đang mở sẽ làm nó đóng lại, nên phải kiểm tra trước.
    */
   private static void openDropdownIfClosed(By buttonDropdown, By searchBox) {
      boolean isOpened = false;
      try {
         List<WebElement> searchBoxes = getWebElements(searchBox);
         isOpened = !searchBoxes.isEmpty() && searchBoxes.get(0).isDisplayed();
      } catch (StaleElementReferenceException ignored) {
      }

      if (!isOpened) {
         clickElement(buttonDropdown);
         waitForElementVisible(searchBox);
      }
   }

   //Wait for Element

   public static void waitForElementVisible(By by) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(EXPLICIT_WAIT_TIMEOUT), Duration.ofMillis(500));
         wait.until(ExpectedConditions.visibilityOfElementLocated(by));
      } catch (Throwable error) {
         logConsole("Timeout waiting for the element Visible. " + by.toString());
         Assert.fail("Timeout waiting for the element Visible. " + by.toString());
      }
   }

   public static void waitForElementVisible(By by, int timeOut) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeOut), Duration.ofMillis(500));
         wait.until(ExpectedConditions.visibilityOfElementLocated(by));
      } catch (Throwable error) {
         logConsole("Timeout waiting for the element Visible. " + by.toString());
         Assert.fail("Timeout waiting for the element Visible. " + by.toString());
      }
   }

   public static void waitForElementInVisible(By by) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(EXPLICIT_WAIT_TIMEOUT), Duration.ofMillis(500));
         wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
      } catch (Throwable error) {
         logConsole("Timeout waiting for the element In Visible. " + by.toString());
         Assert.fail("Timeout waiting for the element In Visible. " + by.toString());
      }
   }

   public static void waitForElementInVisible(By by, int timeOut) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeOut), Duration.ofMillis(500));
         wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
      } catch (Throwable error) {
         logConsole("Timeout waiting for the element In Visible. " + by.toString());
         Assert.fail("Timeout waiting for the element In Visible. " + by.toString());
      }
   }

   public static void waitForElementPresent(By by) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(EXPLICIT_WAIT_TIMEOUT), Duration.ofMillis(500));
         wait.until(ExpectedConditions.presenceOfElementLocated(by));
      } catch (Throwable error) {
         logConsole("Element not exist. " + by.toString());
         Assert.fail("Element not exist. " + by.toString());
      }
   }

   public static void waitForElementPresent(By by, int timeOut) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeOut), Duration.ofMillis(500));
         wait.until(ExpectedConditions.presenceOfElementLocated(by));
      } catch (Throwable error) {
         logConsole("Element not exist. " + by.toString());
         Assert.fail("Element not exist. " + by.toString());
      }
   }

   public static void waitForElementClickable(By by) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(EXPLICIT_WAIT_TIMEOUT), Duration.ofMillis(500));
         //Truyền By chứ KHÔNG truyền WebElement: bản nhận By sẽ tìm lại element ở mỗi vòng poll,
         //còn bản nhận WebElement giữ mãi node cũ, node đó bị thay mới là chờ tới hết giờ vô ích.
         wait.until(ExpectedConditions.elementToBeClickable(by));
      } catch (Throwable error) {
         logConsole("Timeout waiting for the element ready to click. " + by.toString());
         Assert.fail("Timeout waiting for the element ready to click. " + by.toString());
      }
   }

   public static void waitForElementClickable(By by, int timeOut) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeOut), Duration.ofMillis(500));
         wait.until(ExpectedConditions.elementToBeClickable(by));
      } catch (Throwable error) {
         logConsole("Timeout waiting for the element ready to click. " + by.toString());
         Assert.fail("Timeout waiting for the element ready to click. " + by.toString());
      }
   }

   public static void waitForAlertIsPresent() {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(EXPLICIT_WAIT_TIMEOUT), Duration.ofMillis(500));
         wait.until(ExpectedConditions.alertIsPresent());
      } catch (Throwable error) {
         logConsole("Timeout waiting for Alert is present. " + error.toString());
         Assert.fail("Timeout waiting for Alert is present. " + error.toString());
      }

   }

   public static void waitForCurrentURLContains(String url) {
      logConsole("Current URL: " + DriverManager.getDriver().getCurrentUrl());
      logConsole("Waiting for the current URL contains: " + url);
      WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(EXPLICIT_WAIT_TIMEOUT), Duration.ofMillis(500));
      wait.until(ExpectedConditions.urlContains(url));
   }

   /**
    * Chờ URL khớp biểu thức chính quy.
    * Dùng khi "chứa" là chưa đủ để phân biệt hai trang: ví dụ trang thêm mới là /clients/client
    * còn trang chi tiết là /clients/client/123, chờ theo contains sẽ khớp nhầm ngay trang thêm mới.
    */
   public static void waitForCurrentURLMatches(String regex) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(EXPLICIT_WAIT_TIMEOUT), Duration.ofMillis(500));
         wait.until(ExpectedConditions.urlMatches(regex));
      } catch (Throwable error) {
         logConsole("Timeout chờ URL khớp '" + regex + "'. URL hiện tại: " + DriverManager.getDriver().getCurrentUrl());
         Assert.fail("Timeout chờ URL khớp '" + regex + "'. URL hiện tại: " + DriverManager.getDriver().getCurrentUrl());
      }
   }

   //Chờ đợi trang load xong mới thao tác
   public static void waitForPageLoaded() {
      WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(PAGE_LOAD_TIMEOUT), Duration.ofMillis(500));
      JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();

      //Wait for Javascript to load
      ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
         @Override
         public Boolean apply(WebDriver _driver) {
            return js.executeScript("return document.readyState").toString().equals("complete");
         }
      };

      //Check JS is Ready
      boolean jsReady = js.executeScript("return document.readyState").toString().equals("complete");

      //Wait Javascript until it is Ready!
      if (!jsReady) {
         //System.out.println("Javascript is NOT Ready.");
         //Wait for Javascript to load
         try {
            wait.until(jsLoad);
         } catch (Throwable error) {
            error.printStackTrace();
            Assert.fail("FAILED. Timeout waiting for page load.");
         }
      }

      //document.readyState = complete chỉ nói HTML đã tải xong,
      //các request ajax chạy sau đó vẫn có thể đang vẽ lại giao diện nên phải chờ tiếp.
      waitForJQueryLoad();
      waitForAngularLoad();
   }

   /**
    * Chờ jQuery chạy xong toàn bộ request ajax đang treo (jQuery.active == 0).
    * Cần thiết với các thành phần tự vẽ lại DOM như Datatable, nếu không
    * các element tìm được trước đó sẽ bị stale khi bảng vẽ lại.
    */
   public static void waitForJQueryLoad() {
      WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(PAGE_LOAD_TIMEOUT));
      try {
         wait.until(_driver -> (Boolean) ((JavascriptExecutor) DriverManager.getDriver())
                 .executeScript(
                         "return window.jQuery == undefined || jQuery.active == 0"));
      } catch (Exception ignored) {
      }
   }

   public static void waitForAngularLoad() {
      WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(PAGE_LOAD_TIMEOUT));
      try {
         wait.until(_driver -> (Boolean) ((JavascriptExecutor) DriverManager.getDriver())
                 .executeScript(
                         "return window.getAllAngularTestabilities ? " +
                                 "window.getAllAngularTestabilities()" +
                                 ".every(x=>x.isStable()) : true"));
      } catch (Exception ignored) {
      }
   }

   public static void sleep(double second) {
      try {
         Thread.sleep((long) (1000 * second));
      } catch (InterruptedException e) {
         throw new RuntimeException(e);
      }
   }

   public static void logConsole(Object message) {
      System.out.println(message);
   }

   public static WebElement getWebElement(By by) {
      return DriverManager.getDriver().findElement(by);
   }

   public static List<WebElement> getWebElements(By by) {
      return DriverManager.getDriver().findElements(by);
   }

   /**
    * Verify if a web element is present (findElements.size > 0).
    *
    * @param by Represent a web element as the By object
    * @return true/false
    */
   public static boolean checkElementExist(By by) {
      boolean result = false;

      List<WebElement> elementList = getWebElements(by);
      if (elementList.size() > 0) {
         System.out.println("✅ Element " + by + " existing.");
         result = true;
      } else {
         System.out.println("❌ Element " + by + " NOT exists.");
         result = false;
      }
      return result;
   }

   // Hàm kiểm tra sự tồn tại của phần tử với lặp lại nhiều lần dùng FluentWait
   public static boolean checkElementExist(By by, int maxRetries, int waitTimeMillis) {
      System.out.println("Kiểm tra tồn tại phần tử với retry: " + by);

      long totalTimeoutMillis = (long) maxRetries * waitTimeMillis;

      try {
         // FluentWait tương tự như vòng lặp của bạn nhưng hiệu quả hơn,
         // không block thread của hệ thống bằng Thread.sleep().
         Wait<WebDriver> wait = new FluentWait<>(DriverManager.getDriver())
                 .withTimeout(Duration.ofMillis(totalTimeoutMillis)) // Tổng thời gian chờ tối đa
                 .pollingEvery(Duration.ofMillis(waitTimeMillis)) // Tần suất lặp lại (Polling)
                 .ignoring(NoSuchElementException.class) // Tiếp tục lặp nếu không tìm thấy element
                 .ignoring(StaleElementReferenceException.class); // Tiếp tục lặp nếu element bị thay đổi

         WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));

         if (element != null) {
            System.out.println("✅ Tồn tại phần tử: " + by);
            return true;
         }
      } catch (TimeoutException e) {
         System.out.println("❌ Không tìm thấy phần tử sau " + maxRetries + " lần thử.");
         return false;
      }
      return false;
   }

   public static void openURL(String url) {
      DriverManager.getDriver().get(url);
      sleep(STEP_TIME);
      logConsole("\uD83C\uDF10 Open URL:  " + url);
   }

   public static String getCurrentURL() {
      logConsole("Current URL: " + DriverManager.getDriver().getCurrentUrl());
      return DriverManager.getDriver().getCurrentUrl();
   }

   public static void clickElement(By by) {
      waitForElementClickable(by);
      sleep(STEP_TIME);
      //Tìm lại element ngay trước khi click, và thử lại nếu node bị thay mới đúng lúc đó
      retryUntil(_driver -> {
         _driver.findElement(by).click();
         return true;
      });
      logConsole("Click on element " + by);

      if(ConfigData.SCREENSHOT_ALL_STEPS.equalsIgnoreCase("true")){
         CaptureHelper.captureScreenshot("clickElement");
      }
   }

   public static void clickElement(By by, int timeout) {
      waitForElementClickable(by, timeout);
      sleep(STEP_TIME);
      retryUntil(_driver -> {
         _driver.findElement(by).click();
         return true;
      });
      logConsole("Click on element " + by);

      if(ConfigData.SCREENSHOT_ALL_STEPS.equalsIgnoreCase("true")){
         CaptureHelper.captureScreenshot("clickElement");
      }
   }

   /**
    * LƯU Ý: hàm này có clear() trước khi gõ.
    * Bắt buộc phải có, vì nếu element bị thay mới giữa chừng thì lần thử lại sẽ gõ đè lên
    * phần chữ đã gõ dở, cho ra chuỗi kiểu "helhello". Clear trước thì gõ lại bao nhiêu lần
    * kết quả vẫn đúng bằng value.
    */
   public static void setText(By by, String value) {
      waitForElementVisible(by);
      sleep(STEP_TIME);
      retryUntil(_driver -> {
         WebElement element = _driver.findElement(by);
         element.clear();
         element.sendKeys(value);
         return true;
      });
      logConsole("Set text " + value + " on element " + by);

      if(ConfigData.SCREENSHOT_ALL_STEPS.equalsIgnoreCase("true")){
         CaptureHelper.captureScreenshot("setText");
      }
   }

   public static void setTextAndKey(By by, String value, Keys key) {
      waitForPageLoaded();

      retryUntil(
              _driver -> {
                 WebElement input = _driver.findElement(by);
                 input.sendKeys(value, key);
                 return true;
              }
      );

      //waitForElementVisible(by);
      //getWebElement(by).sendKeys(value, key);
      System.out.println("Set text: " + value + " on element " + by);

      if(ConfigData.SCREENSHOT_ALL_STEPS.equalsIgnoreCase("true")){
         CaptureHelper.captureScreenshot("setText");
      }
   }

   public static String getElementText(By by) {
      waitForElementVisible(by);
      sleep(STEP_TIME);
      logConsole("Get text of element " + by);
      String text = retryUntil(_driver -> _driver.findElement(by).getText());
      logConsole("==> TEXT: " + text);

      if(ConfigData.SCREENSHOT_ALL_STEPS.equalsIgnoreCase("true")){
         CaptureHelper.captureScreenshot("getElementText");
      }

      return text; //Trả về một giá trị kiểu String
   }

   public static String getElementAttribute(By by, String attributeName) {
      waitForElementVisible(by);
      logConsole("Get attribute " + attributeName + " of element " + by);
      String value = retryUntil(_driver -> Optional.ofNullable(_driver.findElement(by).getAttribute(attributeName)))
              .orElse(null);
      logConsole("==> Attribute value: " + value);
      return value;
   }

   public static String getElementCssValue(By by, String cssPropertyName) {
      waitForElementVisible(by);
      System.out.println("Get CSS value " + cssPropertyName + " of element " + by);
      String value = retryUntil(_driver -> _driver.findElement(by).getCssValue(cssPropertyName));
      System.out.println("==> CSS value: " + value);
      return value;
   }

   public static void smartWait() {
      waitForPageLoaded();
      sleep(STEP_TIME);
   }

   public static void scrollToElement(By by) {
      JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
      js.executeScript("arguments[0].scrollIntoView(false);", getWebElement(by));
   }

   public static void scrollToElement(WebElement element) {
      JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
      js.executeScript("arguments[0].scrollIntoView(false);", element);
   }

   public static void scrollToElementAtTop(By by) {
      JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
      js.executeScript("arguments[0].scrollIntoView(true);", getWebElement(by));
   }

   public static void scrollToElementAtBottom(By by) {
      JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
      js.executeScript("arguments[0].scrollIntoView(false);", getWebElement(by));
   }

   public static void scrollToElementAtTop(WebElement element) {
      JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
      js.executeScript("arguments[0].scrollIntoView(true);", element);
   }

   public static void scrollToElementAtBottom(WebElement element) {
      JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
      js.executeScript("arguments[0].scrollIntoView(false);", element);
   }

   /**
    * Scroll an element into the visible area of the browser window. (at CENTER)
    *
    * @param by Represent a web element as the By object
    */
   public static void scrollToElementAtCenter(By by) {
      smartWait();
      JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
      js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: "
                      + "'center', inline: 'center'});",
              getWebElement(by));
      logConsole("Scroll to element completely centered: " + by);
   }

   /**
    * Scroll an element into the visible area of the browser window. (at CENTER)
    *
    * @param webElement Represent a web element as the By object
    */
   public static void scrollToElementAtCenter(WebElement webElement) {
      smartWait();
      JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
      js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: "
                      + "'center', inline: 'center'});",
              webElement);
      logConsole("Scroll to element completely centered: " + webElement);
   }

   public static void scrollToPosition(int X, int Y) {
      JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
      js.executeScript("window.scrollTo(" + X + "," + Y + ");");
   }

   public static boolean moveToElement(By by) {
      try {
         Actions action = new Actions(DriverManager.getDriver());
         action.moveToElement(getWebElement(by)).release(getWebElement(by)).build().perform();
         return true;
      } catch (Exception e) {
         logConsole(e.getMessage());
         return false;
      }
   }

   public static boolean moveToOffset(int X, int Y) {
      try {
         Actions action = new Actions(DriverManager.getDriver());
         action.moveByOffset(X, Y).build().perform();
         return true;
      } catch (Exception e) {
         logConsole(e.getMessage());
         return false;
      }
   }

   public static boolean hoverElement(By by) {
      try {
         Actions action = new Actions(DriverManager.getDriver());
         action.moveToElement(getWebElement(by)).perform();
         return true;
      } catch (Exception e) {
         return false;
      }
   }

   public static boolean mouseHover(By by) {
      try {
         Actions action = new Actions(DriverManager.getDriver());
         action.moveToElement(getWebElement(by)).perform();
         return true;
      } catch (Exception e) {
         return false;
      }
   }

   public static boolean dragAndDrop(By fromElement, By toElement) {
      try {
         Actions action = new Actions(DriverManager.getDriver());
         action.dragAndDrop(getWebElement(fromElement), getWebElement(toElement)).perform();
         //action.clickAndHold(getWebElement(fromElement)).moveToElement(getWebElement(toElement)).release(getWebElement(toElement)).build().perform();
         return true;
      } catch (Exception e) {
         logConsole(e.getMessage());
         return false;
      }
   }

   public static boolean dragAndDropElement(By fromElement, By toElement) {
      try {
         Actions action = new Actions(DriverManager.getDriver());
         action.clickAndHold(getWebElement(fromElement)).moveToElement(getWebElement(toElement)).release(getWebElement(toElement)).build().perform();
         return true;
      } catch (Exception e) {
         logConsole(e.getMessage());
         return false;
      }
   }

   public static boolean dragAndDropOffset(By fromElement, int X, int Y) {
      try {
         Actions action = new Actions(DriverManager.getDriver());
         //Tính từ vị trí click chuột đầu tiên (clickAndHold)
         action.clickAndHold(getWebElement(fromElement)).pause(1).moveByOffset(X, Y).release().build().perform();
         return true;
      } catch (Exception e) {
         logConsole(e.getMessage());
         return false;
      }
   }

   public static boolean pressENTER() {
      try {
         Robot robot = new Robot();
         robot.keyPress(KeyEvent.VK_ENTER);
         robot.keyRelease(KeyEvent.VK_ENTER);
         return true;
      } catch (Exception e) {
         return false;
      }
   }

   public static boolean pressESC() {
      try {
         Robot robot = new Robot();
         robot.keyPress(KeyEvent.VK_ESCAPE);
         robot.keyRelease(KeyEvent.VK_ESCAPE);
         return true;
      } catch (Exception e) {
         return false;
      }
   }

   public static boolean pressF11() {
      try {
         Robot robot = new Robot();
         robot.keyPress(KeyEvent.VK_F11);
         robot.keyRelease(KeyEvent.VK_F11);
         return true;
      } catch (Exception e) {
         return false;
      }
   }

   /**
    * @param by truyền vào đối tượng element dạng By
    * @return Tô màu viền đỏ cho Element trên website
    */
   public static WebElement highLightElement(By by) {
      // Tô màu border ngoài chính element chỉ định - màu đỏ (có thể đổi màu khác)
      if (DriverManager.getDriver() instanceof JavascriptExecutor) {
         ((JavascriptExecutor) DriverManager.getDriver()).executeScript("arguments[0].style.border='3px solid red'", getWebElement(by));
         sleep(1);
      }
      return getWebElement(by);
   }

   public static void acceptAlert() {
      waitForAlertIsPresent();
      Alert alert = DriverManager.getDriver().switchTo().alert();
      alert.accept();
   }

   public static void dismissAlert() {
      waitForAlertIsPresent();
      Alert alert = DriverManager.getDriver().switchTo().alert();
      alert.dismiss();
   }

   public static String getTextOnAlert() {
      waitForAlertIsPresent();
      Alert alert = DriverManager.getDriver().switchTo().alert();
      return alert.getText();
   }

   public static void setTextOnAlert(String text) {
      waitForAlertIsPresent();
      Alert alert = DriverManager.getDriver().switchTo().alert();
      alert.sendKeys(text);
   }

   public static boolean verifyEquals(Object actual, Object expected) {
      System.out.println("Verify equals: " + actual + " and " + expected);
      boolean check = actual.equals(expected);
      if (check) {
         logConsole("➡\uFE0F Equals.");
      } else {
         logConsole("⛔\uFE0F NOT Equals.");
      }
      return check;
   }

   public static void assertEquals(Object actual, Object expected, String message) {
      System.out.println("Assert equals: " + actual + " \uD83D\uDFF0 " + expected);
      if (actual.equals(expected)) {
         logConsole("➡\uFE0F Equals.");
      } else {
         logConsole("⛔\uFE0F NOT Equals.");
      }

      if(ConfigData.SCREENSHOT_ALL_STEPS.equalsIgnoreCase("true")){
         CaptureHelper.captureScreenshot("assertEquals");
      }

      Assert.assertEquals(actual, expected, message);
   }

   public static boolean verifyContains(String actual, String expected) {
      System.out.println("Verify contains: " + actual + " and " + expected);
      boolean check = actual.contains(expected);
      return check;
   }

   public static void assertContains(String actual, String expected, String message) {
      System.out.println("Assert contains: " + actual + " and " + expected);
      boolean check = actual.contains(expected);

      if(ConfigData.SCREENSHOT_ALL_STEPS.equalsIgnoreCase("true")){
         CaptureHelper.captureScreenshot("assertContains");
      }

      Assert.assertTrue(check, message);
   }

   //==================== Dropdown chuẩn HTML thẻ <select> ====================

   /**
    * Chọn option theo chữ hiển thị. Chỉ dùng được cho thẻ <select> chuẩn.
    * Dropdown do plugin javascript vẽ ra (selectpicker, select2...) KHÔNG phải thẻ <select>,
    * phải bấm mở rồi bấm chọn như element thường - xem searchSelectPickerOption().
    */
   public static void selectOptionByText(By by, String text) {
      waitForElementVisible(by);
      sleep(STEP_TIME);
      retryUntil(_driver -> {
         new Select(_driver.findElement(by)).selectByVisibleText(text);
         return true;
      });
      logConsole("Select option by text '" + text + "' on element " + by);
   }

   public static void selectOptionByValue(By by, String value) {
      waitForElementVisible(by);
      sleep(STEP_TIME);
      retryUntil(_driver -> {
         new Select(_driver.findElement(by)).selectByValue(value);
         return true;
      });
      logConsole("Select option by value '" + value + "' on element " + by);
   }

   public static void selectOptionByIndex(By by, int index) {
      waitForElementVisible(by);
      sleep(STEP_TIME);
      retryUntil(_driver -> {
         new Select(_driver.findElement(by)).selectByIndex(index);
         return true;
      });
      logConsole("Select option by index " + index + " on element " + by);
   }

   public static String getSelectedOptionText(By by) {
      waitForElementVisible(by);
      String text = retryUntil(_driver -> new Select(_driver.findElement(by)).getFirstSelectedOption().getText());
      logConsole("==> Selected option: " + text);
      return text;
   }

   /**
    * Lấy toàn bộ chữ hiển thị của các option trong dropdown.
    * Dùng để kiểm tra danh sách lựa chọn có đủ và đúng thứ tự hay không.
    */
   public static List<String> getAllOptionsText(By by) {
      waitForElementVisible(by);
      List<String> options = retryUntil(_driver -> {
         List<String> result = new ArrayList<>();
         for (WebElement option : new Select(_driver.findElement(by)).getOptions()) {
            result.add(option.getText().trim());
         }
         return result;
      });
      logConsole("==> Có " + options.size() + " option: " + options);
      return options;
   }

   //==================== Checkbox và Radio button ====================

   public static void checkCheckbox(By by) {
      setCheckboxState(by, true);
   }

   public static void uncheckCheckbox(By by) {
      setCheckboxState(by, false);
   }

   /**
    * Đưa checkbox về đúng trạng thái mong muốn, bấm hay không là do hàm tự quyết.
    * Bấm thẳng vào trạng thái mong muốn thay vì bấm mù giúp gọi bao nhiêu lần kết quả vẫn như nhau,
    * còn bấm mù thì gọi hai lần là trạng thái lật ngược trở lại.
    * Điều kiện trả về trạng thái thực tế nên nếu cú bấm chưa ăn, vòng chờ sẽ tự bấm lại.
    * LƯU Ý: nhiều giao diện ẩn thẻ input thật và chỉ hiện thẻ label được tô vẽ đè lên,
    * trường hợp đó bấm vào input sẽ không ăn - hãy truyền By của label, hoặc dùng clickElementByJS().
    */
   public static void setCheckboxState(By by, boolean expectedChecked) {
      waitForElementPresent(by);
      sleep(STEP_TIME);
      retryUntil(_driver -> {
         WebElement element = _driver.findElement(by);
         if (element.isSelected() != expectedChecked) {
            element.click();
            return false;
         }
         return true;
      });
      logConsole((expectedChecked ? "Check" : "Uncheck") + " checkbox " + by);
   }

   public static void selectRadioButton(By by) {
      waitForElementPresent(by);
      sleep(STEP_TIME);
      retryUntil(_driver -> {
         WebElement element = _driver.findElement(by);
         if (!element.isSelected()) {
            element.click();
            return false;
         }
         return true;
      });
      logConsole("Select radio button " + by);
   }

   //==================== Kiểm tra trạng thái - trả về true/false, KHÔNG làm fail test ====================

   /**
    * Các hàm isXxx bên dưới chỉ trả lời có hay không, không tự làm fail test như nhóm waitForXxx.
    * Dùng khi cần rẽ nhánh: element có hiện thì làm A, không hiện thì làm B.
    * Còn khi element BẮT BUỘC phải có thì dùng waitForXxx để fail sớm ngay tại chỗ sai.
    */
   public static boolean isElementVisible(By by) {
      return isElementVisible(by, EXPLICIT_WAIT_TIMEOUT);
   }

   public static boolean isElementVisible(By by, int timeOut) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeOut), Duration.ofMillis(500));
         wait.until(ExpectedConditions.visibilityOfElementLocated(by));
         return true;
      } catch (TimeoutException | NoSuchElementException error) {
         return false;
      }
   }

   public static boolean isElementClickable(By by) {
      return isElementClickable(by, EXPLICIT_WAIT_TIMEOUT);
   }

   public static boolean isElementClickable(By by, int timeOut) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeOut), Duration.ofMillis(500));
         wait.until(ExpectedConditions.elementToBeClickable(by));
         return true;
      } catch (TimeoutException | NoSuchElementException error) {
         return false;
      }
   }

   public static boolean isElementEnabled(By by) {
      try {
         return getWebElement(by).isEnabled();
      } catch (NoSuchElementException | StaleElementReferenceException error) {
         return false;
      }
   }

   /**
    * Kiểm tra checkbox hoặc radio button đang được chọn hay không.
    */
   public static boolean isElementSelected(By by) {
      try {
         return getWebElement(by).isSelected();
      } catch (NoSuchElementException | StaleElementReferenceException error) {
         return false;
      }
   }

   //==================== Lấy dữ liệu hàng loạt - dùng cho datatable ====================

   /**
    * Lấy chữ của TẤT CẢ element khớp locator, ví dụ toàn bộ tên khách hàng trong một cột.
    * Trả về danh sách rỗng nếu không có element nào khớp, không làm fail test.
    */
   public static List<String> getAllElementsText(By by) {
      List<String> texts = retryUntil(_driver -> {
         List<String> result = new ArrayList<>();
         for (WebElement element : _driver.findElements(by)) {
            result.add(element.getText().trim());
         }
         return result;
      });
      logConsole("==> Text của " + texts.size() + " element " + by + ": " + texts);
      return texts;
   }

   public static int getElementCount(By by) {
      int count = getWebElements(by).size();
      logConsole("==> Số lượng element " + by + ": " + count);
      return count;
   }

   /**
    * Lấy giá trị ĐANG có trong ô input, tức là chữ mà người dùng vừa gõ vào.
    * Khác với getElementAttribute("value"): hàm đó đọc thuộc tính value viết trong HTML gốc,
    * nên với ô input người dùng vừa gõ nó vẫn trả về giá trị ban đầu chứ không phải giá trị mới.
    */
   public static String getElementValue(By by) {
      waitForElementVisible(by);
      String value = retryUntil(_driver -> Optional.ofNullable(_driver.findElement(by).getDomProperty("value")))
              .orElse("");
      logConsole("==> Value: " + value);
      return value;
   }

   /**
    * Đọc đúng thuộc tính viết trong HTML gốc, ví dụ href, placeholder, data-id.
    */
   public static String getElementDomAttribute(By by, String attributeName) {
      waitForElementVisible(by);
      String value = retryUntil(_driver -> Optional.ofNullable(_driver.findElement(by).getDomAttribute(attributeName)))
              .orElse(null);
      logConsole("==> DOM attribute " + attributeName + ": " + value);
      return value;
   }

   //==================== Thao tác bằng Javascript - dùng khi cách thường không ăn ====================

   public static Object executeJS(String script, Object... args) {
      return ((JavascriptExecutor) DriverManager.getDriver()).executeScript(script, args);
   }

   /**
    * Bấm bằng Javascript, dùng khi cách bấm thường bị chặn:
    * có thanh menu dính trên đầu trang che mất nút, có lớp phủ mờ đè lên, hoặc nút nằm ngoài vùng nhìn thấy.
    * LƯU Ý: cách bấm này gọi thẳng vào sự kiện click của trang nên KHÔNG phản ánh đúng thao tác người dùng thật,
    * chỉ dùng khi đã thử clickElement() mà không được.
    */
   public static void clickElementByJS(By by) {
      waitForElementPresent(by);
      sleep(STEP_TIME);
      retryUntil(_driver -> {
         WebElement element = _driver.findElement(by);
         ((JavascriptExecutor) _driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
         ((JavascriptExecutor) _driver).executeScript("arguments[0].click();", element);
         return true;
      });
      logConsole("Click by JS on element " + by);
   }

   /**
    * Gán chữ vào ô input bằng Javascript, dùng cho ô bị ẩn hoặc ô chỉ đọc.
    * Phải bắn kèm sự kiện input và change, vì gán thẳng value thì trang không hề biết giá trị đã đổi,
    * dẫn tới nút Save vẫn xám hoặc phần kiểm tra dữ liệu vẫn báo ô còn trống.
    */
   public static void setTextByJS(By by, String value) {
      waitForElementPresent(by);
      sleep(STEP_TIME);
      retryUntil(_driver -> {
         WebElement element = _driver.findElement(by);
         ((JavascriptExecutor) _driver).executeScript(
                 "arguments[0].value = arguments[1];"
                         + "arguments[0].dispatchEvent(new Event('input', {bubbles: true}));"
                         + "arguments[0].dispatchEvent(new Event('change', {bubbles: true}));",
                 element, value);
         return true;
      });
      logConsole("Set text by JS '" + value + "' on element " + by);
   }

   public static void scrollToTopPage() {
      executeJS("window.scrollTo(0, 0);");
      logConsole("Scroll lên đầu trang.");
   }

   public static void scrollToBottomPage() {
      executeJS("window.scrollTo(0, document.body.scrollHeight);");
      logConsole("Scroll xuống cuối trang.");
   }

   //==================== Frame ====================

   /**
    * Chờ frame sẵn sàng rồi nhảy vào luôn.
    * Mọi element nằm trong frame chỉ tìm thấy sau khi đã nhảy vào, tìm từ ngoài luôn báo không tồn tại.
    * Xong việc nhớ gọi switchToDefaultContent() để quay ra, nếu không các thao tác sau sẽ tìm nhầm chỗ.
    */
   public static void switchToFrame(By by) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(EXPLICIT_WAIT_TIMEOUT), Duration.ofMillis(500));
         wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(by));
         logConsole("Switch to frame " + by);
      } catch (TimeoutException error) {
         logConsole("Timeout waiting for the frame available. " + by);
         Assert.fail("FAILED. Timeout waiting for the frame available. " + by);
      }
   }

   public static void switchToFrame(int index) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(EXPLICIT_WAIT_TIMEOUT), Duration.ofMillis(500));
         wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(index));
         logConsole("Switch to frame index " + index);
      } catch (TimeoutException error) {
         logConsole("Timeout waiting for the frame available. Index: " + index);
         Assert.fail("FAILED. Timeout waiting for the frame available. Index: " + index);
      }
   }

   public static void switchToFrame(String nameOrId) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(EXPLICIT_WAIT_TIMEOUT), Duration.ofMillis(500));
         wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(nameOrId));
         logConsole("Switch to frame " + nameOrId);
      } catch (TimeoutException error) {
         logConsole("Timeout waiting for the frame available. " + nameOrId);
         Assert.fail("FAILED. Timeout waiting for the frame available. " + nameOrId);
      }
   }

   public static void switchToDefaultContent() {
      DriverManager.getDriver().switchTo().defaultContent();
      logConsole("Switch về trang chính (default content).");
   }

   //==================== Cửa sổ và tab ====================

   public static String getCurrentWindowHandle() {
      return DriverManager.getDriver().getWindowHandle();
   }

   public static Set<String> getAllWindowHandles() {
      return DriverManager.getDriver().getWindowHandles();
   }

   public static void switchToWindowByIndex(int index) {
      List<String> handles = new ArrayList<>(DriverManager.getDriver().getWindowHandles());
      if (index < 0 || index >= handles.size()) {
         logConsole("❌ Không có cửa sổ ở vị trí " + index + ". Hiện đang mở " + handles.size() + " cửa sổ.");
         Assert.fail("FAILED. Không có cửa sổ ở vị trí " + index + ". Hiện đang mở " + handles.size() + " cửa sổ.");
      }
      DriverManager.getDriver().switchTo().window(handles.get(index));
      logConsole("Switch to window index " + index + " - Title: " + DriverManager.getDriver().getTitle());
   }

   /**
    * Duyệt lần lượt từng cửa sổ đang mở và dừng ở cửa sổ đầu tiên có title chứa chuỗi cần tìm.
    * Nếu không thấy thì quay về đúng cửa sổ ban đầu rồi mới báo fail,
    * tránh để driver mắc kẹt ở cửa sổ cuối cùng khiến các bước sau sai hết.
    */
   public static void switchToWindowByTitle(String title) {
      String originalHandle = DriverManager.getDriver().getWindowHandle();
      for (String handle : DriverManager.getDriver().getWindowHandles()) {
         DriverManager.getDriver().switchTo().window(handle);
         if (DriverManager.getDriver().getTitle() != null && DriverManager.getDriver().getTitle().contains(title)) {
            logConsole("Switch to window có title chứa: " + title);
            return;
         }
      }
      DriverManager.getDriver().switchTo().window(originalHandle);
      logConsole("❌ Không tìm thấy cửa sổ nào có title chứa: " + title);
      Assert.fail("FAILED. Không tìm thấy cửa sổ nào có title chứa: " + title);
   }

   public static void openNewTab(String url) {
      DriverManager.getDriver().switchTo().newWindow(WindowType.TAB);
      DriverManager.getDriver().get(url);
      waitForPageLoaded();
      logConsole("🌐 Mở tab mới: " + url);
   }

   /**
    * Đóng tab hiện tại rồi nhảy về một tab còn lại.
    * Bắt buộc phải nhảy về, vì sau khi đóng thì driver không còn trỏ vào cửa sổ nào,
    * mọi lệnh gọi tiếp theo sẽ báo lỗi no such window.
    */
   public static void closeCurrentTab() {
      Set<String> handles = DriverManager.getDriver().getWindowHandles();
      if (handles.size() <= 1) {
         logConsole("⚠️ Chỉ còn 1 tab nên không đóng, đóng nốt là mất luôn phiên làm việc của driver.");
         return;
      }
      String closingHandle = DriverManager.getDriver().getWindowHandle();
      DriverManager.getDriver().close();
      for (String handle : DriverManager.getDriver().getWindowHandles()) {
         if (!handle.equals(closingHandle)) {
            DriverManager.getDriver().switchTo().window(handle);
            break;
         }
      }
      logConsole("Đóng tab hiện tại và quay về tab còn lại - Title: " + DriverManager.getDriver().getTitle());
   }

   //==================== Điều khiển trình duyệt ====================

   public static void refreshPage() {
      DriverManager.getDriver().navigate().refresh();
      waitForPageLoaded();
      logConsole("🔄 Refresh page.");
   }

   public static void navigateBack() {
      DriverManager.getDriver().navigate().back();
      waitForPageLoaded();
      logConsole("Quay lại trang trước.");
   }

   public static void navigateForward() {
      DriverManager.getDriver().navigate().forward();
      waitForPageLoaded();
      logConsole("Tiến tới trang sau.");
   }

   public static String getPageTitle() {
      String title = DriverManager.getDriver().getTitle();
      logConsole("Page title: " + title);
      return title;
   }

   public static void setWindowSize(int width, int height) {
      DriverManager.getDriver().manage().window().setSize(new org.openqa.selenium.Dimension(width, height));
      logConsole("Set window size: " + width + "x" + height);
   }

   /**
    * Đặt cửa sổ về đúng 1920x1080 theo quy định debug của dự án.
    */
   public static void setWindowSizeDesktop() {
      setWindowSize(1920, 1080);
   }

   public static void maximizeWindow() {
      DriverManager.getDriver().manage().window().maximize();
      logConsole("Maximize window.");
   }

   //==================== Chụp màn hình ====================

   public static final String SCREENSHOT_FOLDER = "exports/screenshots/";

   /**
    * Chụp màn hình bằng chính trình duyệt đang chạy test.
    * Khác với cách chụp bằng Robot của CaptureUtils: cách này chỉ chụp đúng nội dung trang,
    * chạy được ở chế độ ẩn cửa sổ và chạy được trên máy CI không có màn hình.
    * Không làm fail test nếu chụp hỏng, vì hàm này hay được gọi lúc test ĐANG fail,
    * lỗi chụp mà ném ra ngoài sẽ che mất nguyên nhân fail thật.
    *
    * @return Đường dẫn đầy đủ của file ảnh, hoặc null nếu chụp không thành công
    */
   public static String takeScreenshot(String screenshotName) {
      try {
         File source = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.FILE);
         return copyScreenshotFile(source, screenshotName);
      } catch (WebDriverException error) {
         logConsole("⚠️ Không chụp được màn hình: " + error.getMessage());
         return null;
      }
   }

   /**
    * Chỉ chụp riêng một element, hữu ích khi cần bằng chứng cho đúng dòng bị lỗi trong bảng dữ liệu
    * thay vì chụp cả trang rồi ngồi dò.
    */
   public static String takeElementScreenshot(By by, String screenshotName) {
      try {
         waitForElementVisible(by);
         scrollToElementAtCenter(by);
         File source = getWebElement(by).getScreenshotAs(OutputType.FILE);
         return copyScreenshotFile(source, screenshotName);
      } catch (WebDriverException error) {
         logConsole("⚠️ Không chụp được element " + by + ": " + error.getMessage());
         return null;
      }
   }

   private static String copyScreenshotFile(File source, String screenshotName) {
      try {
         //Bỏ các ký tự Windows không cho đặt tên file, tránh chụp xong không lưu được
         String safeName = screenshotName.replaceAll("[^a-zA-Z0-9-_]", "_");
         String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
         Path target = Paths.get(SCREENSHOT_FOLDER, safeName + "_" + timestamp + ".png");

         Files.createDirectories(target.getParent());
         Files.copy(source.toPath(), target, StandardCopyOption.REPLACE_EXISTING);

         String fullPath = target.toAbsolutePath().toString();
         logConsole("📸 Screenshot: " + fullPath);
         return fullPath;
      } catch (IOException error) {
         logConsole("⚠️ Không lưu được file ảnh: " + error.getMessage());
         return null;
      }
   }

   //==================== Upload file ====================

   /**
    * Đưa file vào ô chọn file bằng cách gõ thẳng đường dẫn vào thẻ input type=file.
    * Cách này KHÔNG mở hộp thoại chọn file của hệ điều hành nên chạy được cả trên CI.
    * Locator phải trỏ đúng vào thẻ input type=file, không phải nút Browse được tô vẽ đè lên.
    */
   public static void uploadFile(By inputFile, String filePath) {
      String absolutePath = getUploadFileAbsolutePath(filePath);
      waitForElementPresent(inputFile);
      retryUntil(_driver -> {
         _driver.findElement(inputFile).sendKeys(absolutePath);
         return true;
      });
      logConsole("Upload file " + absolutePath + " vào element " + inputFile);
   }

   /**
    * Dùng khi thẻ input type=file bị CSS giấu đi và cách upload thường báo element không thao tác được.
    * Hàm gỡ phần CSS đang giấu để gõ được đường dẫn vào, đây là trường hợp rất phổ biến
    * ở các giao diện tự vẽ lại nút Browse cho đẹp rồi ẩn thẻ input gốc.
    */
   public static void uploadFileToHiddenInput(By inputFile, String filePath) {
      String absolutePath = getUploadFileAbsolutePath(filePath);
      waitForElementPresent(inputFile);
      retryUntil(_driver -> {
         WebElement input = _driver.findElement(inputFile);
         ((JavascriptExecutor) _driver).executeScript(
                 "arguments[0].style.display = 'block';"
                         + "arguments[0].style.visibility = 'visible';"
                         + "arguments[0].style.opacity = 1;"
                         + "arguments[0].style.height = 'auto';"
                         + "arguments[0].style.width = 'auto';",
                 input);
         input.sendKeys(absolutePath);
         return true;
      });
      logConsole("Upload file " + absolutePath + " vào element ẩn " + inputFile);
   }

   /**
    * Kiểm tra file có thật rồi đổi sang đường dẫn đầy đủ.
    * Phải kiểm tra trước khi gõ vào ô upload, vì gõ đường dẫn sai thì trình duyệt im lặng bỏ qua,
    * test chạy tiếp và fail ở tận bước assert phía sau với thông báo chẳng liên quan gì tới file.
    */
   private static String getUploadFileAbsolutePath(String filePath) {
      File file = new File(filePath);
      if (!file.exists()) {
         logConsole("❌ File upload không tồn tại: " + file.getAbsolutePath());
         Assert.fail("FAILED. File upload không tồn tại: " + file.getAbsolutePath());
      }
      return file.getAbsolutePath();
   }

   //==================== Nhóm chờ bổ sung ====================

   public static void waitForTextToBePresent(By by, String text) {
      waitForTextToBePresent(by, text, EXPLICIT_WAIT_TIMEOUT);
   }

   public static void waitForTextToBePresent(By by, String text, int timeOut) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(timeOut), Duration.ofMillis(500));
         wait.until(ExpectedConditions.textToBePresentInElementLocated(by, text));
      } catch (Throwable error) {
         logConsole("Timeout waiting for the text '" + text + "' present in element. " + by);
         Assert.fail("Timeout waiting for the text '" + text + "' present in element. " + by);
      }
   }

   /**
    * Chờ số lượng element khớp locator đúng bằng con số mong muốn.
    * Rất hợp với bảng dữ liệu tự vẽ lại: sau khi lọc hay xoá, bảng cần thời gian vẽ lại,
    * đếm sớm quá là đếm nhầm số dòng của lần vẽ trước.
    */
   public static void waitForNumberOfElements(By by, int expectedNumber) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(EXPLICIT_WAIT_TIMEOUT), Duration.ofMillis(500));
         wait.until(ExpectedConditions.numberOfElementsToBe(by, expectedNumber));
      } catch (Throwable error) {
         logConsole("Timeout chờ đủ " + expectedNumber + " element. Thực tế: " + getWebElements(by).size() + ". " + by);
         Assert.fail("Timeout chờ đủ " + expectedNumber + " element. Thực tế: " + getWebElements(by).size() + ". " + by);
      }
   }

   /**
    * Chờ element cũ bị gỡ khỏi trang.
    * Dùng để biết chắc bảng đã vẽ xong lần mới chứ không phải vẫn còn là bảng cũ:
    * lấy WebElement của bảng cũ trước khi bấm lọc, bấm xong thì chờ nó biến mất rồi mới đọc dữ liệu.
    */
   public static void waitForStalenessOf(WebElement element) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(EXPLICIT_WAIT_TIMEOUT), Duration.ofMillis(500));
         wait.until(ExpectedConditions.stalenessOf(element));
      } catch (Throwable error) {
         logConsole("Timeout chờ element cũ bị gỡ khỏi trang.");
         Assert.fail("Timeout chờ element cũ bị gỡ khỏi trang.");
      }
   }

   /**
    * Chờ thuộc tính có giá trị ĐÚNG BẰNG chuỗi mong muốn.
    * Hợp với dropdown do plugin vẽ (selectpicker): sau khi bấm chọn, plugin mới cập nhật
    * thuộc tính title của nút, chờ mốc này mới chắc lựa chọn đã ăn.
    */
   public static void waitForAttributeToBe(By by, String attributeName, String value) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(EXPLICIT_WAIT_TIMEOUT), Duration.ofMillis(500));
         wait.until(ExpectedConditions.attributeToBe(by, attributeName, value));
      } catch (Throwable error) {
         logConsole("Timeout chờ thuộc tính " + attributeName + " bằng '" + value + "'. " + by);
         Assert.fail("Timeout chờ thuộc tính " + attributeName + " bằng '" + value + "'. " + by);
      }
   }

   /**
    * Chờ ô nhập mang đúng giá trị mong muốn.
    * Đọc value theo DOM property nên thấy được cả giá trị vừa gán bằng Javascript.
    * Dùng để chốt lại là thao tác nhập đã ăn thật, tránh trường hợp plugin xoá trắng ô ngay sau đó
    * mà test vẫn chạy tiếp rồi fail ở tận bước sau với thông báo chẳng liên quan.
    */
   public static void waitForValueToBe(By by, String expectedValue) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(EXPLICIT_WAIT_TIMEOUT), Duration.ofMillis(500));
         wait.until(_driver -> expectedValue.equals(_driver.findElement(by).getDomProperty("value")));
      } catch (Throwable error) {
         logConsole("Timeout chờ ô nhập có giá trị '" + expectedValue + "'. " + by);
         Assert.fail("Timeout chờ ô nhập có giá trị '" + expectedValue + "'. " + by);
      }
   }

   public static void waitForAttributeContains(By by, String attributeName, String value) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(EXPLICIT_WAIT_TIMEOUT), Duration.ofMillis(500));
         wait.until(ExpectedConditions.attributeContains(by, attributeName, value));
      } catch (Throwable error) {
         logConsole("Timeout chờ thuộc tính " + attributeName + " chứa '" + value + "'. " + by);
         Assert.fail("Timeout chờ thuộc tính " + attributeName + " chứa '" + value + "'. " + by);
      }
   }

   public static void waitForTitleContains(String title) {
      try {
         WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(EXPLICIT_WAIT_TIMEOUT), Duration.ofMillis(500));
         wait.until(ExpectedConditions.titleContains(title));
      } catch (Throwable error) {
         logConsole("Timeout chờ title chứa '" + title + "'. Title hiện tại: " + DriverManager.getDriver().getTitle());
         Assert.fail("Timeout chờ title chứa '" + title + "'. Title hiện tại: " + DriverManager.getDriver().getTitle());
      }
   }

   //==================== Cookie ====================

   public static void addCookie(Cookie cookie) {
      DriverManager.getDriver().manage().addCookie(cookie);
      logConsole("Thêm cookie: " + cookie.getName());
   }

   public static void addCookie(String name, String value) {
      addCookie(new Cookie(name, value));
   }

   public static Set<Cookie> getAllCookies() {
      return DriverManager.getDriver().manage().getCookies();
   }

   public static String getCookieValue(String name) {
      Cookie cookie = DriverManager.getDriver().manage().getCookieNamed(name);
      String value = cookie == null ? null : cookie.getValue();
      logConsole("==> Cookie " + name + ": " + value);
      return value;
   }

   public static void deleteCookie(String name) {
      DriverManager.getDriver().manage().deleteCookieNamed(name);
      logConsole("Xoá cookie: " + name);
   }

   public static void deleteAllCookies() {
      DriverManager.getDriver().manage().deleteAllCookies();
      logConsole("Xoá toàn bộ cookie.");
   }

}
