package com.anhtester.Bai28_DriverManager_Parallel.pages;

import com.anhtester.keywords.WebUI;
import org.openqa.selenium.By;

public class BasePage {

   By menuDashboard = By.xpath("//span[normalize-space()='Dashboard']");
   By menuCustomers = By.xpath("//span[normalize-space()='Customers']");
   By menuProjects = By.xpath("//span[normalize-space()='Projects']");
   By menuTasks = By.xpath("//span[normalize-space()='Tasks']");

   public DashboardPage clickDashboardMenu() {
      WebUI.clickElement(menuDashboard);

      return new DashboardPage();
   }

   public CustomersPage clickCustomersMenu() {
      WebUI.clickElement(menuCustomers);

      return new CustomersPage();
   }

   public ProjectsPage clickProjectsMenu() {
      WebUI.clickElement(menuProjects);

      return new ProjectsPage();
   }

   public TasksPage clickTasksMenu() {
      WebUI.clickElement(menuTasks);

      return new TasksPage();
   }

   /**
    * Bọc chuỗi text thành literal an toàn cho XPath (xử lý trường hợp text có dấu nháy).
    * Dùng chung cho các page cần lọc dòng trong datatable theo tên.
    */
   protected String xpathLiteral(String text) {
      if (!text.contains("'")) {
         return "'" + text + "'";
      }
      if (!text.contains("\"")) {
         return "\"" + text + "\"";
      }
      String[] parts = text.split("'");
      StringBuilder builder = new StringBuilder("concat(");
      for (int i = 0; i < parts.length; i++) {
         if (i > 0) {
            builder.append(", \"'\", ");
         }
         builder.append("'").append(parts[i]).append("'");
      }
      builder.append(")");
      return builder.toString();
   }

}
