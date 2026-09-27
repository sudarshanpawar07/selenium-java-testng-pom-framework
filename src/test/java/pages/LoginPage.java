package pages;

import extensions.UIHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {

    private final WebDriver driver;

    private static final By TXT_USERNAME = By.name("UserName");
    private static final By TXT_PASSWORD = By.id("Password");
    private static final By BTN_LOGIN = By.xpath("//button[@type='submit']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public HomePage performLogin(String userName, String password) {
        UIHelper.enterText(driver, TXT_USERNAME, userName);
        UIHelper.enterText(driver, TXT_PASSWORD, password);
        UIHelper.click(driver, BTN_LOGIN);
        return new HomePage(driver);
    }
}
