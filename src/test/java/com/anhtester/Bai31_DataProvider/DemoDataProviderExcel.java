package com.anhtester.Bai31_DataProvider;

import com.anhtester.dataproviders.DataProviderFactory;
import com.anhtester.helpers.ExcelHelper;
import org.testng.annotations.Test;

import java.util.Hashtable;

public class DemoDataProviderExcel {

   @Test(dataProvider = "data_provider_addcustomer_excel", dataProviderClass = DataProviderFactory.class)
   public void testDataProviderAddCustomerExcel(String company, String vat, String address, String phone) {
      System.out.println(company);
      System.out.println(Double.parseDouble(vat) + 20);
      System.out.println(address);
      System.out.println(phone);
   }

   @Test(dataProvider = "dp_addcustomer_excel_start_end", dataProviderClass = DataProviderFactory.class)
   public void testDataProviderAddCustomerExcelStartEnd(Hashtable<String, String> data) {
      System.out.println(data.get("COMPANY"));
      System.out.println(Double.parseDouble(data.get("VAT")));
      System.out.println(data.get("ADDRESS"));
      System.out.println(data.get("PHONE"));
   }

   // Sử dụng DataProvider với các dòng cụ thể (1, 3, 4)
   @Test(dataProvider = "data_provider_excel_specific_rows", dataProviderClass = DataProviderFactory.class)
   public void testLoginWithSpecificRows(String company, String vat, String address, String phone) {
      System.out.println(company);
      System.out.println(Double.parseDouble(vat));
      System.out.println(address);
      System.out.println(phone);
   }

   // Sử dụng DataProvider với các dòng cụ thể dạng Hashtable
   @Test(dataProvider = "data_provider_excel_specific_rows_hashtable", dataProviderClass = DataProviderFactory.class)
   public void testLoginWithSpecificRowsHashtable(Hashtable<String, String> data) {
      System.out.println(data.get("COMPANY"));
      System.out.println(Double.parseDouble(data.get("VAT")));
      System.out.println(data.get("ADDRESS"));
      System.out.println(data.get("PHONE"));
   }

}
