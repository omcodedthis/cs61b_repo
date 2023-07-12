package byow.Core;

import java.util.ArrayList;

/** RoomTracker keeps track of the location of all the rooms in the world. It has X instance variables. The
 * functionality of each method is explained in greater depth below. */

public class RoomTracker {
    public ArrayList<Position> roomList = new ArrayList<Position>();
    public int size;

    /** Constructor for instantiation. */
    public RoomTracker() {
        size = 0;
    }


    /** Constructor for instantiation with a room Position. */
    public RoomTracker(Position roomLoc) {
        roomList.add(roomLoc);
        size = 1;
    }


    /** Adds a room to roomList. */
    public void addRoom(Position roomLoc) {
        roomList.add(roomLoc);
        size += 1;
    }


    /** Returns the size of roomList. */
    public int getTotalRooms() {
        return size;
    }


    /** Returns roomList. */
    public ArrayList<Position> getRoomList() {
        return roomList;
    }

}
