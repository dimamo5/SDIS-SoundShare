package server;

import database.Database;
import player.Track;
import streaming.Room;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class Server implements Runnable{
    private Hashtable<Integer, Room> rooms = new Hashtable<>();
    private final int ssl_port = 9000;
    private SSLServerSocket sslserversocket = null;

    public static void main(String[] args){
        Server s = Singleton.getInstance().getServer();

        Thread msgHandling = new Thread(s);
        msgHandling.start();
        s.newRoom(Room.DEFAULTPORT);
    }

    public String roomsToString() {
        String[] args = new String[rooms.size() * 2];
        int i = 0;

        Iterator it = rooms.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            Room r = (Room) pair.getValue();

            args[i] = String.valueOf(r.getPort());
            Track t = r.getPlaylist().getCurrentTrack();
            if (t == null)
                args[i + 1] = "No music playing";
            else args[i + 1] = t.getInfo().getTitle();
            i = i + 2;
        }
        return args.toString();
    }

    public Server() {
        System.setProperty("javax.net.ssl.keyStore","keystore");
        System.setProperty("javax.net.ssl.keyStorePassword","123456");
        SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        try {
            sslserversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(this.ssl_port);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void run() {
        while(true){
            try {
                final SSLSocket sslSocket = (SSLSocket) sslserversocket.accept();
                ServerClientHandler serverClient = new ServerClientHandler(sslSocket);
                new Thread(serverClient).start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public int newRoom(){
        return newRoom(0);
    }

    public int newRoom(int port){
        Room r = new Room(port);
        new Thread(r).start();
        this.rooms.put(r.getPort(),r);
        return r.getPort();
    }

    public Hashtable<Integer, Room> getRooms() {
        return rooms;
    }

    public void setRooms(Hashtable<Integer, Room> rooms) {
        this.rooms = rooms;
    }

    public void removeRoom(int port){
        this.rooms.remove(port);
    }
}
