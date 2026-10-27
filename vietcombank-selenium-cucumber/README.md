# Vietcombank Selenium Cucumber

Selenium WebDriver + Java + Maven + Cucumber JVM automation for the Vietcombank search functionality.

## Prerequisites

- Java 11+
- Maven 3.8+
- Google Chrome (latest stable)
- Internet access to `https://www.vietcombank.com.vn/`

## Project Structure

```
vietcombank-selenium-cucumber/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/vietcombank/
│   │   │   ├── config/ConfigReader.java
│   │   │   ├── driver/DriverFactory.java
│   │   │   ├── pages/HomePage.java
│   │   │   ├── pages/SearchResultsPage.java
│   │   │   ├── models/SearchResult.java
│   │   │   └── utils/{WaitUtils,ScreenshotUtils}.java
│   │   └── resources/config.properties
│   └── test/
│       ├── java/com/vietcombank/
│       │   ├── context/TestContext.java
│       │   ├── hooks/Hooks.java
│       │   ├── runners/CucumberTestRunner.java
│       │   └── steps/VietcombankSearchSteps.java
│       └── resources/
│           ├── features/VietcombankSearch.feature
│           └── junit-platform.properties
```

## Configuration

Edit `src/main/resources/config.properties`:

| Property   | Default                              | Description                    |
|------------|--------------------------------------|--------------------------------|
| browser    | chrome                               | Browser to use (chrome only)   |
| headless   | false                                | Run in headless mode           |
| base.url   | https://www.vietcombank.com.vn/      | Target URL                     |
| timeout    | 15                                   | Explicit wait timeout (seconds)|

## Running Tests

```bash
# Run all @smoke scenarios (headed Chrome)
mvn clean test

# Run headless
mvn clean test -Dheadless=true

# Ensure UTF-8 encoding for Vietnamese keywords
mvn clean test -Dfile.encoding=UTF-8
```

## Reports

After a test run, reports are available at:

- `target/cucumber-reports/cucumber.html` — HTML report
- `target/cucumber-reports/cucumber.json` — JSON report
- `target/screenshots/` — Screenshots captured on scenario failure

## BDD Scenarios

The feature tests search on the Vietcombank homepage with two keywords:

1. `Mở tài khoản`
2. `Thẻ tín dụng`

Both **results found** and **no-results message** are valid passing states.
If the site blocks automation (anti-bot/Cloudflare), the test will report a BLOCKED state
with diagnostic output rather than falsely passing.
