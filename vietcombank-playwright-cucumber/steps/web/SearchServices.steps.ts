import { createBdd } from 'playwright-bdd';
import { HomePage } from '../../pages/HomePage';
import { SearchResultPage } from '../../pages/SearchResultPage';

const { Given, When, Then } = createBdd();

Given('I open the Vietcombank homepage', async ({ page }) => {
    const homePage = new HomePage(page);
    await homePage.open();
});

When('I handle any visible cookie consent banner', async ({ page }) => {
    const homePage = new HomePage(page);
    await homePage.handleCookieBanner();
});

When('I search for {string} using the homepage search bar', async ({ page }, searchTerm: string) => {
    const homePage = new HomePage(page);
    await homePage.searchFor(searchTerm);
});

Then('the search interface should display a response for {string}', async ({ page }, searchTerm: string) => {
    const searchResultPage = new SearchResultPage(page);
    await searchResultPage.assertSearchResponseContains(searchTerm);
});

Then('the search results or an explicit no-results state should be displayed', async ({ page }) => {
    const searchResultPage = new SearchResultPage(page);
    await searchResultPage.assertResultsOrNoResultsDisplayed();
});