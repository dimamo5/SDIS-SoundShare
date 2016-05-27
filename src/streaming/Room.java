package streaming;

import org.json.JSONException;
import player.Converter;
import player.Playlist;
import player.SCTrack;
import player.Track;
import soundcloud.TrackGetter;
import streaming.messages.InfoMessage;
import streaming.messages.Message;
import streaming.messages.MusicMessage;
import server.Singleton;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Created by diogo on 12/05/2016.
 */
public class Room implements Runnable {
    public static final int DEFAULTPORT = 5000;
    public static final int FRAMESIZE = 2048;
    private static final int MAX_NUM_SKIP_VOTES = 5;

    private ServerSocket socket;
    private int port = 0;
    private ArrayList<ClientHandler> clients = new ArrayList<>();
    private Map<String, String> clientList = new HashMap<>();
    public Semaphore clientsSemaphore = new Semaphore(1);
    private Timer timer = new Timer();
    private double musicSec = 0;
    private Playlist playlist = new Playlist();
    private Set<Integer> skipList = new TreeSet<>();

    private TrackGetter trackGetter = new TrackGetter(Singleton.getInstance().getSoundCloudComms());

    public Playlist getPlaylist() {
        return playlist;
    }

    public static void main(String[] args) {
        Room r = new Room(DEFAULTPORT);
        new Thread(r).start();
    }

    public void fillPlayList() {
        new Converter("resources/batmobile.wav", "resources/batmobile.mp3").encodeMP3();
        //new Converter("resources/little_mermaid_choices.wav","resources/mermaid.mp3").encodeMP3();

        //playlist.addRequestedUploadedTrack("batmobile.mp3", "Local");
        playlist.addRequestedUploadedTrack("batmobile.mp3", "Local");
        //playlist.addRequestedUploadedTrack("renegades.mp3", "Local");
        //playlist.addRequestedUploadedTrack("renegades.mp3", "Local");

        try {
            SCTrack scTrack = trackGetter.getTrackByName("numb", "client01");
            playlist.addRequestedTrack(scTrack);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public Room() {
        this(0);
    }

    public Room(int port) {
        //this.fillPlayList();

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

    public TrackGetter getTrackGetter() {
        return trackGetter;
    }

    public void setPort(int port) {
        this.port = port;
        try {
            this.socket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void voteSkip(int user) {
        skipList.add(user);
        if (skipList.size() >= MAX_NUM_SKIP_VOTES) {
            try {
                skipTrack();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void skipTrack() throws InterruptedException {
        skipList.clear();
        playlist.skipTrack();
        sendSkipMessage();
        Thread.sleep(2000);
        sendNewTrack(playlist.getCurrentTrack());
    }

    public void sendSkipMessage() {
        Message message = new Message(Message.Type.SKIP);
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void sendNewTrack(Track track) {
        System.out.println("new");
        // ENVIAR TODA A GENTE
        final Room room = this;
        for (ClientHandler clientHandler : clients)
            new Thread() {
                @Override
                public void run() {
                    if (track != null) {
                        track.sendTrack(0, room, clientHandler);
                    }
                }
            }.start();
    }

    public void sendActualTrack(ClientHandler u) {
        System.out.println("actual");
        // ENVIAR SO UM HANDLER
        final Room room = this;
        new Thread() {
            @Override
            public void run() {
                playlist.getCurrentTrack().sendTrack(musicSec, room, u);
            }
        }.start();
    }

    public void sendNewTrackMessageToAllClients(Track track, double sec, ClientHandler c) {
            sendMusicMessage(c, track, sec);
    }


    public void sendRoomInfoMessage(ClientHandler user, String clients, String playlist) {
        InfoMessage message = new InfoMessage(clients, playlist);
        user.sendMessage(message);
    }

    public void sendMusicMessage(ClientHandler user, Track track, double sec) {
        MusicMessage message = new MusicMessage(track, sec);
        user.sendMessage(message);
    }

    public ArrayList<ClientHandler> getClients() {
        return clients;
    }

    public void setClients(ArrayList<ClientHandler> clients) {
        this.clients = clients;
    }

    public void startTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                musicSec += 0.5;
                Track currentTrack = playlist.getCurrentTrack();

                if (currentTrack == null) {
                    return;
                }else
                    System.out.println(currentTrack.isSent());

                Track t1 = playlist.getNextTrack();


                // ELEE CORRERA RINAM MUSICA 15 secs TODO
                if (musicSec >= currentTrack.getInfo().getFullTime()&& currentTrack.isSent()) {
                        // DIZER A PLAYLIST QUE ESTMAMOS NA PROXIMA MUSICA
                        if (playlist.skipTrack())
                            musicSec = 0;
                } else if ((t1 != null) && ((musicSec / playlist.getCurrentTrack().getInfo().getFullTime() >= 0.9) && (!t1.isSent()))) {
                    System.out.println("new song 0.9");
                    // ENVIAR PROXIMA TRACK
                    t1.setSent(true);
                    sendNewTrack(playlist.getNextTrack());
                } else if (!currentTrack.isSent()) { // INICIO DA PLAYLIST QUANDO ESTA VAZIA
                    System.out.println("new song");
                    try {
                        clientsSemaphore.acquire();
                        musicSec = 0;
                        for (int j = 0; j < clients.size(); j++) {
                            sendActualTrack(clients.get(j));
                        }
                        clientsSemaphore.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 500, 500);
    }

    public void removeClientFromList(ClientHandler clientHandler) {
        clientList.remove(clientHandler.getToken());
        clients.remove(clientHandler);
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
                ClientHandler c = new ClientHandler(connectionSocket, this);
                new Thread(c).start();

                clients.add(c);
                this.clientList.put(c.getToken().getToken(), c.getUsername());
                sendRoomInfoMessage(c, getClientListString(), getPlaylist().getPlaylistString());

                Track t = this.playlist.getCurrentTrack();
                if (t != null) {
                    sendMusicMessage(c, t, musicSec);
                    sendActualTrack(c);
                }
                clientsSemaphore.release();
                if (clients.size() == 1) {
                    musicSec = 0;
                }


            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String getClientListString() {
        StringBuilder sb;
        String clients_list;
        sb = new StringBuilder();
        int i = 0;
        for (Map.Entry<String, String> entry : this.clientList.entrySet()) {
            i++;
            sb.append(i);
            sb.append(": ");
            sb.append(entry);
            sb.append("\n");
        }
        clients_list = sb.toString();
        return clients_list;
    }
}
