package io.portx.cbs.connector.cucumber.util;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import io.portx.camel.test.util.TestResourceReader;
import org.apache.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

public class WiremockFactory {

    private static final String CREATE_PERSON_REQUEST_MAMBU = "test-data/json/mambuAPI/create-person/createPersonRequestMambu.json";
    private static final String CREATE_PERSON_RESPONSE_MAMBU = "test-data/json/mambuAPI/create-person/createPersonResponseMambu.json";
    private static final String FIND_PERSON_BY_ID_RESPONSE_MAMBU = "test-data/json/mambuAPI/find-person/findPersonByIdResponseMambu.json";
    private static final String FIND_PERSON_BY_FIRSTNAME_AND_LAST_NAME_RESPONSE_MAMBU
            = "test-data/json/mambuAPI/find-person/findPersonByFirstNameAndLastNameResponseMambu.json";
    private static final StringValuePattern MAMBU_AUTHORIZATION_TOKEN = WireMock.equalTo("Basic dGVzdDp0ZXN0");
    private static final StringValuePattern MAMBU_HEADER = WireMock.equalTo("application/vnd.mambu.v2+json");
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String MAMBU_POST_CLIENTS_URL = "/api/clients";
    private static final String MAMBU_GET_CLIENT_BY_ID_URL = "/api/clients/12345";
    private static final String MAMBU_FIND_CLIENTS_BY_FIRSTNAME_AND_LASTNAME_URL = "/api/clients:search?detailsLevel=FULL";
    private static final String MAMBU_POST_GROUPS_URL = "/api/groups";
    private static final String MAMBU_POST_DEPOSITS_URL = "/api/deposits";
    private static final String MAMBU_POST_DEPOSITS_CHANGE_STATE_URL = "/api/deposits/99999999:changeState";
    private static final String MAMBU_POST_DEPOSITS_TRANSACTION_URL = "/api/deposits/99999999/deposit-transactions";
    private static final String MAMBU_GET_ACCOUNT_BY_ID_URL = "/api/deposits/99999999";
    private static final String CREATE_ORGANIZATION_REQUEST_MAMBU = "test-data/json/mambuAPI/create-organization/createOrganizationRequestMambu.json";
    private static final String CREATE_ORGANIZATION_RESPONSE_MAMBU = "test-data/json/mambuAPI/create-organization/createOrganizationResponseMambu.json";
    private static final String CREATE_ACCOUNT_REQUEST_MAMBU = "test-data/json/mambuAPI/create-account/createAccountRequestMambu.json";
    private static final String CREATE_ACCOUNT_RESPONSE_MAMBU = "test-data/json/mambuAPI/create-account/createAccountResponseMambu.json";
    private static final String CHANGE_ACCOUNT_STATE_REQUEST_MAMBU = "test-data/json/mambuAPI/create-account/changeAccountStateRequestMambu.json";
    private static final String CHANGE_ACCOUNT_STATE_RESPONSE_MAMBU = "test-data/json/mambuAPI/create-account/changeAccountStateResponseMambu.json";
    private static final String DEPOSIT_ACCOUNT_REQUEST_MAMBU = "test-data/json/mambuAPI/create-account/depositAccountRequestMambu.json";
    private static final String DEPOSIT_ACCOUNT_RESPONSE_MAMBU = "test-data/json/mambuAPI/create-account/depositAccountResponseMambu.json";
    private static final String FIND_ACCOUNT_BY_ID_RESPONSE_MAMBU = "test-data/json/mambuAPI/create-account/findAccountByIdResponseMambu.json";
    private static final String MAMBU_GET_ORGANIZATION_BY_ID_URL = "/api/groups/12345";
    private static final String FIND_ORGANIZATION_BY_ID_RESPONSE_MAMBU = "test-data/json/mambuAPI/find-organization/findOrganizationByIdResponseMambu.json";
    private static final String FIND_PERSON_BY_FIRSTNAME_AND_LASTNAME_REQUEST_MAMBU =
            "test-data/json/mambuAPI/find-person/findPersonByFirstNameAndLastNameRequestMambu.json";

    ;
    private static WireMockServer wireMockServer;
    private static final int WIREMOCK_PORT = 8043;
    public static void init() {
        wireMockServer =  new WireMockServer(WIREMOCK_PORT);
        wireMockServer.start();
        WireMock.configureFor(WIREMOCK_PORT);
        createWiremockResponsesForMambuIntegration();
    }


    public static void stop() {
        wireMockServer.stop();
    }

