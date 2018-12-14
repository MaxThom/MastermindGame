import java.util.HashMap;

/**
 * The HTTP reply and request factory. Responsible of creating correctly every type of HTTP
 * reply the server can send back to the client and to create the appropriate HTTP request so
 * the worker thread can know how to analyse and treat it.
 */
public class HttpFactory {

    /**
     * Creates the appropriate HTTP request type from the request header. Is used mainly by the worker thread but
     * is also used before by the server to find existing associated game with the cookie ID. Can also directly create
     * HTTP error replies if there is problem with the received request.
     * @param httpHeader the HTTP request header
     * @return the right http request (nly GET or POST in our case)
     */
    public static HttpRequest createHttpRequestFromHttpHeader(String httpHeader) {
        HttpRequest request;

        //Dispatching the right request by interpreting its header
        try {
            String values[] = httpHeader.split(HttpConstant.line_break);
            String firstRow[] = values[0].split(" ");
            switch (firstRow[0]) {
                case "GET":
                    request = new HttpGet();
                    break;
                case "POST":
                    boolean is411 = true;
                    for (String value : values) {
                        if (value.contains("Content-Length:")) {
                            is411 = false;
                        }
                    }
                    if (is411) {
                        request = createHttpReplyFromCode(HttpConstant.code_411, null);
                        return request;
                    } else {
                        request = new HttpPost();
                        ((HttpPost) request).setBody(values[values.length-1]);
                    }
                    break;
                case "PUT":
                    request = createHttpReplyFromCode(HttpConstant.code_405, null);
                    return request;
                case "DELETE":
                    request = createHttpReplyFromCode(HttpConstant.code_405, null);
                    return request;
                case "HEAD":
                    request = createHttpReplyFromCode(HttpConstant.code_405, null);
                    return request;
                case "TRACE":
                    request = createHttpReplyFromCode(HttpConstant.code_405, null);
                    return request;
                case "OPTIONS":
                    request = createHttpReplyFromCode(HttpConstant.code_405, null);
                    return request;
                case "CONNECT":
                    request = createHttpReplyFromCode(HttpConstant.code_405, null);
                    return request;
                case "PATCH":
                    request = createHttpReplyFromCode(HttpConstant.code_405, null);
                    return request;
                default:
                    request = createHttpReplyFromCode(HttpConstant.code_400, null);
                    return request;
            }

            request.setHttpUrl(firstRow[1]);

            // Check for bad http version
            request.httpVersion = firstRow[2];
            if (!request.httpVersion.equals(HttpConstant.http_version)) {
                request = createHttpReplyFromCode(HttpConstant.code_505, null);
                return request;
            }

            // Set headers
            for (int i = 1; i < values.length; i++) {
                if (!values[i].equals("") && values[i].split(": ").length == 2) {
                    String keyValue[] = values[i].split(": ");
                    request.httpHeaders.put(keyValue[0], keyValue[1]);
                }
            }

        } catch (Exception e) {
            request = createHttpReplyFromCode(HttpConstant.code_400, null);
        }

        return request;
    }

    /**
     * Creates the reply containing the html. Used to answer client GET requests for new games or POST requests
     * @param html the html page to be sent to the client
     * @param extraHeaders possible extra headers to the request
     * @return the HTTP reply
     */
    public static HttpReply createHttpReplyFromHtmlFile(String html, HashMap<String, String> extraHeaders) {
        HttpReply reply = new HttpReply();

        reply.httpVersion = HttpConstant.http_version;

        //request success
        reply.code = HttpConstant.code_200;
        reply.status = HttpConstant.getStatusFromErrorCode(HttpConstant.code_200);

        reply.httpHeaders.put("Content-Type:", "text/html");
        reply.httpHeaders.put("Transfer-Encoding:", "chunked");
        reply.httpHeaders.put("Connection:", "keep-alive");
        if (extraHeaders != null) reply.httpHeaders.putAll(extraHeaders);

        reply.body = html;

        return reply;
    }

    /**
     * Creates the HTTP reply from a string (code representing the number of good and bad colors). Used
     * to answer client GET requests when they submit a combination.
     * @param text the good/bad color code generated in Mastermind addNewRoundValues
     * @param extraHeaders possible extra headers of the request
     * @return the HTTP reply
     */
    public static HttpReply createHttpReplyFromTextFile(String text, HashMap<String, String> extraHeaders) {
        HttpReply reply = new HttpReply();

        reply.httpVersion = HttpConstant.http_version;

        //request success
        reply.code = HttpConstant.code_200;
        reply.status = HttpConstant.getStatusFromErrorCode(HttpConstant.code_200);

        reply.httpHeaders.put("Content-Type:", "text/plain");
        reply.httpHeaders.put("Transfer-Encoding:", "chunked");
        reply.httpHeaders.put("Connection:", "keep-alive");
        if (extraHeaders != null) reply.httpHeaders.putAll(extraHeaders);

        reply.body = text;

        return reply;
    }

    /**
     * Creates the error http replies corresponding to the error code.
     * @param code error code
     * @param extraHeaders possible reply extra headers
     * @return the HTTP reply
     */
    public static HttpReply createHttpReplyFromCode(String code, HashMap<String, String> extraHeaders) {
        HttpReply reply = new HttpReply();

        reply.httpVersion = HttpConstant.http_version;
        reply.code = code;
        reply.status = HttpConstant.getStatusFromErrorCode(code);
        reply.httpHeaders.put("Content-Type:", "text/html");
        reply.httpHeaders.put("Transfer-Encoding:", "chunked");
        reply.httpHeaders.put("Connection:", "keep-alive");
        if (extraHeaders != null) reply.httpHeaders.putAll(extraHeaders);

        //Get the html body corresponding to the error code
        reply.body = HttpConstant.getBodyFromErrorCode(code);
        return reply;
    }
}
