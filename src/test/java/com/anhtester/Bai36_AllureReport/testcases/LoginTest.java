package com.anhtester.Bai36_AllureReport.testcases;

import com.anhtester.Bai36_AllureReport.pages.DashboardPage;
import com.anhtester.Bai36_AllureReport.pages.LoginPage;
import com.anhtester.common.BaseTest;
import com.anhtester.dataproviders.DataProviderFactory;
import io.qameta.allure.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Hashtable;

@Epic("Xác thực người dùng")
@Feature("Đăng nhập với thông tin điền vào")
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

   @Link(name = "DEV-5853", url = "https://slope.atlassian.net/browse/DEV-5853")
   @Severity(SeverityLevel.CRITICAL)
   @Description("Đăng nhập thành công với thông tin hợp lệ")
   @Owner("Anh Tester")
   @Test(priority = 1, dataProvider = "data_login", dataProviderClass = DataProviderFactory.class)
   public void testLoginCRM_Success(Hashtable<String, String> data) {
      dashboardPage = loginPage.loginCRM(data.get("EMAIL"), data.get("PASSWORD"));
      loginPage.verifyLoginSuccess();
   }

   @Link(name = "DEV-5854", url = "https://slope.atlassian.net/browse/DEV-5854")
   @Severity(SeverityLevel.MINOR)
   @Description("Đăng nhập thất bại với email không hợp lệ")
   @Owner("An Vo")
   @Test(priority = 2, dataProvider = "data_login", dataProviderClass = DataProviderFactory.class)
   public void testLoginFailWithEmailInvalid(Hashtable<String, String> data) {
      dashboardPage = loginPage.loginCRM(data.get("EMAIL"), data.get("PASSWORD"));
      loginPage.verifyLoginFail("Invalid email or password 123");
   }

   @Link(name = "DEV-5855", url = "https://slope.atlassian.net/browse/DEV-5855")
   @Severity(SeverityLevel.MINOR)
   @Description("Đăng nhập thất bại với password không hợp lệ")
   @Owner("Anh Tester")
   @Test(priority = 3, dataProvider = "data_login", dataProviderClass = DataProviderFactory.class)
   public void testLoginFailWithPasswordInvalid(Hashtable<String, String> data) {
      dashboardPage = loginPage.loginCRM(data.get("EMAIL"), data.get("PASSWORD"));
      loginPage.verifyLoginFail("Invalid email or password");
   }

   @Link(name = "DEV-5856", url = "https://slope.atlassian.net/browse/DEV-5856")
   @Severity(SeverityLevel.MINOR)
   @Description("Đăng nhập thất bại khi bỏ trống email")
   @Owner("Anh Tester")
   @Test(priority = 4, dataProvider = "data_login", dataProviderClass = DataProviderFactory.class)
   public void testLoginFailWithEmailNull(Hashtable<String, String> data) {
      dashboardPage = loginPage.loginCRM(data.get("EMAIL"), data.get("PASSWORD"));
      loginPage.verifyLoginFail("The Email Address field is required.");
   }

   @Link(name = "DEV-5857", url = "https://slope.atlassian.net/browse/DEV-5857")
   @Severity(SeverityLevel.MINOR)
   @Description("Đăng nhập thất bại khi bỏ trống password")
   @Owner("Anh Tester")
   @Test(priority = 5, dataProvider = "data_login", dataProviderClass = DataProviderFactory.class)
   public void testLoginFailWithPasswordNull(Hashtable<String, String> data) {
      dashboardPage = loginPage.loginCRM(data.get("EMAIL"), data.get("PASSWORD"));
      loginPage.verifyLoginFail("The Password field is required.");
   }

   @Link(name = "DEV-5858", url = "https://slope.atlassian.net/browse/DEV-5858")
   @Severity(SeverityLevel.MINOR)
   @Description("Đăng nhập thất bại khi bỏ trống cả email và password")
   @Owner("Anh Tester")
   @Test(priority = 6, dataProvider = "data_login", dataProviderClass = DataProviderFactory.class)
   public void testLoginFailWithEmailAndPasswordNull(Hashtable<String, String> data) {
      dashboardPage = loginPage.loginCRM(data.get("EMAIL"), data.get("PASSWORD"));
      loginPage.verifyLoginFailWithEmailAndPasswordNull();
   }

   @Link(name = "DEV-5859", url = "https://slope.atlassian.net/browse/DEV-5859")
   @Severity(SeverityLevel.MINOR)
   @Description("Đăng nhập thất bại với email sai định dạng (thiếu phần sau @)")
   @Owner("Anh Tester")
   @Test(priority = 7, dataProvider = "data_login", dataProviderClass = DataProviderFactory.class)
   public void testLoginFailWithEmailFormatInvalid_01(Hashtable<String, String> data) {
      dashboardPage = loginPage.loginCRM(data.get("EMAIL"), data.get("PASSWORD"));
      loginPage.verifyAlertEmailFormatInvalid();
   }

   @Link(name = "DEV-5860", url = "https://slope.atlassian.net/browse/DEV-5860")
   @Severity(SeverityLevel.MINOR)
   @Description("Đăng nhập thất bại với email sai định dạng - trường hợp 02")
   @Owner("Anh Tester")
   @Test(priority = 8, dataProvider = "data_login", dataProviderClass = DataProviderFactory.class)
   public void testLoginFailWithEmailFormatInvalid_02(Hashtable<String, String> data) {
      dashboardPage = loginPage.loginCRM(data.get("EMAIL"), data.get("PASSWORD"));
      loginPage.verifyLoginFail("The Password field is required.");
   }

}
