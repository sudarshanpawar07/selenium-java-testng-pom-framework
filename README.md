# Selenium Java TestNG Framework

A Selenium WebDriver + TestNG automation framework, built incrementally. The target
application is the **EA Employee Management** demo app, and the tests cover the login
flow and employee creation.

This framework is a work in progress and is being improved day by day.

---

## Tech Stack

| Component    | Version / Detail                          |
| ------------ | ----------------------------------------- |
| Java         | 17 (source/target)                        |
| Selenium     | 4.49.0 (`selenium-java`)                  |
| TestNG       | 7.10.2 (test scope)                       |
| SLF4J        | 2.0.13 `slf4j-simple` (test scope)        |
| Surefire     | 3.5.2                                     |
| IDE          | Eclipse (Maven project)                   |
| Build tool   | Maven 3.9                                 |

---

## Project Structure

```
seleniumjava/
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java
    │   │   └── extensions/
    │   │       ├── UIHelper.java          # Shared low-level UI actions (waits, clicks, input)
    │   │       └── TestNGTest.java        # Scratch/experiment file (see Known Issues)
    │   └── resources/                     # Empty
    └── test
        ├── java
        │   ├── gettingstarted/
        │   │   └── EmployeeTest.java      # The test class driven by testng.xml
        │   └── pages/
        │       ├── HomePage.java
        │       ├── LoginPage.java
        │       ├── EmployeeListPage.java
        │       └── CreateEmployeePage.java
        └── resources
            ├── config.properties          # URL, credentials, browser
            └── testng.xml                 # TestNG suite definition
```

**Design:** Page Object Model. Each page object owns its locators (`@FindBy`) and
exposes intent-named methods that return the next page object, so a test reads as a
linear user journey with no locators leaking into it.

---

## How to Run

```bash
# From the project root
mvn clean test
```

Surefire is wired to `src/test/resources/testng.xml`, so `mvn test` always runs the
suite defined there. To run in the Eclipse IDE, right-click the test class or the
`testng.xml` file and choose **Run As > TestNG Suite**.

---

## Configuration

All environment-specific values live in `src/test/resources/config.properties`:

```properties
url=https://eaapp.somee.com/
username=admin
password=password
browser=chrome
```

`EmployeeTest.loadConfig()` reads this file from the classpath at runtime, so no value
is hardcoded in the Java source. Changing the target environment means editing only
this file.

---

## What Has Been Built So Far

Progress is tracked in the commit history on `main`:

| Commit    | What it added                                                                |
| --------- | ---------------------------------------------------------------------------- |
| `1408085` | Initial Selenium automation framework setup — Maven project, TestNG, Surefire |
| `b41261c` | Page Object Model using `@FindBy` annotations and `PageFactory`              |
| `05f64eb` | TestNG framework improvements using various annotations                      |

### Current test coverage

One end-to-end test, `EmployeeTest.testLoginAndCreateEmployee()`, which:

1. Launches Chrome, maximises the window, and opens the configured URL (`@BeforeMethod`)
2. Navigates Home → Login and signs in with the configured credentials
3. Asserts the login did **not** produce an "Invalid login attempt" error
4. Opens the Employee List, then the Create Employee form
5. Fills in name, age, salary, duration worked, grade (dropdown) and a unique email
   generated from `System.currentTimeMillis()`
6. Asserts the list page is displayed after creation
7. Searches by that email and asserts the new record is present in the table
8. Quits the driver in a null-guarded `@AfterMethod`

### Framework decisions worth knowing

- **`UIHelper` (src/main/java/extensions/UIHelper.java)** centralises every low-level
  action so page objects never deal with timing. Every action waits for its element
  first, using a 20 second default timeout.
- **Waits are built from the driver, not cached.** `PageFactory` hands out lazy proxies,
  so a `WebDriverWait` has to be created fresh from the driver on each call.
- **Click has a JavaScript fallback.** `UIHelper.click` catches
  `ElementClickInterceptedException` and `StaleElementReferenceException` and retries via
  `JavascriptExecutor`, which makes clicks survive overlays and re-rendered DOM nodes.
- **Pagination is handled explicitly.** The employee list shows 5 rows per page, so
  `EmployeeListPage.isEmployeePresent()` filters by email before searching, and treats
  `StaleElementReferenceException` as "not yet" inside the wait rather than a failure.
- **Unique test data.** The email is timestamped, so the test can be run repeatedly
  against the same shared demo instance without colliding with a previous run.
- **Driver cleanup is defensive.** `tearDown()` null-checks before quitting, so a failure
  during setup does not turn into a second failure during teardown.

---

## Known Issues

- **`TestNGTest.java` does not compile as written.** It has two problems:
  1. It imports `org.testng.Test`, which is a *class*, not the annotation. The annotation
     is `org.testng.annotations.Test`.
  2. It lives in `src/main/java`, but TestNG is declared with `<scope>test</scope>` in
     the pom. Test-scoped jars are not on the main compile classpath, so `org.testng.*`
     cannot be resolved from `src/main/java` at all.

  Fix: move the file under `src/test/java` and change the import to
  `org.testng.annotations.Test`. It is currently only a scratch file.

- **Credentials are committed to the repo.** `config.properties` holds a real username
  and password and is not gitignored. For a public demo app with default credentials this
  is low risk, but the right pattern is to keep `config.properties` local (gitignore it),
  commit a `config.properties.example` with blanks, and pull real values from environment
  variables or `-D` system properties in CI.

- **Eclipse runtime mismatch.** `.classpath` points at a `JavaSE-11` container while
  `pom.xml` targets Java 17. The build works from the command line, but the IDE may use a
  different JDK than Maven does.

---

## Next Steps

Planned improvements as the framework develops:

- [ ] Fix `TestNGTest.java` (move to `src/test/java`, fix the import)
- [ ] Move credentials out of version control into environment variables / system properties
- [ ] Align the Eclipse JDK container with the pom's Java 17
- [ ] Add `ITestListener` / a base class to capture screenshots and logs on failure
- [ ] Replace hardcoded `@BeforeMethod` / `@AfterMethod` driver setup with a BaseTest class
- [ ] Add more employee lifecycle tests (edit, delete, validation errors, search edge cases)
- [ ] Parameterise tests with TestNG `@DataProvider` instead of inline values
- [ ] Add cross-browser execution (Firefox, Edge) via a driver factory
- [ ] Add a Maven `testng.xml` group structure (smoke / regression) and CI integration
- [ ] Set up reporting (Surefire reports, optionally Allure)
