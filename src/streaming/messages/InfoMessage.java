package streaming.messages;

/**
 * Created by Sonhs on 22/05/2016.
 */
public class InfoMessage extends Message {

    public InfoMessage(String clients, String playlist) {
        super(Type.INFO_ROOM);
        this.args = new String[2];
        this.args[0] = clients; //clients
        this.args[1] = playlist; //playlist
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.args[0]);
        sb.append("\n\n");
        sb.append(this.args[1]);
        return sb.toString();
    }
}
