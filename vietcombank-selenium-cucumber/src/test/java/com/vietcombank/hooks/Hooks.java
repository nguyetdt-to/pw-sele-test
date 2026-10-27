package com.vietcombank.hooks;

import com.vietcombank.context.TestContext;
import com.vietcombank.driver.DriverFactory;
import com.vietcombank.utils.ScreenshotUtils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;

public class Hooks {

    private final TestContext testContext;

    public Hooks(TestContext testContext) {
        this.testContext = testContext;
    }

    /**
     * Before each scenario: create a new WebDriver and store it in TestContext.
     */
    @Before(order = 1)
    public void setUp(Scenario scenario) {
        System.out.println("\n=== Starting scenario: " + scenario.getName() + " ===");
        WebDriver driver = DriverFactory.createDriver();
        testContext.setDriver(driver);
    }

    /**
     * After each scenario: capture a screenshot on failure, then quit the driver.
     */
    @After(order = 1)
    public void tearDown(Scenario scenario) {
        WebDriver driver = testContext.getDriver();

        if (scenario.isFailed() && driver != null) {
            System.out.println("Scenario FAILED — capturing screenshot...");
            String safeName = scenario.getName().replaceAll("[^a-zA-Z0-9_\\-]", "_");
            String screenshotPath = ScreenshotUtils.captureScreenshot(driver, safeName);
            if (screenshotPath != null) {
                System.out.println("Screenshot saved: " + screenshotPath);
            }
        }

        DriverFactory.quitDriver(driver);
        System.out.println("=== Finished scenario: " + scenario.getName()
                + " | Status: " + scenario.getStatus() + " ===\n");
    }
}
