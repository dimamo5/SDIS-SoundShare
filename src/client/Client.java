package client;

import auth.Credential;
import auth.Token;
import client.commands.Command;
import client.commands.CommandException;
import streaming.messages.Message;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by duarte on 21-05-2016.
 */
public class Client{

    private static Client ourInstance = new Client();

    private ServerConnection serverConnection;
    private RoomConnection roomConnection;
    private CLInterface clInterface = new CLInterface();
    private Token token;

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

        this.serverConnection = new ServerConnection(sv_address,sv_port);

        login();

        String list = this.serverConnection.getRoom_list();

        if(list != null)
            System.out.println("Room list:\n"+ list+"\n");
        else
            System.out.println("Room list: (Empty)");



        //recebe input (porta) do user
        /*int room_port = this.clInterface.choosePortFromList(this.serverConnection.getRoom_list());
        InetAddress address = null;
        try {
             address = InetAddress.getByName(sv_address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        this.roomConnection = new RoomConnection(address,room_port);*/

        new Thread(this.clInterface).start();
    }

    private void login() {
        Credential credentials_CLI = getClInterface().receiveInputCredentials();

        //loop while not successfully connected
        while(!getServerConnection().connectToServer(credentials_CLI)){
            Client.getInstance().getClInterface().println("Invalid Login!");
            credentials_CLI = Client.getInstance().getClInterface().receiveInputCredentials();
        }
    }

    private void logout(){
        getServerConnection().logout();
        setToken(null);
    }


    void executeCommand(Command command){
        try {
            switch (command.getType()){
                case SKIP:
                    this.roomConnection.skip();
                    break;
                case REQUEST:
                    if(this.roomConnection != null)
                        this.getRoomConnection().requestSong(command.getArgs()[0], Boolean.parseBoolean(command.getArgs()[1]));
                    else
                        this.clInterface.println("You have to be connected to a room in order to request a song!");
                    break;
                case LOGOUT:
                    logout();
                    break;
                case CREATE_ROOM:
                    getServerConnection().sendMessage(new Message(Message.Type.NEW_ROOM,getToken()));
                    Message message = this.getServerConnection().receiveMessage();
                    if(message.getType().equals(Message.Type.NEW_ROOM)){
                        getClInterface().println("New room created in port: "+message.getArgs()[0]);
                    }
                    break;
                case CONNECT_ROOM:
                    if (this.roomConnection == null)
                        try {
                            this.roomConnection = new RoomConnection(InetAddress.getByName(this.serverConnection.getServerAddress()), Integer.parseInt(command.getArgs()[0]));
                            if (!this.roomConnection.connected) {
                                this.clInterface.println("That port is not valid.");
                                this.roomConnection = null;
                            }
                        }
                        catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    else this.clInterface.println("Trying to connect to a room when you are already connected");
                    break;
                case DISCONNECT_ROOM:
                    if (this.roomConnection != null) {
                        this.roomConnection.dcFromRoom();
                        this.roomConnection = null;
                    }
                    else this.clInterface.println("Trying to disconnect from a room when you are already disconnected");
                    break;
                case ROOM_LIST:
                    getServerConnection().sendMessage(new Message(Message.Type.ROOM_LIST,token));
                    Message roomList = getServerConnection().receiveMessage();
                    getClInterface().println(roomList.toString());
                    break;
                default:
                    throw new CommandException("Error executing the command");
            }
        } catch (CommandException e) {
            e.printStackTrace();
        }
    }

    private RoomConnection getRoomConnection() {
        return roomConnection;
    }

    private CLInterface getClInterface() {
        return clInterface;
    }

    private ServerConnection getServerConnection() {
        return serverConnection;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
