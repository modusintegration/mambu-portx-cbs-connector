package io.portx.cbs.connector.cucumber;

import com.google.gson.Gson;
import io.cucumber.spring.CucumberContextConfiguration;
import io.portx.cbs.connector.Application;
import io.portx.cbs.connector.cucumber.util.GsonBuilderUtil;
import io.portx.cbs.connector.cucumber.util.RestHttpRequestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "server.port=8042"
        }, classes = {Application.class})
@ActiveProfiles("cucumber")
public class CucumberTestDefinition {

    protected static final String BASE_PATH = "/mambu-portx-cbs-connector/1.0";
    protected static String LOCALHOST_URL = "http://localhost:8042" + BASE_PATH;
    protected Gson gson = GsonBuilderUtil.getGson();
    protected RestHttpRequestFactory restFactory = new RestHttpRequestFactory();

}
