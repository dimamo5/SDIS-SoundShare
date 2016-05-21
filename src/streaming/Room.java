package streaming;

import org.json.JSONException;
import org.json.JSONObject;
import player.*;
import soundcloud.SCComms;

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
    private ArrayList<User> clients = new ArrayList<User>();
    private Semaphore sem = new Semaphore(1);
    private Timer timer = new Timer();
    private double musicSec = 0;
    private Playlist playlist = new Playlist();
    private Set<Integer> skipList = new TreeSet<>();
    private SCComms sccoms_instance = null;


    public static void main(String[] args) {
        Room r = new Room(DEFAULTPORT);
        new Thread(r).start();
    }

    public void fillPlayList() {
        //new Converter("resources/batmobile.wav","resources/test1.mp3").encodeMP3();
        //new Converter("resources/little_mermaid_choices.wav","resources/mermaid.mp3").encodeMP3();

        //playlist.addRequestedUploadedTrack("batmobile.mp3", "Local");
        playlist.addRequestedUploadedTrack("mermaid.mp3", "Local");


        try {
            JSONObject track = (JSONObject) sccoms_instance.search_for_track("numb").get(0);
            SCTrack scTrack = new SCTrack("Local", track);
            playlist.addRequestedSCTrack(scTrack);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public Room() {
        this(0);
    }

    public Room(int port) {
        this.sccoms_instance = new SCComms();
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
        sendNewTrack(playlist.getCurrentTrack());
    }

    public void sendNewTrack(Track track) {
        if (track instanceof SCTrack) {
            System.out.println("SEND NEW TRACK (SCTRACK)");

            new Thread() {
                @Override
                public void run() {
                    sendAndRead(track);
                }
            }.start();}
        else {
            for (User user : clients)
                new Thread() {
                    @Override
                    public void run() {
                        user.sendFile(track, 0, false);
                    }
                }.start();
        }
    }

    public void sendActualTrack(User u) {
        new Thread() {
            @Override
            public void run() {
                // TODO: 21/05/2016 ficheiro
                if (playlist.getCurrentTrack() instanceof SCTrack) {
                    sendAndRead(playlist.getCurrentTrack());
                    //u.sendFile(playlist.getCurrentTrack(), 0, true);
                }
                else u.sendFile(playlist.getCurrentTrack(), musicSec, false);
            }
        }.start();
    }

    public void sendAndRead(Track track) {

        System.out.println("SEND 'N READ");

        InputStream is = sccoms_instance.getStreamData(sccoms_instance.get_stream_from_url(track.getStream_url()));

        int bytesRead = 0;
        byte[] buf = new byte[FRAMESIZE];
        try {
            while((bytesRead = is.read(buf)) != -1) {
                sem.acquire();
                for(User u:this.clients){
                    u.send(buf);
                }
                sem.release();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        System.out.println("Room started!");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                musicSec += 0.5;
                if (musicSec == playlist.getCurrentTrack().getInfo().getFullTime()) {
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


        while (true) {
            try {
                Socket connectionSocket = socket.accept();
                connectionSocket.setSendBufferSize(64000);
                sem.acquire();
                User c = new User(connectionSocket,this);
                new Thread(c).start();
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
