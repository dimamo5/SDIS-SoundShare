package Client;

import javafx.util.Pair;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.Scanner;

/**
 * Created by Sonhs on 22/05/2016.
 */
public class ServerConnection {

    private SSLSocketFactory sslsocketfactory = null;
    private SSLSocket sslsocket = null;
    private  String serverAddress;
    private int serverPort;
    private final int sslPort = 9000;
    private String token = null;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String room_list;

    public String getRoom_list() {
        return room_list;
    }

    public void setRoom_list(String room_list) {
        this.room_list = room_list;
    }

    public ServerConnection(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        Pair<String,String> credentials = receiveInputCredentials();
        connectToServer(credentials);
    }

    public Pair<String,String> receiveInputCredentials(){

        Scanner reader = new Scanner(System.in);
        System.out.print("Name: ");
        String name = reader.next();
        System.out.print("Pass: ");
        String pass = reader.next();
        return new Pair<>(name,pass);

    }

    public void connectToServer(Pair<String,String> credentials) {
        try {
            System.setProperty("javax.net.ssl.trustStore","keystore");
            System.setProperty("javax.net.ssl.trustStorePassword","123456");

            sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            sslsocket = (SSLSocket) sslsocketfactory.createSocket(serverAddress, sslPort);
            sslsocket.startHandshake();

            OutputStream outputstream = sslsocket.getOutputStream();

            String msg = "CONNECT " + credentials.getKey() + " " + credentials.getValue();
            outputstream.write(msg.getBytes());

            InputStream inputStream = sslsocket.getInputStream();
            byte[] b = new byte[64];
            int bytesRead = inputStream.read(b);
            parseToken(b, bytesRead);

            ObjectInputStream ois = new ObjectInputStream(inputStream);

            // TODO: 23/05/2016 merge related (below) -> uncomment after merge  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //ListRoomMessage m = (ListRoomMessage) ois.readObject();
            //this.room_list = m.toString();
            //System.out.println(s);

            sslsocket.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void parseToken(byte[] b, int bytesRead) {
        token = new String(b, 0, bytesRead);
        token = token.substring("CONNECT ".length(), token.length());
    }
}
