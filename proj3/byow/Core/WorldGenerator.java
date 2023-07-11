package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.util.Random;

public class WorldGenerator {
    /** The frame of the world that contains all the tiles. */
    public static final int ORIGIN = 0;  // bottom left
    public int WIDTH;
    public int HEIGHT;
    public int MIDPOINTx;
    public int MIDPOINTy;
    public TETile[][] finalWorldFrame;
    private Random rand;


    /** Constructor for this class, which sets multiple global constants & fills finalWorldFrame with NOTHING tiles. */
    public WorldGenerator(TETile[][] frame, int width, int height, long seed) {
        finalWorldFrame = frame;
        WIDTH = width;
        MIDPOINTx = WIDTH / 2;
        HEIGHT = height;
        MIDPOINTy = HEIGHT / 2;
        rand = new Random(seed);

        fillWithNothingTiles();
        drawWorld();
    }


    /** Fills the world with NOTHING Tiles. */
    private void fillWithNothingTiles() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                finalWorldFrame[i][j] = Tileset.NOTHING;
            }
        }
    }


    /** Returns finalWorldFrame. */
    public TETile[][] getWorld() {
        return finalWorldFrame;
    }


    /** Draws a world. WORK IN PROGRESS. */
    public void drawWorld() {
        int value = rand.nextInt(10);
        System.out.println(value);

        for (int i = 8; i < (8 + value); i++) {
            for (int j = 8; j < (8 + value); j++) {
                finalWorldFrame[i][j] = Tileset.WALL;
            }
        }
    }
}