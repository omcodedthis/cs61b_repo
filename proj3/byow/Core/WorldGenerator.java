package byow.Core;

import afu.org.checkerframework.checker.oigj.qual.O;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
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
    private RoomTracker rooms;
    private Random rand;

    /** World Assets constants. */
    public static final int ROOMMIN = 5;
    public static final int ROOMMAX = 10;
    public static final int LINKBOUND = 1;
    public static final int HALLWAYWIDTHBOUND = 3;


    /** Constructor for this class, which sets multiple global constants & fills worldFrame with NOTHING tiles. */
    public WorldGenerator(TETile[][] frame, int width, int height, long seed) {
        worldFrame = frame;
        WIDTH = width;
        HEIGHT = height;
        MIDPOINTx = WIDTH / 2;
        MIDPOINTy = HEIGHT / 2;
        rooms = new RoomTracker();
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


    /** Draws a world filled with rooms & hallways. */
    public void drawWorld() {
        drawSectors();
        connectRooms();
    }


    /** Returns worldFrame. */
    public TETile[][] getWorld() {
        return worldFrame;
    }


    /** Splits the world into five sectors. Each sector has a maximum of two rooms. */
    public void drawSectors() {
        int sectorWidth = WIDTH / 5;
        int x = ORIGIN;
        int y = uniform(rand, ORIGIN + ROOMMAX, HEIGHT);

        for (int s = 1; s < 6; s++) {
            for (int i = 0; i < 1; i++) {
                drawRoom(x, y);
            }
            y = randomY(y);

            if (drawSecondRoom()) {
                drawRoom(x+ROOMMIN, y);
            }

            x = sectorWidth * s;
            y = randomY(y);
        }
    }


    /** Using pseudorandomness to determine whether a second room in the sector should be drawn. */
    public boolean drawSecondRoom() {
        int outcome = uniform(rand, 0, 5);

        if (outcome == 0) {
            return false;
        } else {
            return true;
        }
    }


    /** Returns a random y-value that is between the bounds of ORIGIN & HEIGHT. The new y-value is also has an absolute
     * difference greater than ROOMMIN so that rooms are spaced apart sufficiently. */
    public int randomY(int prevY) {
        int newY = uniform(rand, ORIGIN + ROOMMAX, HEIGHT);
        int absoluteDifference = Math.abs(newY - prevY);

        while (absoluteDifference <= ROOMMIN) {
            newY = uniform(rand, ORIGIN + ROOMMAX, HEIGHT);
            absoluteDifference = Math.abs(newY - prevY);
        }
        return newY;
    }


    /** Draws a room (top-down) with a pseudorandom width & length. */
    public void drawRoom(int x, int y) {
        int width = uniform(rand, ROOMMIN, ROOMMAX);
        int length = uniform(rand, ROOMMIN, ROOMMAX);
        Position roomLoc = new Position(x, y, width, length);
        rooms.addRoom(roomLoc);

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


    /** Connects all the rooms. */
    public void connectRooms() {
        ArrayList<Position> roomList = rooms.getRoomList();

        for (int i = 0; i < (rooms.size - 1); i++) {
            Position roomA = roomList.get(i);
            Position roomB = roomList.get(i + 1);

            drawLink(roomA, roomB);
        }
    }


    /** Draws a 'L' shaped hallway between 2 rooms. */
    public void drawLink(Position roomA, Position roomB) {
        int aY = roomA.getMidy();
        int bY = roomB.getMidy();
        int difference = aY - bY;

        if (difference < -LINKBOUND) {
            drawUpLink(roomA, roomB);
        } else if (difference > LINKBOUND) {
            drawDownLink(roomA, roomB);
        } else {
            drawStraightLink(roomA, roomB, difference);
        }
    }


    /** Draws a 'L' shaped hallway between 2 rooms where roomA is higher than roomB (in terms of y-coordinates). */
    public void drawUpLink(Position roomA, Position roomB) {
        int aX = roomA.getMidx() + roomA.getHalfWidth() - 1;
        int aY = roomA.getMidy() + 1;
        int bX = roomB.getMidx() - 1;
        int bY = roomB.getMidy() - roomB.getHalfLength() + 2; // +2 so that the link ends at least inside the room.
        int width = uniform(rand, 1, HALLWAYWIDTHBOUND);
        int i;

        drawL(aX, aY, bX, bY, Tileset.WALL);
        for (i = 1; i < width + 1; i++) {
            drawL(aX, aY - i, bX + i, bY, Tileset.FLOOR);
        }
        drawL(aX, aY - i, bX + i, bY, Tileset.WALL);
    }



    /** Draws a 'L' shaped hallway between 2 rooms where roomA is lower than roomB (in terms of y-coordinates). */
    public void drawDownLink(Position roomA, Position roomB) {
        int aX = roomA.getMidx() + roomA.getHalfWidth();
        int aY = roomA.getMidy() + 1;
        int bX = roomB.getMidx() + 1;
        int bY = roomB.getMidy() + roomB.getHalfLength();
        int width = uniform(rand, 1, HALLWAYWIDTHBOUND);
        int i;

        drawL(aX, aY, bX, bY, Tileset.WALL);
        for (i = 1; i < width + 1; i++) {
            drawL(aX - i, aY - i, bX - i, bY - i, Tileset.FLOOR);
        }
        drawL(aX, aY - i, bX - i, bY, Tileset.WALL);
    }


    /** Draws a horizontal 'I' shaped hallway between 2 rooms where roomA is equal to roomB
     * (in terms of y-coordinates). */
    public void drawStraightLink(Position roomA, Position roomB, int difference) {
        int aX = roomA.getMidx() + roomA.getHalfWidth();
        int bX = roomB.getMidx() + 1;
        int bY = roomB.getMidy() + 1 + difference; // + difference to position the hallway properly between both rooms.
        int width = uniform(rand, 1, HALLWAYWIDTHBOUND);
        int i;

        drawI(aX, bX, bY, Tileset.WALL);
        for (i = 1; i < width + 1; i++) {
            drawI(aX - i, bX - i, bY - i, Tileset.FLOOR);
        }
        drawI(aX, bX - i, bY - i, Tileset.WALL);

    }


    /** Draws a down 'L' shape of tile tileType. */
    public void drawL(int aX, int aY, int bX, int bY, TETile tileType) {
        for (int x = aX; x < bX; x++) {
            worldFrame[x][aY] = tileType;
        }

        if (aY >= bY) {
            for (int y = bY; y < aY + 1; y++) {
                worldFrame[bX][y] = tileType;
            }
        } else {
            for (int y = aY; y < bY; y++) {
                worldFrame[bX][y] = tileType;
            }
        }
    }


    /** Draws a horizontal 'I' shape of tile tileType. */
    public void drawI(int aX, int bX, int bY, TETile tileType) {
        for (int x = aX; x < bX; x++) {
            worldFrame[x][bY] = tileType;
        }
    }
}