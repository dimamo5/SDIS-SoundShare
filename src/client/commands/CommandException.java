package client.commands;

import client.Client;

/**
 * Created by duarte on 24-05-2016.
 */
public class CommandException extends Exception {
    public CommandException(String message) {
        super(message);
    }

    @Override
    public void printStackTrace() {
        System.out.println(getMessage());
    }
}
