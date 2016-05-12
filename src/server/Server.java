package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Created by diogo on 12/05/2016.
 */
public class Server {
    private static ServerSocket welcomeSocket;
    private static final int listenPort = 5000;
    public static final int FRAMESIZE = 2048;
    private static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
    private static Semaphore sem = new Semaphore(1);

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

        while (true) {
            Socket connectionSocket = null;
            try {
                connectionSocket = welcomeSocket.accept();
                sem.acquire();
                clients.add(new ClientHandler(connectionSocket));
                sem.release();
                System.out.println("New Client");
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

        try {
            sem.acquire(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int m = 0; m < chunks; m++) {
            for (ClientHandler client : clients) {
                try {
                    bis.read(mybytearray, 0, FRAMESIZE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                client.send(mybytearray);
            }
        }

        sem.release();
    }

}
