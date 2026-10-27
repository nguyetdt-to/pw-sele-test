package com.vietcombank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class SearchResultsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final String RESULTS_URL_FRAGMENT = "Tim-kiem";

    // Results count container: shows "{count} kết quả tìm kiếm cho ..."
    private static final By RESULTS_COUNT_CONTAINER =
            By.xpath("//div[contains(text(),'kết quả tìm kiếm')]"
                    + " | //*[contains(@class,'search-result') and contains(.,'kết quả')]"
                    + " | //*[contains(@class,'search-count')]"
                    + " | //*[contains(@class,'search-header') and contains(.,'kết quả')]");

    private static final By RESULT_ITEMS =
            By.xpath("//div[contains(@class,'result')]//a"
                    + " | //div[contains(@class,'search-item')]//a"
                    + " | //li[contains(@class,'result')]//a");

    private static final By NO_RESULTS_MESSAGE =
            By.xpath("//*[contains(text(),'Không tìm thấy')]"
                    + " | //*[contains(text(),'0 kết quả')]"
                    + " | //*[contains(@class,'no-result')]"
                    + " | //div[contains(text(),'0') and contains(text(),'kết quả')]");

    public SearchResultsPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public void waitForSearchResponse() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {
            longWait.until(ExpectedConditions.urlContains(RESULTS_URL_FRAGMENT));
            System.out.println("SearchResultsPage: URL changed to results page: " + driver.getCurrentUrl());
        } catch (TimeoutException urlTimeout) {
            System.out.println("SearchResultsPage: URL did not contain '" + RESULTS_URL_FRAGMENT
                    + "' within timeout. Checking for results container...");
        }

        try {
            longWait.until(driver -> {
                try {
                    List<WebElement> containers = driver.findElements(RESULTS_COUNT_CONTAINER);
                    List<WebElement> noResults = driver.findElements(NO_RESULTS_MESSAGE);
                    List<WebElement> items = driver.findElements(RESULT_ITEMS);
                    return !containers.isEmpty() || !noResults.isEmpty() || !items.isEmpty();
                } catch (Exception e) {
                    return false;
                }
            });
            System.out.println("SearchResultsPage: Search response container is visible.");
        } catch (TimeoutException containerTimeout) {
            System.out.println("SearchResultsPage: Results container not found within timeout. "
                    + "Current URL: " + driver.getCurrentUrl());
        }
    }

    public boolean isSearchResponseDisplayed() {
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl != null && currentUrl.contains(RESULTS_URL_FRAGMENT)) {
            System.out.println("SearchResultsPage: Results URL confirmed: " + currentUrl);
            return true;
        }

        List<WebElement> containers = driver.findElements(RESULTS_COUNT_CONTAINER);
        if (!containers.isEmpty() && containers.get(0).isDisplayed()) {
            return true;
        }

        List<WebElement> noResults = driver.findElements(NO_RESULTS_MESSAGE);
        return !noResults.isEmpty() && noResults.get(0).isDisplayed();
    }

    public boolean hasResults() {
        try {
            List<WebElement> items = driver.findElements(RESULT_ITEMS);
            long visibleCount = items.stream()
                    .filter(el -> {
                        try {
                            return el.isDisplayed();
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .count();
            System.out.println("SearchResultsPage: Visible result items: " + visibleCount);
            return visibleCount > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true if the page explicitly shows a no-results message.
     */
    public boolean hasNoResultsMessage() {
        try {
            List<WebElement> noResults = driver.findElements(NO_RESULTS_MESSAGE);
            boolean found = noResults.stream()
                    .anyMatch(el -> {
                        try {
                            return el.isDisplayed();
                        } catch (Exception e) {
                            return false;
                        }
                    });
            if (found) {
                System.out.println("SearchResultsPage: No-results message detected.");
            }
            return found;
        } catch (Exception e) {
            return false;
        }
    }

    public String getSubmittedKeyword() {
        try {
            List<WebElement> containers = driver.findElements(RESULTS_COUNT_CONTAINER);
            for (WebElement el : containers) {
                String text = el.getText();
                if (text != null && text.contains("\"")) {
                    int start = text.indexOf('"') + 1;
                    int end = text.lastIndexOf('"');
                    if (start > 0 && end > start) {
                        return text.substring(start, end);
                    }
                }
            }
        } catch (Exception e) {
            // fall through to URL parsing
        }

        // Fallback: try to parse keyword from URL query params
        try {
            String url = driver.getCurrentUrl();
            if (url.contains("?")) {
                String query = url.substring(url.indexOf('?') + 1);
                for (String param : query.split("&")) {
                    if (param.startsWith("q=") || param.startsWith("keyword=") || param.startsWith("s=")) {
                        return java.net.URLDecoder.decode(
                                param.substring(param.indexOf('=') + 1), "UTF-8");
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }

        return "";
    }
}
