package byow.Core;

/** Position keeps track of the positional data (x & y coordinates) of a room / hallway. It has two instance variables.
 * The functionality of each method is explained in greater depth below. The Position class is always instantiated with
 * the top-left position of the room / hallway. Note that asset refers to both rooms & hallways. */

public class Position {
    private int MIDx;
    private int MIDy;
    private int HALFWIDTH;
    private int HALFLENGTH;

    private int xPos;
    private int yPos;



    /** Constructor for the Position class, sets the xPos & yPos. */
    public Position(int x, int y, int w, int l) {
        HALFWIDTH = w / 2;
        HALFLENGTH = l / 2;

        MIDx = x + HALFWIDTH;
        MIDy = y - HALFLENGTH;
        xPos = x;
        yPos = y;
    }


    /** Changes xPos by the given value. */
    public void changexPos(int value) {
        xPos += value;
    }


    /** Changes xPos by the given value. */
    public void changeyPos(int value) {
        yPos += value;
    }


    /** Returns xPos. */
    public int getxPos() {
        return xPos;
    }


    /** Returns yPos. */
    public int getyPos() {
        return yPos;
    }


    /** Returns the midpoint x-coordinate of the asset. */
    public int getMidx() {
        return MIDx;
    }


    /** Returns the midpoint y-coordinate of the asset. */
    public int getMidy() {
        return MIDy;
    }


    /** Returns HALFWIDTH (WIDTH / 2). */
    public int getHalfWidth() {
        return HALFWIDTH;
    }


    /** Returns HALFHEIGHT (HEIGHT / 2). */
    public int getHalfLength() {
        return HALFLENGTH;
    }
}
