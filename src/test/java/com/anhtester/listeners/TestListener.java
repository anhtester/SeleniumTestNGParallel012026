package com.anhtester.listeners;

import com.anhtester.constants.ConfigData;
import com.anhtester.helpers.CaptureHelper;
import com.anhtester.helpers.PropertiesHelper;
import com.anhtester.keywords.WebUI;
import com.anhtester.utils.LogUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

   @Override
   public void onStart(ITestContext result) {
      LogUtils.info("⏰ Bắt đầu chạy test lúc: " + result.getStartDate());
      PropertiesHelper.loadAllFiles();
   }

   @Override
   public void onFinish(ITestContext result) {
      LogUtils.info("⏰ Kết thúc bộ test lúc: " + result.getEndDate());

   }

   @Override
   public void onTestStart(ITestResult result) {
      LogUtils.info("\uD83D\uDE80 Bắt đầu chạy test case: " + result.getName());

      if(ConfigData.VIDEO_RECORD_ACTIVE.equalsIgnoreCase("true")) {
         CaptureHelper.startRecord(result.getName());
      }
   }

   @Override
   public void onTestSuccess(ITestResult result) {
      LogUtils.info("✅ Test case " + result.getName() + " is passed.");

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

      if(ConfigData.VIDEO_RECORD_ACTIVE.equalsIgnoreCase("true")) {
         WebUI.sleep(2);
         CaptureHelper.stopRecord();
      }
   }
}