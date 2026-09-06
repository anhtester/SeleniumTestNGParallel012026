package com.anhtester.Bai35_ExtentReport.testcases;

import com.anhtester.Bai35_ExtentReport.pages.DashboardPage;
import com.anhtester.Bai35_ExtentReport.pages.LoginPage;
import com.anhtester.common.BaseTest;
import com.anhtester.dataproviders.DataProviderFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Hashtable;

public class LoginTest extends BaseTest {

   private LoginPage loginPage;
   private DashboardPage dashboardPage;
   private SoftAssert softAssert;

   @BeforeClass
   public void beforeClass(){
      softAssert  = new SoftAssert();
   }

   @AfterClass
   public void afterClass(){
      softAssert.assertAll();
   }

   @BeforeMethod
   public void beforeMethod(){
      loginPage = new LoginPage();
   }

   @Test(priority = 1, dataProvider = "data_login", dataProviderClass = DataProviderFactory.class)
   public void testLoginCRM_Success(Hashtable<String, String> data) {
      dashboardPage = loginPage.loginCRM(data.get("EMAIL"), data.get("PASSWORD"));
      loginPage.verifyLoginSuccess();
   }

   @Test(priority = 2, dataProvider = "data_login", dataProviderClass = DataProviderFactory.class)
   public void testLoginFailWithEmailInvalid(Hashtable<String, String> data) {
      dashboardPage = loginPage.loginCRM(data.get("EMAIL"), data.get("PASSWORD"));
      loginPage.verifyLoginFail("Invalid email or password 123");
   }

   @Test(priority = 3, dataProvider = "data_login", dataProviderClass = DataProviderFactory.class)
   public void testLoginFailWithPasswordInvalid(Hashtable<String, String> data) {
      dashboardPage = loginPage.loginCRM(data.get("EMAIL"), data.get("PASSWORD"));
      loginPage.verifyLoginFail("Invalid email or password");
   }

   @Test(priority = 4, dataProvider = "data_login", dataProviderClass = DataProviderFactory.class)
   public void testLoginFailWithEmailNull(Hashtable<String, String> data) {
      dashboardPage = loginPage.loginCRM(data.get("EMAIL"), data.get("PASSWORD"));
      loginPage.verifyLoginFail("The Email Address field is required.");
   }

   @Test(priority = 5, dataProvider = "data_login", dataProviderClass = DataProviderFactory.class)
   public void testLoginFailWithPasswordNull(Hashtable<String, String> data) {
      dashboardPage = loginPage.loginCRM(data.get("EMAIL"), data.get("PASSWORD"));
      loginPage.verifyLoginFail("The Password field is required.");
   }

   @Test(priority = 6, dataProvider = "data_login", dataProviderClass = DataProviderFactory.class)
   public void testLoginFailWithEmailAndPasswordNull(Hashtable<String, String> data) {
      dashboardPage = loginPage.loginCRM(data.get("EMAIL"), data.get("PASSWORD"));
      loginPage.verifyLoginFailWithEmailAndPasswordNull();
   }

   @Test(priority = 7, dataProvider = "data_login", dataProviderClass = DataProviderFactory.class)
   public void testLoginFailWithEmailFormatInvalid_01(Hashtable<String, String> data) {
      dashboardPage = loginPage.loginCRM(data.get("EMAIL"), data.get("PASSWORD"));
      loginPage.verifyAlertEmailFormatInvalid();
   }

   @Test(priority = 8, dataProvider = "data_login", dataProviderClass = DataProviderFactory.class)
   public void testLoginFailWithEmailFormatInvalid_02(Hashtable<String, String> data) {
      dashboardPage = loginPage.loginCRM(data.get("EMAIL"), data.get("PASSWORD"));
      loginPage.verifyLoginFail("The Password field is required.");
   }

}
