package player;

import streaming.Room;
import streaming.ClientHandler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Sonhs on 21/05/2016.
 */
public abstract class Track {

    //private Date requestTimestamp = new Date();
    protected InfoMusic info;
    private String clientRequested;
    private boolean sent=false;
    private InputStream stream;

    public Track(String clientRequested){
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

    abstract public void sendTrack(double sec, Room room);

    protected void sendTrackFromStream(Room room, BufferedInputStream stream, double frameToElapseRounded) {
        ArrayList<ClientHandler> clients = room.getClients();
        byte[] buf = new byte[Room.FRAMESIZE];
        int i = 0;
        try {
            while(stream.read(buf,0,buf.length) != -1){
                i++;
                if (i >= frameToElapseRounded)
                    try {
                        room.clientsSemaphore.acquire();
                        for(ClientHandler client : clients){
                            client.send(buf);
                        }
                        room.clientsSemaphore.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                buf = new byte[Room.FRAMESIZE];
            }
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
