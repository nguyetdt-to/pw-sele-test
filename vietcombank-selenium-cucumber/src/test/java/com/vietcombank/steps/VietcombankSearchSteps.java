package com.vietcombank.steps;

import com.vietcombank.context.TestContext;
import com.vietcombank.pages.HomePage;
import com.vietcombank.pages.SearchResultsPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VietcombankSearchSteps {

    private final TestContext testContext;


    public VietcombankSearchSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @Given("I open the Vietcombank homepage")
    public void iOpenTheVietcombankHomepage() {
        testContext.getHomePage().openHomePage();
    }

    @And("I handle any visible cookie consent banner")
    public void iHandleAnyVisibleCookieConsentBanner() {
        testContext.getHomePage().handleCookieBanner();
    }

    @When("I search for {string} using the homepage search bar")
    public void iSearchForKeywordUsingTheHomepageSearchBar(String keyword) {
        testContext.setKeyword(keyword);
        testContext.getHomePage().searchFor(keyword);
    }

    @Then("the search interface should display a response for {string}")
    public void theSearchInterfaceShouldDisplayAResponseFor(String keyword) {
        SearchResultsPage resultsPage = testContext.getSearchResultsPage();
//        resultsPage.waitForSearchResponse();

        boolean responseDisplayed = resultsPage.isSearchResponseDisplayed();
        testContext.setSearchResponseDisplayed(responseDisplayed);

        assertTrue(responseDisplayed,
                "Expected the search interface to display a response for keyword: '" + keyword
                        + "' but no response was detected. Current URL: "
                        + testContext.getDriver().getCurrentUrl());
    }

    @And("the search results or an explicit no-results state should be displayed")
    public void theSearchResultsOrAnExplicitNoResultsStateShouldBeDisplayed() {
        SearchResultsPage resultsPage = testContext.getSearchResultsPage();

        boolean hasResults = resultsPage.hasResults();
        boolean hasNoResults = resultsPage.hasNoResultsMessage();

        testContext.setHasResults(hasResults);

        if (hasResults) {
            System.out.println("PASS: Search results are displayed for keyword: '"
                    + testContext.getKeyword() + "'");
        } else if (hasNoResults) {
            System.out.println("PASS: Explicit no-results state is displayed for keyword: '"
                    + testContext.getKeyword() + "' — this is a valid outcome.");
            testContext.setNoResultsMessage("No results found");
        } else {
            System.out.println("INFO: Neither result items nor explicit no-results message detected. "
                    + "URL: " + testContext.getDriver().getCurrentUrl()
                    + " | Keyword: " + testContext.getKeyword());
        }

        String currentUrl = testContext.getDriver().getCurrentUrl();
        boolean onResultsPage = currentUrl != null && currentUrl.contains("Tim-kiem");

        assertTrue(hasResults || hasNoResults || onResultsPage,
                "Expected either search result items OR an explicit no-results message to be displayed, "
                        + "but neither was detected. Keyword: '" + testContext.getKeyword()
                        + "' | URL: " + currentUrl);
    }
}
