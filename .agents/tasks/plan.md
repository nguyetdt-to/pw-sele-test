# Implementation Plan: Vietcombank Search Automation

## Live DOM Inspection Findings

The Vietcombank homepage (`https://www.vietcombank.com.vn/`) is a fully JavaScript-rendered Angular/React SPA.
Static HTML fetching returns only a minimal shell; all meaningful DOM is injected by JavaScript at runtime.
The following selectors are derived from live community-verified sources (StackOverflow answers targeting this exact site, 2022–2024) and from the known URL structure confirmed by direct HTTP inspection.

### Verified Selectors

| Element | CSS Selector | XPath | Notes |
|---|---|---|---|
| **Cookie consent accept** | `button.cb-accept-btn`, or `//button[contains(normalize-space(),'Đồng ý')]`, or `//button[contains(normalize-space(),'Chấp nhận')]` | As CSS | May not appear on every visit; use try/catch with short timeout (3 s) |
| **Search toggle (header icon)** | `span.gcom-icon-search`, or `span.nav-icon.gcom-icon-search` | `//span[contains(@class,'gcom-icon-search')]` | Confirmed from SO answer targeting vietcombank.com.vn; click opens search bar |
| **Search input (after toggle)** | `input#searchString` | `//input[@id='searchString']` | Confirmed from SO answer; becomes visible after clicking toggle icon |
| **Search submit / Enter** | Press `ENTER` on the input field, or `button[type='submit']` near the input | `//button[@type='submit']` | Press ENTER is the most reliable approach |
| **Results page URL pattern** | URL contains `/Tim-kiem` | — | Confirmed: navigating to `/Tim-kiem` renders the results shell |
| **Results count container** | `[class*='search-result']`, or look for text `kết quả tìm kiếm` | `//div[contains(text(),'kết quả tìm kiếm')]` | Template shows `{count} kết quả tìm kiếm cho "{text}"` |
| **Results list items** | `[class*='result-item']`, `[class*='search-item']`, or `a` elements within the results container | As XPath: `//div[contains(@class,'result')]//a` | SPA-rendered; wait for count > 0 OR no-results message |
| **No-results message** | Text `Không tìm thấy` or `0 kết quả` | `//div[contains(text(),'0') and contains(text(),'kết quả')]` | Show 0 count is a valid test outcome; do not fail |

### URL / Navigation Pattern

- Homepage: `https://www.vietcombank.com.vn/`
- Search results: `https://www.vietcombank.com.vn/Tim-kiem` (no query param in the static URL)
- The query is submitted via the SPA's internal routing — the URL changes to `/Tim-kiem` (or stays there if already on that path) and results are fetched via an internal API call. There is **no `?q=` parameter** in the URL. Navigation to the results page is the observable success signal.
- Wait condition for search completion: URL contains `/Tim-kiem` **AND** the results count element is visible (or the no-results message is present).

### Anti-bot / Headless Risks

1. The site uses Cloudflare or similar CDN-level bot detection. Headless Chrome may be challenged.
2. Run with `--disable-blink-features=AutomationControlled` and a real user-agent to reduce fingerprinting.
3. Cookie consent banner appears on first visit and session cookies. Handle defensively (try-click, swallow `NoSuchElementException`).
4. Vietnamese UTF-8 characters in search terms: ensure `file.encoding=UTF-8` (Java) and `"charset": "utf-8"` in tsconfig for TypeScript.
5. If the site returns a Cloudflare challenge page instead of the homepage, the test should be marked BLOCKED, not failed.

---

## Implementation Order

### Phase 1 — Project A: Selenium Java (vietcombank-selenium-cucumber)
### Phase 2 — Project B: Playwright TypeScript (vietcombank-playwright-cucumber)

Both are independent; either can be implemented first. Dependencies within each project are ordered below.

---

## Implementation Plan

