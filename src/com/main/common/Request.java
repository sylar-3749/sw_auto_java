package common;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Request {

    public enum HttpMethod {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE"),
        PATCH("PATCH"),
        HEAD("HEAD"),
        OPTIONS("OPTIONS");

        private final String methodName;

        HttpMethod(String methodName) {
            this.methodName = methodName;
        }

        public String getMethodName() {
            return methodName;
        }
    }

    public static Response get(String uri, String... headers) {
        return request(HttpMethod.GET, uri, null, headers);
    }

    public static Response post(String uri, String body, String... headers) {
        return request(HttpMethod.POST, uri, body, headers);
    }

    public static Response put(String uri, String body, String... headers) {
        return request(HttpMethod.PUT, uri, body, headers);
    }

    public static Response delete(String uri, String... headers) {
        return request(HttpMethod.DELETE, uri, null, headers);
    }

    public static Response patch(String uri, String body, String... headers) {
        return request(HttpMethod.PATCH, uri, body, headers);
    }

    public static Response head(String uri, String... headers) {
        return request(HttpMethod.HEAD, uri, null, headers);
    }

    public static Response options(String uri, String... headers) {
        return request(HttpMethod.OPTIONS, uri, null, headers);
    }

    private static Response request(HttpMethod method, String uri, String body, String... headers) {
        Map<String, String> headerMap = parseHeaders(headers);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .headers(toHeaderString(headerMap))
                .method(method.getMethodName(), body == null 
                    ? BodyPublishers.noBody() 
                    : BodyPublishers.ofString(body))
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            
            Response result = new Response(
                String.valueOf(response.statusCode()),
                new String(response.body(), StandardCharsets.UTF_8)
            );
            
            System.out.println("Method: " + method.getMethodName());
            System.out.println("URL: " + uri);
            System.out.println("Status: " + result.code);
            System.out.println("Body: " + result.body);
            System.out.println();
            return result;
        } catch (IOException | InterruptedException e) {
            System.err.println("HTTP Request failed: " + e.getMessage());
            return null;
        }
    }

    private static Map<String, String> parseHeaders(String... headers) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < headers.length; i += 2) {
            if (i + 1 < headers.length) {
                map.put(headers[i], headers[i + 1]);
            }
        }
        return map;
    }

    private static String toHeaderString(Map<String, String> headers) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (i++ > 0) sb.append(", ");
            sb.append(entry.getKey()).append(": ").append(entry.getValue());
        }
        return sb.toString();
    }
}
