import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

/**
 * The main thread that will analyse and manage a HTTP request.
 */
public class WorkerThread implements Runnable {
    private final String SERVER_FILE_URL = "/play.html";
    private final String SERVER_ROOT = "/";

    private Socket clientSocket;
    private OutputStream out;
    private InputStream in;
    private Mastermind game;
    private IHttpAdapter protocolAdapter;
    private HttpRequest request;

    public WorkerThread(Socket s, Mastermind game, HttpRequest request){
        this.clientSocket = s;
        this.game = game;
        this.request = request;
        this.protocolAdapter = new HtmlAdapter();
    }

    /**
     * When executed, will set the socket connection, analyze the request and close the connection.
     */
    @Override
    public void run() {
        try
        {
            setStream();
            analyzeIncomingRequest();
            closeConnection();

        } catch (IOException ex ) {
            System.err.println("Can't get input and output stream from client :" + ex) ;
        } catch (Exception any) {
            System.err.println("worker died " + any) ;
        }
    }

    /**
     * Set stream of the client
     * @throws IOException connection error
     */
    private void setStream() throws IOException {
        out = clientSocket.getOutputStream();
        in = clientSocket.getInputStream();
        System.out.println("Server accepted client connection from address : " + clientSocket.toString());
    }

    /**
     * Close connection with the client
     * @throws IOException connection error
     */
    private void closeConnection() throws IOException {
        clientSocket.close();
    }

    @Override
    public String toString(){
        return "";
    }

    /**
     * The main method that will dispatch to the appropriated action in answer to the request.
     */
    private void analyzeIncomingRequest() {

        HashMap<String, String> extraHeaders = new HashMap<>();
        extraHeaders.put("Set-Cookie: ", HttpConstant.mastermind_cookie_name + "=" + game.gameId);

        // Mean there was a bad format http request
        if (request instanceof HttpReply) {
            sendReplyToClient((HttpReply) request);
        }
        // Redirection to the server root file
        else if (request.httpUrl.equals(SERVER_ROOT)) {

            extraHeaders.put("Location:", "http://" + request.httpHeaders.get("Host") + SERVER_FILE_URL);
            sendReplyToClient(HttpFactory.createHttpReplyFromCode(HttpConstant.code_303, extraHeaders));
        }
        // If file is not found
        else if (!request.httpUrl.equals(SERVER_FILE_URL)) {
            sendReplyToClient(HttpFactory.createHttpReplyFromCode(HttpConstant.code_404, extraHeaders));
        }
        // If everything is good
        else {
           if (request instanceof HttpGet) {
               actOnGetRequest((HttpGet) request, extraHeaders);
           }
           else if (request instanceof HttpPost) {
               actOnPostRequest((HttpPost) request, extraHeaders);
           }
        }

        //Kill the first request mechanism
        this.request = null;
    }

    /**
     * Will sent a HTTP Reply via the socket outputStream
     * @param reply the http reply to be sent
     */
    private void sendReplyToClient(HttpReply reply) {
        try {
            out.write(reply.getHttpResponse().getBytes("UTF-8"));
        }
        catch (Exception e) {
            System.err.println("Error while sending response to client : " + e.getMessage());
        }
    }

    /**
     * Responds accordingly to the client's get request by updating the game and sending back a reply
     * @param request HTTP GET request
     * @param extraHeaders extra headers for the reply
     */
    private void actOnGetRequest(HttpGet request, HashMap<String, String> extraHeaders) {

        // Mean new game
        if (request.parameters.isEmpty()) {
            String html = protocolAdapter.generateStringFromMastermind(game);
            sendReplyToClient(HttpFactory.createHttpReplyFromHtmlFile(html, extraHeaders));
        }
        
        // Mean ajax with params
        else {
            String colors = game.gameColors.get(request.parameters.get("color1")) +
                            game.gameColors.get(request.parameters.get("color2")) +
                            game.gameColors.get(request.parameters.get("color3")) +
                            game.gameColors.get(request.parameters.get("color4"));

            String goodColors = game.addNewRoundValues(colors);

            //If the game is over, send an expired cookie to terminate the session
            boolean isEndGame = checkForVictoryOrDefeat(goodColors);
            if (isEndGame) {
                extraHeaders.put("Set-Cookie: ", HttpConstant.mastermind_cookie_name + "=" + game.gameId + "; expires=Thu, 01 Jan 1970 00:00:00 GMT");
                this.game = null;
            }

            sendReplyToClient(HttpFactory.createHttpReplyFromTextFile(goodColors, extraHeaders));
        }
    }

    /**
     * Responds accordingly to the client's get request by updating the game and sending back a reply
     * @param request HTTP POST request
     * @param extraHeaders extra headers for the reply
     */
    private void actOnPostRequest(HttpPost request, HashMap<String, String> extraHeaders) {
        String colors = game.gameColors.get(request.parameters.get("color1")) +
                game.gameColors.get(request.parameters.get("color2")) +
                game.gameColors.get(request.parameters.get("color3")) +
                game.gameColors.get(request.parameters.get("color4"));

        String goodColors = game.addNewRoundValues(colors);

        //If the game is over, send an expired cookie to terminate the session
        boolean isEndGame = checkForVictoryOrDefeat(goodColors);
        if (isEndGame) {
            extraHeaders.put("Set-Cookie: ", HttpConstant.mastermind_cookie_name + "=" + game.gameId + "; expires=Thu, 01 Jan 1970 00:00:00 GMT");
            String html = protocolAdapter.generateStringFromMastermind(game);
            sendReplyToClient(HttpFactory.createHttpReplyFromHtmlFile(html, extraHeaders));
            this.game = null;
        }
        else {
            String html = protocolAdapter.generateStringFromMastermind(game);
            sendReplyToClient(HttpFactory.createHttpReplyFromHtmlFile(html, extraHeaders));
        }
    }

    /**
     * Verifies if the game is finished or not.
     * @param goodColors the secret color combination
     * @return true if the game is ended
     */
    private boolean checkForVictoryOrDefeat(String goodColors) {
        boolean isEndGame = false;
        String results[] = goodColors.split("_");
        if (results[0].equals("12")) {
            // Mean defeat
            isEndGame = true;
        } else if (results[1].equals("4")) {
            // Mean victory
            isEndGame = true;
        }
        this.game.isGameOver = isEndGame;
        return isEndGame;
    }
}
