package byow.Core;

/** Position keeps track of the positional data (x & y coordinates) of a room / hallway. It has two instance variables.
 * The functionality of each method is explained in greater depth below. The Position class is always instantiated with
 * the top-left position of the room / hallway. Note that asset refers to both rooms & hallways. */

public class Position {
    private int TOPLx;
    private int TOPLy;
    private int xPos;
    private int yPos;
    private int width;
    private int length;


    /** Constructor for the Position class, sets the xPos & yPos. */
    public Position(int x, int y, int w, int l) {
        TOPLx = x;
        TOPLy = y;
        xPos = x;
        yPos = y;
        width = w;
        length = l;
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
        int halfWidth = width / 2;
        return TOPLx + halfWidth;
    }


    /** Returns the midpoint y-coordinate of the asset. */
    public int getMidy() {
        int halfLength = length / 2;
        return TOPLy + halfLength;
    }
}
