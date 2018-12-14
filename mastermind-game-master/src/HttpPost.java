
import java.util.HashMap;

/**
 * Concrete class of the HTTP request. Represents a HTTP POST request that the server can receive.
 */
public class HttpPost extends HttpRequest {
    private String body;
    HashMap<String, String> parameters;

    /**
     * Public constructor, initiates the parameters hashmap
     */
    public HttpPost() {
        super();
        parameters = new HashMap<>();
    }

    /**
     * Getter for the body attribute
     * @return the body
     */
    public String getBody(){
        return body;
    }

    /**
     * Sets the body as what was received in the POST request.
     * @param body the HTTP POST request body
     */
    public void setBody(String body) {
        this.body = body;
            String params[] = body.split("&");
            for (String param : params) {
                String keyValue[] = param.split("=");
                parameters.put(keyValue[0], keyValue[1]);
            }

    }
}
