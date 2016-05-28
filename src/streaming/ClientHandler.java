package streaming;

import auth.Token;
import database.Database;
import org.json.JSONArray;
import org.json.JSONException;
import player.Converter;
import player.SCTrack;
import server.Singleton;
import streaming.messages.Message;
import streaming.messages.MessageException;
import streaming.messages.RequestMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

import static streaming.Room.FRAMESIZE;

/**
 * Created by diogo on 12/05/2016.
 */
public class ClientHandler implements Runnable {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket communicationSocket;
    private Socket streamingSocket;
    private InputStream streamIn;
    private boolean connected = true;
    private Room room;
    private int userId = new Random().nextInt(2048) + 1;
    private Database db;
    private Token token;
    private String username;

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public ClientHandler(Socket socket, Room room) {
        this.db = Singleton.getInstance().getDatabase();
        this.room = room;
        this.communicationSocket = socket;

        try {
            ServerSocket roomStreamingSocket = new ServerSocket(0);
            this.out = new ObjectOutputStream(this.communicationSocket.getOutputStream());
            this.in = new ObjectInputStream(this.communicationSocket.getInputStream());

            //message de recepçao de token
            boolean loggedIn = false;

            while (!loggedIn) {
                Message m = (Message) in.readObject();

                if (m.getType() == Message.Type.ONLY_TOKEN) {
                    loggedIn = true;
                    this.token = m.getToken();

                    //get user from database
                    this.username = db.getUserByToken(this.token.getToken());
                    System.out.println("client.RoomConnection: " + username + " " + token);
                }
            }

            //Send message with the streaming port for the client to connect to in order to receive streaming data
            out.writeObject(new Message(Message.Type.STREAM, null, new String[]{roomStreamingSocket.getLocalPort() + ""}));
            this.streamingSocket = roomStreamingSocket.accept();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] bytes) {
        try {
            streamingSocket.getOutputStream().write(bytes, 0, FRAMESIZE);
        } catch (SocketException s) {
            System.out.println("Client disconnected abruptly");
            connected = false;
        }
        catch (IOException e) {
            // IR A ROOM
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void handleMessage(Message message) {
        try {
            switch (message.getType()) {
                case VOTE_SKIP:
                    if (db.verifyToken(message.getToken().getToken()))
                        room.voteSkip(getUserId());
                    break;
                case REQUEST:
                    if (db.verifyToken(message.getToken().getToken())) {
                        RequestMessage requestMessage = (RequestMessage) message;
                        switch (requestMessage.getRequestType()) {
                            case SOUNDCLOUD:
                                SCTrack scTrack = room.getTrackGetter().getTrackByName(message.getArgs()[0], username);
                                room.getPlaylist().addRequestedTrack(scTrack);
                                sendMessage(new Message(Message.Type.TRUE, null, message.getArgs()));
                                break;
                            case STREAM_SONG:
                                System.out.println(message);
                                String temp = message.getArgs()[0];
                                String[] t = temp.split("/");
                                readSongFromUser(t[t.length - 1]);
                                room.getPlaylist().addRequestedUploadedTrack(t[t.length - 1], token.getToken());
                                sendMessage(new Message(Message.Type.TRUE, null, message.getArgs()));
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                default:
                    throw new MessageException("Message Type not valid");
            }
        } catch (MessageException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void readSongFromUser(String filename) {
        try {
            streamIn = streamingSocket.getInputStream();
            // write the inputStream to a FileOutputStream
            OutputStream outputStream = new FileOutputStream(new File(System.getProperty("user.dir") + "/resources/" + filename));

            int read = 0;
            byte[] bytes = new byte[Room.FRAMESIZE];

            DataInputStream dis = new DataInputStream(streamIn);
            int chunks = 0;
            chunks = dis.readShort();

            for (int i = 0; i < chunks; i++) {
                dis.read(bytes, 0, Room.FRAMESIZE);
                outputStream.write(bytes);
            }
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public void run() {
        while (connected) {
            if (communicationSocket.isClosed()) {
                setConnected(false);
                break;
            }

            try {
                final Message message = (Message) in.readObject();
                new Thread(() -> {
                    handleMessage(message);
                }).start();
            } catch (IOException e) {
                connected = false;
                //e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        this.room.removeClientFromList(this);

        try {
            System.out.println("ENTRARRR");
            streamingSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
