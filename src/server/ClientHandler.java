package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by diogo on 12/05/2016.
 */
public class ClientHandler {
    private DataOutputStream out;

    public ClientHandler(Socket s){
        try {
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] bytes){
        try {
            out.write(bytes, 0, Server.FRAMESIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
