package Server;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by diogo on 10/05/2016.
 */
class Receiver {

    static AudioInputStream ais;
    static AudioFormat format;
    static boolean active = true;
    static int port = 50020;
    static DatagramSocket serverSocket, socket;
    static byte[] receiveData;
    static DatagramPacket receivePacket, packet;
    static ByteArrayInputStream bais;
    static int sampleRate = 8000;
    static int time = 10;

    static DataLine.Info dataLineInfo;
    static SourceDataLine sourceDataLine;

    public static void main(String args[]) throws Exception {
        socket = new DatagramSocket();
        InetAddress destination = InetAddress.getByName("localhost");
        byte[] temp = new byte[256];
        //putting buffer in the packet
        packet = new DatagramPacket(temp, temp.length, destination, 50010);

        socket.send(packet);

        //Define the Receiving Datagram Socket
        serverSocket = new DatagramSocket(port);

        //Define data size, 1400 is best sound rate so far
        receiveData = new byte[1400];
        //Define the format sampleRate, Sample Size in Bits, Channels (Mono), Signed, Big Endian
        format = new AudioFormat(sampleRate, 16, 1, true, false);
        //Define the DatagramPacket object
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        //Prepare the Byte Array Input Stream
        bais = new ByteArrayInputStream(receivePacket.getData());
        //Now concert the Byte Array into an Audio Input Stream
        ais = new AudioInputStream(bais, format, receivePacket.getLength());

        //Define DataLineInfo
        dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
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
        try {
            while (active) {
                System.out.println("Receiving");
                //Wait until packet is received
                serverSocket.receive(receivePacket);
                //Reset time
                time = 10;
                //Send data to speakers
                toSpeaker(receivePacket.getData());
            }
        } catch (IOException e) {
        }
    }

    /***
     * Function that plays the sound bytes with the speakers.
     * @param soundbytes = bytes of sound sent to speakers
     */
    public static void toSpeaker(byte soundbytes[]) {
        try {
            sourceDataLine.write(soundbytes, 0, soundbytes.length);
        } catch (Exception e) {
            System.out.println("Not working in speakers...");
            e.printStackTrace();
        }
    }
}