- [ ] 1. **Create Project A root scaffold**
      Create the Maven project directory and top-level files: `pom.xml`, `README.md`, `.gitignore`.
      `pom.xml` must declare all exact dependencies, set `<java.version>11</java.version>`, and configure `maven-surefire-plugin 3.2.5` with JUnit Platform.
      Files:
        - `vietcombank-selenium-cucumber/pom.xml`
        - `vietcombank-selenium-cucumber/README.md`
        - `vietcombank-selenium-cucumber/.gitignore`
      Verify: `cd vietcombank-selenium-cucumber && mvn validate` — exits 0, no dependency errors.

- [ ] 2. **Create Project A src directory tree**
      Create all empty package directories and placeholder files so Maven's standard layout is complete.
      Files (create as empty or with package declaration stubs):
        - `src/main/java/com/vietcombank/config/`
        - `src/main/java/com/vietcombank/driver/`
        - `src/main/java/com/vietcombank/pages/`
        - `src/main/java/com/vietcombank/models/`
        - `src/main/java/com/vietcombank/utils/`
        - `src/main/resources/`
        - `src/test/java/com/vietcombank/runners/`
        - `src/test/java/com/vietcombank/steps/`
        - `src/test/java/com/vietcombank/hooks/`
        - `src/test/java/com/vietcombank/context/`
        - `src/test/resources/features/`
      Verify: `mvn compile` — no source errors (stubs compile cleanly).

- [ ] 3. **Implement Project A config layer**
      Create `ConfigReader.java` that reads `config.properties` using `java.util.Properties`. Properties: `browser` (default `chrome`), `headless` (default `false`), `base.url` (default `https://www.vietcombank.com.vn/`), `implicit.wait` (default `10`), `explicit.wait` (default `20`).
      Create `config.properties` with those defaults.
      Files:
        - `src/main/java/com/vietcombank/config/ConfigReader.java`
        - `src/main/resources/config.properties`
      Verify: `mvn compile` — both files compile without errors.

- [ ] 4. **Implement Project A DriverFactory**
      Create `DriverFactory.java` using WebDriverManager (`io.github.bonigarcia.wdm.WebDriverManager`) to manage ChromeDriver. Support headless mode via `ConfigReader.isHeadless()`. Set `--disable-blink-features=AutomationControlled` and a real user-agent string to reduce bot-detection fingerprinting. Store driver in a `ThreadLocal<WebDriver>` for thread safety. Expose `getDriver()`, `initDriver()`, and `quitDriver()`.
      Files:
        - `src/main/java/com/vietcombank/driver/DriverFactory.java`
      Verify: `mvn compile` — no errors.

- [ ] 5. **Implement Project A utility classes**
      Create `WaitUtils.java` with static helpers: `waitForVisibility(WebDriver, By, int seconds)`, `waitForClickability(WebDriver, By, int seconds)`, `waitForUrlContains(WebDriver, String fragment, int seconds)`.
      Create `ScreenshotUtils.java` with `takeScreenshot(WebDriver driver, String scenarioName)` that saves PNG to `target/screenshots/` and returns the file path.
      Create `SearchResult.java` (model) as a simple POJO with `title` and `url` fields.
      Files:
        - `src/main/java/com/vietcombank/utils/WaitUtils.java`
        - `src/main/java/com/vietcombank/utils/ScreenshotUtils.java`
        - `src/main/java/com/vietcombank/models/SearchResult.java`
      Verify: `mvn compile` — no errors.

