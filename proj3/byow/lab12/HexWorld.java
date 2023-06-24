package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    /** nested class that keeps track of the position of the Hexagon. */
    private static class Postion {
        public int xPos;

        public int yPos;

        private Postion(int x, int y) {
            xPos = x;
            yPos = y;
        }
    }


    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    /**
     * Fills the given 2D array of tiles with NOTHING (blank) tiles.
     */
    public static void fillWithNothingTiles(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }


    /** Starts the recursive call to drawHexActual(). */
    public static void drawHex(int s, TETile[][] tiles, Postion hexPos) {
        drawHexActual(s - 1, s, tiles, hexPos);
    }


    /** Recursively draws the hexagon. */
    public static void drawHexActual(int blanks, int notBlanks, TETile[][] tiles, Postion hexPos) {
        drawRow(blanks, notBlanks, tiles, hexPos);

        if (blanks > 0) {
            drawHexActual(blanks - 1, notBlanks + 2, tiles, hexPos);
        }

        drawRow(blanks, notBlanks, tiles, hexPos);
    }


    /** Draws a single row, used in the recursive calls of drawHexActual(). */
    public static void drawRow(int blanks, int notBlanks, TETile[][] tiles, Postion hexPos) {
        int initalX = hexPos.xPos;
        hexPos.xPos += blanks;

        for (int dx = 0; dx < notBlanks; dx++) {
            tiles[hexPos.xPos + dx][hexPos.yPos] = Tileset.FLOWER;
        }

        hexPos.xPos = initalX;
        hexPos.yPos--;
    }


    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] randomTiles = new TETile[WIDTH][HEIGHT];
        fillWithNothingTiles(randomTiles);

        Postion hexPos = new Postion(15, 25);
        drawHex(8, randomTiles, hexPos);

        ter.renderFrame(randomTiles);
    }
}
