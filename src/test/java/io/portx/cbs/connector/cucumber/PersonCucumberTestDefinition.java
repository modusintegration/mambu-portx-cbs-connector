package io.portx.cbs.connector.cucumber;


import com.atlassian.oai.validator.model.Request;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.portx.camel.test.util.TestResourceReader;
import io.portx.cbs.connector.Person;
import io.portx.cbs.connector.PersonRequest;
import io.portx.cbs.connector.cucumber.util.OpenApiResponseValidator;
import io.portx.cbs.connector.cucumber.util.WiremockFactory;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PersonCucumberTestDefinition extends CucumberTestDefinition {
    private Person findPersonByIdResponse;
    private Person createPersonResponse;
    private String payload;
    private HttpResponse<String> personCreationResponse;
    private HttpResponse<String> findPersonResponse;
    public static String personIdCreated;

    @BeforeAll
    public static void beforeAll() {
        WiremockFactory.init();
    }

    @AfterAll
    public static void afterAll() {
        WiremockFactory.stop();
    }

    /**
     * Scenario: Create a Person
     */
    @Given("I have the person payload located at {string} that I want to create a person")
    public void readPayloadFromPath(String path) throws URISyntaxException, IOException {
        payload = TestResourceReader
                .readFileAsString(path);
    }

    @When("I create a person using the API")
    public void requestCreatePerson() throws URISyntaxException, IOException, JSONException {
        personCreationResponse = restFactory.post(LOCALHOST_URL + "/persons", payload);
        JSONAssert.assertEquals(personCreationResponse.body(), TestResourceReader
                        .readFileAsString("test-data/json/mambuAPI/create-person/createPersonResponseOBA.json"),
                JSONCompareMode.STRICT);
        personIdCreated = extractValueFromJson(personCreationResponse.body(), "personId");
    }

    @Then("I check that the status code coming from the API response is {string}")
    public void checkResponseHttpStatus(String httpStatus) {
        assertEquals(Integer.valueOf(httpStatus), personCreationResponse.statusCode());
    }

    @And("I validate that the API response has the required fields using the validation rules engine")
    public void validateCreatePersonAPIResponse() {
        OpenApiResponseValidator
                .validateResponseAgainstSwaggerConstraints("/persons",
                        Request.Method.POST,
                        personCreationResponse);

    }

    @And("I get the person id")
    public void findPersonCreatedById() {
        findPersonResponse = restFactory.get(LOCALHOST_URL + "/persons/" + personIdCreated);
        findPersonByIdResponse = gson.fromJson(findPersonResponse.body(), Person.class);
        assertNotNull(findPersonByIdResponse);
    }

    @And("I validate that the fields on the requests are present on the response")
    public void validatePersonRequestWithPersonResponse() throws JSONException {
        JSONAssert.assertEquals(personCreationResponse.body(), findPersonResponse.body(), JSONCompareMode.STRICT);
    }

    /**
     * Scenario: Retrieve a Person
     */
    @When("I retrieve a person with the id {string} using the API")
    public void findPersonById(String personId) {
        findPersonResponse = restFactory.get(LOCALHOST_URL + "/persons/" + personId);
        assertNotNull(findPersonResponse.body());
    }
    @Then("I check that the status code coming from getPerson from the API response is {string}")
    public void checkResponseGetPersonHttpStatus(String httpStatus) {
        assertEquals(Integer.valueOf(httpStatus), findPersonResponse.statusCode());
    }

    @And("I validate that the API response from findPerson has the required fields using the validation rules engine")
    public void validateFindPersonAPIResponse() {
        OpenApiResponseValidator
                .validateResponseAgainstSwaggerConstraints("/persons/{personId}",
                        Request.Method.GET,
                        findPersonResponse);
    }
}
