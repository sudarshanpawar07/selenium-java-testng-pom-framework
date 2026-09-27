package gettingstarted;

import pages.CreateEmployeePage;
import pages.EmployeeListPage;
import pages.HomePage;
import pages.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class EmployeeTest {

    private WebDriver driver;
    private static final String BASE_URL = "https://eaapp.somee.com/";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get(BASE_URL);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLoginAndCreateEmployee() {
        HomePage homePage = new HomePage(driver);

        LoginPage loginPage = homePage.clickLogin();
        HomePage loggedInPage = loginPage.performLogin(USERNAME, PASSWORD);
        Assert.assertFalse(driver.getPageSource().contains("Invalid login attempt"),
                "Login should succeed without error");

        EmployeeListPage employeeListPage = loggedInPage.clickEmployeeListPage();
        CreateEmployeePage createEmployeePage = employeeListPage.clickCreateEmployee();
        String uniqueEmail = "test" + System.currentTimeMillis() + "@gmail.com";
        createEmployeePage.createNewEmployee(
                "Sudarshan Pawar", "22", "50000", "20", "Junior", uniqueEmail);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("Employee"),
                ExpectedConditions.presenceOfElementLocated(By.tagName("table"))
        ));
        Assert.assertTrue(driver.getCurrentUrl().contains("Employee") || driver.findElements(By.tagName("table")).size() > 0,
                "Should navigate to employee list after creation");
    }
}
