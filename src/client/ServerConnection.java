package client;


import auth.Credential;
import auth.Token;
import streaming.messages.Message;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;

/**
 * Created by Sonhs on 22/05/2016.
 */
public class ServerConnection {

    private SSLSocketFactory sslsocketfactory;
    private SSLSocket sslsocket;
    private  String serverAddress;
    private int serverPort;
    private final int sslPort = 9000;

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

        System.setProperty("javax.net.ssl.trustStore","keystore");
        System.setProperty("javax.net.ssl.trustStorePassword","123456");
        try {
            sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            sslsocket = (SSLSocket) sslsocketfactory.createSocket(serverAddress, sslPort);
            sslsocket.startHandshake();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean connectToServer(Credential credentials) {
        try {
            sendConnectMessage(credentials);

            if(!receiveToken()){
                return false;
            }
            ObjectInputStream ois = new ObjectInputStream(sslsocket.getInputStream());

            // TODO: 23/05/2016 merge related (below) -> uncomment after merge  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //ListRoomMessage m = (ListRoomMessage) ois.readObject();
            //this.room_list = m.toString();
            //System.out.println(s);

            sslsocket.close();
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    private boolean receiveToken() throws IOException {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(sslsocket.getInputStream());
            Message message = (Message) inputStream.readObject();
            if(!message.getType().equals(Message.Type.TOKEN))
                return false;
            Client.getInstance().setToken(new Token(message.getArgs()[0]));
            return true;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void sendConnectMessage(Credential credentials) throws IOException {
        ObjectOutputStream outputstream = new ObjectOutputStream(sslsocket.getOutputStream());

        Message connectMessage = new Message(Message.Type.CONNECT,new String[]{credentials.getUsername(),credentials.getPassword()});
        outputstream.writeObject(connectMessage);
    }
}
