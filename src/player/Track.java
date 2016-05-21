package player;

import java.io.InputStream;
import java.util.Date;

/**
 * Created by Sonhs on 21/05/2016.
 */
public class Track {

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

    public void setSent(boolean sent) {
        this.sent = sent;
    }


}
