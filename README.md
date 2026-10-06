# Selenium Java TestNG POM Framework

A Selenium WebDriver automation framework built with **Java**, **TestNG**, **Maven**, and
the **Page Object Model**. It automates an Employee Management web application and
demonstrates reusable UI automation, data-driven testing, TestNG suite execution,
custom listeners, automatic failure screenshots, and Allure reporting.

The framework is intentionally simple and readable. Each layer has one clear
responsibility, so a new test can be added by writing a test class and (if needed) a
page object — with no changes to the base infrastructure.

---

## 🚀 Tech Stack

| Technology       | Purpose                                                      |
| ---------------- | ------------------------------------------------------------ |
| Java             | Programming language (source/target 17)                     |
| Selenium WebDriver | Browser automation                                        |
| TestNG           | Test execution, assertions, data providers, listeners        |
| Maven            | Build and dependency management                             |
| Page Object Model | Maintainable page architecture                             |
| Allure           | Test reporting and failure screenshot attachment             |
| Git/GitHub       | Version control                                             |

Key versions:

| Component        | Version                    |
| ---------------- | -------------------------- |
| Selenium         | `4.49.0` (`selenium-java`) |
| TestNG           | `7.10.2`                   |
| Allure (results) | `2.29.0` (`allure-testng`) |
| Allure (plugin)  | `2.18.0` (`allure-maven`)  |
| Maven Surefire   | `3.5.2`                    |
| Maven            | `3.9.x`                    |

---

## 📁 Project Structure

```
selenium-java-testng-pom-framework/
├── pom.xml
├── README.md
├── .gitignore
│
└── src/
    ├── main/
    │   └── java/
    │       └── extensions/
    │           └── UIHelper.java          # Shared UI actions: waits, clicks, text entry
    │
    └── test/
        ├── java/
        │   ├── gettingstarted/
        │   │   └── EmployeeTest.java      # Test class registered in testng.xml
        │   ├── listeners/
        │   │   └── TestListener.java      # ITestListener: lifecycle logging + failure screenshot
        │   ├── pages/                     # Page Object Model
        │   │   ├── HomePage.java
        │   │   ├── LoginPage.java
        │   │   ├── EmployeeListPage.java
        │   │   └── CreateEmployeePage.java
        │   └── testdata/
        │       └── EmployeeData.java      # Test data holder for employee creation
        │
        └── resources/
            ├── config.properties.example  # Committed template with blank credentials
            ├── config.properties          # Local config — gitignored, not committed
            └── testng.xml                 # TestNG suite definition and listener registration
```

Page objects own their locators via `@FindBy` and expose methods that return the next
page object, so tests read as a linear user journey. `UIHelper` centralises the
low-level actions (waiting, clicking, entering text) so page objects do not handle
timing.

---

## 🧱 Framework Architecture

```
Test Class
    ↓
Page Object
    ↓
UIHelper
    ↓
Selenium WebDriver
```

| Layer              | Responsibility                                                                                                                                   |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Test Class**     | Describes *what* to verify using TestNG annotations and assertions. Holds no locators and performs no low-level browser work.                     |
| **Page Object**    | Encapsulates *where* elements are on the page. Owns all `@FindBy` locators and exposes intent-revealing actions that return the next page object. |
| **UIHelper**       | Encapsulates *how* an element is interacted with. Centralises waits, clicks, scrolling, text entry, and dropdown selection.                          |
| **Selenium WebDriver** | Executes browser commands against the application under test.                                                                               |

**Tests never touch locators directly.** A test calls a page object action; the page
object resolves its element and delegates the mechanics to `UIHelper`, which handles
timing (via `WebDriverWait`) so no page object or test has to think about waits.

`UIHelper` also handles `ElementClickInterceptedException` and
`StaleElementReferenceException` by retrying through `JavascriptExecutor`, which keeps
interaction logic in exactly one place.

Available `UIHelper` actions: `enterText`, `click`, `selectByVisibleText`,
`selectByValue`, `selectByIndex`, `scrollToElement`, `getText`.

---

## 🧪 TestNG Features Implemented

Only features actually present in this project are listed below.

