package common;
public class Response {
    public String code;
    public String body;

    public Response (String code, String body) {
        this.code = code;
        this.body = body;
    }

    public void setResponseCode(String code) {
        this.code = code;
    }
    public void setResponseBody(String body) {
        this.body = body;
    }
}
