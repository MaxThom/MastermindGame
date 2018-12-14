
import java.util.HashMap;

/**
 * Class to share the basic attributes (url, httpversion, headers)
 * for every type of concrete HTTP request class.
 */
public abstract class HttpRequest {

    protected String httpUrl;
    public String httpVersion;
    public HashMap<String, String> httpHeaders;

    public HttpRequest() {
        httpHeaders = new HashMap<>();
    }

    /**
     * Setter for the HTTP url
     * @param url the url
     */
    public void setHttpUrl(String url) {
        this.httpUrl = url;
    }

    /**
     * Getter for the HTTP url
     * @return the url
     */
    public String getHttpUrl() {
        return this.httpUrl;
    }
}
