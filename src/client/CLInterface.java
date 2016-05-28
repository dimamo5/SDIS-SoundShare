package client;

import auth.Credential;
import client.commands.Command;
import client.commands.CommandException;

import java.util.Scanner;

/**
 * Created by duarte on 24-05-2016.
 */
public class CLInterface implements Runnable {

    public Boolean commandNext = false;

    public CLInterface() {
        commandNext = true;
    }

    public void println(String string) {
        System.out.println(string);
    }

    public Credential receiveInputCredentials() {
        Scanner reader = new Scanner(System.in);
        System.out.print("Name: ");
        String name = reader.next();
        System.out.print("Pass: ");
        String pass = reader.next();
        return new Credential(name, pass);
    }

    public int choosePortFromList(String list) {
        System.out.print("Select room port: ");
        Scanner reader = new Scanner(System.in);
        int port = reader.nextInt();
        reader.close();

        return port;
    }

    public void handleCommands(String input) throws CommandException {
        Command command = new Command(input);
        Client.getInstance().executeCommand(command);
    }

    @Override
    public void run() {
            Scanner input = new Scanner(System.in);
            while (commandNext) {
                System.out.print("Command: ");
                try {
                    handleCommands(input.nextLine());
                } catch (CommandException e) {
                    e.printStackTrace();
                }
                commandNext = false;
            }
    }

    public Boolean isCommandNext() {
        return commandNext;
    }

    public void setCommandNext() {
        this.commandNext = true;
    }
}
