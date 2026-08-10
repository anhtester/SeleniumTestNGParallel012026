package com.anhtester.Bai28_DriverManager_Parallel.testcases;

import com.anhtester.Bai28_DriverManager_Parallel.pages.DashboardPage;
import com.anhtester.Bai28_DriverManager_Parallel.pages.LoginPage;
import com.anhtester.common.BaseTest;
import com.anhtester.constants.ConfigData;
import com.anhtester.keywords.WebUI;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

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

   @Test
   public void testPatternAAA(){
      WebUI.openURL(ConfigData.LOGIN_URL);
      String email = "admin@example.com";
      String password = "123456";

      softAssert.assertEquals(WebUI.getCurrentURL(), ConfigData.LOGIN_URL, "Fail. Login page URL is incorrect.");
      softAssert.assertEquals(loginPage.getHeaderLoginPage(), loginPage.LOGIN_PAGE_HEADER_TEXT, "Fail. The Login page header text is not match.");

      dashboardPage = loginPage.loginCRM(email, password);
      dashboardPage.verifyNavigateToDashboardPage();
   }

   @Test(priority = 1)
   public void testLoginCRM_Success() {
      dashboardPage = loginPage.loginCRM("admin@example.com", "123456");
      loginPage.verifyLoginSuccess();
   }

   @Test(priority = 2)
   public void testLoginFailWithEmailInvalid() {
      dashboardPage = loginPage.loginCRM("admin123@example.com", "123456");
      loginPage.verifyLoginFail("Invalid email or password");
   }

   @Test(priority = 3)
   public void testLoginFailWithPasswordInvalid() {
      loginPage.loginCRM("admin@example.com", "123");

      loginPage.verifyLoginFail("Invalid email or password");
   }

   @Test(priority = 4)
   public void testLoginFailWithEmailNull() {
      loginPage.loginCRM("", "123456");

      loginPage.verifyLoginFail("The Email Address field is required.");
   }

   @Test(priority = 5)
   public void testLoginFailWithPasswordNull() {
      loginPage.loginCRM("admin@example.com", "");

      loginPage.verifyLoginFail("The Password field is required.");
   }

   @Test(priority = 6)
   public void testLoginFailWithEmailAndPasswordNull() {
      loginPage.loginCRM("", "");

      loginPage.verifyLoginFailWithEmailAndPasswordNull();
   }

   @Test(priority = 7)
   public void testLoginFailWithEmailFormatInvalid_01() {
      loginPage.loginCRM("admin@", "123456");

      loginPage.verifyAlertEmailFormatInvalid();
   }

   @Test(priority = 8)
   public void testLoginFailWithEmailFormatInvalid_02() {
      loginPage.loginCRM("admin@example", "123456");

      loginPage.verifyLoginFail("The Email Address field must contain a valid email address.");
   }

}
