package com.anhtester.Bai29_PropertiesConfig;

import com.anhtester.drivers.ParameterManager;
import com.anhtester.helpers.PropertiesHelper;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class DemoPropertiesConfig {

   @BeforeSuite
   public void beforeSuite() {
      PropertiesHelper.loadAllFiles();
   }

   @Test
   public void testGetValueProperties() {
      //Key chỉ có trong file chung config.properties
      System.out.println("browser: " + PropertiesHelper.getValue("browser"));
      System.out.println("headless: " + PropertiesHelper.getValue("headless"));
      System.out.println("screenshot_fail_steps: " + PropertiesHelper.getValue("screenshot_fail_steps"));

      //Key đến từ file môi trường (dev.properties / staging.properties)
      System.out.println("env đang chạy: " + ParameterManager.getEnv());
      System.out.println("url: " + PropertiesHelper.getValue("url"));
      System.out.println("base.uri: " + PropertiesHelper.getValue("base.uri"));
   }

   /**
    * Ép chạy một môi trường cụ thể, không phụ thuộc key env trong file chung.
    */
   @Test
   public void testLoadStagingEnv() {
      System.out.println("env đang chạy: " + ParameterManager.getEnv());
      System.out.println("url: " + PropertiesHelper.getValue("url"));
      System.out.println("base.uri: " + PropertiesHelper.getValue("base.uri"));
      System.out.println("browser lấy từ file chung: " + PropertiesHelper.getValue("browser"));
   }
}
