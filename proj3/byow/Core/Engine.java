package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int ORIGIN = 0;
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public static final int HUDHEIGHT = 3;
    public static final int HUDSPACING = 3;
    public static final int WINDOWWIDTH = WIDTH - 1;
    public static final int WINDOWHEIGHT = HEIGHT + HUDHEIGHT;
    public static final String validMoveInputs = "wasd";

    /** File saving constants. */
    public static final File SAVES = new File("saves");


    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() throws IOException {
        ter.initialize(WINDOWWIDTH, WINDOWHEIGHT);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];

        String userChoice = showHomescreen();
        if (userChoice.equals("l")) {
            File savedWorld = Utils.join("saves", "world_save.txt");
            String saveData = Utils.readContentsAsString(savedWorld);
            long seed = parseSeed(saveData);

            WorldGenerator generator = new WorldGenerator(finalWorldFrame, WIDTH, HEIGHT, seed);
            finalWorldFrame = generator.getWorld();
            for (int i = 0; i < saveData.length(); i++) {
                String ch = Character.toString(saveData.charAt(i));

                if (validMoveInputs.contains(ch)) {
                    generator.command(ch);
                }
            }

            boolean gameOver = false;
            while (!gameOver) {
                updateHUD(generator, "fox");
                ter.renderFrame(finalWorldFrame);
                gameOver = commandAvatar(generator);
            }
            showEndScreen(seed);
            generator.saveState();

        } else {
            long seed = getSeedFromUserInput();
            String username = getUsername();

            WorldGenerator generator = new WorldGenerator(finalWorldFrame, WIDTH, HEIGHT, seed);
            finalWorldFrame = generator.getWorld();

            boolean gameOver = false;
            while (!gameOver) {
                updateHUD(generator, username);
                ter.renderFrame(finalWorldFrame);
                gameOver = commandAvatar(generator);
            }
            showEndScreen(seed);
            generator.saveState();
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     *
     * The default username is "CS61B".
     */
    public TETile[][] interactWithInputString(String input) {
        //ter.initialize(WINDOWWIDTH, WINDOWHEIGHT);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];

        try {
            // TODO: Fill out this method so that it run the engine using the input
            // passed in as an argument, and return a 2D tile representation of the
            // world that would have been drawn if the same inputs had been given
            // to interactWithKeyboard().
            //
            // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
            // that works for many different input types. NOTE: For the autograder, the StdDraw class
            // cannot be used (for its testing purposes). Hence, "ter.initialize(WIDTH, HEIGHT);" &
            // "ter.renderFrame(finalWorldFrame);" for this method had to be removed when
            // submitting to the autograder.

            if (input.contains("l")) {
                File savedWorld = Utils.join("saves", "world_save.txt");
                String saveData = Utils.readContentsAsString(savedWorld);
                long seed = parseSeed(saveData);
                String userInput = parseValidInput(input);

                WorldGenerator generator = new WorldGenerator(finalWorldFrame, WIDTH, HEIGHT, seed);
                finalWorldFrame = generator.getWorld();

                for (int i = 0; i < saveData.length(); i++) {
                    String ch = Character.toString(saveData.charAt(i));

                    if (validMoveInputs.contains(ch)) {
                        generator.command(ch);
                    }
                }

                //updateHUD(generator, "CS61B");
                for (int i = 0; i < userInput.length(); i++) {
                    String ch = Character.toString(userInput.charAt(i));
                    generator.command(ch);
                }
                System.out.println("reached here");

                if (input.contains(":q")) {
                    generator.saveState();
                }

                //ter.renderFrame(finalWorldFrame);
                return finalWorldFrame;
            } else {
                long seed = parseSeed(input);
                String userInput = parseValidInput(input);

                WorldGenerator generator = new WorldGenerator(finalWorldFrame, WIDTH, HEIGHT, seed);

                finalWorldFrame = generator.getWorld();

                //updateHUD(generator, "CS61B");
                for (int i = 0; i < userInput.length(); i++) {
                    String ch = Character.toString(userInput.charAt(i));
                    generator.command(ch);
                }

                if (input.contains(":q")) {
                    generator.saveState();
                }

                //ter.renderFrame(finalWorldFrame);

                return finalWorldFrame;
            }
        } catch (IOException e) {
            System.out.println("An IOException has occurred.");
            return finalWorldFrame;
        }
    }


    /** Gets the user's input & updates the world accordingly. */
    public static boolean commandAvatar(WorldGenerator generator) {
        if (StdDraw.hasNextKeyTyped()) {
            String userInput = Character.toString(StdDraw.nextKeyTyped());
            return generator.command(userInput);
        } else {
            return false;
        }
    }

    /** Parses the seed from the command line input. */
    public static long parseSeed(String input) {
        input = input.toLowerCase();
        String stringSeed = "";

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            if ((ch == 's') || (ch == '[')) {
                break;
            }

            boolean isDigit = Character.isDigit(ch);
            if (isDigit) {
                stringSeed += ch;
            }
        }
        long seed = Long.parseLong(stringSeed);
        return seed;
    }


    /** Parses valid input (N/W/A/S/D) from the command line input. */
    public static String parseValidInput(String input) {
        input = input.toLowerCase();
        String stringInput = "";

        int startingIndex = 1;

        for (int i = startingIndex; i < input.length(); i++) {
            char ch = input.charAt(i);

            if (Character.isAlphabetic(ch)) {
                stringInput += ch;
            }
        }
        return stringInput;
    }


    /** Gets the seed from the user's input. */
    public static long getSeedFromUserInput() {
        double centerX = WINDOWWIDTH / 2;
        double centerY = WINDOWHEIGHT / 2;

        String userSeed = "";
        while (true) {
            StdDraw.clear(new Color(0, 0, 0));
            StdDraw.text(centerX, centerY, "Seed (type 'S' to indicate the end): " + userSeed);
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char userInput = StdDraw.nextKeyTyped();

                if (stopScanning(userSeed, userInput)) {
                    break;
                } else if (Character.isDigit(userInput)) {
                    userSeed += userInput;
                }
            }
        }
        long seed = Long.parseLong(userSeed);

        return seed;
    }


    /** Returns true if the criteria to stop taking input for a seed has been met (final char is 's' & length of seed
     * typed is greater than zero. */
    public static boolean stopScanning(String userSeed, char userInput) {
        int length = userSeed.length();
        if (((userInput == 's') || (userInput == 'S')) && (length > 0)) {
            return true;
        } else {
            return false;
        }
    }


    /** Gets the seed from the user's input. WIP. */
    public static String getUsername() {
        double centerX = WINDOWWIDTH / 2;
        double centerY = WINDOWHEIGHT / 2;

        String userString = "";
        while (true) {
            StdDraw.clear(new Color(0, 0, 0));
            StdDraw.text(centerX, centerY, "Your Name (type '.' to indicate the end):  " + userString);
            StdDraw.show();

            if (StdDraw.hasNextKeyTyped()) {
                char userInput = StdDraw.nextKeyTyped();

                if (userInput == '.') {
                    break;
                }
                userString += userInput;
            }
        }

        return userString;
    }


    /** Shows the homescreen. */
    public String showHomescreen() {
        double centerX = WINDOWWIDTH / 2;
        double centerY = WINDOWHEIGHT / 2;
        ArrayDeque<String> menuKeyPress = new ArrayDeque<String>();

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(centerX, centerY + HUDSPACING, "CS61B: The Game");
        StdDraw.text(centerX, centerY, "New Game (N)");
        StdDraw.text(centerX, centerY - HUDSPACING, "Load Game (L)");
        StdDraw.text(centerX, centerY- (2 * HUDSPACING), "Quit (Q)");

        StdDraw.show();

        String keyPressed = null;
        while (keyPressed == null) {
           keyPressed = getUserInput();
        }

        return "l";
    }


    /** Shows the end screen. */
    public static void showEndScreen(long seed) {
        StdDraw.clear(new Color(0, 0, 0));
        double centerX = WINDOWWIDTH / 2;
        double centerY = WINDOWHEIGHT / 2;

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(centerX, centerY + HUDSPACING, "You have successfully saved your progress.");
        StdDraw.text(centerX, centerY, "Seed: " + seed);

        StdDraw.show();
    }


    /** Gets the user's input for menu based actions. */
    public static String getUserInput() {
        if (StdDraw.hasNextKeyTyped()) {
            String userInput = Character.toString(StdDraw.nextKeyTyped());
            userInput = userInput.toLowerCase();

            switch(userInput) {
                case "n":
                    return "n";

                case "l":
                    return "l";

                case "q":
                    return "q";

                default:
                    return null;
            }
        } else {
            return null;
        }
    }


    /** Draws the HUD for the game. */
    public static void updateHUD(WorldGenerator generator, String username) {
        int textHeight = HEIGHT + 2;
        int lineHeight = HEIGHT + 1;

        // StdDraw does not have the ability to clear a specified region of the canvas, hence, a black rectangle is
        // drawn over the previous HUD.
        StdDraw.filledRectangle(WIDTH / 2, textHeight, WIDTH / 2, HUDHEIGHT / 2);

        double mouseX = StdDraw.mouseX();
        double mouseY = StdDraw.mouseY();

        String tileDesc = generator.getTileDescription(mouseX, mouseY);

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.textLeft(1, textHeight, "Tile: " + tileDesc);
        StdDraw.text(WIDTH / 2, textHeight, username + "'s Adventure");
        StdDraw.textRight(WIDTH - 2, textHeight, "CS61B: The Game");
        StdDraw.line(ORIGIN, lineHeight, WIDTH, lineHeight);
    }
}
