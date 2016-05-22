import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import streaming.messages.Message;
import streaming.Room;
import streaming.messages.RequestMessage;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static streaming.Room.FRAMESIZE;

/**
 * Created by diogo on 12/05/2016.
 */
public class Client  implements Runnable {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private InputStream streamIn;
    private OutputStream streamOut;
    private Socket communicationSocket;
    private Socket streamingSocket;
    private boolean playing=false;
    public boolean connected=false;
    public AdvancedPlayer player;
    private SSLSocketFactory sslsocketfactory = null;
    private SSLSocket sslsocket = null;
    private static String serverAddress;
    private static int serverPort;
    private final int sslPort = 9000;
    private String token;



    public static void main(String[] args){
        serverAddress = args[0];
        serverPort = new Integer(args[1]);

        try {
            Client client = new Client(InetAddress.getByName(serverAddress),serverPort);
            new Thread(client).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public Client(InetAddress serverAddress, int serverPort){
        try {
            connectToServer();
            System.out.println("token: " + token);
            communicationSocket = new Socket(serverAddress, serverPort);
            this.out = new ObjectOutputStream(communicationSocket.getOutputStream());
            this.in = new ObjectInputStream(communicationSocket.getInputStream());
            int streamingPort = 0;

            while(streamingPort == 0){
                Message message = (Message) in.readObject();
                if(message.getType().equals(Message.Type.STREAM)){
                    streamingPort = new Integer(message.getArg()[0]);
                    System.out.println("Streaming Port: "+streamingPort);
                    this.connected=true;
                }
            }
            this.streamingSocket = new Socket(serverAddress,streamingPort);
            requestSong("mama.wma", false);
            streamIn = streamingSocket.getInputStream();
            this.play();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void sendMessage(Message message) throws IOException {
        out.writeObject(message);
    }

    private Message getMessage(){
        try {
            return (Message) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean requestSong(String url, boolean isSoundCloud){
        DateFormat format = new SimpleDateFormat("hh:mm:ss");
        String dateMsg = format.format(new Date());
        if (isSoundCloud) {
            try {
                sendMessage(new RequestMessage(new String[]{url,dateMsg}, token, RequestMessage.RequestType.SOUNDCLOUD));
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        else {
            Message m = new RequestMessage(new String[]{url, dateMsg}, token, RequestMessage.RequestType.STREAM_SONG);
            System.out.println(m.toString());
            try {
                sendMessage(m);
                sendSong(url);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

    public boolean sendSong(String filename) {
        byte[] mybytearray = new byte[Room.FRAMESIZE];
        File f = new File(System.getProperty("user.dir") + "/" + filename);

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
        } catch (FileNotFoundException ex) {
            System.err.println("File for UploadedTrack " + filename + "not found");
        }

        int chunks = (int) f.length() / Room.FRAMESIZE;
        BufferedInputStream bis = new BufferedInputStream(fis);

        System.out.println("Tamanho ficheiro: " + f.length() + " Dividido em: " + f.length() / Room.FRAMESIZE);

        for (int m = 0; m < chunks; m++) {
            try {
                bis.read(mybytearray, 0, Room.FRAMESIZE);
                streamingSocket.getOutputStream().write(mybytearray, 0, FRAMESIZE);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public void skip(){
        try {
            sendMessage(new Message(Message.Type.VOTE_SKIP, token, new String[]{}));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play(){
        Client c= this;
        new Thread() {
            @Override
            public void run() {
                System.out.println("Playing!");
                try {
                    c.player=new AdvancedPlayer(c.streamIn);
                    c.player.play();
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void run() {
        while(connected){
            if(communicationSocket.isClosed()){
                connected=false;
                break;
            }

            try {
                final Message message = (Message) in.readObject();
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

    private void handleMessage(Message message) {
        switch(message.getType()){
            case MUSIC:
                System.out.println(message.toString());
                break;
            case TRUE:
                System.out.println(message.toString());
                //sendSong(message.getArg()[0]);
                break;
        }
    }

    public void connectToServer() {
        try {
            System.setProperty("javax.net.ssl.trustStore","keystore");
            System.setProperty("javax.net.ssl.trustStorePassword","123456");

            sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            sslsocket = (SSLSocket) sslsocketfactory.createSocket(serverAddress, sslPort);
            sslsocket.startHandshake();

            OutputStream outputstream = sslsocket.getOutputStream();
            String msg = "CONNECT " + "teste " + "lol";
            outputstream.write(msg.getBytes());

            InputStream inputStream = sslsocket.getInputStream();
            byte[] b = new byte[64];
            int bytesRead = inputStream.read(b);
            parseToken(b, bytesRead);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void parseToken(byte[] b, int bytesRead) {
        token = new String(b, 0, bytesRead);
        token = token.substring("CONNECT ".length(), token.length());
    }
}

