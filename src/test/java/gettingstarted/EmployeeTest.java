package gettingstarted;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.CreateEmployeePage;
import pages.EmployeeListPage;
import pages.HomePage;
import pages.LoginPage;
import testdata.EmployeeData;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

public class EmployeeTest {

    private static final String CONFIG_FILE = "config.properties";

    private WebDriver driver;
    private String url;
    private String username;
    private String password;
    private String emailDomain;

    @BeforeMethod
    public void setUp() throws IOException {
        loadConfig();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(url);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    @DataProvider(name = "employeeData")
    public Object[][] employeeData() {
        return new Object[][]{
                {
                        new EmployeeData(
                                "Sudarshan Pawar",
                                "22",
                                "50000",
                                "20",
                                "Junior",
                                "sudarshan"
                        )
                }
        };
    }

    /**
     * Gives TestNG listeners access to the driver of this test instance.
     * A listener lives in another package, so it needs a public way in.
     */
    public WebDriver getDriver() {
        return driver;
    }

    @Test
    public void testLogin() {
        HomePage homePage = new HomePage(driver);

        LoginPage loginPage = homePage.clickLogin();
        HomePage loggedInPage = loginPage.performLogin(username, password);
        Assert.assertFalse(driver.getPageSource().contains("Invalid login attempt"),
                "Login should succeed without error");
    }

    @Test(dataProvider = "employeeData")
    public void createEmployee(EmployeeData employee) {
        String uniqueEmail = employee.getEmailPrefix() + "."
                + UUID.randomUUID()
                + "@" + emailDomain;

        HomePage loggedInPage = login();

        EmployeeListPage employeeListPage = loggedInPage.clickEmployeeList();
        CreateEmployeePage createEmployeePage = employeeListPage.clickCreateEmployee();
        EmployeeListPage createdListPage =
                createEmployeePage.createNewEmployee(employee, uniqueEmail);

        Assert.assertTrue(createdListPage.isEmployeeListDisplayed(),
                "Should navigate to employee list after creation");
        Assert.assertTrue(createdListPage.isEmployeePresent(uniqueEmail),
                "Newly created employee should appear in the list");
    }

    private HomePage login() {
        HomePage homePage = new HomePage(driver);
        LoginPage loginPage = homePage.clickLogin();
        return loginPage.performLogin(username, password);
    }

    private void loadConfig() throws IOException {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new IOException("Missing " + CONFIG_FILE + " on the test classpath");
            }
            props.load(in);
        }
        url = props.getProperty("url");
        username = props.getProperty("username");
        password = props.getProperty("password");
        emailDomain = props.getProperty("emailDomain");
    }
}
