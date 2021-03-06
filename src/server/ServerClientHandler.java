package server;

import auth.Token;
import streaming.Room;
import streaming.messages.ListRoomMessage;
import streaming.messages.Message;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.Hashtable;

/**
 * Created by duarte on 25-05-2016.
 */
public class ServerClientHandler implements Runnable {
    protected SSLSocket socket;
    protected ObjectInputStream in;
    protected ObjectOutputStream out;
    protected boolean loggedIn = true;

    public ServerClientHandler(SSLSocket socket) {
        this.socket = socket;
        initStreams();
    }

    private void initStreams() {
        try {
            this.in = new ObjectInputStream(this.socket.getInputStream());
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendMessage(Message message) {
        try {
            this.out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(Message message) {
        switch (message.getType()) {
            case CONNECT:
                String stringToken = Singleton.getInstance().getDatabase().select_user_by_credentials(message.getArgs()[0], message.getArgs()[1]);

                if (stringToken.equals("ERROR")) {
                    sendMessage(new Message(Message.Type.FALSE));
                    return;
                }

                Token token = new Token(stringToken);
                sendMessage(new Message(Message.Type.TOKEN, token));
                loggedIn = true;
                break;
            case DISCONNECT:
                loggedIn = false;
                break;
            case ROOM_LIST:
                Hashtable<Integer, Room> roomList = Singleton.getInstance().getServer().getRooms();
                Message roomListMessage = new ListRoomMessage(roomList);
                sendMessage(roomListMessage);
                break;
            case NEW_ROOM:
                int newRoom = Singleton.getInstance().getServer().newRoom();
                sendMessage(new Message(Message.Type.NEW_ROOM, newRoom + ""));
                break;
            default:
                break;
        }
    }

    @Override
    public void run() {
        while (loggedIn) {
            try {

                socket.startHandshake();

                while (true) {
                    Message message = (Message) in.readObject();
                    handleMessage(message);
                }


            } catch (SocketException exception) {
                System.out.println("End point(client) disconnected.");
                try {
                    if (socket != null) {
                        loggedIn = false;
                        socket.close(); //close this point
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                //exception.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return;
        }
    }
}
