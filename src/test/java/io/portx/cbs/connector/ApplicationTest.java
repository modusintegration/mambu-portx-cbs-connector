package io.portx.cbs.connector;

import io.portx.camel.test.APITest;
import io.portx.camel.test.util.TestResourceReader;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes=Application.class)
public class ApplicationTest extends APITest {
    private Logger log = LoggerFactory.getLogger(ApplicationTest.class);

    @Test
    public void testMySampleRoute() throws Exception {
        Exchange exchange = sendTestRequest("direct:mySampleRoute", ExchangePattern.InOut, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Message in = exchange.getIn();
                in.setHeader("id", "123");
            }
        });

        String response = exchange.getIn().getBody(String.class);
        assertNotNull(response);
    }

    @Override
    public String[] getMockedRouteIDs() {
        return new String[] {
                                "mySampleRoute"
                            };
    }

    @Override
    public String[] getMockedURIs() {
        return new String[] { "https://foo.bar*" };
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

