package pages;

import extensions.UIHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage {

    private final WebDriver driver;

    private static final By LNK_LOGIN = By.linkText("Login");
    private static final By LNK_EMPLOYEE_LIST = By.xpath("//a[contains(text(),'Employees')]");
    private static final By LNK_NEW_EMP = By.xpath("//a[contains(text(),'+ New Employee')]");

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    public LoginPage clickLogin() {
        UIHelper.scrollToElement(driver, LNK_LOGIN);
        UIHelper.click(driver, LNK_LOGIN);
        return new LoginPage(driver);
    }

    public EmployeeListPage clickEmployeeListPage() {
        UIHelper.scrollToElement(driver, LNK_EMPLOYEE_LIST);
        UIHelper.click(driver, LNK_EMPLOYEE_LIST);
        return new EmployeeListPage(driver);
    }

    public CreateEmployeePage clickCreateEmp() {
        UIHelper.scrollToElement(driver, LNK_NEW_EMP);
        UIHelper.click(driver, LNK_NEW_EMP);
        return new CreateEmployeePage(driver);
    }
}
