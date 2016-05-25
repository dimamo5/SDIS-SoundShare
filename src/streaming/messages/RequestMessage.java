package streaming.messages;

import auth.Token;

/**
 * Created by duarte on 20-05-2016.
 */
public class RequestMessage extends Message {
    public enum RequestType{
        SOUNDCLOUD,
        STREAM_SONG
    }

    private RequestType requestType;

    public RequestMessage(RequestType requestType, Token token, String... arg) {
        super(Message.Type.REQUEST, token, arg);
        this.requestType = requestType;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}
