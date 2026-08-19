# ⚡ SeleniumTestNGParallel012026

> Source code khóa học **Selenium Java 01/2026** — Anh Tester
> Phần **chạy test song song và cấu hình framework** (Bài 28 → 30), tách riêng từ repo chính [SeleniumMaven012026](https://github.com/anhtester/SeleniumMaven012026) (Bài 5 → 27).
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
- [Bài 30 — Excel Data cho test case](#-bài-30--excel-data-cho-test-case)
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

   ```bash
   mvn test "-Dsurefire.suiteXmlFiles=src/test/resources/suites/Suite_Bai30_Excel_Data.xml"
   ```

---

## 🛠 Công nghệ sử dụng

| Thư viện / Tool          | Phiên bản | Mục đích                                    |
| ------------------------ |-----------| -------------------------------------------- |
| **Selenium Java**        | 4.47.0    | Tự động hóa trình duyệt web                 |
| **TestNG**               | 7.12.0    | Framework quản lý test case + cơ chế parallel |
| **Gson**                 | 2.14.0    | Đọc/ghi file JSON trung gian chia sẻ test data |
| **Apache POI**           | 5.5.1     | Đọc/ghi file Excel — lấy data cho test case  |
| **Apache POI OOXML**     | 5.5.1     | Hỗ trợ định dạng `.xlsx` + tô màu cell (`XSSF`) |
| **Commons IO**           | 2.22.0    | Tiện ích thao tác file, đi kèm khi dùng POI  |
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
│   │   │   └── ConfigData.java          # Hằng số dùng chung (URL, tài khoản, tên file JSON + đường dẫn file Excel)
│   │   ├── drivers/                     # 📌 Trọng tâm Bài 28
│   │   │   ├── DriverManager.java       # Giữ WebDriver theo ThreadLocal — mỗi luồng một driver riêng
│   │   │   └── ParameterManager.java    # Nguồn cấu hình duy nhất: -D > biến môi trường > properties > <parameter> XML
│   │   ├── helpers/                     # 📌 Trọng tâm Bài 29 & 30
│   │   │   ├── PropertiesHelper.java    # Load & đọc/ghi file .properties, chồng file môi trường lên file chung
│   │   │   ├── ExcelHelper.java         # 📌 Bài 30: đọc/ghi file Excel theo TÊN CỘT, tô màu cell Passed/Failed
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
│       │   └── Bai30_Excel_Data/              # 📌 Bài 30: lấy data test từ file Excel
│       │       ├── DemoExcelData.java         # Demo đọc cell theo tên cột + ghi STATUS có tô màu
│       │       ├── pages/                     # Copy nguyên từ Bài 28, KHÔNG sửa gì
│       │       │   ├── BasePage.java
│       │       │   ├── LoginPage.java
│       │       │   ├── DashboardPage.java
│       │       │   ├── CustomersPage.java
│       │       │   ├── ProjectsPage.java
│       │       │   └── TasksPage.java
│       │       └── testcases/
│       │           ├── LoginTest.java         # 8 TC Login — data lấy từ Excel thay vì hardcode
│       │           ├── DashboardTest.java     # 4 TC — giữ nguyên như Bài 28
│       │           ├── CustomersTest.java     # 3 TC — giữ nguyên như Bài 28
│       │           ├── ProjectsTest.java      # 2 TC — giữ nguyên như Bài 28
│       │           └── TasksTest.java         # 1 TC — giữ nguyên như Bài 28
│       │
│       └── resources/
│           ├── configs/                 # 📌 Bài 29: file cấu hình
│           │   ├── config.properties    # Cấu hình chung: env, browser, headless, đường dẫn file Excel, timeout...
│           │   ├── dev.properties       # Key riêng của môi trường dev (url, base.uri)
│           │   └── staging.properties   # Key riêng của môi trường staging (url, base.uri)
│           │
│           ├── suites/                  # TestNG Suite XML
│           │   ├── Suite_Bai28_DriverManager_ParallelExecution.xml   # Chạy POM song song trên 2 trình duyệt
│           │   ├── Suite_Bai29_PropertiesConfig.xml                  # Demo đọc config (suite mặc định trong pom.xml)
│           │   └── Suite_Bai30_Excel_Data.xml                        # LoginTest lấy data Excel, chạy song song Chrome + Edge
│           │
│           └── testdata/
│               ├── crm_data.xlsx            # 📌 Bài 30: data Login — EMAIL | PASSWORD | TEST_CASE_NAME | STATUS
│               ├── crm_customer_data.xlsx   # 📌 Bài 30: file Excel mẫu để tự thực hành thêm
│               ├── customer_data.json       # File JSON trung gian (tự sinh khi chạy test)
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

- **`getValue(key, defaultValue)`** trả về `defaultValue` khi key không tồn tại **hoặc để trống giá trị** (`report_path =`), tránh phải kiểm tra `null` rải rác khắp nơi.

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
