# Employee Management Automation

A Selenium + Java + TestNG automation framework for testing an employee management
application. The suite covers the login flow and employee creation using the Page
Object Model.

---

## Tech Stack

| Component | Version / Detail         |
| --------- | ------------------------ |
| Java      | 17 (source/target)       |
| Selenium  | 4.49.0 (`selenium-java`) |
| TestNG    | 7.10.2 (test scope)      |
| Maven     | 3.9                      |
| Surefire  | 3.5.2                    |
| IDE       | IntelliJ IDEA or Eclipse |

---

## Project Structure

```
seleniumjava/
├── pom.xml
├── README.md
└── src
    ├── main
    │   └── java/extensions/
    │       └── UIHelper.java          # Shared UI actions: waits, clicks, text entry
    └── test
        ├── java
        │   ├── gettingstarted/
        │   │   └── EmployeeTest.java  # Test class registered in testng.xml
        │   ├── pages/                 # Page Object Model
        │   │   ├── HomePage.java
        │   │   ├── LoginPage.java
        │   │   ├── EmployeeListPage.java
        │   │   └── CreateEmployeePage.java
        │   └── testdata/
        │       └── EmployeeData.java  # Test data holder for employee creation
        └── resources
            ├── config.properties      # URL, credentials, browser, email domain
            └── testng.xml             # TestNG suite definition
```

Page objects own their locators via `@FindBy` and expose methods that return the next
page object, so tests read as a linear user journey. `UIHelper` centralises the
low-level actions (waiting, clicking, entering text) so page objects do not handle
timing.

---

## Test Design

`EmployeeTest` contains two tests:

| Test             | Purpose                                                  |
| ---------------- | -------------------------------------------------------- |
| `testLogin`      | Verifies login succeeds and no error is displayed         |
| `createEmployee` | Creates an employee and verifies it appears in the list  |

Driver setup and teardown run around each test through `@BeforeMethod` and
`@AfterMethod`.

---

## DataProvider

Employee creation uses a TestNG `@DataProvider` so test data is defined in one place
instead of inline in the test method. `EmployeeData` holds one employee's data.

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

The provider supplies the method with an `EmployeeData` object:

```java
@Test(dataProvider = "employeeData")
public void createEmployee(EmployeeData employee) { ... }
```

`EmployeeData` is immutable — final fields set through a constructor with getters only.
Adding a row to the provider adds a new test invocation.

---

## Unique Email Generation

The application is shared, so each created employee needs a distinct email or later
searches would match an earlier record. The email is built at test execution time:

```java
String uniqueEmail = employee.getEmailPrefix() + "."
        + UUID.randomUUID()
        + "@" + emailDomain;
```

`UUID.randomUUID()` provides enough randomness that collisions are not a practical
concern.

The UUID is generated **inside the test method**, not inside the `@DataProvider`.
Data provider methods are evaluated once when the suite starts, so a UUID created
there would give every row the same email. Generating it in the test body produces a
fresh value on each run.

---

## Configuration

Environment-specific values are kept out of the Java source and read at runtime by
`EmployeeTest.loadConfig()`.

Two files back this up:

| File                                     | Purpose                                    |
| ---------------------------------------- | ------------------------------------------ |
| `src/test/resources/config.properties`   | Local configuration — gitignored           |
| `src/test/resources/config.properties.example` | Committed template with blank credentials |

Supported keys:

| Key           | Purpose                         |
| ------------- | ------------------------------- |
| `url`         | Application URL under test      |
| `username`    | Login username                  |
| `password`    | Login password                  |
| `browser`     | Browser to run                  |
| `emailDomain` | Domain used in generated emails |

## Getting Started

Copy the example file and add your local credentials:

```bash
cp src/test/resources/config.properties.example src/test/resources/config.properties
```

`config.properties` is gitignored and is not committed, so local credentials stay out
of the repository. The committed example file leaves `username` and `password` empty.
Only `url`, `browser`, and `emailDomain` carry default values.

Run the suite with `mvn test` from the project root.

---

## Running Tests

```bash
mvn test
```

Surefire is configured to use `src/test/resources/testng.xml`, so `mvn test` runs the
suite defined there. Maven also writes its default run output under `target/`.

Individual tests can be run from the IDE:

- **IntelliJ IDEA** — use the gutter icons next to a `@Test` method.
- **Eclipse** — right-click the test class or `testng.xml` and choose
  **Run As > TestNG Suite**.

---

## Current Scope and Limitations

- **No cleanup flow.** Created employee records remain in the application permanently,
  so the list grows with each run. A delete or teardown step is not yet implemented.
- **Driver setup is local to the test class.** `EmployeeTest` creates and quits its own
  `ChromeDriver` in `@BeforeMethod` / `@AfterMethod`. There is no shared base class or
  driver factory, so additional test classes will need to repeat this setup, and only
  Chrome is currently used.
- **`browser` is not yet read.** The configuration key exists, but the driver setup
  creates Chrome directly rather than using that value.