package com.anhtester.common;

import com.anhtester.constants.ConfigData;
import com.anhtester.drivers.DriverManager;
import com.anhtester.drivers.ParameterManager;
import com.anhtester.helpers.PropertiesHelper;
import com.anhtester.listeners.TestListener;
import com.anhtester.utils.LogUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Listeners({TestListener.class})
public class BaseTest {

   @BeforeMethod
   @Parameters({"browser"})
   public void createDriver(@Optional("chrome") String browserName) {
      // Ưu tiên: -Dbrowser (Maven) > biến môi trường > config.properties > <parameter> XML
      // Truyền browserName của XML vào làm giá trị cuối cùng để luôn có fallback chắc chắn
      browserName = ParameterManager.getConfigValue("browser", browserName);

      // headless đi theo đúng thứ tự ưu tiên như browser
      boolean headless = Boolean.parseBoolean(ParameterManager.getHeadlessMode());
      LogUtils.info("⚙\uFE0F Browser sử dụng: " + browserName + " | headless: " + headless);

      WebDriver driver;
      switch (browserName.trim().toLowerCase()) {
         case "chrome":
            LogUtils.info("Launching Chrome browser...");
            driver = new ChromeDriver(getChromeOptions(headless));
            break;
         case "firefox":
            LogUtils.info("Launching Firefox browser...");
            driver = new FirefoxDriver(getFirefoxOptions(headless));
            break;
         case "edge":
            LogUtils.info("Launching Edge browser...");
            driver = new EdgeDriver(getEdgeOptions(headless));
            break;
         default:
            LogUtils.info("Browser: " + browserName + " is invalid, Launching Chrome as browser of choice...");
            driver = new ChromeDriver(getChromeOptions(headless));
      }

      DriverManager.setDriver(driver);

      // Ở headless, maximize() không phóng to mà co cửa sổ về 800x600, đè mất --window-size
      // đã set lúc khởi tạo. Viewport bé làm web chuyển sang layout mobile và ẩn sidebar.
      if (!headless) {
         DriverManager.getDriver().manage().window().maximize();
      }
      DriverManager.getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
   }

   private ChromeOptions getChromeOptions(boolean headless) {
      ChromeOptions options = new ChromeOptions();

      Map<String, Object> prefs = new HashMap<String, Object>();
      prefs.put("profile.default_content_setting_values.notifications", 2); //Block notifications from website
      prefs.put("profile.password_manager_leak_detection", false); // Turn off change your password
      prefs.put("credentials_enable_service", false);
      prefs.put("profile.password_manager_enabled", false);
      prefs.put("autofill.profile_enabled", false); //Turn off Save Address popup
      options.setExperimentalOption("prefs", prefs);
      options.addArguments("--disable-extensions");
      options.addArguments("--disable-infobars");
      options.addArguments("--disable-notifications");
      options.addArguments("--remote-allow-origins=*");
      options.setAcceptInsecureCerts(true);

      if (headless) {
         options.addArguments("--headless=new");
         options.addArguments("--window-size=" + getWindowSize());
         options.addArguments("--disable-gpu");
      }
      return options;
   }

   private EdgeOptions getEdgeOptions(boolean headless) {
      EdgeOptions options = new EdgeOptions();

      Map<String, Object> prefs = new HashMap<String, Object>();
      prefs.put("profile.default_content_setting_values.notifications", 2);
      prefs.put("profile.password_manager_leak_detection", false); // Turn off change your password
      prefs.put("credentials_enable_service", false);
      prefs.put("profile.password_manager_enabled", false);
      prefs.put("autofill.profile_enabled", false); //Turn off Save Address popup
      options.setExperimentalOption("prefs", prefs);
      options.addArguments("--disable-extensions");
      options.addArguments("--disable-infobars");
      options.addArguments("--disable-notifications");
      options.addArguments("--remote-allow-origins=*");
      options.setAcceptInsecureCerts(true);

      if (headless) {
         options.addArguments("--headless=new");
         options.addArguments("--window-size=" + getWindowSize());
         options.addArguments("--disable-gpu");
      }
      return options;
   }

   private FirefoxOptions getFirefoxOptions(boolean headless) {
      FirefoxOptions options = new FirefoxOptions();
      if (headless) {
         // Firefox dùng 1 gạch và nhận width/height rời nhau
         options.addArguments("-headless");
         options.addArguments("--width=" + ParameterManager.getConfigValue("window_size_x", "1920"));
         options.addArguments("--height=" + ParameterManager.getConfigValue("window_size_y", "1080"));
      }
      return options;
   }

   /**
    * Ở chế độ headless thì maximize() không có tác dụng thật, phải set kích thước
    * cửa sổ ngay lúc khởi tạo, nếu không mặc định chỉ 800x600 làm element bị che.
    */
   private String getWindowSize() {
      return ParameterManager.getConfigValue("window_size_x", "1920")
              + "," + ParameterManager.getConfigValue("window_size_y", "1080");
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
