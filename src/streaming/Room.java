package streaming;

import org.json.JSONException;
import player.Converter;
import player.Playlist;
import player.SCTrack;
import player.Track;
import soundcloud.TrackGetter;
import streaming.messages.MusicMessage;
import util.ServerSingleton;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Created by diogo on 12/05/2016.
 */
public class Room implements Runnable{
    public static final int DEFAULTPORT = 5000;
    public static final int FRAMESIZE = 2048;
    private static final int MAX_NUM_SKIP_VOTES = 5;

    private ServerSocket socket;
    private int port = 0;
    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
    public Semaphore clientsSemaphore = new Semaphore(1);
    private Timer timer = new Timer();
    private double musicSec = 0;
    private Playlist playlist = new Playlist();
    private Set<Integer> skipList = new TreeSet<>();
    private TrackGetter trackGetter = new TrackGetter(ServerSingleton.getInstance().getSoundCloudComms());

    public Playlist getPlaylist() {
        return playlist;
    }

    public static void main(String[] args) {
        Room r = new Room(DEFAULTPORT);
        new Thread(r).start();
    }

    public void fillPlayList() {
        new Converter("resources/batmobile.wav","resources/batmobile.mp3").encodeMP3();
        //new Converter("resources/little_mermaid_choices.wav","resources/mermaid.mp3").encodeMP3();

        //playlist.addRequestedUploadedTrack("batmobile.mp3", "Local");
        playlist.addRequestedUploadedTrack("batmobile.mp3", "Local");
        playlist.addRequestedUploadedTrack("renegades.mp3", "Local");
        //playlist.addRequestedUploadedTrack("renegades.mp3", "Local");
        //playlist.addRequestedUploadedTrack("renegades.mp3", "Local");

        try {
            SCTrack scTrack = trackGetter.getTrackByName("numb","client01");
            playlist.addRequestedTrack(scTrack);
        } catch (JSONException e) {
            e.printStackTrace();
        }


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
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
        try {
            this.socket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void voteSkip(int user){
        skipList.add(user);
        if (skipList.size() >= MAX_NUM_SKIP_VOTES){
            skipTrack();
        }
    }

    public void skipTrack(){
        skipList.clear();
        playlist.skipTrack();
        //sendNewTrack(playlist.getCurrentTrack());
    }

    public void sendNewTrack(Track track) {
        final Room room = this;
        for (ClientHandler clientHandler : clients)
            new Thread() {
                @Override
                public void run() {
                    Track t = playlist.getNextTrack();
                    if (t != null)
                        playlist.getNextTrack().sendTrack(musicSec,room);
                }
            }.start();
    }

    public void sendActualTrack(ClientHandler u) {
        final Room room = this;
        new Thread() {
            @Override
            public void run() {
                System.out.println("send actual track");
                playlist.getCurrentTrack().sendTrack(musicSec,room);
            }
        }.start();
    }

    public void sendNewTrackMessageToAllClients(Track track, double sec) {
        for (ClientHandler clientHandler :clients){
            sendMusicMessage(clientHandler, track,sec);
        }
    }

    public void sendMusicMessage(ClientHandler clientHandler, Track track, double sec){
        MusicMessage message = new MusicMessage(track, sec);
        clientHandler.sendMessage(message);
    }

    public ArrayList<ClientHandler> getClients() {
        return clients;
    }

    public void setClients(ArrayList<ClientHandler> clients) {
        this.clients = clients;
    }

    public void startTimer(){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                musicSec += 0.5;
                if (musicSec == playlist.getCurrentTrack().getInfo().getFullTime() && playlist.getNextTrack().isSent() != false) {
                    skipTrack();
                    musicSec=0;
                } else if (musicSec / playlist.getCurrentTrack().getInfo().getFullTime() >= 0.9) {
                    Track t = playlist.getNextTrack();
                    if(t!=null && !t.isSent()) {
                        t.setSent(true);
                        sendNewTrack(playlist.getNextTrack());
                    }
                }
            }
        }, 500, 500);
    }

    @Override
    public void run() {
        System.out.println("Room started!");

        startTimer();

        while (true) {
            try {
                Socket connectionSocket = socket.accept();
                connectionSocket.setSendBufferSize(64000);
                clientsSemaphore.acquire();
                ClientHandler c = new ClientHandler(connectionSocket,this);
                new Thread(c).start();
                sendMusicMessage(c,playlist.getCurrentTrack(),musicSec);
                clients.add(c);
                sendActualTrack(c);
                clientsSemaphore.release();
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
