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

public class OrganizationCucumberTestDefinition extends CucumberTestDefinition {
    private static final String CREATE_ORGANIZATION_OBA_PATH =
            "test-data/json/mambuAPI/create-organization/createOrganizationResponseOBA.json";
    private static final String FIND_ORGANIZATION_RESPONSE_OBA_PATH
            = "test-data/json/mambuAPI/find-organization/findOrganizationByIdResponseOBA.json";
    private HttpResponse<String> createOrganizationResponse;
    private HttpResponse<String> findPersonResponse;
    private static String organizationIdCreated;

    /**
     * Scenario: Create a Organization
     */
    @Given("I have the organization payload located at {string} that I want to create an organization")
    public void readPayloadFromPath(String path) throws URISyntaxException, IOException {
        payload = TestResourceReader
                .readFileAsString(path);
    }

    @When("I create an organization using the API")
    public void requestCreateOrganization() {
        createOrganizationResponse = restFactory.post(LOCALHOST_URL + "/organizations", payload, true);
        organizationIdCreated = extractValueFromJson(createOrganizationResponse.body(), "organizationId");
    }

    @Then("I check that the status code coming from createOrganization operation response is {string}")
    public void checkResponseHttpStatus(String httpStatus) {
        assertEquals(Integer.valueOf(httpStatus), createOrganizationResponse.statusCode());
    }

    @And("I validate that the API response for createOrganization has the required fields using the validation rules engine")
    public void validateCreateOrganizationAPIResponse() throws URISyntaxException, IOException, JSONException {
        JSONAssert.assertEquals(createOrganizationResponse.body(), TestResourceReader
                        .readFileAsString(CREATE_ORGANIZATION_OBA_PATH),
                JSONCompareMode.STRICT);
        OpenApiResponseValidator
                .validateResponseAgainstSwaggerConstraints("/organizations",
                        Request.Method.POST,
                        createOrganizationResponse);

    }

    @And("I get the organization id")
    public void findOrganizationCreatedById() {
        findPersonResponse = restFactory.get(LOCALHOST_URL + "/organizations/" + organizationIdCreated);
        assertNotNull(findPersonResponse.body());
    }

    @And("I validate that the fields on the createOrganization are present on the getOrganization")
    public void validateOrganizationRequestWithPersonResponse() throws JSONException {
        JSONAssert.assertEquals(createOrganizationResponse.body(), findPersonResponse.body(), JSONCompareMode.STRICT);
    }

    /**
     * Scenario: Retrieve an Organization
     */
    @When("I retrieve an organization with the id {string} using the API")
    public void findOrganizationById(String organization) {
        findPersonResponse = restFactory.get(LOCALHOST_URL + "/organizations/" + organization);
        assertNotNull(findPersonResponse.body());
    }
    @Then("I check that the status code coming from getOrganization from the API response is {string}")
    public void checkResponseGetOrganizationHttpStatus(String httpStatus) {
        assertEquals(Integer.valueOf(httpStatus), findPersonResponse.statusCode());
    }

    @And("I validate that the API response from findOrganization has the required fields using the validation rules engine")
    public void validateFindOrganizationAPIResponse() throws URISyntaxException, IOException, JSONException {
        JSONAssert.assertEquals(findPersonResponse.body(), TestResourceReader
                        .readFileAsString(FIND_ORGANIZATION_RESPONSE_OBA_PATH),
                JSONCompareMode.STRICT);
        OpenApiResponseValidator
                .validateResponseAgainstSwaggerConstraints("/organizations/{organizationId}",
                        Request.Method.GET,
                        findPersonResponse);

    }
}