| Feature                          | How it is used                                                                             |
| -------------------------------- | ------------------------------------------------------------------------------------------ |
| `@Test`                          | Marks test methods (`testLogin`, `createEmployee`).                                        |
| `@BeforeMethod`                  | Creates a fresh `ChromeDriver`, maximizes the window, and loads the application URL.       |
| `@AfterMethod`                   | Quits the driver after every test, guaranteeing browser isolation between tests.            |
| `@DataProvider`                  | Supplies `EmployeeData` objects to `createEmployee`, enabling data-driven test execution. |
| Assertions                       | `TestNG Assert` is used to verify login success and that a created employee appears in the list. |
| `ITestListener`                  | `TestListener` implements TestNG's listener interface to hook into test lifecycle events.  |
| TestNG XML suite configuration   | `testng.xml` defines the suite, its classes, and registers the listener.                   |

---

## 📊 Allure Reporting

Allure TestNG integration is added through a single test-scoped dependency. The
`allure-testng` library registers its own `ITestNGListener` through
`META-INF/services/org.testng.ITestNGListener`, so **no `testng.xml` change is needed**
and it coexists with the project's own `TestListener`.

Flow:

```
mvn test
    ↓
Allure TestNG listener (auto-registered)
    ↓
allure-results/          ← JSON result files
    ↓
Allure Maven plugin (mvn allure:report)
    ↓
target/site/allure-maven-plugin/   ← static HTML report
```

**What the report currently captures** (all automatic, no annotations required):

| Information           | Description                                                      |
| --------------------- | ---------------------------------------------------------------- |
| Test name             | The `@Test` method name, plus fully-qualified name               |
| Pass / fail status    | Result of each test execution                                     |
| Test duration         | Calculated from recorded start and stop timestamps                |
| Execution information | Suite name, class, method, host, thread, framework, language      |
| Fixture timings       | `@BeforeMethod` / `@AfterMethod` are recorded as timed fixtures  |
| Stack trace           | Full exception trace for failed tests                             |

Generated Allure artifacts are ignored by Git (`.gitignore` covers `allure-results/`,
`.allure/`, and `target/`).

---

## 📸 Failure Screenshot

When a test fails, a screenshot of the failure state is captured and attached to the
Allure report automatically:

```
Test Failure
    ↓
TestListener.onTestFailure()
    ↓
ITestResult.getInstance()  →  failed test instance
    ↓
EmployeeTest.getDriver()   →  WebDriver of that failed test
    ↓
TakesScreenshot  →  getScreenshotAs(OutputType.BYTES)
    ↓
Allure.addAttachment("Failure Screenshot", "image/png", ...)
```

**How the WebDriver is reached:** `ITestResult` carries the test object that was
running, so the listener obtains the driver belonging to the *specific* test that
failed rather than guessing. `onTestFailure()` fires before `@AfterMethod`, so the
browser session is still alive at capture time.

**No permanent screenshot files are created.** The capture uses `OutputType.BYTES`, so
the PNG is held in memory and handed straight to Allure as a byte stream. Nothing is
written into the project directory, and there is no screenshot cleanup to maintain.

The image appears in the **Attachments** tab of the failed test in the Allure report.

---

## 🧪 Current Test Coverage

The project currently contains **two** test methods:

| Test             | Purpose                                                                                          |
| ---------------- | ------------------------------------------------------------------------------------------------ |
| `testLogin`      | Logs in with configured credentials and verifies the application does not display an invalid-login error. |
| `createEmployee` | Creates an employee record and verifies the application navigates to the employee list and that the new record is present. |

This is a learning and portfolio framework. The suite is intentionally small and can
be extended with additional test cases by adding methods to the test class and, where
needed, new page objects.

---

## 🗂️ Test Data

Employee creation uses a TestNG `@DataProvider` so test data is defined in one place
instead of being inlined in the test method.

```java
@DataProvider(name = "employeeData")
public Object[][] employeeData() {
    return new Object[][] {
        { new EmployeeData(
            "Sudarshan Pawar",
            "22",
            "50000",
            "20",
            "Junior",
            "sudarshan"
        )}
    };
}
```

The provider supplies the test method with an `EmployeeData` object:

```java
@Test(dataProvider = "employeeData")
public void createEmployee(EmployeeData employee) { ... }
```

`EmployeeData` is **immutable** — final fields set through a constructor with getters
only. Adding another row to the provider adds another test invocation, which is what
makes this data-driven.

### Unique Email Generation

The application under test is shared, so each created employee needs a distinct email;
otherwise later searches would match an earlier record. The email is built at test
execution time:

```java
String uniqueEmail = employee.getEmailPrefix() + "."
        + UUID.randomUUID()
        + "@" + emailDomain;
```

The UUID is generated **inside the test method**, not inside the `@DataProvider`.
Data provider methods are evaluated once when the suite starts, so a UUID created
there would give every row the same email. Generating it in the test body produces a
fresh value on each run.

