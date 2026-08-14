package pages;

import common.CommonUtils;
import common.DataProvider;
import common.GlobalVariables;
import common.WebDriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;

/**
 * Cucumber step definitions for the login feature.
 *
 * <p>Mirrors the Python pytest-bdd steps in {@code test_login_steps.py}.
 */
public class LoginSteps {

    private WebDriver driver;
    private LoginPage loginPage;

    // ------------------------------------------------------------------
    // Cucumber lifecycle hooks
    // ------------------------------------------------------------------

    @Before
    public void setUp() {
        driver = WebDriverManager.getDriver();
        loginPage = new LoginPage(driver);
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed() && driver != null) {
            // Capture screenshot on failure (matches Python behaviour in ElementOperations)
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", scenario.getName() + "_failure");
            CommonUtils.captureScreenshot(driver);
        }
        WebDriverManager.quitDriver();
    }

    // ------------------------------------------------------------------
    // Given
    // ------------------------------------------------------------------

    @Given("访问登录页面")
    public void openLoginPage() {
        loginPage.openUrl(GlobalVariables.BASE_URL);
    }

    // ------------------------------------------------------------------
    // When
    // ------------------------------------------------------------------

    @When("输入用户名 {string} 和密码 {string}")
    public void inputLoginCredentials(String username, String password) {
        loginPage.inputUsername(username);
        loginPage.inputPassword(password);
    }

    /**
     * Data-driven step — loads credentials from the JSON data file
     * and iterates through each row.
     *
     * <p>Matches Python's {@code conftest.invalid_login_data} fixture approach.
     * The scenario itself runs once per row in the Scenario Outline.
     */
    @When("输入用户名和密码")
    public void inputInvalidLoginCredentials() {
        try {
            List<Map<String, String>> records = DataProvider.loadTestDataAsStrings(
                "login_validations.json");
            if (!records.isEmpty()) {
                // Use the first row (Scenario Outline drives multiple calls via Examples)
                Map<String, String> row = records.get(0);
                String username = row.getOrDefault("username", "");
                String password = row.getOrDefault("password", "");
                loginPage.inputUsername(username);
                loginPage.inputPassword(password);
            } else {
                loginPage.inputUsername("invalid_user");
                loginPage.inputPassword("invalid_password");
            }
        } catch (Exception e) {
            System.err.println("Failed to load test data: " + e.getMessage());
            loginPage.inputUsername("invalid_user");
            loginPage.inputPassword("invalid_password");
        }
    }

    /**
     * Loads invalid credentials from the JSON data file and attempts login
     * with each row.  Runs all rows within a single scenario execution,
     * matching the Python {@code @data_provider} decorator pattern.
     */
    @When("从数据文件加载无效凭证并登录")
    public void inputCredentialsFromJsonFile() {
        try {
            List<Map<String, String>> records = DataProvider.loadTestDataAsStrings(
                "login_validations.json");
            boolean anyFailed = false;
            StringBuilder failures = new StringBuilder();

            for (Map<String, String> row : records) {
                String username = row.getOrDefault("username", "");
                String password = row.getOrDefault("password", "");

                System.out.println("Testing with — username: '" + username
                    + "', password: '" + password + "'");

                loginPage.inputUsername(username);
                loginPage.inputPassword(password);
                loginPage.clickLoginButton();

                if (!loginPage.isErrorMessageDisplayed()) {
                    anyFailed = true;
                    failures.append("\n  - Row '").append(row.get("name"))
                        .append("': 错误提示没有显示");
                }
            }

            if (anyFailed) {
                throw new AssertionError(
                    "部分数据驱动测试失败:" + failures.toString());
            }
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("无法加载测试数据文件: " + e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------
    // And
    // ------------------------------------------------------------------

    @And("点击登录按钮")
    public void clickLoginButton() {
        loginPage.clickLoginButton();
    }

    // ------------------------------------------------------------------
    // Then
    // ------------------------------------------------------------------

    @Then("显示错误提示")
    public void verifyErrorMessageDisplayed() {
        assert loginPage.isErrorMessageDisplayed() : "错误提示没有显示";
    }
}