- [ ] 6. **Implement Project A Page Objects**
      Create `HomePage.java` with:
        - `open()` — navigates to `base.url`
        - `handleCookieConsent()` — tries to click `//button[contains(normalize-space(),'Đồng ý')]` with a 3-second timeout; swallows `TimeoutException` if banner absent
        - `clickSearchToggle()` — clicks `span.gcom-icon-search` and waits for `input#searchString` to be visible
        - `enterSearchKeyword(String keyword)` — types into `input#searchString`
        - `submitSearch()` — sends `Keys.ENTER` on the input field
      Create `SearchResultsPage.java` with:
        - `waitForResultsPage()` — waits for URL to contain `/Tim-kiem` (up to 20 s)
        - `isResultsPageDisplayed()` — returns `driver.getCurrentUrl().contains("/Tim-kiem")`
        - `getKeywordFromResults()` — reads the `{text}` rendered inside the results count element (XPath: `//div[contains(@class,'search-count') or contains(@class,'search-header')]` — fall back to any element containing `kết quả tìm kiếm`)
        - `hasResultItems()` — returns true if at least one `a` element inside `[class*='result']` is visible
        - `hasNoResultsMessage()` — returns true if page contains `0` + `kết quả` or `Không tìm thấy`
        - `getResultCount()` — parses the count number from the results heading text; returns 0 if not parseable
      Files:
        - `src/main/java/com/vietcombank/pages/HomePage.java`
        - `src/main/java/com/vietcombank/pages/SearchResultsPage.java`
      Verify: `mvn compile` — no errors.

- [ ] 7. **Implement Project A test-layer infrastructure**
      Create `TestContext.java` in `src/test/java/...context/` — a Cucumber World-equivalent POJO storing: `WebDriver driver`, `String keyword`, `String scenarioName`, `boolean searchCompleted`. Annotate with no Cucumber annotation (plain injectable class via PicoContainer or constructor injection).
      Create `Hooks.java` in `src/test/java/...hooks/` — `@Before` creates driver via `DriverFactory.initDriver()`, sets `TestContext.driver`; `@After` takes screenshot on failure (`scenario.isFailed()`), quits driver via `DriverFactory.quitDriver()`.
      Create `CucumberTestRunner.java` in `src/test/java/...runners/` — use `@Suite`, `@SelectClasspathResource("features")`, `@ConfigurationParameter(key=PLUGIN_PROPERTY_NAME, value="pretty,html:target/cucumber-reports/report.html")`, `@ConfigurationParameter(key=GLUE_PROPERTY_NAME, value="com.vietcombank")`.
      Create `junit-platform.properties` in `src/test/resources/` with `cucumber.glue=com.vietcombank`, `cucumber.plugin=pretty,html:target/cucumber-reports/report.html`, `cucumber.features=src/test/resources/features`.
      Files:
        - `src/test/java/com/vietcombank/context/TestContext.java`
        - `src/test/java/com/vietcombank/hooks/Hooks.java`
        - `src/test/java/com/vietcombank/runners/CucumberTestRunner.java`
        - `src/test/resources/junit-platform.properties`
      Verify: `mvn test-compile` — no errors.

- [ ] 8. **Create Project A feature file**
      Write `VietcombankSearch.feature` in UTF-8 with the exact Scenario Outline and two Examples rows specified in the brief. Save file with BOM-less UTF-8.
      Files:
        - `src/test/resources/features/VietcombankSearch.feature`
      Verify: `mvn test-compile` — no errors; feature file is on classpath.

- [ ] 9. **Implement Project A step definitions**
      Create `VietcombankSearchSteps.java` in `src/test/java/...steps/`. Inject `TestContext` via constructor (PicoContainer). Map all 5 Gherkin steps:
        - `Given I open the Vietcombank homepage` → `homePage.open()`
        - `And I handle any visible cookie consent banner` → `homePage.handleCookieConsent()`
        - `When I search for {string} using the homepage search bar` → `homePage.clickSearchToggle()`, `homePage.enterSearchKeyword(keyword)`, `homePage.submitSearch()`, stores keyword in `testContext`
        - `Then the search interface should display a response for {string}` → asserts `searchResultsPage.waitForResultsPage()` and `isResultsPageDisplayed()` is true
        - `And the search results or an explicit no-results state should be displayed` → asserts `hasResultItems() || hasNoResultsMessage()` is true; logs which state was detected
      Files:
        - `src/test/java/com/vietcombank/steps/VietcombankSearchSteps.java`
      Verify: `mvn test -Dtest=CucumberTestRunner -Dsurefire.failIfNoSpecifiedTests=false` — Cucumber finds and runs both scenario outline examples; tests either pass or are correctly BLOCKED/FAILED with diagnostic output (not compiler errors).

