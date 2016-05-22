import streaming.Room;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by m_bot on 19/05/2016.
 */
public class Server {
    private Hashtable<Integer, Room> rooms = new Hashtable<>();

    public static void main(String[] args){
        new Server().newRoom(Room.DEFAULTPORT);
    }

    public Server() {
    }

    public void newRoom(){
        newRoom(0);
    }

    public void newRoom(int port){
        Room r = new Room(port);
        new Thread(r).start();
        this.rooms.put(r.getPort(),r);
    }

    public void removeRoom(int port){
        this.rooms.remove(port);
    }
}
