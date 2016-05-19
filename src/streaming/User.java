package streaming;

import player.InfoMusic;

import java.io.*;
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
        this.streamingSocket = new Socket();
        try {
             this.out = new ObjectOutputStream(this.communicationSocket.getOutputStream());
             this.in = new ObjectInputStream(this.communicationSocket.getInputStream());

            //Send message with the streaming port for the User to connect to in order to receive streaming data
            out.writeObject(new Message(Message.Type.STREAM, new String[]{this.streamingSocket.getPort()+""} ));
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

    public void sendFile(File f, double sec) {
        System.out.println("Envia ficheiro");
        byte[] mybytearray = new byte[Room.FRAMESIZE];

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
        } catch (FileNotFoundException ex) {
            // Do exception handling
        }

        InfoMusic info = new InfoMusic(f);
        info.getMusicInfo();
        int songTime = info.getFullTime();
        System.out.println("Full Time: " + info.getFullTime());

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
