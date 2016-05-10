package Server;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

/**
 * Created by diogo on 10/05/2016.
 */
public class Room {

    static int port = 50005;
    static int listen = 50010;
    static int listenerPort = 50015;
    static DatagramSocket serverSocket, listenSocket,
            broadcastSocket;
    static byte[] receiveData, listenData;
    static DatagramPacket receivePacket, listenPacket;
    static DataOutputStream out;
    static ArrayList<String> listeners = new ArrayList<String>();
    static ArrayList<Integer> ports =new ArrayList<Integer>();
    static File file1 = new File(System.getProperty("user.dir") + "/resources/recording.bin");
    static File forestGump = new File(System.getProperty("user.dir") + "/resources/batmobile.wav");
    static boolean active = true;
    static Semaphore mutex = new Semaphore(1);

    public static void main(String args[]) throws Exception {
        //Define the Receiving Datagram Socket
        serverSocket = new DatagramSocket(port);
        //Define the Timeout of the socket
        serverSocket.setSoTimeout(10000);
        //Define the listening socket
        listenSocket = new DatagramSocket(listen);
        listenSocket.setSoTimeout(10000);
        //Define Broadcasting socket
        broadcastSocket = new DatagramSocket();

        //Define data size, 1400 is best sound rate so far
        receiveData = new byte[30000];
        listenData = new byte[256];

        //Define the DatagramPacket object
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        listenPacket = new DatagramPacket(listenData, listenData.length);

        //Prepare the DataOutputStream to write to file.
        //out = new DataOutputStream(new FileOutputStream(forestGump));
        //Write and Broadcast on a separate thread
        /*Thread t = new Thread() {
            @Override
            public void run() {
                getPackets();
            }
        };
        t.start();*/

        //Set up Connection Listener on a separate thread
        Thread l = new Thread() {
            @Override
            public void run() {
                listen();
            }
        };
        l.start();

        AudioInputStream audioInputStream =
                AudioSystem.getAudioInputStream(forestGump);

        System.out.println("Format: " + audioInputStream.getFormat() + audioInputStream.getFormat().getFrameSize() + audioInputStream.getFormat().getSampleRate());

        long numChunk=(forestGump.length()/64000) +1;
        System.out.println("File will be splited in "+numChunk);

        FileInputStream fs = new FileInputStream(forestGump);
        for(int i =0; i<numChunk;i++){
            byte buffer[] = new byte[64000];
            int numBytesRead=fs.read(buffer);
            if(numBytesRead<0){
                numBytesRead=0;
            }
            byte[] newBuffer= Arrays.copyOfRange(buffer,0,numBytesRead);
            sendData(newBuffer);
            Thread.sleep(100);
            System.out.println("nr" + i);
        }
    }

    /***
     * Function that gets the audio data packets
     * saves them, and outputs the audio to the speakers.
     */
    public static void getPackets() {
        while (active) {
            try {
                //Wait until packet is received
                serverSocket.receive(receivePacket);
                System.out.println("Receiving Data");
                //Write to Binary file
                out.write(receiveData, 0, receiveData.length);
                //Send data
                sendData(receivePacket.getData());
            } catch (IOException e) {
                active = false;
                //If connection times out close it
                try {
                    out.close();
                } catch (IOException t) {
                    //Do nothing
                }
                System.out.println("Converting to audio");
                //Convert audio file
                //new Convert().toWAV();
            }
        }
    }

    /***
     * Function that listens if there are any connections made to
     * the listener port and creates a datagram socket to stream audio
     * to anyone who connects
     */
    public static void listen() {
        while (active) {
            try {
                //Wait until packet is received
                listenSocket.receive(listenPacket);
                System.out.println("antes:" +listeners.size());
                mutex.acquire();
                listeners.add(listenPacket.getAddress().getHostAddress());
                System.out.println("depois" + listeners.size());
                int port = Integer.valueOf(new String(listenPacket.getData()).trim());
                ports.add(port);
                mutex.release();
                System.out.println("Client received in port: " + port);


            } catch (IOException e) {
                if (active) {
                    listen();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendData(byte[] data) {
        System.out.println("tamanho listeners"+ listeners.size());
        while(listeners.size()==0) {
            System.out.print("");
        }


        try {
            mutex.acquire();
            for (int i = 0; i < listeners.size(); i++) {
                InetAddress destination = InetAddress.getByName(listeners.get(i));
                broadcastSocket.send(new DatagramPacket(data, data.length, destination, ports.get(i)));
                System.out.println("Sending Data with size: " + data.length);
            }
            mutex.release();
        } catch (Exception e) {
            //If it failed to send don't do anything
            e.printStackTrace();
        }
    }
}
