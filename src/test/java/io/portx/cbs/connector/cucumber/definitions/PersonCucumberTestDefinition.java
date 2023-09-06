package io.portx.cbs.connector.cucumber.definitions;


import com.atlassian.oai.validator.model.Request;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.portx.camel.test.util.TestResourceReader;
import io.portx.cbs.connector.cucumber.util.OpenApiResponseValidator;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PersonCucumberTestDefinition extends CucumberTestDefinition {
    private static final String CREATE_PERSON_RESPONSE_OBA_PATH =
            "test-data/json/mambuAPI/create-person/createPersonResponseOBA.json";
    private static final String FIND_PERSON_BY_ID_RESPONSE_OBA_PATH
            = "test-data/json/mambuAPI/find-person/findPersonByIdResponseOBA.json";
    private static final String FIND_PERSON_BY_FIRSTNAME_AND_LASTNAME_RESPONSE_OBA_PATH =
            "test-data/json/mambuAPI/find-person/findPersonByFirstNameAndLastNameResponseOBA.json";
    private HttpResponse<String> personCreationResponse;
    private HttpResponse<String> findPersonByIdResponse;
    private HttpResponse<String> findPersonByFirstNameAndLastNameResponse;
    private static String personIdCreated;

    /**
     * Scenario: Create a Person
     */
    @Given("I have the person payload located at {string} that I want to create a person")
    public void readPayloadFromPath(String path) throws URISyntaxException, IOException {
        payload = TestResourceReader
                .readFileAsString(path);
    }

    @When("I create a person using the API")
    public void requestCreatePerson() {
        personCreationResponse = restFactory.post(LOCALHOST_URL + "/persons", payload, true);
        personIdCreated = extractValueFromJson(personCreationResponse.body(), "personId");
    }

    @Then("I check that the status code coming from createPerson from the API response is {string}")
    public void checkResponseHttpStatus(String httpStatus) {
        assertEquals(Integer.valueOf(httpStatus), personCreationResponse.statusCode());
    }

    @And("I validate that the API response from createPerson has the required fields using the validation rules engine")
    public void validateCreatePersonAPIResponse() throws URISyntaxException, IOException, JSONException {
        JSONAssert.assertEquals(personCreationResponse.body(), TestResourceReader
                        .readFileAsString(CREATE_PERSON_RESPONSE_OBA_PATH),
                JSONCompareMode.STRICT);
        OpenApiResponseValidator
                .validateResponseAgainstSwaggerConstraints("/persons",
                        Request.Method.POST,
                        personCreationResponse);

    }

    @And("I get the person id")
    public void findPersonCreatedById() {
        findPersonByIdResponse = restFactory.get(LOCALHOST_URL + "/persons/" + personIdCreated);
        assertNotNull(findPersonByIdResponse.body());
    }

    @And("I validate that the fields on the createPerson are present on the getPerson")
    public void validatePersonRequestWithPersonResponse() throws JSONException {
        JSONAssert.assertEquals(personCreationResponse.body(), findPersonByIdResponse.body(), JSONCompareMode.STRICT);
    }

    /**
     * Scenario: Retrieve a Person by Id
     */
    @When("I retrieve a person with the id {string} using the API")
    public void findPersonById(String personId) {
        findPersonByIdResponse = restFactory.get(LOCALHOST_URL + "/persons/" + personId);
        assertNotNull(findPersonByIdResponse.body());
    }
    @Then("I check that the status code coming from getPerson from the API response is {string}")
    public void checkResponseGetPersonHttpStatus(String httpStatus) {
        assertEquals(Integer.valueOf(httpStatus), findPersonByIdResponse.statusCode());
    }

    @And("I validate that the API response from findPerson has the required fields using the validation rules engine")
    public void validateFindPersonByIdAPIResponse() throws URISyntaxException, IOException, JSONException {
        JSONAssert.assertEquals(findPersonByIdResponse.body(), TestResourceReader
                        .readFileAsString(FIND_PERSON_BY_ID_RESPONSE_OBA_PATH),
                JSONCompareMode.STRICT);
        OpenApiResponseValidator
                .validateResponseAgainstSwaggerConstraints("/persons/{personId}",
                        Request.Method.GET,
                        findPersonByIdResponse);

    }

    /**
     * Scenario: Retrieve a Person by first name and last name
     */
    @When("I retrieve a person with the first name {string} and last name {string} using the API")
    public void findPersonByFirstNameAndLastName(String firstName, String lastName) {
        findPersonByFirstNameAndLastNameResponse = restFactory.get(LOCALHOST_URL + "/persons/?firstName.eq="
                + firstName + "&lastName.eq=" + lastName);
        assertNotNull(findPersonByFirstNameAndLastNameResponse.body());
    }

    @Then("I check that the status code coming from findPersonByFirstNameAndLastName from the API response is {string}")
    public void checkResponseFromFindPersonByFirstNameAndLastNameHttpStatus(String httpStatus) {
        assertEquals(Integer.valueOf(httpStatus), findPersonByFirstNameAndLastNameResponse.statusCode());
    }

    @And("I validate that the API response from findPersonByFirstNameAndLastName has the required fields using the validation rules engine")
    public void validateFindPersonByFirstNameAndLastNameAPIResponse()
            throws URISyntaxException, IOException, JSONException {
        JSONAssert.assertEquals(findPersonByFirstNameAndLastNameResponse.body(), TestResourceReader
                        .readFileAsString(FIND_PERSON_BY_FIRSTNAME_AND_LASTNAME_RESPONSE_OBA_PATH),
                JSONCompareMode.STRICT);
        OpenApiResponseValidator
                .validateResponseAgainstSwaggerConstraints("/persons",
                        Request.Method.GET,
                        findPersonByFirstNameAndLastNameResponse);

    }

    /**
     * Scenario: Test Schema validator Request Related Parties PartyType
     */
    @When("I try to create a person using the API")
    public void tryToRequestCreatePerson() {
        personCreationResponse = restFactory.post(LOCALHOST_URL + "/persons", payload, false);
    }

    @Then("I receive a schema validator exception error for Person scenario like this json response {string}")
    public void matchResponseWithSchemaValidator(String jsonPath) throws URISyntaxException, IOException, JSONException {
        JSONAssert.assertEquals(personCreationResponse.body(), TestResourceReader
                        .readFileAsString(jsonPath),
                JSONCompareMode.STRICT);
    }
}
