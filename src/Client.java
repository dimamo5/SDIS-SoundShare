import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;

import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import streaming.Message;
import streaming.Room;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by diogo on 12/05/2016.
 */
public class Client  implements Runnable {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private InputStream streamIn;
    private Socket communicationSocket;
    private Socket streamingSocket;
    private boolean playing=false;
    public boolean connected=false;
    public AdvancedPlayer player;

    public static void main(String[] args){
        String serverAddress = args[0];
        int serverPort = new Integer(args[1]);

        try {
            Client client = new Client(InetAddress.getByName(serverAddress),serverPort);
            new Thread(client).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public Client(InetAddress serverAddress, int serverPort){
        try {
            System.out.println("add " + serverAddress + " port " + serverPort);
            communicationSocket = new Socket(serverAddress, serverPort);
            this.out = new ObjectOutputStream(communicationSocket.getOutputStream());
            this.in = new ObjectInputStream(communicationSocket.getInputStream());
            int streamingPort = 0;

            while(streamingPort == 0){
                Message message = (Message) in.readObject();
                System.out.println(streamingPort);
                if(message.getType().equals(Message.Type.STREAM)){
                    streamingPort = new Integer(message.getArg()[0]);
                    System.out.println("Streaming Port: "+streamingPort);
                    this.connected=true;
                }
            }

            this.streamingSocket = new Socket(serverAddress,streamingPort);
            streamIn = streamingSocket.getInputStream();
            this.play();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void sendMessage(Message message){
        try {
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Message getMessage(){
        try {
            return (Message) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean requestSong(String url){
        sendMessage(new Message(Message.Type.REQUEST,new String[]{url}));

        Message result = getMessage();
        if(result.getType().equals(Message.Type.AYY_CAPTAIN))
            return true;
        else
            return false;
    }

    public void skip(){
        sendMessage(new Message(Message.Type.VOTE_SKIP,new String[]{}));
    }

    public void play(){
        Client c= this;
        new Thread() {
            @Override
            public void run() {
                System.out.println("Playing!");
                try {
                    c.player=new AdvancedPlayer(c.streamIn);
                    c.player.play();
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void run() {
        while(connected){
            if(communicationSocket.isClosed()){
                connected=false;
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

    private void handleMessage(Message message) {
        switch(message.getType()){
            case MUSIC:
                System.out.println(message.toString());
                break;
        }
    }

}

