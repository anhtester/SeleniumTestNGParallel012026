package com.anhtester.constants;

public class ConfigData {
   public static String BASE_URL = "https://crm.anhtester.com";
   public static String LOGIN_URL = "https://crm.anhtester.com/admin/authentication";
   public static String EMAIL_ADMIN = "admin@example.com";
   public static String PASSWORD_ADMIN = "123456";

   //File JSON trung gian để chia sẻ dữ liệu giữa các test case (Customer -> Project)
   public static String CUSTOMER_DATA_FILE = "customer_data.json";
   public static String KEY_CUSTOMER_NAME = "customerName";
   public static String PROJECT_DATA_FILE = "project_data.json";
   public static String KEY_PROJECT_NAME = "projectName";
}
