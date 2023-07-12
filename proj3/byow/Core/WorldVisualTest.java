package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

/** WorldVisualTest tests the WorldGenerator class for testing purposes (for Phase 1). It has two instance variables. */

public class WorldVisualTest {
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;


    /** Generates a world of WIDTH and HEIGHT with a pre-determined seed. The world is also converted to a String &
     * printed to the terminal. */
    public static void main(String args[]) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        long seed = Engine.parseSeed("N1888111S");

        WorldGenerator generator = new WorldGenerator(finalWorldFrame, WIDTH, HEIGHT, seed);
        finalWorldFrame = generator.getWorld();

        ter.renderFrame(finalWorldFrame);
        System.out.println(TETile.toString(finalWorldFrame));
    }
}
