package gettingstarted;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CreateEmployeePage;
import pages.EmployeeListPage;
import pages.HomePage;
import pages.LoginPage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmployeeTest {

    private static final String CONFIG_FILE = "config.properties";

    private WebDriver driver;
    private String url;
    private String username;
    private String password;

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

    @Test
    public void testLoginAndCreateEmployee() {
        HomePage homePage = new HomePage(driver);

        LoginPage loginPage = homePage.clickLogin();
        HomePage loggedInPage = loginPage.performLogin(username, password);
        Assert.assertFalse(driver.getPageSource().contains("Invalid login attempt"),
                "Login should succeed without error");

        EmployeeListPage employeeListPage = loggedInPage.clickEmployeeList();
        CreateEmployeePage createEmployeePage = employeeListPage.clickCreateEmployee();

        String uniqueEmail = "test" + System.currentTimeMillis() + "@gmail.com";
        EmployeeListPage createdListPage = createEmployeePage.createNewEmployee(
                "Sudarshan Pawar", "22", "50000", "20", "Junior", uniqueEmail);

        Assert.assertTrue(createdListPage.isEmployeeListDisplayed(),
                "Should navigate to employee list after creation");
        Assert.assertTrue(createdListPage.isEmployeePresent(uniqueEmail),
                "Newly created employee should appear in the list");
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
    }
}
