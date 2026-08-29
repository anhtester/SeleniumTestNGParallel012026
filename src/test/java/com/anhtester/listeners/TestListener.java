package com.anhtester.listeners;

import com.anhtester.constants.ConfigData;
import com.anhtester.helpers.CaptureHelper;
import com.anhtester.keywords.WebUI;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

   @Override
   public void onStart(ITestContext result) {
      System.out.println("Setup môi trường onStart: " + result.getStartDate());

   }

   @Override
   public void onFinish(ITestContext result) {
      System.out.println("Kết thúc bộ test: " + result.getEndDate());

   }

   @Override
   public void onTestStart(ITestResult result) {
      System.out.println("Bắt đầu chạy test case: " + result.getName());
//      System.out.println("Bắt đầu chạy test case lúc: " + result.getStartMillis());

      if(ConfigData.VIDEO_RECORD_ACTIVE.equalsIgnoreCase("true")) {
         CaptureHelper.startRecord(result.getName());
      }
   }

   @Override
   public void onTestSuccess(ITestResult result) {
      System.out.println("Test case " + result.getName() + " is passed.");

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
      System.out.println("Test case " + result.getName() + " is failed.");
      System.out.println("==> Nguồn gốc Fail: " + result.getThrowable());

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
      System.out.println("Test case " + result.getName() + " is skipped.");

      if(ConfigData.VIDEO_RECORD_ACTIVE.equalsIgnoreCase("true")) {
         WebUI.sleep(2);
         CaptureHelper.stopRecord();
      }
   }
}