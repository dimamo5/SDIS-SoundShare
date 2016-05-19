package streaming;

import com.sun.deploy.util.SessionState;
import com.sun.javafx.scene.layout.region.Margins;
import player.Converter;
import player.Playlist;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.Timer;

/**
 * Created by diogo on 12/05/2016.
 */
public class Room{
    private static ServerSocket socket;
    private static final int listenPort = 5000;
    public static final int FRAMESIZE = 2048;
    private ArrayList<User> clients = new ArrayList<User>();
    private Semaphore sem = new Semaphore(1);
    private Timer timer = new Timer();
    private double musicSec = 0;
    private Playlist playlist = new Playlist();

    public static void main(String[] args) {
        Room r=new Room();
        r.fillPlayList();
    }

    public void fillPlayList(){
        Converter conv = new Converter("resources/batmobile.wav", "resources/batmobile.mp3");
        conv.encodeMP3();
        conv = new Converter("resources/renegades.mp3", "resources/renegades.mp3");
        conv.encodeMP3();
        conv = new Converter("resources/batmobile.mp3", "resources/batmobile.mp3");
        conv.encodeMP3();

        playlist.addRequestedTrack("batmobile.mp3","Local");
        playlist.addRequestedTrack("renegades.mp3","Local");
        playlist.addRequestedTrack("batmobile.mp3","Local");
    }

    public Room(){

        try {
            socket = new ServerSocket(listenPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                musicSec+=0.5;
                if(musicSec/playlist.getCurrentTrack().getFullTime()>=0.9){
                    sendNewTrack();
                }
            }
        }, 500, 500);


        while (true) {
            try {
                Socket connectionSocket = socket.accept();
                connectionSocket.setSendBufferSize(64000);
                sem.acquire();
                User c = new User(connectionSocket);
                clients.add(c);
                sendActualTrack(c);
                sem.release();
                if (clients.size() == 1) {
                    musicSec = 0;
                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendNewTrack(){
        for(User user:clients)
        new Thread() {
            @Override
            public void run() {
                user.sendFile(playlist.getNextTrack().getFile(),0);
            }
        }.start();
    }

    public void sendActualTrack(User u){
        new Thread() {
            @Override
            public void run() {
                u.sendFile(playlist.getCurrentTrack().getFile(), musicSec);
            }
        }.start();
    }

}
