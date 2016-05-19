package streaming;

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
    private ServerSocket welcomeSocket;
    private final int listenPort = 5000;
    public static final int FRAMESIZE = 2048;
    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
    private Semaphore sem = new Semaphore(1);
    private Timer timer = new Timer();
    private double musicSec = 0;
    private Playlist playlist = new Playlist();

    public static void main(String[] args) {
        new Room();
    }

    public Room() {
        //new Converter("resources/batmobile.wav","resources/batmobile.mp3").encodeMP3();
        try {
            welcomeSocket = new ServerSocket(listenPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File myFile = new File(System.getProperty("user.dir") + "/resources/Mine.mp3");

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                musicSec+=0.5;
            }
        }, 500, 500);


        while (true) {
            try {
                Socket connectionSocket = welcomeSocket.accept();
                connectionSocket.setSendBufferSize(1000000);
                System.out.println("New Client");
                sem.acquire();
                ClientHandler c = new ClientHandler(connectionSocket);
                clients.add(c);
                new Thread() {
                    @Override
                    public void run() {
                        c.sendFile(myFile, musicSec);
                    }
                }.start();
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
