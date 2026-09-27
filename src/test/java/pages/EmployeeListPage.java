package pages;

import extensions.UIHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class EmployeeListPage {

    private final WebDriver driver;

    private static final By LNK_EMPLOYEE_LIST = By.xpath("//a[contains(text(),'Employees')]");
    private static final By LNK_NEW_EMP = By.xpath("//a[contains(text(),'+ New Employee')]");

    public EmployeeListPage(WebDriver driver) {
        this.driver = driver;
    }

    public HomePage performClickEmpList() {
        UIHelper.click(driver, LNK_EMPLOYEE_LIST);
        return new HomePage(driver);
    }

    public CreateEmployeePage clickCreateEmployee() {
        UIHelper.scrollToElement(driver, LNK_NEW_EMP);
        UIHelper.click(driver, LNK_NEW_EMP);
        return new CreateEmployeePage(driver);
    }
}
