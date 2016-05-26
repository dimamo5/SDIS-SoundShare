package client;


import auth.Credential;
import player.Track;
import server.Singleton;
import streaming.Room;
import streaming.messages.Message;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Sonhs on 22/05/2016.
 */
public class ServerConnection {

    private SSLSocketFactory sslsocketfactory;
    private SSLSocket sslsocket;
    private String serverAddress;
    private int serverPort;
    private final int sslPort = 9000;

    private String room_list;
    private ObjectOutputStream outputstream;
    private ObjectInputStream inputStream;

    public String getRoom_list() {
        return room_list;
    }

    public void setRoom_list(String room_list) {
        this.room_list = room_list;
    }

    public ServerConnection(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        System.setProperty("javax.net.ssl.trustStore", "keystore");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
        try {
            sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            sslsocket = (SSLSocket) sslsocketfactory.createSocket(serverAddress, sslPort);
            sslsocket.startHandshake();
            this.outputstream = new ObjectOutputStream(sslsocket.getOutputStream());
            this.inputStream = new ObjectInputStream(sslsocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean connectToServer(Credential credentials) {
        try {
            sendConnectMessage(credentials);

            if (!receiveToken()) {
                return false;
            }

            // TODO: 23/05/2016 merge related (below) -> uncomment after merge  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //ListRoomMessage m = (ListRoomMessage) ois.readObject();
            //this.room_list = m.toString();
            //System.out.println(s);
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    private boolean receiveToken() throws IOException {
        try {
            Message message = (Message) inputStream.readObject();

            if (!message.getType().equals(Message.Type.TOKEN))
                return false;

            Client.getInstance().setToken(message.getToken());
            return true;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void logout() {
        sendMessage(new Message(Message.Type.DISCONNECT, Client.getInstance().getToken()));
    }

    public void sendMessage(Message message) {
        try {
            this.outputstream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Message receiveMessage() {
        try {
            return (Message) this.inputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    private void sendConnectMessage(Credential credentials) throws IOException {
        Message connectMessage = new Message(Message.Type.CONNECT, new String[]{credentials.getUsername(), credentials.getPassword()});
        outputstream.writeObject(connectMessage);
    }
}