    private static void createWiremockResponsesForMambuIntegration() {
        mockPersonOperations();
        mockOrganizationOperations();
        mockAccountOperations();
    }
    private static void mockAccountOperations() {
        mockPost(MAMBU_POST_DEPOSITS_URL,
                Optional.of(CREATE_ACCOUNT_REQUEST_MAMBU),
                CREATE_ACCOUNT_RESPONSE_MAMBU,
                HttpStatus.ACCEPTED.value());
        mockPost(MAMBU_POST_DEPOSITS_CHANGE_STATE_URL,
                Optional.of(CHANGE_ACCOUNT_STATE_REQUEST_MAMBU),
                CHANGE_ACCOUNT_STATE_RESPONSE_MAMBU,
                HttpStatus.ACCEPTED.value());
        mockPost(MAMBU_POST_DEPOSITS_TRANSACTION_URL,
                Optional.of(DEPOSIT_ACCOUNT_REQUEST_MAMBU),
                DEPOSIT_ACCOUNT_RESPONSE_MAMBU,
                HttpStatus.ACCEPTED.value());
        mockGet(MAMBU_GET_ACCOUNT_BY_ID_URL,
                FIND_ACCOUNT_BY_ID_RESPONSE_MAMBU,
                HttpStatus.OK.value());
    }

    private static void mockOrganizationOperations() {
        mockPost(MAMBU_POST_GROUPS_URL,
                Optional.of(CREATE_ORGANIZATION_REQUEST_MAMBU),
                CREATE_ORGANIZATION_RESPONSE_MAMBU,
                HttpStatus.ACCEPTED.value());
        mockGet(MAMBU_GET_ORGANIZATION_BY_ID_URL,
                FIND_ORGANIZATION_BY_ID_RESPONSE_MAMBU,
                HttpStatus.OK.value());
    }

    private static void mockPersonOperations() {
        mockPost(MAMBU_POST_CLIENTS_URL,
                Optional.of(CREATE_PERSON_REQUEST_MAMBU),
                CREATE_PERSON_RESPONSE_MAMBU,
                HttpStatus.ACCEPTED.value());
        mockGet(MAMBU_GET_CLIENT_BY_ID_URL,
                FIND_PERSON_BY_ID_RESPONSE_MAMBU,
                HttpStatus.OK.value());
        mockPost(MAMBU_FIND_CLIENTS_BY_FIRSTNAME_AND_LASTNAME_URL,
                Optional.of(FIND_PERSON_BY_FIRSTNAME_AND_LASTNAME_REQUEST_MAMBU),
                FIND_PERSON_BY_FIRSTNAME_AND_LAST_NAME_RESPONSE_MAMBU,
                HttpStatus.OK.value());
    }

    /**
     * Mocks an HTTP GET request for a specified URL with a pre-defined response.
     *
     * @param url          The URL to mock the GET request for.
     * @param responsePath The path to the file containing the response body.
     * @param httpStatus   The HTTP status code to be returned for the mock response.
     * @throws RuntimeException if there's an issue with URIs or I/O operations.
     */
    public static void mockGet(String url, String responsePath, int httpStatus) {
        try {
            WireMock.stubFor(WireMock.get(WireMock.urlPathMatching(url))
                    .willReturn(WireMock.aResponse()
                            .withStatus(httpStatus)
                            .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .withBody(TestResourceReader
                                    .readFileAsString(responsePath))));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Mocks an HTTP POST request for a specified URL with a pre-defined request body and response.
     *
     * @param url          The URL to mock the POST request for.
     * @param requestPath  The path to the file containing the request body (optional).
     * @param responsePath The path to the file containing the response body.
     * @param httpStatus   The HTTP status code to be returned in the mock response.
     * @throws RuntimeException If there is an issue with URIs or I/O operations.
     */
    public static void mockPost(String url, Optional<String> requestPath, String responsePath, int httpStatus) {
        requestPath.ifPresentOrElse(rp -> {
            try {
                WireMock.stubFor(WireMock.post(WireMock.urlEqualTo(url))
                        .withHeader(HttpHeaders.AUTHORIZATION, MAMBU_AUTHORIZATION_TOKEN)
                        .withHeader(HttpHeaders.ACCEPT, MAMBU_HEADER)
                        .withRequestBody(WireMock.equalToJson(TestResourceReader
                                .readFileAsString(rp)))
                        .willReturn(WireMock.aResponse()
                                .withStatus(httpStatus)
                                .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(TestResourceReader
                                        .readFileAsString(responsePath))));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, () -> {
            try {
                WireMock.stubFor(WireMock.post(WireMock.urlEqualTo(url))
                        .withHeader(HttpHeaders.AUTHORIZATION, MAMBU_AUTHORIZATION_TOKEN)
                        .withHeader(HttpHeaders.ACCEPT, MAMBU_HEADER)
                        .willReturn(WireMock.aResponse()
                                .withStatus(httpStatus)
                                .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(TestResourceReader
                                        .readFileAsString(responsePath))));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}