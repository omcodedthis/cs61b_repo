package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

public class WorldVisualTest {
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;


    public static void main(String args[]) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        long seed = Engine.parseSeed("N1111111S");


        WorldGenerator generator = new WorldGenerator(finalWorldFrame, WIDTH, HEIGHT, seed);
        finalWorldFrame = generator.getWorld();

        ter.renderFrame(finalWorldFrame);
        System.out.println(TETile.toString(finalWorldFrame));
    }
}
