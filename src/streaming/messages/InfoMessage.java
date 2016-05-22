package streaming.messages;

import player.Track;

/**
 * Created by Sonhs on 22/05/2016.
 */
public class InfoMessage extends Message {

    public InfoMessage(String clients, String playlist){
        super(Type.INFO_ROOM);
        this.arg = new String[2];
        this.arg[0]=clients; //clients
        this.arg[1]=playlist; //playlist
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.arg[0]);
        sb.append("\n\n");
        sb.append(this.arg[1]);
        return sb.toString();
    }
}
