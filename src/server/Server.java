package server;

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
public class Server {
    private static ServerSocket welcomeSocket;
    private static final int listenPort = 5000;
    public static final int FRAMESIZE = 2048;
    private static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
    private static Semaphore sem = new Semaphore(1);
    private static Timer timer = new Timer();
    private static int musicSec=0;

    public static void main(String[] args) {
        try {
            welcomeSocket = new ServerSocket(listenPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File myFile = new File(System.getProperty("user.dir") + "/resources/renegades.mp3");

        Thread t = new Thread() {
            @Override
            public void run() {
                sendFile(myFile);
            }
        };

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                musicSec++;
            }
        },1000,1000);


        while (true) {
            try {
                Socket connectionSocket = welcomeSocket.accept();
                connectionSocket.setSendBufferSize(1000000);
                System.out.println("New Client");
                sem.acquire();
                clients.add(new ClientHandler(connectionSocket));
                sem.release();
                if (!t.isAlive())
                    t.start();


            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void sendFile(File f) {
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

    }

}
