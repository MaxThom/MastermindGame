
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;

/**
 * The server instance that will manage all the client requests and the games through threads.
 */
public class WebServer {

    public static final int NUMBER_THREAD = 20;
    public static final int MAX_SESSION_TIME_SECONDS = 600;

    public static HashMap<String, Mastermind> currentGames;

    /**
     * Entry point of the application. Creation of the threadPool and the game verification mechanism.
     * @param args The maximum number of threads wanted
     */
    public static void main(String[] args) {

        currentGames = new HashMap<>();

        //Use of a thread pool to limit the max number of threads to avoid DDOS attacks
        try {
            int maxThread = args.length > 0 ? Integer.parseInt(args[0]) : NUMBER_THREAD;

            ThreadPoolExecutor executor = new ThreadPoolExecutor(maxThread, maxThread,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());

            //Launch the game expiration verification every ten minutes, the first time after ten minutes
            Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(new GameManagerThread(), MAX_SESSION_TIME_SECONDS * 1000, MAX_SESSION_TIME_SECONDS * 1000);

            waitIncomingClient(executor);

        }
        catch (NumberFormatException formatEx) {
            System.err.println("Invalid program arguments : " + formatEx.getMessage());
        }
        catch (Exception e) {
            System.err.println("Unknown error in server : " + e.getMessage());
        }
    }

    /**
     * Wait for clients.
     * Creates a new thread for every new client request
     */
    private static void waitIncomingClient(ThreadPoolExecutor executor) {
        try
        {
            ServerSocket ss = new ServerSocket(8010);
            System.out.println("Server started . . . waiting on future client.");

            while (true) {
                Socket clientSocket = ss.accept();
                InputStream in = clientSocket.getInputStream();
                System.out.println("Server accepted client connection from address : " + clientSocket.toString());

                //Read the http request to get the game associated to the cookie (if there is one)
                HttpRequest request = HttpFactory.createHttpRequestFromHttpHeader(getHeaderToArray(in));
                Mastermind userGame = getUserGame(request);

                //Usage of a thread to manage the single request
                Runnable worker = new WorkerThread(clientSocket, userGame, request);
                executor.execute(worker);
            }

        } catch (IOException ex ) {
            System.err.println("Error while accepting new client : " + ex) ;
        }
    }

    /**
     * Get the game with the good id to recover the good game session
     * @param request httpRequest
     * @return the associated game ID
     */
    private synchronized static Mastermind getUserGame(HttpRequest request){
        String cookieId = "";
        //Get cookie id to find back the good game
        String cookie[] = request.httpHeaders.getOrDefault("Cookie", "").split("=");

        //Verify if there is a corresponding Mastermind game cookie
        if (cookie.length == 2 && cookie[0].equals(HttpConstant.mastermind_cookie_name)) {
            cookieId = cookie[1];
        }

        //If the cookie is not set or there are no corresponding existing game we create a new game
        if (cookieId.equals("") || !currentGames.containsKey(cookieId)) {
            Mastermind game = new Mastermind();
            currentGames.put(game.gameId, game);
            cookieId = game.gameId;
        }

        //If a game exists with that cookieId
        else if(currentGames.containsKey(cookieId)){

            long timeDiff = ChronoUnit.MINUTES.between(LocalTime.now(), currentGames.get(cookieId).creationTime);

            //We verify it has not been there for more than its allowed time to live
            if((-timeDiff) > MAX_SESSION_TIME_SECONDS){

                //Delete expired game
                currentGames.remove(cookieId);

                //Create a new game
                Mastermind game = new Mastermind();
                currentGames.put(game.gameId, game);
                cookieId = game.gameId;
            }
        }

        //The game was precedently finished (set to null) by either a win or a lost, so we create a new one
        else if (currentGames.get(cookieId) == null) {
            Mastermind game = new Mastermind();
            currentGames.put(game.gameId, game);
            cookieId = game.gameId;
        }

        return currentGames.get(cookieId);
    }

    /**
     * Fetches the raw HTTP header and converts it to a single string.
     * @param inputStream the input stream object in which it will read
     * @return the http header as a single string
     */
    private static String getHeaderToArray(InputStream inputStream) {

        String headerTempData = "";

        // chain the InputStream to a Reader
        Reader reader = new InputStreamReader(inputStream);
        try {
            int c;
            while ((c = reader.read()) != -1) {
                headerTempData += (char) c;

                if (headerTempData.contains("\r\n\r\n"))
                    break;
            }

            //In case of a POST request
            if (headerTempData.contains("POST") && headerTempData.contains("Content-Length: ")) {
                int lengthOfContent = headerTempData.indexOf("Content-Length: ") + "Content-Length: ".length();
                int endOfContent = headerTempData.indexOf("\r\n", lengthOfContent);
                int length = Integer.parseInt(headerTempData.substring(lengthOfContent, endOfContent));

                //Read every character to get the POST request body
                while (length > 0) {
                    c = reader.read();
                    headerTempData += (char) c;
                    length--;
                }
            }

        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        return headerTempData;
    }

    /**
     * This task will only loop on the games at a certain pace to verify their expiration time and delete
     * them if they are expired.
     */
    private static class GameManagerThread extends TimerTask{

        /**
         * Will verify the games when executed
         */
        @Override
        public void run() {
            verifyCurrentGames();
        }

        /**
         * Loop through the current games on the server and compare their creation time with the actual time.
         */
        private synchronized void verifyCurrentGames(){

            //Fetch the hashmap's iterator to iterate through the games
            Iterator iterator = currentGames.entrySet().iterator();

            while(iterator.hasNext()){

                Map.Entry<String, Mastermind> keyValuePair = (Map.Entry) iterator.next();
                Mastermind game = keyValuePair.getValue();

                long timeDiff = ChronoUnit.SECONDS.between(LocalTime.now(), game.creationTime);

                //Delete the game if its expired
                if((-timeDiff) > MAX_SESSION_TIME_SECONDS){

                    currentGames.remove(keyValuePair.getKey());

                }
            }
        }
    }



}
