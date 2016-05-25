import database.Database;
import server.ServerClient;
import streaming.Room;
import streaming.messages.Message;
import server.Singleton;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.SocketException;
import java.util.Hashtable;

public class Server implements Runnable{
    private Hashtable<Integer, Room> rooms = new Hashtable<>();
    private final int ssl_port = 9000;
    private SSLServerSocket sslserversocket = null;
    private Database db = null;

    public static void main(String[] args){
        Server s = new Server();

        Thread msgHandling = new Thread(s);
        msgHandling.start();
        s.newRoom(Room.DEFAULTPORT);
    }

    public Server() {
        db = Singleton.getInstance().getDatabase();

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
                ServerClient serverClient = new ServerClient(sslSocket);
                new Thread(serverClient).start();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