---

## ⚙️ Configuration

Environment-specific values are kept out of the Java source and read at runtime by
`EmployeeTest.loadConfig()`.

| File                                         | Purpose                                       |
| -------------------------------------------- | --------------------------------------------- |
| `src/test/resources/config.properties`       | Local configuration — **gitignored**, never committed |
| `src/test/resources/config.properties.example` | Committed template with blank credentials   |

Supported keys:

| Key           | Purpose                          |
| ------------- | -------------------------------- |
| `url`         | Application URL under test       |
| `username`    | Login username                   |
| `password`    | Login password                   |
| `browser`     | Browser to run                   |
| `emailDomain` | Domain used in generated emails  |

The committed example contains **no credentials**:

```properties
url=https://eaapp.somee.com/
username=
password=
browser=chrome
emailDomain=example.com
```

To set up a local environment, copy the template and fill in your own credentials:

```bash
cp src/test/resources/config.properties.example src/test/resources/config.properties
```

`config.properties` is listed in `.gitignore`, so local credentials stay out of the
repository. The application URL is already present in the committed example — only
`username` and `password` need to be supplied.

---

## ▶️ How to Run

Surefire is configured with `src/test/resources/testng.xml`, so `mvn test` runs the
suite defined there.

### Run tests

```bash
mvn test
```

### Clean and run tests

```bash
mvn clean test
```

### Generate Allure report

```bash
mvn allure:report
```

Builds the HTML report from the results already present in `allure-results/`.

### Recommended full verification

```bash
mvn clean verify
```

Runs the full lifecycle — tests, result generation, and HTML report — in one command.

Individual tests can also be run from an IDE:

- **IntelliJ IDEA** — use the gutter icons next to a `@Test` method.
- **Eclipse** — right-click the test class or `testng.xml` and choose
  **Run As > TestNG Suite**.

---

## 📊 View Allure Report

The generated report is a static site located at:

```
target/site/allure-maven-plugin/
```

**Serve it over a local HTTP server rather than opening `index.html` directly.**

```bash
cd target/site/allure-maven-plugin
python3 -m http.server 8765
```

Then open:

```
http://localhost:8765
```

**Why a server is needed:** `index.html` is only a small loader shell. The actual test
data is fetched by `app.js` using `XMLHttpRequest` / `fetch()`. When the report is
opened via the `file://` protocol, browsers treat those requests as cross-origin and
block them, so the report remains stuck on a loading spinner. Serving the folder over
HTTP resolves this. The report requires no internet connection once generated.

---

## 🔍 Framework Highlights

- **Page Object Model** — all locators encapsulated in dedicated page classes
- **Reusable UI helper methods** — waits, clicks, scrolling, text entry, and dropdowns centralized in `UIHelper`
- **Data-driven testing** — `@DataProvider` supplies `EmployeeData` objects to tests
- **Assertions** — TestNG assertions verify login and record creation
- **Browser isolation per test** — a fresh `ChromeDriver` for every test via `@BeforeMethod` / `@AfterMethod`
- **TestNG listener** — `ITestListener` implementation for lifecycle visibility
- **Failure screenshot capture** — screenshot attached automatically to the Allure report
- **In-memory screenshots** — no screenshot files written into the project
- **Allure reporting** — status, duration, execution info, and failure attachments
- **Maven test execution** — suite driven through `testng.xml` via Surefire
- **Externalized configuration** — environment values kept out of source and out of Git
- **Git/GitHub version control** — credentials and generated artifacts excluded

---

## 🛠️ Future Enhancements

*The following are ideas for future work. None of these are implemented in the current
version of this project.*

- REST Assured API automation and API + UI integration
- `ThreadLocal<WebDriver>` for thread-safe driver access under parallel execution
- Cross-browser execution (Firefox, Edge) driven by the `browser` config key
- TestNG groups and parallel suite configuration
- CI/CD pipeline integration (Jenkins / GitHub Actions)
- Advanced logging with log file rotation
- Test retry mechanism for known flaky tests
- Performance testing integration
- Shared base test class and driver factory to remove per-class driver setup

---

## 👨‍💻 Author

**Sudarshan Pawar**

GitHub: [https://github.com/sudarshanpawar07](https://github.com/sudarshanpawar07)

Repository: [selenium-java-testng-pom-framework](https://github.com/sudarshanpawar07/selenium-java-testng-pom-framework)
