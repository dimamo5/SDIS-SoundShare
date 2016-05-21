package util;

import soundcloud.SCComms;

/**
 * Created by duarte on 21-05-2016.
 */
public class ServerSingleton {
    private static ServerSingleton ourInstance = new ServerSingleton();

    public static ServerSingleton getInstance() {
        return ourInstance;
    }
    public SCComms soundCloudComms = new SCComms();
    private ServerSingleton() {
    }

    public SCComms getSoundCloudComms() {
        return soundCloudComms;
    }

    public void setSoundCloudComms(SCComms soundCloudComms) {
        this.soundCloudComms = soundCloudComms;
    }
}
