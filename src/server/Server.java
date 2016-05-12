package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by diogo on 12/05/2016.
 */
public class Server {
    private static ServerSocket welcomeSocket;
    private static final int listenPort=5000;

    public static void main(String[] args) {
        try {
            welcomeSocket= new ServerSocket(listenPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            Socket connectionSocket = null;
            try {
                connectionSocket = welcomeSocket.accept();
                System.out.println("New Client");
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                File myFile = new File( System.getProperty("user.dir") + "/resources/renegades.mp3" );
                byte[] mybytearray = new byte[(int) myFile.length()];

                FileInputStream fis = null;

                try {
                    fis = new FileInputStream(myFile);
                } catch (FileNotFoundException ex) {
                    // Do exception handling
                }

                BufferedInputStream bis = new BufferedInputStream(fis);
                System.out.println("tamanho ficheiro: "+ myFile.length());
                bis.read(mybytearray, 0, mybytearray.length);
                outToClient.write(mybytearray, 0, mybytearray.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
