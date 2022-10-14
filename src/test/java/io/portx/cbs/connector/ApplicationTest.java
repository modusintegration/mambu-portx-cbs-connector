package io.portx.cbs.connector;


import io.portx.camel.test.APITest;
import io.portx.camel.test.util.TestResourceReader;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Application Tests")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes=Application.class)
public class ApplicationTest extends APITest {

//    @Test
//    @DisplayName("Test get branches endpoint")
//    public void testGetBranches() throws Exception {
//        Exchange exchange = sendTestRequest("direct:findBranches", ExchangePattern.InOut, new Processor() {
//            @Override
//            public void process(Exchange exchange) throws Exception {
//                Message in = exchange.getIn();
//                System.out.println(in);
//            }
//        });
//
//        String response = exchange.getIn().getBody(String.class);
//        assertNotNull(response, "Response is not null");
//        JSONAssert.assertEquals(TestResourceReader.readFileAsString("test-data/json/mambuAPI/getBranchesResponse.json"), response, true);
//    }

//    @Test
//    @DisplayName("Test create branch endpoint")
//    public void testCreateBranch() throws Exception {
//        Exchange exchange = sendTestRequest("direct:testCreateBranch", ExchangePattern.InOut, new Processor() {
//            @Override
//            public void process(Exchange exchange) throws Exception {
//                Message in = exchange.getIn();
//                in.setBody(TestResourceReader.readFileAsString("test-data/json/mambuAPI/createBranchRequest.json"));
//            }
//        });
//
//        String response = exchange.getIn().getBody(String.class);
//        JSONAssert.assertEquals(TestResourceReader.readFileAsString("test-data/json/mambuAPI/createBranchRequest.json"), response, true);
//
//        String createBranchRequest = exchange.getProperty("TEST_createBranchRequest", String.class);
//        assertNotNull(createBranchRequest);
//        JSONAssert.assertEquals(TestResourceReader.readFileAsString("test-data/json/mambuAPI/createBranchRequest.json"), createBranchRequest, true);
//    }


//    @Test
//    @DisplayName("Test update branch endpoint")
//    public void testUpdateBranch() throws Exception {
//        Exchange exchange = sendTestRequest("direct:testUpdateBranch", ExchangePattern.InOut, new Processor() {
//            @Override
//            public void process(Exchange exchange) throws Exception {
//                Message in = exchange.getIn();
//                in.setBody(TestResourceReader.readFileAsString("test-data/json/mambuAPI/updateBranchRequest.json"));
//            }
//        });
//
//        String response = exchange.getIn().getBody(String.class);
//        JSONAssert.assertEquals(TestResourceReader.readFileAsString("test-data/json/mambuAPI/updateBranchRequest.json"), response, true);
//
//        String updateBranchRequest = exchange.getProperty("TEST_updateBranchRequest", String.class);
//        assertNotNull(updateBranchRequest);
//        JSONAssert.assertEquals(TestResourceReader.readFileAsString("test-data/json/mambuAPI/updateBranchRequest.json"), updateBranchRequest, true);
//    }

    // @Test
    // @DisplayName("Test Database Query")
    // public void testDatabase() throws Exception {
    //     Exchange exchange = sendTestRequest("direct:testDatabase", ExchangePattern.InOut, new Processor() {
    //         @Override
    //         public void process(Exchange exchange) throws Exception {
    //             Message in = exchange.getIn();
    //         }
    //     });

    //     String response = exchange.getIn().getBody(String.class);
    //     assertEquals(10, response);
    // }

    @Override
    public String[] getMockedRouteIDs() {
        return new String[] { "findBranchesRoute", "testCreateBranchRoute", "testUpdateBranchRoute", "testDatabaseRoute" };
    }

    @Override
    public String[] getMockedURIs() {
        return new String[] { "https://modusbox.sandbox.mambu.com*" };
        
    }

    @Override
    public String getMockRouteURI() {
        return "direct:mockRouteURI";
    }

    @Override
    public String getMockRoutesXML() {
        return "mockroute.xml";
    }

}

