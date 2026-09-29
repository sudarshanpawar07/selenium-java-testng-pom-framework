package pages;

import extensions.UIHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CreateEmployeePage {

    private final WebDriver driver;

    @FindBy(id = "Name")
    private WebElement txtName;

    @FindBy(id = "Age")
    private WebElement txtAge;

    @FindBy(id = "Salary")
    private WebElement txtSalary;

    @FindBy(id = "DurationWorked")
    private WebElement txtDuration;

    @FindBy(id = "Grade")
    private WebElement drpGrade;

    @FindBy(id = "Email")
    private WebElement txtEmail;

    @FindBy(xpath = "//button[contains(text(),'Create Employee')]")
    private WebElement btnCreateEmployee;

    public CreateEmployeePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public EmployeeListPage createNewEmployee(String name, String age, String salary,
                                              String duration, String grade, String email) {
        UIHelper.enterText(driver, txtName, name);
        UIHelper.enterText(driver, txtAge, age);
        UIHelper.enterText(driver, txtSalary, salary);
        UIHelper.enterText(driver, txtDuration, duration);
        UIHelper.selectByVisibleText(driver, drpGrade, grade);
        UIHelper.enterText(driver, txtEmail, email);
        UIHelper.click(driver, btnCreateEmployee);
        return new EmployeeListPage(driver);
    }
}
