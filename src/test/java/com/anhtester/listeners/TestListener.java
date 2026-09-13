package com.anhtester.listeners;

import com.anhtester.constants.ConfigData;
import com.anhtester.helpers.CaptureHelper;
import com.anhtester.helpers.PropertiesHelper;
import com.anhtester.keywords.WebUI;
import com.anhtester.reports.AllureManager;
import com.anhtester.reports.ExtentReportManager;
import com.anhtester.reports.ExtentTestManager;
import com.anhtester.utils.LogUtils;
import com.aventstack.extentreports.Status;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

   public String getTestName(ITestResult result) {
      return result.getTestName() != null ? result.getTestName() : result.getMethod().getConstructorOrMethod().getName();
   }

   public String getTestDescription(ITestResult result) {
      return result.getMethod().getDescription() != null ? result.getMethod().getDescription() : getTestName(result);
   }

   @Override
   public void onStart(ITestContext result) {
      LogUtils.info("⏰ Bắt đầu chạy test lúc: " + result.getStartDate());
      PropertiesHelper.loadAllFiles();
   }

   @Override
   public void onFinish(ITestContext result) {
      LogUtils.info("⏰ Kết thúc bộ test lúc: " + result.getEndDate());

      //Kết thúc và thực thi Extents Report
      ExtentReportManager.getExtentReports().flush();
   }

   @Override
   public void onTestStart(ITestResult result) {
      LogUtils.info("\uD83D\uDE80 Bắt đầu chạy test case: " + result.getName());

      if(ConfigData.VIDEO_RECORD_ACTIVE.equalsIgnoreCase("true")) {
         CaptureHelper.startRecord(result.getName());
      }

      //Bắt đầu ghi 1 TCs mới vào Extent Report
      ExtentTestManager.saveToReport(getTestName(result), getTestDescription(result));
   }

   @Override
   public void onTestSuccess(ITestResult result) {
      LogUtils.info("✅ Test case " + result.getName() + " is passed.");

      //Extent Report
      ExtentTestManager.logMessage(Status.PASS, "✅ Test case " + result.getName() + " is passed.");

      if(ConfigData.SCREENSHOT_PASSED_STEP.equalsIgnoreCase("true")){
         CaptureHelper.captureScreenshot(result.getName());
      }

      if(ConfigData.VIDEO_RECORD_ACTIVE.equalsIgnoreCase("true")) {
         WebUI.sleep(2);
         CaptureHelper.stopRecord();
      }
   }

   @Override
   public void onTestFailure(ITestResult result) {
      LogUtils.error("❌ Test case " + result.getName() + " is failed.");
      LogUtils.error("==> Nguồn gốc Fail: " + result.getThrowable());

      //Extent Report
      ExtentTestManager.addScreenshot(result.getName());
      ExtentTestManager.logMessage(Status.FAIL, result.getThrowable().toString());
      ExtentTestManager.logMessage(Status.FAIL, "❌ Test case " + result.getName() + " is failed.");

      //Allure Report
      //AllureManager.saveTextLog(result.getName() + " is failed.");
      AllureManager.saveScreenshotPNG();

      //Screenshot + Ghi Logs
      if(ConfigData.SCREENSHOT_FAILED_STEP.equalsIgnoreCase("true")){
         CaptureHelper.captureScreenshot(result.getName());
      }

      if(ConfigData.VIDEO_RECORD_ACTIVE.equalsIgnoreCase("true")) {
         WebUI.sleep(2);
         CaptureHelper.stopRecord();
      }
   }

   @Override
   public void onTestSkipped(ITestResult result) {
      LogUtils.warn("\uD83D\uDFE1 Test case " + result.getName() + " is skipped.");

      //Extent Report
      ExtentTestManager.logMessage(Status.SKIP, "\uD83D\uDFE1 Test case " + result.getName() + " is skipped.");
      ExtentTestManager.logMessage(Status.SKIP, result.getThrowable().toString());

      if(ConfigData.VIDEO_RECORD_ACTIVE.equalsIgnoreCase("true")) {
         WebUI.sleep(2);
         CaptureHelper.stopRecord();
      }
   }
}