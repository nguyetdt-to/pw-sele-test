import { Page, Locator, expect } from '@playwright/test';

export class SearchResultPage {
    readonly page: Page;

    readonly searchTextContainer: Locator;

    readonly searchInput: Locator;

    constructor(page: Page) {
        this.page = page;
        this.searchTextContainer = page.locator("//span[@class='search-text-container']");
        this.searchInput         = page.locator("//input[@name='textBoxSearch']");
    }

    async assertSearchResponseContains(keyword: string): Promise<void> {
        await expect(this.searchTextContainer).toBeVisible();
        await expect(this.searchTextContainer).toContainText(keyword);
    }

    async assertResultsOrNoResultsDisplayed(): Promise<void> {
        await expect(this.searchTextContainer).toBeVisible();
        const text = await this.searchTextContainer.textContent();
        if (text && text.trim() !== '') {
            console.log('✅ Search results are displayed.');
        } else {
            console.log('ℹ️  No results found for the search term.');
        }
    }

    async assertHasResults(): Promise<void> {
        await expect(this.searchTextContainer).toBeVisible();
        const count = await this.searchTextContainer.count();
        expect(count).toBeGreaterThan(0);
    }
}
