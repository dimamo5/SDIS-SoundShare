package client;

import auth.Credential;
import auth.Token;
import client.commands.Command;
import client.commands.CommandException;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by duarte on 21-05-2016.
 */
public class Client{

    private static Client ourInstance = new Client();

    private ServerConnection sv_connection;
    private RoomConnection roomConnection;
    private CLInterface clInterface = new CLInterface();
    private Token token;

    private boolean stopLoop = false;

    public static Client getInstance() {
        return ourInstance;
    }


    private Client() {}

    public static void main(String[] args) {

        String address = args[0];
        int sv_port = new Integer(args[1]);

        Client cli = Client.getInstance();
        cli.start(address,sv_port);
    }

    private void start(String sv_address, int sv_port){
        this.sv_connection = new ServerConnection(sv_address,sv_port);
        login();

        //recebe input (porta) do user
        int room_port = this.clInterface.choosePortFromList(this.sv_connection.getRoom_list());

        InetAddress address = null;

        try {
             address = InetAddress.getByName(sv_address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        this.roomConnection = new RoomConnection(address,room_port);

        new Thread(this.clInterface).start();
    }

    private void login() {
        Credential credentials = getClInterface().receiveInputCredentials();
        while(!getSv_connection().connectToServer(credentials)){
            Client.getInstance().getClInterface().println("Invalid Login!");
            credentials = Client.getInstance().getClInterface().receiveInputCredentials();
        }
    }

    public void executeCommand(Command command){
        try {
            switch (command.getType()){
                case SKIP:
                    this.roomConnection.skip();
                    break;
                case REQUEST:
                    //this.roomConnection.requestSong()
                    break;
                default:
                    throw new CommandException("Error executing the command");

            }
        } catch (CommandException e) {
            e.printStackTrace();
        }
    }

    public RoomConnection getRoomConnection() {
        return roomConnection;
    }

    public void setRoomConnection(RoomConnection roomConnection) {
        this.roomConnection = roomConnection;
    }

    public CLInterface getClInterface() {
        return clInterface;
    }

    public void setClInterface(CLInterface clInterface) {
        this.clInterface = clInterface;
    }

    public ServerConnection getSv_connection() {
        return sv_connection;
    }

    public void setSv_connection(ServerConnection sv_connection) {
        this.sv_connection = sv_connection;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
