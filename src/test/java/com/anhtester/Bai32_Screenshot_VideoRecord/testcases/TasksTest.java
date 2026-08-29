package com.anhtester.Bai32_Screenshot_VideoRecord.testcases;

import com.anhtester.Bai32_Screenshot_VideoRecord.pages.DashboardPage;
import com.anhtester.Bai32_Screenshot_VideoRecord.pages.LoginPage;
import com.anhtester.Bai32_Screenshot_VideoRecord.pages.TasksPage;
import com.anhtester.common.BaseTest;
import com.anhtester.constants.ConfigData;
import com.anhtester.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TasksTest extends BaseTest {

   private LoginPage loginPage;
   private DashboardPage dashboardPage;
   private TasksPage tasksPage;

   @BeforeMethod
   public void setUp() {
      loginPage = new LoginPage();
   }

   @Test
   public void testAddNewTaskWithProject() {
      //Lấy lại Project Name đã lưu ở file JSON trung gian từ test case Add New Project
      String projectName = JsonUtils.getValueFromJsonFile(ConfigData.PROJECT_DATA_FILE, ConfigData.KEY_PROJECT_NAME);

      String timestamp = String.valueOf(System.currentTimeMillis());
      String taskName = "AUTO_POM_ADD_TASK_" + timestamp;
      String relatedTo = "Project";
      String priority = "High";
      DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
      String startDate = LocalDate.now().format(dateFormat);
      String dueDate = LocalDate.now().plusDays(7).format(dateFormat);

      //Liên kết trang: LoginPage -> DashboardPage -> TasksPage
      dashboardPage = loginPage.loginCRM_AdminRole();
      dashboardPage.verifyNavigateToDashboardPage();
      tasksPage = dashboardPage.clickTasksMenu();

      tasksPage.verifyNavigateToTasksPage()
              .clickNewTaskButton();

      Assert.assertEquals(tasksPage.getAddNewTaskModalTitle(), "Add new task", "Tiêu đề modal tạo Task không đúng.");

      tasksPage.setTaskName(taskName)
              .selectPriority(priority)
              .setStartDate(startDate)
              .setDueDate(dueDate)
              .selectRelatedTo(relatedTo)
              .selectRelatedProject(projectName);

      Assert.assertTrue(tasksPage.getSelectedRelatedProject().contains(projectName),
              "Project lấy từ file JSON chưa được chọn đúng trong form tạo Task.");

      tasksPage.clickSaveButton();

      //Tuân theo mô hình AAA - phần Assert nằm tại class test
      Assert.assertTrue(tasksPage.getTaskNameOnDetailModal().contains(taskName),
              "Modal chi tiết mở ra sau khi lưu phải hiển thị đúng tên task vừa tạo.");

      tasksPage.closeTaskDetailModal();

      Assert.assertTrue(tasksPage.isTasksTableDisplayed(), "Bảng danh sách Tasks phải hiển thị sau khi lưu.");
      Assert.assertTrue(tasksPage.isTaskDisplayed(taskName), "Task vừa tạo phải hiển thị trong danh sách Tasks.");
      Assert.assertTrue(tasksPage.getRelatedProjectOfTask(taskName).contains(projectName),
              "Task vừa tạo phải được gắn với project: " + projectName);
   }
}
