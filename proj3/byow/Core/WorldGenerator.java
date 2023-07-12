package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.util.Random;
import static byow.Core.RandomUtils.*;

/** WorldGenerator generates a random world consisting of rooms & hallways according to the spec. It has six global
 * constants & two global variables. The functionality of each method is explained in greater depth below. Note that
 * asset refers to both rooms & hallways. */

public class WorldGenerator {
    /** Global constants & variables. */
    public static final int ORIGIN = 0;  // bottom left
    public int WIDTH;
    public int HEIGHT;
    public int MIDPOINTx;
    public int MIDPOINTy;
    public TETile[][] worldFrame;
    private Random rand;

    /** World Assets constants. */
    public static final int ROOMMIN = 3;
    public static final int ROOMMAX = 10;
    public static final int HALLWAYWIDTHBOUND = 3;


    /** Constructor for this class, which sets multiple global constants & fills worldFrame with NOTHING tiles. */
    public WorldGenerator(TETile[][] frame, int width, int height, long seed) {
        worldFrame = frame;
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
        for (int i = ORIGIN; i < WIDTH; i++) {
            for (int j = ORIGIN; j < HEIGHT; j++) {
                worldFrame[i][j] = Tileset.NOTHING;
            }
        }
    }


    /** Returns worldFrame. */
    public TETile[][] getWorld() {
        return worldFrame;
    }


    /** Draws a world. WORK IN PROGRESS. */
    public void drawWorld() {
        int value = rand.nextInt(10- 1) + 1;
        System.out.println(value);

        for (int i = 0; i < 1; i++) {
            drawRoom(MIDPOINTx - 20, MIDPOINTy + 10);
        }

        for (int i = 0; i < 1; i++) {
            drawRoom(MIDPOINTx, MIDPOINTy);
        }

        for (int i = 0; i < 1; i++) {
            drawRoom(MIDPOINTx + 20, MIDPOINTy - 10);
        }
        drawHallway();
    }


    /** Draws a room (top-down) with a pseudorandom width & length. */
    public void drawRoom(int x, int y) {
        int width = uniform(rand, ROOMMIN, ROOMMAX);
        int length = uniform(rand, ROOMMIN, ROOMMAX);
        Position roomLoc = new Position(x, y, width, length);

        for (int dy = 0; dy < length; dy++) {
            if ((dy == 0) || (dy == (length - 1))) {
                for (int dx = 0; dx < width; dx++) {
                    worldFrame[roomLoc.getxPos() + dx][roomLoc.getyPos()] = Tileset.WALL;
                }
                roomLoc.changeyPos(-1);
                continue;
            }

            worldFrame[roomLoc.getxPos()][roomLoc.getyPos()] = Tileset.WALL;
            for (int dx = 1; dx < width - 1; dx++) {
                worldFrame[roomLoc.getxPos() + dx][roomLoc.getyPos()] = Tileset.FLOOR;
            }
            worldFrame[roomLoc.getxPos() + width - 1][roomLoc.getyPos()] = Tileset.WALL;

            roomLoc.changeyPos(-1);
            continue;
        }
    }


    /** Draws a hallway with a pseudorandom width (1 or 2, according to the spec) & length. */
    public void drawHallway() {
        drawHorizontalHallway();
        drawVerticalHallway();
    }


    /** Draws a horizontal hallway with a pseudorandom width (1 or 2, according to the spec) & length. */
    public void drawHorizontalHallway() {
        int width = uniform(rand, 1, HALLWAYWIDTHBOUND) + 1; // +1 to for the wall
        int length = uniform(rand, 1, 10);
        Position hallLoc = new Position(MIDPOINTx + 10, MIDPOINTy + 10, width, length);

        for (int dx = 0; dx < length; dx++) {
            worldFrame[hallLoc.getxPos()][hallLoc.getyPos()] = Tileset.WALL;

            for (int dy = width; dy >= 1; dy--) {
                worldFrame[hallLoc.getxPos()][hallLoc.getyPos() - dy] = Tileset.FLOOR;
            }

            worldFrame[hallLoc.getxPos()][hallLoc.getyPos() - width] = Tileset.WALL;

            hallLoc.changexPos(-1);
        }
    }


    /** Draws a vertical hallway with a pseudorandom width (1 or 2, according to the spec) & length. */
    public void drawVerticalHallway() {
        int width = uniform(rand, 1, HALLWAYWIDTHBOUND) + 1; // +1 to account for the wall
        int length = uniform(rand, 1, 10);
        Position hallLoc = new Position(MIDPOINTx + 10, MIDPOINTy + 10, width, length);

        for (int dy = 0; dy < length; dy++) {
            worldFrame[hallLoc.getxPos()][hallLoc.getyPos()] = Tileset.WALL;

            for (int dx = 1; dx < width; dx++) {
                worldFrame[hallLoc.getxPos() + dx][hallLoc.getyPos()] = Tileset.FLOOR;
            }

            worldFrame[hallLoc.getxPos() + width][hallLoc.getyPos()] = Tileset.WALL;

            hallLoc.changeyPos(-1);
        }
    }
}