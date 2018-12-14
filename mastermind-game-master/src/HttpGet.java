
import java.util.HashMap;

/**
 * Concrete class of the HTTP request. Represents a HTTP GET request that the server can receive.
 */
public class HttpGet extends HttpRequest {
    HashMap<String, String> parameters;

    /**
     * Public constructor, initiates the parameters hashmap
     */
    public HttpGet() {
        super();
        parameters = new HashMap<>();
    }

    /**
     * Sets the url as what was received in the GET request and fetches the parameters in it.
     * @param url the HTTP POST request body
     */
    @Override
    public void setHttpUrl(String url) {
        this.httpUrl = url;
        if (url.contains("?")) {
            this.httpUrl = url.substring(0, url.indexOf("?"));
            String params[] = url.substring(url.indexOf("?")+1, url.length()).split("&");
            for (String param : params) {
                String keyValue[] = param.split("=");
                parameters.put(keyValue[0], keyValue[1]);
            }
        }
    }

}
