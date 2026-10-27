import { Page, Locator, expect } from '@playwright/test';

export class HomePage {
    readonly page: Page;

    // --- Locators ---
    readonly searchBarIcon: Locator;
    readonly searchInput: Locator;
    readonly cookieBanner: Locator;
    readonly cookieAcceptButton: Locator;


    constructor(page: Page) {
        this.page = page;
        this.searchBarIcon    = page.locator("//input[contains(@id,'home-search')]");
        this.searchInput      = page.locator("//input[@name='textBoxSearch']");
        this.cookieBanner     = page.locator(
            "//button[contains(normalize-space(),'Đồng ý')] | " +
            "//button[contains(normalize-space(),'Chấp nhận')]"
        );
        this.cookieAcceptButton = this.cookieBanner;
    }

    async open(): Promise<void> {
        await this.page.goto('/', { waitUntil: 'domcontentloaded', timeout: 60_000 });
    }

    async handleCookieBanner(): Promise<void> {
        if (await this.cookieBanner.isVisible()) {
            await this.cookieAcceptButton.click();
        }
    }

    async searchFor(keyword: string): Promise<void> {
        await this.searchBarIcon.click();
        await this.searchInput.fill(keyword);
        await this.searchInput.press('Enter');
    }

    async assertTitleContains(text: string): Promise<void> {
        await expect(this.page).toHaveTitle(new RegExp(text, 'i'));
    }
}
