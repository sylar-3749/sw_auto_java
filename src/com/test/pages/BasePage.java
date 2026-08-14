package pages;


import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base page object — mirrors the Python {@code ElementOperations} class.
 *
 * <p>Wraps WebDriverWait with convenience methods that accept either:
 * <ul>
 *   <li>A raw {@link WebElement} (already located), or</li>
 *   <li>A {@link By} locator + optional timeout.</li>
 * </ul>
 *
 * <p>All interaction goes through this class so locators are always waited on.
 */
public class BasePage {

    /** Default timeout in seconds when none is specified. */
    protected static final int DEFAULT_TIMEOUT = 30;

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected int defaultTimeout;

    // ------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------

    public BasePage(WebDriver driver) {
        this(driver, DEFAULT_TIMEOUT);
    }

    public BasePage(WebDriver driver, int timeoutSeconds) {
        this.driver = driver;
        this.defaultTimeout = timeoutSeconds;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    // ------------------------------------------------------------------
    // Navigation
    // ------------------------------------------------------------------

    /** Open a URL (mirrors Python {@code open_url}). */
    public void openUrl(String url) {
        driver.get(url);
    }

    // ------------------------------------------------------------------
    // Single-element retrieval
    // ------------------------------------------------------------------

    /**
     * Wait for a single element to be visible and return it.
     * Mirrors Python {@code wait_for_element_visible}.
     */
    public WebElement findElement(By locator) {
        return findElement(locator, defaultTimeout);
    }

    public WebElement findElement(By locator, int timeoutSeconds) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        return shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Wait for an element to be present in the DOM (may not be visible).
     * Mirrors Python {@code wait_for_element_exists}.
     */
    public void waitForElementExists(By locator) {
        waitForElementExists(locator, defaultTimeout);
    }

    public void waitForElementExists(By locator, int timeoutSeconds) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        shortWait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // ------------------------------------------------------------------
    // Multi-element retrieval
    // ------------------------------------------------------------------

    /**
     * Wait for all matching elements to be visible.
     * Returns a list (empty if none found after timeout).
     * Mirrors Python {@code wait_for_elements_visible}.
     */
    public List<WebElement> findElements(By locator) {
        return findElements(locator, defaultTimeout);
    }

    public List<WebElement> findElements(By locator, int timeoutSeconds) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            return shortWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        } catch (Exception e) {
            System.out.println("等待元素列表可见超时: " + locator + " (" + timeoutSeconds + "s)");
            captureScreenshot();
            return new ArrayList<>();
        }
    }

    /**
     * Wait for all matching elements to be present in the DOM.
     * Mirrors Python {@code wait_for_elements_exist}.
     */
    public void waitForElementsExist(By locator) {
        waitForElementsExist(locator, defaultTimeout);
    }

    public void waitForElementsExist(By locator, int timeoutSeconds) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        shortWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    // ------------------------------------------------------------------
    // Click
    // ------------------------------------------------------------------

    /** Click an element, waiting for it to be clickable. */
    public void clickElement(By locator) {
        clickElement(locator, defaultTimeout);
    }

    public void clickElement(By locator, int timeoutSeconds) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        WebElement element = shortWait.until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
    }

    /** Click a raw element (assumes it was already located). */
    public void clickElement(WebElement element) {
        element.click();
    }

    // ------------------------------------------------------------------
    // Text input
    // ------------------------------------------------------------------

    /** Clear and type into an element. */
    public void inputText(By locator, String text) {
        inputText(locator, text, defaultTimeout);
    }

    public void inputText(By locator, String text, int timeoutSeconds) {
        WebElement element = findElement(locator, timeoutSeconds);
        element.clear();
        element.sendKeys(text);
    }

    /** Type into a raw element. */
    public void inputText(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
    }

    // ------------------------------------------------------------------
    // Text retrieval
    // ------------------------------------------------------------------

    /** Get the visible text of a single element. */
    public String getElementText(By locator) {
        return getElementText(locator, defaultTimeout);
    }

    public String getElementText(By locator, int timeoutSeconds) {
        return findElement(locator, timeoutSeconds).getText();
    }

    public String getElementText(WebElement element) {
        return element.getText();
    }

    /** Get text from a list of elements. */
    public List<String> getElementsText(By locator) {
        return getElementsText(locator, defaultTimeout);
    }

    public List<String> getElementsText(By locator, int timeoutSeconds) {
        List<WebElement> elements = findElements(locator, timeoutSeconds);
        List<String> texts = new ArrayList<>();
        for (WebElement el : elements) {
            texts.add(el.getText());
        }
        return texts;
    }

    public List<String> getElementsText(List<WebElement> elements) {
        List<String> texts = new ArrayList<>();
        for (WebElement el : elements) {
            texts.add(el.getText());
        }
        return texts;
    }

    // ------------------------------------------------------------------
    // Attribute
    // ------------------------------------------------------------------

    /** Get a DOM attribute from a located element. */
    public String getElementAttribute(By locator, String attribute) {
        return getElementAttribute(locator, attribute, defaultTimeout);
    }

    public String getElementAttribute(By locator, String attribute, int timeoutSeconds) {
        return findElement(locator, timeoutSeconds).getAttribute(attribute);
    }

    public String getElementAttribute(WebElement element, String attribute) {
        return element.getAttribute(attribute);
    }

    // ------------------------------------------------------------------
    // Visibility check
    // ------------------------------------------------------------------

    /** Returns true if the element is visible within the given seconds. */
    public boolean isElementVisible(By locator, int timeoutSeconds) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isElementVisible(By locator) {
        return isElementVisible(locator, defaultTimeout);
    }

    public boolean isElementDisplayed(WebElement element) {
        return element.isDisplayed();
    }

    // ------------------------------------------------------------------
    // JavaScript helpers
    // ------------------------------------------------------------------

    /**
     * Set an element's value via JavaScript (for read-only / special inputs).
     * Mirrors Python {@code set_element_value}.
     */
    public void setElementValue(By locator, String value) {
        setElementValue(locator, value, defaultTimeout);
    }

    public void setElementValue(By locator, String value, int timeoutSeconds) {
        WebElement element = findElement(locator, timeoutSeconds);
        setElementValue(element, value);
    }

    public void setElementValue(WebElement element, String value) {
        executeScript("arguments[0].value = arguments[1];", element, value);
    }

    /**
     * Scroll an element into view (smooth, centered).
     * Mirrors Python {@code scroll_to_element}.
     */
    public void scrollToElement(By locator) {
        scrollToElement(locator, defaultTimeout);
    }

    public void scrollToElement(By locator, int timeoutSeconds) {
        WebElement element = findElement(locator, timeoutSeconds);
        scrollToElement(element);
    }

    public void scrollToElement(WebElement element) {
        executeScript(
            "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
            element);
    }

    /** Execute an arbitrary script. */
    public Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    // ------------------------------------------------------------------
    // Screenshot (mirrors Python Common.capture_screenshot)
    // ------------------------------------------------------------------

    /** Save a screenshot with an auto-incremented filename. */
    public void captureScreenshot() {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            // Find the next available screenshot number
            int maxNum = 0;
            Pattern pattern = Pattern.compile("^selenium-screenshot-(\\d+)\\.png$");
            File[] files = new File(".").listFiles();
            if (files != null) {
                for (File f : files) {
                    Matcher m = pattern.matcher(f.getName());
                    if (m.matches()) {
                        int num = Integer.parseInt(m.group(1));
                        if (num > maxNum) maxNum = num;
                    }
                }
            }

            int nextNum = maxNum + 1;
            Path dest = Paths.get("selenium-screenshot-" + nextNum + ".png");
            Files.copy(src.toPath(), dest);
            System.out.println("Screenshot saved: " + dest.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
        }
    }
}