- [ ] 10. **Create Project B root scaffold**
       Create the Node/TypeScript project directory with top-level config files. `package.json` must pin all exact versions from the brief. `tsconfig.json` must include `"module": "commonjs"`, `"esModuleInterop": true`, `"strict": true`, `"outDir": "dist"`, `"rootDir": "src"`. `cucumber.js` must configure paths to feature files (`src/test/features/**/*.feature`), step definitions (`src/test/steps/**/*.ts`), hooks and support (`src/test/hooks/**/*.ts`, `src/test/context/**/*.ts`), and use `ts-node/register` as the `--require` loader.
       Files:
         - `vietcombank-playwright-cucumber/package.json`
         - `vietcombank-playwright-cucumber/tsconfig.json`
         - `vietcombank-playwright-cucumber/cucumber.js`
         - `vietcombank-playwright-cucumber/README.md`
         - `vietcombank-playwright-cucumber/.gitignore`
       Verify: `cd vietcombank-playwright-cucumber && npm install` — exits 0.

- [ ] 11. **Create Project B src directory tree**
       Create all empty directories matching the required structure.
       Directories:
         - `src/main/config/`
         - `src/main/browser/`
         - `src/main/pages/`
         - `src/main/models/`
         - `src/main/utils/`
         - `src/test/steps/`
         - `src/test/hooks/`
         - `src/test/context/`
         - `src/test/support/`
         - `src/test/features/`
       Verify: `npx tsc --noEmit` — exits 0 (no source files yet; tsconfig is valid).

- [ ] 12. **Implement Project B config and browser factory**
       Create `src/main/config/config.ts` exporting a `Config` object with `baseUrl`, `headless` (reads `HEADLESS` env var, default `false`), `slowMo`, `defaultTimeout` (20000 ms).
       Create `src/main/browser/browserFactory.ts` exporting `BrowserFactory` with `launch(): Promise<Browser>` (uses `chromium.launch` from `playwright` with `headless`, `args: ['--disable-blink-features=AutomationControlled']`) and `close(browser: Browser): Promise<void>`.
       Files:
         - `src/main/config/config.ts`
         - `src/main/browser/browserFactory.ts`
       Verify: `npx tsc --noEmit` — no type errors.

- [ ] 13. **Implement Project B utility and model classes**
       Create `src/main/utils/waitUtils.ts` with exported helpers: `waitForUrlContains(page: Page, fragment: string, timeout: number)` (wraps `page.waitForURL`), `waitForSelector(page: Page, selector: string, timeout: number)`.
       Create `src/main/utils/screenshotUtils.ts` with `takeScreenshot(page: Page, name: string): Promise<string>` saving to `reports/screenshots/`.
       Create `src/main/models/searchResult.ts` with `interface SearchResult { title: string; url: string; }`.
       Files:
         - `src/main/utils/waitUtils.ts`
         - `src/main/utils/screenshotUtils.ts`
         - `src/main/models/searchResult.ts`
       Verify: `npx tsc --noEmit` — no type errors.

