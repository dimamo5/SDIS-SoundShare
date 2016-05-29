package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by m_bot on 28/05/2016.
 */
public class ServerManager {

    private final int CHECK_TIME = 120000; //2 mins
    private Timer time = new Timer();
    private final String MSG = "U THERE";
    private int port;
    private String IP;
    private DatagramSocket channel;
    private DatagramPacket pack;

    public static void main(String[] args) {
        new ServerManager(args[0], args[1]);
    }

    public ServerManager(String ip, String port) {
        IP = ip;
        this.port = Integer.parseInt(port);
        try {
            channel = new DatagramSocket();
            pack =  new DatagramPacket(MSG.getBytes(), MSG.getBytes().length, InetAddress.getByName(IP), this.port);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        time.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!pingServer()) {
                    ProcessBuilder pb = new ProcessBuilder("cmd.exe","/c","java -cp out\\production\\SDIS-SoundShare;lib\\commons-lang3-3.4.jar;lib\\java-api-wrapper-1.2.0-all.jar;lib\\jave-1.0.2.jar;lib\\jl1.0.1.jar;lib\\mp3spi1.9.5.jar;lib\\sqlite-jdbc-3.8.11.2.jar;lib\\tritonus_share.jar server.Server");
                    pb.redirectOutput();
                    try {
                        Process p = pb.start();
                        BufferedReader reader =
                                new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String line = "";
                        while ((line = reader.readLine())!= null) {
                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, CHECK_TIME); //checkar de 2 em 2 mins
    }

    private boolean pingServer() {
        System.out.print("Pinging server with: ");
        try {
            channel.setSoTimeout(10);
            channel.send(pack);
            byte[] b = new byte[256];
            channel.receive(new DatagramPacket(b, 256));
            System.out.println(new String(b));

        } catch (SocketTimeoutException s) {
            System.out.println("Server didn't respond. Starting new Server.");
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
