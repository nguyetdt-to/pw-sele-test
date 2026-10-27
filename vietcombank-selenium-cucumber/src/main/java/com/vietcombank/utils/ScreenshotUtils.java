package com.vietcombank.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures screenshots on failure and saves them to target/screenshots/.
 */
public class ScreenshotUtils {

    private static final String SCREENSHOT_DIR = "target/screenshots";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtils() {
        // utility class
    }

    /**
     * Captures a PNG screenshot and saves it to {@code target/screenshots/{name}-{timestamp}.png}.
     *
     * @param driver the WebDriver instance
     * @param name   a descriptive name for the screenshot file
     * @return the absolute path of the saved screenshot, or {@code null} if capture failed
     */
    public static String captureScreenshot(WebDriver driver, String name) {
        if (!(driver instanceof TakesScreenshot)) {
            System.err.println("ScreenshotUtils: driver does not support TakesScreenshot");
            return null;
        }

        try {
            Path screenshotDir = Paths.get(SCREENSHOT_DIR);
            Files.createDirectories(screenshotDir);

            String timestamp = LocalDateTime.now().format(FORMATTER);
            String safeName = name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
            String fileName = safeName + "-" + timestamp + ".png";
            Path targetPath = screenshotDir.resolve(fileName);

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(srcFile.toPath(), targetPath);

            String absolutePath = targetPath.toAbsolutePath().toString();
            System.out.println("Screenshot saved: " + absolutePath);
            return absolutePath;
        } catch (IOException e) {
            System.err.println("ScreenshotUtils: failed to save screenshot: " + e.getMessage());
            return null;
        }
    }
}
