package player;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by duarte on 14-05-2016.
 */
public class Track extends InfoMusic{
    private String clientRequested;
    private Date requestTimestamp = new Date();
    private boolean sent=false;

    public Track(File file, String clientRequested) {
        super(file);
        this.clientRequested = clientRequested;
    }

    public Track(String filename, String clientRequested) {
        super(filename);
        getMusicInfo();
        this.clientRequested = clientRequested;
    }

    public String getTrackName() {
        return getTitle();
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

    public void setSent(boolean sent) {
        this.sent = sent;
    }


}
