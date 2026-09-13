package com.anhtester.Bai36_AllureReport.testcases;

import com.anhtester.Bai36_AllureReport.pages.DashboardPage;
import com.anhtester.Bai36_AllureReport.pages.LoginPage;
import com.anhtester.Bai36_AllureReport.pages.ProjectsPage;
import com.anhtester.common.BaseTest;
import com.anhtester.constants.ConfigData;
import com.anhtester.utils.JsonUtils;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Epic("Quản lý dự án")
@Feature("Thêm mới và xoá dự án")
public class ProjectsTest extends BaseTest {

   private LoginPage loginPage;
   private DashboardPage dashboardPage;
   private ProjectsPage projectsPage;

   @BeforeMethod
   public void setUp() {
      loginPage = new LoginPage();
   }

   @Link(name = "DEV-5868", url = "https://slope.atlassian.net/browse/DEV-5868")
   @Severity(SeverityLevel.CRITICAL)
   @Description("Thêm mới dự án gắn với khách hàng đã tạo và kiểm tra dữ liệu đã lưu")
   @Owner("Anh Tester")
   @Test
   public void testAddNewProject() {
      //Lấy lại Customer Name đã lưu ở file JSON trung gian từ test case Add New Customer
      String customerName = JsonUtils.getValueFromJsonFile(ConfigData.CUSTOMER_DATA_FILE, ConfigData.KEY_CUSTOMER_NAME);

      String timestamp = String.valueOf(System.currentTimeMillis());
      String projectName = "AUTO_POM_ADD_PROJECT_" + timestamp;
      String billingType = "Fixed Rate";
      String status = "Not Started";
      String projectCost = "5000";
      String estimatedHours = "100";
      DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
      String startDate = LocalDate.now().format(dateFormat);
      String deadline = LocalDate.now().plusDays(30).format(dateFormat);

      //Liên kết trang: LoginPage -> DashboardPage -> ProjectsPage
      dashboardPage = loginPage.loginCRM_AdminRole();
      dashboardPage.verifyNavigateToDashboardPage();
      projectsPage = dashboardPage.clickProjectsMenu();

      projectsPage.verifyNavigateToProjectsPage()
              .clickNewProjectButton()
              .verifyNavigateToAddNewProjectPage()
              .setProjectName(projectName)
              .selectCustomer(customerName)
              .selectBillingType(billingType)
              .setProjectCost(projectCost)
              .selectStatus(status)
              .setEstimatedHours(estimatedHours)
              .setStartDate(startDate)
              .setDeadline(deadline)
              .clickSaveButton()
              .waitForProjectViewPage();

      //Navigate to Project detail page
      //Tuân theo mô hình AAA - phần Assert nằm tại class test
      Assert.assertEquals(projectsPage.getPageTitle(), projectName, "Tên project trên title trang chi tiết không đúng.");
      Assert.assertEquals(projectsPage.getProjectNameOnViewPage(), projectName, "Project Name lưu không đúng.");
      Assert.assertEquals(projectsPage.getCustomerNameOnViewPage(), customerName, "Customer lấy từ file JSON chưa được gán đúng cho project.");
      Assert.assertEquals(projectsPage.getBillingTypeOnViewPage(), billingType, "Billing Type lưu không đúng.");
      Assert.assertEquals(projectsPage.getStatusOnViewPage(), status, "Status lưu không đúng.");
      Assert.assertEquals(projectsPage.getStartDateOnViewPage(), startDate, "Start Date lưu không đúng.");
      Assert.assertFalse(projectsPage.getProjectIdOnViewPage().isEmpty(), "Project ID phải hiển thị trên trang chi tiết.");

      //Quay lại danh sách Projects bằng menu (liên kết trang từ BasePage)
      projectsPage = projectsPage.clickProjectsMenu();
      projectsPage.verifyNavigateToProjectsPage();

      Assert.assertTrue(projectsPage.isProjectsTableDisplayed(), "Bảng danh sách Projects phải hiển thị.");
      Assert.assertTrue(projectsPage.isProjectDisplayed(projectName), "Project vừa tạo phải hiển thị trong danh sách Projects.");

      //Lưu Project Name ra file JSON trung gian để test case Add New Task dùng lại
      JsonUtils.setDataToJsonFile(ConfigData.PROJECT_DATA_FILE, ConfigData.KEY_PROJECT_NAME, projectName);

      Assert.assertEquals(JsonUtils.getValueFromJsonFile(ConfigData.PROJECT_DATA_FILE, ConfigData.KEY_PROJECT_NAME),
              projectName, "Project Name lưu vào file JSON không đúng.");
   }

   @Link(name = "DEV-5869", url = "https://slope.atlassian.net/browse/DEV-5869")
   @Severity(SeverityLevel.NORMAL)
   @Description("Xoá dự án vừa tạo bằng cách hover vào dòng và xác nhận alert")
   @Owner("Anh Tester")
   @Test
   public void testDeleteProject() {
      //Lấy lại Customer Name đã lưu ở file JSON trung gian từ test case Add New Customer
      String customerName = JsonUtils.getValueFromJsonFile(ConfigData.CUSTOMER_DATA_FILE, ConfigData.KEY_CUSTOMER_NAME);

      String timestamp = String.valueOf(System.currentTimeMillis());
      String projectName = "AUTO_POM_DELETE_PROJECT_" + timestamp;

      //Liên kết trang: LoginPage -> DashboardPage -> ProjectsPage
      dashboardPage = loginPage.loginCRM_AdminRole();
      projectsPage = dashboardPage.clickProjectsMenu();

      projectsPage.verifyNavigateToProjectsPage()
              .clickNewProjectButton()
              .setProjectName(projectName)
              .selectCustomer(customerName)
              .clickSaveButton()
              .waitForProjectViewPage();

      //Quay lại danh sách Projects bằng menu (liên kết trang từ BasePage)
      projectsPage = projectsPage.clickProjectsMenu();
      projectsPage.verifyNavigateToProjectsPage();
      Assert.assertTrue(projectsPage.isProjectDisplayed(projectName), "Project vừa tạo phải hiển thị trong danh sách trước khi xóa.");

      projectsPage.deleteProjectByHoverAndConfirmAlert(projectName);

      Assert.assertTrue(projectsPage.isProjectNotDisplayed(projectName), "Project vừa xóa không được còn hiển thị trong danh sách Projects.");
   }
}
