package com.anhtester.Bai30_Excel_Data.pages;

import com.anhtester.constants.ConfigData;
import com.anhtester.keywords.WebUI;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class TasksPage extends BasePage {
   //Số lần được phép gõ lại từ khoá vào ô search ajax khi danh sách chưa nạp về kịp
   private static final int SEARCH_MAX_RETRIES = 3;

   private String tasksPageUrl = "/admin/tasks";

   private By headerTasksSummary = By.xpath("//span[normalize-space()='Tasks Summary']");
   private By buttonNewTask = By.xpath("//a[contains(@class,'btn-primary') and contains(normalize-space(),'New Task')]");
   private By tableTasks = By.cssSelector("table#tasks");
   private By tableTasksBody = By.cssSelector("#tasks tbody");
   private By inputSearchTask = By.cssSelector("#tasks_filter input[type='search']");
   //Lớp phủ "Processing..." của Datatable, hiện lên trong lúc bảng đang chờ dữ liệu ajax về
   private By tableTasksProcessing = By.cssSelector("#tasks_processing");

   //Form Add New Task nằm trong modal, được render động khi bấm nút New Task
   private By modalTask = By.cssSelector("#_task_modal.in");
   private By modalTaskTitle = By.cssSelector("#_task_modal .modal-title");
   private By inputTaskName = By.cssSelector("#_task_modal input#name");
   private By inputStartDate = By.cssSelector("#_task_modal input#startdate");
   private By inputDueDate = By.cssSelector("#_task_modal input#duedate");
   private By buttonPriorityDropdown = By.cssSelector("button[data-id='priority']");
   private By buttonRelatedToDropdown = By.cssSelector("button[data-id='rel_type']");
   private By buttonRelatedItemDropdown = By.cssSelector("button[data-id='rel_id']");
   private By inputRelatedItemSearch = By.xpath("//select[@id='rel_id']/parent::div//div[contains(@class,'bs-searchbox')]/input");
   private By buttonSave = By.cssSelector("#_task_modal button[type='submit'].btn-primary");
   private By modalBackdrop = By.cssSelector(".modal-backdrop");

   //Modal chi tiết task, tự động mở ra sau khi lưu task mới
   private By modalTaskDetail = By.cssSelector("#task-modal.in");
   private By modalTaskDetailTitle = By.cssSelector("#task-modal .modal-title");
   private By buttonCloseTaskDetail = By.cssSelector("#task-modal button.close");
   private By floatAlert = By.cssSelector(".float-alert");

   public TasksPage openTasksPage() {
      WebUI.openURL(ConfigData.BASE_URL + tasksPageUrl);
      WebUI.waitForPageLoaded();
      WebUI.waitForElementVisible(headerTasksSummary);

      return this;
   }

   public TasksPage verifyNavigateToTasksPage() {
      WebUI.waitForPageLoaded();
      WebUI.waitForElementVisible(headerTasksSummary);
      WebUI.waitForCurrentURLContains(tasksPageUrl);

      return this;
   }

   public TasksPage clickNewTaskButton() {
      WebUI.clickElement(buttonNewTask);
      //Chờ modal chạy xong hiệu ứng fade để tránh click trượt khi modal còn di chuyển
      WebUI.waitForElementVisible(modalTask);
      WebUI.waitForElementClickable(inputTaskName);

      return this;
   }

   public String getAddNewTaskModalTitle() {
      return WebUI.getElementText(modalTaskTitle).trim();
   }

   public TasksPage setTaskName(String taskName) {
      WebUI.setText(inputTaskName, taskName);

      return this;
   }

   /**
    * Điền ngày bằng Javascript thay vì gõ tay vào ô.
    * Ô ngày do plugin xdsoft quản lý: chỉ cần con trỏ chạm vào ô là lịch bung ra, mà lịch lại nằm đè
    * đúng hàng chứa các dropdown ngay bên dưới. Đóng lịch sau khi gõ là không ăn thua - plugin tự bung
    * lại mỗi khi form vẽ lại (rõ nhất là sau khi đổi Related To), nên mọi cú bấm vào dropdown phía sau
    * đều bị lịch chặn cho tới hết thời gian chờ. Không chạm vào ô thì lịch không bao giờ bung.
    * WebUI.setTextByJS có bắn kèm sự kiện input và change nên form vẫn nhận đúng giá trị như gõ tay.
    */
   public TasksPage setStartDate(String startDate) {
      WebUI.setTextByJS(inputStartDate, startDate);
      WebUI.waitForValueToBe(inputStartDate, startDate);

      return this;
   }

   public TasksPage setDueDate(String dueDate) {
      WebUI.setTextByJS(inputDueDate, dueDate);
      WebUI.waitForValueToBe(inputDueDate, dueDate);

      return this;
   }

   public TasksPage selectPriority(String priority) {
      selectPickerByText(buttonPriorityDropdown, "priority", priority);

      return this;
   }

   public TasksPage selectRelatedTo(String relatedType) {
      selectPickerByText(buttonRelatedToDropdown, "rel_type", relatedType);

      return this;
   }

   /**
    * Ô chọn Project là selectpicker ajax-search, phải gõ thật để nạp dữ liệu về.
    * Option hiển thị theo dạng "#id - Tên project - Tên customer" nên chỉ so khớp chứa tên project.
    */
   public TasksPage selectRelatedProject(String projectName) {
      By optionProject = getSelectPickerOptionContains("rel_id", projectName);
      WebUI.searchSelectPickerOption(buttonRelatedItemDropdown, inputRelatedItemSearch, optionProject, projectName, SEARCH_MAX_RETRIES);
      WebUI.retryUntil(driver -> {
         driver.findElement(optionProject).click();
         return true;
      });

      WebUI.waitForAttributeContains(buttonRelatedItemDropdown, "title", projectName);

      return this;
   }

   public String getSelectedRelatedProject() {
      return WebUI.getElementAttribute(buttonRelatedItemDropdown, "title");
   }

   /**
    * Lưu xong, Perfex gỡ form nhập và mở tiếp modal chi tiết của task vừa tạo.
    */
   public TasksPage clickSaveButton() {
      WebUI.clickElement(buttonSave);
      WebUI.waitForElementInVisible(modalTask);
      WebUI.waitForElementVisible(modalTaskDetailTitle);

      return this;
   }

   public String getTaskNameOnDetailModal() {
      return WebUI.getElementText(modalTaskDetailTitle).trim();
   }

   /**
    * Đóng modal chi tiết task và chờ lớp phủ tan hẳn, nếu không bảng danh sách phía dưới chưa thao tác được.
    */
   public TasksPage closeTaskDetailModal() {
      //Toast thông báo lưu thành công nằm đè lên nút đóng ở góc phải trên, chờ nó tự tắt rồi mới bấm
      WebUI.retryUntil(driver -> driver.findElements(floatAlert).stream().noneMatch(WebElement::isDisplayed));
      WebUI.clickElement(buttonCloseTaskDetail);
      WebUI.waitForElementInVisible(modalTaskDetail);
      WebUI.retryUntil(driver -> driver.findElements(modalBackdrop).stream().noneMatch(WebElement::isDisplayed));
      WebUI.waitForPageLoaded();

      return this;
   }

   /**
    * Datatable của Perfex lọc bằng ajax: mỗi ký tự gõ vào ô search bắn một request
    * và mỗi response về sẽ vẽ lại toàn bộ tbody, xoá sạch node cũ.
    * Nếu chỉ chờ "bảng đã có chữ cần tìm" thì vẫn còn request của các ký tự cuối đang bay,
    * lần vẽ kế tiếp sẽ làm mọi element tìm được sau đó bị stale.
    * Vì vậy phải chờ đủ 3 mốc: bảng đã lọc xong, lớp phủ Processing đã tắt, và hết ajax đang treo.
    */
   public TasksPage searchTask(String keyword) {
      WebUI.setText(inputSearchTask, keyword);
      if (!keyword.isEmpty()) {
         //Dùng WebUI.retryUntil vì getText() cũng có thể dính stale khi tbody đang được vẽ lại
         WebUI.retryUntil(driver -> {
            String tableText = driver.findElement(tableTasksBody).getText();
            return tableText.contains(keyword) || tableText.contains("No matching records found");
         });
      }
      WebUI.waitForElementInVisible(tableTasksProcessing);
      WebUI.waitForJQueryLoad();

      return this;
   }

   public boolean isTasksTableDisplayed() {
      WebUI.waitForPageLoaded();
      return WebUI.checkElementExist(tableTasks, 10, 1000);
   }

   public boolean isTaskDisplayed(String taskName) {
      searchTask(taskName);
      return WebUI.retryUntil(driver -> driver.findElement(tableTasksBody).getText().contains(taskName));
   }

   public String getRelatedProjectOfTask(String taskName) {
      searchTask(taskName);
      By relatedProjectLink = By.xpath("//table[@id='tasks']//tbody/tr[contains(., " + xpathLiteral(taskName) + ")]//a[contains(@class,'task-table-related')]");
      //Tìm và lấy text trong cùng một vòng chờ, không tách ra hai bước,
      //vì element tìm được ở bước trước có thể đã bị thay mới khi sang bước sau
      return WebUI.retryUntil(driver -> {
         String text = driver.findElement(relatedProjectLink).getText().trim();
         return text.isEmpty() ? null : text;
      });
   }

   private void selectPickerByText(By buttonDropdown, String selectId, String visibleText) {
      WebUI.clickElement(buttonDropdown);

      By option = getSelectPickerOption(selectId, visibleText);
      WebUI.clickElement(option);

      WebUI.waitForAttributeToBe(buttonDropdown, "title", visibleText);
   }

   private By getSelectPickerOption(String selectId, String visibleText) {
      return By.xpath("//select[@id='" + selectId + "']/parent::div//ul[contains(@class,'dropdown-menu')]//span[@class='text' and normalize-space()=" + xpathLiteral(visibleText) + "]");
   }

   private By getSelectPickerOptionContains(String selectId, String partialText) {
      return By.xpath("//select[@id='" + selectId + "']/parent::div//ul[contains(@class,'dropdown-menu')]//span[@class='text' and contains(normalize-space()," + xpathLiteral(partialText) + ")]");
   }

}
