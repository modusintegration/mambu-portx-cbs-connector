package io.portx.cbs.connector.cucumber.util;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import io.portx.camel.test.util.TestResourceReader;

import java.io.IOException;
import java.net.URISyntaxException;

public class WiremockFactory {

    private static final String BEARER_TOKEN_RESPONSE = "test-data/json/csi/bearerToken.json";
    private static final String CREATE_PERSON_REQUEST_MAMBU = "test-data/json/mambuAPI/create-person/createPersonRequestMambu.json";
    private static final String CREATE_PERSON_RESPONSE_MAMBU = "test-data/json/mambuAPI/create-person/createPersonResponseMambu.json";
    private static final String FIND_PERSON_BY_ID_RESPONSE_MAMBU = "test-data/json/mambuAPI/find-person/findPersonResponseMambu.json";
    public static final StringValuePattern MAMBU_AUTHORIZATION_TOKEN = WireMock.equalTo("Basic dGVzdDp0ZXN0");
    public static final StringValuePattern MAMBU_HEADER = WireMock.equalTo("application/vnd.mambu.v2+json<");
    private static WireMockServer wireMockServer;
    private static final int WIREMOCK_PORT = 8043;
    private static final WireMockConfiguration config = WireMockConfiguration
            .wireMockConfig()
            .httpsPort(WIREMOCK_PORT)
//            .keystorePath(WiremockFactory.class.getResource("/wiremock/https/key/keystore.jks").getPath())
            .keystorePath(WiremockFactory.class.getClassLoader().getResource("wiremock/https/key/keystore.jks").getPath())
            .keystorePassword("password");
    public static void init() {
        wireMockServer =  new WireMockServer(config);
        wireMockServer.start();
//        WireMock.configureFor(WIREMOCK_PORT);
        createWiremockResponsesForMambuIntegration();
    }

    public static void stop() {
        wireMockServer.stop();
    }

    private static void createWiremockResponsesForMambuIntegration() {
        try {
            //Mock Bearer token request
/*            WireMock.stubFor(WireMock.post(WireMock.urlEqualTo("/connect/token"))
                    .willReturn(WireMock.aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(TestResourceReader
                                    .readFileAsString(BEARER_TOKEN_RESPONSE))));*/
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
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
