package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Login page object — mirrors the Python {@code LoginPage} class.
 *
 * <p>All element interaction goes through {@link BasePage} methods,
 * which wrap Selenium's WebDriverWait.
 */
public class LoginPage extends BasePage {

    // Page element locators
    private final By usernameInput = By.cssSelector("input#username");
    private final By passwordInput = By.cssSelector("input#password");
    private final By loginButton  = By.cssSelector("input#Login");
    private final By errorMessage = By.cssSelector("#error.loginError");
    private final By validateCode = By.cssSelector("input#emc");
    private final By validateButton  = By.cssSelector("input#save");
    
    // ------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage(WebDriver driver, int timeoutSeconds) {
        super(driver, timeoutSeconds);
    }

    // ------------------------------------------------------------------
    // Actions  (mirror Python LoginPage methods)
    // ------------------------------------------------------------------

    /** Type the username into the username field. */
    public void inputUsername(String username) {
        inputText(usernameInput, username);
    }
    
    /** Type the password into the password field. */
    public void inputPassword(String password) {
        inputText(passwordInput, password);
    }
    
    /** Click the Login button (scrolls it into view first). */
    public void clickLoginButton() {
        scrollToElement(loginButton);
        clickElement(loginButton);
    }
    
    /** Returns true if the login error banner is visible within 2 seconds. */
    public boolean isErrorMessageDisplayed() {
        return isElementVisible(errorMessage, 2);
    }
    
    // ------------------------------------------------------------------
    // Extended / convenience
    // ------------------------------------------------------------------
    
    /** Get the error message text (empty string if not visible). */
    public String getErrorMessageText() {
        try {
            return getElementText(errorMessage, 2);
        } catch (Exception e) {
            return "";
        }
    }

    // ------------------------------------------------------------------
    // Salesforce login verification
    // ------------------------------------------------------------------

    /** Type the verification code. */
    public void inputVerificationCode(String code) {
        inputText(validateCode, code);
    }

    /** Click the validate button. */
    public void clickValidateButton() {
        scrollToElement(validateButton);
        clickElement(validateButton);
    }
}
