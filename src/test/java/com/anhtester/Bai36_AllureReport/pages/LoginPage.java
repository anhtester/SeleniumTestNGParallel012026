package com.anhtester.Bai36_AllureReport.pages;

import com.anhtester.constants.ConfigData;
import com.anhtester.drivers.ParameterManager;
import com.anhtester.keywords.WebUI;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class LoginPage extends BasePage {

   public String LOGIN_URL = ConfigData.LOGIN_URL;
   public String LOGIN_PAGE_TITLE = "Perfex CRM | Anh Tester Demo - Login";
   public String LOGIN_PAGE_HEADER_TEXT = "Login";

   //Khai báo các element dạng đối tượng By (phương thức tìm kiếm)
   private By headerPage = By.xpath("//h1[normalize-space()='Login']");
   private By inputEmail = By.xpath("//input[@id='email']");
   private By inputPassword = By.xpath("//input[@id='password']");
   private By buttonLogin = By.xpath("//button[normalize-space()='Login']");
   private By errorMessage = By.xpath("//div[contains(@class,'alert-danger')]");
   private By alertEmailRequiredMessage = By.xpath("//div[normalize-space()='The Email Address field is required.']");
   private By alertPasswordRequiredMessage = By.xpath("//div[normalize-space()='The Password field is required.']");

   @Step("Kiểm tra đã đến được trang Login")
   public void verifyNavigateToTheLoginPage() {
      WebUI.waitForPageLoaded();
      //Title, URL, Header
      SoftAssert softAssert = new SoftAssert();
      softAssert.assertEquals(WebUI.getPageTitle(), LOGIN_PAGE_TITLE, "Fail. The Login page title not match.");
      Assert.assertEquals(WebUI.getCurrentURL(), LOGIN_URL, "Fail. The Login page url not match.");
      softAssert.assertEquals(WebUI.getElementText(headerPage), LOGIN_PAGE_HEADER_TEXT, "Fail. The Login page header not match.");
      softAssert.assertAll();
   }

   @Step("Lấy header trang Login")
   public String getHeaderLoginPage() {
      return WebUI.getElementText(headerPage);
   }

   //Khai báo các hàm xử lý automation phục vụ cho trang Login
   @Step("Điền Email {0}")
   private void setEmail(String email) {
      WebUI.setText(inputEmail, email);
   }

   @Step("Điền Password {0}")
   private void setPassword(String password) {
      WebUI.setText(inputPassword, password);
   }

   @Step("Click nút Login")
   private void clickLoginButton() {
      WebUI.clickElement(buttonLogin);
   }

   @Step("Kiểm tra đăng nhập thành công")
   public void verifyLoginSuccess() {
      new DashboardPage().verifyNavigateToDashboardPage();
      WebUI.waitForCurrentURLContains("/admin/");
      Assert.assertTrue(WebUI.getCurrentURL().contains("/admin/"), "FAIL. Không chuyển hướng sang trang Dashboard");
      Assert.assertFalse(WebUI.getCurrentURL().contains("authentication"), "FAIL. Vẫn đang ở trang Login");
   }

   @Step("Kiểm tra đăng nhập thất bại")
   public void verifyLoginFail(String message) {
      WebUI.waitForPageLoaded();
      //WebUI.waitForElementVisible(errorMessage);
      Assert.assertTrue(WebUI.checkElementExist(errorMessage, 10, 1000), "Error message NOT displays");
      WebUI.assertEquals(WebUI.getElementText(errorMessage), message, "Content of error massage NOT match.");
      WebUI.assertContains(WebUI.getCurrentURL(), "authentication", "FAIL. Không còn ở trang Login");
   }

   @Step("Kiểm tra đăng nhập thất bại khi bỏ trống Email và Password")
   public void verifyLoginFailWithEmailAndPasswordNull() {
      boolean checkEmailErrorMessage = WebUI.checkElementExist(alertEmailRequiredMessage, 5, 1000);
      Assert.assertTrue(checkEmailErrorMessage, "Fail. The Email Error Message is not present");

      boolean checkPasswordErrorMessage = WebUI.checkElementExist(alertPasswordRequiredMessage, 5, 1000);
      Assert.assertTrue(checkPasswordErrorMessage, "Fail. The Password Error Message is not present");

      Assert.assertEquals(WebUI.getCurrentURL(), "https://crm.anhtester.com/admin/authentication", "The Current LOGIN_URL is not correct");
   }

   @Step("Kiểm tra thông báo Email sai định dạng")
   public void verifyAlertEmailFormatInvalid() {
      if (ParameterManager.getBrowser().equalsIgnoreCase("firefox")) {
         Assert.assertEquals(WebUI.getElementAttribute(inputEmail, "validationMessage"), "Vui lòng điền một địa chỉ email.", "Fail. The HTML5 Error Message is not match.");
      } else {
         Assert.assertEquals(WebUI.getElementAttribute(inputEmail, "validationMessage"), "Please enter a part following '@'. 'admin@' is incomplete.", "Fail. The HTML5 Error Message is not match.");
      }
   }

   //Các hàm xử lý cho chính trang này
   @Step("Đăng nhập CRM với Email {0}")
   public DashboardPage loginCRM(String email, String password) {
      //https://crm.anhtester.com/admin/authentication
      WebUI.openURL(ConfigData.LOGIN_URL); //Gọi từ class ConfigData dạng biến static
      verifyNavigateToTheLoginPage();
      setEmail(email);
      setPassword(password);
      clickLoginButton();

      return new DashboardPage();
   }

   @Step("Đăng nhập CRM với quyền Admin")
   public DashboardPage loginCRM_AdminRole() {
      WebUI.openURL(ConfigData.LOGIN_URL);
      verifyNavigateToTheLoginPage();
      setEmail(ConfigData.EMAIL_ADMIN);
      setPassword(ConfigData.PASSWORD_ADMIN);
      clickLoginButton();
      verifyLoginSuccess();

      return new DashboardPage();
   }

}
