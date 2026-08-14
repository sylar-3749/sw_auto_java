package common;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriverService;

import java.io.File;
import java.time.Duration;

/**
 * Singleton WebDriver manager matching the Python CreateWebdriverInstance.
 *
 * <p>Creates a ChromeDriver with:
 * <ul>
 *   <li>Headless mode on Linux (with --disable-dev-shm-usage, --disable-gpu)</li>
 *   <li>Maximised window on other platforms</li>
 *   <li>--no-sandbox and --disable-web-security flags everywhere</li>
 *   <li>CHROME_BIN / CHROME_DRIVER environment variable support</li>
 * </ul>
 */
public class WebDriverManager {

    private static WebDriver driver;

    private WebDriverManager() {
        // Private constructor — singleton
    }

    // ------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------

    /** Returns the current WebDriver instance, creating one if necessary. */
    public static WebDriver getDriver() {
        if (driver == null) {
            initializeDriver();
        }
        return driver;
    }

    /** Navigate to a URL using the shared driver. */
    public static void navigateToUrl(String url) {
        getDriver().get(url);
    }

    /** Quit the driver and null the reference. */
    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    // ------------------------------------------------------------------
    // Internal
    // ------------------------------------------------------------------

    private static void initializeDriver() {
        ChromeOptions options = new ChromeOptions();

        // -- Platform-specific options (mirrors CreateWebdriverInstance.py) --
        String osName = System.getProperty("os.name", "").toLowerCase();
        if (osName.contains("linux")) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-gpu");
        } else {
            options.addArguments("--start-maximized");
        }

        // Common flags
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-web-security");
        options.setExperimentalOption("useAutomationExtension", false);

        // Environment-variable overrides
        String chromeBin = System.getenv("CHROME_BIN");
        if (chromeBin != null && !chromeBin.isEmpty()) {
            options.setBinary(chromeBin);
        }

        String chromeDriverPath = System.getenv("CHROME_DRIVER");
        if (chromeDriverPath != null && !chromeDriverPath.isEmpty()) {
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);
            ChromeDriverService service =
                new ChromeDriverService.Builder()
                    .usingDriverExecutable(new File(chromeDriverPath))
                    .build();
            driver = new ChromeDriver(service, options);
        } else {
            driver = new ChromeDriver(options);
        }

        // Implicit wait
        driver.manage().timeouts().implicitlyWait(
            Duration.ofSeconds(GlobalVariables.IMPLICIT_WAIT));
    }
}
