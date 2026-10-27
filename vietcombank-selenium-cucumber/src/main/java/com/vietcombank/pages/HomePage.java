package com.vietcombank.pages;

import com.vietcombank.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HomePage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String baseUrl;

    private static final By COOKIE_ACCEPT_BTN_XPATH =
            By.xpath("//button[contains(normalize-space(),'Đồng ý')] | "
                    + "//button[contains(normalize-space(),'Chấp nhận')]");

    private static final By SEARCH_TOGGLE_CSS =
            By.xpath("////h5[contains(text(),'Tìm kiếm')]");
    private static final By SEARCH_TOGGLE_XPATH =
            By.xpath("//input[contains(@id,'home-search')]");

    private static final By SEARCH_INPUT_ID = By.id("searchString");
    private static final By SEARCH_INPUT_FALLBACK =
            By.xpath("//input[@name='textBoxSearch']");

    public HomePage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
        this.baseUrl = ConfigReader.getInstance().getString("base.url", "https://www.vietcombank.com.vn/");
    }

    public void openHomePage() {
        driver.get(baseUrl);
        waitForPageLoad();
    }

    public void handleCookieBanner() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement acceptBtn = shortWait.until(
                    ExpectedConditions.elementToBeClickable(COOKIE_ACCEPT_BTN_XPATH));
            acceptBtn.click();
            System.out.println("HomePage: Cookie consent banner dismissed.");
        } catch (Exception e) {
            // Banner not present or already dismissed — safe to continue
            System.out.println("HomePage: No cookie consent banner found (or already dismissed).");
        }
    }


    public void clickSearchToggle() {
        try {
            WebElement toggle = wait.until(
                    ExpectedConditions.elementToBeClickable(SEARCH_TOGGLE_CSS));
            toggle.click();
        } catch (Exception e) {
            // Try XPath fallback
            System.out.println("HomePage: CSS search toggle not found, trying XPath fallback.");
            WebElement toggle = wait.until(
                    ExpectedConditions.elementToBeClickable(SEARCH_TOGGLE_XPATH));
            toggle.click();
        }

        // Wait for search input to appear after clicking the toggle
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT_ID));
        } catch (Exception e) {
            System.out.println("HomePage: Primary search input not visible, trying fallback.");
            wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT_FALLBACK));
        }
    }


    public void enterKeyword(String keyword) {
        WebElement input = getSearchInput();
        input.clear();
        input.sendKeys(keyword);
        System.out.println("HomePage: Entered keyword: " + keyword);
    }


    public void submitSearch() {
        WebElement input = getSearchInput();
        input.sendKeys(Keys.ENTER);
        System.out.println("HomePage: Submitted search via ENTER key.");
    }

    public void searchFor(String keyword) {
        clickSearchToggle();
        enterKeyword(keyword);
        submitSearch();
    }


    private WebElement getSearchInput() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT_ID));
        } catch (Exception e) {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT_FALLBACK));
        }
    }

    private void waitForPageLoad() {
        wait.until(driver -> {
            String readyState = ((JavascriptExecutor) driver)
                    .executeScript("return document.readyState").toString();
            return "complete".equals(readyState);
        });
    }
}
