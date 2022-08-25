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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes=Application.class)
public class ApplicationTest extends APITest {

    @Test
    public void testGetBranches() throws Exception {
        Exchange exchange = sendTestRequest("direct:findBranches", ExchangePattern.InOut, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Message in = exchange.getIn();
                System.out.println(in);
            }
        });

        String response = exchange.getIn().getBody(String.class);
        assertNotNull(response);
        JSONAssert.assertEquals(TestResourceReader.readFileAsString("test-data/json/mambuAPI/getBranchesResponse.json"), response, true);
    }
    @Override
    public String[] getMockedRouteIDs() {
        return new String[] { "findBranchesRoute"
                            };
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

