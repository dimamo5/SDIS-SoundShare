package player;

import streaming.Room;
import streaming.ClientHandler;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Sonhs on 21/05/2016.
 */
public abstract class Track {

    //private Date requestTimestamp = new Date();
    protected InfoMusic info;
    private String clientRequested;
    private boolean sent = false;
    private InputStream stream;
    private File f;

    public Track(String clientRequested) {
        this.clientRequested = clientRequested;
    }

    public String getClientRequested() {
        return clientRequested;
    }

    public void setClientRequested(String clientRequested) {
        this.clientRequested = clientRequested;
    }

    public boolean isSent() {
        return sent;
    }

    abstract public String getTrackName();

    public InfoMusic getInfo() {
        return info;
    }

    public void setInfo(InfoMusic info) {
        this.info = info;
    }

    abstract public String getStream_url();

    abstract public void sendTrack(double sec, Room room, ClientHandler c);

    protected void sendTrackFromStream(Room room, BufferedInputStream stream, int chunks, double frameToElapseRounded, boolean isSoundCloud, ClientHandler c) {
        ArrayList<ClientHandler> clients = room.getClients();
        byte[] buf = new byte[Room.FRAMESIZE];

        OutputStream outputStream = null;
        if (isSoundCloud) {
            f = new File(System.getProperty("user.dir") + "/resources/soundcloud/" + info.getTrackName() + ".mp3");
            try {
                outputStream = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        try {
            System.out.println("ENVIAR TRACKAS NISDN");

            for (int i = 0; i < chunks; i++) {
                stream.read(buf, 0, Room.FRAMESIZE);

                if (i >= frameToElapseRounded)
                    c.send(buf);
            }

            if (isSoundCloud)
                outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    abstract public long getBytesPerSec();

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }
}