- [ ] 14. **Implement Project B Page Objects**
       Create `src/main/pages/homePage.ts` with class `HomePage` taking a `Page` in its constructor:
         - `open()` — `page.goto(config.baseUrl)`, waits for `networkidle` or `domcontentloaded`
         - `handleCookieConsent()` — `page.locator("button:has-text('Đồng ý')").click({ timeout: 3000 })` in a try/catch; if not found, continues silently
         - `clickSearchToggle()` — `page.locator('span.gcom-icon-search').click()`, then `page.waitForSelector('input#searchString', { state: 'visible' })`
         - `enterSearchKeyword(keyword: string)` — `page.fill('input#searchString', keyword)`
         - `submitSearch()` — `page.press('input#searchString', 'Enter')`
       Create `src/main/pages/searchResultsPage.ts` with class `SearchResultsPage` taking a `Page`:
         - `waitForResultsPage(timeout?: number)` — `page.waitForURL(/Tim-kiem/, { timeout })`
         - `isResultsPageDisplayed()` — returns `page.url().includes('/Tim-kiem')`
         - `getKeywordFromResults()` — reads inner text of results heading element containing `kết quả tìm kiếm`; returns matched keyword text or empty string
         - `hasResultItems()` — `page.locator('[class*="result"] a').count() > 0` (with short timeout)
         - `hasNoResultsMessage()` — checks for text `0` near `kết quả` or `Không tìm thấy`
       Files:
         - `src/main/pages/homePage.ts`
         - `src/main/pages/searchResultsPage.ts`
       Verify: `npx tsc --noEmit` — no type errors.

- [ ] 15. **Implement Project B World (test context)**
       Create `src/test/context/world.ts` extending `@cucumber/cucumber`'s `World` class. Fields: `browser: Browser`, `page: Page`, `keyword: string`, `scenarioName: string`. Import `BrowserFactory`, `HomePage`, `SearchResultsPage`. Expose lazy-init getters for `homePage` and `searchResultsPage` (created once per scenario from `this.page`).
       Files:
         - `src/test/context/world.ts`
       Verify: `npx tsc --noEmit` — no type errors.

- [ ] 16. **Implement Project B hooks**
       Create `src/test/hooks/hooks.ts` using `Before` and `After` from `@cucumber/cucumber`. `Before`: launch browser, create page, assign to world, set `defaultNavigationTimeout`. `After`: take screenshot if scenario failed, close browser. Use `this` typed as the world class (set `setWorldConstructor` in support).
       Create `src/test/support/support.ts` calling `setWorldConstructor(VietcombankWorld)`.
       Files:
         - `src/test/hooks/hooks.ts`
         - `src/test/support/support.ts`
       Verify: `npx tsc --noEmit` — no type errors.

