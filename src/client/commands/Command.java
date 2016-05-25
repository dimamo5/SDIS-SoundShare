package client.commands;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Created by duarte on 24-05-2016.
 */
public class Command {
    public enum Type {
        CONNECT_ROOM,
        DISCONNECT_ROOM,
        CREATE_ROOM,
        SKIP,
        REQUEST,
        LOGOUT
    }

    protected Type type;
    protected String[] args;

    public Command(String command) throws CommandException {
        parseCommand(command);
    }

    public void parseCommand(String command) throws CommandException {
        String[] args = command.split("\\s+");

        this.type = parseType(args[0]);
        this.args = new String[args.length - 1];
        System.arraycopy(args, 1, this.args, 0, args.length);

        parseArgs(type);
    }

    private boolean parseArgs(Type type) throws CommandException {
        switch (type) {
            case SKIP:
                return validateArgsLength(1);

            case REQUEST:
                return validateArgsLength(2);

            case LOGOUT:
                return validateArgsLength(1);

            case CONNECT_ROOM:
                if(!validateArgsLength(2))
                    return false;
                if (!NumberUtils.isNumber(getArgs()[1]))
                    throw new CommandException("Argument number 1, port, must be an integer");
                else{
                    return true;
                }
            case DISCONNECT_ROOM:
                return validateArgsLength(1);

            case CREATE_ROOM:
                return validateArgsLength(1);

            default:
                throw new CommandException("Invalid command type");
        }
    }

    public boolean validateArgsLength(int numArgsExpected) throws CommandException {
        if (args.length != numArgsExpected) {
            throw new CommandException("Number of arguments expected: " + numArgsExpected + ". Given: " + (args.length - 1));
        }
        return true;
    }


    public Type parseType(String type) throws CommandException {
        for (Type enumType : Command.Type.values()) {
            if (type.equalsIgnoreCase(enumType.toString()))
                return enumType;
        }
        throw new CommandException("Invalid command type!");
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
