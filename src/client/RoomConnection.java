package client;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import streaming.messages.Message;
import streaming.Room;
import streaming.messages.RequestMessage;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static streaming.Room.FRAMESIZE;

/**
 * Created by diogo on 12/05/2016.
 */
public class RoomConnection implements Runnable {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private InputStream streamIn;
    private OutputStream streamOut;
    private Socket communicationSocket;
    private Socket streamingSocket;
    private boolean playing = false;
    public boolean connected = false;
    public AdvancedPlayer player;


    private InetAddress serverAddress;
    private int roomPort;

    public boolean dcFromRoom() {
        try {
            if (player != null)
                player.close();
            communicationSocket.close();
            streamingSocket.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        String serverAddress = args[0];
        int roomPort = new Integer(args[1]);

        try {
            RoomConnection client = new RoomConnection(InetAddress.getByName(serverAddress), roomPort);
            new Thread(client).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public RoomConnection(InetAddress serverAddress, int roomPort) {
        try {
            this.serverAddress = serverAddress;
            this.roomPort = roomPort;

            communicationSocket = new Socket(serverAddress, roomPort);
            this.out = new ObjectOutputStream(communicationSocket.getOutputStream());
            this.in = new ObjectInputStream(communicationSocket.getInputStream());

            //envio de mensagem com o token (PROTOCOLO)
            this.out.writeObject(new Message(Message.Type.ONLY_TOKEN, Client.getInstance().getToken(), null));

            int streamingPort = 0;
            //receive streaming port
            while (streamingPort == 0) {
                Message message = (Message) in.readObject();
                if (message.getType().equals(Message.Type.STREAM)) {
                    streamingPort = new Integer(message.getArgs()[0]);
                    System.out.println("Streaming Port: " + streamingPort);
                    this.connected = true;
                }
            }
            this.streamingSocket = new Socket(serverAddress, streamingPort);
            //requestSong("mama.wma", false);
            streamIn = streamingSocket.getInputStream();
            streamOut = streamingSocket.getOutputStream();
            this.play();
        } catch (ConnectException c) {
            connected = false;
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void sendMessage(Message message) throws IOException {
        out.writeObject(message);
    }

    private Message getMessage() {
        try {
            return (Message) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean requestSong(String url, boolean isSoundCloud) {
        DateFormat format = new SimpleDateFormat("hh:mm:ss");
        String dateMsg = format.format(new Date());
        if (isSoundCloud) {
            try {
                sendMessage(new RequestMessage(RequestMessage.RequestType.SOUNDCLOUD, Client.getInstance().getToken(), new String[]{url, dateMsg}));
                return true;
            } catch (IOException e) {
                return false;
            }
        } else {
            Message m = new RequestMessage(RequestMessage.RequestType.STREAM_SONG, Client.getInstance().getToken(), new String[]{url, dateMsg});
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
        DataOutputStream dos = new DataOutputStream(streamOut);
        try {
            dos.writeShort(chunks);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Tamanho ficheiro: " + f.length() + " Dividido em: " + f.length() / Room.FRAMESIZE);

        for (int m = 0; m < chunks; m++) {
            try {
                bis.read(mybytearray, 0, Room.FRAMESIZE);
                dos.write(mybytearray, 0, Room.FRAMESIZE);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        try {
            if (fis != null) {
                bis.close();
                System.out.println("fechou");
                fis.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void skip() {
        try {
            sendMessage(new Message(Message.Type.VOTE_SKIP, Client.getInstance().getToken(), new String[]{}));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        final RoomConnection c = this;
        new Thread() {
            @Override
            public void run() {
                try {
                    c.player = new AdvancedPlayer(c.streamIn);
                    c.player.play();
                }
                catch (java.lang.ArrayIndexOutOfBoundsException a) {
                    System.out.println("bug do 580");
                    //a.printStackTrace();
                    c.play();
                }
                catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void run() {
        while (connected) {
            if (communicationSocket.isClosed()) {
                connected = false;
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
        switch (message.getType()) {
            case MUSIC:
                System.out.println(message.toString());
                Client.getInstance().getClInterface().setCommandNext();
                Client.getInstance().getClInterface().run();
                break;
            case TRUE:
                System.out.println(message.toString());
                Client.getInstance().getClInterface().setCommandNext();
                Client.getInstance().getClInterface().run();
                break;
            case SKIP:
                System.out.println("SKIP");
                Client.getInstance().getClInterface().setCommandNext();
                Client.getInstance().getClInterface().run();
                try {
                    streamIn.skip(streamIn.available());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


}

