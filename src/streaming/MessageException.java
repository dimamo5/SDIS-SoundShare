package streaming;


/**
 * Created by duarte on 19-05-2016.
 */
public class MessageException extends Exception {
    public MessageException(String message){
        super(message);
    }

    @Override
    public void printStackTrace() {
        System.out.println(getMessage());
    }
}
