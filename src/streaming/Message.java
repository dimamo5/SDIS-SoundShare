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
        TRUE,
        FALSE
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
}
