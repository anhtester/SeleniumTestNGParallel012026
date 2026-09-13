package com.anhtester.Bai36_AllureReport.testcases;

import com.anhtester.Bai36_AllureReport.pages.CustomersPage;
import com.anhtester.Bai36_AllureReport.pages.DashboardPage;
import com.anhtester.Bai36_AllureReport.pages.LoginPage;
import com.anhtester.common.BaseTest;
import com.anhtester.constants.ConfigData;
import com.anhtester.utils.JsonUtils;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Epic("Quản lý khách hàng")
@Feature("Thêm mới và xoá khách hàng")
public class CustomersTest extends BaseTest {

   private LoginPage loginPage;
   private DashboardPage dashboardPage;
   private CustomersPage customersPage;

   @BeforeMethod
   public void setUp() {
      loginPage = new LoginPage();
   }

   @Link(name = "DEV-5861", url = "https://slope.atlassian.net/browse/DEV-5861")
   @Severity(SeverityLevel.CRITICAL)
   @Description("Thêm mới khách hàng với đầy đủ thông tin và kiểm tra dữ liệu đã lưu")
   @Owner("Anh Tester")
   @Test
   public void testAddNewCustomer() {
      String timestamp = String.valueOf(System.currentTimeMillis());
      String companyName = "AUTO_POM_ADD_CUSTOMER_" + timestamp;
      String vatNumber = "VAT" + timestamp;
      String phone = String.format("090%07d", Long.parseLong(timestamp.substring(timestamp.length() - 7)));
      String website = "https://anhtester.com";
      String address = "123 Automation Street";
      String city = "Can Tho";
      String state = "Can Tho";
      String zipCode = "90000";
      String country = "Vietnam";

      //Liên kết trang: LoginPage -> DashboardPage -> CustomersPage
      dashboardPage = loginPage.loginCRM_AdminRole();
      dashboardPage.verifyNavigateToDashboardPage();
      customersPage = dashboardPage.clickCustomersMenu();

      customersPage.verifyNavigateToCustomersPage()
              .clickNewCustomerButton()
              .verifyNavigateToAddNewCustomerPage()
              .fillCustomerDetails(companyName, vatNumber, phone, website)
              .selectDefaultCurrency("USD")
              .selectDefaultLanguage("Vietnamese")
              .fillAddress(address, city, state, zipCode, country)
              .clickSaveButton()
              .waitForCustomerProfilePage();

      //Tuân theo mô hình AAA
      //Nghĩa là phần Assert nằm tại class test
      Assert.assertEquals(customersPage.getPageTitle(), companyName, "Tên công ty trên title trang profile không đúng.");
      Assert.assertEquals(customersPage.getCompanyValue(), companyName, "Company lưu không đúng.");
      Assert.assertEquals(customersPage.getVatNumberValue(), vatNumber, "VAT Number lưu không đúng.");
      Assert.assertEquals(customersPage.getPhoneValue(), phone, "Phone lưu không đúng.");
      Assert.assertEquals(customersPage.getWebsiteValue(), website, "Website lưu không đúng.");
      Assert.assertEquals(customersPage.getAddressValue(), address, "Address lưu không đúng.");
      Assert.assertEquals(customersPage.getCityValue(), city, "City lưu không đúng.");
      Assert.assertEquals(customersPage.getStateValue(), state, "State lưu không đúng.");
      Assert.assertEquals(customersPage.getZipCodeValue(), zipCode, "Zip Code lưu không đúng.");
      Assert.assertEquals(customersPage.getSelectedCountryValue(), country, "Country lưu không đúng.");
      Assert.assertEquals(customersPage.getSelectedDefaultCurrencyValue(), "USD", "Default Currency lưu không đúng.");
      Assert.assertEquals(customersPage.getSelectedDefaultLanguageValue(), "Vietnamese", "Default Language lưu không đúng.");

      //Lưu Customer Name ra file JSON trung gian để test case Project Manage dùng lại
      JsonUtils.setDataToJsonFile(ConfigData.CUSTOMER_DATA_FILE, ConfigData.KEY_CUSTOMER_NAME, customersPage.getCompanyValue());

      Assert.assertEquals(JsonUtils.getValueFromJsonFile(ConfigData.CUSTOMER_DATA_FILE, ConfigData.KEY_CUSTOMER_NAME),
              companyName, "Customer Name lưu vào file JSON không đúng.");
   }

