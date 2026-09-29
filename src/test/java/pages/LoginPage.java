package pages;

import extensions.UIHelper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {

    private final WebDriver driver;

    @FindBy(name = "UserName")
    private WebElement txtUsername;

    @FindBy(id = "Password")
    private WebElement txtPassword;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement btnLogin;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public HomePage performLogin(String username, String password) {
        UIHelper.enterText(driver, txtUsername, username);
        UIHelper.enterText(driver, txtPassword, password);
        UIHelper.click(driver, btnLogin);
        return new HomePage(driver);
    }
}
