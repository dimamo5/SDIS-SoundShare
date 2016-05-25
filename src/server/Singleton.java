package server;

import database.Database;
import soundcloud.SCComms;

/**
 * Created by duarte on 24-05-2016.
 */
public class Singleton {
    private static Singleton ourInstance = new Singleton();
    private SCComms soundCloudComms = new SCComms();
    private Database database= new Database();

    public static Singleton getInstance() {
        return ourInstance;
    }

    public SCComms getSoundCloudComms() {
        return soundCloudComms;
    }

    public void setSoundCloudComms(SCComms soundCloudComms) {
        this.soundCloudComms = soundCloudComms;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    private Singleton() {
    }
}
