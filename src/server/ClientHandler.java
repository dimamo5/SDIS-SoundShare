package server;

import java.io.*;
import java.net.Socket;

/**
 * Created by diogo on 12/05/2016.
 */
public class ClientHandler {
    private DataOutputStream out;

    public ClientHandler(Socket s){
        try {
            this.out = new DataOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] bytes){
        try {
            out.write(bytes, 0, Server.FRAMESIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(File f,int sec) {
        byte[] mybytearray = new byte[Server.FRAMESIZE];

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
        } catch (FileNotFoundException ex) {
            // Do exception handling
        }

        InfoMusic info = new InfoMusic("resources/renegades.mp3");
        info.getMusicInfo();
        int songTime=info.getFullTime();
        System.out.println("Full Time: " + info.getFullTime());

        int chunks = (int) f.length() / Server.FRAMESIZE;
        BufferedInputStream bis = new BufferedInputStream(fis);
        System.out.println("Tamanho ficheiro: " + f.length() + "Dividido em: " + f.length() / Server.FRAMESIZE);
        int bytesperSec=(int) f.length()/songTime;

        int frameToElapse=Math.round(bytesperSec*sec/Server.FRAMESIZE);

        for (int m = 0; m < chunks; m++){
            try {
                bis.read(mybytearray, 0, Server.FRAMESIZE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(m>=frameToElapse)
                this.send(mybytearray);
        }

    }


}
