package com.anhtester.Bai28_DriverManager_Parallel.pages;

import com.anhtester.constants.ConfigData;
import com.anhtester.keywords.WebUI;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class CustomersPage extends BasePage {

   private String customersPageUrl = "/admin/clients";
   private String addNewCustomerPageUrl = "/admin/clients/client";

   private By headerCustomersSummary = By.xpath("//h4[normalize-space()='Customers Summary']");
   private By buttonNewCustomer = By.xpath("//a[contains(@href,'/admin/clients/client') and contains(normalize-space(),'New Customer')]");
   private By tableCustomers = By.xpath("//table[@id='clients' and contains(@class,'dataTable')]");
   private By tableCustomersBody = By.cssSelector("#clients tbody");
   private By inputSearchCustomer = By.cssSelector("#clients_filter input[type='search']");
   //Lớp phủ "Processing..." của Datatable, hiện lên trong lúc bảng đang chờ dữ liệu ajax về
   private By tableCustomersProcessing = By.cssSelector("#clients_processing");

   private By tabCustomerDetails = By.cssSelector("a[href='#contact_info']");
   private By tabBillingAndShipping = By.cssSelector("a[href='#billing_and_shipping']");

   private By checkboxShowPrimaryContact = By.id("show_primary_contact");
   private By labelShowPrimaryContact = By.cssSelector("label[for='show_primary_contact']");
   private By inputCompany = By.id("company");
   private By inputVatNumber = By.id("vat");
   private By inputPhone = By.id("phonenumber");
   private By inputWebsite = By.id("website");
   private By selectGroups = By.id("groups_in[]");
   private By buttonGroupsDropdown = By.cssSelector("button[data-id='groups_in[]']");
   private By selectDefaultCurrency = By.id("default_currency");
   private By buttonDefaultCurrencyDropdown = By.cssSelector("button[data-id='default_currency']");
   private By selectDefaultLanguage = By.id("default_language");
   private By buttonDefaultLanguageDropdown = By.cssSelector("button[data-id='default_language']");
   private By textareaAddress = By.id("address");
   private By inputCity = By.id("city");
   private By inputState = By.id("state");
   private By inputZipCode = By.id("zip");
   private By selectCountry = By.id("country");
   private By buttonCountryDropdown = By.cssSelector("button[data-id='country']");

   private By textareaBillingStreet = By.id("billing_street");
   private By inputBillingCity = By.id("billing_city");
   private By inputBillingState = By.id("billing_state");
   private By inputBillingZipCode = By.id("billing_zip");
   private By selectBillingCountry = By.id("billing_country");
   private By buttonBillingCountryDropdown = By.cssSelector("button[data-id='billing_country']");
   private By linkBillingSameAsCustomerInfo = By.cssSelector("a.billing-same-as-customer");

   private By textareaShippingStreet = By.id("shipping_street");
   private By inputShippingCity = By.id("shipping_city");
   private By inputShippingState = By.id("shipping_state");
   private By inputShippingZipCode = By.id("shipping_zip");
   private By selectShippingCountry = By.id("shipping_country");
   private By buttonShippingCountryDropdown = By.cssSelector("button[data-id='shipping_country']");
   private By linkCopyBillingAddress = By.cssSelector("a.customer-copy-billing-address");

   private By buttonSaveAndCreateContact = By.cssSelector("button.save-and-add-contact.customer-form-submiter");
   private By buttonSave = By.cssSelector("button.only-save.customer-form-submiter");

   //Khai báo trả về theo kiểu Fluent Page
   //Trả về chính class này, để thuận tiện quá trình gọi sử dụng tại class test
   public CustomersPage openCustomersPage() {
      WebUI.openURL(ConfigData.BASE_URL + customersPageUrl);
      WebUI.waitForPageLoaded();
      WebUI.waitForElementVisible(headerCustomersSummary);

      return this;
   }

   public CustomersPage verifyNavigateToCustomersPage() {
      WebUI.waitForPageLoaded();
      WebUI.waitForElementVisible(headerCustomersSummary);
      WebUI.waitForCurrentURLContains(customersPageUrl);

      return this;
   }

   public CustomersPage clickNewCustomerButton() {
      WebUI.clickElement(buttonNewCustomer);
      WebUI.waitForPageLoaded();
      WebUI.waitForElementVisible(inputCompany);

      return this;
   }

   public CustomersPage verifyNavigateToAddNewCustomerPage() {
      WebUI.waitForPageLoaded();
      WebUI.waitForElementVisible(inputCompany);
      WebUI.waitForCurrentURLContains(addNewCustomerPageUrl);

      return this;
   }

   /**
    * Datatable của Perfex lọc bằng ajax: mỗi ký tự gõ vào ô search bắn một request
    * và mỗi response về sẽ vẽ lại toàn bộ tbody, xoá sạch node cũ.
    * Nếu chỉ chờ "bảng đã có chữ cần tìm" thì vẫn còn request của các ký tự cuối đang bay,
    * lần vẽ kế tiếp sẽ làm mọi element tìm được sau đó bị stale.
    * Vì vậy phải chờ đủ 3 mốc: bảng đã lọc xong, lớp phủ Processing đã tắt, và hết ajax đang treo.
    */
   public CustomersPage searchCustomer(String keyword) {
      WebUI.setText(inputSearchCustomer, keyword);
      if (!keyword.isEmpty()) {
         //Dùng WebUI.retryUntil vì getText() cũng có thể dính stale khi tbody đang được vẽ lại
         WebUI.retryUntil(driver -> {
            String tableText = driver.findElement(tableCustomersBody).getText();
            return tableText.contains(keyword) || tableText.contains("No matching records found");
         });
      }
      WebUI.waitForElementInVisible(tableCustomersProcessing);
      WebUI.waitForJQueryLoad();

      return this;
   }

   public CustomersPage clickCustomerDetailsTab() {
      WebUI.clickElement(tabCustomerDetails);
      WebUI.waitForElementVisible(inputCompany);

      return this;
   }

   public CustomersPage clickBillingAndShippingTab() {
      WebUI.clickElement(tabBillingAndShipping);
      WebUI.waitForElementVisible(textareaBillingStreet);

      return this;
   }

   public CustomersPage setShowPrimaryContactOnDocuments(boolean isChecked) {
      clickCustomerDetailsTab();
      //Checkbox gốc bị plugin ẩn đi nên chỉ chờ present, và phải bấm vào label mới ăn
      WebUI.waitForElementPresent(checkboxShowPrimaryContact);
      if (WebUI.getWebElement(checkboxShowPrimaryContact).isSelected() != isChecked) {
         WebUI.clickElement(labelShowPrimaryContact);
      }

      return this;
   }

   public CustomersPage fillCustomerDetails(String company, String vatNumber, String phone, String website) {
      clickCustomerDetailsTab();
      WebUI.setText(inputCompany, company);
      WebUI.setText(inputVatNumber, vatNumber);
      WebUI.setText(inputPhone, phone);
      WebUI.setText(inputWebsite, website);

      return this;
   }

   public CustomersPage selectGroups(String groupName) {
      selectPickerByText(selectGroups, "groups_in[]", groupName);

      return this;
   }

   public CustomersPage selectDefaultCurrency(String currencyName) {
      selectPickerByText(selectDefaultCurrency, "default_currency", currencyName);

      return this;
   }

   public CustomersPage selectDefaultLanguage(String languageName) {
      selectPickerByText(selectDefaultLanguage, "default_language", languageName);

      return this;
   }

   public CustomersPage fillAddress(String address, String city, String state, String zipCode, String countryName) {
      clickCustomerDetailsTab();
      WebUI.setText(textareaAddress, address);
      WebUI.setText(inputCity, city);
      WebUI.setText(inputState, state);
      WebUI.setText(inputZipCode, zipCode);
      selectPickerByText(selectCountry, "country", countryName);

      return this;
   }

   public CustomersPage fillBillingAddress(String street, String city, String state, String zipCode, String countryName) {
      clickBillingAndShippingTab();
      WebUI.setText(textareaBillingStreet, street);
      WebUI.setText(inputBillingCity, city);
      WebUI.setText(inputBillingState, state);
      WebUI.setText(inputBillingZipCode, zipCode);
      selectPickerByText(selectBillingCountry, "billing_country", countryName);

      return this;
   }

   public CustomersPage fillShippingAddress(String street, String city, String state, String zipCode, String countryName) {
      clickBillingAndShippingTab();
      WebUI.setText(textareaShippingStreet, street);
      WebUI.setText(inputShippingCity, city);
      WebUI.setText(inputShippingState, state);
      WebUI.setText(inputShippingZipCode, zipCode);
      selectPickerByText(selectShippingCountry, "shipping_country", countryName);

      return this;
   }

   public CustomersPage clickBillingSameAsCustomerInfo() {
      clickBillingAndShippingTab();
      WebUI.clickElement(linkBillingSameAsCustomerInfo);

      return this;
   }

   public CustomersPage clickCopyBillingAddress() {
      clickBillingAndShippingTab();
      WebUI.clickElement(linkCopyBillingAddress);

      return this;
   }

   public CustomersPage clickSaveButton() {
      WebUI.clickElement(buttonSave);

      return this;
   }

   public CustomersPage clickSaveAndCreateContactButton() {
      WebUI.clickElement(buttonSaveAndCreateContact);

      return this;
   }

   public CustomersPage waitForCustomerProfilePage() {
      WebUI.waitForPageLoaded();
      WebUI.waitForCurrentURLMatches(".*/admin/clients/client/\\d+$");
      WebUI.waitForElementVisible(inputCompany);

      return this;
   }

   public boolean isCustomersTableDisplayed() {
      WebUI.waitForPageLoaded();
      return WebUI.checkElementExist(tableCustomers, 10, 1000);
   }

   public boolean isCustomerDisplayed(String companyName) {
      searchCustomer(companyName);
      return WebUI.retryUntil(driver -> driver.findElement(tableCustomersBody).getText().contains(companyName));
   }

   public boolean isCustomerNotDisplayed(String companyName) {
      searchCustomer(companyName);
      return WebUI.retryUntil(driver -> !driver.findElement(tableCustomersBody).getText().contains(companyName));
   }

   public CustomersPage deleteCustomerByCompanyName(String companyName) {
      searchCustomer(companyName);
      By deleteCustomerLink = getDeleteCustomerLink(companyName);
      String deleteUrl = WebUI.retryUntil(driver -> driver.findElement(deleteCustomerLink).getAttribute("href"));
      WebUI.openURL(deleteUrl);
      WebUI.waitForPageLoaded();
      WebUI.waitForElementVisible(headerCustomersSummary);
      searchCustomer(companyName);
      WebUI.retryUntil(driver -> !driver.findElement(tableCustomersBody).getText().contains(companyName));

      return this;
   }

   public CustomersPage deleteCustomerByHoverAndConfirmAlert(String companyName) {
      searchCustomer(companyName);

      By companyNameLink = getCompanyNameLink(companyName);
      By deleteCustomerLink = getDeleteCustomerLink(companyName);

      //Datatable có thể vẽ lại ngay sau khi lọc làm element cũ bị stale,
      //nên gom hover + bấm Delete vào một vòng chờ có thể thử lại.
      //Điểm mấu chốt: tìm lại element trong từng vòng, KHÔNG giữ sẵn WebElement từ bên ngoài,
      //vì WebElement chỉ là tham chiếu tới node cũ và không tự tìm lại khi node đó bị thay mới.
      WebUI.retryUntil(driver -> {
         WebElement companyNameElement = driver.findElement(companyNameLink);
         //Cuộn dòng vào giữa màn hình rồi mới hover, chuột không di tới element ngoài viewport được
         WebUI.scrollToElement(companyNameElement);
         new Actions(driver).moveToElement(companyNameElement).perform();

         //Hover có thể chưa kịp ăn, link Delete vẫn đang ẩn thì trả false để hover lại ở vòng sau
         WebElement deleteLink = driver.findElement(deleteCustomerLink);
         if (!deleteLink.isDisplayed()) {
            return false;
         }
         deleteLink.click();
         return true;
      });

      WebUI.acceptAlert();

      WebUI.retryUntil(driver -> !driver.findElement(tableCustomersBody).getText().contains(companyName));

      return this;
   }

   public String getPageTitle() {
      return WebUI.getPageTitle();
   }

   public String getCompanyValue() {
      return WebUI.getElementAttribute(inputCompany, "value");
   }

   public String getVatNumberValue() {
      return WebUI.getElementAttribute(inputVatNumber, "value");
   }

   public String getPhoneValue() {
      return WebUI.getElementAttribute(inputPhone, "value");
   }

   public String getWebsiteValue() {
      return WebUI.getElementAttribute(inputWebsite, "value");
   }

   public String getAddressValue() {
      return WebUI.getElementAttribute(textareaAddress, "value");
   }

   public String getCityValue() {
      return WebUI.getElementAttribute(inputCity, "value");
   }

   public String getStateValue() {
      return WebUI.getElementAttribute(inputState, "value");
   }

   public String getZipCodeValue() {
      return WebUI.getElementAttribute(inputZipCode, "value");
   }

   public String getSelectedGroupsValue() {
      return WebUI.getElementAttribute(buttonGroupsDropdown, "title");
   }

   public String getSelectedDefaultCurrencyValue() {
      return WebUI.getElementAttribute(buttonDefaultCurrencyDropdown, "title");
   }

   public String getSelectedDefaultLanguageValue() {
      return WebUI.getElementAttribute(buttonDefaultLanguageDropdown, "title");
   }

   public String getSelectedCountryValue() {
      return WebUI.getElementAttribute(buttonCountryDropdown, "title");
   }

   public String getSelectedBillingCountryValue() {
      return WebUI.getElementAttribute(buttonBillingCountryDropdown, "title");
   }

   public String getSelectedShippingCountryValue() {
      return WebUI.getElementAttribute(buttonShippingCountryDropdown, "title");
   }

   private By getDeleteCustomerLink(String companyName) {
      return By.xpath("//table[@id='clients']//tbody/tr[contains(., " + xpathLiteral(companyName) + ")]//a[contains(@href,'/admin/clients/delete/') and contains(@class,'_delete')]");
   }

   private By getCompanyNameLink(String companyName) {
      return By.xpath("//table[@id='clients']//tbody/tr[contains(., " + xpathLiteral(companyName) + ")]//td[contains(@class,'sorting_1')]/a[normalize-space()=" + xpathLiteral(companyName) + "]");
   }

   private void selectPickerByText(By selectLocator, String selectId, String visibleText) {
      WebUI.waitForElementPresent(selectLocator);
      String js =
              "var sel=document.getElementById(arguments[0]);" +
                      "if(!sel){return 'NO_SELECT';}" +
                      "var found=false;" +
                      "if(sel.multiple){" +
                      "  for(var i=0;i<sel.options.length;i++){" +
                      "    if(sel.options[i].text.trim()===arguments[1]){sel.options[i].selected=true;found=true;break;}" +
                      "  }" +
                      "}else{" +
                      "  for(var j=0;j<sel.options.length;j++){" +
                      "    if(sel.options[j].text.trim()===arguments[1]){sel.value=sel.options[j].value;found=true;break;}" +
                      "  }" +
                      "}" +
                      "if(window.jQuery){jQuery(sel).selectpicker('refresh');jQuery(sel).trigger('change');}" +
                      "else{sel.dispatchEvent(new Event('change'));}" +
                      "return found?'OK':'NO_OPTION';";
      Object result = WebUI.executeJS(js, selectId, visibleText);
      if (!"OK".equals(result)) {
         throw new RuntimeException("Cannot select value '" + visibleText + "' in selectpicker #" + selectId + ". Result: " + result);
      }
   }

}
