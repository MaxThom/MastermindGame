
/**
 * Class containing all the global static constants used for HTTP.
 */
public class HttpConstant {

    // https://en.wikipedia.org/wiki/List_of_HTTP_status_codes

    public static final String http_version = "HTTP/1.1";
    public static final String line_break = "\r\n";
    public static final String mastermind_cookie_name = "MSESSID";
    public static final int COOKIE_ID_LENGTH = 26;

    public static final String code_200 = "200";
    public static final String status_200 = "OK";

    public static final String code_303 = "303";
    public static final String status_303 = "See Other";

    public static final String code_400 = "400";
    public static final String status_400 = "Bad Request";
    public static final String body_400 = "<h2>Bad Request</h2>Incorrect format of http request.";

    public static final String code_404 = "404";
    public static final String status_404 = "Not Found";
    public static final String body_404 = "<h2>Not Found</h2>The resquested document was not found.";

    public static final String code_405 = "405";
    public static final String status_405 = "Method Not Allowed";
    public static final String body_405 = "<h2>Method Not Allowed</h2>A request method is not supported for the requested resource.";

    public static final String code_411 = "411";
    public static final String status_411 = "Length Required";
    public static final String body_411 = "<h2>Length Required</h2>The request did not specify the length of its content, which is required by the requested resource.";

    public static final String code_501 = "501";
    public static final String status_501 = "Not Implemented";
    public static final String body_501 = "<h2>HTTP Request Method Not Implemented</h2>The request method is not supported by the server and cannot be handled.";

    public static final String code_505 = "505";
    public static final String status_505 = "HTTP Version Not Supported";
    public static final String body_505 = "<h2>HTTP Version Not Supported</h2>The server does not support the HTTP protocol version used in the request.";

    public static String getStatusFromErrorCode(String errorCode) {
        switch (errorCode) {
            case code_200:
                return status_200;
            case code_303:
                return status_303;
            case code_400:
                return status_400;
            case code_404:
                return status_404;
            case code_411:
                return status_411;
            case code_405:
                return status_405;
            case code_501:
                return status_501;
            case code_505:
                return status_505;
            default:
                return "";
        }
    }

    public static String getBodyFromErrorCode(String errorCode) {
        switch (errorCode) {
            case code_200:
                return "";
            case code_400:
                return body_400;
            case code_404:
                return body_404;
            case code_405:
                return body_405;
            case code_411:
                return body_411;
            case code_501:
                return body_501;
            case code_505:
                return body_505;
            default:
                return "";
        }
    }
}
