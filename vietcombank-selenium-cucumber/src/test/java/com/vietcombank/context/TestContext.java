package com.vietcombank.context;

import com.vietcombank.config.ConfigReader;
import com.vietcombank.pages.HomePage;
import com.vietcombank.pages.SearchResultsPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Scenario-scoped state shared between hooks and step definitions via PicoContainer.
 */
public class TestContext {

    private WebDriver driver;
    private WebDriverWait wait;
    private String keyword;
    private boolean searchResponseDisplayed;
    private boolean hasResults;
    private String noResultsMessage;

    private HomePage homePage;
    private SearchResultsPage searchResultsPage;

    public TestContext() {
        // default constructor required by PicoContainer
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
        int timeoutSeconds = ConfigReader.getInstance().getInt("timeout", 5);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        this.homePage = new HomePage(driver, wait);
        this.searchResultsPage = new SearchResultsPage(driver, wait);
    }

    public WebDriverWait getWait() {
        return wait;
    }

    public HomePage getHomePage() {
        return homePage;
    }

    public SearchResultsPage getSearchResultsPage() {
        return searchResultsPage;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public boolean isSearchResponseDisplayed() {
        return searchResponseDisplayed;
    }

    public void setSearchResponseDisplayed(boolean searchResponseDisplayed) {
        this.searchResponseDisplayed = searchResponseDisplayed;
    }

    public boolean isHasResults() {
        return hasResults;
    }

    public void setHasResults(boolean hasResults) {
        this.hasResults = hasResults;
    }

    public String getNoResultsMessage() {
        return noResultsMessage;
    }

    public void setNoResultsMessage(String noResultsMessage) {
        this.noResultsMessage = noResultsMessage;
    }
}
