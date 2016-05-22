import JSSE.JSSEServer;
import database.Database;
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
        db = Database.getInstance();
    }

    @Override
    public void run() {
        while(true){
                String[] st = readUser(port);
        }
    }

    private void handleMessage(String[] st) {
        switch (st[0]) {
            case "CONNECT":
                String token = db.select_user_by_credentials(st[1], st[2]);
                break;
            default:
                break;
        }
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
