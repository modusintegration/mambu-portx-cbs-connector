package io.portx.cbs.connector.cucumber.util;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.model.Request;
import com.atlassian.oai.validator.model.Response;
import com.atlassian.oai.validator.model.SimpleResponse;
import com.atlassian.oai.validator.report.LevelResolver;
import com.atlassian.oai.validator.report.ValidationReport;
import io.portx.camel.exception.ValidationException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;

public abstract class OpenApiResponseValidator {

    private static final String DEFAULT_SWAGGER_YAML = "swagger.yaml";

    public static void validateResponseAgainstSwaggerConstraints(String endpoint,
                                                                 Request.Method httpMethod,
                                                                 HttpResponse<String> httpResponse){
        InputStream mappingStream = OpenApiResponseValidator.class
                .getClassLoader().getResourceAsStream(DEFAULT_SWAGGER_YAML);
        String yamlSpec;
        try {
            yamlSpec = IOUtils.toString(mappingStream, "UTF-8");
            mappingStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        OpenApiInteractionValidator validator = OpenApiInteractionValidator.createForInlineApiSpecification(yamlSpec)
                .withLevelResolver(LevelResolver.create()
                        .withLevel("validation.schema.required", ValidationReport.Level.ERROR)
                        .build()).build();
        Response response = buildAtlassianResponse(httpResponse);
        ValidationReport validationReport = validator.validateResponse(endpoint, httpMethod, response);
        if (validationReport.hasErrors()) {
            throw new ValidationException(validationReport);
        }
    }

    private static Response buildAtlassianResponse(HttpResponse<String> httpResponse) {
        SimpleResponse.Builder builder = SimpleResponse.Builder
                .status(httpResponse.statusCode())
                .withBody(httpResponse.body());
        httpResponse.headers().map().forEach(builder::withHeader);
        return builder.build();
    }
}
