package streaming.messages;

/**
 * Created by duarte on 20-05-2016.
 */
public class RequestMessage extends Message {
    public enum RequestType{
        SOUNDCLOUD,
        STREAM_SONG
    }

    private RequestType requestType;

    public RequestMessage(String[] arg, RequestType requestType) {
        super(Message.Type.REQUEST, arg);
        this.requestType = requestType;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}
