package player;

import java.io.File;
import java.io.InputStream;

/**
 * Created by Sonhs on 21/05/2016.
 */
public abstract class Track {

    //private Date requestTimestamp = new Date();

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

    abstract public InfoMusic getInfo();

    abstract public File getFile();

    abstract public String getStream_url();

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
