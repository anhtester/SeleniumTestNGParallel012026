package com.anhtester.drivers;

import org.testng.Reporter;

public class ParameterManager {

   private ParameterManager() {
   }

   /**
    * Đọc <parameter> của TestNG theo đúng luồng đang chạy.
    * Reporter giữ kết quả test hiện tại theo ThreadLocal nên mỗi luồng đọc đúng
    * tham số của <test> mình thuộc về, không lẫn sang luồng khác.
    */
   public static String getParameter(String name, String defaultValue) {
      if (Reporter.getCurrentTestResult() == null) {
         return defaultValue;
      }
      String value = Reporter.getCurrentTestResult()
              .getTestContext()
              .getCurrentXmlTest()
              .getParameter(name);
      return value == null ? defaultValue : value;
   }

   public static String getBrowser() {
      return getParameter("browser", "chrome");
   }
}