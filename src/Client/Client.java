package Client;

import soundcloud.SCComms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by duarte on 21-05-2016.
 */
public class Client {

    private static Client ourInstance = new Client();

    private ServerConnection sv_connection;
    private RoomConnection rooom_connection;

    public SCComms soundCloudComms = new SCComms();
    private boolean stopLoop = false;


    private Client() {}

    public static void main(String[] args) {

        String address = args[0];
        int sv_port = new Integer(args[1]);

        Client cli = Client.getInstance();
        cli.run(address,sv_port);
    }

    private void run(String sv_address, int sv_port){

        this.sv_connection = new ServerConnection(sv_address,sv_port);

        //recebe input (porta) do user
        int room_port = choosePortFromList(this.sv_connection.getRoom_list());

        String token = this.sv_connection.getToken();
        InetAddress address = null;

        try {
             address = InetAddress.getByName(sv_address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        this.rooom_connection = new RoomConnection(address,room_port,token);

        userInputLoop();

    }

    public void userInputLoop(){

        Scanner input = new Scanner(System.in);

        while(!stopLoop) {
            System.out.print("Command: ");
            handleCommands(input.next());
        }

    }

    public void handleCommands(String command){
        // TODO: 23/05/2016 use of enums
        switch(command){
            case "disconnect":
                //handleDisconnectRoom();
                break;
            default:
                System.out.println("Command not found\n");
                break;
        }
    }


    public int choosePortFromList(String list){

        System.out.println("Room list:\n"+list + "\n");

        System.out.print("Select room port: ");
        Scanner reader = new Scanner(System.in);
        int port = reader.nextInt();
        reader.close();

        return port;
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