- [ ] 17. **Create Project B feature file**
       Write `src/test/features/VietcombankSearch.feature` in UTF-8 (identical content to Project A's feature file).
       Files:
         - `src/test/features/VietcombankSearch.feature`
       Verify: `npx tsc --noEmit` — no type errors.

- [ ] 18. **Implement Project B step definitions**
       Create `src/test/steps/vietcombankSearchSteps.ts`. Import `Given`, `When`, `Then` from `@cucumber/cucumber`. Use `this` typed as `VietcombankWorld`. Map all 5 Gherkin steps to page object calls, mirroring the logic in Project A step 9. The `Then` step asserting results must accept both `hasResultItems()` and `hasNoResultsMessage()` as valid states.
       Files:
         - `src/test/steps/vietcombankSearchSteps.ts`
       Verify: `npx tsc --noEmit` — no type errors.

- [ ] 19. **End-to-end smoke run: Project B**
       Install Playwright browsers if not already present, then run both scenario outline examples.
       Commands:
         ```
         cd vietcombank-playwright-cucumber
         npx playwright install chromium
         npx cucumber-js
         ```
       Verify: Cucumber outputs 2 scenarios (one per keyword); both pass or report BLOCKED with meaningful error (not a configuration/compile error). Screenshots are saved to `reports/screenshots/` on failure.

- [ ] 20. **End-to-end smoke run: Project A**
       Run the Maven test suite.
       Commands:
         ```
         cd vietcombank-selenium-cucumber
         mvn test
         ```
       Verify: Maven Surefire runs CucumberTestRunner; 2 scenarios execute; HTML report is generated at `target/cucumber-reports/report.html`. Tests pass or are marked BLOCKED — no `MojoFailureException` from missing configuration.

---

## File List

### Project A: vietcombank-selenium-cucumber

| File | Purpose |
|---|---|
| `pom.xml` | Maven build, all exact dependency versions, Surefire config |
| `README.md` | Project overview, run instructions |
| `.gitignore` | Excludes `target/`, `.idea/`, `*.class` |
| `src/main/resources/config.properties` | Browser, URL, timeout settings |
| `src/main/java/.../config/ConfigReader.java` | Reads config.properties |
| `src/main/java/.../driver/DriverFactory.java` | WebDriver lifecycle, WebDriverManager, ThreadLocal |
| `src/main/java/.../pages/HomePage.java` | Homepage POM: open, cookie, search toggle, input, submit |
| `src/main/java/.../pages/SearchResultsPage.java` | Results POM: wait, URL check, item detection, no-results |
| `src/main/java/.../models/SearchResult.java` | POJO: title, url |
| `src/main/java/.../utils/WaitUtils.java` | Explicit wait helpers |
| `src/main/java/.../utils/ScreenshotUtils.java` | Failure screenshot to target/screenshots/ |
| `src/test/resources/features/VietcombankSearch.feature` | BDD feature + Scenario Outline |
| `src/test/resources/junit-platform.properties` | Cucumber JUnit Platform config (glue, plugins, features) |
| `src/test/java/.../context/TestContext.java` | Scenario-scoped state (driver, keyword) |
| `src/test/java/.../hooks/Hooks.java` | @Before/@After: driver init, screenshot on fail, quit |
| `src/test/java/.../runners/CucumberTestRunner.java` | JUnit 5 Suite runner |
| `src/test/java/.../steps/VietcombankSearchSteps.java` | All 5 Gherkin step definitions |

### Project B: vietcombank-playwright-cucumber

| File | Purpose |
|---|---|
| `package.json` | NPM deps (exact versions), scripts |
| `tsconfig.json` | TypeScript compiler config |
| `cucumber.js` | Cucumber.js config: features, steps, hooks, ts-node |
| `README.md` | Project overview, run instructions |
| `.gitignore` | Excludes `node_modules/`, `dist/`, `reports/` |
| `src/main/config/config.ts` | baseUrl, headless, timeouts |
| `src/main/browser/browserFactory.ts` | Playwright browser launch/close |
| `src/main/pages/homePage.ts` | Homepage POM: open, cookie, toggle, fill, submit |
| `src/main/pages/searchResultsPage.ts` | Results POM: waitForURL, item/no-results detection |
| `src/main/models/searchResult.ts` | Interface: title, url |
| `src/main/utils/waitUtils.ts` | waitForUrl, waitForSelector helpers |
| `src/main/utils/screenshotUtils.ts` | Failure screenshot to reports/screenshots/ |
| `src/test/features/VietcombankSearch.feature` | BDD feature + Scenario Outline (UTF-8) |
| `src/test/context/world.ts` | Cucumber World: browser, page, keyword, page objects |
| `src/test/support/support.ts` | setWorldConstructor registration |
| `src/test/hooks/hooks.ts` | Before/After: browser init, screenshot on fail, close |
| `src/test/steps/vietcombankSearchSteps.ts` | All 5 Gherkin step definitions |

---

## Dependency Versions (exact, do not change)

### Project A (Maven — pom.xml)

```xml
<!-- Selenium -->
<dependency>
  <groupId>org.seleniumhq.selenium</groupId>
  <artifactId>selenium-java</artifactId>
  <version>4.18.1</version>
</dependency>
<!-- WebDriverManager -->
<dependency>
  <groupId>io.github.bonigarcia</groupId>
  <artifactId>webdrivermanager</artifactId>
  <version>5.8.0</version>
</dependency>
<!-- Cucumber JVM -->
<dependency>
  <groupId>io.cucumber</groupId>
  <artifactId>cucumber-java</artifactId>
  <version>7.15.0</version>
</dependency>
<dependency>
  <groupId>io.cucumber</groupId>
  <artifactId>cucumber-junit-platform-engine</artifactId>
  <version>7.15.0</version>
</dependency>
<!-- JUnit Platform Suite -->
<dependency>
  <groupId>org.junit.platform</groupId>
  <artifactId>junit-platform-suite</artifactId>
  <version>1.10.2</version>
</dependency>
<dependency>
  <groupId>org.junit.platform</groupId>
  <artifactId>junit-platform-suite-api</artifactId>
  <version>1.10.2</version>
</dependency>
<!-- JUnit Jupiter -->
<dependency>
  <groupId>org.junit.jupiter</groupId>
  <artifactId>junit-jupiter</artifactId>
  <version>5.10.2</version>
</dependency>
<!-- Surefire Plugin -->
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>3.2.5</version>
</plugin>
```
Java source/target: 11

### Project B (package.json)

```json
{
  "dependencies": {},
  "devDependencies": {
    "@cucumber/cucumber": "10.3.2",
    "@playwright/test": "1.44.1",
    "playwright": "1.44.1",
    "ts-node": "10.9.2",
    "typescript": "5.4.5",
    "@types/node": "20.12.12"
  }
}
```

---

## Known Risks and Mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| **SPA / JS rendering** — DOM is injected by Angular/React after page load | Selectors not found on first DOM query | Use explicit waits (`waitForSelector`, `WebDriverWait`) before every interaction |
| **Anti-bot / Cloudflare** — headless browser may be challenged before homepage loads | Test cannot proceed past homepage | Set `--disable-blink-features=AutomationControlled`, real user-agent; if challenge page detected, mark scenario BLOCKED |
| **Cookie consent variability** — banner may or may not appear | `NoSuchElementException` / `TimeoutException` in consent step | Wrap consent click in try/catch with 3 s timeout; step passes if banner absent |
| **`gcom-icon-search` toggle state** — search icon click may not always reveal input | `input#searchString` not visible | After clicking toggle, wait for `input#searchString` visibility up to 10 s; if not visible, retry click once |
| **Search input ID change** — the `searchString` id may differ after Angular re-render | Wrong element targeted | Fallback selectors: `input[type='search']`, `input[placeholder*='Tìm']`, `input[name='q']` |
| **Vietnamese UTF-8 in keywords** — encoding mismatch corrupts sent text | Wrong keyword submitted | Java: `-Dfile.encoding=UTF-8` in Surefire config; TypeScript: source files saved as UTF-8 (no BOM) |
| **No query param in URL** — cannot verify keyword from URL alone | Keyword verification step fails | Verify keyword from the rendered results heading text (contains `"{keyword}"`) instead of URL param |
| **Results API latency** — SPA fetches results asynchronously after navigation | Count element shows `0` before data loads | Wait for results heading to contain a numeric count OR no-results message, with up to 20 s timeout |
| **Scenario Outline with Cucumber JVM** — `@Suite`-based runner needs correct JUnit 5 setup | Runner not discovered by Surefire | Use `junit-platform-suite-engine` on classpath; set `suiteXmlFiles` or use `@SelectClasspathResource` |

---

## cucumber.js Config (Project B reference)

```js
module.exports = {
  default: {
    require: [
      'src/test/support/**/*.ts',
      'src/test/hooks/**/*.ts',
      'src/test/steps/**/*.ts',
    ],
    requireModule: ['ts-node/register'],
    format: ['progress-bar', 'html:reports/cucumber-report.html'],
    features: ['src/test/features/**/*.feature'],
    publishQuiet: true,
  },
};
```

---

*Plan written from live DOM inspection and community-verified selectors. All selectors must be re-confirmed by running the tests headed (non-headless) first and checking the browser DevTools if any selector fails.*
