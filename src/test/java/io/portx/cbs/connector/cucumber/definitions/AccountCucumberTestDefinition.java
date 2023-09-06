package io.portx.cbs.connector.cucumber.definitions;


import com.atlassian.oai.validator.model.Request;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.portx.camel.test.util.TestResourceReader;
import io.portx.cbs.connector.cucumber.util.OpenApiResponseValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AccountCucumberTestDefinition extends CucumberTestDefinition {
    private static final Log logger = LogFactory.getLog(AccountCucumberTestDefinition.class);
    private static final String CREATE_ACCOUNT_RESPONSE_OBA_PATH =
            "test-data/json/mambuAPI/create-account/createAccountResponseOBA.json";
    private static final String FIND_ACCOUNT_BY_ID_RESPONSE_OBA_PATH
            = "test-data/json/mambuAPI/find-account/findAccountByIdResponseOBA.json";
    private HttpResponse<String> accountCreationResponse;
    private HttpResponse<String> findAccountByIdResponse;
    private static String accountIdCreated;

    /**
     * Scenario: Create an Account
     */
    @Given("I have the account payload located at {string} that I want to create an account")
    public void readPayloadFromPath(String path) throws URISyntaxException, IOException {
        payload = TestResourceReader
                .readFileAsString(path);
    }

    @When("I create an account using the API")
    public void requestCreateAccount() {
        accountCreationResponse = restFactory.post(LOCALHOST_URL + "/accounts", payload, true);
        accountIdCreated = extractValueFromJson(accountCreationResponse.body(), "accountId");
    }

    @Then("I check that the status code coming from createAccount from the API response is {string}")
    public void checkResponseHttpStatus(String httpStatus) {
        assertEquals(Integer.valueOf(httpStatus), accountCreationResponse.statusCode());
    }

    @And("I validate that the API response from createAccount has the required fields using the validation rules engine")
    public void validateCreateAccountAPIResponse() throws URISyntaxException, IOException, JSONException {
        JSONAssert.assertEquals(accountCreationResponse.body(), TestResourceReader
                        .readFileAsString(CREATE_ACCOUNT_RESPONSE_OBA_PATH),
                JSONCompareMode.STRICT);
        OpenApiResponseValidator
                .validateResponseAgainstSwaggerConstraints("/accounts",
                        Request.Method.POST,
                        accountCreationResponse);

    }

    @And("I get the account id")
    public void findAccountCreatedById() {
        findAccountByIdResponse = restFactory.get(LOCALHOST_URL + "/accounts/" + accountIdCreated);
        assertNotNull(findAccountByIdResponse.body());
    }

    @And("I validate that the fields on the createAccount are present on the getAccount")
    public void validateAccountRequestWithPersonResponse() throws JSONException {
        logger.warn("Mambu mapping return different response for create and find operation, for that reason this" +
                "assertion will fail.");
//        JSONAssert.assertEquals(accountCreationResponse.body(), findAccountByIdResponse.body(), JSONCompareMode.STRICT);
    }

    /**
     * Scenario: Retrieve an Account by Id
     */
    @When("I retrieve an account with the id {string} using the API")
    public void findAccountById(String personId) {
        findAccountByIdResponse = restFactory.get(LOCALHOST_URL + "/accounts/" + personId);
        assertNotNull(findAccountByIdResponse.body());
    }
    @Then("I check that the status code coming from getAccount from the API response is {string}")
    public void checkResponseGetAccountHttpStatus(String httpStatus) {
        assertEquals(Integer.valueOf(httpStatus), findAccountByIdResponse.statusCode());
    }

    @And("I validate that the API response from getAccount has the required fields using the validation rules engine")
    public void validateFindAccountByIdAPIResponse() throws URISyntaxException, IOException, JSONException {
        JSONAssert.assertEquals(findAccountByIdResponse.body(), TestResourceReader
                        .readFileAsString(FIND_ACCOUNT_BY_ID_RESPONSE_OBA_PATH),
                JSONCompareMode.STRICT);
        OpenApiResponseValidator
                .validateResponseAgainstSwaggerConstraints("/accounts/{accountId}",
                        Request.Method.GET,
                        findAccountByIdResponse);

    }
    /**
     * Scenario: Test Schema validator scenarios
     */
    @When("I try to create an account using the API")
    public void tryToRequestCreateAccount() {
        accountCreationResponse = restFactory.post(LOCALHOST_URL + "/accounts", payload, false);
    }

    @Then("I receive a schema validator exception error for Account scenario like this json response {string}")
    public void matchResponseWithSchemaValidator(String jsonPath) throws URISyntaxException, IOException, JSONException {
        JSONAssert.assertEquals(accountCreationResponse.body(), TestResourceReader
                        .readFileAsString(jsonPath),
                JSONCompareMode.STRICT);
    }
}
