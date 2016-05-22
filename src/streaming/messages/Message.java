package streaming.messages;

import player.Track;
import player.UploadedTrack;

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
        LIST_ROOM,
        TRUE,
        FALSE
    }

    protected Type type;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    protected String token;
    protected String[] arg;

    public Message(Type type, String token, String[] arg) {
        this.type = type;
        this.token = token;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch(this.type) {
            case MUSIC:
                //Override na class MusicMessage.java
                break;
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
                sb.append(type.toString());
                sb.append(" " + token);
                sb.append(" Request for song ");
                sb.append(arg[0]);
                sb.append(" ");
                sb.append(arg[1]);
                return sb.toString();
            case FALSE:
            case TRUE:
                sb.append(type.toString());
                for(String argument : arg){
                    sb.append(" "+argument);
                }
                return sb.toString();
            default:
                break;
        }
        return null;
    }
}
