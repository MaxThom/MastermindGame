
import java.util.Map;

/**
 * Concrete class of the HTTP request. Represents the HTTP replies that the server will send
 * back to the clients.
 */
public class HttpReply extends HttpRequest {
    private static final int MAX_CHUNKED_SIZE = 15;
    public String code;
    public String status;
    public String body;

    public HttpReply() {

    }

    /**
     * Generates the HTTP response to be sent
     * @return the http reply
     */
    public String getHttpResponse() {
        StringBuilder response = new StringBuilder();
        response.append(this.httpVersion).append(" ");
        response.append(this.code).append(" ");
        response.append(this.status);
        response.append(HttpConstant.line_break);

        for (Map.Entry<String, String> option : httpHeaders.entrySet()) {
            response.append(option.getKey()).append(" ").append(option.getValue());
            response.append(HttpConstant.line_break);
        }

        response.append(HttpConstant.line_break);
        response.append(getChunkedBody(this.body));

        return response.toString();
    }

    /**
     * Usage of the chunk encoding to encode the html body before sending it to the client.
     * @param body the html body (not encoded)
     * @return the encoded body
     */
    private String getChunkedBody(String body) {
        StringBuilder response = new StringBuilder();

        while (body.length() != 0) {
            String nextChunk = body.substring(0, body.length() > MAX_CHUNKED_SIZE ? MAX_CHUNKED_SIZE : body.length());
            body = body.substring(body.length() > MAX_CHUNKED_SIZE ? MAX_CHUNKED_SIZE : body.length(), body.length());
            response.append(Integer.toHexString(nextChunk.length())).append(HttpConstant.line_break);
            response.append(nextChunk).append(HttpConstant.line_break);
        }
        response.append(0).append(HttpConstant.line_break).append(HttpConstant.line_break);

        return response.toString();
    }
}
