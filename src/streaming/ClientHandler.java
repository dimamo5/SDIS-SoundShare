package streaming;

import database.Database;
import player.InfoMusic;
import player.Track;
import player.UploadedTrack;
import streaming.messages.Message;
import streaming.messages.MessageException;
import streaming.messages.RequestMessage;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import static streaming.Room.FRAMESIZE;

/**
 * Created by diogo on 12/05/2016.
 */
public class ClientHandler implements Runnable{
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket communicationSocket;
    private Socket streamingSocket;
    private InputStream streamIn;
    private boolean connected = true;
    private Room room;
    private int userId = new Random().nextInt(2048)+1;
    private Database db;

    public ClientHandler(Socket socket, Room room){
        this.db = Database.getInstance();
        this.room = room;
        this.communicationSocket = socket;

        // TODO: 19-05-2016 Verificar se ao criar o streaming socket desta maneira ele já atribuí um port para o client se ligar

        try {
            ServerSocket serverSocketStreaming = new ServerSocket(0);
            this.out = new ObjectOutputStream(this.communicationSocket.getOutputStream());
            this.in = new ObjectInputStream(this.communicationSocket.getInputStream());

            //Send message with the streaming port for the ClientHandler to connect to in order to receive streaming data
            out.writeObject(new Message(Message.Type.STREAM, "", new String[]{serverSocketStreaming.getLocalPort()+""} ));
            this.streamingSocket = serverSocketStreaming.accept();
         } catch (IOException e) {
             e.printStackTrace();
         }
    }

    public void send(byte[] bytes){
        try {
            streamingSocket.getOutputStream().write(bytes, 0, FRAMESIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message){
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

    public void handleMessage(Message message){
        try{
            switch (message.getType()){
                case VOTE_SKIP:
                    if (db.verifyToken(message.getToken()))
                        room.voteSkip(getUserId());
                    break;
                case REQUEST:
                    if (db.verifyToken(message.getToken())) {
                        RequestMessage requestMessage = (RequestMessage) message;
                        switch (requestMessage.getRequestType()) {
                            case SOUNDCLOUD:
                                break;
                            case STREAM_SONG:
                                System.out.println(message);
                                readSongFromUser(message.getArg()[0]);
                                sendMessage(new Message(Message.Type.TRUE, "", message.getArg()));
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                default:
                    throw new MessageException("Message Type not valid");
            }
        }catch (MessageException e) {
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

            while ((read = streamIn.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
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
        while(connected){
            if(communicationSocket.isClosed()){
                setConnected(false);
                break;
            }

            try {
                final Message message = (Message) in.readObject();
                new Thread(() -> {
                    handleMessage(message);
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        try {
            streamingSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
