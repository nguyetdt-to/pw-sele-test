package com.vietcombank.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Static helpers for common explicit-wait operations.
 */
public class WaitUtils {

    private WaitUtils() {
        // utility class
    }

    /**
     * Waits for the element located by {@code locator} to be visible on the page.
     *
     * @param driver  the WebDriver instance
     * @param locator the By locator
     * @param seconds maximum time to wait
     * @return the visible WebElement
     */
    public static WebElement waitForVisibility(WebDriver driver, By locator, int seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits for the element located by {@code locator} to be clickable.
     *
     * @param driver  the WebDriver instance
     * @param locator the By locator
     * @param seconds maximum time to wait
     * @return the clickable WebElement
     */
    public static WebElement waitForClickability(WebDriver driver, By locator, int seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Waits for the current URL to contain the given {@code fragment}.
     *
     * @param driver   the WebDriver instance
     * @param fragment URL substring to wait for
     * @param seconds  maximum time to wait
     */
    public static void waitForUrlContains(WebDriver driver, String fragment, int seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        wait.until(ExpectedConditions.urlContains(fragment));
    }
}
