package com.anhtester.drivers;

import com.anhtester.helpers.PropertiesHelper;
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
      return isEmpty(value) ? defaultValue : value;
   }

   /**
    * Nguồn cấu hình duy nhất cho toàn framework, theo thứ tự ưu tiên:
    * <ol>
    *   <li>System property từ Maven: {@code mvn test -Dbrowser=firefox}</li>
    *   <li>Biến môi trường: {@code BROWSER=firefox} (dùng cho CI/CD)</li>
    *   <li>File properties: {@code config.properties}</li>
    *   <li>{@code <parameter>} trong file XML của TestNG</li>
    *   <li>defaultValue</li>
    * </ol>
    */
   public static String getConfigValue(String name, String defaultValue) {
      // 1. Maven / JVM: -Dbrowser=firefox
      String value = System.getProperty(name);

      // 2. Biến môi trường: browser hoặc BROWSER
      if (isEmpty(value)) {
         value = System.getenv(name);
      }
      if (isEmpty(value)) {
         value = System.getenv(name.toUpperCase());
      }

      // 3. File properties
      if (isEmpty(value)) {
         value = PropertiesHelper.getValue(name);
      }

      // 4. <parameter> trong XML của luồng đang chạy
      if (isEmpty(value)) {
         value = getParameter(name, null);
      }

      return isEmpty(value) ? defaultValue : value.trim();
   }

   public static String getBrowser() {
      return getConfigValue("browser", "chrome");
   }

   public static String getHeadlessMode() {
      return getConfigValue("headless", "false");
   }

   /**
    * Môi trường đang chạy, chính là file properties đã được chồng lên file chung.
    * Lấy từ PropertiesHelper chứ không qua getConfigValue, vì đây là thứ quyết định
    * file nào được load nên nó phải được chốt xong từ trước lúc load.
    */
   public static String getEnv() {
      return PropertiesHelper.getCurrentEnv();
   }

   public static String getUrl() {
      return getConfigValue("url", "");
   }

   private static boolean isEmpty(String value) {
      return value == null || value.trim().isEmpty();
   }
}
