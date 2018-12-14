
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Random;

/**
 * Class managing the game state of the client
 */
public class Mastermind {
    public static final String GAME_COLORS[] = {"red", "blue", "yellow", "green", "white", "black"};
    public HashMap<String, String> gameColors;
    public static final int MAXIMUM_NUMBER_OF_TRY = 12;
    public int nbOfTry = 0;
    public String roundValues[] = new String[MAXIMUM_NUMBER_OF_TRY];
    private String secretValues = "";
    public String gameId;
    public boolean isGameOver;
    public LocalTime creationTime;

    /**
     * Public constructor, generates a random alhpanumeric game ID that will be used
     * as cookie ID
     */
    public Mastermind()
    {
        gameId = generateCookieId(HttpConstant.COOKIE_ID_LENGTH);
        gameColors = new HashMap<>();
        gameColors.put("red", "0");
        gameColors.put("blue", "1");
        gameColors.put("yellow", "2");
        gameColors.put("green", "3");
        gameColors.put("white", "4");
        gameColors.put("black", "5");
        creationTime = LocalTime.now();
        isGameOver = false;
        generateSecretValues();
    }

    /**
     * Saves the new combination from the client
     * @param values list of values
     * @return the number of good placed and good colors in byte
     */
    public String addNewRoundValues(String values) {
        int nbGoodColorAndPosition = 0, nbGoodColors = 0;
        roundValues[nbOfTry] = values;
        String secretCode = secretValues;

        //Updates the timestamp of the game when updating the game state to signify it is still alive
        this.creationTime = LocalTime.now();

        // Find the good placed colors
        for (int i = 0; i < values.length(); i++) {
            if (values.charAt(i) == secretCode.charAt(i)) {
                nbGoodColorAndPosition++;
                values = values.substring(0, i) + values.substring(i+1);
                secretCode = secretCode.substring(0, i) + secretCode.substring(i+1);
                i--;
            }
        }

        // Find the good colors
        for (int i = 0; i < values.length(); i++) {
            int index  = values.indexOf(secretCode.charAt(i));
            if (index != -1) {
                nbGoodColors++;
                values = values.substring(0, index) + values.substring(index+1);
                secretCode = secretCode.substring(0, i) + secretCode.substring(i+1);
                i--;
            }
        }

        // Save the results
        roundValues[nbOfTry] += Integer.toString(nbGoodColorAndPosition) + Integer.toString(nbGoodColors);
        printColorCode(roundValues[nbOfTry]);
        nbOfTry++;

        return Integer.toString(nbOfTry)+ "_" + Integer.toString(nbGoodColorAndPosition)+ "_" + Integer.toString(nbGoodColors);
    }

    /**
     * Generate the new secret color combinaison to guess
     */
    public void generateSecretValues() {
        Random rand = new Random();

        // Get random value
        while (secretValues.length() < 4)
            secretValues += Integer.toString(rand.nextInt(GAME_COLORS.length));

        // secretValues = "0012";

        // Display the code
        String out = "[" + gameId + "] Secrect Colors : ";
        for (int i = 0; i < secretValues.length(); i++) {
            out += GAME_COLORS[Integer.parseInt(secretValues.substring(i, i+1))] + ", ";
        }
    }

    /**
     * Print the color from in to string in the terminal
     * @param message int in a string
     */
    public void printColorCode(String message) {
        String out = "[" + gameId + "] " + nbOfTry+1 + " - ";

        for (int i = 0; i < message.length()-2; i++) {
            out += GAME_COLORS[Integer.parseInt(message.substring(i, i+1))] + ", ";
        }

        out += "Good and Well Placed : " + message.charAt(message.length()-2);
        out += ", Good Color : " + message.charAt(message.length()-1);

    }

    /**
     * Method that generates a random alphanumeric string to have it as a cookie id.
     * Reference : https://www.boraji.com/how-to-generate-a-random-alphanumeric-string-in-java
     */
    private String generateCookieId(int length){
        char[] ch = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
                'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
                'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
                'w', 'x', 'y', 'z' };

        char[] c=new char[length];
        Random random=new Random();
        for (int i = 0; i < length; i++) {
            c[i]=ch[random.nextInt(ch.length)];
        }
        return new String(c);
    }
}
