import streaming.Room;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by m_bot on 19/05/2016.
 */
public class Server {
    private Hashtable<Integer, Room> rooms;

    public static void main(String[] args){
        new Server();
    }

    public Server() {
        Room r = new Room(Room.DEFAULTPORT);
        this.rooms.put(r.getPort(),r);
    }
    public void newRoom(){
        Room r = new Room();
        this.rooms.put(r.getPort(),r);
    }

    public void removeRoom(int port){
        this.rooms.remove(port);
    }
}
