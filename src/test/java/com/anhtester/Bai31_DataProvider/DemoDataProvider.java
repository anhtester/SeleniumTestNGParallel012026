package com.anhtester.Bai31_DataProvider;

import com.anhtester.Bai28_DriverManager_Parallel.pages.DashboardPage;
import com.anhtester.Bai28_DriverManager_Parallel.pages.LoginPage;
import com.anhtester.dataproviders.DataProviderFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DemoDataProvider {

   @Test(dataProvider = "data_provider_01", dataProviderClass = DataProviderFactory.class)
   public void testDataProvider1(String username, String password, String result) {
      System.out.println("Username is: " + username);
      System.out.println("Password is: " + password);
      System.out.println("Result is: " + result);
   }

   @Test(dataProvider = "data_provider_02", dataProviderClass = DataProviderFactory.class)
   public void testDataProvider2(int username, int password, String result) {
      System.out.println("Username is: " + username);
      System.out.println("Password is: " + password);
      System.out.println("Result is: " + result);
   }

   @Test(dataProvider = "data_provider_03", dataProviderClass = DataProviderFactory.class)
   public void testDataProviderMultiParam(String username, String password, String role) {
      System.out.println("Username is: " + username);
      System.out.println("Password is: " + password);
      System.out.println("Role is: " + role);
   }

}
