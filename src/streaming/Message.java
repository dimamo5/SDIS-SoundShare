package streaming;

/**
 * Created by duarte on 14-05-2016.
 */
public class Message {
    public enum Type{
        DISCONNECT,
        CONNECT,
        STREAM,
        VOTE_SKIP,
        REQUEST,
        VOTE_KICK,
        TRUE,
        FALSE
    }

    private Type type;
    private String[] arg;

    public Message(Type type, String arg[]) {
        this.type = type;
        this.arg = arg;
    }

    public Message(Type type) {
        this.type = type;
    }
}