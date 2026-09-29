package pages;

import extensions.UIHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage {

    private final WebDriver driver;

    @FindBy(linkText = "Login")
    private WebElement lnkLogin;

    @FindBy(xpath = "//nav//a[@href='/Employee']")
    private WebElement lnkEmployeeList;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public LoginPage clickLogin() {
        UIHelper.click(driver, lnkLogin);
        return new LoginPage(driver);
    }

    public EmployeeListPage clickEmployeeList() {
        UIHelper.click(driver, lnkEmployeeList);
        return new EmployeeListPage(driver);
    }
}
