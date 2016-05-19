package streaming;

import player.Track;

import java.io.Serializable;

/**
 * Created by duarte on 14-05-2016.
 */
public class Message implements Serializable {
    public enum Type{
        DISCONNECT,
        CONNECT,
        STREAM,
        VOTE_SKIP,
        REQUEST,
        VOTE_KICK,
        MUSIC,
        AYY_CAPTAIN,
        FUCK_NO
    }

    private Type type;
    private String[] arg;

    public Message(Type type, String arg[]) {
        this.type = type;
        this.arg = arg;
    }

    public Message(){};

    public Message(Type type) {
        this.type = type;
    }

    public String[] getArg() {
        return arg;
    }

    public void setArg(String[] arg) {
        this.arg = arg;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void createMusicMessage(Track track, double sec){
        this.type=Type.MUSIC;
        this.arg=new String[4];
        this.arg[0]= track.getTrackName();
        this.arg[1]= track.getAuthor();
        this.arg[2]=String.valueOf(track.getFullTime());
        this.arg[3]=String.valueOf(sec);
    }

    @Override
    public String toString() {

        switch(this.type) {
            case MUSIC:
                StringBuilder sb = new StringBuilder();
                sb.append(type.toString());
                sb.append(" Currently playing music ");
                sb.append(arg[0]);
                sb.append(" from ");
                sb.append(arg[1]);
                sb.append(" with duration ");
                sb.append(arg[2]);
                sb.append(" seconds.");
                return sb.toString();
            case STREAM:
                break;
            case DISCONNECT:
                break;
            case CONNECT:
                break;
            case VOTE_SKIP:
                break;
            case VOTE_KICK:
                break;
            case REQUEST:
                break;
            case AYY_CAPTAIN:
                break;
            case FUCK_NO:
                break;
        }
        return null;
    }
}
