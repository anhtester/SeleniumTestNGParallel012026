package com.anhtester.constants;

import com.anhtester.helpers.PropertiesHelper;

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

   //File Excel dành cho data test cases
   public static String EXCEL_PATH_CRM_DATA = PropertiesHelper.getValue("EXCEL_PATH_CRM_DATA");
   public static String EXCEL_PATH_CRM_DATA_CUSTOMER = PropertiesHelper.getValue("EXCEL_PATH_CRM_DATA_CUSTOMER");

   public static String SCREENSHOT_PATH = PropertiesHelper.getValue("SCREENSHOT_PATH");
   public static String VIDEO_RECORD_PATH = PropertiesHelper.getValue("VIDEO_RECORD_PATH");

   public static String SCREENSHOT_PASSED_STEP = PropertiesHelper.getValue("SCREENSHOT_PASSED_STEP");
   public static String SCREENSHOT_FAILED_STEP = PropertiesHelper.getValue("SCREENSHOT_FAILED_STEP");
   public static String SCREENSHOT_ALL_STEPS = PropertiesHelper.getValue("SCREENSHOT_ALL_STEPS");
   public static String VIDEO_RECORD_ACTIVE = PropertiesHelper.getValue("VIDEO_RECORD_ACTIVE");
}
