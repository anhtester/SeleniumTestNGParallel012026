package com.anhtester.Bai32_Screenshot_VideoRecord;

import com.anhtester.common.BaseTest;
import com.anhtester.drivers.DriverManager;
import com.anhtester.helpers.CaptureHelper;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.io.FileHandler;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

public class DemoScreenshot extends BaseTest {
   @Test
   public void testHomePage1() {
      DriverManager.getDriver().get("https://anhtester.com");
      Assert.assertEquals(DriverManager.getDriver().getTitle(), "Anh Tester Automation Testing");

      TakesScreenshot ts = (TakesScreenshot) DriverManager.getDriver();
      File source = ts.getScreenshotAs(OutputType.FILE);

      File theDir = new File("./screenshots/");
      if (!theDir.exists()) {
         theDir.mkdirs();
      }

      try {
         FileHandler.copy(source, new File("./screenshots/testHomePage1.png"));
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
      System.out.println("Screenshot success !!");
   }

   @Test
   public void testHomePage2(Method method) {
      DriverManager.getDriver().get("https://anhtester.com");
      Assert.assertEquals(DriverManager.getDriver().getTitle(), "Anh Tester Automation Testing");

      // Chụp màn hình step này lại
      // Tạo tham chiếu của TakesScreenshot
      TakesScreenshot ts = (TakesScreenshot) DriverManager.getDriver();
      // Gọi hàm capture screenshot - getScreenshotAs
      File source = ts.getScreenshotAs(OutputType.FILE);
      // Kiểm tra folder tồn tại. Nếu không thì tạo mới folder
      File theDir = new File("./screenshots/");
      if (!theDir.exists()) {
         theDir.mkdirs();
      }
      // result.getName() lấy tên của test case xong gán cho tên File chụp màn hình
      try {
         FileHandler.copy(source, new File("./screenshots/" + method.getName() + ".png"));
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
      System.out.println("Screenshot success !!");
   }

   @Test
   public void testCaptureScreenshot(Method method) {
      DriverManager.getDriver().get("https://anhtester.com");
      Assert.assertEquals(DriverManager.getDriver().getTitle(), "Anh Tester Automation Testing");

      // Gọi hàm chụp màn hình dùng sẵn TakesScreenshot của Selenium trong CaptureHelper
      // Ảnh lưu vào thư mục ConfigData.SCREENSHOT_PATH, tên file tự gắn thêm ngày giờ
      CaptureHelper.captureScreenshot(method.getName());
   }

   @AfterMethod
   public void takeScreenshot(ITestResult result) {
      // Khởi tạo đối tượng result thuộc ITestResult để lấy trạng thái và tên của từng Step
      // Ở đây sẽ so sánh điều kiện nếu testcase passed hoặc failed
      // passed = SUCCESS và failed = FAILURE
      if (ITestResult.SUCCESS == result.getStatus()) {
         CaptureHelper.captureScreenshot(result.getName());
      }
   }

}