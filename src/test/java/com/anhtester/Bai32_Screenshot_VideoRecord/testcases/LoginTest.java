package com.anhtester.Bai32_Screenshot_VideoRecord.testcases;

import com.anhtester.Bai32_Screenshot_VideoRecord.pages.DashboardPage;
import com.anhtester.Bai32_Screenshot_VideoRecord.pages.LoginPage;
import com.anhtester.common.BaseTest;
import com.anhtester.dataproviders.DataProviderFactory;
import com.anhtester.helpers.CaptureHelper;
import com.anhtester.keywords.WebUI;
import org.testng.annotations.*;
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

   @AfterMethod
   public void afterMethod(){
      WebUI.sleep(2);
      CaptureHelper.stopRecord();
   }

   @Test(priority = 1, dataProvider = "data_login", dataProviderClass = DataProviderFactory.class)
   public void testLoginCRM_Success(Hashtable<String, String> data) {
      CaptureHelper.startRecord("testLoginCRM_Success");
      dashboardPage = loginPage.loginCRM(data.get("EMAIL"), data.get("PASSWORD"));
      loginPage.verifyLoginSuccess();
   }

   @Test(priority = 2, dataProvider = "data_login", dataProviderClass = DataProviderFactory.class)
   public void testLoginFailWithEmailInvalid(Hashtable<String, String> data) {
      CaptureHelper.startRecord("testLoginFailWithEmailInvalid");
      dashboardPage = loginPage.loginCRM(data.get("EMAIL"), data.get("PASSWORD"));
      loginPage.verifyLoginFail("Invalid email or password");
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
