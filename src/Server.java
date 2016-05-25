import database.Database;
import streaming.Room;
import streaming.messages.Message;
import util.Singleton;

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
    private SSLSocket sslsocket = null;
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
            Message message = readUser(ssl_port);
            handleMessage(message);
        }
    }

    private void handleMessage(Message message) {
        switch (message.getType()) {
            case CONNECT:
                String token = db.select_user_by_credentials(message.getArg()[0], message.getArg()[1]);
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

    public Message readUser(int porta) {

        try {
            sslsocket = (SSLSocket) sslserversocket.accept();
            sslsocket.startHandshake();
            ObjectInputStream inputstream = new ObjectInputStream(sslsocket.getInputStream());

            Message message = (Message) inputstream.readObject();

            String token = db.select_user_by_credentials(message.getArg()[0], message.getArg()[1]);

            Message connectMessage = new Message(Message.Type.TOKEN, new String[]{token});

            ObjectOutputStream outputStream = new ObjectOutputStream(sslsocket.getOutputStream());
            outputStream.writeObject(connectMessage);
           // sslsocket.close();
            //sslserversocket.close();
            return message;

        } catch (SocketException exception) {
            if(exception.toString().equals("java.net.SocketException: Connection reset")){
                System.out.println("End point(client) disconnected.");
                try {
                    if(sslsocket != null) {
                        sslsocket.close(); //close this point
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            exception.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
