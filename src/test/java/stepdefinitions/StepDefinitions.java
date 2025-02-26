package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import parsing.Rate;

import java.util.List;
import java.util.function.Predicate;

public class StepDefinitions {
    private static final Logger logger = LoggerFactory.getLogger(StepDefinitions.class);
    private List<Rate> rates;

    @Given("Base URI is set")
    public void configureBaseURI() {
        RestAssured.baseURI = "http://api.nbp.pl/api/";
    }

    @When("Exchange rates are fetched from NBP API")
    public void exchangeRatesAreFetchedFromNbpApi() {
        ValidatableResponse response = RestAssured.given().contentType(ContentType.JSON)
            .when().get("exchangerates/tables/A").then();
        response.assertThat().statusCode(200);
        rates = response.extract().jsonPath().getList("rates[0]", Rate.class);
    }

    @Then("Rate for {string} {word} can be displayed")
    public void displayRate(String parameterValue, String parameterName) throws NotImplementedException {
        Predicate<Rate> parameterPredicate = switch (parameterName) {
            case "code" -> rate -> parameterValue.equals(rate.code());
            case "currency" -> rate -> parameterValue.equals(rate.currency());
            default -> throw new NotImplementedException(
                "No %s currency parameter implemented".formatted(parameterName));
        };

        rates.stream()
            .filter(parameterPredicate)
            .findFirst()
            .ifPresentOrElse(rate -> logger.info("'%s' rate: %f".formatted(parameterValue, rate.mid())),
                () -> Assert.fail("No value found for '%s' %s".formatted(parameterValue, parameterName)));
    }

    @Then("Rates {word} {float} can be displayed")
    public void ratesBelowOrAboveAreDisplayed(String conditionType, float rateValue) {
        Predicate<Rate> valuePredicate = switch (conditionType) {
            case "above" -> rate -> rate.mid() > rateValue;
            case "below" -> rate -> rate.mid() < rateValue;
            default -> throw new NotImplementedException(
                "No %s condition parameter implemented".formatted(conditionType));
        };
        List<Rate> filteredRates = rates.stream()
            .filter(valuePredicate)
            .toList();

        if (filteredRates.isEmpty()) {
            logger.warn("No value found for rate %s %f".formatted(conditionType, rateValue));
        } else {
            logger.info("Found following values for rates %s %f:".formatted(conditionType, rateValue));
            filteredRates.forEach(rate -> logger.info("   " + rate));
        }
    }
}
