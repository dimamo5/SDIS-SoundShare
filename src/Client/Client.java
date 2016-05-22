package Client;

import soundcloud.SCComms;

/**
 * Created by duarte on 21-05-2016.
 */
public class Client {

    private static Client ourInstance = new Client();

    private ServerConnection sv_connection;
    private RoomConnection rooom_connection;

    public SCComms soundCloudComms = new SCComms();


    private Client() {

        // TODO: 23/05/2016 maybe put the below calls in a method ?

        //this.sv_connection = new ServerConnection();

        //this.rooom_connection = new RoomConnection();

        //user input loop
        //can disconnect from room and connect to another one


    }

    public static Client getInstance() {
        return ourInstance;
    }
    public SCComms getSoundCloudComms() {
        return soundCloudComms;
    }

    public void setSoundCloudComms(SCComms soundCloudComms) {
        this.soundCloudComms = soundCloudComms;
    }


}
