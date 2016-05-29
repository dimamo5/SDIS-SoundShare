package server;

import org.omg.SendingContext.RunTime;
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
    private final int CHECK_TIME = 30000; //2 mins
    private Timer time = new Timer();
    private final int minimumMemory = 50000;


    public static void main(String[] args) {
        Server s = Singleton.getInstance().getServer();
        Thread msgHandling = new Thread(s);
        msgHandling.start();
        s.newRoom(Room.DEFAULTPORT);
    }


    public Server(int port) {
        checkOverload();
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


    public void checkOverload() {
        time.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (minimumMemory > (Runtime.getRuntime().freeMemory()/1024) ) {
                    ProcessBuilder pb = new ProcessBuilder("cmd.exe","/c","java -cp out\\production\\SDIS-SoundShare;lib\\commons-lang3-3.4.jar;lib\\java-api-wrapper-1.2.0-all.jar;lib\\jave-1.0.2.jar;lib\\jl1.0.1.jar;lib\\mp3spi1.9.5.jar;lib\\sqlite-jdbc-3.8.11.2.jar;lib\\tritonus_share.jar server.Server");
                    pb.redirectOutput();
                    try {
                        Process p = pb.start();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String line = "";
                        while ((line = reader.readLine())!= null) {
                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, CHECK_TIME); //checkar de 30 em 30 secs
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
