package extensions;

import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Shared low-level UI actions. Every action waits for the element first so that
 * page objects never have to think about timing.
 *
 * <p>Methods take a {@link WebDriver} as well as the {@link WebElement} because
 * {@link org.openqa.selenium.support.PageFactory} hands out lazy proxies: the element
 * is only looked up again every time a method is called on it, so a wait must be
 * created from the driver each time.
 */
public final class UIHelper {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(20);

    private UIHelper() {
        throw new AssertionError("UIHelper is a static utility, do not instantiate it");
    }

    private static WebDriverWait wait(WebDriver driver) {
        return new WebDriverWait(driver, DEFAULT_TIMEOUT);
    }

    public static void enterText(WebDriver driver, WebElement element, String value) {
        wait(driver).until(ExpectedConditions.visibilityOf(element));
        element.click();
        element.clear();
        element.sendKeys(value);
    }

    public static void click(WebDriver driver, WebElement element) {
        scrollToElement(driver, element);
        WebElement target = wait(driver).until(ExpectedConditions.elementToBeClickable(element));
        try {
            target.click();
        } catch (ElementClickInterceptedException | StaleElementReferenceException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", target);
        }
    }

    public static void selectByVisibleText(WebDriver driver, WebElement element, String text) {
        wait(driver).until(ExpectedConditions.visibilityOf(element));
        new Select(element).selectByVisibleText(text);
    }

    public static void selectByValue(WebDriver driver, WebElement element, String value) {
        wait(driver).until(ExpectedConditions.visibilityOf(element));
        new Select(element).selectByValue(value);
    }

    public static void selectByIndex(WebDriver driver, WebElement element, int index) {
        wait(driver).until(ExpectedConditions.visibilityOf(element));
        new Select(element).selectByIndex(index);
    }

    public static void scrollToElement(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", element);
    }

    public static String getText(WebDriver driver, WebElement element) {
        return wait(driver).until(ExpectedConditions.visibilityOf(element)).getText();
    }
}
