package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources/features/nbp.feature",
    glue = "stepdefinitions",
    plugin = {
        "pretty",
        "html:target/reports/cucumber-report.html",
        "json:target/reports/cucumber-report.json"})

public class TestRunner extends AbstractTestNGCucumberTests {
}
