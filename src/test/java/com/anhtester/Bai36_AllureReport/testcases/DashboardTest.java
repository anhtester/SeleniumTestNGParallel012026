package com.anhtester.Bai36_AllureReport.testcases;

import com.anhtester.Bai36_AllureReport.pages.DashboardPage;
import com.anhtester.Bai36_AllureReport.pages.LoginPage;
import com.anhtester.Bai36_AllureReport.pages.ProjectsPage;
import com.anhtester.common.BaseTest;
import io.qameta.allure.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Epic("Dashboard")
@Feature("Thống kê tổng quan trên Dashboard")
public class DashboardTest extends BaseTest {

   private LoginPage loginPage;
   private DashboardPage dashboardPage;
   private ProjectsPage projectsPage;

   @BeforeMethod
   public void setUp() {
      loginPage = new LoginPage();
   }

   @Link(name = "DEV-5864", url = "https://slope.atlassian.net/browse/DEV-5864")
   @Severity(SeverityLevel.NORMAL)
   @Description("Kiểm tra tổng số Invoices Awaiting Payment trên Dashboard")
   @Owner("Anh Tester")
   @Test
   public void test_E2E_VerifyTotalInvoicesAwaitingPayment(){
      dashboardPage = loginPage.loginCRM_AdminRole();
      dashboardPage.verifyNavigateToDashboardPage();
      dashboardPage.verifyTotalInvoicesAwaitingPayment("3 / 5");
   }

   @Link(name = "DEV-5865", url = "https://slope.atlassian.net/browse/DEV-5865")
   @Severity(SeverityLevel.NORMAL)
   @Description("Kiểm tra tổng số Converted Leads trên Dashboard")
   @Owner("Anh Tester")
   @Test
   public void test_E2E_VerifyTotalConvertedLeads(){
      dashboardPage = loginPage.loginCRM_AdminRole();
      dashboardPage.verifyNavigateToDashboardPage();
      dashboardPage.verifyTotalConvertedLeads("0 / 0");
   }

   @Link(name = "DEV-5866", url = "https://slope.atlassian.net/browse/DEV-5866")
   @Severity(SeverityLevel.NORMAL)
   @Description("Kiểm tra tổng số Projects In Progress trên Dashboard khớp với số liệu ở trang Projects")
   @Owner("Anh Tester")
   @Test
   public void test_E2E_VerifyTotalProjectsInProgress() {
      dashboardPage = loginPage.loginCRM_AdminRole();
      dashboardPage.verifyNavigateToDashboardPage();
      projectsPage = dashboardPage.clickProjectsMenu();
      projectsPage.verifyNavigateToProjectsPage();
      int notStartedTotal = projectsPage.getNotStartedTotal();
      int inProgressTotal = projectsPage.getInProgressTotal();
      int onHoldTotal = projectsPage.getOnHoldTotal();
      int cancelledTotal = projectsPage.getCancelledTotal();
      int finishedTotal = projectsPage.getFinishedTotal();
      int projectTotal = notStartedTotal +  inProgressTotal + onHoldTotal + cancelledTotal + finishedTotal;
      System.out.println("Total projects: " + projectTotal);
      dashboardPage = projectsPage.clickDashboardMenu();
      dashboardPage.verifyNavigateToDashboardPage();
      dashboardPage.verifyTotalProjectsInProgress(inProgressTotal, projectTotal);
   }

   @Link(name = "DEV-5867", url = "https://slope.atlassian.net/browse/DEV-5867")
   @Severity(SeverityLevel.NORMAL)
   @Description("Kiểm tra tổng số Tasks Not Finished trên Dashboard")
   @Owner("Anh Tester")
   @Test
   public void test_E2E_VerifyTotalTasksNotFinished(){
      dashboardPage = loginPage.loginCRM_AdminRole();
      dashboardPage.verifyNavigateToDashboardPage();
      dashboardPage.verifyTotalTasksNotFinished("197 / 197");
   }
}
