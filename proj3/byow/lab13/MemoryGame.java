package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};
    private static final int CENTERx = 20;
    /** x-coordinate for the center of the frame. */
    private static final int CENTERy = 20;
    /** y-coordinate for the center of the frame. */

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        String randomString = "";

        for (int i = 0; i < n; i++) {
            int randomInt = rand.nextInt(26);

            char currentChar = CHARACTERS[randomInt];
            randomString += currentChar;
        }

        System.out.println(randomString);
        return randomString;
    }

    public void drawFrame(String s) {
        StdDraw.clear(Color.BLACK);

        if (!gameOver) {
            drawHelpfulUI(null);
        }

        StdDraw.text(CENTERx, CENTERy, s);

        StdDraw.show();
        StdDraw.pause(500);
    }

    public int drawHelpfulUI(Integer phraseIndex) {
        // change font for information bar
        Font font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(font);

        StdDraw.textLeft(1, (height - 2), "Round: " + round);

        if (playerTurn) {
            StdDraw.text(CENTERx, (height - 2), "Type!");
        } else {
            StdDraw.text(CENTERx, (height - 2), "Watch!");
        }

        int randomInt;
        if (phraseIndex == null) {
            randomInt = rand.nextInt(7);
        } else {
            randomInt = phraseIndex;
        }

        StdDraw.textRight((width - 1), (height - 2), ENCOURAGEMENT[randomInt]);

        StdDraw.line(0, (height - 3), width, (height - 3));

        // sets font back to the original
        font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);

        return randomInt;
    }

    public void flashSequence(String letters) {
        int length = letters.length();
        playerTurn = false;
        boolean isFinalChar = false;

        for (int i = 0; i < length; i++) {
            StdDraw.clear(Color.BLACK);

            String charShown = Character.toString(letters.charAt(i));

            isFinalChar = checkFinalChar(i, length);

            drawCharacter(charShown, isFinalChar);
        }
    }

    public static boolean checkFinalChar(Integer n, Integer length) {
        int finalIndex = length - 1;
        return n.equals(finalIndex);
    }

    public void drawCharacter(String charShown, boolean isFinalChar) {
        int phraseIndex = drawHelpfulUI(null);

        // shows character for one second
        StdDraw.text(CENTERx, CENTERy, charShown);

        StdDraw.show();
        StdDraw.pause(1000);

        // clears the frame
        StdDraw.clear(Color.BLACK);

        playerTurn = isFinalChar;

        drawHelpfulUI(phraseIndex);

        // shows a blank screen for half a second
        StdDraw.text(CENTERx, CENTERy, "");

        StdDraw.show();
        StdDraw.pause(500);
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        String userString = "";

        while ((n > 0)) {
            if (StdDraw.hasNextKeyTyped()) {
                char userChar = StdDraw.nextKeyTyped();
                userString += userChar;

                drawFrame(userString);

                n--;
            }
        }
        return userString;
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        round = 1;
        gameOver = false;
        playerTurn = false;

        //TODO: Establish Engine loop
        while (!gameOver) {
            drawFrame("Round: " + round);

            String randomString = generateRandomString(round);

            flashSequence(randomString);

            String userString = solicitNCharsInput(round);

            if (userString.equals(randomString)) {
                round++;
            } else {
                gameOver = true;

                drawFrame("Game Over! You made it to round: " + round);
            }
        }
    }
}
