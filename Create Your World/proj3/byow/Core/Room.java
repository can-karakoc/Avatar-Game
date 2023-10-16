package byow.Core;

import java.util.ArrayList;
import java.util.List;

public class Room extends Build {
    List<Room> connectedRooms;
    public Room(int x, int y, int w, int h) {
        super(x, y, w, h);
        type = "room";
        connectedRooms = new ArrayList<>();
    }
    public boolean isConnected(Room r) {
        return connectedRooms.contains(r);
    }

}