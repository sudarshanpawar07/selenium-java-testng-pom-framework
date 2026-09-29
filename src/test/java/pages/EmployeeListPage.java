package pages;

import extensions.UIHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class EmployeeListPage {

    private static final Duration TIMEOUT = Duration.ofSeconds(20);

    private final WebDriver driver;

    @FindBy(xpath = "//a[@href='/Employee/Create']")
    private WebElement lnkNewEmployee;

    @FindBy(css = "input[name='emailTerm']")
    private WebElement txtEmailSearch;

    @FindBy(css = "button.btn-search")
    private WebElement btnSearch;

    @FindBy(css = "span.emp-email")
    private List<WebElement> employeeEmails;

    @FindBy(css = "table tbody tr")
    private List<WebElement> employeeRows;

    public EmployeeListPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public CreateEmployeePage clickCreateEmployee() {
        UIHelper.click(driver, lnkNewEmployee);
        return new CreateEmployeePage(driver);
    }

    public boolean isEmployeeListDisplayed() {
        return driver.getCurrentUrl().contains("/Employee")
                || new WebDriverWait(driver, TIMEOUT)
                .until(ExpectedConditions.presenceOfElementLocated(By.tagName("table"))) != null;
    }

    /**
     * The list is paginated (5 rows per page), so the table alone cannot prove a
     * new record is present. Filter by the email first, then look for it.
     */
    public boolean isEmployeePresent(String email) {
        UIHelper.enterText(driver, txtEmailSearch, email);
        UIHelper.click(driver, btnSearch);
        try {
            return new WebDriverWait(driver, TIMEOUT).until(condition -> {
                try {
                    List<WebElement> emails = employeeEmails;
                    return emails.stream().anyMatch(el -> el.getText().trim().equalsIgnoreCase(email));
                } catch (StaleElementReferenceException e) {
                    return false;
                }
            });
        } catch (TimeoutException e) {
            return false;
        }
    }

    public int getEmployeeCount() {
        return employeeRows.size();
    }
}
