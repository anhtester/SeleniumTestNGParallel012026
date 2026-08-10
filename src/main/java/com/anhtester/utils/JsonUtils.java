package com.anhtester.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Đọc/ghi dữ liệu trung gian ra file JSON để chia sẻ giữa các test case.
 * Ví dụ: CustomersTest tạo Customer -> lưu Customer Name -> ProjectsTest lấy ra dùng lại.
 */
public class JsonUtils {

   public static final String DATA_FOLDER = "src/test/resources/testdata/";

   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
   private static final Type DATA_TYPE = new TypeToken<LinkedHashMap<String, String>>() {
   }.getType();

   public static synchronized void setDataToJsonFile(String fileName, String key, String value) {
      Map<String, String> data = getDataFromJsonFile(fileName);
      data.put(key, value);

      Path filePath = getFilePath(fileName);
      try {
         Files.createDirectories(filePath.getParent());
         try (Writer writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            GSON.toJson(data, writer);
         }
      } catch (IOException exception) {
         throw new RuntimeException("Không ghi được dữ liệu vào file JSON: " + filePath, exception);
      }
   }

   public static synchronized String getValueFromJsonFile(String fileName, String key) {
      String value = getDataFromJsonFile(fileName).get(key);
      if (value == null) {
         throw new RuntimeException("Không tìm thấy key '" + key + "' trong file JSON: " + getFilePath(fileName));
      }
      return value;
   }

   public static synchronized Map<String, String> getDataFromJsonFile(String fileName) {
      Path filePath = getFilePath(fileName);
      if (!Files.exists(filePath)) {
         return new LinkedHashMap<>();
      }
      try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
         Map<String, String> data = GSON.fromJson(reader, DATA_TYPE);
         return data == null ? new LinkedHashMap<>() : data;
      } catch (IOException exception) {
         throw new RuntimeException("Không đọc được file JSON: " + filePath, exception);
      }
   }

   private static Path getFilePath(String fileName) {
      return Paths.get(DATA_FOLDER, fileName).toAbsolutePath();
   }

}
