package common;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodySubscribers;
import java.util.Map;

/**
 * Enhanced HTTP request client with environment-based host resolution.
 *
 * <p>Mirrors the Python {@code requestUtil.Request} class.  Hosts are configured
 * via environment key rather than passing full URLs every time.
 *
 * <p>Supports all standard HTTP methods: GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS.
 *
 * <p>Usage:
 * <pre>{@code
 * RequestUtil req = new RequestUtil("dev", "my-token");
 * String body = req.get("api/v1/users");
 * String resp = req.post("api/v1/users", "{\"name\":\"Alice\"}", "Content-Type", "application/json");
 * }</pre>
 */
public class RequestUtil {

    private static final Map<String, String> HOST_TEMPLATES = Map.of(
        "dev",  "https://xxxx-dev.test.net/",
        "qa",   "https://xxxx-qa.test.net/",
        "uat",  "https://xxxx-uat.test.net/",
        "prod", "https://xxxx.test.net/"
    );

    private final String host;
    private final String token;

    // ------------------------------------------------------------------
    // Construction
    // ------------------------------------------------------------------

    /**
     * @param host  Environment key (dev, qa, uat, prod) or a full URL.
     * @param token Bearer/API token.
     */
    public RequestUtil(String host, String token) {
        this.host = HOST_TEMPLATES.getOrDefault(
            host,
            host.contains("://") ? host : "https://" + host
        );
        this.token = token;
    }

    public RequestUtil(String host) {
        this(host, "xxxxxx");
    }

    // ------------------------------------------------------------------
    // Convenience methods
    // ------------------------------------------------------------------

    /** Send a GET request. */
    public String get(String path, String... headers) {
        return send("GET", path, null, headers);
    }

    /** Send a POST request. */
    public String post(String path, String body, String... headers) {
        return send("POST", path, body, headers);
    }

    /** Send a PUT request. */
    public String put(String path, String body, String... headers) {
        return send("PUT", path, body, headers);
    }

    /** Send a DELETE request. */
    public String delete(String path, String... headers) {
        return send("DELETE", path, null, headers);
    }

    /** Send a PATCH request. */
    public String patch(String path, String body, String... headers) {
        return send("PATCH", path, body, headers);
    }

    /** Send a HEAD request (returns response info, no body). */
    public String head(String path, String... headers) {
        return send("HEAD", path, null, headers);
    }

    /** Send an OPTIONS request. */
    public String options(String path, String... headers) {
        return send("OPTIONS", path, null, headers);
    }

    // ------------------------------------------------------------------
    // Core dispatcher
    // ------------------------------------------------------------------

    /**
     * Send an arbitrary HTTP request.
     *
     * @param method  HTTP verb (GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS).
     * @param path    Path appended to the configured host.
     * @param body    Request body (may be null for body-less methods).
     * @param headers Optional key-value header pairs (even-index = name, odd-index = value).
     * @return Response body as a string, or status line for HEAD.
     */
    public String send(String method, String path, String body, String... headers) {
        String url = host + (path != null ? path : "");
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(URI.create(url));

        // Attach headers
        boolean hasContentType = false;
        if (headers != null) {
            for (int i = 0; i < headers.length - 1; i += 2) {
                String name = headers[i];
                String value = headers[i + 1];
                builder.header(name, value);
                if ("Content-Type".equalsIgnoreCase(name)) {
                    hasContentType = true;
                }
            }
        }

        // Default JSON content type for methods with a body
        if (body != null && !body.isEmpty() && !hasContentType) {
            builder.header("Content-Type", "application/json");
        }

        // Set method + body
        if (body != null && !body.isEmpty()) {
            builder.method(method, BodyPublishers.ofString(body));
        } else {
            builder.method(method, BodyPublishers.noBody());
        }

        // Execute
        logRequest(method, url);
        try {
            HttpResponse<byte[]> response = client.send(builder.build(),
                responseInfo -> BodySubscribers.ofByteArray());

            String responseBody = new String(response.body());
            logResponse(method, url, response.statusCode(), responseBody.length());
            return responseBody;
        } catch (IOException | InterruptedException e) {
            System.err.println("HTTP " + method + " " + url + " failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        }
    }

    // ------------------------------------------------------------------
    // Logging
    // ------------------------------------------------------------------

    private void logRequest(String method, String url) {
        System.out.println("Sending HTTP Request:");
        System.out.println("  Method: " + method);
        System.out.println("  URL: " + url);
    }

    private void logResponse(String method, String url, int status, int bodyLength) {
        System.out.println("Response: " + method + " " + url
            + " → " + status + " (" + bodyLength + " bytes)");
    }

    // ------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------

    public String getHost() {
        return host;
    }

    public String getToken() {
        return token;
    }
}
