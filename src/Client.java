import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import streaming.Message;
import streaming.Room;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by diogo on 12/05/2016.
 */
public class Client implements Runnable{
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private InputStream streamIn;
    private Socket communicationSocket;
    private Socket streamingSocket;
    public AudioBuffer aBuffer;
    private boolean playing=false;
    public AdvancedPlayer player;

    public static void main(String[] args) {
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
        aBuffer= new AudioBuffer();
        try {
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
                }
            }

            this.streamingSocket = new Socket(serverAddress,streamingPort);
            streamIn = streamingSocket.getInputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void play(){
        Client c= this;
        new Thread() {
            @Override
            public void run() {
                System.out.println("Playing!");
                try {
                    c.player=new AdvancedPlayer(c.aBuffer);
                    c.player.play();
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void run() {
        byte[] buffer = new byte[streaming.Room.FRAMESIZE];
        int bytesRead=0;

        try {
            streamingSocket.setReceiveBufferSize(64000);
        } catch (SocketException e) {
            e.printStackTrace();
        }



        byte[] teste=new byte[Room.FRAMESIZE];

        while(true){
            try {
                bytesRead=streamIn.read(teste);
                if(bytesRead!=-1) {
                    System.out.println(bytesRead);
                    aBuffer.write(teste);
                    if(!playing){
                        this.play();
                        this.playing=true;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            /*try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
    }
}
