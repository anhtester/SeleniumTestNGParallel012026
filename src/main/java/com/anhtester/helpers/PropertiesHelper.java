package com.anhtester.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

public class PropertiesHelper {

   private static Properties properties;
   private static final String CONFIG_DIR = "src/test/resources/configs/";
   private static String relPropertiesFilePathDefault = CONFIG_DIR + "config.properties";
   //Môi trường đang chạy, lưu lại để in log và cho ParameterManager đọc
   private static String currentEnv;

   /**
    * Load file config chung rồi chồng file môi trường lên trên.
    * File nạp sau đè key trùng của file nạp trước, nên chỉ cần khai báo trong
    * dev.properties / staging.properties những key nào thật sự khác với file chung.
    * <p>
    * Môi trường được chọn theo thứ tự ưu tiên: {@code -Denv} (Maven) > biến môi trường
    * {@code ENV} > key {@code env} trong config.properties. Không khai báo ở đâu cả
    * thì chỉ load mỗi config.properties.
    */
   public static Properties loadAllFiles() {
      return loadAllFiles(resolveEnv());
   }

   /**
    * Load rồi gán vào bộ config dùng chung cho cả framework.
    * Là trạng thái static nên chỉ nên gọi một lần ở @BeforeSuite. Gọi giữa lúc test
    * đang chạy song song thì mọi luồng khác bị đổi config theo, không chỉ luồng gọi.
    * Cần đọc một môi trường khác mà không ảnh hưởng ai thì dùng {@link #loadFiles(String)}.
    */
   public static synchronized Properties loadAllFiles(String env) {
      String targetEnv = env == null ? "" : env.trim();
      Properties loaded = loadFiles(targetEnv);

      //Gán một lần sau khi đã load xong, để luồng khác không đọc phải bộ config đang dở dang
      currentEnv = targetEnv;
      properties = loaded;
      return properties;
   }

   /**
    * Load file chung rồi chồng file môi trường lên, trả về bộ Properties độc lập.
    * Không đụng tới biến static nào nên gọi song song từ nhiều luồng vẫn an toàn.
    */
   public static Properties loadFiles(String env) {
      String targetEnv = env == null ? "" : env.trim();

      LinkedList<String> files = new LinkedList<>();
      // 1. File chung, luôn load trước
      files.add(relPropertiesFilePathDefault);
      // 2. File riêng của môi trường, load sau để đè lên file chung
      if (!targetEnv.isEmpty()) {
         files.add(CONFIG_DIR + targetEnv + ".properties");
      }

      Properties loaded = new Properties();
      for (String f : files) {
         // Dùng biến cục bộ, KHÔNG dùng field static: chạy parallel thì luồng này
         // đóng mất stream của luồng kia, gây IOException "Stream Closed"
         String path = SystemHelper.getCurrentDir() + f;
         if (!new File(path).exists()) {
            // Báo lỗi ngay thay vì trả về Properties rỗng, vì gõ sai tên môi trường
            // mà vẫn chạy tiếp thì toàn bộ config biến mất và test fail ở chỗ khác rất khó lần
            throw new IllegalArgumentException("Không tìm thấy file config: " + path);
         }
         try (FileInputStream in = new FileInputStream(path)) {
            Properties tempProp = new Properties();
            tempProp.load(in);
            loaded.putAll(tempProp);
         } catch (IOException ioe) {
            throw new IllegalStateException("Đọc file config thất bại: " + path, ioe);
         }
      }

      System.out.println("Config đã load: " + files + (targetEnv.isEmpty() ? " (không có môi trường riêng)" : " | env = " + targetEnv));
      return loaded;
   }

   /**
    * Tìm tên môi trường trước khi load, nên phải đọc thẳng key env trong file chung
    * thay vì gọi getValue() (lúc này properties còn chưa được nạp).
    */
   private static String resolveEnv() {
      String env = System.getProperty("env");
      if (isEmpty(env)) {
         env = System.getenv("env");
      }
      if (isEmpty(env)) {
         env = System.getenv("ENV");
      }
      if (isEmpty(env)) {
         env = readKeyDirectly(relPropertiesFilePathDefault, "env");
      }
      return isEmpty(env) ? "" : env.trim();
   }

   private static String readKeyDirectly(String relFilePath, String key) {
      String path = SystemHelper.getCurrentDir() + relFilePath;
      if (!new File(path).exists()) {
         return null;
      }
      try (FileInputStream in = new FileInputStream(path)) {
         Properties temp = new Properties();
         temp.load(in);
         return temp.getProperty(key);
      } catch (IOException ioe) {
         return null;
      }
   }

   public static String getCurrentEnv() {
      return currentEnv == null ? "" : currentEnv;
   }

   private static boolean isEmpty(String value) {
      return value == null || value.trim().isEmpty();
   }

   public static synchronized void setFile(String relPropertiesFilePath) {
      Properties loaded = new Properties();
      String path = SystemHelper.getCurrentDir() + relPropertiesFilePath;
      //Stream để cục bộ, chạy parallel mới không đóng nhầm stream của luồng khác
      try (FileInputStream in = new FileInputStream(path)) {
         loaded.load(in);
         properties = loaded;
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static void setFile() {
      setFile(relPropertiesFilePathDefault);
   }

   public static String getValue(String key) {
      String value = null;
      try {
         if (properties == null) {
            // Chưa load thì tự load, phải đi qua loadAllFiles() để không bỏ sót file môi trường
            loadAllFiles();
         }
         // Lấy giá trị từ file đã Set
         value = properties.getProperty(key);
      } catch (Exception e) {
         System.out.println(e.getMessage());
      }
      return value;
   }

   /**
    * Lấy giá trị theo key trong file Properties.
    * Nếu key không tồn tại hoặc để trống giá trị thì trả về defaultValue.
    */
   public static String getValue(String key, String defaultValue) {
      String value = getValue(key);
      if (value == null || value.trim().isEmpty()) {
         return defaultValue;
      }
      return value.trim();
   }

   public static void setValue(String key, String keyValue) {
      setValue(relPropertiesFilePathDefault, key, keyValue);
   }

   /**
    * Ghi một key xuống đúng file được chỉ định.
    * Phải mở lại riêng file đích chứ không dùng biến properties đang giữ trong bộ nhớ,
    * vì đó là bộ đã gộp nhiều file - ghi thẳng nó xuống sẽ đổ hết key của file chung
    * sang file môi trường.
    */
   public static void setValue(String relPropertiesFilePath, String key, String keyValue) {
      String targetFile = SystemHelper.getCurrentDir() + relPropertiesFilePath;
      try {
         Properties fileProps = new Properties();
         try (FileInputStream in = new FileInputStream(targetFile)) {
            fileProps.load(in);
         }
         fileProps.setProperty(key, keyValue);
         try (FileOutputStream out = new FileOutputStream(targetFile)) {
            fileProps.store(out, null);
         }

         //Cập nhật luôn vào bộ nhớ để lần getValue kế tiếp thấy giá trị mới
         if (properties != null) {
            properties.setProperty(key, keyValue);
         }
      } catch (Exception e) {
         System.out.println(e.getMessage());
      }
   }

}