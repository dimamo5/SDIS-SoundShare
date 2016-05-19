package streaming;

import player.Converter;
import player.Playlist;
import player.Track;

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
public class Room implements Runnable{
    public static final int DEFAULTPORT = 5000;
    public static final int FRAMESIZE = 2048;
    private static final int MAX_NUM_SKIP_VOTES = 5;


    private ServerSocket socket;
    private int port = 0;
    private ArrayList<User> clients = new ArrayList<User>();
    private Semaphore sem = new Semaphore(1);
    private Timer timer = new Timer();
    private double musicSec = 0;
    private Playlist playlist = new Playlist();
    private Set<Integer> skipList = new TreeSet<>();

    public static void main(String[] args) {
        Room r = new Room(listenPort);
    }

    public void fillPlayList() {
        new Converter("resources/batmobile.wav","resources/test1.mp3").encodeMP3();
        new Converter("resources/renegades.mp3","resources/test2.mp3").encodeMP3();

        playlist.addRequestedTrack("test1.mp3", "Local");
        playlist.addRequestedTrack("test2.mp3", "Local");
    }

    public Room() {
        this(0);
    }

    public Room(int port) {
        this.fillPlayList();

        try {
            socket = new ServerSocket(port);
            this.port = socket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
        }

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                musicSec += 0.5;
                if (musicSec == playlist.getCurrentTrack().getFullTime()) {
                    playlist.skipTrack();
                    musicSec=0;
                } else if (musicSec / playlist.getCurrentTrack().getFullTime() >= 0.9) {
                    Track t = playlist.getNextTrack();
                    if(t!=null)
                        sendNewTrack(playlist.getNextTrack());
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
}
