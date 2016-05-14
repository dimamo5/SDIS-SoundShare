import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javazoom.spi.mpeg.sampled.file.MpegEncoding;

import javax.sound.sampled.AudioFormat;

import javazoom.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by diogo on 12/05/2016.
 */
public class Client {
    private static Socket receiveSocket;
    private static final int listenPort = 5000;

    public static void main(String[] args) throws JavaLayerException {
        byte[] aByte = new byte[4655334];
        int bytesRead;

        Socket clientSocket = null;
        InputStream is = null;

        try {
            InetAddress destination = InetAddress.getByName("localhost");
            receiveSocket = new Socket(destination, listenPort);
            is = receiveSocket.getInputStream();
        } catch (IOException ex) {
            // Do exception handling
        }
        try {
            receiveSocket.setReceiveBufferSize(64000);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        //Define the format sampleRate, Sample Size in Bits, Channels (Mono), Signed, Big Endian
        AudioFormat format = new AudioFormat(MpegEncoding.MPEG1L1,(float)44100.0,16, 2,4,(float)44100.0,false);

        BufferedInputStream buf = new BufferedInputStream(is);
        Player mp3=new Player(is);


        if (is != null) {

            try {
                bytesRead = is.read(aByte, 0, 1);
                System.out.println(bytesRead + " "+ buf.available());
                mp3.play();
                /*
                do {;
                    bytesRead = is.read(aByte);
                    System.out.println(buf.available());
                    mp3.play();
                } while (bytesRead != -1);
*/
                clientSocket.close();
            } catch (IOException ex) {
                // Do exception handling
            }
        }
    }
}
