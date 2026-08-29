# ⚡ SeleniumTestNGParallel012026

> Source code khóa học **Selenium Java 01/2026** — Anh Tester
> Phần **chạy test song song và cấu hình framework** (Bài 28 → 33), tách riêng từ repo chính [SeleniumMaven012026](https://github.com/anhtester/SeleniumMaven012026) (Bài 5 → 27).
> Sử dụng **Selenium WebDriver 4.48** + **Java 17** + **Maven** + **TestNG 7.12**.

---

## 📋 Mục lục

- [Repo này khác gì repo chính](#-repo-này-khác-gì-repo-chính)
- [Yêu cầu hệ thống](#-yêu-cầu-hệ-thống)
- [Cài đặt và chạy dự án](#-cài-đặt-và-chạy-dự-án)
- [Công nghệ sử dụng](#-công-nghệ-sử-dụng)
- [Cấu trúc dự án](#-cấu-trúc-dự-án)
- [Bài 28 — DriverManager với ThreadLocal](#-bài-28--drivermanager-với-threadlocal)
- [Bài 29 — Properties Config đa môi trường](#-bài-29--properties-config-đa-môi-trường)
- [Bài 30 — Excel Data cho test case](#-bài-30--excel-data-cho-test-case)
- [Bài 31 — DataProvider](#-bài-31--dataprovider)
- [Bài 32 — Screenshot và Record Video](#-bài-32--screenshot-và-record-video)
- [Bài 33 — TestListener](#-bài-33--testlistener)
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
   Muốn chạy bài khác thì đổi `<suiteXmlFile>` trong `pom.xml`, hoặc truyền thẳng từ dòng lệnh:

   | Bài | File suite |
   | :-- | :--- |
   | 28 | `Suite_Bai28_DriverManager_ParallelExecution.xml` |
   | 29 | `Suite_Bai29_PropertiesConfig.xml` *(mặc định)* |
   | 30 | `Suite_Bai30_Excel_Data.xml` |
   | 31 | `Suite_Bai31_DataProvider.xml` |
   | 32 | `Suite_Bai32_Screenshot_VideoRecord.xml` |
   | 33 | `Suite_Bai33_TestListener.xml` |

   ```bash
   mvn test "-Dsurefire.suiteXmlFiles=src/test/resources/suites/Suite_Bai30_Excel_Data.xml"
   ```

---

## 🛠 Công nghệ sử dụng

| Thư viện / Tool          | Phiên bản | Mục đích                                    |
| ------------------------ |-----------| -------------------------------------------- |
| **Selenium Java**        | 4.48.0    | Tự động hóa trình duyệt web                 |
| **TestNG**               | 7.12.0    | Framework quản lý test case + cơ chế parallel |
| **Gson**                 | 2.14.0    | Đọc/ghi file JSON trung gian chia sẻ test data |
| **Apache POI**           | 5.5.1     | Đọc/ghi file Excel — lấy data cho test case  |
| **Apache POI OOXML**     | 5.5.1     | Hỗ trợ định dạng `.xlsx` + tô màu cell (`XSSF`) |
| **Commons IO**           | 2.22.0    | Tiện ích thao tác file, đi kèm khi dùng POI  |
| **Monte Screen Recorder**| 0.7.7.0   | Quay video màn hình lúc chạy test (`.avi`)   |
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
│   │   │   └── ConfigData.java          # Hằng số dùng chung (URL, tài khoản, JSON, Excel, đường dẫn ảnh/video + công tắc bật/tắt)
│   │   ├── drivers/                     # 📌 Trọng tâm Bài 28
│   │   │   ├── DriverManager.java       # Giữ WebDriver theo ThreadLocal — mỗi luồng một driver riêng
│   │   │   └── ParameterManager.java    # Nguồn cấu hình duy nhất: -D > biến môi trường > properties > <parameter> XML
│   │   ├── helpers/                     # 📌 Trọng tâm Bài 29, 30, 31 & 32
│   │   │   ├── PropertiesHelper.java    # Load & đọc/ghi file .properties, chồng file môi trường lên file chung
│   │   │   ├── ExcelHelper.java         # 📌 Bài 30 & 31: đọc/ghi Excel theo TÊN CỘT, tô màu cell, trả data cho DataProvider
│   │   │   ├── CaptureHelper.java       # 📌 Bài 32: chụp màn hình (TakesScreenshot) + quay video (Monte Screen Recorder)
│   │   │   └── SystemHelper.java        # Lấy đường dẫn thư mục gốc dự án (user.dir)
│   │   ├── keywords/
│   │   │   ├── WebUI.java               # Bộ keyword Web dùng chung — lấy driver từ DriverManager, chụp ảnh theo SCREENSHOT_ALL_STEPS
│   │   │   ├── ActionKeyword.java       # Lớp keyword đời đầu (giữ lại từ các bài trước, không còn dùng)
│   │   │   ├── MobileUI.java            # (placeholder) Keyword cho Mobile Automation — Appium
│   │   │   └── APIKeyword.java          # (placeholder) Keyword cho API Automation — REST Assured
│   │   └── utils/
│   │       ├── JsonUtils.java           # Đọc/ghi test data ra file JSON trung gian (Gson)
│   │       ├── ColorUtils.java          # Lấy mã màu HEX của pixel trên màn hình (từ Bài 12)
│   │       └── LocalStorageUtils.java   # Đọc/ghi Local Storage qua JavascriptExecutor (từ Bài 14)
│   │
│   └── test/
│       ├── java/com/anhtester/
│       │   ├── common/
│       │   │   └── BaseTest.java        # @Listeners gắn TestListener (Bài 33) + tạo driver theo browser/headless, tắt popup trình duyệt
│       │   ├── locators/
│       │   │   └── LocatorsCRM.java     # Kho locator dùng chung (giữ lại từ bài CRM)
│       │   ├── listeners/                    # 📌 Bài 33: nghe sự kiện của TestNG
│       │   │   └── TestListener.java         # Tự chụp ảnh khi pass/fail, tự quay video theo công tắc config
│       │   ├── dataproviders/                # 📌 Bài 31: nơi tập trung mọi @DataProvider
│       │   │   └── DataProviderFactory.java  # Data cứng, data Excel, data lọc theo tên test case
│       │   │
│       │   ├── Bai28_DriverManager_Parallel/  # 📌 Bài 28: POM chạy song song
│       │   │   ├── pages/                     # Page class KHÔNG nhận driver ở constructor
│       │   │   │   ├── BasePage.java          # Menu điều hướng + helper xpathLiteral
│       │   │   │   ├── LoginPage.java
│       │   │   │   ├── DashboardPage.java
│       │   │   │   ├── CustomersPage.java
│       │   │   │   ├── ProjectsPage.java
│       │   │   │   └── TasksPage.java
│       │   │   └── testcases/
│       │   │       ├── LoginTest.java         # 9 TC: 8 TC Login + 1 TC mẫu viết theo AAA
│       │   │       ├── DashboardTest.java     # 4 TC thống kê Dashboard
│       │   │       ├── CustomersTest.java     # 3 TC: thêm mới + 2 cách xóa Customer
│       │   │       ├── ProjectsTest.java      # 2 TC: thêm mới + xóa Project
│       │   │       └── TasksTest.java         # 1 TC: thêm Task gắn với Project
│       │   │
│       │   ├── Bai29_PropertiesConfig/        # 📌 Bài 29: cấu hình bằng file .properties
│       │   │   └── DemoPropertiesConfig.java  # Demo load config chung + config theo môi trường
│       │   │
│       │   ├── Bai30_Excel_Data/              # 📌 Bài 30: lấy data test từ file Excel
│       │   │   ├── DemoExcelData.java         # Demo đọc cell theo tên cột + ghi STATUS có tô màu
│       │   │   ├── pages/                     # Copy nguyên từ Bài 28, KHÔNG sửa gì
│       │   │   │   ├── BasePage.java
│       │   │   │   ├── LoginPage.java
│       │   │   │   ├── DashboardPage.java
│       │   │   │   ├── CustomersPage.java
│       │   │   │   ├── ProjectsPage.java
│       │   │   │   └── TasksPage.java
│       │   │   └── testcases/
│       │   │       ├── LoginTest.java         # 8 TC Login — data lấy từ Excel thay vì hardcode
│       │   │       ├── DashboardTest.java     # 4 TC — giữ nguyên như Bài 28
│       │   │       ├── CustomersTest.java     # 3 TC — giữ nguyên như Bài 28
│       │   │       ├── ProjectsTest.java      # 2 TC — giữ nguyên như Bài 28
│       │   │       └── TasksTest.java         # 1 TC — giữ nguyên như Bài 28
│       │   │
│       │   ├── Bai31_DataProvider/            # 📌 Bài 31: TestNG bơm data vào test case
│       │   │   ├── DemoDataProvider.java      # Demo cơ bản: data chuỗi, data số, DataProvider song song
│       │   │   ├── DemoDataProviderExcel.java # 4 kiểu lấy data Excel: cả sheet / khoảng dòng / dòng rời rạc / Hashtable
│       │   │   ├── DemoDataProviderPOM.java   # Gắn DataProvider vào POM — 6 dòng data = 6 lần login
│       │   │   ├── pages/                     # Copy nguyên từ Bài 28, KHÔNG sửa gì
│       │   │   │   ├── BasePage.java
│       │   │   │   ├── LoginPage.java
│       │   │   │   ├── DashboardPage.java
│       │   │   │   ├── CustomersPage.java
│       │   │   │   ├── ProjectsPage.java
│       │   │   │   └── TasksPage.java
│       │   │   └── testcases/
│       │   │       ├── LoginTest.java         # 8 TC Login — bỏ hẳn code đọc Excel, data do DataProvider bơm vào
│       │   │       ├── DashboardTest.java     # 4 TC — giữ nguyên như Bài 28
│       │   │       ├── CustomersTest.java     # 3 TC — giữ nguyên như Bài 28
│       │   │       ├── ProjectsTest.java      # 2 TC — giữ nguyên như Bài 28
│       │   │       └── TasksTest.java         # 1 TC — giữ nguyên như Bài 28
│       │   │
│       │   ├── Bai32_Screenshot_VideoRecord/  # 📌 Bài 32: chụp màn hình & quay video
│       │   │   ├── DemoScreenshot.java        # 3 cách chụp: hardcode tên / theo tên method / gọi CaptureHelper + @AfterMethod chụp khi PASS
│       │   │   ├── DemoVideoRecord.java       # Class trống — chỗ để tự thực hành quay video
│       │   │   ├── pages/                     # Copy nguyên từ Bài 31, KHÔNG sửa gì
│       │   │   │   ├── BasePage.java
│       │   │   │   ├── LoginPage.java
│       │   │   │   ├── DashboardPage.java
│       │   │   │   ├── CustomersPage.java
│       │   │   │   ├── ProjectsPage.java
│       │   │   │   └── TasksPage.java
│       │   │   └── testcases/
│       │   │       ├── LoginTest.java         # 8 TC Login — 2 TC đầu có quay video
│       │   │       ├── DashboardTest.java     # 4 TC — giữ nguyên như Bài 28
│       │   │       ├── CustomersTest.java     # 3 TC — cả 3 đều quay video (start ở @Test, stop ở @AfterMethod)
│       │   │       ├── ProjectsTest.java      # 2 TC — giữ nguyên như Bài 28
│       │   │       └── TasksTest.java         # 1 TC — giữ nguyên như Bài 28
│       │   │
│       │   └── Bai33_TestListener/            # 📌 Bài 33: TestListener làm hết, test case sạch
│       │       ├── pages/                     # Copy từ Bài 32 — DashboardPage đã gỡ lời gọi chụp ảnh
│       │       │   ├── BasePage.java
│       │       │   ├── LoginPage.java
│       │       │   ├── DashboardPage.java
│       │       │   ├── CustomersPage.java
│       │       │   ├── ProjectsPage.java
│       │       │   └── TasksPage.java
│       │       └── testcases/
│       │           ├── LoginTest.java         # 8 TC Login — bỏ hết code chụp/quay, có 1 TC cố tình fail
│       │           ├── DashboardTest.java     # 4 TC — giữ nguyên như Bài 28
│       │           ├── CustomersTest.java     # 3 TC — bỏ hết code chụp/quay
│       │           ├── ProjectsTest.java      # 2 TC — giữ nguyên như Bài 28
│       │           └── TasksTest.java         # 1 TC — giữ nguyên như Bài 28
│       │
│       └── resources/
│           ├── configs/                 # 📌 Bài 29: file cấu hình
│           │   ├── config.properties    # Cấu hình chung: env, browser, headless, Excel, timeout + 4 công tắc ảnh/video (Bài 33)
│           │   ├── dev.properties       # Key riêng của môi trường dev (url, base.uri)
│           │   └── staging.properties   # Key riêng của môi trường staging (url, base.uri)
│           │
│           ├── suites/                  # TestNG Suite XML
│           │   ├── Suite_Bai28_DriverManager_ParallelExecution.xml   # Chạy POM song song trên 2 trình duyệt
│           │   ├── Suite_Bai29_PropertiesConfig.xml                  # Demo đọc config (suite mặc định trong pom.xml)
│           │   ├── Suite_Bai30_Excel_Data.xml                        # LoginTest lấy data Excel, chạy song song Chrome + Edge
│           │   ├── Suite_Bai31_DataProvider.xml                      # DemoDataProviderPOM — bật data-provider-thread-count
│           │   ├── Suite_Bai32_Screenshot_VideoRecord.xml            # CustomersTest có quay video — bắt buộc parallel="none"
│           │   └── Suite_Bai33_TestListener.xml                      # LoginTest — listener gắn ở BaseTest, <listeners> XML để comment
│           │
│           └── testdata/
│               ├── crm_data.xlsx            # 📌 Sheet Login (Bài 30) + sheet AddCustomer (Bài 31)
│               ├── crm_customer_data.xlsx   # 📌 Bài 30: file Excel mẫu để tự thực hành thêm
│               ├── customer_data.json       # File JSON trung gian (tự sinh khi chạy test)
│               └── project_data.json
│
├── exports/                         # 📌 Bài 32: output hình ảnh (đã cho vào .gitignore)
│   ├── screenshots/                 # Ảnh chụp màn hình — WebUI.takeScreenshot() & CaptureHelper.captureScreenshot()
│   └── videorecords/                # Video .avi — CaptureHelper.startRecord() / stopRecord()
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
excel_path_crm_data = src/test/resources/testdata/crm_data.xlsx
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

- **`getValue(key, defaultValue)`** trả về `defaultValue` khi key không tồn tại **hoặc để trống giá trị** (kiểu `report_path =`), tránh phải kiểm tra `null` rải rác khắp nơi. Bản một tham số `getValue(key)` thì trả thẳng `null` — gọi `.equalsIgnoreCase()` lên đó là NPE, xem lưu ý ở [Bài 33](#-bài-33--testlistener).

- **Ở chế độ headless, `maximize()` không có tác dụng thật** — cửa sổ co về 800x600, viewport bé làm web chuyển sang layout mobile và ẩn mất sidebar. Phải set `--window-size` ngay lúc khởi tạo options, và bỏ qua `maximize()` khi headless. Firefox lại dùng cú pháp khác: một gạch `-headless`, và `--width` / `--height` rời nhau.

---

## 📖 Bài 30 — Excel Data cho test case

> Kéo data test ra khỏi code Java, gom vào file `.xlsx`. Tester không biết code vẫn thêm/sửa được bộ data, và test case chỉ còn giữ phần logic.

**Class trọng tâm**

| File | Nội dung |
| :--- | :--- |
| `helpers/ExcelHelper.java` | Mở file Excel theo sheet, đọc cell bằng **tên cột**, ghi cell kèm tô màu theo trạng thái. |
| `constants/ConfigData.java` | Thêm `excel_path_crm_data` — đường dẫn file Excel lấy từ `config.properties`, không hardcode trong test. |
| `Bai30_Excel_Data/DemoExcelData.java` | Demo trần: đọc 3 dòng data, ghi 3 dòng `STATUS` để thấy màu. |
| `Bai30_Excel_Data/testcases/LoginTest.java` | Bộ 8 TC Login của Bài 28, thay toàn bộ data hardcode bằng data đọc từ Excel. |

### Thêm thư viện Apache POI

```xml
<dependency>
   <groupId>org.apache.poi</groupId>
   <artifactId>poi</artifactId>
   <version>5.5.1</version>
</dependency>
<dependency>
   <groupId>org.apache.poi</groupId>
   <artifactId>poi-ooxml</artifactId>
   <version>5.5.1</version>
</dependency>
```

> **Vì sao cần cả hai:** `poi` chỉ xử lý được định dạng `.xls` đời cũ (HSSF). Muốn đọc `.xlsx` — định dạng thật ra là một file zip chứa XML — thì phải có thêm `poi-ooxml` (XSSF). Thiếu nó là `WorkbookFactory.create()` ném `IllegalArgumentException: Your InputStream was neither an OLE2 stream, nor an OOXML stream`.

### Cấu trúc file Excel

`src/test/resources/testdata/crm_data.xlsx` — sheet **`Login`**:

| | A — EMAIL | B — PASSWORD | C — TEST_CASE_NAME | D — STATUS |
| :-- | :--- | :--- | :--- | :--- |
| **0** | *(dòng tiêu đề)* | | | |
| **1** | admin@example.com | 123456 | testLoginCRM_Success | |
| **2** | admin123@example.com | 123456 | testLoginFailWithEmailInvalid | |
| **3** | admin@example.com | 123 | testLoginFailWithPasswordInvalid | |
| **4** | *(trống)* | 123456 | testLoginFailWithEmailNull | |
| **5** | admin@example.com | *(trống)* | testLoginFailWithPasswordNull | |
| **6** | *(trống)* | *(trống)* | testLoginFailWithEmailAndPasswordNull | |
| **7** | admin@ | 123456 | testLoginFailWithEmailFormatInvalid_01 | |
| **8** | admin@example | *(trống)* | testLoginFailWithEmailFormatInvalid_02 | |

> **Dòng 0 luôn là tiêu đề.** `setExcelFile()` đọc dòng này để dựng `Map<String, Integer> columns` — tên cột ánh xạ sang chỉ số cột. Nhờ vậy code gọi `getCellData("EMAIL", 1)` chứ không phải `getCellData(0, 1)`. Chèn thêm một cột vào giữa file Excel thì code **không phải sửa dòng nào**, còn đếm chỉ số bằng tay là sai hàng loạt.
>
> Chỉ số dòng tính từ **0** như POI, nên dòng data đầu tiên là **1** — lệch một so với số dòng nhìn thấy trong Excel (dòng 2).

### Đọc data — `getCellData()`

```java
ExcelHelper excelHelper = new ExcelHelper();
excelHelper.setExcelFile(ConfigData.excel_path_crm_data, "Login");

String email = excelHelper.getCellData("EMAIL", 1);        //admin@example.com
String password = excelHelper.getCellData("PASSWORD", 1);  //123456
```

Áp vào test case, phần data biến mất khỏi code:

```java
@Test(priority = 1)
public void testLoginCRM_Success() {
   ExcelHelper excelHelper = new ExcelHelper();
   excelHelper.setExcelFile(ConfigData.excel_path_crm_data, "Login");
   dashboardPage = loginPage.loginCRM(excelHelper.getCellData("EMAIL", 1), excelHelper.getCellData("PASSWORD", 1));
   loginPage.verifyLoginSuccess();
}
```

> **Vì sao phải `switch (cell.getCellType())`:** Excel không lưu mọi thứ dưới dạng chuỗi. Cột `PASSWORD` gõ `123456` là Excel hiểu **số**, gọi thẳng `getStringCellValue()` sẽ ném `IllegalStateException: Cannot get a STRING value from a NUMERIC cell`. Với ô số, `ExcelHelper` ép `(long)` trước rồi mới `String.valueOf()` — không ép thì `123456` in ra thành `123456.0` và login sai mật khẩu.
>
> Ô ngày tháng cũng là ô NUMERIC, nên phải tách riêng bằng `DateUtil.isCellDateFormatted(cell)`, không thì `01-01-2026` trả về con số `46023`.

### Ghi kết quả — `setCellData()` và màu nền

```java
excelHelper.setCellData("Passed", "STATUS", 1);          //nền xanh lá, chữ trắng đậm
excelHelper.setCellData("Failed", "STATUS", 2);          //nền đỏ, chữ trắng đậm
excelHelper.setCellData("SELENIUM JAVA", "STATUS", 3);   //giá trị khác -> không tô nền
```

`getStyleByStatus()` so giá trị ghi vào (không phân biệt hoa thường): `passed`/`pass` → xanh, `failed`/`fail` → đỏ, còn lại → để trắng.

**Kiến thức chính:**

- **Style phải cache lại, không tạo mới mỗi lần ghi.** Một workbook Excel chỉ chứa tối đa **64.000 CellStyle**. Cứ mỗi `setCellData()` mà gọi `wb.createCellStyle()` là chạy vài nghìn dòng data đã ném `The maximum number of cell styles was exceeded`. `ExcelHelper` giữ sẵn `passedStyle`, `failedStyle`, `defaultStyle` và chỉ tạo một lần.

- **Đổi workbook thì style cũ vứt đi.** `CellStyle` gắn chặt với workbook sinh ra nó — gán style của workbook A cho cell của workbook B là file Excel hỏng, mở lên Excel báo lỗi repair. Vì vậy `setExcelFile()` reset cả ba biến style về `null` mỗi lần mở file mới.

- **Mỗi lần `setCellData()` là ghi lại toàn bộ file.** `wb.write(fileOut)` viết đè cả workbook, không phải chỉ một cell. Ghi 100 cell là mở/đóng file 100 lần — chấp nhận được cho demo, nhưng bộ data lớn thì nên gom lại ghi một lần ở cuối.

- **Ghi Excel KHÔNG an toàn khi chạy song song.** Đây là cùng một cái bẫy với file JSON ở phần dưới: file Excel là tài nguyên dùng chung cho cả máy. Hai luồng cùng `wb.write()` xuống một file là ghi đè kết quả của nhau, tệ hơn là file hỏng hẳn. **Đọc song song thì không sao** — mỗi test case tự `new ExcelHelper()` và mở stream riêng của mình, đọc xong đóng luôn. Đây là lý do `LoginTest` của Bài 30 chỉ đọc, phần ghi `STATUS` để riêng ở `DemoExcelData` chạy tuần tự.

- **Đường dẫn file Excel nằm ở `config.properties`, không hardcode.** `ConfigData.excel_path_crm_data` gọi `PropertiesHelper.getValue()` ngay lúc class được nạp — an toàn vì `getValue()` tự gọi `loadAllFiles()` khi config chưa được load.

### Suite của Bài 30

`Suite_Bai30_Excel_Data.xml` chạy `LoginTest` **song song trên hai trình duyệt**, mỗi trình duyệt lại chạy song song **4 method** cùng lúc:

```xml
<suite name="Suite Excel Data" parallel="tests">
   <test name="Login Test" parallel="methods" thread-count="4">
      <parameter name="browser" value="chrome" />
      <classes>
         <class name="com.anhtester.Bai30_Excel_Data.testcases.LoginTest"/>
      </classes>
   </test>
   <test name="Login Test on Firefox" parallel="methods" thread-count="4">
      <parameter name="browser" value="edge" />
      ...
   </test>
</suite>
```

> **`parallel="tests"` lồng với `parallel="methods"`:** hai thẻ `<test>` chạy đồng thời, và bên trong mỗi `<test>` lại có 4 luồng chạy method — tổng cộng tối đa **8 trình duyệt** mở cùng lúc. Chạy được là nhờ `DriverManager` dùng `ThreadLocal` từ Bài 28, và nhờ 8 TC Login **độc lập hoàn toàn** với nhau (chỉ đọc Excel, không ghi, không phụ thuộc thứ tự).
>
> `priority` trong `LoginTest` **không còn tác dụng sắp thứ tự** khi chạy `parallel="methods"` — nó chỉ quyết định thứ tự đưa method vào hàng đợi, còn chạy xong lúc nào là tùy luồng.

---

## 📖 Bài 31 — DataProvider

> Bài 30 đã kéo data ra file Excel, nhưng test case vẫn phải tự mở file và tự chỉ định lấy dòng nào. Bài 31 giao việc đó cho TestNG: `@DataProvider` trả về bộ data, TestNG **chạy lại method một lần cho mỗi dòng** — trong test case chỉ còn lại tham số.

**Class trọng tâm**

| File | Nội dung |
| :--- | :--- |
| `dataproviders/DataProviderFactory.java` | Nơi tập trung mọi `@DataProvider`: data cứng, data Excel, data lọc theo tên test case. |
| `helpers/ExcelHelper.java` | Bổ sung 4 hàm trả thẳng `Object[][]` đúng chuẩn DataProvider. |
| `Bai31_DataProvider/DemoDataProvider.java` | Demo cơ bản: data chuỗi, data số, DataProvider chạy song song. |
| `Bai31_DataProvider/DemoDataProviderExcel.java` | 4 kiểu lấy data Excel: cả sheet / khoảng dòng / dòng rời rạc / dạng `Hashtable`. |
| `Bai31_DataProvider/DemoDataProviderPOM.java` | Gắn DataProvider vào POM — 6 dòng data là 6 lần login thật. |
| `Bai31_DataProvider/testcases/LoginTest.java` | 8 TC Login của Bài 30, bỏ hẳn code đọc Excel — mỗi TC tự nhận đúng dòng data của mình. |

### `@DataProvider` hoạt động thế nào

```java
@DataProvider(name = "data_provider_01")
public Object[][] dpMethod1() {
   return new Object[][]{
           {"Value1", "Value2", "Value3"},
           {"Value4", "Value5", "Value6"},
           {"Value7", "Value8", "Value9"}
   };
}
```

```java
@Test(dataProvider = "data_provider_01", dataProviderClass = DataProviderFactory.class)
public void testDataProvider1(String username, String password, String result) {
   System.out.println("Username is: " + username);
}
```

- Kiểu trả về bắt buộc là **`Object[][]`** — mảng hai chiều: **mỗi dòng là một lần chạy**, mỗi cột là một tham số truyền vào method.
- Số cột phải **bằng đúng số tham số** của method, đúng thứ tự và đúng kiểu. Lệch một chỗ là TestNG ném `MethodMatcherException: Data provider mismatch` — không phải lỗi biên dịch nên chỉ lộ ra lúc chạy.
- Kiểu dữ liệu không nhất thiết phải là `String` — `data_provider_02` trả `int` và method nhận `int`, khớp là chạy.
- `dataProviderClass` cho phép để DataProvider ở **class riêng**. Bỏ thuộc tính này thì TestNG chỉ tìm DataProvider trong chính class test (hoặc class cha) — đây là lỗi hay gặp nhất khi mới tách `DataProviderFactory` ra ngoài.

> **Một method, nhiều test result.** Report hiện `testDataProvider1` **3 lần** chứ không phải 1. Mỗi dòng data là một kết quả độc lập: dòng 2 fail không chặn dòng 3 chạy, và tên hiển thị kèm luôn giá trị tham số để biết dòng nào hỏng.

### DataProvider chạy song song

```java
@DataProvider(name = "data_provider_04", parallel = true)
public Object[][] dataCRM() {
   return new Object[][]{
           {"admin@example.com", "123456"},
           //... tổng cộng 6 dòng
   };
}
```

```xml
<suite name="Suite DataProvider" parallel="tests" data-provider-thread-count="3">
   <test name="Login Test" parallel="methods">
      <parameter name="browser" value="chrome" />
      <classes>
         <class name="com.anhtester.Bai31_DataProvider.DemoDataProviderPOM"/>
      </classes>
   </test>
</suite>
```

- `parallel = true` bật cho **các dòng data của cùng một method** chạy đồng thời — khác hẳn `parallel="methods"` (các method khác nhau chạy đồng thời). Hai tầng này độc lập và dùng chung được.
- `data-provider-thread-count` khai báo ở thẻ `<suite>`, mặc định là **10**. Suite Bài 31 hạ xuống **3** cho đỡ nặng máy: 6 dòng data chạy 3 luồng một lượt.
- `parallel = true` mà chạy lẻ từ IDE hoặc `-Dtest=...` (không qua suite XML) thì vẫn lấy mặc định 10 luồng — dễ mở bung 10 trình duyệt cùng lúc.

> **Mỗi dòng data là một lần `@BeforeMethod`.** `BaseTest.createDriver()` chạy lại cho **từng dòng**, nên `data_provider_04` với 6 dòng là mở và đóng **6 trình duyệt**. Không đụng nhau là nhờ `ThreadLocal` của Bài 28 — mỗi luồng vẫn giữ driver riêng.

### Lấy data từ Excel — 4 kiểu

`ExcelHelper` bổ sung 4 hàm, khác nhau ở **phạm vi dòng** và **hình dạng data trả về**:

| Hàm | Mỗi dòng trả về | Dùng khi |
| :--- | :--- | :--- |
| `getExcelData(path, sheet)` | Mảng các cột → method nhận **nhiều tham số** | Lấy **cả sheet** từ dòng 1 đến hết |
| `getDataHashTable(path, sheet, startRow, endRow)` | **Một** `Hashtable<String, String>` | Lấy một **khoảng dòng** liên tiếp |
| `getDataFromSpecificRows(path, sheet, int[] rows)` | Mảng các cột | Chỉ chạy vài dòng **rời rạc** |
| `getDataHashTableFromSpecificRows(path, sheet, int[] rows)` | **Một** `Hashtable<String, String>` | Dòng rời rạc + lấy theo **tên cột** |

`src/test/resources/testdata/crm_data.xlsx` có thêm sheet **`AddCustomer`**:

| | A — COMPANY | B — VAT | C — ADDRESS | D — PHONE |
| :-- | :--- | :--- | :--- | :--- |
| **0** | *(dòng tiêu đề)* | | | |
| **1** | Google | 10 | USA | 123456 |
| **2** | Microsoft | 5 | USA | 12345 |
| **3** | FPT | 7 | VN | 1234 |
| **4** | NVIDIA | 15 | CHINA | 1234567 |
| **5** | SAMSUNG | 20 | KOREA | 0986875 |
| **6** | VIETTEL | 25.5 | VN | 099864745 |
| **7** | VNPT | 30.7 | VN | 077547935 |

**Kiểu 1 — cả sheet, method nhận từng cột làm một tham số:**

```java
@DataProvider(name = "data_provider_addcustomer_excel")
public Object[][] dataAddCustomerFromExcel() {
   ExcelHelper excelHelper = new ExcelHelper();
   return excelHelper.getExcelData(ConfigData.excel_path_crm_data, "AddCustomer");
}
```

```java
@Test(dataProvider = "data_provider_addcustomer_excel", dataProviderClass = DataProviderFactory.class)
public void testDataProviderAddCustomerExcel(String company, String vat, String address, String phone) { ... }
```

**Kiểu 2 — `Hashtable`, method chỉ nhận đúng 1 tham số:**

```java
Object[][] data = excelHelper.getDataHashTable(ConfigData.excel_path_crm_data, "AddCustomer", 3, 5);
```

```java
@Test(dataProvider = "dp_addcustomer_excel_start_end", dataProviderClass = DataProviderFactory.class)
public void testDataProviderAddCustomerExcelStartEnd(Hashtable<String, String> data) {
   System.out.println(data.get("COMPANY"));
}
```

> **File Excel nhiều cột thì nên dùng `Hashtable`.** Kiểu 1 buộc signature của method phải khớp **số cột và thứ tự cột** — chèn thêm một cột vào giữa file Excel là hỏng hết các method đang dùng. Kiểu 2 chỉ có một tham số duy nhất, lấy giá trị bằng **tên cột** (`data.get("COMPANY")`), thêm cột thoải mái mà không method nào phải sửa.
>
> Chỉ số dòng vẫn tính từ **0** và dòng 0 là tiêu đề, nên `(..., 3, 5)` là ba dòng **FPT, NVIDIA, SAMSUNG**, còn `int[]{1, 3, 4}` là **Google, FPT, NVIDIA**.

### Mỗi test case tự lấy đúng dòng data của mình

Đây là phần đáng giá nhất của Bài 31 — DataProvider nhận tham số `Method` do TestNG tự tiêm vào:

```java
@DataProvider(name = "data_login")
public Object[][] dataLogin(Method method) {
   String testCaseName = method.getName();          // TestNG tự truyền vào

   ExcelHelper excelHelper = new ExcelHelper();
   excelHelper.setExcelFile(ConfigData.excel_path_crm_data, "Login");

   // Quét tìm dòng có TEST_CASE_NAME khớp tên method
   for (int i = 1; i <= excelHelper.getLastRowNum(); i++) {
      if (testCaseName.equals(excelHelper.getCellData("TEST_CASE_NAME", i))) {
         Hashtable<String, String> data = new Hashtable<>();
         data.put("EMAIL", excelHelper.getCellData("EMAIL", i));
         data.put("PASSWORD", excelHelper.getCellData("PASSWORD", i));
         return new Object[][]{{data}};            // 1 dòng cho TC này
      }
   }
   throw new SkipException("Không tìm thấy data cho test case: " + testCaseName);
}
```

Nhờ vậy test case sạch hẳn phần data — cả 8 TC dùng chung một DataProvider mà vẫn nhận đúng data của riêng mình:

```java
@Test(priority = 1, dataProvider = "data_login", dataProviderClass = DataProviderFactory.class)
public void testLoginCRM_Success(Hashtable<String, String> data) {
   dashboardPage = loginPage.loginCRM(data.get("EMAIL"), data.get("PASSWORD"));
   loginPage.verifyLoginSuccess();
}
```

So với Bài 30 — cùng một bộ 8 TC Login:

| | Bài 30 | Bài 31 |
| :--- | :--- | :--- |
| Code đọc Excel | Viết lại trong **từng TC** | Nằm gọn trong `DataProviderFactory` |
| Chọn dòng data | `getCellData("EMAIL", 1)` — **số dòng hardcode** trong code | Khớp theo cột `TEST_CASE_NAME` |
| Chèn/xóa dòng trong Excel | Phải sửa lại số dòng ở cả 8 TC | Không đụng vào code |
| Thiếu data | TC fail với lỗi khó hiểu | TC **Skipped** kèm thông báo rõ ràng |

> **`throw new SkipException(...)` chứ không để fail.** Không tìm thấy dòng data là lỗi của **bộ data**, không phải của web đang test. Cho TC vào trạng thái **Skipped** giúp phân biệt ngay hai loại vấn đề này khi đọc report — thói quen nên giữ cho cả framework.

**Kiến thức chính:**

- **DataProvider chạy TRƯỚC `@BeforeMethod`.** TestNG phải gọi DataProvider trước để biết method sẽ chạy bao nhiêu lần, rồi mới chạy `@BeforeMethod` cho từng lần. Nghĩa là trong DataProvider **chưa có driver** — gọi `DriverManager.getDriver()` ở đó là `null`. Data phải lấy từ file, DB hoặc API, không lấy từ trình duyệt.

- **Ô số đọc ra bị kèm `.0`.** `getExcelData()` xử lý ô NUMERIC bằng `String.valueOf(cell.getNumericCellValue())`, nên cột `VAT` gõ `20` trả về chuỗi `"20.0"`. Đó là lý do demo phải `Double.parseDouble(vat)` trước khi tính toán — so sánh chuỗi thẳng là sai. Ngược lại, `getCellData()` của Bài 30 ép `(long)` nên `25.5` bị cắt còn `"25"`. Cột có số thập phân thì nên để Excel định dạng **Text**, hoặc đọc qua `getExcelData()`.

- **Ô trống làm `getExcelData()` ném NPE.** Cell chưa từng được gõ thì `row.getCell(j)` trả về `null`, mà hàm gọi thẳng `cell.getCellType()`. Sheet `AddCustomer` điền đủ nên không dính, nhưng sheet `Login` có nhiều ô trống cố ý — đó là lý do `data_login` dùng `getCellData()` (đã bọc `try/catch` trả `""`) thay vì `getExcelData()`.

- **`System.out.println(data)` với mảng là vô nghĩa.** Nó chỉ in ra `[[Ljava.lang.Object;@1b6d3586`. Muốn xem data thật sự lấy được gì thì dùng `Arrays.deepToString(data)`.

- **`priority` không còn sắp thứ tự khi bật parallel** — giống Bài 30. Trong `Suite_Bai31_DataProvider.xml`, `priority = 1` của `testLoginCRM_Success` chỉ quyết định thứ tự đưa method vào hàng đợi.

- **DataProvider chỉ nên đọc file, đừng ghi.** Nhiều luồng cùng gọi một DataProvider có `parallel = true`; ghi ngược xuống Excel là dính đúng cái bẫy tranh chấp file đã nói ở Bài 30.

---

## 📖 Bài 32 — Screenshot và Record Video

> Test fail lúc 2 giờ sáng trên máy CI thì đọc log không đủ. Bài 32 thêm **bằng chứng hình ảnh**: chụp màn hình từng bước bằng chính trình duyệt đang chạy test, và quay lại nguyên phiên chạy thành file video.

**Class trọng tâm**

| File | Nội dung |
| :--- | :--- |
| `helpers/CaptureHelper.java` | Gộp 2 việc vào một class: `captureScreenshot()` chụp ảnh bằng Selenium, `startRecord()` / `stopRecord()` quay video bằng Monte Media. |
| `constants/ConfigData.java` | Thêm `SCREENSHOT_PATH` và `VIDEO_RECORD_PATH`, đọc từ `config.properties` chứ không hardcode. |
| `keywords/WebUI.java` | `clickElement()` và `setText()` gọi thêm `CaptureHelper.captureScreenshot()` — chụp ở mức keyword. |
| `Bai32_Screenshot_VideoRecord/DemoScreenshot.java` | 3 cách chụp màn hình từ thô đến gọn + `@AfterMethod` chụp tự động theo trạng thái TC. |
| `Bai32_Screenshot_VideoRecord/testcases/LoginTest.java` | Quay video từng TC Login (data vẫn lấy từ DataProvider của Bài 31). |
| `Bai32_Screenshot_VideoRecord/testcases/CustomersTest.java` | Quay video 3 TC Customers — TC dài, xem lại video dễ hơn đọc log. |

`utils/CaptureUtils.java` (chụp bằng `Robot` từ Bài 12) **đã bị xóa** — thay hoàn toàn bằng `CaptureHelper`.

### Chụp màn hình — 3 bước của `TakesScreenshot`

```java
// 1. Ép kiểu driver về TakesScreenshot
TakesScreenshot ts = (TakesScreenshot) DriverManager.getDriver();
// 2. Chụp và nhận về dạng File tạm
File source = ts.getScreenshotAs(OutputType.FILE);
// 3. Copy file tạm ra chỗ mình muốn lưu
FileHandler.copy(source, new File("./screenshots/testHomePage1.png"));
```

> **`getScreenshotAs()` trả về file TẠM.** Selenium ghi ảnh vào thư mục temp của hệ điều hành và file đó bị xóa khi JVM thoát. Không `FileHandler.copy()` ra chỗ khác là mất ảnh — đây là lỗi hay gặp nhất khi mới học phần này.
>
> `OutputType` còn 2 kiểu khác: `OutputType.BYTES` (mảng byte, dùng khi đẩy thẳng vào report) và `OutputType.BASE64` (chuỗi base64, nhúng thẳng vào HTML report).

### Đặt tên file — đừng hardcode

`testHomePage1` hardcode tên file nên chạy lại là ghi đè. Hai cách lấy tên TC tự động:

```java
// Cách 1: nhận tham số Method — TestNG tự bơm vào
@Test
public void testHomePage2(Method method) {
   ...
   FileHandler.copy(source, new File("./screenshots/" + method.getName() + ".png"));
}

// Cách 2: nhận ITestResult ở @AfterMethod — có thêm cả trạng thái PASS/FAIL
@AfterMethod
public void takeScreenshot(ITestResult result) {
   if (ITestResult.SUCCESS == result.getStatus()) {
      CaptureHelper.captureScreenshot(result.getName());
   }
}
```

> **`Method` và `ITestResult` là tham số TestNG tự bơm vào**, không cần khai báo `@DataProvider` hay `@Parameters` gì cả. Khác nhau ở chỗ: `Method` dùng được ngay trong `@Test` (lúc TC còn đang chạy, chưa biết kết quả), còn `ITestResult` chỉ có ở `@AfterMethod` (lúc đã biết PASS hay FAIL).
>
> Trạng thái trong `ITestResult` là hằng số `int`: `SUCCESS = 1`, `FAILURE = 2`, `SKIP = 3`. Muốn chụp **khi fail** — trường hợp thực tế cần nhất — thì đổi điều kiện thành `ITestResult.FAILURE == result.getStatus()`.

### Gom vào `CaptureHelper.captureScreenshot()`

```java
private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");

public static void captureScreenshot(String screenshotName) {
   try {
      TakesScreenshot ts = (TakesScreenshot) DriverManager.getDriver();
      File source = ts.getScreenshotAs(OutputType.FILE);

      File theDir = new File(SystemHelper.getCurrentDir() + ConfigData.SCREENSHOT_PATH);
      if (!theDir.exists()) {
         theDir.mkdirs();
      }
      FileHandler.copy(source, new File(SystemHelper.getCurrentDir() + ConfigData.SCREENSHOT_PATH
              + File.separator + screenshotName + "_" + dateFormat.format(new Date()) + ".png"));
   } catch (Exception e) {
      System.out.println("Exception while taking screenshot: " + e.getMessage());
   }
}
```

Ba thứ hàm này lo giùm mà code thô ở trên phải tự làm:

- **Tên file gắn thêm ngày giờ** (`dd-MM-yyyy HH-mm-ss`) → chạy 10 lần ra 10 file, không cái nào đè cái nào.
- **`mkdirs()` trước khi copy** → thư mục chưa có thì tự tạo, kể cả nhiều cấp.
- **`try/catch` nuốt lỗi, không ném ra ngoài** → hàm này hay được gọi đúng lúc test **đang fail**; nếu chụp hỏng mà ném exception thì nó che mất nguyên nhân fail thật.

### Chụp ở mức keyword — `WebUI`

Thay vì rải lời gọi chụp trong từng TC, Bài 32 đặt thẳng vào keyword:

```java
public static void clickElement(By by) {
   waitForElementClickable(by);
   sleep(STEP_TIME);
   CaptureHelper.captureScreenshot("clickElement");
   ...
}
```

> **Ở Bài 32, cách này chụp MỌI bước của MỌI test case.** Chạy hết bộ POM là ra hàng trăm tấm ảnh và test chậm đi thấy rõ (mỗi lần chụp mất vài trăm ms). **Bài 33 đã sửa lại**: bọc trong công tắc `SCREENSHOT_ALL_STEPS` và dời lời gọi xuống **sau** thao tác — xem [Bài 33](#-bài-33--testlistener).
>
> ```java
> if (ConfigData.SCREENSHOT_ALL_STEPS.equalsIgnoreCase("true")) {
>    CaptureHelper.captureScreenshot("clickElement");
> }
> ```

`WebUI` cũng đã có sẵn `takeScreenshot()` và `takeElementScreenshot()` từ các bài trước. Khác biệt: `WebUI.takeScreenshot()` **trả về đường dẫn file** (tiện gắn vào report) và chỉ bắt `WebDriverException`, còn `CaptureHelper.captureScreenshot()` trả về `void` và bắt mọi `Exception`.

### Quay video với Monte Screen Recorder

Thêm thư viện vào `pom.xml`:

```xml
<dependency>
   <groupId>com.github.stephenc.monte</groupId>
   <artifactId>monte-screen-recorder</artifactId>
   <version>0.7.7.0</version>
</dependency>
```

`CaptureHelper` **kế thừa** `ScreenRecorder` để ghi đè lại cách đặt tên file video:

```java
public class CaptureHelper extends ScreenRecorder {

   public static ScreenRecorder screenRecorder;
   public String name;

   public CaptureHelper(GraphicsConfiguration cfg, Rectangle captureArea, Format fileFormat,
                        Format screenFormat, Format mouseFormat, Format audioFormat,
                        File movieFolder, String name) throws IOException, AWTException {
      super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, movieFolder);
      this.name = name;
   }

   // Bắt buộc ghi đè: mặc định thư viện đặt tên file kiểu "ScreenRecording ...",
   // nhìn vào không biết video nào của test case nào
   @Override
   protected File createMovieFile(Format fileFormat) throws IOException {
      if (!movieFolder.exists()) {
         movieFolder.mkdirs();
      }
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
      return new File(movieFolder, name + "-" + dateFormat.format(new Date())
              + "." + Registry.getInstance().getExtension(fileFormat));
   }
}
```

Hai hàm dùng trong test case:

```java
public static void startRecord(String methodName) {
   File file = new File(SystemHelper.getCurrentDir() + ConfigData.VIDEO_RECORD_PATH);

   // Lấy kích thước màn hình DESKTOP, không phải kích thước trình duyệt
   Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
   Rectangle captureSize = new Rectangle(0, 0, screenSize.width, screenSize.height);

   GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
           .getDefaultScreenDevice().getDefaultConfiguration();

   screenRecorder = new CaptureHelper(gc, captureSize,
           new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),          // định dạng file: .avi
           new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                   DepthKey, 24, FrameRateKey, Rational.valueOf(15), ...),           // 24-bit màu, 15 hình/giây
           new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black",
                   FrameRateKey, Rational.valueOf(30)),                              // con trỏ chuột
           null,                                                                     // audio: không thu
           file, methodName);
   screenRecorder.start();
}

public static void stopRecord() {
   screenRecorder.stop();
}
```

Gắn vào test case — start ở đầu mỗi `@Test`, stop ở `@AfterMethod`:

```java
@AfterMethod
public void afterMethod() {
   WebUI.sleep(2);          // chờ 2 giây để video không bị cắt cụt ở khung hình cuối
   CaptureHelper.stopRecord();
}

@Test
public void testAddNewCustomer() {
   CaptureHelper.startRecord("testAddNewCustomer");
   ...
}
```

### Đường dẫn lưu file

`config.properties`:

```properties
SCREENSHOT_PATH = exports/screenshots
VIDEO_RECORD_PATH = exports/videorecords
```

`ConfigData` đọc lên thành hằng số, `SystemHelper.getCurrentDir()` (`System.getProperty("user.dir")`) ghép vào thành đường dẫn tuyệt đối — nhờ vậy chạy từ IDE hay từ Maven đều ra đúng một chỗ. Thư mục `exports/` đã được thêm vào `.gitignore` nên ảnh và video không bị commit lên Git.

**Kiến thức chính:**

- **`TakesScreenshot` chụp trình duyệt, Monte quay cả màn hình desktop.** Khác biệt này quyết định mọi thứ còn lại: chụp bằng Selenium thì chạy được ở **headless** và trên **máy CI không có màn hình**, còn quay video bằng Monte thì **bắt buộc phải có màn hình thật** và cửa sổ trình duyệt phải đang hiện lên.

- **Quay video KHÔNG chạy song song được.** Monte quay nguyên cái desktop, nên 3 luồng chạy cùng lúc sẽ cho ra 3 file video giống hệt nhau, mỗi file đều có đủ 3 trình duyệt chồng lên nhau. Đó là lý do `Suite_Bai32_Screenshot_VideoRecord.xml` để `parallel="none"` — ngược hẳn với tinh thần Bài 28 → 31. Muốn vừa parallel vừa có video thì phải đổi hướng: mỗi luồng chạy trong một container riêng (Selenium Grid, Docker + `selenium/video`).

- **`screenRecorder` là biến `static`** — cả JVM chỉ giữ được **một** recorder. TC nào quên gọi `startRecord()` mà `@AfterMethod` vẫn gọi `stopRecord()` thì hoặc dính `NullPointerException` (chưa TC nào start), hoặc **dừng nhầm/ghi đè lên video của TC trước**. Trong `LoginTest` của bài này chỉ 2/8 TC có `startRecord()` — chạy cả class là thấy ngay hiện tượng. Đúng cái bẫy này là lý do **Bài 33 chuyển hẳn việc start/stop sang `TestListener`**, không để test case tự gọi nữa.

- **`WebUI.sleep(2)` trước `stopRecord()` không phải thừa.** Recorder ghi theo khung hình, dừng ngay lập tức sau assert cuối thì khung hình quan trọng nhất (kết quả) hay bị cắt mất.

- **File `.avi` rất nặng.** Codec TechSmith ở 15 fps toàn màn hình 1920x1080 cho ra tầm vài chục MB cho một TC ngắn. Chạy cả bộ test là đầy ổ đĩa — nên chỉ bật quay cho những TC dài, hoặc dọn thư mục `exports/videorecords` định kỳ.

- **`DemoScreenshot` cố tình để lộn xộn.** Hai TC đầu lưu vào `./screenshots/`, TC thứ ba dùng `CaptureHelper` nên lưu vào `exports/screenshots/`. Đây là chủ ý minh họa đường đi từ **code thô** → **code gom vào helper**; khi viết framework thật thì chỉ giữ lại cách thứ ba.

- **Suite của bài này đặt tên hơi lệch nội dung:** `<test name="Login Test">` nhưng bên trong lại chạy `CustomersTest`. Không ảnh hưởng kết quả, nhưng tên `<test>` hiện thẳng lên report nên sửa lại cho khớp thì đọc report đỡ rối.

---

## 📖 Bài 33 — TestListener

> Bài 32 phải gọi tay `startRecord()` / `stopRecord()` / `captureScreenshot()` trong từng test case — code nghiệp vụ lẫn với code hạ tầng, và chỉ cần quên một dòng là mất bằng chứng. Bài 33 giao hết cho TestNG: viết **một** class `TestListener` nghe sự kiện, test case trở lại sạch sẽ như chưa từng có chụp ảnh quay video.

**Class trọng tâm**

| File | Nội dung |
| :--- | :--- |
| `listeners/TestListener.java` | Implement `ITestListener` — nghe 6 sự kiện của TestNG, tự chụp ảnh và quay video theo công tắc trong config. |
| `common/BaseTest.java` | Gắn `@Listeners({TestListener.class})` cho **mọi** class kế thừa; thêm loạt `prefs` tắt popup của Chrome/Edge. |
| `constants/ConfigData.java` | 4 công tắc mới: `SCREENSHOT_PASSED_STEP`, `SCREENSHOT_FAILED_STEP`, `SCREENSHOT_ALL_STEPS`, `VIDEO_RECORD_ACTIVE`. |
| `keywords/WebUI.java` | Chụp ảnh ở mức keyword giờ **có điều kiện** và chụp **sau** khi thao tác xong. |
| `Bai33_TestListener/testcases/` | Bộ test case của Bài 32 nhưng **xóa sạch** code chụp/quay — so sánh trực tiếp thấy ngay cái được. |
| `suites/Suite_Bai33_TestListener.xml` | Chạy `LoginTest`, phần `<listeners>` để **comment sẵn** — minh họa cách đăng ký thứ hai. |

### `ITestListener` — nghe những sự kiện gì

```java
public class TestListener implements ITestListener {

   @Override
   public void onStart(ITestContext result) { ... }        // vào thẻ <test> trong XML

   @Override
   public void onTestStart(ITestResult result) { ... }     // trước mỗi @Test

   @Override
   public void onTestSuccess(ITestResult result) { ... }   // TC pass

   @Override
   public void onTestFailure(ITestResult result) { ... }   // TC fail
   @Override
   public void onTestSkipped(ITestResult result) { ... }   // TC bị skip

   @Override
   public void onFinish(ITestContext result) { ... }       // hết thẻ <test>
}
```

> **`ITestContext` khác `ITestResult`.** `onStart`/`onFinish` nhận `ITestContext` — phạm vi cả thẻ `<test>`, có `getStartDate()`, `getEndDate()`, `getPassedTests()`, `getFailedTests()`. Bốn callback còn lại nhận `ITestResult` — phạm vi **một** test case, có `getName()`, `getStatus()`, `getThrowable()`.
>
> `ITestListener` còn vài callback nữa ít dùng, đáng nhớ nhất là `onTestFailedButWithinSuccessPercentage()` (dùng chung với `successPercentage` của `@Test`). Không override thì TestNG dùng bản mặc định rỗng.

**Thứ tự chạy thực tế** (chạy thử một class có đủ annotation sẽ in ra đúng như vậy):

```
onStart
  @BeforeClass
    @BeforeMethod          ← driver được tạo ở đây
    onTestStart            ← startRecord()
      (thân test case)
    onTestSuccess / onTestFailure / onTestSkipped   ← captureScreenshot(), stopRecord()
    @AfterMethod           ← driver bị quit ở đây
  @AfterClass
onFinish
```

> **Đây là điểm mấu chốt khiến listener làm được việc:** callback kết quả chạy **sau** khi test case xong nhưng **trước** `@AfterMethod`. Nhờ vậy lúc `onTestFailure` chụp màn hình thì driver **vẫn còn sống** — đúng khoảnh khắc màn hình đang hiển thị lỗi. Đảo lại thứ tự (chụp trong `@AfterMethod` đặt sau `closeDriver()`) là dính `NoSuchSessionException`.

### Nội dung `TestListener`

```java
@Override
public void onTestStart(ITestResult result) {
   System.out.println("Bắt đầu chạy test case: " + result.getName());
   if (ConfigData.VIDEO_RECORD_ACTIVE.equalsIgnoreCase("true")) {
      CaptureHelper.startRecord(result.getName());
   }
}

@Override
public void onTestSuccess(ITestResult result) {
   System.out.println("Test case " + result.getName() + " is passed.");
   if (ConfigData.SCREENSHOT_PASSED_STEP.equalsIgnoreCase("true")) {
      CaptureHelper.captureScreenshot(result.getName());
   }
   if (ConfigData.VIDEO_RECORD_ACTIVE.equalsIgnoreCase("true")) {
      WebUI.sleep(2);
      CaptureHelper.stopRecord();
   }
}

@Override
public void onTestFailure(ITestResult result) {
   System.out.println("Test case " + result.getName() + " is failed.");
   System.out.println("==> Nguồn gốc Fail: " + result.getThrowable());   // in ra exception gốc
   if (ConfigData.SCREENSHOT_FAILED_STEP.equalsIgnoreCase("true")) {
      CaptureHelper.captureScreenshot(result.getName());
   }
   ...
}
```

> **`result.getThrowable()` là thứ đáng giá nhất trong `ITestResult`.** Nó trả về đúng exception làm TC fail — `AssertionError` với thông báo so sánh, hay `TimeoutException` khi chờ element. In ra đây là có ngay dòng đầu tiên để đọc khi mở log, không phải lội ngược lên tìm.

### Hai cách đăng ký listener

```java
// Cách 1 — gắn vào BaseTest: mọi class kế thừa BaseTest đều tự có, không phải khai báo lại
@Listeners({TestListener.class})
public class BaseTest { ... }
```

```xml
<!-- Cách 2 — khai báo trong Suite XML: áp cho toàn bộ suite, đổi listener không cần build lại -->
<listeners>
   <listener class-name="com.anhtester.listeners.TestListener"></listener>
</listeners>
```

Trong `Suite_Bai33_TestListener.xml`, khối `<listeners>` đang **để comment** vì `BaseTest` đã gắn `@Listeners` rồi.

> **Đừng đăng ký cả hai chỗ cùng lúc.** Listener rất dễ bị gọi **hai lần** cho một sự kiện — hệ quả là mỗi TC ra 2 tấm ảnh, và tệ hơn: `startRecord()` chạy 2 lần đè lên biến `static screenRecorder`, `stopRecord()` chỉ dừng được cái sau, cái trước treo lại. Chọn một chỗ và giữ nguyên.
>
> Cách 3 (không dùng trong bài này) là `ServiceLoader` — đặt tên class vào `META-INF/services/org.testng.ITestNGListener`, listener tự động áp cho mọi lần chạy mà không cần khai báo ở đâu cả.

### 4 công tắc trong `config.properties`

```properties
SCREENSHOT_PATH = exports/screenshots
VIDEO_RECORD_PATH = exports/videorecords
SCREENSHOT_PASSED_STEP = false      # chụp khi TC pass
SCREENSHOT_FAILED_STEP = true       # chụp khi TC fail — cái cần nhất, để mặc định bật
SCREENSHOT_ALL_STEPS = false        # chụp MỌI bước (click, setText, getText, assert)
VIDEO_RECORD_ACTIVE = false         # quay video cả phiên chạy
```

Bốn key này thay cho `record_video` / `screenshot_all_steps` / `screenshot_fail_steps` / `screenshot_pass_steps` của các bài trước (key `report_path` bỏ hẳn). `ConfigData` đọc lên thành hằng số để cả framework dùng chung:

```java
public static String SCREENSHOT_PASSED_STEP = PropertiesHelper.getValue("SCREENSHOT_PASSED_STEP");
public static String SCREENSHOT_FAILED_STEP = PropertiesHelper.getValue("SCREENSHOT_FAILED_STEP");
public static String SCREENSHOT_ALL_STEPS = PropertiesHelper.getValue("SCREENSHOT_ALL_STEPS");
public static String VIDEO_RECORD_ACTIVE = PropertiesHelper.getValue("VIDEO_RECORD_ACTIVE");
```

### `WebUI` — chụp có điều kiện và chụp ĐÚNG lúc

```java
public static void clickElement(By by) {
   waitForElementClickable(by);
   sleep(STEP_TIME);
   retryUntil(_driver -> {
      _driver.findElement(by).click();
      return true;
   });
   logConsole("Click on element " + by);

   if (ConfigData.SCREENSHOT_ALL_STEPS.equalsIgnoreCase("true")) {
      CaptureHelper.captureScreenshot("clickElement");
   }
}
```

Hai thay đổi so với Bài 32:

- **Có công tắc** `SCREENSHOT_ALL_STEPS` — mặc định `false`, không còn chụp vô điều kiện làm chậm cả bộ test.
- **Chụp SAU khi thao tác**, không phải trước. Chụp trước cú click chỉ thấy trang cũ; chụp sau mới thấy **kết quả** của cú click — đúng thứ cần khi dò lỗi.

Danh sách keyword có chụp: `clickElement()` (2 overload), `setText()`, `setTextAndKey()`, `getElementText()`, `checkEquals()`, `checkContains()`.

### Test case sạch trở lại

| Bài 32 | Bài 33 |
| :--- | :--- |
| `@AfterMethod { WebUI.sleep(2); CaptureHelper.stopRecord(); }` trong từng class | không còn — listener lo |
| `CaptureHelper.startRecord("testAddNewCustomer");` ở dòng đầu mỗi `@Test` | không còn — listener lo |
| `DashboardPage` gọi `captureScreenshot("Dashboard Page")` ngay trong page class | đã gỡ bỏ |
| Bật/tắt = sửa code, build lại | Bật/tắt = sửa `config.properties` |

`Bai33_TestListener/testcases/LoginTest.java` có một TC **cố tình fail** để xem `onTestFailure` chạy:

```java
loginPage.verifyLoginFail("Invalid email or password 123");   // web trả về chuỗi không có " 123"
```

### `BaseTest` — tắt popup của trình duyệt

Bài 33 thêm vào `ChromeOptions` / `EdgeOptions` một loạt `prefs`:

```java
Map<String, Object> prefs = new HashMap<String, Object>();
prefs.put("profile.default_content_setting_values.notifications", 2);  // chặn xin quyền thông báo
prefs.put("profile.password_manager_leak_detection", false);           // tắt "đổi mật khẩu ngay"
prefs.put("credentials_enable_service", false);
prefs.put("profile.password_manager_enabled", false);                  // tắt "lưu mật khẩu?"
prefs.put("autofill.profile_enabled", false);                          // tắt "lưu địa chỉ?"
options.setExperimentalOption("prefs", prefs);
options.addArguments("--disable-extensions");
options.addArguments("--disable-infobars");
options.addArguments("--disable-notifications");
options.addArguments("--remote-allow-origins=*");
options.setAcceptInsecureCerts(true);
```

> **Vì sao đặt đúng vào bài này:** popup "Save password?" của Chrome nhảy ra ngay sau khi login — nó **che element** làm TC fail vô cớ, và tấm ảnh listener chụp được lại dính nguyên cái popup thay vì nội dung trang. Tắt từ lúc khởi tạo driver là gọn nhất.
>
> `setAcceptInsecureCerts(true)` cho phép chạy trên site có chứng chỉ SSL tự ký — hay gặp ở môi trường staging nội bộ.

**Kiến thức chính:**

- **Listener chạy trước `@AfterMethod`, sau `@BeforeMethod`** — nhờ vậy driver luôn còn sống trong mọi callback. Đây là lý do mô hình "listener chụp ảnh khi fail" là chuẩn mực của gần như mọi framework Selenium, chứ không phải chụp trong `@AfterMethod`.

- **`ConfigData.X.equalsIgnoreCase("true")` sẽ ném `NullPointerException` nếu thiếu key.** `PropertiesHelper.getValue(key)` trả `null` khi không tìm thấy, mà `null.equalsIgnoreCase(...)` là NPE ngay từ `onTestStart` — hỏng cả suite chỉ vì gõ sai một dòng trong file properties. An toàn hơn thì đảo vế (`"true".equalsIgnoreCase(ConfigData.X)`) hoặc dùng bản 2 tham số `getValue("VIDEO_RECORD_ACTIVE", "false")` đã có sẵn từ Bài 29.

- **`ConfigData` là `static` nên chỉ đọc config MỘT lần**, đúng lúc class được nạp. Bốn công tắc này đọc thẳng `PropertiesHelper.getValue()` chứ **không đi qua** `ParameterManager.getConfigValue()` của Bài 29 — nghĩa là `mvn test "-DVIDEO_RECORD_ACTIVE=true"` **không có tác dụng**, phải sửa file `config.properties`. Muốn đè được từ dòng lệnh thì đổi sang `ParameterManager.getConfigValue("VIDEO_RECORD_ACTIVE", "false")`.

- **Listener không cứu được chuyện quay video song song.** `CaptureHelper.screenRecorder` vẫn là biến `static` và Monte vẫn quay cả màn hình desktop — bật `VIDEO_RECORD_ACTIVE = true` rồi chạy `parallel="methods"` là các luồng giẫm lên recorder của nhau. Đó là lý do `Suite_Bai33_TestListener.xml` vẫn để `parallel="none"` (thuộc tính `thread-count="4"` trong suite chỉ có tác dụng khi `parallel` khác `none`).

- **Listener nhận cùng một `ITestResult` mà `@AfterMethod` nhận.** Nếu muốn dùng lại các `@AfterMethod(ITestResult result)` đã viết ở Bài 32, chỉ cần nhớ: cùng dữ liệu, khác thời điểm — và chọn **một** trong hai chỗ để làm, đừng làm cả hai.

- **`ITestListener` chỉ nghe, không sửa được luồng chạy.** Muốn can thiệp sâu hơn thì dùng interface khác: `IRetryAnalyzer` (chạy lại TC fail), `IAnnotationTransformer` (gắn retry cho mọi `@Test` mà không sửa code), `IInvokedMethodListener` (nghe cả các method `@Before...`/`@After...`), `ISuiteListener` (phạm vi cả suite thay vì từng thẻ `<test>`).

- **`import com.anhtester.listeners.TestListener;` trong `Bai33_TestListener/testcases/LoginTest.java` là import thừa** — class đó không dùng tới `TestListener` vì `@Listeners` đã nằm ở `BaseTest`. Xóa đi cho gọn.

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
# Chạy suite của một bài khác mà không phải sửa pom.xml
# = LoginTest của Bài 30, data lấy từ crm_data.xlsx
mvn test "-Dsurefire.suiteXmlFiles=src/test/resources/suites/Suite_Bai30_Excel_Data.xml"
```

```bash
# = DemoDataProviderPOM của Bài 31, 6 dòng data chạy 3 luồng một lượt
mvn test "-Dsurefire.suiteXmlFiles=src/test/resources/suites/Suite_Bai31_DataProvider.xml"
```

```bash
# = CustomersTest của Bài 32, vừa chạy vừa quay video — phải chạy tuần tự và KHÔNG headless
mvn test "-Dsurefire.suiteXmlFiles=src/test/resources/suites/Suite_Bai32_Screenshot_VideoRecord.xml"
```

```bash
# = LoginTest của Bài 33, TestListener tự chụp ảnh khi fail (có 1 TC cố tình fail để xem)
mvn test "-Dsurefire.suiteXmlFiles=src/test/resources/suites/Suite_Bai33_TestListener.xml"
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
- Video quay màn hình: `exports/videorecords/` (chỉ có khi bật `VIDEO_RECORD_ACTIVE = true`)

> Từ Bài 33, việc chụp ảnh / quay video do `TestListener` lo — bật tắt bằng 4 key `SCREENSHOT_PASSED_STEP`, `SCREENSHOT_FAILED_STEP`, `SCREENSHOT_ALL_STEPS`, `VIDEO_RECORD_ACTIVE` trong `config.properties`, không phải sửa code.

---

## 📄 Giấy phép

Dự án này được phân phối dưới giấy phép **MIT**. Xem file [LICENSE](LICENSE) để biết thêm chi tiết.

---

> **Tác giả:** [Anh Tester](https://anhtester.com) — Khóa học Selenium Java 01/2026
