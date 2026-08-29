package com.anhtester.Bai32_Screenshot_VideoRecord.pages;

import com.anhtester.constants.ConfigData;
import com.anhtester.helpers.CaptureHelper;
import com.anhtester.keywords.WebUI;
import org.openqa.selenium.By;
import org.testng.Assert;

public class DashboardPage extends BasePage {

   private String dashboardPageUrl = "/admin/";
   private By menuDashboard = By.xpath("//span[normalize-space()='Dashboard']");
   private By totalInvoicesAwaitingPayment = By.xpath("(//span[normalize-space()='Invoices Awaiting Payment']/parent::div)/following-sibling::span");
   private By totalConvertedLeads = By.xpath("(//span[normalize-space()='Converted Leads']/parent::div)/following-sibling::span");
   private By totalProjectsInProgress = By.xpath("(//span[normalize-space()='Projects In Progress']/parent::div)/following-sibling::span");
   private By totalTasksNotFinished = By.xpath("(//span[normalize-space()='Tasks Not Finished']/parent::div)/following-sibling::span");

   public void verifyNavigateToDashboardPage() {
      WebUI.waitForPageLoaded();
      CaptureHelper.captureScreenshot("Dashboard Page");
      WebUI.waitForElementVisible(menuDashboard);
      WebUI.assertEquals(WebUI.getCurrentURL(), ConfigData.BASE_URL + dashboardPageUrl, "The dashboard page URL not match.");
   }

   public void verifyTotalInvoicesAwaitingPayment(String expectedValue) {
      WebUI.waitForPageLoaded();
      Assert.assertTrue(WebUI.checkElementExist(totalInvoicesAwaitingPayment), "The section Invoices Awaiting Payment not display.");
      WebUI.assertEquals(WebUI.getElementText(totalInvoicesAwaitingPayment), expectedValue, "FAIL!! Invoices Awaiting Payment total not match.");
   }

   public void verifyTotalConvertedLeads(String expectedValue) {
      WebUI.waitForPageLoaded();
      Assert.assertTrue(WebUI.checkElementExist(totalConvertedLeads), "The section Converted Leads not display.");
      WebUI.assertEquals(WebUI.getElementText(totalConvertedLeads), expectedValue, "FAIL!! Converted Leads total not match.");
   }

   public void verifyTotalProjectsInProgress(int projectsInProgress, int projectsTotal) {
      WebUI.waitForPageLoaded();
      WebUI.assertEquals(WebUI.getElementText(totalProjectsInProgress), projectsInProgress + " / " + projectsTotal, "Total Projects In Progress not match.");
   }

   public void verifyTotalTasksNotFinished(String expectedValue) {
      WebUI.waitForPageLoaded();
      Assert.assertTrue(WebUI.checkElementExist(totalTasksNotFinished), "The section Tasks Not Finished not display.");
      WebUI.assertEquals(WebUI.getElementText(totalTasksNotFinished), expectedValue, "FAIL!! Tasks Not Finished total not match.");
   }

}
