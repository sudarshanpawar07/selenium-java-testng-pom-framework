package listeners;

import gettingstarted.EmployeeTest;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

/**
 * Basic TestNG listener. TestNG calls each method below automatically at the matching
 * moment in a test's lifecycle, so there is no need to call them from the test class.
 *
 * <p>On failure it attaches a screenshot to the Allure report. The screenshot is kept
 * in memory as bytes and handed straight to Allure, so no image file is ever written
 * to the project.
 */
public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("TEST STARTED: " + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("TEST PASSED: " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("TEST FAILED: " + result.getName());

        // This is the whole point of this step: does the listener get the driver's driver?
        WebDriver driver = getDriver(result);

        if (driver == null) {
            System.out.println("DRIVER AVAILABLE: false");
            return;
        }

        System.out.println("DRIVER AVAILABLE: true");
        System.out.println("DRIVER CLASS: " + driver.getClass().getName());
        System.out.println("CURRENT URL: " + driver.getCurrentUrl());
        System.out.println("SESSION ID: " + ((org.openqa.selenium.remote.RemoteWebDriver) driver).getSessionId());

        attachScreenshot(driver);
    }

    /**
     * Captures the failure state and attaches it to the Allure report.
     * OutputType.BYTES returns the PNG in memory, so nothing is written to disk.
     */
    private void attachScreenshot(WebDriver driver) {
        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            byte[] image = screenshot.getScreenshotAs(OutputType.BYTES);

            Allure.addAttachment("Failure Screenshot", "image/png",
                    new ByteArrayInputStream(image), ".png");
            System.out.println("  Allure attachment added: Failure Screenshot ("
                    + image.length + " bytes)");
        } catch (Exception e) {
            // A failed screenshot must never hide the original test failure.
            System.out.println("  Screenshot could not be attached: " + e.getMessage());
        }
    }

    /**
     * ITestResult carries the test object that was running, so the listener can reach
     * that test's own driver instead of guessing which driver to use.
     */
    private WebDriver getDriver(ITestResult result) {
        Object instance = result.getInstance();
        if (instance instanceof EmployeeTest) {
            return ((EmployeeTest) instance).getDriver();
        }
        return null;
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("TEST SKIPPED: " + result.getName());
    }
}