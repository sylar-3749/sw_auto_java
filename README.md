# Introduction 
This is a demo project showcasing the same Salesforce Lightning tests implemented across three automation frameworks side-by-side: Java (Cucumber + JUnit 5 + Maven). It serves as a learning/comparison reference rather than a production test suite.

# Getting Started

1. Download [chromedriver](https://googlechromelabs.github.io/chrome-for-testing/), be aware of the chrome browser version; Then put it under the *\\Python\\* folder, like: *..\Users\${userfolder}\AppData\Local\Programs\Python\Python310*
3. Download [VSCode](https://code.visualstudio.com/download), and select your preferred version for your mac or windows desktop.

# Java

**Build tool:** Maven (`pom.xml`)

```
./
  pom.xml            # Maven config: Cucumber 7.14, Selenium 4.14, JUnit 5.9, Java 21
  main/              # Standalone Java classes (Request.java, Mail.java, Response.java)
  src/test/java/com/test/
    pages/           # Page Object Model (BasePage + LoginPage extends BasePage)
    steps/           # Cucumber step definitions (LoginSteps.java)
    runners/         # CucumberTestRunner.java (JUnit Platform Suite)
    utils/           # GlobalVariables.java, WebDriverManager.java (singleton)
  src/test/resources/
    features/        # Gherkin .feature files (Cucumber)
    junit-platform.properties
```

## Running tests

```bash
# Run all Cucumber tests via Maven
mvn clean test

# Run a specific test runner
mvn test -Dtest=CucumberTestRunner
```

## Architecture notes

- `WebDriverManager` is a singleton holding a static `WebDriver` instance, initialized lazily.
- `BasePage` wraps `WebDriverWait` with `findElement`, `inputText`, `clickElement`, and `isElementVisible` methods. All page classes extend it.
- `CucumberTestRunner` uses the JUnit Platform Suite API (`@Suite`, `@IncludeEngines("cucumber")`) rather than the older `@RunWith(Cucumber.class)` pattern.
- The `main/` directory contains standalone utility classes (`Request.java`, `Mail.java`, `Response.java`) that are not part of the test framework — they are independent API/HTTP utilities.

## Java Launch Config

Add to VSCode *launch.json*:

```json
{
    "configurations": [
        {
            "type": "java",
            "name": "test",
            "request": "launch",
            "mainClass": "__java__.test.test",
            "projectName": "your_project_name"
        }
    ]
}
```
