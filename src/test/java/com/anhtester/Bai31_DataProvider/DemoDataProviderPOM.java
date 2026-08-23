package com.anhtester.Bai31_DataProvider;

import com.anhtester.Bai28_DriverManager_Parallel.pages.DashboardPage;
import com.anhtester.Bai28_DriverManager_Parallel.pages.LoginPage;
import com.anhtester.common.BaseTest;
import com.anhtester.dataproviders.DataProviderFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DemoDataProviderPOM extends BaseTest {
   private LoginPage loginPage;
   private DashboardPage dashboardPage;
   @BeforeMethod
   public void beforeMethod(){
      loginPage = new LoginPage();
   }

   @Test(priority = 1, dataProvider = "data_provider_04",  dataProviderClass = DataProviderFactory.class)
   public void testLoginCRM_Success(String email, String password) {
      dashboardPage = loginPage.loginCRM("admin@example.com", "123456");
      loginPage.verifyLoginSuccess();
   }
}
