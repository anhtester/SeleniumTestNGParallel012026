package com.anhtester.dataproviders;

import com.anhtester.constants.ConfigData;
import com.anhtester.helpers.ExcelHelper;
import com.anhtester.helpers.SystemHelper;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.util.Hashtable;

public class DataProviderFactory {
   @DataProvider(name = "data_provider_01")
   public Object[][] dpMethod1() {
      return new Object[][]{
              {"Value1", "Value2", "Value3"},
              {"Value4", "Value5", "Value6"},
              {"Value7", "Value8", "Value9"}
      };
   }

   @DataProvider(name = "data_provider_02")
   public Object[][] dpMethod2() {
      return new Object[][]{
              {1, 2, "Value3"},
              {3, 4, "Value6"},
              {5, 6, "Value9"}
      };
   }

   @DataProvider(name = "data_provider_03", parallel = true)
   public Object[][] dataHRM() {
      return new Object[][]{{"anhtester", "123456", "Admin"}, {"joe.larson", "joe.larson", "Employee"}};
   }

   @DataProvider(name = "data_provider_04", parallel = true)
   public Object[][] dataCRM() {
      return new Object[][]{
              {"admin@example.com", "123456"},
              {"admin@example.com", "123456"},
              {"admin@example.com", "123456"},
              {"admin@example.com", "123456"},
              {"admin@example.com", "123456"},
              {"admin@example.com", "123456"}
      };
   }

   @DataProvider(name = "data_provider_addcustomer_excel")
   public Object[][] dataAddCustomerFromExcel() {
      ExcelHelper excelHelper = new ExcelHelper();
      Object[][] data = excelHelper.getExcelData(ConfigData.excel_path_crm_data, "AddCustomer");
      System.out.println("Data from Excel: " + data);
      return data;
   }

   @DataProvider(name = "dp_addcustomer_excel_start_end")
   public Object[][] dp_addcustomer_excel_start_end() {
      ExcelHelper excelHelper = new ExcelHelper();
      Object[][] data = excelHelper.getDataHashTable(ConfigData.excel_path_crm_data, "AddCustomer", 3,5);
      System.out.println("Data from Excel: " + data);
      return data;
   }

   @DataProvider(name = "data_provider_excel_specific_rows", parallel = true)
   public Object[][] data_provider_excel_specific_rows() {
      ExcelHelper excelHelper = new ExcelHelper();
      int[] specificRows = new int[] {
              1,
              3,
              4
      }; //Dòng cụ thể cần lấy
      Object[][] data = excelHelper.getDataFromSpecificRows(ConfigData.excel_path_crm_data, "AddCustomer", specificRows);
      System.out.println("getDataFromSpecificRows: " + data);
      return data;
   }

   @DataProvider(name = "data_provider_excel_specific_rows_hashtable")
   public Object[][] data_provider_excel_specific_rows_hashtable() {
      ExcelHelper excelHelper = new ExcelHelper();
      int[] specificRows = new int[] {
              1,
              3,
              4
      }; //Dòng cụ thể cần lấy
      Object[][] data = excelHelper.getDataHashTableFromSpecificRows(ConfigData.excel_path_crm_data, "AddCustomer", specificRows);
      System.out.println("getDataHashTableFromSpecificRows: " + data);
      return data;
   }

   @DataProvider(name = "data_login")
   public Object[][] dataLogin(Method method) {
      String testCaseName = method.getName();          // TestNG tự truyền vào

      ExcelHelper excelHelper = new ExcelHelper();
      excelHelper.setExcelFile(ConfigData.excel_path_crm_data, "Login");

      // Quét tìm dòng có TEST_CASE_NAME khớp tên method
      for (int i = 1; i <= excelHelper.getLastRowNum(); i++) {
         if (testCaseName.equals(excelHelper.getCellData("TEST_CASE_NAME", i))) {
            Hashtable<String, String> data = new Hashtable<>();
            data.put("EMAIL", excelHelper.getCellData("EMAIL", i));
            data.put("PASSWORD", excelHelper.getCellData("PASSWORD", i));
            return new Object[][]{{data}};            // 1 dòng cho TC này
         }
      }
      throw new SkipException("Không tìm thấy data cho test case: " + testCaseName);
   }

}
