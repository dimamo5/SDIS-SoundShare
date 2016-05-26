package server;

import database.Database;
import soundcloud.SCComms;

/**
 * Created by duarte on 24-05-2016.
 */
public class Singleton {
    private static Singleton ourInstance = new Singleton();
    private SCComms soundCloudComms = new SCComms();
    private Database database = new Database();
    private Server server = new Server();

    public static Singleton getInstance() {
        return ourInstance;
    }

    public SCComms getSoundCloudComms() {
        return soundCloudComms;
    }


    public Database getDatabase() {
        return database;
    }

    public Server getServer() {
        return server;
    }

    void setServer(Server server) {
        this.server = server;
    }

    private Singleton() {
    }

}
