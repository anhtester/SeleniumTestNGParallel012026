package com.anhtester.Bai30_Excel_Data;

import com.anhtester.helpers.ExcelHelper;
import org.testng.annotations.Test;

public class DemoExcelData {
   @Test(priority = 1)
   public void testReadExcelData() {
      ExcelHelper excelHelper = new ExcelHelper();
      excelHelper.setExcelFile("src/test/resources/testdata/crm_data.xlsx", "Login");

      System.out.println(excelHelper.getCellData("EMAIL", 1));
      System.out.println(excelHelper.getCellData("PASSWORD", 1));

      System.out.println(excelHelper.getCellData("EMAIL", 2));
      System.out.println(excelHelper.getCellData("PASSWORD", 2));

      System.out.println(excelHelper.getCellData("EMAIL", 3));
      System.out.println(excelHelper.getCellData("PASSWORD", 3));
   }

   @Test(priority = 2)
   public void testWriteExcelData() {
      ExcelHelper excelHelper = new ExcelHelper();
      excelHelper.setExcelFile("src/test/resources/testdata/crm_data.xlsx", "Login");

      excelHelper.setCellData("Passed", "STATUS", 1);
      excelHelper.setCellData("Failed", "STATUS", 2);
      excelHelper.setCellData("SELENIUM JAVA", "STATUS", 3);

      System.out.println(excelHelper.getCellData("STATUS", 1));
      System.out.println(excelHelper.getCellData("STATUS", 2));
      System.out.println(excelHelper.getCellData("STATUS", 3));
   }
}
