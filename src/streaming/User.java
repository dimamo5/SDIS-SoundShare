package streaming;

import player.InfoMusic;
import player.Track;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static streaming.Room.FRAMESIZE;

/**
 * Created by diogo on 12/05/2016.
 */
public class User implements Runnable{
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket communicationSocket;
    private Socket streamingSocket;
    private boolean connected = true;

    public User(Socket socket){
        this.communicationSocket = socket;

        // TODO: 19-05-2016 Verificar se ao criar o streaming socket desta maneira ele já atribuí um port para o client se ligar

        try {
            ServerSocket serverSocketStreaming = new ServerSocket(0);
            this.out = new ObjectOutputStream(this.communicationSocket.getOutputStream());
            this.in = new ObjectInputStream(this.communicationSocket.getInputStream());

            //Send message with the streaming port for the User to connect to in order to receive streaming data
            out.writeObject(new Message(Message.Type.STREAM, new String[]{serverSocketStreaming.getLocalPort()+""} ));
            this.streamingSocket = serverSocketStreaming.accept();
         } catch (IOException e) {
             e.printStackTrace();
         }
    }

    public void send(byte[] bytes){
        try {
            streamingSocket.getOutputStream().write(bytes, 0, FRAMESIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(Track track, double sec) {
        byte[] mybytearray = new byte[Room.FRAMESIZE];

        File f= track.getFile();
        InfoMusic info = new InfoMusic(f);
        info.getMusicInfo();
        int songTime = info.getFullTime();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
        } catch (FileNotFoundException ex) {
            System.err.println("File for Track " + track + "not found");
        }

        System.out.println("Enviar " + track.getTrackName()+ " - "+track.getAuthor() + " Duration: " + track.getFullTime());

        /*try {
            this.out.writeObject(new Message().createMusicMessage(track,sec));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        int chunks = (int) f.length() / Room.FRAMESIZE;
        BufferedInputStream bis = new BufferedInputStream(fis);
        double bytesperSec = (f.length()-4) / songTime;
        double frameToElapse = bytesperSec * sec / Room.FRAMESIZE;
        double frameToElapseRounded=Math.round(frameToElapse);

        //TODO ver tolerancia
        //long tolerance= (long) Math.abs((frameToElapse-frameToElapseRounded)/(bytesperSec/Server.FRAMESIZE)*1000);

        /*try {
            Thread.sleep(tolerance);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        System.out.println("Tamanho ficheiro: " + f.length() + " Dividido em: " + f.length() / Room.FRAMESIZE +
                " Bytes per sec: " + bytesperSec + " Frames passed: " + frameToElapse);


        for (int m = 0; m < chunks; m++) {
            try {
                bis.read(mybytearray, 0, Room.FRAMESIZE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (m >= frameToElapseRounded)
                this.send(mybytearray);
        }

        //TODO close socket somehow
        /*try {
            this.streamingSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void handleMessage(Message message){

    }

    @Override
    public void run() {
        while(connected){
            if(communicationSocket.isClosed()){
                setConnected(false);
                break;
            }

            try {
                final Message message = (Message) in.readObject();

                //ANTES ESTAVA ASSIM MAS O IDEA SUGERIU USAR LAMBDA. SE DER ERRO MUDAR PARA ISTO
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handleMessage(message);
                    }
                }).start();*/

                new Thread(() -> {
                    handleMessage(message);
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        try {
            streamingSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
