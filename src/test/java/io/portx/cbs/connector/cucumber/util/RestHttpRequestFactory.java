package io.portx.cbs.connector.cucumber.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.fail;


/**
 * Utility class for creating and sending REST HTTP requests.
 */
public class RestHttpRequestFactory {

    private Gson gson = GsonBuilderUtil.getGson();
    public static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Sends an HTTP GET request to the specified URL and returns the deserialized response.
     *
     * @param httpUrl      the URL to send the GET request to
     * @param responseType the class of the response type to deserialize the response to
     * @param <T>          the type of the response
     * @return the deserialized response object
     * @throws RestClientException if an unexpected response or error occurs
     */
    public <T> T get(String httpUrl, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(httpUrl))
                .setHeader("User-Agent", "Java 11 HttpClient")
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> httpResponse = null;
        try {
            httpResponse = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            resolveHttpResponseStatus(httpResponse);
        } catch (Throwable e) {
            failTest(e, request.uri().getPath());
        }
        return parseResponseBody(responseType, request, httpResponse);
    }

    /**
     * Sends an HTTP GET request to the specified URL and returns the deserialized response.
     *
     * @param httpUrl      the URL to send the GET request to
     * @return the deserialized response object
     * @throws RestClientException if an unexpected response or error occurs
     */
    public HttpResponse<String> get(String httpUrl) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(httpUrl))
                .setHeader("User-Agent", "Java 11 HttpClient")
                .header("Content-Type", "application/json")
                .GET()
                .build();
        HttpResponse<String> httpResponse = null;
        try {
            httpResponse = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            resolveHttpResponseStatus(httpResponse);
        } catch (Throwable e) {
            failTest(e, request.uri().getPath());
        }
        return httpResponse;
    }


    /**
     * Sends an HTTP GET request with a custom request object and returns the deserialized response.
     *
     * @param request      the custom HttpRequest object representing the GET request
     * @param responseType the class of the response type to deserialize the response to
     * @param <T>          the type of the response
     * @return the deserialized response object
     * @throws RestClientException if an unexpected response or error occurs
     */
    public <T> T get(HttpRequest request, Class<T> responseType) {
        HttpResponse<String> httpResponse = null;
        try {
            httpResponse = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            resolveHttpResponseStatus(httpResponse);
        } catch (Throwable e) {
            failTest(e, request.uri().getPath());
        }
        return parseResponseBody(responseType, request, httpResponse);
    }

    /**
     * Sends an HTTP POST request to the specified URL with the given JSON body and returns the deserialized response.
     *
     * @param httpUrl       the URL to send the POST request to
     * @param jsonBody      the JSON body of the request
     * @param responseType the class of the response type to deserialize the response to
     * @param <T>           the type of the response
     * @return the deserialized response object
     * @throws RestClientException if an unexpected response or error occurs
     */
    public <T> T post(String httpUrl, String jsonBody, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(httpUrl))
                .setHeader("User-Agent", "Java 11 HttpClient")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> httpResponse = null;
        try {
            httpResponse = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            resolveHttpResponseStatus(httpResponse);
        } catch (Throwable e) {
            failTest(e, httpUrl);
        }
        return parseResponseBody(responseType, request, httpResponse);
    }

    /**
     * Sends an HTTP POST request to the specified URL with the given JSON body and returns the deserialized response.
     *
     * @param httpUrl       the URL to send the POST request to
     * @param jsonBody      the JSON body of the request
     * @return the deserialized response object
     * @throws RestClientException if an unexpected response or error occurs
     */
    public HttpResponse post(String httpUrl, String jsonBody, Boolean resolveHttpStatus) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(httpUrl))
                .setHeader("User-Agent", "Java 11 HttpClient")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> httpResponse = null;
        try {
            httpResponse = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (resolveHttpStatus) {
                resolveHttpResponseStatus(httpResponse);
            }
        } catch (Throwable e) {
            failTest(e, httpUrl);
        }
        return httpResponse;
    }

    /**
     * Resolves the HTTP response and handles any errors or unexpected responses.
     *
     * @param httpResponse the HTTP response to resolve
     * @throws HttpClientErrorException if the response status code indicates a client error
     * @throws HttpServerErrorException if the response status code indicates a server error
     * @throws RestClientException     if the response status code is unexpected
     */
    private void resolveHttpResponseStatus(HttpResponse<String> httpResponse) {
        int statusCode = httpResponse.statusCode();
        if (statusCode < 200 || statusCode >= 300) {
            if (statusCode >= 400 && statusCode < 500) {
                throw new HttpClientErrorException(HttpStatus.resolve(statusCode), "Client error: "
                        + httpResponse.body() + " URI: " + httpResponse.request().uri());
            } else if (statusCode >= 500 && statusCode < 600) {
                throw new HttpServerErrorException(HttpStatus.resolve(statusCode), "Server error: "
                        + httpResponse.body() + " URI: " + httpResponse.request().uri());
            } else {
                throw new RestClientException("Unexpected response: " + httpResponse.body()
                        + " URI: " + httpResponse.request().uri());
            }
        }
    }

    /**
     * Parses the response body of an HTTP request and deserializes it to the specified response type.
     *
     * @param responseType the class of the response type to deserialize the response to
     * @param request      the original HTTP request
     * @param httpResponse the HTTP response containing the response body to parse
     * @param <T>          the type of the response
     * @return the deserialized response object
     * @throws RestClientException if an error occurs during parsing or deserialization
     */
    private <T> T parseResponseBody(Class<T> responseType, HttpRequest request, HttpResponse<String> httpResponse) {
        T deserializedResponse = null;
        try {
            deserializedResponse = gson.fromJson(httpResponse.body(), responseType);
        } catch (JsonSyntaxException e) {
            failTest(e, request.uri().getPath());
        } catch (Throwable e) {
            failTest(e, request.uri().getPath());
        }
        return deserializedResponse;
    }

    /**
     * Handles the failure of a test by printing the stack trace of the exception and failing the test with a specific message.
     *
     * @param e       the Throwable object representing the exception that occurred
     * @param request the URL of the request that caused the failure
     */
    private static void failTest(Throwable e, String request) {
        e.printStackTrace();
        fail("Step failed: I tried to make a request to the URL: " + request + " but I received an error :: " + e.getMessage());
    }
}
