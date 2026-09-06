package com.anhtester.Bai34_Log4j2.testcases;

import com.anhtester.Bai34_Log4j2.pages.DashboardPage;
import com.anhtester.Bai34_Log4j2.pages.LoginPage;
import com.anhtester.Bai34_Log4j2.pages.ProjectsPage;
import com.anhtester.common.BaseTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DashboardTest extends BaseTest {

   private LoginPage loginPage;
   private DashboardPage dashboardPage;
   private ProjectsPage projectsPage;

   @BeforeMethod
   public void setUp() {
      loginPage = new LoginPage();
   }

   @Test
   public void test_E2E_VerifyTotalInvoicesAwaitingPayment(){
      dashboardPage = loginPage.loginCRM_AdminRole();
      dashboardPage.verifyNavigateToDashboardPage();
      dashboardPage.verifyTotalInvoicesAwaitingPayment("3 / 5");
   }

   @Test
   public void test_E2E_VerifyTotalConvertedLeads(){
      dashboardPage = loginPage.loginCRM_AdminRole();
      dashboardPage.verifyNavigateToDashboardPage();
      dashboardPage.verifyTotalConvertedLeads("0 / 0");
   }

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

   @Test
   public void test_E2E_VerifyTotalTasksNotFinished(){
      dashboardPage = loginPage.loginCRM_AdminRole();
      dashboardPage.verifyNavigateToDashboardPage();
      dashboardPage.verifyTotalTasksNotFinished("197 / 197");
   }
}
