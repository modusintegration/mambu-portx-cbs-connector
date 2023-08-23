package io.portx.cbs.connector.cucumber.util;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import io.portx.camel.test.util.TestResourceReader;
import wiremock.org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.net.URISyntaxException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WiremockFactory {

    private static final Logger logger = Logger.getLogger(WiremockFactory.class.getName());
    private static final String CREATE_PERSON_REQUEST_MAMBU = "test-data/json/mambuAPI/create-person/createPersonRequestMambu.json";
    private static final String CREATE_PERSON_RESPONSE_MAMBU = "test-data/json/mambuAPI/create-person/createPersonResponseMambu.json";
    private static final String FIND_PERSON_BY_ID_RESPONSE_MAMBU = "test-data/json/mambuAPI/find-person/findPersonResponseMambu.json";
    public static final StringValuePattern MAMBU_AUTHORIZATION_TOKEN = WireMock.equalTo("Basic dGVzdDp0ZXN0");
    public static final StringValuePattern MAMBU_HEADER = WireMock.equalTo("application/vnd.mambu.v2+json");
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
        try {
            // Mock requests and responses
            WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/api/clients"))
                    .withHeader("Authorization", MAMBU_AUTHORIZATION_TOKEN)
                    .withHeader("Accept", MAMBU_HEADER)
                    .withRequestBody(WireMock.equalToJson(TestResourceReader
                            .readFileAsString(CREATE_PERSON_REQUEST_MAMBU)))
                    .willReturn(WireMock.aResponse()
                            .withStatus(202)
                            .withHeader("Content-Type", "application/json")
                            .withBody(TestResourceReader
                                    .readFileAsString(CREATE_PERSON_RESPONSE_MAMBU))));
            WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/api/clients/12345"))
                    .willReturn(WireMock.aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(TestResourceReader
                                    .readFileAsString(FIND_PERSON_BY_ID_RESPONSE_MAMBU))));
        } catch (URISyntaxException e) {
            logger.log(Level.SEVERE, "An error occurred:", ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred:", ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        } catch (RuntimeException ex) {
            logger.log(Level.SEVERE, "An error occurred:", ExceptionUtils.getStackTrace(ex));
            throw new RuntimeException(ex);
        }
    }

}
