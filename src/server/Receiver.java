package server;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.*;

/**
 * Created by diogo on 10/05/2016.
 */
class Receiver {

    static AudioInputStream ais;
    static AudioFormat format;
    static boolean active = true;
    static int port = 50015;
    static DatagramSocket serverSocket, socket;
    static byte[] receiveData;
    static DatagramPacket receivePacket, packet;
    static ByteArrayInputStream bais;
    static int sampleRate = 8000;
    static int time = 10;

    static DataLine.Info dataLineInfo;
    static SourceDataLine sourceDataLine;

    public static void main(String args[]) throws Exception {
        //TODO Send sound to server
        socket = new DatagramSocket();
        InetAddress destination = InetAddress.getByName("localhost");
        byte[] temp = args[0].getBytes();
        //putting buffer in the packet
        packet = new DatagramPacket(temp, temp.length, destination,50010);

        socket.send(packet);


        //Define the Receiving Datagram Socket
        serverSocket = new DatagramSocket(Integer.parseInt(args[0]));

        //Define data size, 1400 is best sound rate so far
        receiveData = new byte[2048];
        //Define the format sampleRate, Sample Size in Bits, Channels (Mono), Signed, Big Endian
        format = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED,(float)11025.0,8, 1,1,(float)11025.0,false);
        //Define the DatagramPacket object
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        //Prepare the Byte Array Input Stream
        bais = new ByteArrayInputStream(receivePacket.getData());
        //Now concert the Byte Array into an Audio Input Stream
        ais = new AudioInputStream(bais, format, receivePacket.getLength());

        //Define DataLineInfo
        dataLineInfo = new DataLine.Info(SourceDataLine.class, format,1000000);
        //Buffer size
        System.out.println("Buffer Size: " + dataLineInfo.getMaxBufferSize());

        System.out.println(serverSocket.getSoTimeout() +" "+ serverSocket.getSendBufferSize() + " " + serverSocket.getReceiveBufferSize());
        //Get the current Audio Line from the system
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
        //Open up sourceDataLine and Start it
        sourceDataLine.open(format);
        sourceDataLine.start();

        //Write and play on a separate thread
        Thread t = new Thread() {
            @Override
            public void run() {
                getPackets();
            }
        };
        t.start();
        //Now keep track of time
        while (time > 0) {
            time--;
            Thread.sleep(1000);
            if (time == 0) {
                active = false;
            }
        }
        //Close SourceDataLine
        sourceDataLine.drain();
        sourceDataLine.close();
    }

    /***
     * Function that gets the audio data packets
     * saves them, and outputs the audio to the speakers.
     */
    public static void getPackets() {
        int count=0;

        try {
            while (active) {
                System.out.println("Receiving");
                DatagramPacket rec = new DatagramPacket(receiveData,receiveData.length);
                //Wait until packet is received
                serverSocket.receive(rec);
                //Reset time
                time = 10;
                //Send data to speakers
                //toSpeaker(rec.getData());
                //RandomAccessFile fs = new RandomAccessFile("Ola.wav","rw");
                //fs.seek(count*2048);
                //fs.write(rec.getData(),0,rec.getLength());
                //fs.close();
                count++;
                System.out.println("atamha"+ rec.getLength() + "it:" + count);
            }
        }
        catch (IOException e) {
        }
    }

    /***
     * Function that plays the sound bytes with the speakers.
     * @param soundbytes = bytes of sound sent to speakers
     */
    public static void toSpeaker(byte soundbytes[]) {
        try {
            sourceDataLine.write(soundbytes, 0, soundbytes.length);
            System.out.println("recebendo");
        } catch (Exception e) {
            System.out.println("Not working in speakers...");
            e.printStackTrace();
        }
    }
}