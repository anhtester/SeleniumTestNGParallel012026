package com.anhtester.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class LocalStorageUtils {
   public static void setValueToLocalStorage(WebDriver driver, String key, String value) {
      String script = String.format("window.localStorage.setItem('%s', '%s');", key, value);
      ((JavascriptExecutor) driver).executeScript(script);
   }

   public static String getValueFromLocalStorage(WebDriver driver, String key) {
      String script = String.format("return window.localStorage.getItem('%s');", key);
      return (String) ((JavascriptExecutor) driver).executeScript(script);
   }
}
