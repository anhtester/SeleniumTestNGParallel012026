# ⚡ SeleniumTestNGParallel012026

> Source code khóa học **Selenium Java 01/2026** — Anh Tester
> Phần **chạy test song song và cấu hình framework** (Bài 28 → 29), tách riêng từ repo chính [SeleniumMaven012026](https://github.com/anhtester/SeleniumMaven012026) (Bài 5 → 27).
> Sử dụng **Selenium WebDriver 4.47** + **Java 17** + **Maven** + **TestNG 7.12**.

---

## 📋 Mục lục

- [Repo này khác gì repo chính](#-repo-này-khác-gì-repo-chính)
- [Yêu cầu hệ thống](#-yêu-cầu-hệ-thống)
- [Cài đặt và chạy dự án](#-cài-đặt-và-chạy-dự-án)
- [Công nghệ sử dụng](#-công-nghệ-sử-dụng)
- [Cấu trúc dự án](#-cấu-trúc-dự-án)
- [Bài 28 — DriverManager với ThreadLocal](#-bài-28--drivermanager-với-threadlocal)
- [Bài 29 — Properties Config đa môi trường](#-bài-29--properties-config-đa-môi-trường)
- [Bộ keyword WebUI](#-bộ-keyword-webui)
- [Dữ liệu trung gian giữa các test case](#-dữ-liệu-trung-gian-giữa-các-test-case)
- [Cách chạy test](#-cách-chạy-test)
- [Giấy phép](#-giấy-phép)

---

## 🔀 Repo này khác gì repo chính

Toàn bộ kiến thức nền (Locators, WebElement, WebDriver, TestNG, POM, Page Factory, class `WebUI`...) nằm ở repo **SeleniumMaven012026**. Repo này bắt đầu từ phần **chạy song song (parallel)** — và để chạy song song được thì **cách quản lý driver phải đổi**:

| | Repo chính (Bài 24 → 27) | Repo này (Bài 28) |
| :--- | :--- | :--- |
| Nơi giữ driver | Biến `static WebDriver` trong `WebUI` | `ThreadLocal<WebDriver>` trong `DriverManager` |
| Cách nạp driver | `new WebUI(driver)` gọi trong `BasePage` | `DriverManager.setDriver(driver)` gọi trong `BaseTest` |
| Page class | `new LoginPage(driver)` — constructor nhận driver | `new LoginPage()` — **không cần** truyền driver |
| Chạy song song | ❌ Các luồng ghi đè driver của nhau | ✅ Mỗi luồng giữ driver riêng |

> **Vì sao biến `static` thường không chạy song song được:** biến `static` là **một ô nhớ dùng chung cho cả JVM**. Luồng thứ hai gọi `new WebUI(driver)` là ghi đè luôn driver của luồng thứ nhất — từ đó cả hai luồng cùng điều khiển một trình duyệt, test fail lung tung không theo quy luật nào. `ThreadLocal` giữ **mỗi luồng một ô nhớ riêng**, nên cách gọi vẫn gọn như biến static mà không đụng nhau.

---

## 💻 Yêu cầu hệ thống

| Thành phần       | Yêu cầu                                                     |
| ---------------- |-------------------------------------------------------------|
| **Java JDK**     | 17 hoặc cao hơn                                             |
| **Maven**        | 3.x                                                         |
| **Trình duyệt**  | Chrome, Firefox, Edge (suite mẫu dùng cả 3)                 |
| **IDE**          | IntelliJ IDEA, VS Code                      |
| **RAM**          | Nên có ≥ 16GB — chạy song song mở nhiều trình duyệt cùng lúc |

> **Lưu ý:** Selenium 4.x tự động quản lý WebDriver thông qua Selenium Manager — không cần tải `chromedriver` / `geckodriver` thủ công.

---

## 🚀 Cài đặt và chạy dự án

1. **Clone repository:**
   ```bash
   git clone https://github.com/anhtester/SeleniumTestNGParallel012026.git
   cd SeleniumTestNGParallel012026
   ```

2. **Mở dự án** trong IDE (IntelliJ IDEA khuyến nghị).

3. **Tải dependencies:**
   ```bash
   mvn clean install -DskipTests
   ```

4. **Chạy test** — `pom.xml` đang trỏ sẵn tới suite của **Bài 29**:
   ```bash
   mvn test
   ```
   Muốn chạy lại bộ test POM song song của Bài 28 thì đổi `<suiteXmlFile>` trong `pom.xml` sang `Suite_Bai28_DriverManager_ParallelExecution.xml`.

---

## 🛠 Công nghệ sử dụng

| Thư viện / Tool          | Phiên bản | Mục đích                                    |
| ------------------------ |-----------| -------------------------------------------- |
| **Selenium Java**        | 4.47.0    | Tự động hóa trình duyệt web                 |
| **TestNG**               | 7.12.0    | Framework quản lý test case + cơ chế parallel |
| **Gson**                 | 2.14.0    | Đọc/ghi file JSON trung gian chia sẻ test data |
| **SLF4J API**            | 2.0.18    | Logging API chuẩn                            |
| **SLF4J Simple**         | 2.0.18    | Implementation đơn giản cho SLF4J            |
| **Maven Surefire Plugin**| 3.5.6     | Plugin chạy test và tích hợp TestNG suite    |

---

## 📁 Cấu trúc dự án

```
SeleniumTestNGParallel012026/
├── pom.xml                          # Cấu hình Maven & dependencies
├── README.md
├── LICENSE
│
├── src/
│   ├── main/java/com/anhtester/
│   │   ├── Main.java                    # Entry point (demo)
│   │   ├── constants/
│   │   │   └── ConfigData.java          # Hằng số dùng chung (URL, tài khoản, tên file JSON test data)
│   │   ├── drivers/                     # 📌 Trọng tâm Bài 28
│   │   │   ├── DriverManager.java       # Giữ WebDriver theo ThreadLocal — mỗi luồng một driver riêng
│   │   │   └── ParameterManager.java    # Nguồn cấu hình duy nhất: -D > biến môi trường > properties > <parameter> XML
│   │   ├── helpers/                     # 📌 Trọng tâm Bài 29
│   │   │   ├── PropertiesHelper.java    # Load & đọc/ghi file .properties, chồng file môi trường lên file chung
│   │   │   └── SystemHelper.java        # Lấy đường dẫn thư mục gốc dự án (user.dir)
│   │   ├── keywords/
│   │   │   ├── WebUI.java               # Bộ keyword Web dùng chung — lấy driver từ DriverManager
│   │   │   ├── ActionKeyword.java       # Lớp keyword đời đầu (giữ lại từ các bài trước, không còn dùng)
│   │   │   ├── MobileUI.java            # (placeholder) Keyword cho Mobile Automation — Appium
│   │   │   └── APIKeyword.java          # (placeholder) Keyword cho API Automation — REST Assured
│   │   └── utils/
│   │       ├── JsonUtils.java           # Đọc/ghi test data ra file JSON trung gian (Gson)
│   │       ├── CaptureUtils.java        # Chụp màn hình bằng Robot class (từ Bài 12)
│   │       ├── ColorUtils.java          # Lấy mã màu HEX của pixel trên màn hình (từ Bài 12)
│   │       └── LocalStorageUtils.java   # Đọc/ghi Local Storage qua JavascriptExecutor (từ Bài 14)
│   │
│   └── test/
│       ├── java/com/anhtester/
│       │   ├── common/
│       │   │   └── BaseTest.java        # @BeforeSuite load config → tạo driver theo browser/headless, quit sau mỗi test
│       │   ├── locators/
│       │   │   └── LocatorsCRM.java     # Kho locator dùng chung (giữ lại từ bài CRM)
│       │   │
│       │   ├── Bai28_DriverManager_Parallel/   # 📌 Bài 28: POM chạy song song
│       │       ├── pages/                      # Page class KHÔNG nhận driver ở constructor
│       │       │   ├── BasePage.java           # Menu điều hướng + helper xpathLiteral
│       │       │   ├── LoginPage.java
│       │       │   ├── DashboardPage.java
│       │       │   ├── CustomersPage.java
│       │       │   ├── ProjectsPage.java
│       │       │   └── TasksPage.java
│       │       └── testcases/
│       │           ├── LoginTest.java          # 9 TC: 8 TC Login + 1 TC mẫu viết theo AAA
│       │           ├── DashboardTest.java      # 4 TC thống kê Dashboard
│       │           ├── CustomersTest.java      # 3 TC: thêm mới + 2 cách xóa Customer
│       │           ├── ProjectsTest.java       # 2 TC: thêm mới + xóa Project
│       │           └── TasksTest.java          # 1 TC: thêm Task gắn với Project
│       │   │
│       │   └── Bai29_PropertiesConfig/          # 📌 Bài 29: cấu hình bằng file .properties
│       │       └── DemoPropertiesConfig.java    # Demo load config chung + config theo môi trường
│       │
│       └── resources/
│           ├── configs/                 # 📌 Bài 29: file cấu hình
│           │   ├── config.properties    # Cấu hình chung: env, browser, headless, window size, timeout...
│           │   ├── dev.properties       # Key riêng của môi trường dev (url, base.uri)
│           │   └── staging.properties   # Key riêng của môi trường staging (url, base.uri)
│           │
│           ├── suites/                  # TestNG Suite XML
│           │   ├── Suite_Bai28_DriverManager_ParallelExecution.xml   # Chạy POM song song trên 2 trình duyệt
│           │   └── Suite_Bai29_PropertiesConfig.xml                  # Demo đọc config (suite mặc định trong pom.xml)
│           │
│           └── testdata/                # File JSON trung gian (tự sinh khi chạy test)
│               ├── customer_data.json
│               └── project_data.json
│
├── exports/screenshots/             # Ảnh chụp màn hình do WebUI.takeScreenshot() sinh ra
└── target/                          # Thư mục output (auto-generated)
```

---

## 📖 Bài 28 — DriverManager với ThreadLocal

> Áp dụng parallel vào bộ test POM thật (Perfex CRM): mỗi luồng một trình duyệt riêng, chạy đồng thời **Firefox và Edge**.

**Ba class trọng tâm**

| File | Nội dung |
| :--- | :--- |
| `drivers/DriverManager.java` | Giữ `WebDriver` trong `ThreadLocal` — `getDriver()`, `setDriver()`, `quit()`. Constructor để `private` vì đây là class tiện ích thuần static. |
| `drivers/ParameterManager.java` | Đọc `<parameter>` của TestNG **theo đúng luồng đang chạy** qua `Reporter.getCurrentTestResult()`. Sang Bài 29 được mở rộng thêm `getConfigValue()` — xem [phần dưới](#-bài-29--properties-config-đa-môi-trường). |
| `common/BaseTest.java` | `@BeforeMethod` + `@Parameters({"browser"})` → khởi tạo đúng loại driver (Chrome / Firefox / Edge), nạp vào `DriverManager`, maximize và set `pageLoadTimeout`. `@AfterMethod` gọi `DriverManager.quit()`. |

```java
public class DriverManager {

   private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

   private DriverManager() {
   }

   public static WebDriver getDriver() {
      return driver.get();
   }

   public static void setDriver(WebDriver driver) {
      DriverManager.driver.set(driver);
   }

   public static void quit() {
      DriverManager.driver.get().quit();
      driver.remove();   //Bắt buộc: trả ô nhớ về cho luồng, tránh rò rỉ bộ nhớ
   }
}
```

**Suite chạy song song đa trình duyệt** — `Suite_Bai28_DriverManager_ParallelExecution.xml`:

```xml
<suite name="Suite Demo DriverManager Parallel" parallel="tests">
   <test name="Demo Parallel Testing By Methods 1" parallel="methods" thread-count="5">
      <parameter name="browser" value="firefox"/>
      ...
   </test>
   <test name="Demo Parallel Testing By Methods 2" parallel="methods" thread-count="5">
      <parameter name="browser" value="edge"/>
      ...
   </test>
</suite>
```

Hai thẻ `<test>` chạy song song với nhau (mỗi thẻ một trình duyệt), bên trong mỗi thẻ lại chạy song song tối đa 5 method — tổng cộng có thể mở tới **10 trình duyệt cùng lúc**.

**Kiến thức chính:**

- **`ThreadLocal` = mỗi luồng một bản sao riêng của cùng một biến.** Code gọi vẫn ngắn gọn như biến static (`WebUI.clickElement(by)` không cần truyền driver) nhưng các luồng hoàn toàn không thấy driver của nhau.

- **Bắt buộc gọi `driver.remove()` sau khi `quit()`.** TestNG (và các thread pool nói chung) **tái sử dụng luồng** cho test tiếp theo. Không `remove()` thì test sau nhận lại driver cũ đã chết → `SessionNotCreatedException` hoặc `NoSuchSessionException`, và các driver cũ vẫn bị giữ tham chiếu gây rò rỉ bộ nhớ.

- **Tạo driver trong `@BeforeMethod`, không phải `@BeforeClass`.** Với `parallel="methods"`, mỗi method chạy trên luồng riêng — driver tạo ở `@BeforeClass` chỉ nằm trên luồng của class, các method khác gọi `getDriver()` sẽ nhận `null`.

- **`@Parameters` chỉ đọc được tham số bên trong `@Before*` / `@Test`.** Muốn đọc tham số ở nơi khác (page class, keyword) thì dùng `ParameterManager.getParameter()`:

  ```java
  //LoginPage — validation message HTML5 khác nhau giữa các trình duyệt
  if (ParameterManager.getBrowser().equalsIgnoreCase("firefox")) {
     Assert.assertEquals(WebUI.getElementAttribute(inputEmail, "validationMessage"),
             "Vui lòng điền một địa chỉ email.", "...");
  } else {
     Assert.assertEquals(WebUI.getElementAttribute(inputEmail, "validationMessage"),
             "Please enter a part following '@'. 'admin@' is incomplete.", "...");
  }
  ```

  > **Vì sao `Reporter.getCurrentTestResult()` an toàn khi chạy song song:** bản thân nó cũng lưu theo `ThreadLocal`, nên mỗi luồng đọc đúng `<test>` mà mình thuộc về. Hàm có kiểm tra `null` để vẫn chạy được khi gọi ngoài luồng TestNG (chạy trực tiếp từ IDE, không qua suite XML).

- **Page class bỏ hẳn constructor nhận driver.** So với Bài 24 & 25:

  ```java
  //Bài 24 & 25 — phải truyền driver qua từng lớp
  DashboardPage dashboardPage = new DashboardPage(driver);

  //Bài 28 — driver đã nằm trong DriverManager theo luồng
  DashboardPage dashboardPage = new DashboardPage();
  ```

  `BasePage` cũng không còn đoạn `new WebUI(driver)` — mọi keyword tự lấy driver của luồng hiện tại.

- **Validation message HTML5 phụ thuộc trình duyệt và ngôn ngữ hệ điều hành.** Chạy đa trình duyệt là lộ ngay điểm này — assert cứng một chuỗi là fail ở trình duyệt còn lại. Đây là lý do thật sự cần `ParameterManager` ở tầng page.

---

## 📖 Bài 29 — Properties Config đa môi trường

> Đưa mọi thứ hay đổi (browser, headless, url, timeout...) ra khỏi code Java, gom vào file `.properties` — đổi cấu hình không cần sửa code, không cần build lại.

**Ba class trọng tâm**

| File | Nội dung |
| :--- | :--- |
| `helpers/PropertiesHelper.java` | Load file config, đọc `getValue()`, ghi `setValue()`. Load **file chung trước, file môi trường sau** để đè key trùng. |
| `helpers/SystemHelper.java` | `getCurrentDir()` — lấy thư mục gốc dự án qua `user.dir` để ghép đường dẫn tuyệt đối. |
| `drivers/ParameterManager.java` | Được mở rộng thêm `getConfigValue()` — **nguồn cấu hình duy nhất** cho cả framework, gộp 4 nguồn theo thứ tự ưu tiên. |

### Ba file cấu hình

```
src/test/resources/configs/
├── config.properties     # Cấu hình chung — luôn được load trước
├── dev.properties        # Chỉ khai báo key KHÁC với file chung
└── staging.properties
```

```properties
# config.properties
env = staging          # Chọn môi trường. Để trống = chỉ dùng mỗi file này
browser = firefox
headless = true
window_size_x = 1920
window_size_y = 1080
explicit_wait_timeout = 10
```

```properties
# staging.properties — load sau nên đè lên file chung
url = https://staging.anhtester.com
base.uri = https://staging.anhtester.com/api/v1
```

> **Vì sao tách hai tầng file:** file chung giữ những gì mọi môi trường đều giống nhau, file môi trường **chỉ khai báo phần khác biệt**. Copy nguyên bộ key sang từng file môi trường là sớm muộn cũng lệch nhau — sửa `explicit_wait_timeout` ở `config.properties` mà quên sửa ở `dev.properties`, test chạy dev lại dùng giá trị cũ.

### Thứ tự ưu tiên khi lấy một giá trị cấu hình

`ParameterManager.getConfigValue(name, defaultValue)` — trên đè dưới:

| # | Nguồn | Ví dụ |
| :-- | :--- | :--- |
| 1 | System property (Maven / JVM) | `mvn test -Dbrowser=firefox` |
| 2 | Biến môi trường (`name` rồi `NAME`) | `BROWSER=firefox` — dùng cho CI/CD |
| 3 | File properties | `browser = firefox` trong `config.properties` |
| 4 | `<parameter>` trong suite XML của luồng đang chạy | `<parameter name="browser" value="edge"/>` |
| 5 | `defaultValue` truyền vào | `"chrome"` |

```java
public static String getBrowser() {
   return getConfigValue("browser", "chrome");
}
```

> **Vì sao dòng lệnh phải đứng trên file:** file `.properties` là cấu hình mặc định của dự án, được commit lên git. Chạy CI hay muốn thử nhanh một trình duyệt khác thì truyền `-Dbrowser=...` là đè được ngay mà **không phải sửa file rồi lỡ tay commit lên**.

### Chọn môi trường (`env`)

Riêng `env` **không** đi qua `getConfigValue()` — nó là thứ quyết định file nào được load, nên phải chốt xong **trước** lúc load. `PropertiesHelper.resolveEnv()` tìm theo thứ tự: `-Denv` → biến môi trường `env` / `ENV` → key `env` trong `config.properties`. Không khai báo ở đâu cả thì chỉ load mỗi `config.properties`.

```bash
mvn test "-Denv=dev"
```

Gõ sai tên môi trường (`-Denv=devv`) thì `loadFiles()` **ném lỗi ngay**, chứ không âm thầm trả về bộ config rỗng — vì rỗng thì test sẽ fail ở một chỗ hoàn toàn khác, rất khó lần ra nguyên nhân.

### Nạp config ở đâu

`BaseTest` gọi một lần duy nhất ở `@BeforeSuite`, trước khi bất kỳ driver nào được tạo:

```java
@BeforeSuite(alwaysRun = true)
public void loadConfigFiles() {
   PropertiesHelper.loadAllFiles();
}
```

Sau đó `@BeforeMethod` đã có sẵn config để dựng driver:

```java
browserName = ParameterManager.getConfigValue("browser", browserName);
boolean headless = Boolean.parseBoolean(ParameterManager.getHeadlessMode());
```

`@Optional("chrome")` của XML được truyền vào làm **giá trị cuối cùng** thay vì để cứng chuỗi `"chrome"` — nhờ vậy chạy suite Bài 28 (mỗi `<test>` một trình duyệt) vẫn đúng, mà chạy lẻ từ IDE cũng vẫn có mặc định.

**Kiến thức chính:**

- **`loadAllFiles()` là trạng thái static dùng chung cho cả JVM** — chỉ gọi ở `@BeforeSuite`. Gọi giữa lúc test đang chạy song song là **đổi config của mọi luồng**, không riêng luồng gọi. Cần đọc môi trường khác mà không ảnh hưởng ai thì dùng `loadFiles(env)` — hàm này trả về `Properties` độc lập, không đụng biến static nào.

- **`FileInputStream` phải để biến cục bộ, không để field static.** Đây là lỗi kinh điển khi chạy song song: luồng này đóng mất stream của luồng kia giữa chừng → `IOException: Stream Closed`. Trong `PropertiesHelper` mọi stream đều nằm trong `try-with-resources` cục bộ.

- **Gán vào biến static chỉ sau khi load xong hoàn toàn:**

  ```java
  Properties loaded = loadFiles(targetEnv);   //load vào biến cục bộ trước
  currentEnv = targetEnv;
  properties = loaded;                        //gán một lần, khi đã đủ dữ liệu
  ```

  Nếu gán `properties = new Properties()` rồi mới `load()` dần vào, luồng khác đọc đúng lúc đó sẽ nhận bộ config **đang dở dang** — thiếu key, `getValue()` trả `null`.

- **`setValue()` phải mở lại đúng file đích để ghi**, không ghi thẳng biến `properties` trong bộ nhớ. Vì `properties` là bộ **đã gộp** nhiều file — `store()` nó xuống `staging.properties` là đổ hết key của file chung sang file môi trường.

- **`getValue(key, defaultValue)`** trả về `defaultValue` khi key không tồn tại **hoặc để trống giá trị** (`report_path =`), tránh phải kiểm tra `null` rải rác khắp nơi.

- **Ở chế độ headless, `maximize()` không có tác dụng thật** — cửa sổ co về 800x600, viewport bé làm web chuyển sang layout mobile và ẩn mất sidebar. Phải set `--window-size` ngay lúc khởi tạo options, và bỏ qua `maximize()` khi headless. Firefox lại dùng cú pháp khác: một gạch `-headless`, và `--width` / `--height` rời nhau.

---

## 🧰 Bộ keyword WebUI

`WebUI` giữ nguyên toàn bộ **122 hàm** đã xây dựng từ Bài 24 → 26, chỉ thay nguồn lấy driver: từ biến `static` sang `DriverManager.getDriver()`.

| Nhóm | Hàm tiêu biểu |
| :--- | :--- |
| **Retry chống stale** | `retryWait`, `retryUntil` |
| **Wait** | `waitForElementVisible/InVisible/Present/Clickable`, `waitForPageLoaded`, `waitForJQueryLoad`, `waitForAngularLoad`, `waitForCurrentURLContains/Matches`, `waitForTextToBePresent`, `waitForNumberOfElements`, `waitForStalenessOf`, `waitForAttributeToBe/Contains`, `waitForValueToBe`, `waitForTitleContains` |
| **Hành động cơ bản** | `openURL`, `clickElement`, `setText`, `setTextAndKey`, `getCurrentURL` |
| **Lấy dữ liệu** | `getElementText`, `getElementAttribute`, `getElementValue`, `getElementDomAttribute`, `getElementCssValue`, `getAllElementsText`, `getElementCount` |
| **Search ajax** | `searchSelectPickerOption` — gõ lại từ khóa nhiều lần khi danh sách chưa nạp về kịp |
| **Dropdown `<select>`** | `selectOptionByText/Value/Index`, `getSelectedOptionText`, `getAllOptionsText` |
| **Checkbox / Radio** | `checkCheckbox`, `uncheckCheckbox`, `setCheckboxState`, `selectRadioButton` |
| **Kiểm tra trạng thái** | `isElementVisible`, `isElementClickable`, `isElementEnabled`, `isElementSelected`, `checkElementExist` |
| **Scroll & chuột** | `scrollToElement(AtTop/AtBottom/AtCenter)`, `scrollToTopPage`, `scrollToBottomPage`, `scrollToPosition`, `moveToElement`, `hoverElement`, `mouseHover`, `dragAndDrop`, `dragAndDropOffset` |
| **Bàn phím** | `pressENTER`, `pressESC`, `pressF11` |
| **Alert** | `acceptAlert`, `dismissAlert`, `getTextOnAlert`, `setTextOnAlert`, `waitForAlertIsPresent` |
| **Javascript fallback** | `executeJS`, `clickElementByJS`, `setTextByJS`, `highLightElement` |
| **Frame** | `switchToFrame` (By / index / name), `switchToDefaultContent` |
| **Cửa sổ & tab** | `getCurrentWindowHandle`, `getAllWindowHandles`, `switchToWindowByIndex/ByTitle`, `openNewTab`, `closeCurrentTab` |
| **Điều khiển browser** | `refreshPage`, `navigateBack`, `navigateForward`, `getPageTitle`, `setWindowSize`, `setWindowSizeDesktop`, `maximizeWindow` |
| **Chụp màn hình** | `takeScreenshot`, `takeElementScreenshot` — lưu vào `exports/screenshots/` kèm timestamp |
| **Upload file** | `uploadFile`, `uploadFileToHiddenInput` |
| **Cookie** | `addCookie`, `getAllCookies`, `getCookieValue`, `deleteCookie`, `deleteAllCookies` |
| **Assert / Verify** | `verifyEquals`, `verifyContains` (trả `true/false`) — `assertEquals`, `assertContains` (fail test) |
| **Tiện ích** | `sleep`, `smartWait`, `logConsole` |

> Giải thích chi tiết từng nhóm (vì sao cần `retryUntil`, bẫy `getAttribute("value")`, `setTextByJS` phải bắn event...) nằm ở phần Bài 24 & 25 của repo chính.

---

## 🔗 Dữ liệu trung gian giữa các test case

Test data được truyền giữa các module qua file JSON trong `src/test/resources/testdata/` (đọc/ghi bằng `JsonUtils`):

```
CustomersTest.testAddNewCustomer    →  customer_data.json  { "customerName": "..." }
                                               ↓
ProjectsTest.testAddNewProject      →  project_data.json   { "projectName": "..." }
                                               ↓
TasksTest.testAddNewTaskWithProject  →  Task được gắn vào đúng Project vừa tạo
```

```java
// Ghi lại sau khi tạo thành công
JsonUtils.setDataToJsonFile(ConfigData.CUSTOMER_DATA_FILE, ConfigData.KEY_CUSTOMER_NAME, companyName);

// Đọc ra ở test case sau
String customerName = JsonUtils.getValueFromJsonFile(ConfigData.CUSTOMER_DATA_FILE, ConfigData.KEY_CUSTOMER_NAME);
```

> ⚠️ **Đây chính là điểm xung đột kinh điển khi chạy song song.** File JSON là tài nguyên **dùng chung cho cả máy**, không tách theo luồng như driver. Trong `Suite_Bai28_DriverManager_ParallelExecution.xml`, cả hai `<test>` (Firefox và Edge) đều chạy `CustomersTest` → cùng ghi vào `customer_data.json`, và `ProjectsTest` của luồng này có thể đọc trúng Customer do luồng kia vừa tạo.
>
> Hệ quả: khi chạy song song thì **thứ tự Customers → Projects → Tasks không còn được đảm bảo**, `ProjectsTest` có thể chạy trước `CustomersTest` và đọc phải dữ liệu cũ. Đây là hạn chế **cố ý để lại** làm ví dụ — hướng xử lý là tách tên file JSON theo luồng/trình duyệt, hoặc gom cả 3 bước vào **một test case** duy nhất thay vì chia sẻ dữ liệu qua file.

---

## ▶ Cách chạy test

### Chạy từ IDE
- Mở file test → Click chuột phải → **Run** (chạy đơn lẻ, trình duyệt lấy theo `browser` trong `config.properties`)
- Mở file `.xml` trong `src/test/resources/suites/` → Click chuột phải → **Run As TestNG Suite** (chạy song song đúng cấu hình)

### Chạy bằng Maven

```bash
# Chạy suite mặc định đã khai báo trong pom.xml
# = demo đọc Properties Config của Bài 29
mvn test
```

```bash
# Đổi môi trường: load config.properties + dev.properties
mvn test "-Denv=dev"
```

```bash
# Đè cấu hình từ dòng lệnh, không cần sửa file properties
mvn test "-Dbrowser=chrome" "-Dheadless=false"
```

```bash
# Chạy một class cụ thể (tuần tự, Chrome mặc định)
mvn test "-Dtest=CustomersTest"
```

```bash
# Chạy một test case cụ thể trong class
mvn test "-Dtest=ProjectsTest#testAddNewProject"
```

```bash
# Clean và chạy lại từ đầu
mvn clean test
```

> **Windows / PowerShell:** nên bọc tham số `-D...` trong dấu ngoặc kép như các ví dụ trên để tránh lỗi parse tham số.
>
> **Lưu ý:** chạy `-Dtest=...` là bỏ qua suite XML, nên **không có `<parameter>` `browser`** và test chạy tuần tự. Trình duyệt lúc này lấy từ `config.properties` (hoặc `-Dbrowser=...` nếu có truyền), chỉ khi cả hai đều trống mới rơi về mặc định `chrome`.

### Kết quả test
- Log tóm tắt: `target/surefire-reports/*.txt`
- Báo cáo HTML của TestNG: `target/surefire-reports/index.html`
- Ảnh chụp màn hình: `exports/screenshots/`

---

## 📄 Giấy phép

Dự án này được phân phối dưới giấy phép **MIT**. Xem file [LICENSE](LICENSE) để biết thêm chi tiết.

---

> **Tác giả:** [Anh Tester](https://anhtester.com) — Khóa học Selenium Java 01/2026
