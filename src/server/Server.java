package server;

import streaming.Room;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.*;

public class Server implements Runnable {
    private Hashtable<Integer, Room> rooms = new Hashtable<>();
    private final int ssl_port = 9000;
    private final String MSG = "AYY CAPTAIN";
    private DatagramSocket channel;
    private SSLServerSocket sslserversocket = null;


    public static void main(String[] args) {
        Server s = Singleton.getInstance().getServer();
        Thread msgHandling = new Thread(s);
        msgHandling.start();
        s.newRoom(Room.DEFAULTPORT);
    }


    public Server(int port) {
        try {
            channel = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        //CONNECT TO EXTERN SERVICE
        new Thread() {
            public void run() {
                while (true) {
                    byte[] buf = new byte[256];
                    DatagramPacket received = new DatagramPacket(buf, buf.length);
                    try {
                        channel.receive(received);
                        System.out.println("Got a ping from external service: "+new String (received.getData()));
                        DatagramPacket sent = new DatagramPacket(MSG.getBytes(), MSG.getBytes().length, received.getAddress(), received.getPort());
                        channel.send(sent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        System.setProperty("javax.net.ssl.keyStore", "keystore");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
        SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try {
            sslserversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(this.ssl_port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while (true) {
            try {
                final SSLSocket sslSocket = (SSLSocket) sslserversocket.accept();
                ServerClientHandler serverClient = new ServerClientHandler(sslSocket);
                new Thread(serverClient).start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public int newRoom() {
        return newRoom(0);
    }

    public int newRoom(int port) {
        Room r = new Room(port);
        new Thread(r).start();
        this.rooms.put(r.getPort(), r);
        return r.getPort();
    }

    public Hashtable<Integer, Room> getRooms() {
        return rooms;
    }

    public void setRooms(Hashtable<Integer, Room> rooms) {
        this.rooms = rooms;
    }

    public void removeRoom(int port) {
        this.rooms.remove(port);
    }
}
