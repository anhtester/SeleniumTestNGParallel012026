package com.anhtester.Bai30_Excel_Data.testcases;

import com.anhtester.Bai30_Excel_Data.pages.DashboardPage;
import com.anhtester.Bai30_Excel_Data.pages.LoginPage;
import com.anhtester.common.BaseTest;
import com.anhtester.constants.ConfigData;
import com.anhtester.helpers.ExcelHelper;
import com.anhtester.keywords.WebUI;
import org.testng.ITestResult;
import org.testng.annotations.*;
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

   @Test(priority = 1)
   public void testLoginCRM_Success() {
      ExcelHelper excelHelper = new ExcelHelper();
      excelHelper.setExcelFile(ConfigData.excel_path_crm_data, "Login");
      dashboardPage = loginPage.loginCRM(excelHelper.getCellData("EMAIL", 1), excelHelper.getCellData("PASSWORD", 1));
      loginPage.verifyLoginSuccess();
   }

   @Test(priority = 2)
   public void testLoginFailWithEmailInvalid() {
      ExcelHelper excelHelper = new ExcelHelper();
      excelHelper.setExcelFile(ConfigData.excel_path_crm_data, "Login");
      dashboardPage = loginPage.loginCRM(excelHelper.getCellData("EMAIL", 2), excelHelper.getCellData("PASSWORD", 2));
      loginPage.verifyLoginFail("Invalid email or password");
   }

   @Test(priority = 3)
   public void testLoginFailWithPasswordInvalid() {
      ExcelHelper excelHelper = new ExcelHelper();
      excelHelper.setExcelFile(ConfigData.excel_path_crm_data, "Login");
      dashboardPage = loginPage.loginCRM(excelHelper.getCellData("EMAIL", 3), excelHelper.getCellData("PASSWORD", 3));
      loginPage.verifyLoginFail("Invalid email or password");
   }

   @Test(priority = 4)
   public void testLoginFailWithEmailNull() {
      ExcelHelper excelHelper = new ExcelHelper();
      excelHelper.setExcelFile(ConfigData.excel_path_crm_data, "Login");
      dashboardPage = loginPage.loginCRM(excelHelper.getCellData("EMAIL", 4), excelHelper.getCellData("PASSWORD", 4));
      loginPage.verifyLoginFail("The Email Address field is required.");
   }

   @Test(priority = 5)
   public void testLoginFailWithPasswordNull() {
      ExcelHelper excelHelper = new ExcelHelper();
      excelHelper.setExcelFile(ConfigData.excel_path_crm_data, "Login");
      dashboardPage = loginPage.loginCRM(excelHelper.getCellData("EMAIL", 5), excelHelper.getCellData("PASSWORD", 5));
      loginPage.verifyLoginFail("The Password field is required.");
   }

   @Test(priority = 6)
   public void testLoginFailWithEmailAndPasswordNull() {
      ExcelHelper excelHelper = new ExcelHelper();
      excelHelper.setExcelFile(ConfigData.excel_path_crm_data, "Login");
      dashboardPage = loginPage.loginCRM(excelHelper.getCellData("EMAIL", 6), excelHelper.getCellData("PASSWORD", 6));
      loginPage.verifyLoginFailWithEmailAndPasswordNull();
   }

   @Test(priority = 7)
   public void testLoginFailWithEmailFormatInvalid_01() {
      ExcelHelper excelHelper = new ExcelHelper();
      excelHelper.setExcelFile(ConfigData.excel_path_crm_data, "Login");
      dashboardPage = loginPage.loginCRM(excelHelper.getCellData("EMAIL", 7), excelHelper.getCellData("PASSWORD", 7));
      loginPage.verifyAlertEmailFormatInvalid();
   }

   @Test(priority = 8)
   public void testLoginFailWithEmailFormatInvalid_02() {
      ExcelHelper excelHelper = new ExcelHelper();
      excelHelper.setExcelFile(ConfigData.excel_path_crm_data, "Login");
      dashboardPage = loginPage.loginCRM(excelHelper.getCellData("EMAIL", 8), excelHelper.getCellData("PASSWORD", 8));
      loginPage.verifyLoginFail("The Password field is required.");
   }

}
