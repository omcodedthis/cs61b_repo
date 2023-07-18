package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.*;


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


    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        ter.initialize(WINDOWWIDTH, WINDOWHEIGHT);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];

        showHomescreen();
        long seed = getSeedFromUserInput();
        String username = getUsername();

        WorldGenerator generator = new WorldGenerator(finalWorldFrame, WIDTH, HEIGHT, seed);
        finalWorldFrame = generator.getWorld();

        while (true) {
            updateHUD(generator, username);
            ter.renderFrame(finalWorldFrame);
            moveAvatar(generator);
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
     */
    public TETile[][] interactWithInputString(String input) {
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

        ter.initialize(WINDOWWIDTH, WINDOWHEIGHT);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];

        long seed = parseSeed(input);

        WorldGenerator generator = new WorldGenerator(finalWorldFrame, WIDTH, HEIGHT, seed);
        finalWorldFrame = generator.getWorld();

        updateHUD(generator, "Fox");
        ter.renderFrame(finalWorldFrame);

        return finalWorldFrame;
    }


    /** Gets the user's input & updates the world accordingly. */
    public static void moveAvatar(WorldGenerator generator) {
        if (StdDraw.hasNextKeyTyped()) {
            String userInput = Character.toString(StdDraw.nextKeyTyped());

            generator.move(userInput);
        }
    }

    /** Parses the seed from the command line input. */
    public static long parseSeed(String input) {
        input = input.toLowerCase();
        String stringSeed = "";

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);

            if (ch == 's') {
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

                if ((userInput == 's') || (userInput == 'S')) {
                    break;
                } else if (Character.isDigit(userInput)) {
                    userSeed += userInput;
                }
            }
        }
        long seed = Long.parseLong(userSeed);

        return seed;
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
    public static void showHomescreen() {
        double centerX = WINDOWWIDTH / 2;
        double centerY = WINDOWHEIGHT / 2;

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(centerX, centerY + HUDSPACING, "CS61B: The Game");
        StdDraw.text(centerX, centerY, "New Game (N)");
        StdDraw.text(centerX, centerY - HUDSPACING, "Load Game (L)");
        StdDraw.text(centerX, centerY- (2 * HUDSPACING), "Quit (Q)");

        StdDraw.show();

        boolean endMenu = false;
        while (!endMenu) {
           endMenu = getUserInput();
        }
    }


    /** Gets the user's input for menu based actions. */
    public static boolean getUserInput() {
        if (StdDraw.hasNextKeyTyped()) {
            String userInput = Character.toString(StdDraw.nextKeyTyped());

            userInput = userInput.toLowerCase();

            switch(userInput) {
                case "n":
                    System.out.println("N typed.");
                    return true;

                case "l":
                    return true;

                case "q":
                    return true;

                default:
                    return false;
            }
        } else {
            return false;
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