   @Link(name = "DEV-5862", url = "https://slope.atlassian.net/browse/DEV-5862")
   @Severity(SeverityLevel.NORMAL)
   @Description("Xoá khách hàng vừa tạo bằng link Delete")
   @Owner("Anh Tester")
   @Test
   public void testDeleteCustomer() {
      String timestamp = String.valueOf(System.currentTimeMillis());
      String companyName = "AUTO_POM_DELETE_CUSTOMER_" + timestamp;
      String vatNumber = "VAT" + timestamp;
      String phone = String.format("091%07d", Long.parseLong(timestamp.substring(timestamp.length() - 7)));
      String website = "https://anhtester.com";

      //Liên kết trang: LoginPage -> DashboardPage -> CustomersPage
      dashboardPage = loginPage.loginCRM_AdminRole();
      customersPage = dashboardPage.clickCustomersMenu();

      customersPage.verifyNavigateToCustomersPage()
              .clickNewCustomerButton()
              .fillCustomerDetails(companyName, vatNumber, phone, website)
              .clickSaveButton()
              .waitForCustomerProfilePage();

      //Quay lại danh sách Customers bằng menu (liên kết trang từ BasePage)
      customersPage = customersPage.clickCustomersMenu();
      customersPage.verifyNavigateToCustomersPage();
      Assert.assertTrue(customersPage.isCustomerDisplayed(companyName), "Customer vừa tạo phải hiển thị trong danh sách trước khi xóa.");

      customersPage.deleteCustomerByCompanyName(companyName);

      Assert.assertTrue(customersPage.isCustomerNotDisplayed(companyName), "Customer vừa xóa không được còn hiển thị trong danh sách.");
   }

   @Link(name = "DEV-5863", url = "https://slope.atlassian.net/browse/DEV-5863")
   @Severity(SeverityLevel.NORMAL)
   @Description("Xoá khách hàng vừa tạo bằng cách hover vào dòng và xác nhận alert")
   @Owner("Anh Tester")
   @Test
   public void testDeleteCustomerByHoverAndConfirmAlert() {
      String timestamp = String.valueOf(System.currentTimeMillis());
      String companyName = "AUTO_POM_DELETE_HOVER_" + timestamp;
      String vatNumber = "VAT" + timestamp;
      String phone = String.format("092%07d", Long.parseLong(timestamp.substring(timestamp.length() - 7)));
      String website = "https://anhtester.com";

      //Liên kết trang: LoginPage -> DashboardPage -> CustomersPage
      dashboardPage = loginPage.loginCRM_AdminRole();
      customersPage = dashboardPage.clickCustomersMenu();

      //Fluent Page
      customersPage.verifyNavigateToCustomersPage()
              .clickNewCustomerButton()
              .fillCustomerDetails(companyName, vatNumber, phone, website)
              .clickSaveButton()
              .waitForCustomerProfilePage();

      //Page Pbject Model thuần tuý
//      customersPage.verifyNavigateToCustomersPage();
//      customersPage.clickNewCustomerButton();
//      customersPage.fillCustomerDetails(companyName, vatNumber, phone, website);

      //Quay lại danh sách Customers bằng menu (liên kết trang từ BasePage)
      customersPage = customersPage.clickCustomersMenu();
      customersPage.verifyNavigateToCustomersPage();
      Assert.assertTrue(customersPage.isCustomerDisplayed(companyName), "Customer vừa tạo phải hiển thị trong danh sách trước khi xóa.");

      customersPage.deleteCustomerByHoverAndConfirmAlert(companyName);

      Assert.assertTrue(customersPage.isCustomerNotDisplayed(companyName), "Customer vừa xóa qua hover + confirm alert không được còn hiển thị trong danh sách.");
   }
}
