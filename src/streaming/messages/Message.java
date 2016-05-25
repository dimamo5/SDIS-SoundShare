package streaming.messages;

import auth.Token;

import java.io.Serializable;

/**
 * Created by duarte on 14-05-2016.
 */
public class Message implements Serializable {
    public enum Type{
        DISCONNECT,
        CONNECT,
        INFO_ROOM,
        STREAM,
        VOTE_SKIP,
        REQUEST,
        TOKEN,
        VOTE_KICK,
        MUSIC,
        TRUE,
        FALSE,
        GET_ROOM_LIST,
        ONLY_TOKEN //é usado
    }

    protected Type type;
    protected Token token;
    protected String[] args;

    public Message(Type type, String... args){
        this.type = type;
        this.args = args;
    }

    public Message(Type type, Token token, String... args) {
        this.type = type;
        this.token = token;
        this.args = args;
    }

    public Message(){};

    public Message(Type type) {
        this.type = type;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
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
                sb.append(args[0]);
                sb.append(" ");
                sb.append(args[1]);
                return sb.toString();
            case FALSE:
                break;
            case TOKEN:

                break;
            case TRUE:
                sb.append(type.toString());
                for(String argument : args){
                    sb.append(" "+argument);
                }
                return sb.toString();

            default:
                break;
        }
        return null;
    }
}
