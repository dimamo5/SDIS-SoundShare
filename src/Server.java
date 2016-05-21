import JSSE.JSSEServer;
import database.Database;
import streaming.Room;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Hashtable;

public class Server implements Runnable{
    private Hashtable<Integer, Room> rooms = new Hashtable<>();
    private final int port = 9000;
    private SSLServerSocket sslserversocket = null;
    private SSLSocket sslsocket = null;
    private Database db = null;

    public static void main(String[] args){
        Server s = new Server();

        Thread msgHandling = new Thread(s);
        msgHandling.start();
        s.newRoom(Room.DEFAULTPORT);
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

    public String[] readUser(int porta) {
        System.setProperty("javax.net.ssl.keyStore","keystore");
        System.setProperty("javax.net.ssl.keyStorePassword","123456");

        SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try {
            sslserversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(porta);
            System.out.println("waiting");
            sslsocket = (SSLSocket) sslserversocket.accept();
            sslsocket.startHandshake();
            System.out.println("hand");
            InputStream inputstream = sslsocket.getInputStream();
            byte[] msg = new byte[128];

            int bytesRead = inputstream.read(msg);
            String[] ret = parseMessage(msg);
            ret[2] = ret[2].trim();

            String token = db.select_user_by_credentials(ret[1], ret[2]);

            String msgSend = "CONNECT " + token;

            OutputStream outputStream = sslsocket.getOutputStream();
            outputStream.write(msgSend.getBytes());
            sslsocket.close();
            sslserversocket.close();
            return ret;

        } catch (SocketException exception) {
            if(exception.toString().equals("java.net.SocketException: Connection reset")){
                System.out.println("End point(client) disconnected.");
                try {
                    if(sslsocket != null) {
                        sslsocket.close(); //close this point
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return new String[1];
                }
            }
            exception.printStackTrace();
            return new String[1];
        }
        catch (IOException e) {
            e.printStackTrace();
            return new String[1];
        }
    }

    private String[] parseMessage(byte[] msg) {
        StringBuilder sb = new StringBuilder();
        String[] ret = new String[3];
        int contador = 0;
        StringBuilder temp = new StringBuilder("");

        for(int i = 0; i < msg.length; i++) {
            sb.append((char)msg[i]);
        }

        for (int i = 0; i < msg.length - 1; i++) {
            if (sb.substring(i, i + 1).equals(" ")) {
                i++;
                ret[contador] = temp.toString();
                temp = new StringBuilder("");
                contador++;
            }
            if (msg.length - 2 == i) {
                temp.append(sb.substring(i, i+1));
                ret[contador] = temp.toString();
                contador++;
                break;
            }
            else temp.append(sb.substring(i, i + 1));
        }
        return ret;
    }
}
