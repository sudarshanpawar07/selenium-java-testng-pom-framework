package pages;

import extensions.UIHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CreateEmployeePage {

    private final WebDriver driver;

    private static final By TXT_NAME = By.id("Name");
    private static final By TXT_AGE = By.id("Age");
    private static final By TXT_SALARY = By.id("Salary");
    private static final By TXT_DURATION = By.id("DurationWorked");
    private static final By TXT_GRADE = By.id("Grade");
    private static final By TXT_EMAIL = By.id("Email");
    private static final By BTN_CREATE_EMP = By.xpath("//button[contains(text(),'Create Employee')]");

    public CreateEmployeePage(WebDriver driver) {
        this.driver = driver;
    }

    public EmployeeListPage createNewEmployee(String name, String age, String salary,
                                               String exp, String grade, String email) {
        UIHelper.enterText(driver, TXT_NAME, name);
        UIHelper.enterText(driver, TXT_AGE, age);
        UIHelper.enterText(driver, TXT_SALARY, salary);
        UIHelper.enterText(driver, TXT_DURATION, exp);
        UIHelper.enterText(driver, TXT_GRADE, grade);
        UIHelper.enterText(driver, TXT_EMAIL, email);
        UIHelper.scrollToElement(driver, BTN_CREATE_EMP);
        UIHelper.click(driver, BTN_CREATE_EMP);
        return new EmployeeListPage(driver);
    }
}
