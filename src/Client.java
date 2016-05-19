import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import streaming.Room;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by diogo on 12/05/2016.
 */
public class Client implements Runnable{
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket communicationSocket;
    private Socket streamingSocket;
    private final int listenPort = 5000;
    private boolean playing=false;
    public AdvancedPlayer player;

    public static void main(String[] args) {
        new Client();
    }

    public Client(InetAddress serverAddress, int serverPort){
        byte[] buffer = new byte[streaming.Room.FRAMESIZE];
        int bytesRead=0;

        InputStream is = null;

        try {
            communicationSocket = new Socket(serverAddress, serverPort);
            this.out = new ObjectOutputStream(communicationSocket.getOutputStream());
            this.in = new ObjectInputStream(communicationSocket.getInputStream());

            is = streamingSocket.getInputStream();
        } catch (IOException ex) {
            // Do exception handling
        }
        try {
            streamingSocket.setReceiveBufferSize(64000);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        AudioBuffer abuffer= new AudioBuffer();

        try {
            this.player=new AdvancedPlayer(abuffer);

        } catch (JavaLayerException e) {
            e.printStackTrace();
        }

        byte[] teste=new byte[Room.FRAMESIZE];

        while(true){
            try {
                bytesRead=is.read(teste);
                if(bytesRead!=-1) {
                    System.out.println(bytesRead);
                    abuffer.write(teste);
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

    public void play(){
        Client c= this;
        new Thread() {
            @Override
            public void run() {
                System.out.println("Playing!");
                try {
                    c.player.play();
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void run() {

    }
}
