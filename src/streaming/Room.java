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
public class Room implements Runnable{
    private static ServerSocket welcomeSocket;
    private static final int listenPort = 5000;
    public static final int FRAMESIZE = 2048;
    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
    private Semaphore sem = new Semaphore(1);
    private Timer timer = new Timer();
    private double musicSec = 0;
    private Playlist playlist = new Playlist();

    public static void main(String[] args) {

    }

    /*public static void sendFile(File f) {
        byte[] mybytearray = new byte[FRAMESIZE];

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
        } catch (FileNotFoundException ex) {
            // Do exception handling
        }

        int chunks = (int) f.length() / FRAMESIZE;
        BufferedInputStream bis = new BufferedInputStream(fis);
        System.out.println("Tamanho ficheiro: " + f.length() + "Dividido em: " + f.length() / FRAMESIZE);
        System.out.println("Fram per sec: " + f.length() / songTime);

        for (int m = 0; m < chunks; m++) {
            try {
                sem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                bis.read(mybytearray, 0, FRAMESIZE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (ClientHandler client : clients) {
                client.send(mybytearray);
                System.out.println(musicSec);
            }
            sem.release();
        }

    }*/

    @Override
    public void run() {
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
