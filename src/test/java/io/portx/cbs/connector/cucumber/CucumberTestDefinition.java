package io.portx.cbs.connector.cucumber;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

    /**
     * Extracts a value from a JSON structure using Gson.
     *
     * @param json      The JSON string containing the data.
     * @param fieldName The name of the field to extract the value of.
     * @return The extracted value as a string.
     * @throws IllegalArgumentException If the field is not found or an error occurs.
     */
    protected String extractValueFromJson(String json, String fieldName) {
        try {
            JsonElement rootElement = JsonParser.parseString(json);
            if (rootElement.isJsonObject()) {
                JsonObject jsonObject = rootElement.getAsJsonObject();
                if (jsonObject.has(fieldName)) {
                    return jsonObject.get(fieldName).getAsString();
                } else {
                    throw new IllegalArgumentException("Field '" + fieldName + "' not found in the JSON");
                }
            }
            throw new IllegalArgumentException("Invalid JSON structure");
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while extracting value from JSON: " + e.getMessage());
        }
    }

}
