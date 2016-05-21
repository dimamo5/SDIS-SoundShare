package player;

import org.tritonus.share.midi.TMidiDevice;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by Sonhs on 21/05/2016.
 */
public abstract class Track {

    //private Date requestTimestamp = new Date();

    private String clientRequested;
    private boolean sent=false;

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


}
